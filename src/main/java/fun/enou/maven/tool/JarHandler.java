package fun.enou.maven.tool;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarHandler {
	
	
	public String findJarFileAndRetPath() throws IOException {
		File targeDir = new File("target");

		String[] filenameArray = targeDir.list((dir,name)->{return name.matches("(.*).jar.original");});
		if(filenameArray.length != 1) {
			throw new IOException("more than one jar.original file found in target diretory");
		}

		
		File origin = new File("target/"+filenameArray[0]);
		
		String copyFileName = MessageFormat.format("target/{0}.copy", origin.getName());
		File originCopy = new File(copyFileName);
		if(originCopy.exists())
			originCopy.delete();
		
		Files.copy(origin.toPath(), originCopy.toPath());
		
		return originCopy.getAbsolutePath();

	}

	public List<Class<?>> loadAllClasses(String pathToJar) throws IOException, ClassNotFoundException   {
		try (JarFile jarFile = new JarFile(pathToJar)) {


			File fileForUrl = new File(pathToJar);
			URLClassLoader classLoader = new URLClassLoader(
					new URL[] {fileForUrl.toURI().toURL()},
					this.getClass().getClassLoader()
			);
			
			LinkedList<Class<?>> classList = new LinkedList<Class<?>>();
			Enumeration<JarEntry> e = jarFile.entries();
			while (e.hasMoreElements()) {
				JarEntry javaEntry = e.nextElement();
				if (javaEntry.isDirectory() || !javaEntry.getName().endsWith(".class")) {
					continue;
				}
				
				String className = javaEntry.getName().substring(0, javaEntry.getName().length() - 6);
				className = className.replace('/', '.');
				
				//attention:
				//have to load class in this way, otherwise cannot find the annotation.
				Class<?> aClass = Class.forName(className, true, classLoader);

				classList.add(aClass);
			}

			return classList;
		}
	}

}
