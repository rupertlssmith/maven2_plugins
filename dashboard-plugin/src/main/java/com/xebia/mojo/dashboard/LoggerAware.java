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

import org.apache.maven.plugin.logging.Log;


/**
 * If a {@link DashboardReport} implementation implements this interface, the {@link DashboardMojo}
 * will set a logger onto it.
 *
 * @author <a href="mailto:jvanerp@xebia.com">Jeroen van Erp</a>
 */
public interface LoggerAware {

    /**
     * Sets the {@link Log} on the {@link DashboardReport}.
     *
     * @param log The {@link Log} to set.
     */
    void setLog(Log log);
}
