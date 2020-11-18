package fun.enou.maven.model;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import fun.enou.maven.file.FileHandler;
import fun.enou.maven.tool.Logger;

public class PojoEntity {
	
	private static final String POJO_NAME_STUB = "^pojoNameStub$";
	private static final String FIELD_STUB = "^fieldStub$";

	private static HashSet<String> pojoToBeGenerated = new HashSet<>();
	private static HashSet<String> pojoGenerated = new HashSet<>();
	
	public static boolean addPojoToGenerate(String classFullName) {
		if(classFullName.equals("void"))
			return false;
		if(classFullName.startsWith("java."))
			return false;
		if(pojoToBeGenerated.contains(classFullName)) 
			return false;

		pojoToBeGenerated.add(classFullName);
		return true;
	}
	
	//todo rebuild
	public static void generateAllPojo(List<String> pojoTemplateLines) throws ClassNotFoundException, IOException {
		
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		for(String className : pojoToBeGenerated) {
			try {
				
				Class<?> aClass = Class.forName(className, true, classLoader);
				if(pojoGenerated.contains(className))
					continue;
				
				pojoGenerated.add(className);
				List<String> pojoLines = generateOnePojo(aClass, pojoTemplateLines);
				String fileName = FileHandler.getBaseDir() + aClass.getSimpleName() + ".java";

				FileHandler.writeToFile(fileName, pojoLines);
			} catch (ClassNotFoundException exception) {
				Logger.debug(className + "not found ");
				Logger.debug(exception.getMessage());
			}
		}
	}
	
	private static List<String> generateOnePojo(Class<?> aClass, List<String> pojoTemplateLines) {
		
		LinkedList<String> lineList = new LinkedList<>();
		for(String line : pojoTemplateLines) {
			if(line.contains(POJO_NAME_STUB)) {
				lineList.add(line.replace(POJO_NAME_STUB, aClass.getSimpleName()));
				continue;
			} 
			
			if(line.contains(FIELD_STUB)) {
				
				for(Field field : aClass.getDeclaredFields()) {
					
					String className = field.getType().getSimpleName();
					String fieldName = field.getName();
					
					String fieldLine = MessageFormat.format("private {0} {1};", className, fieldName);
					
					
					String fieldCaptureName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
					String setLine =MessageFormat.format(
							"public void set{2}({1} {0}) '{'this.{0}={0};'}'",
							fieldName, className, fieldCaptureName);
					
					String getLine =MessageFormat.format(
							"public {1} get{2}() '{' return this.{0};'}'",
							fieldName, className, fieldCaptureName);

					lineList.add(fieldLine);
					lineList.add(setLine);
					lineList.add(getLine);
				}
				
				continue;
			}
			
			lineList.add(line);
		}
		
		return lineList;
	}

}
