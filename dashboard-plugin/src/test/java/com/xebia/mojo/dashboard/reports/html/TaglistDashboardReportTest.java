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

/**
 * Test class for the {@link TaglistDashboardReport}.
 *
 * @author jeroen
 */
public class TaglistDashboardReportTest extends AbstractDashboardReportTester
{
    private TaglistDashboardReport report;

    private MockReporting reporting;

    private MockMavenProject mavenProject;

    protected XmlUtil xmlUtil = new TidyXmlUtil();

    private File testFile;

    public void testShouldHaveCorrectReportFileName()
    {
        assertEquals("taglist.html", report.getReportFileName());
    }

    public void testShouldHaveOneColumn()
    {
        assertEquals(1, report.getColumnNames().size());
    }

    public void testShouldHaveTasksAsTitleForFirstColumn()
    {
        assertEquals("Tasks", report.getHeaderForColumn(0));
    }

    public void testShouldHaveTaglistAsReportTitle()
    {
        assertEquals("Taglist", report.title());
    }

    public void testShouldHaveCorrectLinkLocation()
    {
        assertEquals("taglist.html", report.getLinkLocation());
    }

    public void testShouldHave2Tasks() throws Exception
    {
        assertEquals("2", report.getContent(mavenProject, 0).getText());
    }

    protected void setUp() throws Exception
    {
        testFile = new File(this.getClass().getResource("/taglist.html").getPath());

        reporting = new MockReporting();
        mavenProject = new MockMavenProject(getBaseDir(testFile), reporting, null);

        report = new TaglistDashboardReport();
    }
}
