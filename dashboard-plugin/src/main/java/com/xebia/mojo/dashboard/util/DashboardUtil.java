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
package com.xebia.mojo.dashboard.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import com.xebia.mojo.dashboard.DashboardReport;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.dom4j.Document;
import org.dom4j.Node;

/**
 * Util class with convienence methods used by the dashboard and reports implementations.
 *
 * @author <a href="mailto:lvonk@xebia.com">Lars Vonk</a>
 * @author <a href="mailto:jvanerp@xebia.com">Jeroen van Erp</a>
 */
public class DashboardUtil
{
    /** Private constructor. */
    private DashboardUtil()
    {
    }

    /**
     * Determines the complete directory path to a module in the build, by traversing up the {@link MavenProject} tree.
     * The recursion stops if there is no parent, or if it has arrived at the execution root
     * ({@link MavenProject#isExecutionRoot()}).
     *
     * @param  mavenProject The {@link MavenProject} to determine the path of.
     *
     * @return A String containing the directory path to the {@link MavenProject}.
     */
    public static String determineCompletePath(MavenProject mavenProject)
    {
        if ((mavenProject.getParent() != null) &&
                mavenProject.getParent().getCollectedProjects().contains(mavenProject))
        {
            return determineCompletePath(mavenProject.getParent()) + mavenProject.getArtifactId() + "/";
        }

        return "";
    }

    /**
     * Execute an XPath function (like count) on a {@link Document}, and return a String containing the result of the
     * function. If the result was a {@link Double}, it returns the integer value.
     *
     * @param  document The Dom4J {@link Document} to parse using the XPath function.
     * @param  xpath    The XPath function to set loose on the document.
     *
     * @return A String containing the result of the function.
     */
    public static String executeXpathFunctionOnDocument(Node document, String xpath)
    {
        String value = null;
        Object o = document.selectObject(xpath);

        if (o instanceof Double)
        {
            value = String.valueOf(((Double) o).intValue());
        }
        else
        {
            value = o.toString();
        }

        return value;
    }

    /**
     * Read an ordered list of {@link DashboardReport}s from the properties file passed in, and store them in the
     * reportNames {@link Map} passed in.
     *
     * @param reportNames A {@link Map} containing a mapping from the report name to the {@link DashboardReport} class
     *                    name.
     * @param string      The name of the properties file which contains the mapping.
     */
    public static void readReports(Map reportNames, String propertyfile) throws MojoExecutionException
    {
        InputStream is = null;
        BufferedReader reader = null;

        try
        {
            is = DashboardUtil.class.getResourceAsStream(propertyfile);
            reader = new BufferedReader(new InputStreamReader(is));

            String line;

            while ((line = reader.readLine()) != null)
            {
                String[] keyval = line.split("=");
                reportNames.put(keyval[0], keyval[1]);
            }
        }
        catch (Exception e)
        {
            throw new MojoExecutionException("Couldn't parse the reportnames properties file.", e);
        }
        finally
        {
            try
            {
                if (is != null)
                {
                    is.close();
                }

                if (reader != null)
                {
                    reader.close();
                }
            }
            catch (IOException e)
            {
                throw new MojoExecutionException("Cannot close the input stream, which reads the properties file.", e);
            }
        }
    }

    /**
     * Creates a new {@link DashboardReport} instance from the classname.
     *
     * @param  className The classname to create a new instance of.
     *
     * @return A new {@link DashboardReport} instance.
     *
     * @throws MojoExecutionException If the {@link DashboardReport} cannot be created.
     */
    public static DashboardReport createDashboardReportFromClassName(String className) throws MojoExecutionException
    {
        try
        {
            DashboardReport report = (DashboardReport) Class.forName(className).newInstance();

            return report;
        }
        catch (InstantiationException e)
        {
            throw new MojoExecutionException("Error instantiating report: " + className, e);
        }
        catch (IllegalAccessException e)
        {
            throw new MojoExecutionException("Cannot access report: " + className, e);
        }
        catch (ClassNotFoundException e)
        {
            throw new MojoExecutionException("Class not found: " + className, e);
        }
    }

}
