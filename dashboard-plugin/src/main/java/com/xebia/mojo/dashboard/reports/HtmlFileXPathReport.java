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
package com.xebia.mojo.dashboard.reports;

import java.io.File;

import com.xebia.mojo.dashboard.DashboardReport;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.dom4j.Node;

/**
 * Uses XPath expressions to locate {@link Node}s in the HTML file that are needed in the {@link DashboardReport}
 * report.
 */
public abstract class HtmlFileXPathReport extends AbstractDashboardReport
{
    private final String[] xpaths;

    public HtmlFileXPathReport(String[] xpaths)
    {
        this.xpaths = xpaths;
    }

    /** {@inheritDoc} */
    public Node getContent(MavenProject subProject, int column) throws MojoExecutionException
    {
        if (column >= xpaths.length)
        {
            throw new IndexOutOfBoundsException();
        }

        Node contentNode = findNode(subProject, xpaths[column]);

        return postProcess(subProject, contentNode, column);
    }

    /** {@inheritDoc} */
    protected File getReportingPath(MavenProject project)
    {
        //return new File(project.getBasedir(), project.getReporting().getOutputDirectory());
        return new File(project.getReporting().getOutputDirectory());
    }

    protected Node findNode(MavenProject subProject, String xpath) throws MojoExecutionException
    {
        return xmlUtil.findNode(xmlUtil.readXhtmlDocument(getReportFile(subProject)), xpath);
    }

    protected Node postProcess(MavenProject subProject, Node contentNode, int column) throws MojoExecutionException
    {
        return contentNode;
    }

}
