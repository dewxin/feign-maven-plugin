package fun.enou.maven.file;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;

import fun.enou.maven.tool.Logger;

public class FileHandler {

	private static String codeDir = "target/enou-feignpp/src/main/java/fun/enou/feign/generated/auto_client/";
	
	private static String baseDir = "target/enou-feignpp/";

	
	public static void writeToFile(String fileName, List<String> lines) throws IOException {
		File file = new File(fileName);
		if(file.exists()) 
			file.delete();
		
		file.createNewFile();
		Files.write(file.toPath(), lines, Charset.defaultCharset());
		Logger.info(fileName +"  has been created");
	}
	
	public static String getBaseDir() {
		return baseDir;
	}
	
	public static String getCodeDir() {
		return codeDir;
	}

	public static void setBaseDir(String baseDir) {
		FileHandler.codeDir = baseDir;
	}
	
	
	
}
