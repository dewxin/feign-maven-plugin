package fun.enou.maven.tool;

import java.lang.annotation.Annotation;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

public class ClassFilter {
	
	public static boolean hasCtrlAnote(Class<?> aClass) {
		
		
		Logger.debug("class annotation length is "+aClass.getAnnotations().length);
		for(Annotation annotation : aClass.getAnnotations()) {
			Logger.debug("annotation name is "+annotation.toString());
			if(annotation instanceof Controller || annotation instanceof RestController) {
				return true;
			}
		}

		return false;
		
	}

}
