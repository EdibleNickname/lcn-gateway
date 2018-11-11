package com.can.web.exception;

import com.can.web.exception.type.AuthenticationException;
import com.can.response.Response;
import com.can.response.enums.ResponseEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @description: SpringBoot的全局异常处理
 * @author: LCN
 * @date: 2018-05-18 10:09
 */

@ResponseBody
@ControllerAdvice
public class GlobalExceptionHandler {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	private final static String MSG_KEY = "errorMsg";

	public GlobalExceptionHandler() {
	}

	@ExceptionHandler({Exception.class})
	public Response<Map<String, String>> exceptionHandler (Exception exception) {

		log.error(exception.getMessage(), exception);

		Response<Map<String, String>> response = new Response<>();
		Map<String, String> map = new HashMap<>(16);

		//认证失败异常
		if (exception instanceof AuthenticationException) {
			AuthenticationException ex = (AuthenticationException)exception;
			response.setCode(ResponseEnum.UNAUTHORIZED.getCode());
			response.setMessage(ResponseEnum.UNAUTHORIZED.getMessage());
			map.put(MSG_KEY, ex.getMessage());
			response.setResult(map);
			return response;
		}
		// 方法参数验证失败
		if (exception instanceof MethodArgumentNotValidException ) {
			MethodArgumentNotValidException ex = (MethodArgumentNotValidException)exception;

			FieldError fieldError;
			Iterator iterator = ex.getBindingResult().getFieldErrors().iterator();

			while (iterator.hasNext()) {
				fieldError = (FieldError)iterator.next();
				map.put(fieldError.getField(), fieldError.getDefaultMessage());
			}

			response.setCode(ResponseEnum.PARAMETER_ERROR.getCode());
			response.setMessage(ResponseEnum.PARAMETER_ERROR.getMessage());
			response.setResult(map);
			return response;
		}

		// 未知异常
		response.setCode(ResponseEnum.FAILURE.getCode());
		response.setMessage(ResponseEnum.FAILURE.getMessage());
		return response;
	}

}
