package com.github.dewxin.tool;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;

public class DebugOutput {
	
	private DebugOutput() {
		throw new IllegalStateException("Utility class");
	}

	public static void annotatedClassAndMethod(List<Class<?>> classList) {
		annotateClass(classList);
		annotateMethod(classList);
	}

	public static void annotateClass(List<Class<?>> ctrlAnnotatedClassList) {
		Logger.debug("");
		Logger.debug("--- start print annotated class ---");
		for(Class<?> aClass : ctrlAnnotatedClassList) {
			Logger.debug(aClass.toString());
		}
		Logger.debug("---  end print annotated class ---");
	}
	
	public static void annotateMethod(List<Class<?>> ctrlAnnotatedClassList) {
		Logger.debug("");
		Logger.debug("--- start print class request mapping methods ---");
		
		for(Class<?> aClass : ctrlAnnotatedClassList) {
			Logger.debug("");
			String debugLog = MessageFormat.format("--print {0} class declaredMethods--", aClass.getName());
			Logger.debug(debugLog);
			Method[] moMethods = aClass.getDeclaredMethods();
			for(Method m : moMethods) {
				Annotation[] methodAnnotations = m.getAnnotations();
				for(Annotation annotation : methodAnnotations) {
					if(Filter.isHttpMappingAnnotation(annotation) || annotation instanceof RequestMapping) {
						
						Logger.debug("annotation is " + annotation.toString());
						Logger.debug("annotated method is " + m);
					}
				}
			}
		}
		
		Logger.debug("--- end print class request mapping methods ---");
	}
}
