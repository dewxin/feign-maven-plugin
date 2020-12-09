package com.github.dewxin;

import java.io.IOException;
import java.util.List;

import javax.xml.crypto.Data;

import com.github.dewxin.file.FileHandler;
import com.github.dewxin.file.JarDataParser;
import com.github.dewxin.file.TemplateHandler;
import com.github.dewxin.tool.DataCenter;
import com.github.dewxin.tool.Logger;
import com.github.dewxin.tool.PhaseMarker;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;


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
		try (PhaseMarker pm = new PhaseMarker("project-info-collecting")) {
			Logger.info("base dir is {0} ", project.getBasedir());
			Logger.info("artifact id is {0} ", project.getArtifactId());
			Logger.info("version is {0} ", project.getVersion());
			DataCenter.instance().setProjectBaseDir(project.getBasedir().getAbsolutePath());
			DataCenter.instance().setProjectArtifactId(project.getArtifactId());
			DataCenter.instance().setProjectGroupId(project.getGroupId());
			DataCenter.instance().setProjectVersion(project.getVersion());
		}

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
