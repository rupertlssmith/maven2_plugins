/* Copyright Rupert Smith, 2005 to 2008, all rights reserved. */
package org.codehaus.mojo.simian;

import java.io.IOException;

import au.com.redhillconsulting.simian.Options;

import org.apache.maven.plugin.MojoExecutionException;

/**
 * Generates an XML report from Simian.
 *
 * <p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Run the Simian checker and produce an XML report. <td> {@link AuditReport}, {@link SimianMojoUtils}.
 * </table>
 *
 * @author Rupert Smith
 *
 * @goal simian
 * @description Runs the simian tool on the project sources and produces an XML report. Only sources in the main source
 *              directory are scanned, any generated sources are ignored.
 */
public class SimianMojo extends SimianMojoBase
{
    /**
     * Runs Simian over all the Java source files in the project producing an XML report as the output.
     *
     * @throws MojoExecutionException If the some or all of the files to check cannot be loaded.
     */
    public void execute() throws MojoExecutionException
    {
        // Run the duplicates check using the options set up on this mojo.
        Options options = getOptions();

        AuditReport report = null;

        try
        {
            report = SimianMojoUtils.performCheck(options, project);
        }
        catch (IOException e)
        {
            throw new MojoExecutionException("Error reading source files.", e);
        }

        // Output the results of the check as an XML format report.
        try
        {
            SimianMojoUtils.writeXMLReport("target/simian.xml", report);
        }
        catch (IOException e)
        {
            throw new MojoExecutionException("The XML report could not be written out.", e);
        }
    }
}
