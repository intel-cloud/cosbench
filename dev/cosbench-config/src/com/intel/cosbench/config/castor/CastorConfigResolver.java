/** 
 
Copyright 2013 Intel Corporation, All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. 
*/ 

package com.intel.cosbench.config.castor;

import java.io.*;

import org.exolab.castor.xml.*;

import com.intel.cosbench.config.ConfigException;

/**
 * Abstract class for Castor resolver, this class converts xml configuration into domain object.
 * 
 * @author ywang19, qzheng7
 *
 */
abstract class CastorConfigResolver extends CastorConfigBase {

    private Unmarshaller unmarshaller;

    protected abstract Class<?> getExpectedClass();

    public CastorConfigResolver() {
        XMLContext context = getContext();
        Unmarshaller unmarshaller = context.createUnmarshaller();
        this.unmarshaller = unmarshaller;
        bindClassMetaInfo();
    }

    private void bindClassMetaInfo() {
        Class<?> clazz = getExpectedClass();
        unmarshaller.setClass(clazz);
        unmarshaller.setClassLoader(clazz.getClassLoader());
    }

    protected Object toDomainObject(String path) {
        Reader reader = loadXmlConfig(path);
        return toDomainObject(reader, path);
    }

    private static Reader loadXmlConfig(String path) {
        Reader reader = null;
        try {
            reader = new FileReader(path);
        } catch (Exception e) {
            String msg = "cannot load the xml configuration from " + path;
            msg += ", due to " + e.getMessage();
            throw new ConfigException(msg, e);
        }
        return reader;
    }

    protected Object toDomainObject(Reader reader, String path) {
        Object object = null;
        try {
            object = unmarshaller.unmarshal(reader);
        } catch (Exception e) {
            String msg = "cannot parse the xml configuration from " + path;
            msg += ", due to " + e.getMessage();
            throw new ConfigException(msg, e);
        }
        return object;
    }

}
