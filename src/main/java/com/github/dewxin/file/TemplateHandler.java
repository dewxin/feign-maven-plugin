package com.github.dewxin.file;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.xml.crypto.Data;

import com.github.dewxin.model.CtrlEntity;
import com.github.dewxin.model.PojoEntity;
import com.github.dewxin.tool.DataCenter;
import com.github.dewxin.tool.Logger;

import junit.framework.Assert;


public class TemplateHandler {
	
	private static final String INTERFACE_FILE_NAME ="/feign-client-interface-template.txt";
	private static final String POJO_FILE_NAME ="/feign-client-pojo-template.txt";
	private static final String MAVEN_POM_FILE_NAME ="/feign-maven-template.txt";
	private static final String MAVEN_SELF_PACKAGE_STUB = "^selfDefinedPackageStub$";
	private static final String FEIGN_CLIENT_STUB = "^feignClientAnnotationStub$";
	private static final String APP_NAME_STUB = "^appNameStub$";
	private static final String METHOD_STUB = "^methodStub$"; 
	private static final String APPLICATION_NAME_STUB = "^applicationNameStub$"; 
	private static final String PROJECT_ARTIFACT_ID_STUB = "^projectArtifactIdStub$";
	private static final String PROJECT_GROUP_ID_STUB = "^projectGroupIdStub$";
	private static final String PROJECT_VERSION_STUB ="^projectVersionStub$";
	
	private static final String IMPORT_STUB = "^importStub$";

	private static TemplateHandler templateHandler = new TemplateHandler();

	public static TemplateHandler instance() {
		return templateHandler;
	}

	public static void reset() {
		templateHandler = new TemplateHandler();
	}

	
	private List<String> interfaceTemplateLines = new LinkedList<>();
	private List<String> pojoTemplateLines = new LinkedList<>();
	
	
	public void init() throws IOException {
		readInterfaceResource();
		readPojoResource();
	}


	public void readInterfaceResource() throws IOException {
		InputStream path = this.getClass().getResourceAsStream(INTERFACE_FILE_NAME);
		BufferedReader reader = new BufferedReader(new InputStreamReader(path));
		
		String line = "";
		while((line = reader.readLine()) != null) {
			interfaceTemplateLines.add(line);
		}
		Logger.debug("interface template file lines is {0}", interfaceTemplateLines.size());
	}
	
	public void readPojoResource() throws IOException {
		InputStream path = this.getClass().getResourceAsStream(POJO_FILE_NAME);
		BufferedReader reader = new BufferedReader(new InputStreamReader(path));
		
		String line = "";
		while((line = reader.readLine()) != null) {
			pojoTemplateLines.add(line);
		}
		Logger.debug("pojo template file lines is {0}", pojoTemplateLines.size());
	}
	
	private List<String> generateControllerFile(List<CtrlEntity> ctrlEntityList) {
		List<String> outputLines = new LinkedList<>();
		for(String line : interfaceTemplateLines) {
			List<String> lines = handleOneLine(line, ctrlEntityList);
			outputLines.addAll(lines);
		}
		
		return outputLines;
	}
	
	//todo need tab to make the code look beautiful
	public void generateAllCtrlAndPojo() throws IOException, ClassNotFoundException {
		String baseDir = FileHandler.getCodeDir();
		File dir = new File(baseDir);
		if(!dir.exists()) {
			dir.mkdirs();
		}

		List<CtrlEntity> controllerList = DataCenter.instance().getCtrlEntityList();

		List<String> lines = generateControllerFile(controllerList);
		FileHandler.writeToFile(baseDir+ DataCenter.instance().getFormattedAppName()+"Client.java",lines);

		PojoEntity.generateAllPojo(pojoTemplateLines);
	}

	public void generatePomFile() throws IOException {
		List<String> pomLines = getMavenPomLines();
		FileHandler.writeToFile(FileHandler.getBaseDir()+"pom.xml", pomLines);
	}

	public void install() throws IOException, InterruptedException {
		
		ProcessBuilder pb = new ProcessBuilder("mvn.cmd", "install", "-f", FileHandler.getBaseDir()+"pom.xml");
		pb.redirectErrorStream();
		Process process = pb.start();

		Logger.info("start installing auto generated project");

		BufferedInputStream in = new BufferedInputStream(process.getInputStream());
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));

		String str  = "";

		while(!process.waitFor(1, TimeUnit.MILLISECONDS)) {
			try {
				while((str = reader.readLine())!=null) {
					Logger.info(str);
				}
			} catch(IOException exception) {
				Logger.info(exception.getMessage());
			}
		}

		if(process.exitValue() != 0) {
			System.exit(process.exitValue());
		}
	}
	
	private List<String> getMavenPomLines() throws IOException {
		InputStream inputStream =this.getClass().getResourceAsStream(MAVEN_POM_FILE_NAME);
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		
		List<String> mavenPomLines = new LinkedList<>();
		String line = "";
		while ((line = reader.readLine()) != null) {
			if (line.contains(PROJECT_ARTIFACT_ID_STUB)) {
				line = line.replace(PROJECT_ARTIFACT_ID_STUB, DataCenter.instance().getProjectArtifactId());
				mavenPomLines.add(line);
			} else if (line.contains(PROJECT_VERSION_STUB)) {
				line = line.replace(PROJECT_VERSION_STUB, DataCenter.instance().getProjectVersion());
				mavenPomLines.add(line);
			} else if (line.contains(PROJECT_GROUP_ID_STUB)) {
				line = line.replace(PROJECT_GROUP_ID_STUB, DataCenter.instance().getProjectGroupId());
				mavenPomLines.add(line);
			} else if (line.contains(MAVEN_SELF_PACKAGE_STUB)){
				if(DataCenter.instance().hasAutoWarpMsg()) {
					mavenPomLines.add("<dependency>");
					mavenPomLines.add("<groupId>fun.enou.alpha</groupId>");
					mavenPomLines.add("<artifactId>fun-enou-alpha-core</artifactId>");
					mavenPomLines.add("<version>0.0.1</version>");
					mavenPomLines.add("</dependency>");
				}

			} else {
				mavenPomLines.add(line);
			}
		}
		
		return mavenPomLines;
	}
	
	
	private List<String> handleOneLine(String inputLine, List<CtrlEntity> ctrlEntityList) {
		
		if(inputLine.contains(IMPORT_STUB)) {
			ArrayList<String> importLineList = new ArrayList<>();
			for(CtrlEntity ctrlEntity : ctrlEntityList)
			{
				if(ctrlEntity.isAutoWrapMsg()) {
					importLineList.add("import fun.enou.core.msg.EnouMsgJson;");
					break;
				}
			}
			return importLineList;
		}
		
		if(inputLine.contains(FEIGN_CLIENT_STUB)) {
			Logger.debug("has application name");
			Logger.debug(inputLine);
			
			Assert.assertFalse(DataCenter.instance().getOriginAppName().isEmpty());
			ArrayList<String> feignClientParamList = new ArrayList<>();
			String valueParam = MessageFormat.format("value=\"{0}\"", DataCenter.instance().getOriginAppName());
			feignClientParamList.add(valueParam);
			
			String feignClientParam = String.join(",", feignClientParamList);


			ArrayList<String> resultList = new ArrayList<>();
			resultList.add(inputLine.replace(FEIGN_CLIENT_STUB, feignClientParam));
			return resultList;
		}

		if(inputLine.contains(APP_NAME_STUB)) {
			Logger.debug("has controller name");
			Logger.debug(inputLine);
			ArrayList<String> resultList = new ArrayList<>();
			resultList.add(inputLine.replace(APP_NAME_STUB, DataCenter.instance().getFormattedAppName()));
			return resultList;
		}

		if(inputLine.contains(METHOD_STUB)) {
			List<String> resultList = new LinkedList<>();
			for(CtrlEntity ctrlEntity : ctrlEntityList) {
				List<String> strLines = ctrlEntity.methodToStringList();
				resultList.addAll(strLines);
			}
			return resultList;
		}

		ArrayList<String> resultList = new ArrayList<>();
		resultList.add(inputLine);
		return resultList;
		
	}
	
	

}
