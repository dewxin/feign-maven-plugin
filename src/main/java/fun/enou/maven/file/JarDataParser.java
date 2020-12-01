package fun.enou.maven.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import fun.enou.maven.tool.DataCenter;
import fun.enou.maven.tool.Logger;
import junit.framework.Assert;

public class JarDataParser {
	
	private String pathToJarFile;

	private static JarDataParser jarDataParser = new JarDataParser();
	public static JarDataParser instance() {
		return jarDataParser;
	}
	
	/**
	 * create the classLoader will be used later, and get the application name
	 * @throws IOException
	 */
	public void init() throws IOException {
		pathToJarFile = findJarFileAndRetPath();
		Assert.assertNotNull(pathToJarFile);
		Logger.debug("pathToJarFile value is {0}", pathToJarFile);
		EnouJarLuncher.newInstance(pathToJarFile).init();
		getApplicationName();
	}
	
	private String findJarFileAndRetPath(){
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
	
	public String getApplicationName() throws IOException {
		try (JarFile jarFile = new JarFile(pathToJarFile)) {
			JarEntry entry = jarFile.getJarEntry("BOOT-INF/classes/application.properties");
			InputStream input = jarFile.getInputStream(entry);
	        BufferedReader br = new BufferedReader(new InputStreamReader(input));
	        String line = "";
	        while((line = br.readLine())!=null) {
	        	String[] splitStr = line.split("=");
				if(splitStr[0].equals("spring.application.name"))
				{
					String applicationName = splitStr[1];
					Logger.debug("application name is "+ applicationName);
					DataCenter.instance().setApplicationName(applicationName);
					return applicationName;
				}
	        }
		}
		
		return null;
	}


	public List<Class<?>> loadAllClasses() throws IOException, ClassNotFoundException   {
		try (JarFile jarFile = new JarFile(pathToJarFile)) {
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

			LinkedList<Class<?>> classList = new LinkedList<>();
			Enumeration<JarEntry> e = jarFile.entries();
			while (e.hasMoreElements()) {
				JarEntry javaEntry = e.nextElement();

				boolean isDir = javaEntry.isDirectory();
				boolean isClass = javaEntry.getName().endsWith("class");
				boolean isSource = javaEntry.getName().startsWith("BOOT-INF/classes/");

				if (isDir || !isClass || !isSource)
					continue;
				
				int prefixSize = "BOOT-INF/classes/".length();
				int suffixSize= ".class".length();
				String className = javaEntry.getName().substring(prefixSize, javaEntry.getName().length() - suffixSize);
				className = className.replace('/', '.');
				
				try {
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
	

}
