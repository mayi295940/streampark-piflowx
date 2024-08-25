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

package org.apache.streampark.console.flow.component.stopsComponent.mapper.provider;

import org.apache.streampark.console.flow.base.utils.DateUtils;
import org.apache.streampark.console.flow.base.utils.SqlUtils;
import org.apache.streampark.console.flow.component.stopsComponent.entity.StopsHubFileRecord;

import org.apache.ibatis.jdbc.SQL;

import java.util.Date;

public class StopsHubFileRecordMapperProvider {

    private String id;
    private String fileName;
    private String filePath;
    private String stopsHubId;
    private String dockerImagesName;
    private String crtDttmStr;

    private boolean preventSQLInjectionStopsHubFileRecord(StopsHubFileRecord record) {
        if (null == record) {
            return false;
        }
        this.id = SqlUtils.preventSQLInjection(record.getId());
        this.fileName = SqlUtils.preventSQLInjection(record.getFileName());
        this.filePath = SqlUtils.preventSQLInjection(record.getFilePath());
        this.stopsHubId = SqlUtils.preventSQLInjection(record.getStopsHubId());
        this.dockerImagesName = SqlUtils.preventSQLInjection(record.getDockerImagesName());
        this.crtDttmStr =
            SqlUtils.preventSQLInjection(
                DateUtils.dateTimesToStr(
                    null != record.getCrtDttm() ? record.getCrtDttm() : new Date()));

        return true;
    }

    private void reset() {
        this.id = null;
        this.fileName = null;
        this.filePath = null;
        this.stopsHubId = null;
        this.dockerImagesName = null;
        this.crtDttmStr = null;
    }

    /**
     * add StopsHubFileRecord
     *
     * @param record
     * @return
     */
    public String addStopsHubFileRecord(StopsHubFileRecord record) {
        String sqlStr = "SELECT 0";
        boolean flag = this.preventSQLInjectionStopsHubFileRecord(record);
        if (flag) {
            SQL sql = new SQL();
            sql.INSERT_INTO("stops_hub_file_record");
            sql.VALUES("id", id);
            sql.VALUES("file_name", fileName);
            sql.VALUES("file_path", filePath);
            sql.VALUES("stops_hub_id", stopsHubId);
            sql.VALUES("docker_images_name", dockerImagesName);
            sql.VALUES("crt_dttm", crtDttmStr);
            sqlStr = sql.toString();
        }
        this.reset();
        return sqlStr;
    }
}
