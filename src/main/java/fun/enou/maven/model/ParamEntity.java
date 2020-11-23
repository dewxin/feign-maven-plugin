package fun.enou.maven.model;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import fun.enou.maven.tool.Logger;

public class ParamEntity {

	// @PathVariable(value="{value}")
	private String annotation = ""; //maybe is null, so we need to set the initial value

	private String paramType;
	private String paramName;
	
	
	public String getAnnotation() {
		return annotation;
	}
	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}
	public String getParamType() {
		return paramType;
	}
	public void setParamType(String paramType) {
		this.paramType = paramType;
	}
	public String getParamName() {
		return paramName;
	}
	public void setParamName(String paramName) {
		this.paramName = paramName;
	}
	
	
}
