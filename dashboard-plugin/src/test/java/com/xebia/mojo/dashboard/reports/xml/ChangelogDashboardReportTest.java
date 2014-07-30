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

import junit.framework.TestCase;

import org.apache.maven.project.MavenProject;

public class ChangelogDashboardReportTest extends TestCase
{
    private ChangelogDashboardReport changelogDashboardReport;

    public void testShouldReturnLocationOfFindBugsXml()
    {
        assertEquals("changelog.xml", changelogDashboardReport.getReportFileName());
    }

    public void testShouldHaveTwoColumns()
    {
        assertEquals(2, changelogDashboardReport.getColumnNames().size());
    }

    public void testShouldHaveTitleFilesForFirstColumn()
    {
        assertEquals("Commits", changelogDashboardReport.getHeaderForColumn("commits"));
    }

    public void testShouldHaveTitleViolationsForSecondColumn()
    {
        assertEquals("Changed files", changelogDashboardReport.getHeaderForColumn("changes"));
    }

    public void testShouldHaveFindBugsAsTitle()
    {
        assertEquals("Changelog", changelogDashboardReport.title());
    }

    public void testShouldLinkToFindBugsIndexPage()
    {
        assertEquals("changelog.html", changelogDashboardReport.getLinkLocation());
    }

    public void testShouldHave25CommitsInExampleReport() throws Exception
    {
        assertEquals("25", changelogDashboardReport.getContent(null, "commits").getText());
    }

    public void testShouldHave130ChangedFilesInExampleReport() throws Exception
    {
        assertEquals("130", changelogDashboardReport.getContent(null, "changes").getText());
    }

    public void testShouldExecuteOnCorrectFindBugsXMLFile()
    {
        assertTrue(changelogDashboardReport.canExecute(null));
    }

    protected void setUp() throws Exception
    {
        changelogDashboardReport =
            new ChangelogDashboardReport()
            {
                protected File getReportFile(MavenProject project)
                {
                    return new File("src/test/resources/changelog.xml");
                }
            };
    }
}
