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
    name: 'FlinkTableBaseInfo',
  };
</script>
<script setup lang="ts" name="FlinkTableBaseInfo">
  import { ref, watch, PropType, onMounted } from 'vue';
  import { createAsyncComponent } from '/@/utils/factory/createAsyncComponent';
  import { Form, Input, Switch } from 'ant-design-vue';
  import { TFlinkTableBaseInfo } from '/@/api/model/flinkTableDefinition';

  const FlinkSqlEditor = createAsyncComponent(() => import('./SqlEditor.vue'), {
    loading: true,
  });

  const props = defineProps({
    modelValue: {
      type: Object as PropType<TFlinkTableBaseInfo>,
      default: null,
    },
  });

  let formData = ref({
    catalogName: null,
    dbname: null,
    schema: null,
    ifNotExists: null,
    registerTableName: null,
    registerTableComment: null,
    selectStatement: null,
    likeStatement: null,
  });

  const emits = defineEmits(['update:value']);

  // onMounted(() => {
  //   const { catalogName, dbname, schema, ifNotExists, registerTableName, registerTableComment } =
  //     props.modelValue || {};
  //   Object.assign(formData, {
  //     catalogName,
  //     dbname,
  //     schema,
  //     ifNotExists,
  //     registerTableName,
  //     registerTableComment,
  //   });
  // });

  // watch(
  //   () => props,
  //   (newValue) => {
  //     if (!newValue || !Object.keys(newValue).length) return;
  //     debugger;
  //     const { catalogName, dbname, schema, ifNotExists, registerTableName, registerTableComment } =
  //       newValue.modelValue || {};
  //     Object.assign(formData, {
  //       catalogName,
  //       dbname,
  //       schema,
  //       ifNotExists,
  //       registerTableName,
  //       registerTableComment,
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
  <Form ref="formRef" autocomplete="off">
    <!-- <Form :model="formData" ref="formRef" autocomplete="off"> -->
    <Form.Item name="catalogName" label="目录" :label-col="formStyle.labelCol">
      <Input v-model:value="formData.catalogName" size="large" class="fix-auto-fill" />
    </Form.Item>
    <Form.Item name="dbname" label="数据库" :label-col="formStyle.labelCol">
      <Input v-model:value="formData.dbname" size="large" class="fix-auto-fill" />
    </Form.Item>
    <Form.Item name="schema" label="Schema" :label-col="formStyle.labelCol">
      <Input v-model:value="formData.schema" size="large" class="fix-auto-fill" />
    </Form.Item>
    <Form.Item name="tableName" label="注册表名" :label-col="formStyle.labelCol">
      <Input v-model:value="formData.registerTableName" size="large" class="fix-auto-fill" />
    </Form.Item>
    <Form.Item name="tableComment" label="注册表备注" :label-col="formStyle.labelCol">
      <Input v-model:value="formData.registerTableComment" size="large" class="fix-auto-fill" />
    </Form.Item>
    <Form.Item name="ifNotExists" label="存在检查" :label-col="formStyle.labelCol">
      <Switch
        checked-children="是"
        un-checked-children="否"
        v-model:checked="formData.ifNotExists"
      />
    </Form.Item>
    <Form.Item name="selectStatement" label="查询语句" :label-col="formStyle.labelCol">
      <Input v-model:value="formData.selectStatement" size="large" class="fix-auto-fill" />
    </Form.Item>
    <Form.Item
      name="likeStatement"
      class="enter-x"
      label="Like语句"
      :label-col="formStyle.labelCol"
    >
      <Input v-model:value="formData.likeStatement" size="large" class="fix-auto-fill" />
    </Form.Item>
  </Form>
</template>
