package com.app.utils;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.io.FileUtils;
import org.springframework.web.multipart.MultipartFile;

import com.app.pojo.FileMovedPojo;
import com.app.pojo.FileUploadedPojo;



public class FUtils {

	private FUtils() {}

	public static String getBaseDirectory(String key) {
		return System.getProperty(key);
	}

	public static String getBaseFileDirectoryPath() {
		return Constants.BASE_DIRECTORY;
	}

	public static String getDirectoryPath(String directory) {
		return getBaseFileDirectoryPath() + Constants.PATH_SEPARATOR + directory;
	}

	public static String getUploadDirectoryPath() {
		return getDirectoryPath(Constants.UPLOAD_DIR);
	}

	public static File getDirectoryFile(String dirPath) {
		File dir = new File(dirPath);

		if (!dir.exists()) {
			dir.mkdirs();
		}

		return dir;
	}

	public static String generateFileName(String fileExt) throws NoSuchAlgorithmException {
		String newFilename = String.valueOf(System.currentTimeMillis());
		newFilename = JUtils.HashStr(newFilename) + "_" + newFilename + "." + fileExt;

		return newFilename;
	}

	public static String generateFileName(MultipartFile file) throws NoSuchAlgorithmException {
		String originalFilename = file.getOriginalFilename();
		String[] fileNameParts = (originalFilename != null) ? originalFilename.split("\\.") : new String[0];
		String fileExt = fileNameParts[fileNameParts.length - 1];

		return generateFileName(fileExt);
	}

	public static FileUploadedPojo uploadFile(MultipartFile file) throws NoSuchAlgorithmException, IOException {
		// String dirPath = getUploadDirectoryPath();
		String dirPath = FileUtils.getTempDirectoryPath();
		File dirFile = getDirectoryFile(dirPath);

		String originalName = file.getOriginalFilename();
		String filename = generateFileName(file);

		File tempFile = new File(dirFile, filename);
		String tempFilePath = tempFile.getPath();

		file.transferTo(tempFile);
		/*
		 * BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(tempFile)); stream.write(file.getBytes()); stream.close();
		 */

		return new FileUploadedPojo(filename, originalName, tempFile, tempFilePath, dirFile, dirPath);
	}

	public static FileMovedPojo moveFile(File file, String targetDirectory) throws IOException {
		String dirPath = getDirectoryPath(targetDirectory);
		File dirFile = getDirectoryFile(dirPath);

		return moveFileDirect(dirFile, file);
	}

	public static FileMovedPojo moveFileDirect(File dirFile, File file) throws IOException {

		File newFile = new File(dirFile, file.getName());
		FileUtils.moveToDirectory(file, dirFile, true);

		return new FileMovedPojo(file, file.getPath(), newFile.getPath());
	}

	public static FileMovedPojo moveFile(String filePath, String targetDirectory) throws IOException {
		return moveFile(new File(filePath), targetDirectory);
	}



	public static boolean deleteFile(File file) {
		return file.delete();
	}

	public static boolean deleteFile(String filePath) {
		return deleteFile(new File(filePath));
	}

}

