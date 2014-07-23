/* Copyright Rupert Smith, 2005 to 2008, all rights reserved. */
package org.codehaus.mojo.simian;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import au.com.redhillconsulting.simian.Checker;
import au.com.redhillconsulting.simian.FileLoader;
import au.com.redhillconsulting.simian.Options;

import org.apache.maven.project.MavenProject;
import org.codehaus.doxia.sink.Sink;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;
import org.znerd.xmlenc.LineBreak;
import org.znerd.xmlenc.XMLOutputter;

/**
 * SimianMojoUtils provides some helper methods for working with Simian.
 *
 * <table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Run Simian on all sources in a project and produce a report.
 *     <td> {@link SimianAuditListener}, {@link FileLoader}, {@link Checker}
 * <tr><td> Transform a report into an XML file. <td> {@link AuditReport}, {@link XMLOutputter}
 * </table>
 *
 * @author Miguel Griffa
 * @author Rupert Smith
 */
public class SimianMojoUtils
{
    /**
     * Runs Simian against the source files in a maven projects source directory.
     *
     * @param options The options to run Simian with.
     * @param project A reference to the maven project.
     *
     * @return An audit report containing details of all repeated blocks found.
     *
     * @throws IOException If the check fails with an IO error on any of the source files.
     */
    public static AuditReport performCheck(Options options, MavenProject project) throws IOException
    {
        // Holds a listener to listen to the duplication errors produced by Simian.
        final SimianAuditListener listener = new SimianAuditListener();

        // Holds the Simian duplication checker to perform the checking with.
        final Checker checker = new Checker(listener, options);

        // Holds the Simian file loader to pass files to process to Simian.
        final FileLoader loader = new FileLoader(checker);

        // Create a list of all the source files to process, using an Ant style pattern.
        List<File> files = getFilesToProcess("**/*.java", null, project);

        for (File file : files)
        {
            loader.load(file);
        }

        // Perform the duplicates analysis on the source files.
        checker.check();

        return listener;
    }

    /**
     * Transforms an audit report into XML and writes it out to the specified file.
     *
     * @param fileName The name of the file to write the XML report to.
     * @param report   The contents of the simian report.
     *
     * @throws IOException If an IO error prevents the report being written.
     */
    public static void writeXMLReport(String fileName, AuditReport report) throws IOException
    {
        File outputFile = new File(fileName);

        XMLOutputter outputter = new XMLOutputter(new FileWriter(outputFile), "UTF-8");

        // Set the line break type in an OS dependant manner. Its a shame xlmenc can't take this from system properties
        // itself and makes it difficult by using its own enumeration.
        String lineBreak = System.getProperty("line.separator");

        if ("\r\n".equals(lineBreak))
        {
            outputter.setLineBreak(LineBreak.DOS);
        }
        else if ("\n".equals(lineBreak))
        {
            outputter.setLineBreak(LineBreak.UNIX);
        }
        else if ("\r".equals(lineBreak))
        {
            outputter.setLineBreak(LineBreak.MACOS);
        }
        else
        {
            outputter.setLineBreak(LineBreak.NONE);
        }

        outputter.setIndentation("  ");

        outputter.startTag("simian");
        outputter.attribute("version", "2.2.4");

        // Write out the entries for each repeated block.
        for (Map.Entry<BlockSummary, List<BlockInstance>> entry : report.getRecordsByBlock().entrySet())
        {
            BlockSummary summary = entry.getKey();
            List<BlockInstance> repeats = entry.getValue();

            outputter.startTag("set");
            outputter.attribute("lineCount", Integer.toString(summary.getSize()));

            for (BlockInstance repeat : repeats)
            {
                outputter.startTag("block");
                outputter.attribute("sourceFile", repeat.getSourcefile().getFilename());
                outputter.attribute("startLineNumber", Integer.toString(repeat.getStartLine()));
                outputter.attribute("endLineNumber", Integer.toString(repeat.getEndLine()));
                outputter.endTag();
            }

            outputter.endTag();
        }

        // Write out summary statistics for the whole scan.
        outputter.startTag("summary");
        outputter.attribute("duplicateFileCount", Integer.toString(report.getDuplicateFileCount()));
        outputter.attribute("duplicateLineCount", Integer.toString(report.getDuplicateLineCount()));
        outputter.attribute("duplicateBlockCount", Integer.toString(report.getDuplicateBlockCount()));
        outputter.attribute("totalFileCount", Integer.toString(report.getTotalFileCount()));
        outputter.attribute("totalSignificantLineCount", Integer.toString(report.getSignificantLineCount()));
        outputter.attribute("processingTime", Long.toString(report.getProcessingTime()));
        outputter.endTag();

        outputter.endTag();
        outputter.endDocument();
    }

    /**
     * Converts a file name into a link to its Maven source x-ref report.
     *
     * <p/>The following rules are used to convert a source file name into a link to its x-ref report:
     *
     * <ul>
     * <li>Any leading slash '/' is removed from the file name.</li>
     * <li>If the file is in the project source directory, the source directory path is stripped from its begining and
     * replaced with 'ref'.</li>
     * <li>If the file is in the project test source directory, the test source directory path is stripped from its
     * begining and replaced with 'xref-test'.</li>
     * <li>If the file ends in '.java' its ending is replaced with '.html'.</li>
     * </ul>
     *
     * @param sourceFile The name of the file to convert to a link.
     *
     * @return The path to link to the files x-ref report.
     */
    public static String getLink(String sourceFile, MavenProject project)
    {
        String sourceDirectory = project.getBuild().getSourceDirectory();
        String testDirectory = project.getBuild().getTestSourceDirectory();

        String ret = "#";

        // check if source is 'core' or test link to jxr is different

        if (ret.startsWith("/"))
        {
            ret = ret.substring(1);
        }

        if (sourceFile.startsWith(sourceDirectory))
        {
            ret = "xref" + sourceFile.substring(sourceDirectory.length());
        }

        if (sourceFile.startsWith(testDirectory))
        {
            ret = "xref-test" + sourceFile.substring(testDirectory.length());
        }

        if (ret.endsWith(".java"))
        {
            ret = ret.substring(0, ret.length() - 5).concat(".html");
        }

        return ret;
    }

    /**
     * Outputs a table row containing two cells, one with a property name, and one with a property value, to the
     * specified HTML sink.
     *
     * @param sink  The HTML sink to write to.
     * @param name  The name to place in the first cell.
     * @param value The value to place in the second cell.
     */
    public static void sinkTableRow(Sink sink, String name, String value)
    {
        sink.tableRow();
        sink.tableCell();
        sink.text(name);
        sink.tableCell_();
        sink.tableCell();
        sink.text(value);
        sink.tableCell_();
        sink.tableRow_();
    }

    /**
     * Provides a path to a source file within a project, relative to the project base directory.
     *
     * @param fileName The full path to the source file including the base directory path.
     * @param project  The maven project.
     *
     * @return The path to the source file with the base directory path removed.
     */
    protected static String getSourceFilenameWithoutBasedir(String fileName, MavenProject project)
    {
        StringBuffer sb = new StringBuffer(fileName);
        String path = project.getBasedir().getAbsolutePath();

        if (sb.subSequence(0, path.length()).equals(path))
        {
            sb = sb.delete(0, path.length());
        }

        return sb.toString();
    }

    /**
     * Creates a list of files from the project source directory with ant-style include and exclude definitions
     * applied. The projects default exlcusions are automaitcally added to the exclude definitions. If the source
     * directory does not exist, then an empty list is provided rather than a failure.
     *
     * @param includes The files to include.
     * @param excludes The files to exclude.
     * @param project  The maven project.
     *
     * @return A list of matching source files.
     *
     * @throws IOException If an IO error prevents the source files being read.
     */
    protected static List<File> getFilesToProcess(final String includes, final String excludes, MavenProject project)
        throws IOException
    {
        final File dir = new File(project.getBuild().getSourceDirectory());

        if (!dir.exists())
        {
            return new LinkedList<File>();
        }

        final StringBuffer excludesStr = new StringBuffer();

        if (StringUtils.isNotEmpty(excludes))
        {
            excludesStr.append(excludes);
        }

        final String[] defaultExcludes = FileUtils.getDefaultExcludes();

        for (String defaultExclude : defaultExcludes)
        {
            if (excludesStr.length() > 0)
            {
                excludesStr.append(",");
            }

            excludesStr.append(defaultExclude);
        }

        return FileUtils.getFiles(dir, includes, excludesStr.toString());
    }
}
