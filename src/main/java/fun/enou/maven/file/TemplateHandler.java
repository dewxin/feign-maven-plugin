package fun.enou.maven.file;

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
import java.util.stream.Collectors;

import edu.emory.mathcs.backport.java.util.Collections;
import fun.enou.maven.model.CtrlEntity;
import fun.enou.maven.model.PojoEntity;
import fun.enou.maven.tool.Logger;
import junit.framework.Assert;


public class TemplateHandler {
	
	private static final String INTERFACE_FILE_NAME ="/feign-client-interface-template.txt";
	private static final String POJO_FILE_NAME ="/feign-client-pojo-template.txt";
	private static final String MAVEN_POM_FILE_NAME ="/feign-maven-template.txt";
	private static final String FEIGN_CLIENT_STUB = "^feignClientAnnotationStub$";
	private static final String CONTROLLER_NAME_STUB = "^controllerNameStub$";
	private static final String METHOD_STUB = "^methodStub$"; 
	private static final String APPLICATION_NAME_STUB = "^applicationNameStub$"; 
	
	private static final String IMPORT_STUB = "^importStub$";

	
	
	private List<String> interfaceTemplateLines = new LinkedList<>();
	private List<String> pojoTemplateLines = new LinkedList<>();
	
	private String applicationName = "";
	private List<CtrlEntity> controllerList;
	
	
	public TemplateHandler(String applicationName, List<CtrlEntity> controllerList) {
		this.applicationName = applicationName;
		this.controllerList = controllerList;
	}
	
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
	}
	
	public void readPojoResource() throws IOException {
		InputStream path = this.getClass().getResourceAsStream(POJO_FILE_NAME);
		BufferedReader reader = new BufferedReader(new InputStreamReader(path));
		
		String line = "";
		while((line = reader.readLine()) != null) {
			pojoTemplateLines.add(line);
		}
	}
	
	public List<String> generateControllerFile(CtrlEntity ctrlEntity) {
		List<String> outputLines = new LinkedList<>();
		for(String line : interfaceTemplateLines) {
			List<String> lines = handleOneLine(line, ctrlEntity);
			outputLines.addAll(lines);
		}
		
		return outputLines;
	}
	
	//todo need tab to make the code look beautiful
	public void generateAllCtrlAndPojo() throws IOException, ClassNotFoundException, InterruptedException {
		String baseDir = FileHandler.getCodeDir();
		File dir = new File(baseDir);
		if(!dir.exists())
			dir.mkdirs();
		
		for(CtrlEntity ctrlEntity : controllerList) {
			List<String> lines = generateControllerFile(ctrlEntity);
			FileHandler.writeToFile(baseDir+ctrlEntity.getName()+"Client.java",lines);
		}
		
		PojoEntity.generateAllPojo(pojoTemplateLines);
		List<String> pomLines = getMavenPomLines();
		FileHandler.writeToFile(FileHandler.getBaseDir()+"pom.xml", pomLines);
		ProcessBuilder pb = new ProcessBuilder("mvn.cmd", "install", "-f", FileHandler.getBaseDir()+"pom.xml");
		pb.redirectErrorStream();
		Process process = pb.start();
		process.waitFor();
		System.out.println(process.exitValue());
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "GBK"))) {
		    String result = reader.lines()
		            .collect(Collectors.joining("\n"));
		    System.out.println(result);
		}
	}
	
	private List<String> getMavenPomLines() throws IOException {
		InputStream inputStream =this.getClass().getResourceAsStream(MAVEN_POM_FILE_NAME);
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		
		List<String> mavenPomLines = new LinkedList<String>();
		String line = "";
		while((line = reader.readLine()) != null) {
			if(line.contains(APPLICATION_NAME_STUB))
				line = line.replace(APPLICATION_NAME_STUB, applicationName.toLowerCase());
			mavenPomLines.add(line);
		}
		
		return mavenPomLines;
	}
	
	
	
	private List<String> handleOneLine(String inputLine, CtrlEntity ctrlEntity) {
		
		if(inputLine.contains(IMPORT_STUB)) {
			ArrayList<String> importLineList = new ArrayList<>();
			if(ctrlEntity.IsAutoWrapMsg()) {
				importLineList.add("import fun.enou.core.msg.EnouMsgJson;");
				
			}
			
			return importLineList;
		}
		
		if(inputLine.contains(FEIGN_CLIENT_STUB)) {
			Logger.debug("has application name");
			Logger.debug(inputLine);
			
			Assert.assertFalse(applicationName.isEmpty());
			ArrayList<String> feignClientParamList = new ArrayList<>();
			String valueParam = MessageFormat.format("value=\"{0}\"", applicationName);
			feignClientParamList.add(valueParam);
			
			if(!ctrlEntity.getPath().isEmpty()) {
				String pathParam = MessageFormat.format("path=\"{0}\"", ctrlEntity.getPath());
				feignClientParamList.add(pathParam);
			}
			
			String feignClientParam = String.join(",", feignClientParamList);


			ArrayList<String> resultList = new ArrayList<>();
			resultList.add(inputLine.replace(FEIGN_CLIENT_STUB, feignClientParam));
			return resultList;
		}

		if(inputLine.contains(CONTROLLER_NAME_STUB)) {
			Logger.debug("has controller name");
			Logger.debug(inputLine);
			ArrayList<String> resultList = new ArrayList<>();
			resultList.add(inputLine.replace(CONTROLLER_NAME_STUB, ctrlEntity.getName()));
			return resultList;
		}

		if(inputLine.contains(METHOD_STUB)) {
			return ctrlEntity.methodToStringList();
		}

		ArrayList<String> resultList = new ArrayList<>();
		resultList.add(inputLine);
		return resultList;
		
	}
	
	

}
