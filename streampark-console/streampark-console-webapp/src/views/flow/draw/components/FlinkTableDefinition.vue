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
<template>
  <div class="ml-15px">
    <Tabs tabPosition="top">
      <TabPane key="tableBaseInfo" tab="表信息">
        <FlinkTableBaseInfo
          v-model:modelValue="tableBaseInfo"
          @update:value="handleBaseInfoEvent"
        />
      </TabPane>
      <TabPane key="physicalColumnDefinition" tab="物理列">
        <FlinkTablePhysicalColumn
          v-model:modelValue="physicalColumnDefinition"
          @update:value="handlePhysicalColumnEvent"
        />
      </TabPane>
      <TabPane key="metadataColumnDefinition" tab="元数据列">
        <FlinkTableMetadataColumn
          v-model:modelValue="metadataColumnDefinition"
          @update:value="handleMetadataColumnEvent"
        />
      </TabPane>
      <TabPane key="computedColumnDefinition" tab="计算列">
        <FlinkTableComputedColumn
          v-model:modelValue="computedColumnDefinition"
          @update:value="handleComputedColumnEvent"
        />
      </TabPane>
      <TabPane key="watermarkDefinition" tab="水印">
        <FlinkTableWatermark v-model="watermarkDefinition" @update:value="handleWatermarkEvent" />
      </TabPane>
    </Tabs>
  </div>
</template>
<script lang="ts">
  export default {
    name: 'FlinkTableDefinition',
  };
</script>
<script setup lang="ts" name="FlinkTableDefinition">
  import { computed, watch, ref } from 'vue';
  import { Tabs } from 'ant-design-vue';
  import {
    TFlinkTableDefinition,
    TFlinkTableBaseInfo,
    TFlinkTablePhysicalColumn,
    TFlinkTableMetadataColumn,
    TFlinkTableComputedColumn,
    TFlinkTableWatermark,
    TFlinkTableAsSelectStatement,
    TFlinkTableLikeStatement,
  } from '/@/api/model/flinkTableDefinition';
  import FlinkTablePhysicalColumn from './FlinkTablePhysicalColumn.vue';
  import FlinkTableComputedColumn from './FlinkTableComputedColumn.vue';
  import FlinkTableMetadataColumn from './FlinkTableMetadataColumn.vue';
  import FlinkTableBaseInfo from './FlinkTableBaseInfo.vue';
  import FlinkTableWatermark from './FlinkTableWatermark.vue';

  const TabPane = Tabs.TabPane;

  const props = defineProps<TFlinkTableDefinition>();

  const tableBaseInfo = computed<TFlinkTableBaseInfo>(() => props.tableBaseInfo);
  const selectStatement = computed<TFlinkTableAsSelectStatement>(() => props.asSelectStatement);
  const likeStatement = computed<TFlinkTableLikeStatement>(() => props.likeStatement);
  const physicalColumnDefinition = computed<Array<TFlinkTablePhysicalColumn>>(
    () => props.physicalColumnDefinition,
  );
  const metadataColumnDefinition = computed<Array<TFlinkTableMetadataColumn>>(
    () => props.metadataColumnDefinition,
  );
  const computedColumnDefinition = computed<Array<TFlinkTableComputedColumn>>(
    () => props.computedColumnDefinition,
  );
  const watermarkDefinition = computed<TFlinkTableWatermark>(() => props.watermarkDefinition);

  const updateTableBaseInfo = ref<TFlinkTableBaseInfo>();
  const updatePhysicalColumnDefinition = ref<Array<TFlinkTablePhysicalColumn>>();
  const updateMetadataColumnDefinition = ref<Array<TFlinkTableMetadataColumn>>();
  const updateComputedColumnDefinition = ref<Array<TFlinkTableComputedColumn>>();
  const updateWatermarkDefinition = ref<TFlinkTableWatermark>();
  const updateAsSelectStatement = ref<TFlinkTableAsSelectStatement>();
  const updateLikeStatement = ref<TFlinkTableLikeStatement>();

  const updateTableDefinition = ref({
    tableBaseInfo: updateTableBaseInfo,
    physicalColumnDefinition: updatePhysicalColumnDefinition,
    metadataColumnDefinition: updateMetadataColumnDefinition,
    computedColumnDefinition: updateComputedColumnDefinition,
    watermarkDefinition: updateWatermarkDefinition,
    asSelectStatement: updateAsSelectStatement,
    likeStatement: updateLikeStatement,
  });

  const emits = defineEmits(['update:updateTableDefinition']);

  watch(
    () => updateTableDefinition,
    () => {
      const result = {
        tableBaseInfo: updateTableBaseInfo.value,
        physicalColumnDefinition: updatePhysicalColumnDefinition.value,
        metadataColumnDefinition: updateMetadataColumnDefinition.value,
        computedColumnDefinition: updateComputedColumnDefinition.value,
        watermarkDefinition: updateWatermarkDefinition.value,
        asSelectStatement: updateAsSelectStatement.value,
        likeStatement: updateLikeStatement.value,
      };
      emits('update:updateTableDefinition', result);
    },
    { deep: true, immediate: true },
  );

  function handleBaseInfoEvent(event: TFlinkTableBaseInfo) {
    updateTableBaseInfo.value = event;
    updateAsSelectStatement.value = { selectStatement: event.selectStatement };
    updateLikeStatement.value = { likeStatement: event.likeStatement };
  }

  function handlePhysicalColumnEvent(event: Array<TFlinkTablePhysicalColumn>) {
    updatePhysicalColumnDefinition.value = event;
  }

  function handleMetadataColumnEvent(event: Array<TFlinkTableMetadataColumn>) {
    updateMetadataColumnDefinition.value = event;
  }

  function handleComputedColumnEvent(event: Array<TFlinkTableComputedColumn>) {
    updateComputedColumnDefinition.value = event;
  }

  function handleWatermarkEvent(event: TFlinkTableWatermark) {
    updateWatermarkDefinition.value = event;
  }
</script>
