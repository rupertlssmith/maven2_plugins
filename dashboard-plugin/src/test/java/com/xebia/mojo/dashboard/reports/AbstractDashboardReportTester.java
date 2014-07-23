/*
 * Copyright 2007 Xebia BV, the Netherlands.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xebia.mojo.dashboard.reports;

import java.io.File;

import junit.framework.TestCase;

import com.xebia.mojo.dashboard.mocks.MockMavenProject;


/**
 * Base class that can be used for all tests for a {@link HtmlFileXPathReport}. The
 * {@link #getBaseDir(File)} is used to extract the directory from the path of the test file. This
 * path is needed to create a {@link MockMavenProject}.
 */
public abstract class AbstractDashboardReportTester extends TestCase {

    protected File getBaseDir(File testFile) {
        String path = testFile.getAbsolutePath();
        path = path.substring(0, path.lastIndexOf(File.separator));
        return new File(path);
    }
}
