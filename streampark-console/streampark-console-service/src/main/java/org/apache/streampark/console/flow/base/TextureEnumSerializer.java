/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.streampark.console.flow.base;

import org.apache.streampark.console.flow.base.utils.LoggerUtil;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.slf4j.Logger;

import java.io.IOException;

public class TextureEnumSerializer extends JsonSerializer<Object> {

    /** Introducing logs, note that they are all packaged under "org.slf4j" */
    private Logger logger = LoggerUtil.getLogger();

    @Override
    public void serialize(Object value, JsonGenerator generator,
                          SerializerProvider provider) throws IOException, JsonProcessingException {
        if (value == null) {
            return;
        }
        generator.writeStartObject();
        generator.writeFieldName("stringValue");
        generator.writeString(value.toString());
        generator.writeFieldName("text");
        try {
            generator.writeString(org.apache.commons.beanutils.BeanUtils.getProperty(value, "text"));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        generator.writeEndObject();
    }
}
