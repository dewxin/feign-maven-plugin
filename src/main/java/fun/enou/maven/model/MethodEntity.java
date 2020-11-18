package fun.enou.maven.model;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import fun.enou.maven.tool.Logger;


public class MethodEntity {
	
	private CtrlEntity ctrlEntity;
	//annotation
	private String mapType = "";
	private String mapPath = "";
	
	//method
	private String retType = ""; 
	private String methodName = "";
	private List<String> parameterTypeList = new ArrayList<String>();
	
	
	
	public List<String> toStringList() {
		ArrayList<String> arrayList = new ArrayList<>();
		
		String annotation = MessageFormat.format("@{0}(\"{1}\")", mapType, mapPath);
		arrayList.add(annotation);
		
		List<String> paramTypeWithVarList = new ArrayList<>();
		
		int index = 0;
		for(String paramType : parameterTypeList) {
			index++;
			String paramTypeWithVar = MessageFormat.format("{0} {1}", paramType, "arg"+index);
			paramTypeWithVarList.add(paramTypeWithVar);
		}
		
		String params = String.join(",", paramTypeWithVarList);
		String method = MessageFormat.format("{0} {1}({2});", retType, methodName, params);
		arrayList.add(method);
		
		return arrayList;
	}
	
	
	public static MethodEntity toEntity(Method method, CtrlEntity ctrlEntity) {
		Logger.debug("method to entity , method is " + method.toGenericString());
		MethodEntity methodEntity = new MethodEntity();
		methodEntity.ctrlEntity = ctrlEntity;
		
		//todo handle requestMapping
		//todo rebuild the code , find a better way
		GetMapping getRequestMothed = (GetMapping) method.getAnnotation(GetMapping.class);
		if(getRequestMothed != null) {
			methodEntity.mapType = "GetMapping";
			if(getRequestMothed.value().length > 0)
				methodEntity.mapPath = getRequestMothed.value()[0];
		}
		
	    PutMapping putRequestMothed = (PutMapping) method.getAnnotation(PutMapping.class);
	    if(putRequestMothed != null) {
			methodEntity.mapType = "PutMapping";
			if(putRequestMothed.value().length > 0)
				methodEntity.mapPath = putRequestMothed.value()[0];
	    }
	    
	    PatchMapping patchRequestMothed = (PatchMapping)method.getAnnotation(PatchMapping.class);
	    if(patchRequestMothed != null) {
			methodEntity.mapType = "PatchMapping";
			if(patchRequestMothed.value().length > 0)
				methodEntity.mapPath = patchRequestMothed.value()[0];
	    }	
	    
	    PostMapping postRequestMothed = (PostMapping) method.getAnnotation(PostMapping.class);
	    if(postRequestMothed != null) {
			methodEntity.mapType = "PostMapping";
			if(postRequestMothed.value().length > 0)
				methodEntity.mapPath = postRequestMothed.value()[0];
	    }
	    
	    DeleteMapping deleteRequestMothed = (DeleteMapping)method.getAnnotation(DeleteMapping.class);
	    if(deleteRequestMothed != null) {
			methodEntity.mapType = "DeleteMapping";
			if(deleteRequestMothed.value().length > 0)
				methodEntity.mapPath = deleteRequestMothed.value()[0];
	    }	
		
		methodEntity.methodName = method.getName();
		
		
		methodEntity.retType = method.getGenericReturnType().getTypeName();
		
		Logger.debug("method.getReturnType().getName() " + method.getReturnType().getName());
		Logger.debug("method.getGenericReturnType().getTypeName() " + method.getGenericReturnType().getTypeName());
		Logger.debug("method.getReturnType().getClass().toGenericString() " + method.getReturnType().getClass().toGenericString());
		Logger.debug("method.getReturnType().getSimpleName() " + method.getReturnType().getSimpleName());
		Logger.debug("method.getReturnType().getClass().getName() " + method.getReturnType().getClass().getName());

		if(methodEntity.retType.contains("ResponseEntity")) {
			
			methodEntity.retType = TypeEntityByStrFormat.getInnerGenriceType(methodEntity.retType);
		}

		methodEntity.retType = TypeEntityByStrFormat.parse(methodEntity.retType).getSelfDefSimpleName();
		if(ctrlEntity.IsAutoWrapMsg() && !methodEntity.retType.equals("void")) {
			methodEntity.retType = "EnouMsgJson<" + methodEntity.retType + ">";
		}

		for(Class<?> type: method.getParameterTypes()) {
			TypeEntityByStrFormat typeEntity = TypeEntityByStrFormat.parse(type.getName());
			methodEntity.parameterTypeList.add(typeEntity.getSelfDefSimpleName());
		}
		
		return methodEntity;
	}
	
	
	public String getMapType() {
		return mapType;
	}
	public void setMapType(String mapType) {
		this.mapType = mapType;
	}
	public String getMapPath() {
		return mapPath;
	}
	public void setMapPath(String mapPath) {
		this.mapPath = mapPath;
	}
	public String getRetType() {
		return retType;
	}
	public void setRetType(String retType) {
		this.retType = retType;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public void addParameterType(String type) {
		parameterTypeList.add(type);
	}
	
	

	
	
}
