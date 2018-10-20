package com.can.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description:
 * @author: LCN
 * @date: 2018-05-30 19:53
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest extends PageParam {

	private String userName;

}
