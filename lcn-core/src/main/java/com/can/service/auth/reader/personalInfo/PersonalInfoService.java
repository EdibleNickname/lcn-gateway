package com.can.service.auth.reader.personalInfo;

import com.can.model.UserDto;
import com.can.response.Response;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @description: 个人信息接口
 * @author: LCN
 * @date: 2018-07-18 16:44
 */
public interface PersonalInfoService {

	@interface UpdatePersonalInfoGroup{
	}

	/**
	 * 更新个人基础信息
	 * @param userDto	个人基础信息
	 * @return
	 */
	Response<Map<String, String>> updateBaseInfo(UserDto userDto);

	/**
	 * 更新个人头像
	 * @param file		头像
	 * @param redisKey	基础信息暂存的redis key
	 * @return
	 */
	Response<Map<String, String>> updateHeaderPortrait(MultipartFile file, String redisKey);


}
