package fun.enou.maven.tool;

import org.apache.maven.plugin.logging.Log;

public class Logger {
	
	private static Log log;
	
	

	public static void setLog(Log log) {
		Logger.log = log;
	}

	public static void info (CharSequence charSequence) {
		log.info(charSequence);
	}
	
	public static void debug (CharSequence charSequence) {
		log.debug(charSequence);
	}

}
