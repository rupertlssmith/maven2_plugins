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
import org.dom4j.Element;
import org.dom4j.Node;

import com.xebia.mojo.dashboard.DashboardReport;
import com.xebia.mojo.dashboard.reports.HtmlFileXPathReport;
import com.xebia.mojo.dashboard.util.DashboardUtil;
import com.xebia.mojo.dashboard.util.HtmlUtil;
import com.xebia.mojo.dashboard.util.XmlUtil;


/**
 * {@link DashboardReport} for Clover.
 * 
 * @author <a href="mailto:mwinkels@xebia.com">Maarten Winkels</a>
 */
public class CloverDashboardReport extends HtmlFileXPathReport {

    private static final List COLUMN_NAMES = Arrays.asList(new String[] {"percentage", "bar"});

    public CloverDashboardReport() {
        super(new String[] {".//td[@class='graphBarLeft']", ".//table[@class='barGraph']"});
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
        return XmlUtil.NBSP;
    }

    /**
     * {@inheritDoc}
     */
    protected String getReportFileName() {
        return "clover/pkgs-summary.html";
    }

    /**
     * {@inheritDoc}
     */
    protected Node postProcess(MavenProject subProject, Node node, int column) throws MojoExecutionException {
        return createLinkableContent(subProject, node, column);
    }

    private Node createLinkableContent(MavenProject subProject, Node node, int column) throws MojoExecutionException {
        switch (column) {
        case 0:
            return xmlUtil.getFirstChild(node);
        case 1:
            changeImageSrc(subProject, node);
            applyColours(node);
            HtmlUtil.removeClassAttributes(node);
            return node;
        default:
            throw new MojoExecutionException("Incorrect column: " + column);
        }
    }

    private void applyColours(Node node) throws MojoExecutionException {
        HtmlUtil.addStyles(xmlUtil.findElement(node, ".//td[@class='covered'] | .//td[@class='fullcover']"),
                "BACKGROUND: #00df00; BORDER:#9c9c9c 1px solid;");
        HtmlUtil.addStyles(xmlUtil.findElement(node, ".//td[@class='uncovered']"), "BACKGROUND: #df0000; BORDER:#9c9c9c 1px solid;");
    }

    private void changeImageSrc(MavenProject subProject, Node node) throws MojoExecutionException {
        List list = xmlUtil.findNodes(node, ".//img");
        for (int i = 0; i < list.size(); i++) {
            Node aNode = (Node) list.get(i);
            if (aNode instanceof Element) {
                Element anElement = (Element) aNode;
                Node srcAtt = anElement.attribute("src");
                srcAtt.setText(DashboardUtil.determineCompletePath(subProject) + "clover/" + srcAtt.getText());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public String title() {
        return "Clover";
    }

    /**
     * {@inheritDoc}
     */
    public String getLinkLocation() {
        return "clover/index.html";
    }

}
