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
  import { ref } from 'vue';
  import { PageWrapper } from '/@/components/Page';
  import { createAsyncComponent } from '/@/utils/factory/createAsyncComponent';
  import { BasicForm, useForm } from '/@/components/Form';
  import { useFlinkTableBaseInfo } from './useFlinkTableBaseInfo';

  const FlinkSqlEditor = createAsyncComponent(() => import('./SqlEditor.vue'), {
    loading: true,
  });

  const selectStatement = ref();

  const { getFlinkTableInfoFormSchema } = useFlinkTableBaseInfo();

  const [registerAppForm, {}] = useForm({
    labelCol: { lg: { span: 5, offset: 0 }, sm: { span: 7, offset: 0 } },
    wrapperCol: { lg: { span: 16, offset: 0 }, sm: { span: 17, offset: 0 } },
    baseColProps: { span: 24 },
    colon: true,
    showActionButtonGroup: false,
  });
</script>

<template>
  <PageWrapper contentBackground contentClass="p-26px app_controller">
    <BasicForm @register="registerAppForm" :schemas="getFlinkTableInfoFormSchema">
      <template #selectStatement="{ model, field }">
        <FlinkSqlEditor ref="selectStatement" v-model:value="model[field]" />
      </template>
    </BasicForm>
  </PageWrapper>
</template>
