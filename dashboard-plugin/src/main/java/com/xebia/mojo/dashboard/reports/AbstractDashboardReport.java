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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.dom4j.Node;

import com.xebia.mojo.dashboard.DashboardReport;
import com.xebia.mojo.dashboard.util.TidyXmlUtil;
import com.xebia.mojo.dashboard.util.XmlUtil;


/**
 * Abstract Base Report providing a number of hooks.
 *
 * @author <a href="mailto:jvanerp@xebia.com">Jeroen van Erp</a>
 */
public abstract class AbstractDashboardReport implements DashboardReport {

    protected XmlUtil xmlUtil = new TidyXmlUtil();

    /**
     * Gets the path where the reports are being written to.
     *
     * @param project The project to get the path of.
     * @return The path where the reports are being written to
     */
    protected abstract File getReportingPath(MavenProject project);

    /**
     * Gets a {@link File} object containing the report that is required to generate this
     * {@link DashboardReport}.
     *
     * @param project The project that contains the report file.
     * @return a new File object pointing to the report.
     */
    protected File getReportFile(MavenProject project) {
        return new File(getReportingPath(project), getReportFileName());
    }

    /**
     * Must be implemented and return the filename that is used to retrieve the information from
     * used in the dashboard. The name should be relative to the reporting output directory of the
     * maven project.
     *
     * @return The filename of the report that is required for this {@link DashboardReport}
     */
    protected abstract String getReportFileName();

    /**
     * {@inheritDoc}
     */
    public boolean canExecute(MavenProject subProject) {
        return getReportFile(subProject).exists();
    }

    /**
     * {@inheritDoc}
     */
    public Node getContent(MavenProject subProject, String columnName) throws MojoExecutionException {
        return getContent(subProject, getColumnNames().indexOf(columnName));
    }

    /**
     * {@inheritDoc}
     */
    public String getHeaderForColumn(String columnName) {
        return getHeaderForColumn(getColumnNames().indexOf(columnName));
    }

    /**
     * As Java 1.4 does not support enums, we make it easier on the backend to do index based
     * switches. The index passed in should match the index of the column in the columnNames list,
     * see {@link DashboardReport#getColumnNames()}
     *
     * @param subProject
     * @param columnIndex The index in the columnNames list.
     * @return The {@link Node} containing the content.
     * @throws MojoExecutionException If the content cannot be retrieved.
     */
    protected abstract Node getContent(MavenProject subProject, int columnIndex) throws MojoExecutionException;

    /**
     * As Java 1.4 does not support enums, we make it easier on the backend to do index based
     * switches. The index passed in should match the index of the column in the columnNames list,
     * see {@link DashboardReport#getColumnNames()}
     *
     * @param subProject
     * @param columnIndex The index in the columnNames list.
     * @return The String containing the header for displaying in the table on the page.
     */
    protected abstract String getHeaderForColumn(int columnIndex);
}
