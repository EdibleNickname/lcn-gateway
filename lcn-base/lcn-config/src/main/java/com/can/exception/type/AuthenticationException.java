package com.can.exception.type;

/**
 * @description: 认证异常
 *
 * @author: LCN
 * @date: 2018-05-18 11:23
 */
public class AuthenticationException extends RuntimeException {

	public AuthenticationException() {
	}

	public AuthenticationException(String message, Throwable cause) {
		super(message, cause);
	}

	public AuthenticationException(String message) {
		super(message);
	}

	public AuthenticationException(Throwable cause) {
		super(cause);
	}

}
