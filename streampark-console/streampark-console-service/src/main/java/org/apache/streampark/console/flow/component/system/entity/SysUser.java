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

package org.apache.streampark.console.flow.component.system.entity;

import org.apache.streampark.console.flow.base.BaseModelUUIDNoCorpAgentId;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Use JPA to define users. Implement the UserDetails interface, the user entity is the user used by
 * springSecurity.
 */
@Getter
@Setter
public class SysUser extends BaseModelUUIDNoCorpAgentId {

    private static final long serialVersionUID = 1L;

    private String username;
    private String password;
    private String name;
    private Byte status;
    private Integer age;
    private String sex;
    private String lastLoginIp;
    private String developerAccessKey;
    private List<SysRole> roles;
}
