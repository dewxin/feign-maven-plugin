package fun.enou.maven.model;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;

import fun.enou.maven.file.FileHandler;
import fun.enou.maven.tool.DataCenter;
import fun.enou.maven.tool.Logger;

public class PojoEntity {
	
	private static final String POJO_NAME_STUB = "^pojoNameStub$";
	private static final String FIELD_STUB = "^fieldStub$";

	public static void generateAllPojo(List<String> pojoTemplateLines) throws ClassNotFoundException, IOException {
		Logger.debug("start generating all pojo");
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		for(String className : DataCenter.instance().getPojoClassNameList()){
			try {
				
				Class<?> aClass = Class.forName(className, true, classLoader);
				List<String> pojoLines = generateOnePojo(aClass, pojoTemplateLines);
				String fileName = FileHandler.getCodeDir() + aClass.getSimpleName() + ".java";

				FileHandler.writeToFile(fileName, pojoLines);
			} catch (ClassNotFoundException exception) {
				Logger.warn(className + "not found ");
				Logger.warn(exception.getMessage());
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
					
					
					String className = field.getGenericType().getTypeName();
					className = TypeNameEntity.parse(className).getSelfDefSimpleName();
					String fieldName = field.getName();
					// if the filed's class is not in the pojoTobeGenerated or pojoGenerated,
					// then put it in the pojoTobeGenerated
					// and be careful here, if It's a list or array, get the inner type
					
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
