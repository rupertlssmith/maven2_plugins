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
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import com.xebia.mojo.dashboard.DashboardReport;
import com.xebia.mojo.dashboard.LoggerAware;
import com.xebia.mojo.dashboard.reports.HtmlFileXPathReport;
import com.xebia.mojo.dashboard.util.HtmlUtil;
import com.xebia.mojo.dashboard.util.XmlUtil;


/**
 * {@link DashboardReport} for Cobertura.
 * 
 * @author <a href="mailto:mwinkels@xebia.com">Maarten Winkels</a>
 */
public class CoberturaDashboardReport extends HtmlFileXPathReport implements LoggerAware {

    private static final String REPORT_ROW = "//table[@id='packageResults']/tbody/tr[1]/";

    private static final List COLUMN_NAMES = Arrays.asList(new String[] {"linecoverage", "branchcoverage", "complexity"});

    private Log log;

    public CoberturaDashboardReport() {
        super(new String[] {REPORT_ROW + "td[3]/table", REPORT_ROW + "td[4]/table", REPORT_ROW + "td[5]/text()"});
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
    protected String getReportFileName() {
        return "cobertura/frame-summary.html";
    }

    /**
     * {@inheritDoc}
     */
    public String getHeaderForColumn(int columnNumber) {
        switch (columnNumber) {
        case 0:
            return "Line Coverage";
        case 1:
            return "Branche Coverage";
        case 2:
            return "Complexity";
        default:
            return XmlUtil.NBSP;
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getLinkLocation() {
        return "cobertura/index.html";
    }

    /**
     * {@inheritDoc}
     */
    public String title() {
        return "Cobertura";
    }

    /**
     * {@inheritDoc}
     */
    protected Node postProcess(MavenProject subProject, Node contentNode, int column) throws MojoExecutionException {
        switch (column) {
        case 0:
        case 1:
            Element graph = xmlUtil.findElement(contentNode, ".//div[@class='percentgraph']");
            // Need to distinguish between plugin versions. If no graph found, try to locate
            // alternate location.
            if (graph != null) {
                HtmlUtil.addStyles(graph,
                        "background-color:#F02020; border:1px solid #808080; height:1.3em; padding:0px; width:100px; border-collapse:collapse;");
                Element bar = xmlUtil.findElement(graph, "div[@class='greenbar']");
                // Possibly no green bar, because no coverage.
                Element text = null;
                if (bar != null) {
                    HtmlUtil.addStyles(bar, "background-color:#00F000; height:1.3em; padding:0px; border-collapse:collapse; ");
                    text = xmlUtil.findElement(bar, "span[@class='text']");
                } else {
                    text = xmlUtil.findElement(graph, "span[@class='text']");
                }
                HtmlUtil.addStyles(text, "display:block; position:absolute; text-align:center; width:100px; border-collapse:collapse;");
                HtmlUtil.removeClassAttributes(contentNode);
                break;
            } else {
                log.info("Old version Cobertura report plugin used.");
                graph = xmlUtil.findElement(contentNode, ".//table[@class='percentGraph']");
                if (graph != null) {
                    HtmlUtil.addStyles(graph, "width: 100px;");
                    Element greenBar = xmlUtil.findElement(graph, ".//td[@class='covered']");
                    HtmlUtil.addStyles(greenBar, "background-color:#00F000; height:1.3em; padding:0px; border-collapse:collapse; ");
                    Element redBar = xmlUtil.findElement(graph, ".//td[@class='uncovered']");
                    HtmlUtil.addStyles(redBar,
                            "background-color:#F02020; border:1px solid #808080; height:1.3em; padding:0px; border-collapse:collapse;");
                    HtmlUtil.removeClassAttributes(contentNode);
                    break;
                } else {
                    log.error("Cobertura report found, but cannot extract data from it.");
                }
            }
        default:
            return DocumentHelper.createText(XmlUtil.NBSP);
        }
        return contentNode;
    }

    /**
     * {@inheritDoc}
     */
    public void setLog(Log log) {
        this.log = log;
    }
}
