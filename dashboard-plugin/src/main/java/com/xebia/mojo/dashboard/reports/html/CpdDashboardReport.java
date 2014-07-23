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
package com.xebia.mojo.dashboard.reports.html;

import java.util.Arrays;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;

import com.xebia.mojo.dashboard.DashboardReport;
import com.xebia.mojo.dashboard.reports.HtmlFileXPathReport;
import com.xebia.mojo.dashboard.util.DashboardUtil;
import com.xebia.mojo.dashboard.util.TidyXmlUtil;
import com.xebia.mojo.dashboard.util.XmlUtil;


/**
 * {@link DashboardReport} for the CPD report.
 *
 * @author <a href="mailto:lvonk@xebia.com">Lars Vonk</a>
 */
public class CpdDashboardReport extends HtmlFileXPathReport {

    private static final List COLUMN_NAMES = Arrays.asList(new String[] {"violations"});

    public CpdDashboardReport() {
        super(new String[] {"count(//th[text()='File'])"});
    }

    /**
     * {@inheritDoc}
     */
    public List getColumnNames() {
        return COLUMN_NAMES;
    }

    /**
     * Method overridden because the super findNode cannot be used, we use a xpath function. The
     * super class uses {@link TidyXmlUtil#findElement(Node, String)} which cannot handle xpath
     * functions.
     */
    protected Node findNode(MavenProject subProject, String xpath) throws MojoExecutionException {
        Document document = xmlUtil.readXhtmlDocument(getReportFile(subProject));
        return DocumentHelper.createText(DashboardUtil.executeXpathFunctionOnDocument(document, xpath));
    }

    /**
     * {@inheritDoc}
     */
    protected String getReportFileName() {
        return "cpd.html";
    }

    /**
     * {@inheritDoc}
     */
    public String getHeaderForColumn(int number) {
        switch (number) {
        case 0:
            return "Violations";
        default:
            return XmlUtil.NBSP;
        }
    }

    /**
     * {@inheritDoc}
     */
    public String title() {
        return "Copy Paste Detector";
    }

    /**
     * {@inheritDoc}
     */
    public String getLinkLocation() {
        return "cpd.html";
    }

}
