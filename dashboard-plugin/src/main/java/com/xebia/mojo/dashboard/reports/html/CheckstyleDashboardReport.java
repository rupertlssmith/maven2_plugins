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
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import com.xebia.mojo.dashboard.DashboardReport;
import com.xebia.mojo.dashboard.reports.HtmlFileXPathReport;
import com.xebia.mojo.dashboard.util.XmlUtil;


/**
 * {@link DashboardReport} for checkstyle.
 * 
 * @author <a href="mailto:mwinkels@xebia.com">Maarten Winkels</a>
 */
public class CheckstyleDashboardReport extends HtmlFileXPathReport {

    private static final String XPATH = ".//table[@class='bodyTable']//tr[@class='b']";

    private static final List COLUMN_NAMES = Arrays.asList(new String[] {"files", "infos", "warnings", "errors"});

    public CheckstyleDashboardReport() {
        // Use the same string 4 times.
        super(new String[] {XPATH, XPATH, XPATH, XPATH});
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
    public String getHeaderForColumn(int number) {
        switch (number) {
        case 0:
            return "Files";
        case 1:
            return "Info";
        case 2:
            return "Warn";
        case 3:
            return "Error";
        default:
            return XmlUtil.NBSP;
        }
    }

    /**
     * {@inheritDoc}
     */
    protected String getReportFileName() {
        return "checkstyle.html";
    }

    /**
     * {@inheritDoc}
     */
    protected Node postProcess(MavenProject subProject, Node node, int column) throws MojoExecutionException {
        Node n = xmlUtil.getChild(node, column);
        if (n instanceof Element) {
            n = DocumentHelper.createText(((Element) n).getText());
        }

        return n;
    }

    /**
     * {@inheritDoc}
     */
    public String title() {
        return "CheckStyle";
    }

    /**
     * {@inheritDoc}
     */
    public String getLinkLocation() {
        return "checkstyle.html";
    }

}
