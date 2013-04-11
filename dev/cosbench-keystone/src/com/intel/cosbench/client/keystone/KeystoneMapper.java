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

package com.intel.cosbench.client.keystone;

import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

public class KeystoneMapper {

    private ObjectMapper mapper;

    public KeystoneMapper() {
        mapper = new ObjectMapper();
        configSerialization(mapper);
        configDeserializarion(mapper);
    }

    private void configSerialization(ObjectMapper mapper) {
        SerializationConfig config = mapper.copySerializationConfig();
        config.setSerializationInclusion(Inclusion.NON_NULL);
        mapper.setSerializationConfig(config);

    }

    private void configDeserializarion(ObjectMapper mapper) {
        DeserializationConfig config = mapper.copyDeserializationConfig();
        config.disable(Feature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.setDeserializationConfig(config);
    }

    public String toJson(Object obj) {
        String json = null;
        try {
            json = mapper.writeValueAsString(obj);
        } catch (Exception ex) {
            String e = "fail to generate any json string from the given object";
            throw new KeystoneClientException(e, ex); // should never happen
        }
        return json;
    }

    public <T> T fromJson(String json, Class<T> clazz) {
        T value = null;
        try {
            value = mapper.readValue(json, clazz);
        } catch (Exception ex) {
            String e = "fail to generate any obj from the given json string";
            throw new KeystoneClientException(e, ex); // should never happen
        }
        return value;
    }

}
