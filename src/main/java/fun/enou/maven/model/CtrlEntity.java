package fun.enou.maven.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;

import fun.enou.maven.tool.DataCenter;
import fun.enou.maven.tool.Filter;
import fun.enou.maven.tool.Logger;

public class CtrlEntity {
	
	private String name= ""; // {name}Controller  
	private String path= ""; 
	
	private boolean isAutoWrapMsg; // todo make this funtion as a module plugin 
	
	private List<MethodEntity> methodEntityList = new LinkedList<>();
	
	
	public List<String> methodToStringList() {
		ArrayList<String> arrayList = new ArrayList<>();
		for(MethodEntity methodEntity: methodEntityList) {
			arrayList.addAll(methodEntity.toStringList());
		}
		return arrayList;
	}
	
	public static CtrlEntity toEntity(Class<?> aClass) {
		CtrlEntity ctrlEntity = new CtrlEntity();
		ctrlEntity.name = aClass.getSimpleName().replace("Controller", "");
		
		for(Annotation annotation : aClass.getAnnotations()) {
			ctrlEntity.isAutoWrapMsg = annotation.toString().contains("AutoWrapMsg");
			if(ctrlEntity.isAutoWrapMsg){
				DataCenter.instance().setHasAutoWrapMsg(true);
			}
			Logger.debug("{0} has annotation autoWrapMsg", ctrlEntity.name);
		}
		RequestMapping reqMap = aClass.getAnnotation(RequestMapping.class);
		if(reqMap != null)
			Logger.debug(MessageFormat.format("{0} has {1} ", ctrlEntity.name, reqMap.toString()));
		if(reqMap != null &&  reqMap.value().length > 0) { // todo handle path annotation
			Logger.debug(MessageFormat.format("{0} has path", ctrlEntity.name));
			for(String path: reqMap.value()) {
				Logger.debug(path);
			}
			ctrlEntity.path = reqMap.value()[0];
		}
		
		List<Method> methodArray = getMapAnnotatedMethod(aClass);

		for(Method method : methodArray) {
			MethodEntity methodEntity = MethodEntity.toEntity(method, ctrlEntity);
			ctrlEntity.addMethodEntity(methodEntity);
		}

		return ctrlEntity;
	}

	public static List<Method> getMapAnnotatedMethod(Class<?> aClass) {
		List<Method> methodList = new LinkedList<>();
		Method[] methodArray = aClass.getDeclaredMethods();

		for(Method method : methodArray) {
			//todo handle requestMapping as well
			for(Annotation annotation: method.getAnnotations()) {
				if(Filter.isHttpMappingAnnotation(annotation)) {
					methodList.add(method);
					break;
				}
			}
		}

		return methodList;
	}
	
	public boolean isAutoWrapMsg() {
		return this.isAutoWrapMsg;
	}
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void addMethodEntity(MethodEntity method) {
		methodEntityList.add(method);
	}

}
