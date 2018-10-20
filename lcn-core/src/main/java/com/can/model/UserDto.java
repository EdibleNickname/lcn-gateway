package com.can.model;

import com.can.service.auth.reader.personalInfo.PersonalInfoService.UpdatePersonalInfoGroup;
import com.can.service.open.register.RegisterService.RegisterGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Date;

/**
 * @description:
 * @author: LCN
 * @date: 2018-05-23 17:46
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto implements Serializable {

	/** 用户id */
	private Integer userId;

	@NotEmpty(message = "用户名不能为空", groups = {UpdatePersonalInfoGroup.class})
	@NotEmpty(message = "用户名不能为空", groups = {RegisterGroup.class})
	private String userName;

	@NotEmpty(message = "密码不能为空", groups = {RegisterGroup.class})
	private String password;

	@Email(message = "邮箱格式不正确")
	@NotEmpty(message = "邮箱不能为空", groups = {RegisterGroup.class})
	private String email;

	@NotEmpty(message = "验证码不能为空", groups = {RegisterGroup.class})
	private String authCode;

	@NotEmpty(message = "邮件的RedisKey不能为空", groups = {RegisterGroup.class})
	private String emailRedisKey;

	private String headPortraits;

	private Date birthday;

	@Length(max = 50, message = "长度超过50了", groups = {UpdatePersonalInfoGroup.class})
	private String introduce;

	@Range(min = 0, max = 2, message = "可取值在[0,2]", groups = {UpdatePersonalInfoGroup.class})
	private int sex;

	/** 用户是否上传了头像的标识 */
	@Range(min = 0, max = 1, message = "可取值在[0,1]", groups = {UpdatePersonalInfoGroup.class})
	private int isModify;

}
