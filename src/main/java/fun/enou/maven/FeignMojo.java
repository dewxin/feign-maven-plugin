package fun.enou.maven;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import fun.enou.maven.tool.ClassFilter;
import fun.enou.maven.tool.JarHandler;
import fun.enou.maven.tool.Logger;


@Mojo(name="feign", defaultPhase=LifecyclePhase.PACKAGE)
public class FeignMojo extends AbstractMojo{

	@Parameter(defaultValue = "${project}", required = true, readonly = true)
	MavenProject project;
	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		Logger.setLog(getLog());
		
		
		try {
			example();
			learn();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void example() throws IOException, ClassNotFoundException {
		
		JarHandler jarHandler = new JarHandler();
		
		Logger.info("feign mojo is working");

		String path = jarHandler.findJarFileAndRetPath();
		
		List<Class<?>> classList = jarHandler.loadAllClasses(path);
		for(Class<?> aClass : classList) {
			Logger.debug("print all class");
			Logger.debug(aClass.toString());
		}
		
		List<Class<?>> ctrlAnnotatedClassList = 
				classList.stream().filter(ClassFilter::hasCtrlAnote).collect(Collectors.toList());

		for(Class<?> aClass : ctrlAnnotatedClassList) {
			Logger.debug("print controller annotated class");
			Logger.debug(aClass.toString());
		}
		
	}
	
	
	
	private void learn() {
		String packageName = this.getClass().getPackage().getName();
		Logger.debug(packageName);
	}

}
