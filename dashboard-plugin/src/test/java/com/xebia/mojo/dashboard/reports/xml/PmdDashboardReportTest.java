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

import com.xebia.mojo.dashboard.reports.AbstractDashboardReportTester;

import org.apache.maven.project.MavenProject;

/**
 * Test class for the {@link PmdDashboardReport}.
 *
 * @author Jeroen van Erp
 */
public class PmdDashboardReportTest extends AbstractDashboardReportTester
{
    private PmdDashboardReport pmdReport;

    /**
     * Test method for
     * {@link com.xebia.mojo.dashboard.reports.xml.PmdDashboardReport#canExecute(org.apache.maven.project.MavenProject)}.
     */
    public void testCanExecute()
    {
        assertTrue(pmdReport.canExecute(null));
    }

    /**
     * Test method for
     * {@link com.xebia.mojo.dashboard.reports.xml.PmdDashboardReport#getContent(org.apache.maven.project.MavenProject, int)}.
     */
    public void testGetContent() throws Exception
    {
        assertEquals("1", pmdReport.getContent(null, "files").getText());
        assertEquals("2", pmdReport.getContent(null, "violations").getText());
        assertEquals("&nbsp;", pmdReport.getContent(null, 99).getText());
    }

    /** Test method for {@link com.xebia.mojo.dashboard.reports.xml.PmdDashboardReport#getHeaderForColumn(int)}. */
    public void testGetHeaderForColumn()
    {
        assertEquals("Files", pmdReport.getHeaderForColumn("files"));
        assertEquals("Violations", pmdReport.getHeaderForColumn("violations"));
        assertEquals("&nbsp;", pmdReport.getHeaderForColumn(99));
    }

    /** Test method for {@link com.xebia.mojo.dashboard.reports.xml.PmdDashboardReport#numberOfColumns()}. */
    public void testNumberOfColumns()
    {
        assertEquals(2, pmdReport.getColumnNames().size());
    }

    public void testShouldLinkToPmdHtmlFileForReport()
    {
        assertEquals("pmd.html", pmdReport.getLinkLocation());
    }

    /*
     * (non-Javadoc)
     *
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        pmdReport =
            new PmdDashboardReport()
            {
                protected File getReportFile(MavenProject project)
                {
                    return new File("src/test/resources/pmd.xml");
                }
            };
    }
}
