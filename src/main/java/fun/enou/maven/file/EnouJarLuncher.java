package fun.enou.maven.file;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.springframework.boot.loader.JarLauncher;
import org.springframework.boot.loader.archive.Archive;
import org.springframework.boot.loader.archive.JarFileArchive;
import org.springframework.boot.loader.jar.JarFile;

import fun.enou.maven.tool.Logger;

/**
 * use the springframwork loader to load the class
 * @author xinlu
 *
 */
public class EnouJarLuncher extends JarLauncher{
	
	public EnouJarLuncher(Archive archive) {
		super(archive);
	}
	
	public void init(){
		if (!isExploded()) {
			JarFile.registerUrlProtocolHandler();
		}

		try {
			ClassLoader classLoader = createClassLoader(getClassPathArchivesIterator());
			Thread.currentThread().setContextClassLoader(classLoader);
		} catch (Exception e) {
			Logger.error("cannot create classLoader");
			Logger.error(Arrays.toString(e.getStackTrace()));
			System.exit(1);
		}
	}
	
	
	public static EnouJarLuncher newInstance(String path) throws IOException   {
		File root = new File(path);
		if (!root.exists()) {
			throw new IllegalStateException("Unable to determine code source archive from " + root);
		}
		Archive archive = new JarFileArchive(root);
		return new EnouJarLuncher(archive);
	}
}
