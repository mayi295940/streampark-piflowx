/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import { BasicTableParams } from '../model/baseModel';
import { defHttp } from '/@/utils/http/axios';
import { Result } from '/#/axios';
import { AxiosResponse } from 'axios';
import { AddMemberParams, FlowListRecord, UpdateMemberParams } from './model/flowModel';

enum FLOW_API {
  POST = '/flow/saveFlowInfo',
  UPDATE = '/flow/updateFlowInfo',
  LIST = '/flow/getFlowListPage',
  DELETE = '/flow/deleteFlow',
}

/**
 * get member list
 * @param params
 * @returns
 */
export function fetchFlowList(data: BasicTableParams): Promise<FlowListRecord[]> {
  return defHttp.get({ url: FLOW_API.LIST, data });
}

/**
 * add member
 * @param {String} userName username
 * @param {Number} roleId role id
 * @returns {Promise<boolean>}
 */
export function fetchAddFlow(data: AddMemberParams) {
  return defHttp.post({ url: FLOW_API.POST, data });
}
/**
 * update member
 * @param {UpdateMemberParams} data
 * @returns {Promise<boolean|undefined>}
 */
export function fetchUpdateFlow(data: UpdateMemberParams): Promise<boolean | undefined> {
  return defHttp.put({ url: FLOW_API.UPDATE, data });
}

/**
 * delete
 * @param {String} data memeber Id
 * @returns {Promise<AxiosResponse<Result>>}
 */
export function fetchFlowDelete(data: { id: string }): Promise<AxiosResponse<Result>> {
  return defHttp.delete({ url: FLOW_API.DELETE, data }, { isReturnNativeResponse: true });
}
