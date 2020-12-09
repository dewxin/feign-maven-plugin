package com.github.dewxin.file;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;

import com.github.dewxin.tool.DataCenter;
import com.github.dewxin.tool.Logger;

public class FileHandler {

	private static String codeDir = "/target/dewxin-feign-plugin/src/main/java/com/github/dewxin/generated/auto_client/";
	
	private static String baseDir = "/target/dewxin-feign-plugin/";

	
	public static void writeToFile(String fileName, List<String> lines) throws IOException {
		File file = new File(fileName);
		if(file.exists()) 
			file.delete();
		
		file.createNewFile();
		Files.write(file.toPath(), lines, Charset.defaultCharset());
		Logger.info(fileName +"  has been created");
	}
	
	public static String getBaseDir() {
		return DataCenter.instance().getProjectBaseDir() + baseDir;
	}
	
	public static String getCodeDir() {
		return DataCenter.instance().getProjectBaseDir() + codeDir;
	}

	public static void setBaseDir(String baseDir) {
		FileHandler.codeDir = baseDir;
	}
	
	
}
