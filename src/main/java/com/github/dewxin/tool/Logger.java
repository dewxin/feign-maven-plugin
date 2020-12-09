package com.github.dewxin.tool;

import java.text.MessageFormat;

import org.apache.maven.plugin.logging.Log;

public class Logger {
	
	private static Log log;
	

	public static void setLog(Log log) {
		Logger.log = log;
	}

	public static void error(CharSequence charSequence) {
		log.error(charSequence);
	}
	
	public static void warn(CharSequence charSequence) {
		log.warn(charSequence);
	}

	public static void info (CharSequence charSequence) {
		log.info(charSequence);
	}

	public static void info (String format, Object... args) {
		String str = MessageFormat.format(format, args);
				
		log.info(str);
	}	
	
	public static void debug (CharSequence charSequence) {
		log.debug(charSequence);
	}

	public static void debug (String format, Object... args) {
		String str = MessageFormat.format(format, args);
				
		log.debug(str);
	}	

}
