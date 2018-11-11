package com.can.web.config;

import com.can.util.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @description: 全局时间格式处理，将前端传过来的时间字符串转为Date
 *
 * @author: LCN
 * @date: 2018-07-21 16:33
 */

@Slf4j
@Component
public class DateConverterConfig implements Converter<String, Date> {

	private static final List<String> FORMARTS = new ArrayList<>(4);

	@Override
	public Date convert(String time) {

		if (StringUtils.isEmpty(time.trim())) {
			return null;
		}

		return DateUtil.stringToDate(time);
	}

}
