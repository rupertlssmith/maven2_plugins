/*
 * Copyright The Sett Ltd, 2005 to 2014.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xebia.mojo.dashboard;

import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.dom4j.Node;

/**
 * Interface for dashboard reports.
 *
 * @author <a href="mailto:mwinkels@xebia.com">Maarten Winkels</a>
 * @author <a href="mailto:lvonk@xebia.com">Lars Vonk</a>
 * @author <a href="mailto:jvanerp@xebia.com">Jeroen van Erp</a>
 */
public interface DashboardReport
{
    /**
     * Title of this report.
     *
     * @return The title of this report.
     */
    String title();

    /**
     * Returns the title of the column shown on the screen in the dashboard. A report may have multiple columns to show
     * the information on the dashboard.
     *
     * @param  the column number on the screen shown in the dashboard.
     *
     * @return The title of the column for the given number.
     */
    String getHeaderForColumn(String columnName);

    /**
     * The names of the Columns of the report, which can be configured.
     *
     * @return A List containing all the names of the columns this report supports.
     */
    List getColumnNames();

    /**
     * Whether the report has meaning/content for the given (sub) project. For instance is the report available for this
     * project.
     *
     * @return Whether the report has meaning/content for the given (sub) project.
     */
    boolean canExecute(MavenProject subProject);

    /** Returns the content for the given (sub) project and column. The result node will be placed in the dashboard. */
    Node getContent(MavenProject subProject, String columnName) throws MojoExecutionException;

    /** @return The location relative to the project directory that the content should be linked to. */
    String getLinkLocation();

}
