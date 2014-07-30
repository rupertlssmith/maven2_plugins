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

import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.AbstractMavenReportRenderer;
import org.apache.maven.reporting.MavenReportException;

/**
 * Report that will generate a page to be used by the DashboardMojo in a later stage.
 *
 * @author <a href="mailto:mwinkels@xebia.com">Maarten Winkels</a>
 * @goal   report
 */
public class MavenDashboardReport extends AbstractMavenReport
{
    /**
     * <i>Maven Internal</i>: The Project descriptor.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * <i>Maven Internal</i>: The Doxia Site Renderer.
     *
     * @component
     */
    private Renderer siteRenderer;

    private ResourceBundle bundle;

    public String getDescription(Locale locale)
    {
        return getBundle(locale).getString("description");
    }

    public String getName(Locale locale)
    {
        return getBundle(locale).getString("name");
    }

    public boolean canGenerateReport()
    {
        return project.isExecutionRoot();
    }

    public String getOutputName()
    {
        return "maven-dashboard-report";
    }

    protected void executeReport(Locale locale) throws MavenReportException
    {
        new MyRenderer(getSink(), locale).render();
    }

    protected String getOutputDirectory()
    {
        return getReportOutputDirectory().getAbsolutePath();
    }

    protected MavenProject getProject()
    {
        return project;
    }

    protected Renderer getSiteRenderer()
    {
        return siteRenderer;
    }

    private ResourceBundle getBundle(Locale locale)
    {
        if (bundle == null)
        {
            bundle = ResourceBundle.getBundle("dashboard", locale);
        }

        return bundle;
    }

    /**
     * Renders the dashboard report page.
     */
    private final class MyRenderer extends AbstractMavenReportRenderer
    {
        private final Locale locale;

        private MyRenderer(Sink sink, Locale locale)
        {
            super(sink);
            this.locale = locale;
        }

        public String getTitle()
        {
            return getBundle(locale).getString("title");
        }

        protected void renderBody()
        {
            startSection("Dashboard");
            startTable();
            tableCell("This should be replaced when dashboard is created.");
            endTable();
            endSection();
        }
    }

}
