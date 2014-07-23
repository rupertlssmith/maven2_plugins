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
package com.xebia.mojo.dashboard.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.DOMReader;
import org.dom4j.io.HTMLWriter;
import org.dom4j.io.SAXReader;
import org.w3c.tidy.Tidy;


/**
 * Implementation of {@link XmlUtil} based upon {@link Tidy}.
 * 
 * @author <a href="mailto:mwinkels@xebia.com">Maarten Winkels</a>
 * @author <a href="mailto:jvanerp@xebia.com">Jeroen van Erp</a>\
 */
public class TidyXmlUtil implements XmlUtil {

    private Tidy tidy;

    public TidyXmlUtil() {
        tidy = new Tidy();
        tidy.setQuiet(true);
        tidy.setShowWarnings(false);
        tidy.setQuoteAmpersand(false);
        tidy.setQuoteNbsp(false);
    }

    /**
     * {@inheritDoc}
     */
    public Node findNode(Node node, String xpath) {
        return node.selectSingleNode(xpath);
    }

    /**
     * {@inheritDoc}
     */
    public Element findElement(Node node, String xpath) throws MojoExecutionException {
        return (Element) findNode(node, xpath);
    }

    /**
     * {@inheritDoc}
     */
    public List findNodes(Node node, String xpath) throws MojoExecutionException {
        return node.selectNodes(xpath);
    }

    /**
     * {@inheritDoc}
     */
    public void writeDocument(Document document, File file) throws MojoExecutionException {
        try {
            HTMLWriter writer = new HTMLWriter(new OutputStreamWriter(new FileOutputStream(file), "ISO-8859-1"));
            writer.setEscapeText(false);
            writer.write(document);
            writer.close();
        } catch (UnsupportedEncodingException e1) {
        } catch (IOException e) {
        }
    }

    /**
     * {@inheritDoc}
     */
    public Document readXhtmlDocument(File file) throws MojoExecutionException {
        try {
            return new DOMReader().read(tidy.parseDOM(new FileInputStream(file), null));
        } catch (FileNotFoundException e) {
            throw new MojoExecutionException("Cannot open file: " + file, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Document readXmlDocument(File file) throws MojoExecutionException {
        try {
            return new SAXReader().read(file);
        } catch (DocumentException e) {
            throw new MojoExecutionException("Cannot open file: " + file, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Node getFirstChild(Node parent) {
        return getChild(parent, 0);
    }

    /**
     * {@inheritDoc}
     */
    public Node getChild(Node parent, int index) {
        if (parent instanceof Element) {
            Element parentElement = (Element) parent;
            if (parentElement.nodeCount() > index) {
                return parentElement.node(index);
            }
        }

        return null;
    }
}
