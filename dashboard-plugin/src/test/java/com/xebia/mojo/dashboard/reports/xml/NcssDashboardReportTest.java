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
package com.xebia.mojo.dashboard.reports.xml;

import java.io.File;

import com.xebia.mojo.dashboard.mocks.MockMavenProject;
import com.xebia.mojo.dashboard.reports.AbstractDashboardReportTester;

import org.apache.maven.project.MavenProject;

/**
 * Test class for the {@link NcssDashboardReport}.
 *
 * @author Jeroen van Erp
 */
public class NcssDashboardReportTest extends AbstractDashboardReportTester
{
    private MockMavenProject mavenProject;

    private NcssDashboardReport ncssReport;

    /**
     * Test method for
     * {@link com.xebia.mojo.dashboard.reports.xml.PmdDashboardReport#canExecute(org.apache.maven.project.MavenProject)}.
     */
    public void testCanExecute()
    {
        assertTrue(ncssReport.canExecute(mavenProject));
    }

    /**
     * Test method for
     * {@link com.xebia.mojo.dashboard.reports.xml.PmdDashboardReport#getContent(org.apache.maven.project.MavenProject, int)}.
     */
    public void testGetContent() throws Exception
    {
        assertEquals("3", ncssReport.getContent(mavenProject, "packages").getText());
        assertEquals("5", ncssReport.getContent(mavenProject, "classes").getText());
        assertEquals("26", ncssReport.getContent(mavenProject, "functions").getText());
        assertEquals("160", ncssReport.getContent(mavenProject, "ncss").getText());
        assertEquals("12", ncssReport.getContent(mavenProject, "javadoc").getText());
    }

    /** Test method for {@link com.xebia.mojo.dashboard.reports.xml.PmdDashboardReport#getHeaderForColumn(int)}. */
    public void testGetHeaderForColumn()
    {
        assertEquals("Packages", ncssReport.getHeaderForColumn("packages"));
        assertEquals("Classes", ncssReport.getHeaderForColumn("classes"));
        assertEquals("Functions", ncssReport.getHeaderForColumn("functions"));
        assertEquals("NCSS", ncssReport.getHeaderForColumn("ncss"));
        assertEquals("Javadoc", ncssReport.getHeaderForColumn("javadoc"));
    }

    /** Test method for {@link com.xebia.mojo.dashboard.reports.xml.PmdDashboardReport#numberOfColumns()}. */
    public void testNumberOfColumns()
    {
        assertEquals(5, ncssReport.getColumnNames().size());
    }

    public void testShouldLinkToNcssReportHtml()
    {
        assertEquals("javancss.html", ncssReport.getLinkLocation());
    }

    /*
     * (non-Javadoc)
     *
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        ncssReport =
            new NcssDashboardReport()
            {
                protected File getReportFile(MavenProject project)
                {
                    return new File("src/test/resources/javancss-raw-report.xml");
                }
            };
    }

}
