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

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.apache.maven.project.MavenProject;

import com.xebia.mojo.dashboard.util.DashboardUtil;


public class DashboardUtilTest extends TestCase {

    private MavenProject parentProject;

    private MavenProject subProject;

    private MavenProject subSubProject;

    protected void setUp() throws Exception {
        MavenProject baseProject = new MavenProject();
        baseProject.setArtifactId("base");

        parentProject = newMavenProject(baseProject);
        parentProject.setArtifactId("parent");

        subProject = newMavenProject(parentProject);
        subProject.setArtifactId("subProject");

        subSubProject = newMavenProject(subProject);
        subSubProject.setArtifactId("subSubProject");

        subProject.setCollectedProjects(Arrays.asList(new MavenProject[]{subSubProject}));
        parentProject.setCollectedProjects(Arrays.asList(new MavenProject[]{subProject, subSubProject}));
    }

    public void testShouldCalculateThePathForSubprojects() {
        assertEquals("subProject/", DashboardUtil.determineCompletePath(subProject));
        assertEquals("subProject/subSubProject/", DashboardUtil.determineCompletePath(subSubProject));
        assertEquals("", DashboardUtil.determineCompletePath(parentProject));
    }

    public void testPrivateConstructor() throws Exception {
        Constructor constructor = DashboardUtil.class.getDeclaredConstructor(null);
        constructor.setAccessible(true);
        Object object = constructor.newInstance(null);
        assertEquals(DashboardUtil.class, object.getClass());
    }

    private MavenProject newMavenProject(MavenProject parent) {
        MavenProject child = new MavenProject();
        child.setParent(parent);
        return child;
    }
}
