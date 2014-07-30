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

import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.Node;

/**
 * Utility class that provides some helping methods that deal with HTML files.
 *
 * @author <a href="mailto:mwinkels@xebia.com">Maarten Winkels</a>
 */
public abstract class HtmlUtil
{
    /**
     * Removes all "class" {@link Attribute}s from the {@link Node} passed in and all its children.
     *
     * @param node The {@link Node} to start the search from.
     */
    public static final void removeClassAttributes(Node node)
    {
        Element element = (Element) node;
        removeAttribute(element, "class");

        List list = element.selectNodes(".//*[@class]");

        for (int i = 0; i < list.size(); i++)
        {
            removeAttribute((Element) list.get(i), "class");
        }
    }

    /**
     * Removes an {@link Attribute} specified by the attribute name from an {@link Element}.
     *
     * @param element   The {@link Element} from which to remove the {@link Attribute}.
     * @param attribute The name of the {@link Attribute} that is to be removed.
     */
    public static final void removeAttribute(Element element, String attribute)
    {
        Attribute attr = element.attribute(attribute);

        if (attr != null)
        {
            element.remove(attr);
        }
    }

    /**
     * Adds a "style" {@link Attribute} containing the passed in styles to an {@link Element}.
     *
     * @param element The {@link Element} to stylize with the styles passed in.
     * @param styles  The value of the "style" {@link Attribute} that needs to be applied to the {@link Element}.
     */
    public static final void addStyles(Element element, String styles)
    {
        if (element != null)
        {
            Attribute attribute = element.attribute("style");

            if (attribute != null)
            {
                styles += attribute.getValue();
            }

            element.addAttribute("style", styles);
        }
    }

}
