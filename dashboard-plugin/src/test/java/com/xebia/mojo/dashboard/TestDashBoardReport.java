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
package com.xebia.mojo.dashboard;

import java.util.Arrays;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;


public class TestDashBoardReport implements DashboardReport {

    private static final List COLUMN_NAMES = Arrays.asList(new String[] {"col1", "col2", "col3", "col4"});

    public boolean canExecute(MavenProject subProject) {
        return true;
    }

    public Node getContent(MavenProject subProject, String column) throws MojoExecutionException {
        return DocumentHelper.createText("Test " + COLUMN_NAMES.indexOf(column));
    }

    public String getHeaderForColumn(String column) {
        return "Header " + COLUMN_NAMES.indexOf(column);
    }

    public String getLinkLocation() {
        return null;
    }

    public int numberOfColumns() {
        return COLUMN_NAMES.size();
    }

    public String title() {
        return "Test";
    }

    public List getColumnNames() {
        return COLUMN_NAMES;
    }
}
