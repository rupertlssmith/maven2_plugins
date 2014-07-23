/* Copyright Rupert Smith, 2005 to 2008, all rights reserved. */
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
package com.xebia.mojo.dashboard;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.xebia.mojo.dashboard.util.DashboardUtil;
import com.xebia.mojo.dashboard.util.TidyXmlUtil;
import com.xebia.mojo.dashboard.util.XmlUtil;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

/**
 * This is the main goal of the dashboard plugin. It reads in an XHTML table and adds the
 * dashboard report table to it.
 *
 * @goal dashboard
 * @aggregator
 *
 * @author <a href="mailto:mwinkels@xebia.com">Maarten Winkels</a>
 * @author <a href="mailto:lvonk@xebia.com">Lars Vonk</a>
 * @author <a href="mailto:jvanerp@xebia.com">Jeroen van Erp</a>
 */
public class DashboardMojo extends AbstractMojo
{
    /**
     * The name of the file where the dashboard is generated into.
     *
     * @parameter expression="${project.reporting.outputDirectory}/index.html"
     * @required
     */
    private File destinationFile;

    /**
     * The xpath expression of node in the destinationFile where the dashboard table will be
     * generated in. A section will be added to this node, containing a table with the statistics.
     *
     * @parameter expression="//div[@id='contentBox']"
     * @required
     */
    private String xpathToDestinationNode;

    /**
     * The reports to generate. This is a list of report aliases or full class names. The aliases
     * are generally the same as the plugin artifactId for the plugin that generates the report, eg:
     * maven-clover-plugin for the Clover report and findbugs-maven-plugin for the FindBugs report.
     * The known aliases are configured in the reports.properties file.
     *
     * @parameter
     */
    private String[] reports;

    /**
     * The columns to generate per report. This is a mapping from report (by report alias or full
     * class name) to comma separated column indexes.
     *
     * @parameter
     */
    private Map columns;

    /**
     * <i>Maven Internal</i>: The Project descriptor.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * Holds the parsed config for this mojo. This is a map from {@link DashboardReport} instance to
     * {@link List}. The list might be null. If it is not, it contains {@link Integer}s that are
     * the numbers of the columns shown.
     */
    private Map config = new LinkedHashMap();

    private XmlUtil xmlUtil = new TidyXmlUtil();

    private Map reportNames;

    /**
     * Default constructor of the {@link DashboardMojo} plugin goal. Upon creation it scans all the names of all
     * of the default reports packaged with the plugin.
     *
     * @throws MojoExecutionException If the reports.properties file cannot be found in the JAR package.
     */
    public DashboardMojo() throws MojoExecutionException
    {
        reportNames = new LinkedHashMap();
        DashboardUtil.readReports(reportNames, "/reports.properties");
    }

    public void execute() throws MojoExecutionException
    {
        parseConfig();

        Document destinationFileAsDom = xmlUtil.readXhtmlDocument(destinationFile);
        Element dashboardTableDestination = xmlUtil.findElement(destinationFileAsDom, xpathToDestinationNode);

        removePlaceHolder(dashboardTableDestination);

        Element containerDiv = dashboardTableDestination.addElement("div");
        containerDiv.addAttribute("class", "section");

        Element heading = containerDiv.addElement("h2");
        heading.addText("Dashboard");

        Element dashboardTable = containerDiv.addElement("table");
        dashboardTable.addAttribute("class", "bodyTable");

        generateHeaders(dashboardTable);
        generateContent(dashboardTable);

        xmlUtil.writeDocument(destinationFileAsDom, destinationFile);
    }

    /** Mainly for testing purposes. */
    protected void setProject(MavenProject project)
    {
        this.project = project;
    }

    /** Mainly for testing purposes. */
    protected void setXmlUtil(XmlUtil xmlUtil)
    {
        this.xmlUtil = xmlUtil;
    }

    /** Protected for testing purposes. */
    protected DashboardReport getReport(String reportName) throws MojoExecutionException
    {
        String className = reportName;

        if (reportNames.containsKey(reportName))
        {
            className = (String) reportNames.get(reportName);
        }

        DashboardReport report = DashboardUtil.createDashboardReportFromClassName(className);

        if (report instanceof LoggerAware)
        {
            ((LoggerAware) report).setLog(getLog());
        }

        return report;
    }

    protected void generateContent(Element dashboardTable) throws MojoExecutionException
    {
        int rowNum = 0;

        if (project.getModules().isEmpty())
        {
            createProjectRow(dashboardTable, project, rowNum);
        }
        else
        {
            for (Iterator i = project.getCollectedProjects().iterator(); i.hasNext();)
            {
                createProjectRow(dashboardTable, (MavenProject) i.next(), rowNum);
                rowNum = (rowNum + 1) % 2;
            }
        }
    }

    private void parseConfig() throws MojoExecutionException
    {
        Iterator i;

        if (reports == null)
        {
            i = reportNames.values().iterator();
        }
        else
        {
            i = Arrays.asList(reports).iterator();
        }

        while (i.hasNext())
        {
            String reportName = (String) i.next();
            DashboardReport report = getReport(reportName);
            List list = null;

            if (columns != null)
            {
                String cols = (String) columns.get(reportName);

                if (cols != null)
                {
                    String[] columns = cols.split(",");
                    list = new ArrayList(columns.length);

                    for (int j = 0; j < columns.length; j++)
                    {
                        list.add(columns[j]);
                    }
                }
            }

            config.put(report, list);
        }
    }

    private void removePlaceHolder(Element dashboardTableDestination)
    {
        Node node = dashboardTableDestination.selectSingleNode("div[@class='section']/h2[text()='Dashboard']");

        if (node != null)
        {
            Element div = node.getParent();
            dashboardTableDestination.remove(div);
        }
    }

    private void doWithReports(ReportCallback callback) throws MojoExecutionException
    {
        for (Iterator i = config.keySet().iterator(); i.hasNext();)
        {
            callback.handleReport((DashboardReport) i.next());
        }
    }

    private void doWithReports(final ReportColumnCallback callback) throws MojoExecutionException
    {
        doWithReports(new ColumnsReportCallback(callback));
    }

    private void generateHeaders(Element dashboardTable) throws MojoExecutionException
    {
        generateReportHeadersRow(dashboardTable);
        generateReportSubHeadersRow(dashboardTable);
    }

    private void generateReportHeadersRow(Element dashboardTable) throws MojoExecutionException
    {
        String headerFirstRow = "SubProject";
        final Element firstRow = newRow(dashboardTable, headerFirstRow);
        doWithReports(new ReportCallback()
            {
                public void handleReport(DashboardReport report)
                {
                    List columns = (List) config.get(report);

                    if (columns != null)
                    {
                        createTableHeaderCell(firstRow, report.title(), columns.size());
                    }
                    else
                    {
                        createTableHeaderCell(firstRow, report.title(), report.getColumnNames().size());
                    }
                }
            });
    }

    private void generateReportSubHeadersRow(Element dashboardTable) throws MojoExecutionException
    {
        String headerSecondRow = XmlUtil.NBSP;
        final Element secondRow = newRow(dashboardTable, headerSecondRow);
        doWithReports(new ReportColumnCallback()
            {
                public void handleReportColumn(DashboardReport report, String col) throws MojoExecutionException
                {
                    createTableHeaderCell(secondRow, report.getHeaderForColumn(col), 1);
                }
            });
    }

    private Element newRow(Element dashboardTable, String header)
    {
        Element firstRow = dashboardTable.addElement("tr");
        createTableHeaderCell(firstRow, header, 1);

        return firstRow;
    }

    private void createProjectRow(Element dashboardTable, final MavenProject subProject, int rowNum)
        throws MojoExecutionException
    {
        final Element tableRow = dashboardTable.addElement("tr");
        String cssClass = (rowNum == 0) ? "a" : "b";
        tableRow.addAttribute("class", cssClass);
        createProjectNameCell(subProject, tableRow);
        doWithReports(new ReportColumnCallback()
            {
                public void handleReportColumn(DashboardReport report, String col) throws MojoExecutionException
                {
                    if (report.canExecute(subProject))
                    {
                        Node node = report.getContent(subProject, col);

                        if (node != null)
                        {
                            Node content = node.detach();
                            Element cell = createTableCell(tableRow, null);
                            String href = report.getLinkLocation();

                            if (href != null)
                            {
                                createLink(cell, DashboardUtil.determineCompletePath(subProject) + href, content);
                            }
                            else
                            {
                                cell.add(content);
                            }
                        }
                        else
                        {
                            createTableCell(tableRow, "-");
                        }
                    }
                    else
                    {
                        createTableCell(tableRow, "-");
                    }
                }
            });
    }

    private void createProjectNameCell(MavenProject subProject, Element tableRow)
    {
        Element projectElement = createTableCell(tableRow, null);
        createLink(projectElement, DashboardUtil.determineCompletePath(subProject) + "index.html",
            subProject.getName());
    }

    private void createTableHeaderCell(Element tableRow, String content, int noColumns)
    {
        Element header = createTablePart(tableRow, "th", content);
        header.addAttribute("colspan", Integer.toString(noColumns));
    }

    private Element createTableCell(Element tableRow, String content)
    {
        return createTablePart(tableRow, "td", content);
    }

    private Element createTablePart(Element tableRow, String type, String content)
    {
        Element element = tableRow.addElement(type);

        if (content != null)
        {
            element.setText(content);
        }

        return element;
    }

    private void createLink(Element parent, String href, Node content)
    {
        Element link = parent.addElement("a");
        link.add(content);
        link.addAttribute("href", href);
    }

    private void createLink(Element parent, String href, String content)
    {
        Element link = parent.addElement("a");
        link.setText(content);
        link.addAttribute("href", href);
    }

    /**
     * Callback interface that handles a report.
     */
    private interface ReportCallback
    {
        void handleReport(DashboardReport report) throws MojoExecutionException;
    }

    /**
     * Callback interface that handles a column in a report.
     */
    private interface ReportColumnCallback
    {
        void handleReportColumn(DashboardReport report, String columnName) throws MojoExecutionException;
    }

    /**
     * Class that iterates over all columns in a report that are enabled, and calls the {@link ReportColumnCallback}
     * for each of them.
     */
    private final class ColumnsReportCallback implements ReportCallback
    {
        private final ReportColumnCallback callback;

        private ColumnsReportCallback(ReportColumnCallback callback)
        {
            this.callback = callback;
        }

        public void handleReport(DashboardReport report) throws MojoExecutionException
        {
            List columns = (List) config.get(report);

            if (columns == null)
            {
                for (Iterator it = report.getColumnNames().iterator(); it.hasNext();)
                {
                    String columnName = (String) it.next();
                    callback.handleReportColumn(report, columnName);
                }
            }
            else
            {
                for (Iterator i = columns.iterator(); i.hasNext();)
                {
                    String columnName = (String) i.next();
                    callback.handleReportColumn(report, columnName);
                }
            }
        }
    }
}
