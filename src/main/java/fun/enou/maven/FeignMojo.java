package fun.enou.maven;

import java.io.IOException;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import fun.enou.maven.file.FileHandler;
import fun.enou.maven.file.JarDataParser;
import fun.enou.maven.file.TemplateHandler;
import fun.enou.maven.tool.DataCenter;
import fun.enou.maven.tool.Logger;
import fun.enou.maven.tool.PhaseMarker;


@Mojo(name="feign", defaultPhase=LifecyclePhase.PACKAGE)
public class FeignMojo extends AbstractMojo{

	@Parameter(defaultValue = "${project}", required = true, readonly = true)
	MavenProject project;
	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		Logger.setLog(getLog());
		
		
		try {
			run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void run() throws IOException, ClassNotFoundException, InterruptedException {
		try (PhaseMarker pm = new PhaseMarker("init")) {
			JarDataParser.instance().init();
		}

		List<Class<?>> entryClassList = null;
		try (PhaseMarker pm = new PhaseMarker("loading-classes")) {
			entryClassList = JarDataParser.instance().loadAllClasses();
		}
		
		try(PhaseMarker pm = new PhaseMarker("collecting-controller-and-pojo-class")) {
			DataCenter.instance().collectControllerAndPojo(entryClassList);
		}

		try(PhaseMarker pm = new PhaseMarker("generating-files")) {
			TemplateHandler.instance().init();
			TemplateHandler.instance().generateAllCtrlAndPojo();
			TemplateHandler.instance().generatePomFile();
		}

		try(PhaseMarker pm = new PhaseMarker("maven-install")) {
			TemplateHandler.instance().install();
		}
		
	}
	

}
