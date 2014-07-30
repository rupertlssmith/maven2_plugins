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
package com.xebia.mojo.dashboard.reports.xml;

import java.util.Arrays;
import java.util.List;

import com.xebia.mojo.dashboard.DashboardReport;
import com.xebia.mojo.dashboard.LoggerAware;
import com.xebia.mojo.dashboard.reports.XmlFileXpathReport;
import com.xebia.mojo.dashboard.util.DashboardUtil;
import com.xebia.mojo.dashboard.util.XmlUtil;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;

/**
 * {@link DashboardReport} for a FindBugs report.
 *
 * @author <a href="mailto:jvanerp@xebia.com">Jeroen van Erp</a>
 */
public class FindBugsDashboardReport extends XmlFileXpathReport implements LoggerAware
{
    private static final List COLUMN_NAMES = Arrays.asList(new String[] { "files", "violations" });

    private Log log;

    /** {@inheritDoc} */
    public List getColumnNames()
    {
        return COLUMN_NAMES;
    }

    /** {@inheritDoc} */
    public boolean canExecute(MavenProject subProject)
    {
        boolean valid = super.canExecute(subProject);

        if (valid)
        {
            try
            {
                xmlUtil.readXmlDocument(getReportFile(subProject));
            }
            catch (MojoExecutionException e)
            {
                log.error("The XML document is probably not well formed, use Findbugs 1.2");
                valid = false;
            }
        }

        return valid;
    }

    /** {@inheritDoc} */
    public Node getContent(MavenProject subProject, int column) throws MojoExecutionException
    {
        String xpath = null;

        switch (column)
        {
        case 0:
            xpath = "count(//file)";
            break;

        case 1:
            xpath = "count(//BugInstance)";
            break;

        default:
            return DocumentHelper.createText(XmlUtil.NBSP);
        }

        Document document = xmlUtil.readXmlDocument(getReportFile(subProject));

        return DocumentHelper.createText(DashboardUtil.executeXpathFunctionOnDocument(document, xpath));
    }

    /** {@inheritDoc} */
    public String getHeaderForColumn(int columnNumber)
    {
        switch (columnNumber)
        {
        case 0:
            return "Files";

        case 1:
            return "Warn";

        default:
            return XmlUtil.NBSP;
        }
    }

    /** {@inheritDoc} */
    public String getLinkLocation()
    {
        return "findbugs.html";
    }

    /** {@inheritDoc} */
    public String title()
    {
        return "FindBugs";
    }

    /** {@inheritDoc} */
    public void setLog(Log log)
    {
        this.log = log;
    }

    /** {@inheritDoc} */
    protected String getReportFileName()
    {
        return "findbugs.xml";
    }
}
