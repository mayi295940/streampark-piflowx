<!--
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

      https://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->
<script lang="ts">
  export default {
    name: 'FlinkTableWatermark',
  };
</script>
<script setup lang="ts" name="FlinkTableWatermark">
  import { ref, watch, PropType } from 'vue';
  import { PageWrapper } from '/@/components/Page';
  import { Form, Input, Select } from 'ant-design-vue';
  import { TFlinkTableWatermark } from '/@/api/model/flinkTableDefinition';

  const props = defineProps({
    modelValue: {
      type: Object as PropType<TFlinkTableWatermark>,
      default: null,
    },
  });

  let formData = ref({
    rowTimeColumnName: null,
    time: null,
    timeUnit: null,
  });

  const emits = defineEmits(['update:value']);

  const timeUnitList = [
    { label: '天', value: 'DAY' },
    { label: '小时', value: 'HOUR' },
    { label: '分钟', value: 'MINUTE' },
    { label: '秒', value: 'SECOND' },
    { label: '毫秒', value: 'MILLISECOND' },
    { label: '微秒', value: 'MICROSECOND' },
    { label: '纳秒', value: 'NANOSECOND' },
  ];

  // onMounted(() => {
  //   const { catalogName, dbname, schema, ifNotExists, tableName, tableComment } =
  //     props.modelValue || {};
  //   Object.assign(formData, {
  //     catalogName,
  //     dbname,
  //     schema,
  //     ifNotExists,
  //     tableName,
  //     tableComment,
  //   });
  // });

  // watch(
  //   () => props,
  //   (newValue) => {
  //     if (!newValue || !Object.keys(newValue).length) return;
  //     debugger;
  //     const { catalogName, dbname, schema, ifNotExists, tableName, tableComment } =
  //       newValue.modelValue || {};
  //     Object.assign(formData, {
  //       catalogName,
  //       dbname,
  //       schema,
  //       ifNotExists,
  //       tableName,
  //       tableComment,
  //     });
  //     console.log('formData000000000000', formData);
  //   },
  //   { deep: true, immediate: true },
  // );
  watch(
    () => formData,
    (newValue) => {
      emits('update:value', newValue.value);
    },
    { deep: true, immediate: true },
  );
  const formStyle = {
    labelCol: { lg: { span: 3, offset: 0 }, sm: { span: 7, offset: 0 } },
    wrapperCol: { lg: { span: 16, offset: 0 }, sm: { span: 4, offset: 0 } },
  };
</script>
<template>
  <PageWrapper contentBackground contentClass="p-26px">
    <Form ref="formRef" autocomplete="off">
      <Form.Item
        name="rowTimeColumnName"
        class="enter-x"
        label="时间字段"
        :label-col="formStyle.labelCol"
      >
        <Input v-model:value="formData.rowTimeColumnName" size="large" class="fix-auto-fill" />
      </Form.Item>
      <Form.Item name="time" class="enter-x" label="水印时间" :label-col="formStyle.labelCol">
        <Input v-model:value="formData.time" type="number" size="large" class="fix-auto-fill" />
      </Form.Item>
      <Form.Item name="timeUnit" class="enter-x" label="时间单位" :label-col="formStyle.labelCol">
        <Select
          v-model:value="formData.timeUnit"
          allowClear
          @change="(value: string[]) => (formData.timeUnit = value)"
        >
          <SelectOption v-for="(v, k) in timeUnitList" :key="`kind_${k}`" :value="v.value">
            {{ v.label }}
          </SelectOption>
        </Select>
      </Form.Item>
    </Form>
  </PageWrapper>
</template>
