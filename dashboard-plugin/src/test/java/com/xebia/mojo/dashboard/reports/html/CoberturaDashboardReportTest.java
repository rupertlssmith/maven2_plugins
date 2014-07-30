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
package com.xebia.mojo.dashboard.reports.html;

import java.io.File;

import com.xebia.mojo.dashboard.mocks.MockMavenProject;
import com.xebia.mojo.dashboard.mocks.MockReporting;
import com.xebia.mojo.dashboard.reports.AbstractDashboardReportTester;
import com.xebia.mojo.dashboard.util.TidyXmlUtil;
import com.xebia.mojo.dashboard.util.XmlUtil;

import org.apache.maven.plugin.logging.Log;
import org.dom4j.Element;
import org.dom4j.Node;
import org.easymock.EasyMock;

/**
 * Test class for {@link CoberturaDashboardReport}.
 */
public class CoberturaDashboardReportTest extends AbstractDashboardReportTester
{
    private MockReporting reporting;

    private MockMavenProject mavenProject;

    private CoberturaDashboardReport coberturaDashboardReport;

    private XmlUtil xmlUtil = new TidyXmlUtil();

    public void testFilenameMustBeCorrect()
    {
        assertEquals("cobertura/frame-summary.html", new CoberturaDashboardReport().getReportFileName());
    }

    public void testShouldHaveCorrectHeader()
    {
        assertEquals("Cobertura", coberturaDashboardReport.title());
    }

    public void testShouldRetrieveLineMetrics() throws Exception
    {
        Node content = coberturaDashboardReport.getContent(mavenProject, 0);
        assertEquals("table", content.getName().toLowerCase());

        Element div = xmlUtil.findElement(content, ".//div");
        assertEquals(
            "background-color:#F02020; border:1px solid #808080; height:1.3em; padding:0px; width:100px; border-collapse:collapse;",
            div.attributeValue("style"));
    }

    public void testShouldParseOldFrameSummary() throws Exception
    {
        coberturaDashboardReport =
            new CoberturaDashboardReport()
            {
                protected String getReportFileName()
                {
                    return "frame-summary-1.7.html";
                }
            };

        Log log = (Log) EasyMock.createMock(Log.class);
        coberturaDashboardReport.setLog(log);
        log.info("Old version Cobertura report plugin used.");
        EasyMock.replay(new Object[] { log });

        Node content = coberturaDashboardReport.getContent(mavenProject, 0);
        assertEquals("table", content.getName().toLowerCase());

        Element td = xmlUtil.findElement(content, ".//td[2]/table//td[2]");
        assertEquals(
            "background-color:#F02020; border:1px solid #808080; height:1.3em; padding:0px; border-collapse:collapse;",
            td.attributeValue("style"));
        EasyMock.verify(new Object[] { log });
    }

    protected void setUp() throws Exception
    {
        File file = new java.io.File(this.getClass().getResource("/pkgs-summary.html").getPath());
        reporting = new MockReporting();
        mavenProject = new MockMavenProject(getBaseDir(file), reporting, null);
        coberturaDashboardReport =
            new CoberturaDashboardReport()
            {
                protected String getReportFileName()
                {
                    return "frame-summary.html";
                }
            };
    }
}
