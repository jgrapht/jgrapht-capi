package org.jgrapht.capi;

import java.util.Collections;
import java.util.List;

import org.graalvm.nativeimage.c.CContext;

import com.oracle.svm.core.c.ProjectHeaderFile;

public class JGraphTDirectives implements CContext.Directives {

	public JGraphTDirectives() {
	}

	@Override
	public boolean isInConfiguration() {
		return true;
	}

	@Override
	public List<String> getHeaderFiles() {

		/*
		 * The header file with the C declarations that are imported. We use a helper
		 * class that locates the file in our project structure.
		 */
		return Collections.singletonList(ProjectHeaderFile.resolve("org.jgrapht.capi", "jgrapht_capi_types.h"));
	}

}