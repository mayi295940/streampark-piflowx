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

package org.apache.streampark.console.flow.controller.system;

import org.apache.streampark.console.flow.base.utils.ReturnMapUtils;
import org.apache.streampark.console.flow.common.constant.MessageConfig;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Api(value = "system language", tags = "system language")
@Controller
@RequestMapping("/language")
public class ChangeLanguageCtrl {

    @RequestMapping(value = "/changZH", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "changZH", notes = "chang system language ZH")
    public String changZH() {
        MessageConfig.LANGUAGE = MessageConfig.LANGUAGE_TYPE_ZH;
        return ReturnMapUtils.setSucceededMsgRtnJsonStr(MessageConfig.SUCCEEDED_MSG());
    }

    @RequestMapping(value = "/changEN", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "changEN", notes = "chang system language ZH")
    public String changEN(Integer page, Integer limit, String param) {
        MessageConfig.LANGUAGE = MessageConfig.LANGUAGE_TYPE_EN;
        return ReturnMapUtils.setSucceededMsgRtnJsonStr(MessageConfig.SUCCEEDED_MSG());
    }
}
