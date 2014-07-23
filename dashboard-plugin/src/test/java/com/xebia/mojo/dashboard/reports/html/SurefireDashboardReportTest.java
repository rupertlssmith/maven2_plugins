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

import org.apache.maven.plugin.MojoExecutionException;
import org.dom4j.Element;
import org.dom4j.Node;

import com.xebia.mojo.dashboard.mocks.MockMavenProject;
import com.xebia.mojo.dashboard.mocks.MockReporting;
import com.xebia.mojo.dashboard.reports.AbstractDashboardReportTester;

/**
 * Test class for the {@link SurefireDashboardReport}.
 */
public class SurefireDashboardReportTest extends AbstractDashboardReportTester {

    SurefireDashboardReport surefireDashboardReport;
    MockMavenProject mavenProject;
    File testDir;

    protected void setUp() throws Exception {
        File testFile = new java.io.File(this.getClass().getResource("/cpd.html").getPath());

        surefireDashboardReport = new SurefireDashboardReport();
        MockReporting reporting = new MockReporting();
        mavenProject = new MockMavenProject(getBaseDir(testFile), reporting, null);
    }
    public void testTitleShouldBeTestReport() {
        String actual = surefireDashboardReport.title();

        assertEquals("Surefire Test Report", actual);
    }

    public void testRelativeFilenameShouldBe_Surefire_report_html() {
        String actual = surefireDashboardReport.getReportFileName();

        assertEquals("surefire-report.html", actual);
    }

    public void testShouldHaveFourColumns() {
        int actual = surefireDashboardReport.getColumnNames().size();

        assertEquals(4, actual);
    }
    public void testFirstColumnIsCalled_Tests() {
        String actual = surefireDashboardReport.getHeaderForColumn(0);

        assertEquals("Tests", actual);
    }
    public void testSecondColumnIsCalled_Errors() {
        String actual = surefireDashboardReport.getHeaderForColumn(1);

        assertEquals("Errors", actual);
    }
    public void testThirdColumnIsCalled_Failures() {
        String actual = surefireDashboardReport.getHeaderForColumn(2);

        assertEquals("Failures", actual);
    }
    public void testFourthColumnIsCalled_SuccessRate() {
        String actual = surefireDashboardReport.getHeaderForColumn(3);

        assertEquals("Success Rate", actual);
    }

    public void testShouldGetSuccessRatePercentageFromSurefireFile() throws MojoExecutionException {
        Node successRate = surefireDashboardReport.getContent(mavenProject, 3);

        assertEquals("div", successRate.getName());
        Element child = ((Element)successRate).element("div");
        assertNotNull(child);
        Element child2 = child.element("span");
        assertNotNull(child2);
        assertEquals("100%", child2.getText());
    }

    public void testShouldGetNumberOfFailuresFromSurefireFile() throws MojoExecutionException {
        Node successRate = surefireDashboardReport.getContent(mavenProject, 2);

        assertEquals("0", successRate.getText());

    }
    public void testShouldGetNumberOfErrorsFromSurefireFile() throws MojoExecutionException {
        Node successRate = surefireDashboardReport.getContent(mavenProject, 1);

        assertEquals("0", successRate.getText());

    }
    public void testShouldGetNumberOfTestsFromSurefireFile() throws MojoExecutionException {
        Node successRate = surefireDashboardReport.getContent(mavenProject, 0);

        assertEquals("638", successRate.getText());

    }
}
