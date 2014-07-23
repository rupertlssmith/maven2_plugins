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

import org.apache.maven.project.MavenProject;
import org.dom4j.Node;

import com.xebia.mojo.dashboard.util.XmlUtil;


public class JDependDashboardReportTest extends TestCase {
    private JDependDashboardReport report;

    protected void setUp() throws Exception {
        report = new JDependDashboardReport() {
            protected File getReportFile(MavenProject project) {
                return new File("src/test/resources/jdepend-report.xml");
            }
        };

    }

    public void testShouldHaveCorrectTitle() {
        assertEquals("JDepend", report.title());
    }

    public void testShouldHaveOneColumn() {
        assertEquals(6, report.getColumnNames().size());
    }

    public void testShouldLinkToJDependReportHtml() {
        assertEquals("jdepend-report.html", report.getLinkLocation());
    }

    public void testShouldReturnNBPSForNonExistingColumn() {
        assertEquals(XmlUtil.NBSP, report.getHeaderForColumn(99));
    }

    public void testShouldReturnTotalClassesAsFirstColumnName() {
        assertEquals("TC", report.getHeaderForColumn("totalclasses"));
    }

    public void testShouldReturnConcreteClassesAsSecondColumnName() {
        assertEquals("CC", report.getHeaderForColumn("concreteclasses"));
    }

    public void testShouldReturnAbstractClassesAsThirdColumnName() {
        assertEquals("AC", report.getHeaderForColumn("abstractclasses"));
    }

    public void testShouldReturnAfferentCouplingsAsFourthColumnName() {
        assertEquals("Ca", report.getHeaderForColumn("afferent"));
    }

    public void testShouldReturnEfferentCouplingsAsFifthColumnName() {
        assertEquals("Ce", report.getHeaderForColumn("efferent"));
    }

    public void testShouldReturnCyclesAsSixthColumnName() {
        assertEquals("Cycles", report.getHeaderForColumn("cycles"));
    }

    public void testShouldUseJDependReportXmlFileAsReportFile() {
        assertEquals("jdepend-report.xml", report.getReportFileName());
    }

    public void testShouldReturnNodeWithTotalClassesAsValue() throws Exception {
        Node result = report.getContent(null, "totalclasses");
        assertEquals("8", result.getStringValue());
    }

    public void testShouldReturnNodeWithConcreteClassesAsValue() throws Exception {
        Node result = report.getContent(null, "concreteclasses");
        assertEquals("6", result.getStringValue());
    }

    public void testShouldReturnNodeWithAbstractClassesAsValue() throws Exception {
        Node result = report.getContent(null, "abstractclasses");
        assertEquals("2", result.getStringValue());
    }

    public void testShouldReturnNodeWithAfferentCouplingsAsValue() throws Exception {
        Node result = report.getContent(null, "afferent");
        assertEquals("3", result.getStringValue());
    }

    public void testShouldReturnNodeWithEfferentCouplingsAsValue() throws Exception {
        Node result = report.getContent(null, "efferent");
        assertEquals("8", result.getStringValue());
    }

    public void testShouldReturnNodeWithCyclesAsValue() throws Exception {
        Node result = report.getContent(null, "cycles");
        assertEquals("20", result.getStringValue());
    }
}
