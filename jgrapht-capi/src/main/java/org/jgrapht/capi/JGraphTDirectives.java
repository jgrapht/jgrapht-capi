/*
 * (C) Copyright 2020, by Dimitrios Michail.
 *
 * JGraphT C-API
 *
 * See the CONTRIBUTORS.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the
 * GNU Lesser General Public License v2.1 or later
 * which is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1-standalone.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR LGPL-2.1-or-later
 */
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