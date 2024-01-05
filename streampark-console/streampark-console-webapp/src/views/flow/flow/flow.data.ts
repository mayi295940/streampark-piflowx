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
import { BasicColumn, FormSchema } from '/@/components/Table';
import { FormTypeEnum } from '/@/enums/formEnum';
import { useI18n } from '/@/hooks/web/useI18n';
import { renderEngineType } from './useFlowRender';

const { t } = useI18n();

export enum EngineTypeEnum {
  FLINK = 'flink',
  SPARK = 'spark',
}

export const columns: BasicColumn[] = [
  { title: t('flow.flow.flow_columns.name'), dataIndex: 'name', sorter: true },
  { title: t('flow.flow.flow_columns.engine_type'), dataIndex: 'engineType', sorter: true },
  { title: t('flow.flow.flow_columns.description'), dataIndex: 'description' },
  { title: t('common.createTime'), dataIndex: 'crtDttmString' },
];

export const searchFormSchema: FormSchema[] = [
  {
    field: 'name',
    label: t('flow.flow.flow_columns.name'),
    component: 'Input',
    colProps: { span: 8 },
  },
  {
    field: 'createTime',
    label: t('common.createTime'),
    component: 'RangePicker',
    colProps: { span: 8 },
  },
];

export const formSchema = (formType: string): FormSchema[] => {
  const isCreate = formType === FormTypeEnum.Create;
  const isView = formType === FormTypeEnum.View;
  return [
    { field: 'id', label: 'Id', component: 'Input', show: false },
    {
      field: 'name',
      label: t('flow.flow.flow_columns.name'),
      component: 'Input',
      rules: [
        { required: isCreate, message: t('flow.flow.flow_form.required') },
        { min: 2, message: t('flow.flow.flow_form.min') },
        { max: 20, message: t('flow.flow.flow_form.max') },
        {
          validator: async (_, value) => {
            if (!isCreate || !value || value.length < 2 || value.length > 20) {
              return Promise.resolve();
            }
          },
          trigger: 'blur',
        },
      ],
      componentProps: { id: 'formUserName', disabled: !isCreate },
    },
    {
      label: t('flow.flow.flow_columns.engine_type'),
      field: 'engineType',
      component: 'Select',
      render: ({ model }) => renderEngineType({ model }),
      defaultValue: EngineTypeEnum.FLINK,
      rules: [{ required: true }],
    },
    {
      field: 'description',
      label: t('common.description'),
      component: 'InputTextArea',
      componentProps: { rows: 5 },
      ifShow: isCreate,
    },
  ];
};
