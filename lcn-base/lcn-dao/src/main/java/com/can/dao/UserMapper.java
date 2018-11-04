package com.can.dao;

import com.can.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {

    int deleteByPrimaryKey(Integer userId);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer userId);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    /**
     * 通过用户名查询用户的所有信息
     * @param userName
     * @return
     */
    User selectAllUserInfoByUserName(String userName);

    /**
     * 通过用户id查询用户的所有信息
     * @param userId
     * @return
     */
    User selectUserInfoByUserId(int userId);

    /**
     * 通过用户名查询用户信息
     * @param userName
     * @return
     */
    User selectUserByUserName(String userName);

    /**
     * 通过邮箱查询用户信息
     * @param email
     * @return
     */
    User selectUserByEmail(String email);

    /**
     * 查询用户名或者邮箱是否被注册过了
     *
     * @param userInfo
     * @return
     */
    List<User> selectUserUnique(User userInfo);

    /**
     * 新增用户
     *
     * @param record
     * @return
     */
    int userInsertSelective(User record);

    /**
     * 为用户添加角色
     *
     * @param userName  用户名
     * @param roleName  角色名
     * @return
     */
    int addUserRole(@Param("userName")String userName, @Param("roleName")String roleName);

    /**
     * 通过用户名对用户信息进行更新
     * @param record
     * @return
     */
    int updateByUserNameOrUserIdSelective(User record);

    /**
     * 通过用户名查询用户id
     *
     * @param userName 用户名
     * @return
     */
    String selectUserIdByUserName(String userName);

    /**
     * 通过用户名或者用户id查询用户信息
     * @param user
     * @return
     */
    User selectUserByUserNameOrUserId(User user);
}