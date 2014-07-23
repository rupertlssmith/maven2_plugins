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

import java.io.File;

import javax.xml.transform.TransformerException;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.StringUtils;
import org.dom4j.Document;
import org.dom4j.Node;

import com.xebia.mojo.dashboard.mocks.MockMavenProject;
import com.xebia.mojo.dashboard.mocks.MockReporting;
import com.xebia.mojo.dashboard.reports.AbstractDashboardReportTester;
import com.xebia.mojo.dashboard.util.TidyXmlUtil;
import com.xebia.mojo.dashboard.util.XmlUtil;


/**
 * Test class for the {@link CpdDashboardReport}.
 */
public class CpdDashboardReportTest extends AbstractDashboardReportTester {
    private CpdDashboardReport cpdDashboardReport;

    private MockReporting reporting;

    private MockMavenProject mavenProject;

    protected XmlUtil xmlUtil = new TidyXmlUtil();

    private File testFile;

    protected void setUp() throws Exception {
        testFile = new java.io.File(this.getClass().getResource("/cpd.html").getPath());

        reporting = new MockReporting();
        mavenProject = new MockMavenProject(getBaseDir(testFile), reporting, null);
        cpdDashboardReport = new CpdDashboardReport();
    }

    public void testShouldCountCorrectNumberOfViolations() throws Exception {
        String line = FileUtils.readFileToString(testFile, null);
        int aantal = StringUtils.countMatches(line, "<th>File</th>");
        Node result = cpdDashboardReport.findNode(mavenProject, "count(//th[text()='File'])");
        assertEquals(String.valueOf(aantal), result.getText());
    }

    public void testShouldCountCorrectNumberOfViolationsWithCorrectColumn() throws Exception {
        String line = FileUtils.readFileToString(testFile, null);
        int expected = StringUtils.countMatches(line, "<th>File</th>");
        Node result = cpdDashboardReport.getContent(mavenProject, 0);
        assertEquals(String.valueOf(expected), result.getText());
    }

    public void testToSeeHowToImplement() throws MojoExecutionException, TransformerException {
        Document readDocument = xmlUtil.readXhtmlDocument(testFile);
        assertNotNull(readDocument);
        Object object = readDocument.selectObject("count(//th[text()='File'])");
        assertNotNull(object);
        String y = object.toString();
        assertNotNull(y);
    }

    public void testShouldHaveCorrectHeaderForReport() {
        assertEquals("Copy Paste Detector", cpdDashboardReport.title());
    }

    public void testShouldHaveCorrectHeaderForContent() {
        assertEquals("Violations", cpdDashboardReport.getHeaderForColumn(0));
    }

    public void testShouldHaveCorrectDefaultTitle() {
        assertEquals(XmlUtil.NBSP, cpdDashboardReport.getHeaderForColumn(3));
    }
}
