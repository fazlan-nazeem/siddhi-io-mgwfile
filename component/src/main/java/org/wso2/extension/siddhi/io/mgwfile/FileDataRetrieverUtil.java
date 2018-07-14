/*
 * Copyright (c) 2018 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.extension.siddhi.io.mgwfile;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.wso2.carbon.databridge.commons.StreamDefinition;
import org.wso2.extension.siddhi.io.mgwfile.exception.FileBasedAnalyticsException;

import java.util.HashMap;
import java.util.Map;

/**
 * Util Class for FileDataRetriever
 */
public class FileDataRetrieverUtil {

    private static volatile Map<String, JSONArray> streamDefinitions = new HashMap<>();

    public static void initStreamDefinitions(StreamDefinition streamDefinition, String streamId) throws FileBasedAnalyticsException {
        try {
            String jsonStr = streamDefinition.toString();
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(jsonStr.toString());
            streamDefinitions.put(streamId, (JSONArray) jsonObject.get("payloadData"));
        } catch (ParseException e) {
            throw new FileBasedAnalyticsException("error");
        }
    }


    public static Object createMetaData(String str) throws Exception {
        if (str.isEmpty() || "null".equals(str)) {
            return null;
        }
        return new Object[]{str};
    }

    public static Object[] createPayload(String streamId, String str) throws Exception {

        JSONArray jsonArray = streamDefinitions.get(streamId);
        if (jsonArray != null) {
            String[] strings = str.split(MGWFileSourceConstants.OBJECT_SEPARATOR);
            Object[] objects = new Object[strings.length];
            for (int i = 0; i < strings.length; i++) {
                JSONObject obj = (JSONObject) jsonArray.get(i);
                objects[i] = getPayloadObject((String) obj.get("type"), strings[i].trim());
            }
            return objects;
        }
        return new Object[0];
    }

    public static Object getPayloadObject(String type, String string) throws Exception {
        if (string == null || string.isEmpty()) {
            return null;
        }
        switch (type) {
            case "STRING":
                return string;
            case "INT":
                return Integer.parseInt(string);
            case "LONG":
                return Long.parseLong(string);
            case "BOOL":
                return Boolean.parseBoolean(string);
            default:
                return string;
        }
    }

}
