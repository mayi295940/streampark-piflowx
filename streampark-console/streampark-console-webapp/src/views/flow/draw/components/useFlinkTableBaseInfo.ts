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
import { FormSchema } from '/@/components/Table';
import { computed, ref, unref } from 'vue';

import { useI18n } from '/@/hooks/web/useI18n';
const { t } = useI18n();

export const useFlinkTableBaseInfo = () => {
  const getFlinkTableInfoFormSchema = computed((): FormSchema[] => {
    return [
      {
        field: 'catalogName',
        label: '目录',
        component: 'Input',
      },
      {
        field: 'dbname',
        label: '数据库',
        component: 'Input',
      },
      {
        field: 'schema',
        label: 'Schema',
        component: 'Input',
      },
      {
        field: 'ifNotExists',
        label: '检测存在',
        component: 'Switch',
        defaultValue: false,
      },
      {
        field: 'tableName',
        label: '表名',
        component: 'Input',
      },
      {
        field: 'tableComment',
        label: '表备注',
        component: 'Input',
      },
      {
        field: 'selectStatement',
        label: 'Select语句',
        component: 'Input',
        slot: 'selectStatement',
      },
      {
        field: 'likeStatement',
        label: 'Like语句',
        component: 'Input',
      },
    ];
  });

  return { getFlinkTableInfoFormSchema };
};
