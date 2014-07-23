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
package com.xebia.mojo.dashboard.reports.html;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

import com.xebia.mojo.dashboard.reports.HtmlFileXPathReport;
import com.xebia.mojo.dashboard.util.HtmlUtil;
import com.xebia.mojo.dashboard.util.XmlUtil;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

/**
 * {@link DashboardReport} for the Surefire report.
 */
public class SurefireDashboardReport extends HtmlFileXPathReport
{
    private static final String RESULT_ROW = "//div[@id='contentBox']/div[2]/table[1]/tr[2]/";

    /**
     * Surefire report file.
     */
    private static final String SUREFIRE_REPORT_HTML = "surefire-report.html";

    private static final String XPATH_NUMBER_OF_TESTS = RESULT_ROW + "td[1]/text()";

    private static final String XPATH_NUMBER_OF_ERRORS = RESULT_ROW + "td[2]/text()";

    private static final String XPATH_NUMBER_OF_FAILURES = RESULT_ROW + "td[3]/text()";

    private static final String XPATH_SUCCESSRATE = RESULT_ROW + "td[5]/text()";

    private static final List COLUMN_NAMES =
        Arrays.asList(new String[] { "tests", "errors", "failures", "successrate" });

    public SurefireDashboardReport()
    {
        super(
            new String[]
            {
                XPATH_NUMBER_OF_TESTS, XPATH_NUMBER_OF_ERRORS, XPATH_NUMBER_OF_FAILURES, XPATH_SUCCESSRATE
            });
    }

    /**
     * {@inheritDoc}
     */
    public List getColumnNames()
    {
        return COLUMN_NAMES;
    }

    /**
     * {@inheritDoc}
     */
    public String getHeaderForColumn(int number)
    {
        switch (number)
        {
        case 0:
            return "Test";

        case 1:
            return "Error";

        case 2:
            return "Fail";

        case 3:
            return "Rate";

        default:
            return XmlUtil.NBSP;
        }
    }

    /**
     * {@inheritDoc}
     */
    public String title()
    {
        return "Surefire Test Report";
    }

    /**
     * {@inheritDoc}
     */
    public String getLinkLocation()
    {
        return "surefire-report.html";
    }

    /**
     * Returns the filename where the content used for the dashboard is retrieved from. The file
     * used is denoted by: {@link #SUREFIRE_REPORT_HTML} {@inheritDoc}
     */
    protected String getReportFileName()
    {
        return SUREFIRE_REPORT_HTML;
    }

    protected Node postProcess(MavenProject subProject, Node contentNode, int column) throws MojoExecutionException
    {
        if (column == 3) // Add red/green bar to sucess rate.
        {
            Element bar = JacocoDashboardReport.createPercentageBar(contentNode, 60);
            return bar;
        }

        return super.postProcess(subProject, contentNode, column);
    }
}
