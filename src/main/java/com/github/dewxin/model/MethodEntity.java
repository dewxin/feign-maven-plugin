package com.github.dewxin.model;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import com.github.dewxin.tool.Logger;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;


public class MethodEntity {
	
	private CtrlEntity ctrlEntity;
	//annotation
	private String mapType = "";
	private String mapPath = "";
	
	//method
	private String retType = ""; 
	private String methodName = "";
	private ArrayList<ParamEntity> parameterList = new ArrayList<ParamEntity>();
	
	
	public List<String> toStringList() {
		ArrayList<String> arrayList = new ArrayList<>();
		
		String annotation = MessageFormat.format("@{0}(\"{1}\")", mapType, ctrlEntity.getPath() + mapPath);
		arrayList.add(annotation);
		
		List<String> paramTypeWithVarList = new ArrayList<>();
		
		for(ParamEntity paramEntity : parameterList) {

			String paramType = paramEntity.getParamType();
			String paramName = paramEntity.getParamName();
			String paramAnote = paramEntity.getAnnotation();
			String paramTypeWithVar = MessageFormat.format("{2} {0} {1}", paramType, paramName, paramAnote);
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
			
			methodEntity.retType = TypeNameEntity.getInnerGenriceType(methodEntity.retType);
		}

		methodEntity.retType = TypeNameEntity.parse(methodEntity.retType).getSelfDefSimpleName();
		if(ctrlEntity.isAutoWrapMsg() && !methodEntity.retType.equals("void")) {
			methodEntity.retType = "EnouMsgJson<" + methodEntity.retType + ">";
		}

		for(Parameter param: method.getParameters()) {
			
			ParamEntity paramEntity = new ParamEntity();
			
			TypeNameEntity typeEntity = TypeNameEntity.parse(param.getType().getName());
			paramEntity.setParamName(param.getName());
			paramEntity.setParamType(typeEntity.getSelfDefSimpleName());
			
			// could only exists one.
			PathVariable pathVariable = param.getAnnotation(PathVariable.class);
			if(pathVariable != null){
				// @PathVariable(value="value")
				String pathVariableValue = pathVariable.value();
				if(pathVariableValue.equals(""))
					pathVariableValue = param.getName();

				String annotationStr = MessageFormat.format(
						"@PathVariable(value=\"{0}\")", pathVariableValue);
				
				paramEntity.setAnnotation(annotationStr);
			}
			
			RequestParam requestParam = param.getAnnotation(RequestParam.class);
			if(requestParam != null) {
				String requestParamValue = requestParam.value();
				if(requestParamValue.equals(""))
					requestParamValue = param.getName();
				
				String annotationStr = MessageFormat.format(
						"@RequestParam(value=\"{0}\")", requestParamValue);
				
				paramEntity.setAnnotation(annotationStr);
			}
			
			
			methodEntity.parameterList.add(paramEntity);
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

	
	
	
}
