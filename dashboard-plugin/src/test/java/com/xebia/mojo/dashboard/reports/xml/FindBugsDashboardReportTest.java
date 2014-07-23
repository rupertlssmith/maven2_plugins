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
package com.xebia.mojo.dashboard.reports.xml;

import java.io.File;

import junit.framework.TestCase;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.easymock.EasyMock;


public class FindBugsDashboardReportTest extends TestCase {
    private FindBugsDashboardReport findBugsDashboardReport;

    protected void setUp() throws Exception {
        findBugsDashboardReport = new FindBugsDashboardReport() {
            protected File getReportFile(MavenProject project) {
                return new File("src/test/resources/findbugs.xml");
            }
        };
    }

    public void testShouldReturnLocationOfFindBugsXml() {
        assertEquals("findbugs.xml", findBugsDashboardReport.getReportFileName());
    }

    public void testShouldHaveTwoColumns() {
        assertEquals(2, findBugsDashboardReport.getColumnNames().size());
    }

    public void testShouldHaveTitleFilesForFirstColumn() {
        assertEquals("Files", findBugsDashboardReport.getHeaderForColumn("files"));
    }

    public void testShouldHaveTitleViolationsForSecondColumn() {
        assertEquals("Violations", findBugsDashboardReport.getHeaderForColumn("violations"));
    }

    public void testShouldHaveFindBugsAsTitle() {
        assertEquals("FindBugs", findBugsDashboardReport.title());
    }

    public void testShouldLinkToFindBugsIndexPage() {
        assertEquals("findbugs.html", findBugsDashboardReport.getLinkLocation());
    }

    public void testShouldHave4filesInExampleReport() throws Exception {
        assertEquals("4", findBugsDashboardReport.getContent(null, "files").getText());
    }

    public void testShouldHave14BugInstancesInExampleReport() throws Exception {
        assertEquals("14", findBugsDashboardReport.getContent(null, "violations").getText());
    }

    public void testShouldExecuteOnCorrectFindBugsXMLFile() {
        assertTrue(findBugsDashboardReport.canExecute(null));
    }

    public void testShouldNotExecuteOnIncorrectFindBugsXmlFile() {
        findBugsDashboardReport = new FindBugsDashboardReport() {
            protected File getReportFile(MavenProject project) {
                return new File("src/test/resources/findbugs-1.1.xml");
            }
        };
        Log log = (Log) EasyMock.createMock(Log.class);
        findBugsDashboardReport.setLog(log);
        log.error("The XML document is probably not well formed, use Findbugs 1.2");
        EasyMock.replay(new Object[] {log});
        assertFalse(findBugsDashboardReport.canExecute(null));
        EasyMock.verify(new Object[] {log});

    }
}
