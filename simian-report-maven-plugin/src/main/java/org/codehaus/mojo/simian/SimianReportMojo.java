/* Copyright Rupert Smith, 2005 to 2008, all rights reserved. */
package org.codehaus.mojo.simian;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import au.com.redhillconsulting.simian.Options;

import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReport;
import org.apache.maven.reporting.MavenReportException;
import org.codehaus.doxia.sink.Sink;
import org.codehaus.doxia.site.renderer.SiteRenderer;

/**
 * Generates an XML and an HTML report from Simian.
 *
 * <p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Run the Simian checker and produce an XML report. <td> {@link AuditReport}, {@link SimianMojoUtils}.
 * <tr><td> Produce an HTML report. <td> {@link AuditReport}, {@link SimianMojoUtils}.
 * </table>
 *
 * @author Miguel Griffa
 * @author Rupert Smith
 *
 * @goal report
 * @description Runs the simian tool on the project sources and produces an XML report and an HTML report. Only sources
 *              in the main source directory are scanned, any generated sources are ignored.
 */
public class SimianReportMojo extends SimianMojoBase implements MavenReport
{
    /**
     * @component
     */
    protected SiteRenderer siteRenderer;

    /**
     * @parameter expression="${project.reporting.outputDirectory}"
     * @required
     */
    protected String outputDirectory;

    /**
     * If this option is set to <code>true</code>, links are added to jxr plugin.
     *
     * @parameter default-value="true"
     * @optional
     */
    protected boolean linkToJxr = true;

    /**
     * @parameter expression="${project.build.sourceDirectory}"
     * @required
     * @readonly
     */
    protected String sourceDirectory;

    /**
     * @parameter expression="${project.build.testSourceDirectory}"
     * @required
     * @readonly
     */
    protected String testDirectory;

    /** Holds the report mojo to delegate all reporting methods to. */
    private final SimianReportDelegate reportDelegate = new SimianReportDelegate();

    /** Used to hold the output of running a Simian check. */
    private AuditReport report;

    /**
     * Runs Simian over all the Java source files in the project producing an XML report and an HTML report as the
     * output.
     *
     * @throws MojoExecutionException If any errors occur that prevent the report being run or written out.
     */
    public void execute() throws MojoExecutionException
    {
        // Use the reporting mojo to write out the report in HTML format.
        reportDelegate.execute();
    }

    /** {@inheritDoc} */
    public void generate(Sink sink, Locale locale) throws MavenReportException
    {
        reportDelegate.generate(sink, locale);
    }

    /** {@inheritDoc} */
    public String getOutputName()
    {
        return reportDelegate.getOutputName();
    }

    /** {@inheritDoc} */
    public String getName(Locale locale)
    {
        return reportDelegate.getName(locale);
    }

    /** {@inheritDoc} */
    public String getCategoryName()
    {
        return reportDelegate.getCategoryName();
    }

    /** {@inheritDoc} */
    public String getDescription(Locale locale)
    {
        return reportDelegate.getDescription(locale);
    }

    /** {@inheritDoc} */
    public void setReportOutputDirectory(File file)
    {
        reportDelegate.setReportOutputDirectory(file);
    }

    /** {@inheritDoc} */
    public File getReportOutputDirectory()
    {
        return reportDelegate.getReportOutputDirectory();
    }

    /** {@inheritDoc} */
    public boolean isExternalReport()
    {
        return reportDelegate.isExternalReport();
    }

    /**
     * Checks if this report can be generated. It can be generated if the project artifact built using the 'java'
     * language.
     *
     * @return <tt>true</tt> if this report can be generated for the project.
     */
    public boolean canGenerateReport()
    {
        final ArtifactHandler artifactHandler = project.getArtifact().getArtifactHandler();

        return ("java".equals(artifactHandler.getLanguage()));
    }

    /**
     * Provides the resource bundle for the 'simian-report' resource for the specified locale.
     *
     * @param locale The locale to get the resource bundle for.
     *
     * @return The resource bundle for the 'simian-report' resource for the specified locale.
     */
    protected static ResourceBundle getBundle(final Locale locale)
    {
        return ResourceBundle.getBundle("simian-report", locale, SimianReportMojo.class.getClassLoader());
    }

    /**
     * SimianReportDelegate builds on the AbstractMavenReport mojo to provide a report in HTML format. It is an
     * inner class delegate of the main reporting mojo, {@link SimianReportMojo}, to allow that class to extend
     * the base {@link SimianMojoBase} class, that is reponsible for gathering options to run Simian with.
     *
     * <p/><table id="crc"><caption>CRC Card</caption>
     * <tr><th> Responsibilities <th> Collaborations
     * <tr><td> Produce an HTML report. <td> {@link AuditReport}, {@link SimianMojoUtils}.
     * </table>
     */
    private class SimianReportDelegate extends AbstractMavenReport
    {
        /**
         * Outputs the Simian report in HTML format for the specified locale. The results of running Simian on the
         * projects source files are supplied by the 'report' field of the surrounding class, and this field
         * is expected to be instantiated to a valid set of results prior to this method being invoked.
         *
         * @param locale The local to generate the HTML report for.
         */
        public void executeReport(final Locale locale) throws MavenReportException
        {
            // Run the duplicates check using the options set up on this mojo.
            Options options = getOptions();

            try
            {
                report = SimianMojoUtils.performCheck(options, project);
            }
            catch (IOException e)
            {
                throw new MavenReportException("Error reading source files.", e);
            }

            // Output the report in XML format.
            try
            {
                SimianMojoUtils.writeXMLReport("target/simian.xml", report);
            }
            catch (IOException e)
            {
                throw new MavenReportException("The XML report could not be written out.", e);
            }

            Sink sink = getSink();

            sink.body();

            // Create a summary of the repeat blocks report at the top of the HTML report.
            sink.section1();
            sink.sectionTitle1();
            sink.text(getBundle(locale).getString("report.simian.summary"));
            sink.sectionTitle1_();

            sink.table();
            SimianMojoUtils.sinkTableRow(sink, getBundle(locale).getString("report.simian.threshold"),
                Integer.toString(threshold));
            SimianMojoUtils.sinkTableRow(sink, getBundle(locale).getString("report.simian.total.duplicate.lines"),
                Integer.toString(report.getDuplicateLineCount()));
            SimianMojoUtils.sinkTableRow(sink, getBundle(locale).getString("report.simian.total.duplicate.blocks"),
                Integer.toString(report.getDuplicateBlockCount()));
            SimianMojoUtils.sinkTableRow(sink, getBundle(locale).getString("report.simian.total.duplicate.files"),
                Integer.toString(report.getDuplicateFileCount()));
            SimianMojoUtils.sinkTableRow(sink, getBundle(locale).getString("report.simian.total.processed.lines"),
                Integer.toString(report.getSignificantLineCount()));
            SimianMojoUtils.sinkTableRow(sink, getBundle(locale).getString("report.simian.total.processed.files"),
                Integer.toString(report.getTotalFileCount()));
            SimianMojoUtils.sinkTableRow(sink, getBundle(locale).getString("report.simian.total.scantime"),
                report.getProcessingTime() + "ms");
            sink.table_();
            sink.section1_();

            // Create a detailed report on the individual repeat blocks.
            sink.section1();
            sink.sectionTitle1();
            sink.text(getBundle(locale).getString("report.simian.duplications"));
            sink.sectionTitle1_();

            for (Map.Entry<BlockSummary, List<BlockInstance>> entry : report.getRecordsByBlock().entrySet())
            {
                BlockSummary summary = entry.getKey();
                List<BlockInstance> repeats = entry.getValue();

                sink.section2();
                sink.sectionTitle2();
                sink.text(MessageFormat.format(getBundle(locale).getString("report.simian.duplications.subsection"),
                        new Integer[] { summary.getSize() }));
                sink.sectionTitle2_();

                sink.list();

                for (BlockInstance repeat : repeats)
                {
                    sink.listItem();

                    if (linkToJxr)
                    {
                        sink.link(SimianMojoUtils.getLink(repeat.getSourcefile().getFilename(), project) + "#" +
                            repeat.getStartLine());
                    }

                    sink.text(SimianMojoUtils.getSourceFilenameWithoutBasedir(repeat.getSourcefile().getFilename(),
                            project));

                    sink.text(" ( " + repeat.getStartLine() + " - " + repeat.getEndLine() + " ) ");

                    sink.listItem_();
                }

                if (linkToJxr)
                {
                    sink.link_();
                }

                sink.list();
                sink.section2_();
            }

            sink.section1_();

            sink.body_();
            sink.close();

            getLog().info("Simian report done in " + report.getProcessingTime() + " ms");
        }

        /**
         * {@inheritDoc}
         */
        public String getOutputName()
        {
            return "simian";
        }

        /**
         * {@inheritDoc}
         */
        public String getName(final Locale locale)
        {
            return getBundle(locale).getString("report.simian.name");
        }

        /**
         * {@inheritDoc}
         */
        public String getDescription(final Locale locale)
        {
            return getBundle(locale).getString("report.simian.description");
        }

        /**
         * {@inheritDoc}
         */
        protected SiteRenderer getSiteRenderer()
        {
            return siteRenderer;
        }

        /**
         * {@inheritDoc}
         */
        protected String getOutputDirectory()
        {
            return outputDirectory;
        }

        /**
         * {@inheritDoc}
         */
        protected MavenProject getProject()
        {
            return project;
        }
    }
}
