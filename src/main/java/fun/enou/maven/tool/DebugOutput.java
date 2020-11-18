package fun.enou.maven.tool;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

public class DebugOutput {

	public static void annotatedClass(List<Class<?>> ctrlAnnotatedClassList) {
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
					if(Filter.isSpecificMapping(annotation) || annotation instanceof RequestMapping) {
						
						Logger.debug("annotation is " + annotation.toString());
						Logger.debug("annotated method is " + m);
					}
				}
			}
		}
		
		Logger.debug("--- end print class request mapping methods ---");
	}
}
