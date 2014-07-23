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
package com.xebia.mojo.dashboard.reports.xml;

import java.util.Arrays;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;

import com.xebia.mojo.dashboard.DashboardReport;
import com.xebia.mojo.dashboard.reports.XmlFileXpathReport;
import com.xebia.mojo.dashboard.util.DashboardUtil;
import com.xebia.mojo.dashboard.util.XmlUtil;


/**
 * {@link DashboardReport} for the JavaNCSS metrics.
 *
 * @author <a href="mailto:jvanerp@xebia.com">Jeroen van Erp</a>
 */
public class NcssDashboardReport extends XmlFileXpathReport {

    private static final List COLUMN_NAMES = Arrays.asList(new String[] {"packages", "classes", "functions", "ncss", "javadoc"});

    /**
     * {@inheritDoc}
     */
    protected String getReportFileName() {
        return "javancss-raw-report.xml";
    }

    /**
     * {@inheritDoc}
     */
    public List getColumnNames() {
        return COLUMN_NAMES;
    }

    /**
     * {@inheritDoc}
     */
    public Node getContent(MavenProject subProject, int column) throws MojoExecutionException {
        Node value = null;
        Document document = xmlUtil.readXmlDocument(getReportFile(subProject));

        switch (column) {
        case 0:
            String xpath = "count(//packages/package)";
            value = DocumentHelper.createText(DashboardUtil.executeXpathFunctionOnDocument(document, xpath));
            break;
        case 1:
            value = document.selectSingleNode("//total/classes/text()");
            break;
        case 2:
            value = document.selectSingleNode("//total/functions/text()");
            break;
        case 3:
            value = document.selectSingleNode("//total/ncss/text()");
            break;
        case 4:
            value = document.selectSingleNode("//total/javadocs/text()");
            break;
        default:
            value = DocumentHelper.createText(XmlUtil.NBSP);
        }

        return value;
    }

    /**
     * {@inheritDoc}
     */
    public String getHeaderForColumn(int columnNumber) {
        switch (columnNumber) {
        case 0:
            return "Packages";
        case 1:
            return "Classes";
        case 2:
            return "Functions";
        case 3:
            return "NCSS";
        case 4:
            return "Javadoc";
        default:
            return XmlUtil.NBSP;
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getLinkLocation() {
        return "javancss.html";
    }

    /**
     * {@inheritDoc}
     */
    public String title() {
        return "JavaNCSS";
    }
}
