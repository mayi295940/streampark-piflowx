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

package org.apache.streampark.console.flow.component.system.mapper.provider;

import org.apache.streampark.console.flow.base.utils.DateUtils;
import org.apache.streampark.console.flow.base.utils.SqlUtils;
import org.apache.streampark.console.flow.base.utils.UUIDUtils;
import org.apache.streampark.console.flow.component.system.entity.SysInitRecords;

import org.apache.commons.lang3.StringUtils;

import java.util.Date;

public class SysInitRecordsMapperProvider {

    private String id;
    private String initDate;
    private int isSucceed;

    private boolean preventSQLInjection(SysInitRecords sysInitRecords) {

        if (null == sysInitRecords) {
            return false;
        }
        String id =
            StringUtils.isBlank(sysInitRecords.getId())
                ? UUIDUtils.getUUID32()
                : sysInitRecords.getId();
        Date initDate = sysInitRecords.getInitDate();
        String initDateStr = DateUtils.dateTimesToStr(null != initDate ? initDate : new Date());
        this.id = SqlUtils.preventSQLInjection(id);
        this.initDate = SqlUtils.preventSQLInjection(initDateStr);
        this.isSucceed =
            ((null != sysInitRecords.getIsSucceed() && sysInitRecords.getIsSucceed()) ? 1 : 0);
        return true;
    }

    private void reset() {
        this.id = null;
        this.initDate = null;
        this.isSucceed = 0;
    }

    /**
     * insertSysInitRecords
     *
     * @param sysInitRecords
     * @return
     */
    public String insertSysInitRecords(SysInitRecords sysInitRecords) {
        String sqlStr = "select 0";
        if (preventSQLInjection(sysInitRecords)) {
            StringBuffer strBuf = new StringBuffer();
            strBuf.append("INSERT INTO sys_init_records ");
            strBuf.append("( ");
            strBuf.append("id, ");
            strBuf.append("init_date, ");
            strBuf.append("is_succeed ");
            strBuf.append(") ");
            strBuf.append("values ");
            strBuf.append("(");
            strBuf.append(this.id + ", ");
            strBuf.append(this.initDate + ", ");
            strBuf.append(this.isSucceed + " ");
            strBuf.append(") ");
            sqlStr = strBuf.toString();
        }
        this.reset();
        return sqlStr;
    }
}
