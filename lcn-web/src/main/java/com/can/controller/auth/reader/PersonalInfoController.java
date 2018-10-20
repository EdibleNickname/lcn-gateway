package com.can.controller.auth.reader;

import com.can.model.UserDto;
import com.can.response.Response;
import com.can.service.auth.reader.personalInfo.PersonalInfoService;
import com.can.service.auth.reader.personalInfo.PersonalInfoService.UpdatePersonalInfoGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Map;


/**
 * @description:
 * @author: LCN
 * @date: 2018-07-18 16:20
 */

@Slf4j
@RestController
@RequestMapping("/info")
@PreAuthorize("hasRole('READER')")
public class PersonalInfoController {

	@Resource
	private PersonalInfoService personalInfoService;

	/**
	 * 更新个人基础信息
	 * @param userDto	个人基础信息
	 * @return
	 */
	@PutMapping("/uploadBaseData")
	public Response<Map<String, String>> uploadBaseInfo(@Validated({UpdatePersonalInfoGroup.class}) @RequestBody UserDto userDto) {
		return personalInfoService.updateBaseInfo(userDto);
	}

	/**
	 * 更改个人头像
	 *
	 * @param file
	 * @param redisKey
	 * @return
	 */
	@PostMapping("/uploadHeaderPortrait")
	public Response<Map<String, String>> uploadHeaderPortrait(MultipartFile file, String redisKey) {
		return personalInfoService.updateHeaderPortrait(file, redisKey);
	}


}
