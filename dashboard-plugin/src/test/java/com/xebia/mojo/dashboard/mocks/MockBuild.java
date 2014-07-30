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
package com.xebia.mojo.dashboard.mocks;

import org.apache.maven.model.Build;

/**
 * Mock implementation of the Maven Build object.
 */
public class MockBuild extends Build
{
    /**  */
    private static final long serialVersionUID = 1L;

    private String directory;

    public MockBuild(String dir)
    {
        super();
        this.directory = dir;
    }

    public String getDirectory()
    {
        return directory;
    }
}
