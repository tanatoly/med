/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.configuration2.beanutils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.configuration2.Configuration;



/**
 * The {@code ConfigurationDynaClass} dynamically determines properties for
 * a {@code ConfigurationDynaBean} from a wrapped configuration-collection
 * {@link org.apache.commons.configuration2.Configuration} instance.
 *
 * @author <a href="mailto:ricardo.gladwell@btinternet.com">Ricardo Gladwell</a>
 * @version $Id: ConfigurationDynaClass.java 1624601 2014-09-12 18:04:36Z oheger $
 * @since 1.0-rc1
 */
public class ConfigurationDynaClass implements DynaClass
{
  
    /** Stores the associated configuration.*/
    private final Configuration configuration;

    /**
     * Construct an instance of a {@code ConfigurationDynaClass}
     * wrapping the specified {@code Configuration} instance.
     * @param configuration {@code Configuration} instance.
     */
    public ConfigurationDynaClass(Configuration configuration)
    {
        super();
        this.configuration = configuration;
    }

    @Override
    public DynaProperty getDynaProperty(String name)
    {
        if (name == null)
        {
            throw new IllegalArgumentException("Property name must not be null!");
        }

        Object value = configuration.getProperty(name);
        if (value == null)
        {
            return null;
        }
        else
        {
            Class<?> type = value.getClass();

            if (type == Byte.class)
            {
                type = Byte.TYPE;
            }
            if (type == Character.class)
            {
                type = Character.TYPE;
            }
            else if (type == Boolean.class)
            {
                type = Boolean.TYPE;
            }
            else if (type == Double.class)
            {
                type = Double.TYPE;
            }
            else if (type == Float.class)
            {
                type = Float.TYPE;
            }
            else if (type == Integer.class)
            {
                type = Integer.TYPE;
            }
            else if (type == Long.class)
            {
                type = Long.TYPE;
            }
            else if (type == Short.class)
            {
                type = Short.TYPE;
            }

            return new DynaProperty(name, type);
        }
    }

    @Override
    public DynaProperty[] getDynaProperties()
    {

        Iterator<String> keys = configuration.getKeys();
        List<DynaProperty> properties = new ArrayList<DynaProperty>();
        while (keys.hasNext())
        {
            String key = keys.next();
            DynaProperty property = getDynaProperty(key);
            properties.add(property);
        }

        DynaProperty[] propertyArray = new DynaProperty[properties.size()];
        properties.toArray(propertyArray);
 
        return propertyArray;
    }

    @Override
    public String getName()
    {
        return ConfigurationDynaBean.class.getName();
    }

    @Override
    public DynaBean newInstance() throws IllegalAccessException, InstantiationException
    {
        return new ConfigurationDynaBean(configuration);
    }
}
