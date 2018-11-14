package com.can.controller.open;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description:
 * @author: LCN
 * @date: 2018-11-14 19:06
 */

@Slf4j
@RestController
@RequestMapping("/open")
public class DemoController {

	@GetMapping("/get")
	public String getRequest() {

		return "get-ok-change";
	}

	@PostMapping("/post")
	public String postRequest() {

		return "post-ok";
	}


}
