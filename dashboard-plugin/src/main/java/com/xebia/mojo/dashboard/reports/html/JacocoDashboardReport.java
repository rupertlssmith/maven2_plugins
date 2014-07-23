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

import com.xebia.mojo.dashboard.reports.HtmlFileXPathReport;
import com.xebia.mojo.dashboard.util.HtmlUtil;
import com.xebia.mojo.dashboard.util.XmlUtil;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

public class JacocoDashboardReport extends HtmlFileXPathReport
{
    private static final String XPATH_SUCCESSRATE = "//tfoot/tr[1]/td[3]/text()";

    /**
     * Surefire report file.
     */
    private static final String SUREFIRE_REPORT_HTML = "jacoco-ut/index.html";

    private static final List COLUMN_NAMES =
        Arrays.asList(new String[] { "lines" });

    public JacocoDashboardReport()
    {
        super(
            new String[]
            {
                XPATH_SUCCESSRATE
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
            return "Lines";

        default:
            return XmlUtil.NBSP;
        }
    }

    /**
     * {@inheritDoc}
     */
    public String title()
    {
        return "JaCoCo";
    }

    /**
     * {@inheritDoc}
     */
    public String getLinkLocation()
    {
        return "jacoco-ut/index.html";
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
        if (column == 0) // Add red/green bar to line coverage rate.
        {
            Element bar = createPercentageBar(contentNode, 60);
            return bar;
        }

        return super.postProcess(subProject, contentNode, column);
    }

    public static Element createPercentageBar(Node contentNode, int pixelsWide) {
        Element bar=null;

        try
        {
            String percentageString = (contentNode != null) ? contentNode.getText() : "0.0";

            double fraction = NumberFormat.getPercentInstance().parse(percentageString).doubleValue();
            bar = DocumentHelper.createElement("div");
            int pixWide = pixelsWide;
            int greenPixWide = (int) (pixWide * fraction);

            HtmlUtil.addStyles(bar,
                    "border: 1px solid #808080; padding: 0px; background-color: #F02020; width: " + pixWide + "px; border-collapse: collapse;");

            Element greenBar = bar.addElement("div");
            HtmlUtil.addStyles(greenBar,
                "padding: 0px; background-color: #00F000; height: 1.3em; border-collapse: collapse; width: " +
                greenPixWide + "px;");

            Element text = greenBar.addElement("span");
            HtmlUtil.addStyles(text,
                "display:block; position:absolute; text-align:center; width:"+ pixWide +"px; border-collapse:collapse;");
            text.setText(percentageString);


        }
        catch (ParseException e)
        {
        }
        return bar;
    }
}
