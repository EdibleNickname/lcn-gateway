package com.can.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @description:
 * @author: LCN
 * @date: 2018-05-29 19:47
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Page<T> {

	/** 当前的页数 */
	private Integer currentPage;

	/** 总的页数 */
	private Integer totalPage;

	/** 查询结果 */
	private List<T> result;

}
