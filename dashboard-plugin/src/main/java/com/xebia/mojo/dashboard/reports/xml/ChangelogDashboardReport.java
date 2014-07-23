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
 * {@link DashboardReport} for the Changelog plugin.
 *
 * @author <a href="mailto:jvanerp@xebia.com">Jeroen van Erp</a>
 */
public class ChangelogDashboardReport extends XmlFileXpathReport {

    private static final List COLUMN_NAMES = Arrays.asList(new String[] {"commits", "changes"});

    /**
     * {@inheritDoc}
     */
    protected String getReportFileName() {
        return "changelog.xml";
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
        String xpath = null;
        switch (column) {
        case 0:
            xpath = "count(//changelog-entry)";
            break;
        case 1:
            xpath = "count(//file)";
            break;
        default:
            return DocumentHelper.createText(XmlUtil.NBSP);
        }

        Document document = xmlUtil.readXmlDocument(getReportFile(subProject));
        return DocumentHelper.createText(DashboardUtil.executeXpathFunctionOnDocument(document, xpath));
    }

    /**
     * {@inheritDoc}
     */
    public String getHeaderForColumn(int columnNumber) {
        switch (columnNumber) {
        case 0:
            return "Commits";
        case 1:
            return "Changed files";
        default:
            return XmlUtil.NBSP;
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getLinkLocation() {
        return "changelog.html";
    }

    /**
     * {@inheritDoc}
     */
    public String title() {
        return "Changelog";
    }
}
