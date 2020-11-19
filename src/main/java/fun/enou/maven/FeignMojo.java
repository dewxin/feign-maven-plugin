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

import fun.enou.maven.file.JarHandler;
import fun.enou.maven.file.TemplateHandler;
import fun.enou.maven.model.CtrlEntity;
import fun.enou.maven.model.DataHolder;
import fun.enou.maven.tool.Filter;
import fun.enou.maven.tool.DebugOutput;
import fun.enou.maven.tool.Logger;


@Mojo(name="feign", defaultPhase=LifecyclePhase.PACKAGE)
public class FeignMojo extends AbstractMojo{

	@Parameter(defaultValue = "${project}", required = true, readonly = true)
	MavenProject project;
	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		Logger.setLog(getLog());
		
		
		try {
			run();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	private void run() throws IOException, ClassNotFoundException, InterruptedException {
		
		JarHandler jarHandler = new JarHandler();
		
		Logger.info("feign mojo is working");

		jarHandler.init();
		
		Logger.debug("");
		Logger.debug("application name is "+jarHandler.getApplicationName());
		DataHolder.instance().setApplicationName(jarHandler.getApplicationName());
		
		List<Class<?>> classList = jarHandler.loadAllClasses();
		
		List<Class<?>> ctrlAnnotatedClassList = 
				classList.stream().filter(Filter::hasRestCtrlAnote).collect(Collectors.toList());

		
		DebugOutput.annotatedClass(ctrlAnnotatedClassList);
		DebugOutput.annotateMethod(ctrlAnnotatedClassList);
		
		for(Class<?> aClass : ctrlAnnotatedClassList) {
			CtrlEntity ctrlEntity = CtrlEntity.toEntity(aClass);
			DataHolder.instance().addCtrlEntity(ctrlEntity);
		}
		
		TemplateHandler templateHandler = 
				new TemplateHandler(DataHolder.instance().getApplicationName(), DataHolder.instance().getCtrlEntityList());
		
		templateHandler.init();
		
		templateHandler.generateAllCtrlAndPojo();
		
		
		//todo genereate restTemplate and package the genereated file
		
	}
	

}
