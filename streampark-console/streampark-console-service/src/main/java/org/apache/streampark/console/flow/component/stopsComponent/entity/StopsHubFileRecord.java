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

package org.apache.streampark.console.flow.component.stopsComponent.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/** 上传算法包的具体文件记录表（stops_hub_file_record） */
@Setter
@Getter
public class StopsHubFileRecord {

    private static final long serialVersionUID = 1L;

    private String id;
    private String fileName; // file name
    private String filePath; // file path
    private String stopsHubId; // sparkHub id
    private String dockerImagesName; // image name
    private Date crtDttm = new Date(); // create time

    private Boolean isComponent =
        false; // is component(not save in db,get for link select,used for component list display )
    // TODO 是否要加上
    private StopsComponent stopsComponent;
}
