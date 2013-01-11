/*
 * Copyright (c) 2007-2012 Sonatype, Inc. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */

package org.sonatype.nexus.examples.attributes;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.thoughtworks.xstream.annotations.XStreamAlias;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Attribute collection.  This class is not thread-safe.
 *
 * @since 1.0
 */
@XStreamAlias("attributes")
public class Attributes
{
    public static final String SYSTEM_ATTR_PREFIX = "storageItem-";

    /**
     * The actual list holding the payload.
     */
    private final List<Attribute> attributes = Lists.newArrayList();

    /**
     * Transient attribute lookup map.
     */
    private transient final Map<String, Attribute> attributesMap = Maps.newHashMap();

    public Attribute addAttribute(final Attribute attribute) {
        final Attribute result = attributesMap.put(attribute.getKey(), attribute);
        if (result != null) {
            attributes.remove(result);
        }
        attributes.add(attribute);
        return result;
    }

    public Attribute getAttribute(final String key) {
        return attributesMap.get(key);
    }

    public void clear() {
        attributes.clear();
        attributesMap.clear();
    }

    public void applyTo(org.sonatype.nexus.proxy.attributes.Attributes proxyAttributes)
        throws IllegalArgumentException
    {
        for (Attribute attribute : attributes) {
            String key = attribute.getKey();
            checkArgument(!key.startsWith(SYSTEM_ATTR_PREFIX), "Can not override system attribute: %s", key);
            proxyAttributes.put(key, attribute.getValue());
        }
    }

    public static Attributes buildFrom(final org.sonatype.nexus.proxy.attributes.Attributes proxyAttributes) {
        final Map<String, String> attributesMap = proxyAttributes.asMap();
        final Attributes result = new Attributes();
        for (Map.Entry<String, String> entry : attributesMap.entrySet()) {
            final Attribute attribute = new Attribute(entry.getKey(), entry.getValue());
            result.addAttribute(attribute);
        }
        return result;
    }
}
