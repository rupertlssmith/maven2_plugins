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

import org.dom4j.Element;
import org.dom4j.Node;

/**
 * Test class for clover.
 */
public class CloverDashboardReportTest extends AbstractDashboardReportTester
{
    private MockReporting reporting;

    private MockMavenProject mavenProject;

    private CloverDashboardReport cloverDashboardReport;

    private XmlUtil xmlUtil = new TidyXmlUtil();

    public void testFilenameMustBeCorrect()
    {
        assertEquals("clover/pkgs-summary.html", cloverDashboardReport.getReportFileName());
    }

    public void testShouldHaveCorrectHeader()
    {
        assertEquals("Clover", cloverDashboardReport.title());
    }

    public void testShouldRetrieveCoverageInBoldTags() throws Exception
    {
        // we need to override the getRelativeFileName because for the
        // test it doesnot reside in the clover subfolder.
        cloverDashboardReport =
            new CloverDashboardReport()
            {
                protected String getReportFileName()
                {
                    return "pkgs-summary.html";
                }
            };

        Node content = cloverDashboardReport.getContent(mavenProject, 0);
        assertEquals("b", content.getName().toLowerCase());

        String coverage = xmlUtil.getFirstChild(content).getText();
        assertNotNull("60.6%", coverage);
    }

    public void testCoverageBarShouldHaveNoClassAttributes() throws Exception
    {
        // we need to override the getRelativeFileName because for the
        // test it doesnot reside in the clover subfolder.
        cloverDashboardReport =
            new CloverDashboardReport()
            {
                protected String getReportFileName()
                {
                    return "pkgs-summary.html";
                }
            };

        Node coverageTable = cloverDashboardReport.getContent(mavenProject, 1);
        assertEquals("table", coverageTable.getName());

        Element coverageTableElement = (Element) coverageTable;
// TODO
//        assertNull(coverageTableElement.attributes()getAttributes().getNamedItem("class"));
//        Node coverageTableRow = xmlUtil.getFirstChild(xmlUtil.getFirstChild(coverageTable));
//        assertNull(coverageTableRow.getFirstChild().getAttributes().getNamedItem("class"));
//        assertNull(coverageTableRow.getFirstChild().getNextSibling().getAttributes().getNamedItem("class"));
    }

    protected void setUp() throws Exception
    {
        File file = new java.io.File(this.getClass().getResource("/pkgs-summary.html").getPath());
        reporting = new MockReporting();
        mavenProject = new MockMavenProject(getBaseDir(file), reporting, null);
        cloverDashboardReport = new CloverDashboardReport();
    }
}
