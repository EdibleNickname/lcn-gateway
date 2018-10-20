package com.can.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description:
 * @author: LCN
 * @date: 2018-05-29 19:50
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageParam {

	/** 前面这2个的作用:给前端做分页请求的 */

	/** 每页的条数 */
	private Integer pageSize;

	/** 请求的页数 */
	private Integer pageNum;


	/*******************************************************************************/

	/**
	 * 下面的参数，是给予前端分页响应的
	 *
	 * 1: 请求进来时，到了dao层，在dao层将会拦截sql
	 *
	 * 2: 拦截完sql后，会进行 总页数等数据的设置，到时会暂时放到
	 * 这个请求参数里面，然后到了service层，利用aop，手动为返回
	 * 值Page<T>设置响应的页数等参数，数据就来源于下面这几个参数
	 *
	 */

	/** 当前的页数 */
	private Integer currentPage;

	/** 总的页数 */
	private Integer totalPage;

}
