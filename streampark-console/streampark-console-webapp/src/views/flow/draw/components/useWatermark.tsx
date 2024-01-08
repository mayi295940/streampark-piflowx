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
import { computed } from 'vue';
import { useForm } from '/@/components/Form';

export const filterOption = (input: string, options: Recordable) => {
  return options.label.toLowerCase().indexOf(input.toLowerCase()) >= 0;
};

export const useWatermark = () => {
  const watermarkFormSchema = computed((): FormSchema[] => {
    return [
      {
        field: 'rowTimeColumnName',
        label: '时间字段',
        component: 'Input',
        rules: [{ required: true, trigger: 'blur' }],
      },
      {
        field: 'time',
        label: '水印时间',
        component: 'InputNumber',
        rules: [{ required: true, trigger: 'blur' }],
        componentProps: { width: '100%' },
      },
      {
        field: 'timeUnit',
        label: '时间单位',
        component: 'Select',
        rules: [{ required: true, trigger: 'blur' }],
        componentProps: {
          options: [
            { label: '小时', value: 'h', disabled: false },
            { label: '分钟', value: 'm', disabled: false },
            { label: '秒', value: 's', disabled: false },
          ],
          showSearch: true,
          optionFilterProp: 'children',
          filterOption,
        },
      },
    ];
  });

  const [registerForm, { submit, setFieldsValue }] = useForm({
    labelWidth: 50,
    colon: true,
    labelCol: { lg: { span: 5, offset: 0 }, sm: { span: 7, offset: 0 } },
    wrapperCol: { lg: { span: 16, offset: 0 }, sm: { span: 17, offset: 0 } },
    schemas: watermarkFormSchema.value,
    showActionButtonGroup: false,
    showSubmitButton: false,
    baseColProps: { span: 24 },
  });

  return {
    submit,
    registerForm,
  };
};
