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
  <div>
    <Tabs tabPosition="top">
      <TabPane key="tableBaseInfo" tab="表信息">
        <FlinkTableBaseInfo v-model:value="tableBaseInfo" />
      </TabPane>
      <TabPane key="physicalColumnDefinition" tab="物理列">
        <FlinkTablePhysicalColumn v-model:value="physicalColumnDefinition" />
      </TabPane>
      <TabPane key="metadataColumnDefinition" tab="元数据列">
        <FlinkTableMetadataColumn v-model:value="metadataColumnDefinition" />
      </TabPane>
      <TabPane key="computedColumnDefinition" tab="计算列">
        <FlinkTableComputedColumn v-model:value="computedColumnDefinition" />
      </TabPane>
      <TabPane key="watermarkDefinition" tab="水印">
        <FlinkTableWatermark v-model:value="watermarkDefinition" />
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
  import { computed } from 'vue';
  import { Tabs } from 'ant-design-vue';
  import { FlinkTableDefinition } from '/@/api/flink/model/flinkTableDefinition';
  import FlinkTablePhysicalColumn from './FlinkTablePhysicalColumn.vue';
  import FlinkTableComputedColumn from './FlinkTableComputedColumn.vue';
  import FlinkTableMetadataColumn from './FlinkTableMetadataColumn.vue';
  import FlinkTableBaseInfo from './FlinkTableBaseInfo.vue';
  import FlinkTableWatermark from './FlinkTableWatermark.vue';

  const TabPane = Tabs.TabPane;

  const props = defineProps<FlinkTableDefinition>();
  const tableBaseInfo = computed(() => props.tableBaseInfo);
  const selectStatement = computed(() => props.asSelectStatement);
  const likeStatement = computed(() => props.likeStatement);
  const physicalColumnDefinition = computed(() => props.physicalColumnDefinition);
  const metadataColumnDefinition = computed(() => props.metadataColumnDefinition);
  const computedColumnDefinition = computed(() => props.computedColumnDefinition);
  const watermarkDefinition = computed(() => props.watermarkDefinition);

  // defineExpose({
  //   validSchema,
  // });
</script>
