package com.can.service.auth.reader.personalInfo.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.can.dao.UserMapper;
import com.can.entity.User;
import com.can.model.UserDto;
import com.can.redis.util.RedisUtil;
import com.can.response.Response;
import com.can.service.auth.reader.personalInfo.PersonalInfoService;
import com.can.utils.Constant;
import com.can.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @description: 个人信息实现类
 *
 * @author: LCN
 * @date: 2018-07-18 16:53
 */

@Slf4j
@Service
public class PersonalInfoServiceImpl implements PersonalInfoService {

	@Resource
	private UserMapper userMapper;

	@Resource
	private RedisUtil redisUtil;

	/** 头像文件默认的保存地址*/
	@Value("${file.headerPortrait}")
	private String basePath;

	@Value("${file.headerPortraitBaseUrl}")
	private String headerPortraitBaseUrl;


	/** 基础信息保留在缓存中的过期时间 600s=10min */
	private final static Long BASE_INFO_EXPIRE_TIME = 600L;

	/** 在给定的基础目录/headerPortrait保存用户头像文件 */
	private final static String HEADER_PORTRAIT_PATH = "/headerPortrait";

	@Override
	public Response<Map<String, String>> updateBaseInfo(UserDto userDto) {

		log.info("需要更新的用户基础信息--------->{}", JSON.toJSONString(userDto));

		Map<String, String> map = new HashMap<>(16);
		Response<Map<String, String>> response = new Response<>();


		User user = new User();
		BeanUtils.copyProperties(userDto, user);

		// 没有更新头像
		if(Constant.FALSE == userDto.getIsModify()) {
			int result = userMapper.updateByUserNameOrUserIdSelective(user);

			if(result != 1) {
				log.info("更新用户-----{}----基础信息失败", user.getUserName());
				map.put("isSuccess", "0");
				response.setResult(map);
				log.info("返回结果================>{}", JSON.toJSONString(response));
				return response;
			}

			log.info("更新用户-----{}----基础信息成功", user.getUserName());
			map.put("isSuccess", "1");
			map.put("userId", userMapper.queryUserIdByUserName(user.getUserName()));
			response.setResult(map);
			log.info("返回结果================>{}", JSON.toJSONString(response));
			return response;
		}

		// 用户有上传图片 将用户的基础信息暂存到缓存中，接收到了头像文件，在一起处理

		String redisKey = redisUtil.setNoKey(user, BASE_INFO_EXPIRE_TIME);
		map.put("isSuccess", "1");
		map.put("redisKey", redisKey);
		response.setResult(map);
		log.info("返回结果================>{}", JSON.toJSONString(response));
		return response;
	}

	@Override
	public Response<Map<String, String>> updateHeaderPortrait(MultipartFile file, String redisKey) {

		log.info("用户提交的redisKey为------------>{}", redisKey);

		Map<String, String> map = new HashMap<>(16);
		Response<Map<String, String>> response = new Response<>();

		if(StringUtils.isEmpty(redisKey)) {
			log.info("用户提交的redisKey为空");
			map.put("isSuccess", "0");
			response.setResult(map);
			log.info("返回结果================>{}", JSON.toJSONString(response));
			return response;
		}
		// 从缓存中获取对象
		User user = (User)redisUtil.get(redisKey);

		// 对象不存在
		if (user == null) {
			log.info("用户提交的redisKey获取的用户基础信息为空");
			map.put("isSuccess", "0");
			response.setResult(map);
			log.info("返回结果================>{}", JSON.toJSONString(response));
			return response;
		}

		User oldUser = userMapper.queryUserByUserNameOrUserId(user);
		if(oldUser == null) {
			log.info("需要更新的用户不存在,查询的条件---->{}", JSONObject.toJSONString(user));
			map.put("isSuccess", "0");
			map.put("hint", "用户不存在");
			response.setResult(map);
			log.info("返回结果================>{}", JSON.toJSONString(response));
			return response;
		}

		if(file != null) {
			if(StringUtils.isNotEmpty(file.getOriginalFilename())) {

				// 用户有旧的头像，进行删除
				if(StringUtils.isNotEmpty(oldUser.getHeadPortraits())) {
					String filePath = oldUser.getHeadPortraits().replaceAll(headerPortraitBaseUrl, basePath);
					log.info("用户旧的头像的存放地址-------->{}", filePath);
					FileUtils.deleteFile(filePath);
				}

				// 上传了头像
				String filePath = FileUtils.saveFile(basePath, HEADER_PORTRAIT_PATH, file);
				if(StringUtils.isEmpty(filePath)) {
					map.put("isSuccess", "0");
					map.put("hint", "更新用户头像失败");
					response.setResult(map);
					log.info("返回结果================>{}", JSON.toJSONString(response));
					return response;
				}

				log.info("用户:{}的头像的存放路径为----->{}", user.getUserName(), filePath);

				// 服务器的路径映射地址没有设置
				if (StringUtils.isEmpty(headerPortraitBaseUrl)) {
					map.put("isSuccess", "0");
					map.put("hint", "服务器未设置路径映射地址");
					response.setResult(map);
					log.info("返回结果================>{}", JSON.toJSONString(response));
					return response;
				}

				//将用户的头像存放地址映射为url
				String imgUrl = headerPortraitBaseUrl + HEADER_PORTRAIT_PATH + File.separator + filePath;

				log.info("用户的头像的url------->{}", imgUrl);
				// 设置用户头像路径
				user.setHeadPortraits(imgUrl);
			}
		}


		// 如果用户名修改了,通过用户id进行修改
		int result = userMapper.updateByUserNameOrUserIdSelective(user);

		if(result != 1) {
			log.info("更新用户-----{}----基础信息失败", user.getUserName());
			map.put("isSuccess", "0");
			map.put("hint", "更新用户基础信息失败");
			response.setResult(map);
			log.info("返回结果================>{}", JSON.toJSONString(response));
			return response;
		}

		log.info("更新用户-----{}----基础信息成功", user.getUserName());
		map.put("isSuccess", "1");
		map.put("path", user.getHeadPortraits());
		map.put("userId", userMapper.queryUserIdByUserName(user.getUserName()));
		response.setResult(map);
		log.info("返回结果================>{}", JSON.toJSONString(response));
		return response;
	}


}
