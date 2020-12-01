package fun.enou.maven.tool;

import java.lang.annotation.Annotation;
import java.text.MessageFormat;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

public class Filter {
	
	private Filter() {
	}

	public static boolean hasRestCtrlAnote(Class<?> aClass) {
		
		String message = MessageFormat.format(
				"{0} class annotation length is {1}", aClass.getName(), aClass.getAnnotations().length);
		Logger.debug(message);

		boolean hasCtrlAnote = false;
		boolean hasResbodyAnote = false;

		for(Annotation annotation : aClass.getAnnotations()) {
			Logger.debug("annotation name is "+annotation.toString());

			if(annotation instanceof RestController)
				return true;
			
			if(annotation instanceof Controller)
				hasCtrlAnote = true;
			
			if(annotation instanceof ResponseBody)
				hasResbodyAnote = true;
		}
		
		if(hasCtrlAnote && hasResbodyAnote)
			return true;

		return false;
	}
	
	public static boolean isHttpMappingAnnotation(Annotation annotation) {
		return (annotation instanceof GetMapping|| annotation instanceof PostMapping
				|| annotation instanceof PatchMapping || annotation instanceof DeleteMapping);
	}


}
