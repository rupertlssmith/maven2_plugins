package org.codehaus.mojo.xdoclet;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
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


import java.io.File;
import java.net.URL;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.antrun.AntPropertyHelper;
import org.apache.maven.plugin.antrun.AbstractAntMojo;
import org.apache.maven.project.MavenProject;
import org.apache.tools.ant.PropertyHelper;
import org.apache.tools.ant.RuntimeConfigurable;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.UnknownElement;
import org.apache.tools.ant.taskdefs.Taskdef;
import org.codehaus.classworlds.ClassRealm;

/**
 * XDoclet Maven Plugin
 *
 * @author <a href="mailto:kenney@codehaus.org">Kenney Westerhof</a>
 *
 * @configurator override
 *
 * @goal xdoclet
 * 
 * @phase generate-sources
 *
 * @requiresDependencyResolution compile
 * 
 * @description Runs XDoclet
 *
 */
public class XDocletMojo
    extends AbstractAntMojo
{
    private static final String XDOCLET_CP = "xdoclet.class.path";
    
    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * @parameter expression="${project.build.sourceDirectory}"
     * @required
     * @readonly
     */
    private String sourceDirectory;


    /**
     * @parameter expression="${project.build.directory}/generated-sources/xdoclet"
     * @required
     */
    private String generatedSourcesDirectory;


    /**
     * @parameter expression="${tasks}"
     */
    private Target tasks;

    
    /**
     * @parameter expression="${dummyExpression}"
     * @readonly
     */
    private ClassRealm classRealm;
    

    /**
     */
    public void execute()
        throws MojoExecutionException
    {
        String oldXDocletClasspath = System.getProperty( XDOCLET_CP );
                
        try
        {            
            System.setProperty( XDOCLET_CP, createClasspath() );
            
            initializeDocletMappings();
        
            executeTasks( tasks, project );

            if ( new File( generatedSourcesDirectory ).exists() )
            {
                project.addCompileSourceRoot( generatedSourcesDirectory );
            }
        }
        catch (MojoExecutionException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new MojoExecutionException("Error", e);
        }
        finally
        {
            if ( oldXDocletClasspath == null )
            {
                System.getProperties().remove( XDOCLET_CP );
            }
            else
            {
                System.setProperty( XDOCLET_CP, oldXDocletClasspath );
            }
        }
    }

    
    private String createClasspath()
    {
        URL [] cpe = classRealm.getConstituents();
    
        String classpath = "";
        
        for (int i=0; i<cpe.length; i++)
        {
            classpath += (classpath.length() == 0 ?"" : java.io.File.pathSeparator ) +
                cpe[i].getFile();
        }
        
        return classpath;
    }

    
    private boolean isInitialized = false;

    /**
     * Initializes the DocletTask taskdefs for ant.
     * Since XDoclet only has discovery for subtasks
     * but fails to provide a mechanism to setup the docletTasks
     * by discovery, it is required to do so using TaskDef's.
     */
    private void initializeDocletMappings()
    {
        if ( isInitialized )
        {
            getLog().info( "TaskDefinitions already in place." );
            return;
        }
        else
        {
            getLog().info( "Initializing DocletTasks." );
        }

        final String [] mappings =
        {
            "doclet", "xdoclet.DocletTask",
            "tapestrydoclet", "xdoclet.modules.apache.tapestry.TapestryDocletTask",
            "docdoclet" /* ? */, "xdoclet.modules.doc.DocumentDocletTask",
            "ejbdoclet", "xdoclet.modules.ejb.EjbDocletTask",
            "hibernatedoclet", "xdoclet.modules.hibernate.HibernateDocletTask",
            "jdodoclet", "xdoclet.modules.jdo.JdoDocletTask",
            "jmxdoclet", "xdoclet.modules.jmx.JMXDocletTask",
            "mockdoclet", "xdoclet.modules.mockobjects.MockObjectDocletTask",
            "portletdoclet", "xdoclet.modules.portlet.PortletDocletTask",
            "springdoclet", "xdoclet.modules.spring.SpringDocletTask",
            "webdoclet", "xdoclet.modules.web.WebDocletTask",
            "wseedoclet", "xdoclet.modules.wsee.WseeDocletTask"
        };

        for (int i=0; i<mappings.length; i+=2)
        {
            Taskdef taskDef = new Taskdef();
            taskDef.setProject( tasks.getProject() );
            taskDef.setName( mappings[i] );
            taskDef.setClassname(  mappings[i+1] );
            taskDef.setOwningTarget( tasks );
            
            // execute now, adding to tasks will append
            // which is too late.
            taskDef.perform(); 
        }

        isInitialized = true;
    }
}
