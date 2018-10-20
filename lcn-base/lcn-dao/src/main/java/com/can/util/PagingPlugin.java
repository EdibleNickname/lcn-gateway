package com.can.util;

/**
 * @description: mybatis的分页插件
 *
 * @author: LCN
 * @date: 2018-05-30 17:49
 */

import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.session.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class PagingPlugin implements Interceptor {

	private Logger logger = LoggerFactory.getLogger(PagingPlugin.class);

	/** 数据库类型 */
	private String dbType;

	/** 正则匹配mapper.xml中需要拦截的ID */
	private String pageSqlId;

	/** 默认的每页条数 */
	private Integer defaultPageSize;

	/** 默认的页数 */
	private Integer defaultPageNum;

	/** 支持的数据库类型 */
	private static final String DB_TYPE_MYSQL = "mysql";
	private static final String DB_TYPE_ORACLE = "oracle";


	@Override
	public Object intercept(Invocation invocation) throws Throwable {

		StatementHandler stmtHandler = (StatementHandler) getUnProxyObject(invocation.getTarget());

		MetaObject metaStatementHandler = SystemMetaObject.forObject(stmtHandler);

		MappedStatement mappedStatement =  (MappedStatement) metaStatementHandler.getValue("delegate.mappedStatement");

		// sql语句不需要分页的
		if(!mappedStatement.getId().matches(pageSqlId)){
			return invocation.proceed();
		}

		BoundSql boundSql = (BoundSql) metaStatementHandler.getValue("delegate.boundSql");
		// 获取 mapper文件里面的 sql的 paramType的类型(既参数类型)
		Object parameterObject = boundSql.getParameterObject();

		if (parameterObject == null) {
			logger.error("分页查询条件没有参数");
			throw new Exception("分页查询条件没有参数");
		}

		// 获取请求参数
		PageParam pageParam = (PageParam)parameterObject;

		// 每页的条数
		Integer pageSize = pageParam.getPageSize() == null ? defaultPageSize : pageParam.getPageSize();

		// 查询第几页
		Integer pageNum = pageParam.getPageNum() == null ? defaultPageNum : pageParam.getPageNum();

		// 计算总条数
		int total = getTotal(invocation, metaStatementHandler, boundSql, dbType);

		//计算总页数
		int totalPage = total % pageSize == 0 ? total / pageSize : total / pageSize + 1;

		//检查当前页码的有效性
		checkPage(pageNum, totalPage);

		//给参数设置总页数
		pageParam.setTotalPage(totalPage);
		pageParam.setCurrentPage(pageParam.getPageNum());

		return preparedSQL(invocation, metaStatementHandler, boundSql, pageNum, pageSize);
	}

	/**
	 * 生成代理对象
	 *
	 * @param statementHandler 原始对象
	 * @return 代理对象
	 */
	@Override
	public Object plugin(Object statementHandler) {
		return Plugin.wrap(statementHandler, this);
	}

	/**
	 * 从配置里面读取设置
	 *
	 * @param properties
	 */
	@Override
	public void setProperties(Properties properties) {
		dbType = properties.getProperty("dbType", "mysql");
		pageSqlId= properties.getProperty("pageSqlId", ".*Page$");
		defaultPageSize = Integer.parseInt(properties.getProperty("pageSize", "10"));
		defaultPageNum = Integer.parseInt(properties.getProperty("pageNum", "1"));
	}

	/**
	 * 从代理对象中分离出真实对象.
	 *
	 * @param target --Invocation
	 * @return 非代理StatementHandler对象
	 */
	private Object getUnProxyObject(Object target) {
		MetaObject metaStatementHandler = SystemMetaObject.forObject(target);
		// 分离代理对象链(由于目标类可能被多个拦截器拦截，从而形成多次代理，通过循环可以分离出最原始的的目标类)
		Object object = null;
		while (metaStatementHandler.hasGetter("h")) {
			object = metaStatementHandler.getValue("h");
		}
		if (object == null) {
			return target;
		}
		return object;
	}

	/**
	 * 获取总条数
	 *
	 * @param ivt
	 * @param metaStatementHandler
	 * @param boundSql
	 * @param dbType
	 * @return
	 * @throws Throwable
	 */
	private int getTotal(Invocation ivt, MetaObject metaStatementHandler, BoundSql boundSql, String dbType) throws Throwable {

		//获取当前的mappedStatement
		MappedStatement mappedStatement = (MappedStatement) metaStatementHandler.getValue("delegate.mappedStatement");
		//配置对象
		Configuration cfg = mappedStatement.getConfiguration();

		//当前需要执行的SQL
		String sql = (String) metaStatementHandler.getValue("delegate.boundSql.sql");
		String countSql = getTotalSQL(sql, dbType);

		//获取拦截方法参数，根据插件签名，知道是Connection对象.
		Connection connection = (Connection) ivt.getArgs()[0];
		PreparedStatement ps = null;
		int total = 0;

		try {
			//预编译统计总数SQL
			ps = connection.prepareStatement(countSql);

			//构建统计总数SQL
			BoundSql countBoundSql = new BoundSql(cfg, countSql, boundSql.getParameterMappings(), boundSql.getParameterObject());

			//构建MyBatis的ParameterHandler用来设置总数Sql的参数。
			ParameterHandler handler = new DefaultParameterHandler(mappedStatement, boundSql.getParameterObject(), countBoundSql);

			//设置总数SQL参数
			handler.setParameters(ps);

			//执行查询.
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				total = rs.getInt("total");
			}
		} finally {
			//这里不能关闭Connection否则后续的SQL就没法继续了。
			if (ps != null) {
				ps.close();
			}
		}
		return total;
	}

	/**
	 * 获取统计总条数的sql语句
	 *
	 * @param currSql
	 * @param dbType
	 * @return
	 * @throws Exception
	 */
	private String getTotalSQL(String currSql, String dbType) throws Exception{
		if (DB_TYPE_MYSQL.equals(dbType)) {
			return  "select count(*) as total from (" + currSql + ") $_paging";
		}
		if (DB_TYPE_ORACLE.equals(dbType)) {
			return "select count(*) as total from (" + currSql +")";
		}

		logger.info("插件还没支持oracle和mysql以外的数据库");
		throw new Exception("插件还没支持oracle和mysql以外的数据库");

	}

	/**
	 * 检查当前页码的有效性
	 *
	 * @param pageNum
	 * @param pageTotal
	 */
	private void checkPage(Integer pageNum, Integer pageTotal) throws Exception{
		if (pageNum > pageTotal) {
			throw new Exception("查询失败，查询页码【" + pageNum + "】大于总页数【" + pageTotal + "】！！");
		}
	}

	/**
	 * 执行分页sql语句
	 * @param invocation
	 * @param metaStatementHandler
	 * @param boundSql
	 * @return
	 * @throws Exception
	 */
	private Object preparedSQL(Invocation invocation, MetaObject metaStatementHandler, BoundSql boundSql,
		int pageNum, int pageSize) throws Exception  {
		//获取当前需要执行的SQL
		String sql = boundSql.getSql();
		// 拼凑出分页sql语句
		String pageSql = getPageDataSQL(sql);

		//修改当前需要执行的SQL
		metaStatementHandler.setValue("delegate.boundSql.sql", pageSql);
		//执行编译，这里相当于StatementHandler执行了prepared()方法，这个时候，就剩下2个分页参数没有设置。
		Object statementObj = invocation.proceed();

		//设置两个分页参数
		preparePageDataParams((PreparedStatement)statementObj, pageNum, pageSize);

		return statementObj;
	}

	/**
	 * 拼凑分页的sql语句
	 *
	 * @param currSql
	 * @return
	 */
	private String getPageDataSQL(String currSql) throws Exception{
		if (DB_TYPE_MYSQL.equals(dbType)) {
			return "select * from (" + currSql + ") $_paging_table limit ?, ?";
		}

		if (DB_TYPE_ORACLE.equals(dbType)) {
			return " select * from (select cur_sql_result.*, rownum rn from (" + currSql + ") cur_sql_result  where rownum <= ?) where rn > ?";
		}

		logger.info("插件还没支持oracle和mysql以外的数据库");
		throw new Exception("插件还没支持oracle和mysql以外的数据库");
	}

	/**
	 * 使用PreparedStatement预编译两个分页参数
	 *
	 * @param ps
	 * @param pageNum
	 * @param pageSize
	 */
	private void preparePageDataParams(PreparedStatement ps, int pageNum, int pageSize) throws Exception {

		int idx = ps.getParameterMetaData().getParameterCount();

		if (DB_TYPE_MYSQL.equals(dbType)) {
			//最后两个是我们的分页参数

			//开始行
			ps.setInt(idx -1, (pageNum - 1) * pageSize);

			//限制条数
			ps.setInt(idx, pageSize);

			return;
		}

		if (DB_TYPE_ORACLE.equals(dbType)) {

			//结束行
			ps.setInt(idx -1, pageNum * pageSize);
			//开始行
			ps.setInt(idx, (pageNum - 1) * pageSize);

			return;
		}

		logger.info("插件还没支持oracle和mysql以外的数据库");
		throw new Exception("插件还没支持oracle和mysql以外的数据库");
	}

}
