package fun.enou.maven.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.LookAndFeel;

import fun.enou.maven.tool.Logger;

public class JarHandler {
	
	private String pathToJarFile;
	
	public void init() {
		try {
			pathToJarFile = findJarFileAndRetPath();
			EnouJarLuncher.newInstance(pathToJarFile).init();
		} catch (Exception e) {
			Logger.warn(e.getMessage());
		}
	}
	
	private String findJarFileAndRetPath() throws MalformedURLException {
		File targeDir = new File("target");

		String[] filenameArray = targeDir.list((dir,name)->{return name.matches("(.*).jar");});
		if(filenameArray.length != 1) {
			Logger.warn("more than one jar.original file found in target diretory");
		}
		
		File origin = new File("target/"+filenameArray[0]);
		
		String copyFileName = MessageFormat.format("target/{0}.copy", origin.getName());
		File originCopy = new File(copyFileName);
		if(originCopy.exists())
			originCopy.delete();
		
		try {
			Files.copy(origin.toPath(), originCopy.toPath());
		} catch (IOException e) {
			Logger.warn(e.getMessage());
		}
		
		pathToJarFile = originCopy.getAbsolutePath();

		return pathToJarFile;
	}
	

	public List<Class<?>> loadAllClasses() throws IOException, ClassNotFoundException   {
		try (JarFile jarFile = new JarFile(pathToJarFile)) {
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			

			LinkedList<Class<?>> classList = new LinkedList<Class<?>>();
			Enumeration<JarEntry> e = jarFile.entries();
			while (e.hasMoreElements()) {
				JarEntry javaEntry = e.nextElement();
				if (javaEntry.isDirectory() || !javaEntry.getName().endsWith(".class")) {
					continue;
				}
				
				String className = javaEntry.getName().substring(17, javaEntry.getName().length() - 6);
				className = className.replace('/', '.');
				
				//attention:
				//have to load class in this way, otherwise cannot find the annotation.
				try {
					Logger.debug(className);
					Class<?> aClass = Class.forName(className, true, classLoader);
					classList.add(aClass);
				} catch (NoClassDefFoundError error) {
					Logger.warn(error.getMessage());
				} catch (ClassNotFoundException exception) {
					Logger.warn(exception.getMessage());
				}

			}

			return classList;
		}
	}
	
	public String getApplicationName() throws IOException {
		try (JarFile jarFile = new JarFile(pathToJarFile)) {
			JarEntry entry = jarFile.getJarEntry("BOOT-INF/classes/application.properties");
			InputStream input = jarFile.getInputStream(entry);
	        BufferedReader br = new BufferedReader(new InputStreamReader(input));
	        String line = "";
	        while((line = br.readLine())!=null) {
	        	String[] splitStr = line.split("=");
	        	if(splitStr[0].equals("spring.application.name"))
	        		return splitStr[1];
	        }
		}
		
		return null;
	}

}
