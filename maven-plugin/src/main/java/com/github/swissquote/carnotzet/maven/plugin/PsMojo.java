package com.github.swissquote.carnotzet.maven.plugin;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

/**
 * Lists the state of the carnotzet containers
 */
@Mojo(name = "ps", defaultPhase = LifecyclePhase.NONE, threadSafe = true, requiresDependencyResolution = ResolutionScope.COMPILE)
public class PsMojo extends AbstractZetMojo {

	@Override
	public void executeInternal() throws MojoExecutionException, MojoFailureException {
		getRuntime().status();
	}

}
