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
 * Class for castor configuration writer, this class writes domain object to xml configuration.
 * 
 * @author ywang19, qzheng7
 *
 */
class CastorConfigWriter extends CastorConfigBase {

    private Marshaller marshaller;

    public CastorConfigWriter() {
        XMLContext context = getContext();
        Marshaller marshaller = context.createMarshaller();
        this.marshaller = marshaller;
    }

    protected String toXMLConfig(Object domain) {
        StringWriter writer = new StringWriter();
        try {
            marshaller.setWriter(writer);
        } catch (IOException e) {
            /* ignore it -- will not happen */
        }
        doMarshall(domain);
        return writer.toString();
    }

    private void doMarshall(Object domain) {
        try {
            marshaller.marshal(domain);
        } catch (Exception e) {
            String msg = "cannot generate XML configuration";
            msg += ", due to " + e.getMessage();
            throw new ConfigException(msg, e);
        }
    }

}
