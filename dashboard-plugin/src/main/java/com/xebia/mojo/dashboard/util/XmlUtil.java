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

import java.io.File;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

/**
 * Utility methods for XML.
 *
 * @author <a href="mailto:mwinkels@xebia.com">Maarten Winkels</a>
 * @author <a href="mailto:jvanerp@xebia.com">Jeroen van Erp</a>\
 */
public interface XmlUtil
{
    public static final String NBSP = "&nbsp;";

    /** Finds a node by XPath. */
    Node findNode(Node node, String xpath) throws MojoExecutionException;

    /** Finds an element by XPath. */
    Element findElement(Node node, String xpath) throws MojoExecutionException;

    /** Finds a number of nodes by xpath. */
    List findNodes(Node node, String xpath) throws MojoExecutionException;

    /** Write a document to a file. */
    void writeDocument(Document document, File file) throws MojoExecutionException;

    /** Reads an Xhtml document from a file. */
    Document readXhtmlDocument(File file) throws MojoExecutionException;

    /** Reads an Xml document from a file. */
    Document readXmlDocument(File file) throws MojoExecutionException;

    /**
     * Gets the first child Node of a Node, if the passed in Node is an Element. Else returns <b>null</b>
     *
     * @param  parent The node to get the first child of.
     *
     * @return
     */
    Node getFirstChild(Node parent);

    /**
     * Gets the <i>index</i>-th child from a parent.
     *
     * @param  parent The parent to get the child from.
     * @param  index  The index of the child to find
     *
     * @return The node found or <code>null</code> if no such node exist.
     */
    Node getChild(Node parent, int index);
}
