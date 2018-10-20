package com.can.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @description:
 * @author: LCN
 * @date: 2018-07-18 14:53
 */


@Slf4j
public class FileUtils {

	/** 时间格式化 */
	private final static SimpleDateFormat FORMAT = new SimpleDateFormat( "yyyyMMdd");

	/**
	 * 保存文件(在给定的文件路径后面拼接上子级目录，在进行保存)
	 * @param basePath	基础目录
	 * @param otherPath	子级目录
	 * @param file	需要保存的文件
	 * @return	保存的路径
	 */
	public static String saveFile(String basePath, String otherPath, MultipartFile file) {

		String newFilePath = basePath + otherPath;

		log.info("完整的保存路径为------>{}", newFilePath);

		return saveFile(newFilePath, file);
	}

	/**
	 * 保存文件
	 * @param basePath	需要保存基础路径的路径
	 * @param file	需要保存的文件
	 * @return		文件路径
	 */
	public static String saveFile(String basePath, MultipartFile file) {

		// 通过日期对文件进行分类
		String dataPath = FORMAT.format(new Date());

		// 图片存放的地址 日期/文件名
		String filePath = dataPath + Constant.FORWARD_SLASH + file.getOriginalFilename();

		// 完整的保存路径,不包含文件
		String savedPath;

		// 以基础路径以/结尾
		if(basePath.endsWith(Constant.FORWARD_SLASH)) {
			savedPath = basePath + dataPath;
		} else {
			savedPath = basePath + Constant.FORWARD_SLASH + dataPath;
		}

		// 创建保存的目录
		File savedDir = new File(savedPath);

		if(!savedDir.exists() && !savedDir.isDirectory()) {
			log.info("文件目录不存在, 创建中....");
			savedDir.mkdir();
		}

		// 图片的存放地址，绝对路径，包含文件
		String absoluteFile = basePath + Constant.FORWARD_SLASH + filePath;

		// 创建需要保存的文件
		File saveFile = new File(absoluteFile);

		// 文件已存在
		if(saveFile.exists() && saveFile.isFile()) {
			log.info("文件已存在,删除中....");
			saveFile.delete();
		}

		if(!saveFile.exists()) {
			try {
				log.info("保存文件中....");
				saveFile.createNewFile();
				// 保存文件
				file.transferTo(saveFile);
				log.info("文件保存成功....");
				return filePath;
			} catch (IOException e) {
				log.info("文件保存失败---------->{}", e);

			}
		}
		return null;
	}

	/**
	 * 删除文件
	 * @param filePath
	 * @return
	 */
	public static boolean deleteFile(String filePath){

		File saveFile = new File(filePath);

		if(saveFile.exists() && saveFile.isFile()) {
			log.info("文件已存在,删除中....");
			saveFile.delete();
			return true;
		}

		return false;
	}
}
