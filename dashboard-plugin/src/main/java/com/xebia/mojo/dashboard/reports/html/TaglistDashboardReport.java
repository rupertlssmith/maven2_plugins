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
import java.util.Iterator;
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
 * {@link DashboardReport} for the Taglist plugin.
 *
 * @author <a href="mailto:jvanerp@xebia.com">Jeroen van Erp</a>
 */
public class TaglistDashboardReport extends HtmlFileXPathReport {

    private static final List COLUMN_NAMES = Arrays.asList(new String[] {"tasks"});

    public TaglistDashboardReport() {
        super(new String[] {"//div[@id='contentBox']/div[@class='section']/table"});
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
        return "taglist.html";
    }

    /**
     * {@inheritDoc}
     */
    public String getHeaderForColumn(int columnNumber) {
        switch (columnNumber) {
        case 0:
            return "Tasks";
        default:
            return XmlUtil.NBSP;
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getLinkLocation() {
        return "taglist.html";
    }

    /**
     * {@inheritDoc}
     */
    public String title() {
        return "Taglist";
    }

    /**
     * {@inheritDoc}
     */
    protected Node postProcess(MavenProject subProject, Node contentNode, int column) throws MojoExecutionException {
        List rows = contentNode.selectNodes("tr");
        int count = 0;
        for (Iterator it = rows.iterator(); it.hasNext();) {
            Element row = (Element) it.next();
            List cells = row.selectNodes("td");
            if (!cells.isEmpty() && cells.size() > 1) {
                String text = ((Element) cells.get(1)).getText();
                count += Integer.parseInt(text);
            }
        }
        return DocumentHelper.createText(String.valueOf(count));
    }
}
