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
package com.xebia.mojo.dashboard;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.xebia.mojo.dashboard.mocks.MockBuild;
import com.xebia.mojo.dashboard.mocks.MockMavenProject;
import com.xebia.mojo.dashboard.mocks.MockReporting;
import com.xebia.mojo.dashboard.util.XmlUtil;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.easymock.EasyMock;

/**
 * Unittests for {@link DashboardMojo}.
 */
public class DashboardMojoTest extends AbstractMojoTestCase
{
    // class under test
    private DashboardMojo dashboardMojo;

    private XmlUtil xmlUtil;

    private MockReporting reporting;

    private MockMavenProject project;

    public void testShouldAddTableWithJustProjectNameWhenNoReports() throws Exception
    {
        Element element = setUpMockBehaviourAndReturnDashBoardNode();

        configureMojo(dashboardMojo, "maven-dashboard-plugin",
            getTestFile("src/test/resources/mojo/config/no-reports.xml"));
        dashboardMojo.execute();

        Element div = element.element("div");
        assertEquals("Dashboard", div.element("h2").getText());

        Element table = div.element("table");
        assertNotNull(table);

        List rows = table.elements();
        assertEquals(3, rows.size());

        Element headerRow = (Element) rows.get(0);
        assertEquals(1, headerRow.elements().size());
        assertEquals("SubProject", headerRow.element("th").getText());

        Element subHeaderRow = (Element) rows.get(1);
        assertEquals(1, subHeaderRow.elements().size());
        assertEquals("&nbsp;", subHeaderRow.element("th").getText());

        Element projectRow = (Element) rows.get(2);
        assertEquals(1, projectRow.elements().size());
        assertEquals("test-project", projectRow.element("td").element("a").getText());

        verifyAllMocks();
    }

    public void testShouldAddTableWithAllReportsWhenNotConfigured() throws Exception
    {
        Element element = setUpMockBehaviourAndReturnDashBoardNode();

        configureMojo(dashboardMojo, "maven-dashboard-plugin",
            getTestFile("src/test/resources/mojo/config/all-reports.xml"));
        dashboardMojo.execute();

        Element div = element.element("div");
        assertEquals("Dashboard", div.element("h2").getText());

        Element table = div.element("table");
        assertNotNull(table);

        List rows = table.elements();
        assertEquals(3, rows.size());

        Element headerRow = (Element) rows.get(0);
        assertTrue(1 < headerRow.elements().size());
        assertEquals("SubProject", headerRow.element("th").getText());

        Element subHeaderRow = (Element) rows.get(1);
        assertTrue(1 < subHeaderRow.elements().size());
        assertEquals("&nbsp;", subHeaderRow.element("th").getText());

        Element projectRow = (Element) rows.get(2);
        assertTrue(1 < projectRow.elements().size());

        Iterator cells = projectRow.elements("td").iterator();
        assertEquals("test-project", ((Element) cells.next()).element("a").getText());

        while (cells.hasNext())
        {
            assertEquals("-", ((Element) cells.next()).getText());
        }

        verifyAllMocks();
    }

    public void testShouldAddTableWithOneReportsWhenOneConfigured() throws Exception
    {
        Element element = setUpMockBehaviourAndReturnDashBoardNode();

        configureMojo(dashboardMojo, "maven-dashboard-plugin",
            getTestFile("src/test/resources/mojo/config/one-report.xml"));
        dashboardMojo.execute();

        Element div = element.element("div");
        assertEquals("Dashboard", div.element("h2").getText());

        Element table = div.element("table");
        assertNotNull(table);

        List rows = table.elements();
        assertEquals(3, rows.size());

        Element headerRow = (Element) rows.get(0);
        assertEquals(2, headerRow.elements().size());

        Iterator headers = headerRow.elements("th").iterator();
        assertEquals("SubProject", ((Element) headers.next()).getText());
        assertEquals("PMD", ((Element) headers.next()).getText());

        Element subHeaderRow = (Element) rows.get(1);

        // 3: One column for the project name and two for the report.
        assertEquals(3, subHeaderRow.elements().size());

        Iterator subHeaders = subHeaderRow.elements("th").iterator();
        assertEquals("&nbsp;", ((Element) subHeaders.next()).getText());
        assertEquals("Files", ((Element) subHeaders.next()).getText());
        assertEquals("Violations", ((Element) subHeaders.next()).getText());

        Element projectRow = (Element) rows.get(2);
        assertEquals(3, projectRow.elements().size());

        Iterator contents = projectRow.elements("td").iterator();
        assertEquals("test-project", ((Element) contents.next()).element("a").getText());
        assertEquals("-", ((Element) contents.next()).getText());
        assertEquals("-", ((Element) contents.next()).getText());
        verifyAllMocks();
    }

    public void testShouldAddTableWithColumnsConfigured() throws Exception
    {
        Element element = setUpMockBehaviourAndReturnDashBoardNode();

        configureMojo(dashboardMojo, "maven-dashboard-plugin",
            getTestFile("src/test/resources/mojo/config/columns-config.xml"));
        dashboardMojo.execute();

        Element div = element.element("div");
        assertEquals("Dashboard", div.element("h2").getText());

        Element table = div.element("table");
        assertNotNull(table);

        List rows = table.elements();
        assertEquals(3, rows.size());

        Element headerRow = (Element) rows.get(0);
        assertEquals(3, headerRow.elements().size());

        Iterator headers = headerRow.elements("th").iterator();
        assertEquals("SubProject", ((Element) headers.next()).getText());
        assertEquals("Surefire Test Report", ((Element) headers.next()).getText());
        assertEquals("Test", ((Element) headers.next()).getText());

        Element subHeaderRow = (Element) rows.get(1);

        // 4: One column for the project name and two for the report.
        assertEquals(4, subHeaderRow.elements().size());

        Iterator subHeaders = subHeaderRow.elements("th").iterator();
        assertEquals("&nbsp;", ((Element) subHeaders.next()).getText());
        assertEquals("Errors", ((Element) subHeaders.next()).getText());
        assertEquals("Header 0", ((Element) subHeaders.next()).getText());
        assertEquals("Header 2", ((Element) subHeaders.next()).getText());

        Element projectRow = (Element) rows.get(2);
        assertEquals(4, projectRow.elements().size());

        Iterator contents = projectRow.elements("td").iterator();
        assertEquals("test-project", ((Element) contents.next()).element("a").getText());
        assertEquals("-", ((Element) contents.next()).getText());
        assertEquals("Test 0", ((Element) contents.next()).getText());
        assertEquals("Test 2", ((Element) contents.next()).getText());
        verifyAllMocks();
    }

    public void testOneReportThatCanExecute() throws Exception
    {
        final DashboardReport report = (DashboardReport) EasyMock.createMock(DashboardReport.class);
        final List columnNames = Arrays.asList(new String[] { "col1", "col2" });
        DashboardMojo mojo =
            new DashboardMojo()
            {
                protected DashboardReport getReport(String reportName)
                {
                    assertEquals("maven-test-report", reportName);

                    return report;
                }
            };
        mojo.setProject(project);
        mojo.setXmlUtil(xmlUtil);
        configureMojo(mojo, "maven-dashboard-plugin", getTestFile("src/test/resources/mojo/config/test-report.xml"));

        Element element = setUpMockBehaviourAndReturnDashBoardNode();
        EasyMock.expect(report.title()).andReturn("test-title");
        EasyMock.expect(report.getColumnNames()).andReturn(columnNames).atLeastOnce();
        EasyMock.expect(report.getHeaderForColumn("col1")).andReturn("column1");
        EasyMock.expect(report.getHeaderForColumn("col2")).andReturn("column2");
        EasyMock.expect(new Boolean(report.canExecute(project))).andReturn(Boolean.TRUE).atLeastOnce();
        EasyMock.expect(report.getContent(project, "col1")).andReturn(DocumentHelper.createText("content1"));
        EasyMock.expect(report.getContent(project, "col2")).andReturn(DocumentHelper.createText("content2"));
        EasyMock.expect(report.getLinkLocation()).andReturn("link");

        // Second cell will not be linked.
        EasyMock.expect(report.getLinkLocation()).andReturn(null);

        EasyMock.replay(new Object[] { report });
        mojo.execute();

        Element div = element.element("div");
        assertEquals("Dashboard", div.element("h2").getText());

        Element table = div.element("table");
        assertNotNull(table);

        List rows = table.elements();
        assertEquals(3, rows.size());

        Element headerRow = (Element) rows.get(0);
        assertEquals(2, headerRow.elements().size());

        Iterator headers = headerRow.elements("th").iterator();
        assertEquals("SubProject", ((Element) headers.next()).getText());
        assertEquals("test-title", ((Element) headers.next()).getText());

        Element subHeaderRow = (Element) rows.get(1);

        // 3: One column for the project name and two for the report.
        assertEquals(3, subHeaderRow.elements().size());

        Iterator subHeaders = subHeaderRow.elements("th").iterator();
        assertEquals("&nbsp;", ((Element) subHeaders.next()).getText());
        assertEquals("column1", ((Element) subHeaders.next()).getText());
        assertEquals("column2", ((Element) subHeaders.next()).getText());

        Element projectRow = (Element) rows.get(2);
        assertEquals(3, projectRow.elements().size());

        Iterator contents = projectRow.elements("td").iterator();
        assertEquals("test-project", ((Element) contents.next()).element("a").getText());

        Element link = ((Element) contents.next()).element("a");
        assertEquals("content1", link.getText());
        assertEquals("link", link.attribute("href").getText());
        assertEquals("content2", ((Element) contents.next()).getText());

        verifyAllMocks();
        EasyMock.verify(new Object[] { report });
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        dashboardMojo = new DashboardMojo();
        xmlUtil = (XmlUtil) EasyMock.createMock(XmlUtil.class);
        dashboardMojo.setXmlUtil(xmlUtil);
        reporting = new MockReporting();
        project = new MockMavenProject(new File(getBasedir()), reporting, new MockBuild(getBasedir()));
        project.setName("test-project");
        dashboardMojo.setProject(project);
    }

    private Element setUpMockBehaviourAndReturnDashBoardNode() throws MojoExecutionException
    {
        Document document = DocumentFactory.getInstance().createDocument();
        File file = new File("${project.reporting.outputDirectory}", "index.html");
        EasyMock.expect(xmlUtil.readXhtmlDocument(file)).andReturn(document);

        Element element = document.addElement("div");
        EasyMock.expect(xmlUtil.findElement(document, "//div[@id='contentBox']")).andReturn(element);
        xmlUtil.writeDocument(document, file);

        replayAllMocks();

        return element;
    }

    private void verifyAllMocks()
    {
        EasyMock.verify(allMocks());
    }

    private void replayAllMocks()
    {
        EasyMock.replay(allMocks());
    }

    private Object[] allMocks()
    {
        return new Object[] { xmlUtil };
    }
}
