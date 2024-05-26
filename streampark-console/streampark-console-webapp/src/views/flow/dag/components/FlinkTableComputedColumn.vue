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
    <Form ref="tableFormRef" :model="columnList" :rules="rules">
      <BasicTable @register="registerTable">
        <template #toolbar>
          <a-button type="primary" @click="addColumn">
            <Icon icon="ant-design:plus-outlined" />
            {{ t('common.add') }}
          </a-button>
        </template>
        <template #bodyCell="{ column, record, index }">
          <template v-if="column.dataIndex === 'columnName'">
            <a-form-item label="" :name="[index, 'columnName']" :rules="rules.columnName">
              <Input v-model:value="record.columnName" allowClear />
            </a-form-item>
          </template>
          <template v-if="column.dataIndex === 'computedColumnExpression'">
            <a-form-item label="" :name="[index, 'computedColumnExpression']">
              <Input
                v-model:value="record.computedColumnExpression"
                name="computedColumnExpression"
                allowClear
              />
            </a-form-item>
          </template>
          <template v-if="column.dataIndex === 'comment'">
            <a-form-item label="" :name="[index, 'comment']">
              <Input v-model:value="record.comment" name="comment" allowClear />
            </a-form-item>
          </template>
          <template v-if="column.dataIndex === 'action'">
            <a-popconfirm
              title="确定要删除么？"
              @confirm="removeColumn(record.columnName)"
              placement="topRight"
            >
              <icon type="delete" :title="t('common.delText')" icon="ant-design:delete-outlined" />
            </a-popconfirm>
          </template>
        </template>
      </BasicTable>
    </Form>
  </div>
</template>
<script lang="ts">
  export default {
    name: 'FlinkTableComputedColumn',
  };
</script>
<script setup lang="ts" name="FlinkTableComputedColumn">
  import { defineEmits, watch, ref } from 'vue';
  import { BasicTable, useTable } from '/@/components/Table';
  import { useI18n } from '/@/hooks/web/useI18n';
  import { Icon } from '/@/components/Icon';
  import { Input, Popconfirm, Form } from 'ant-design-vue';
  import { TFlinkTableComputedColumn } from '/@/api/model/flinkTableDefinition';

  const APopconfirm = Popconfirm;
  const AFormItem = Form.Item;

  const tableFormRef = ref();

  const emit = defineEmits(['update:value']);

  const props = defineProps({
    modelValue: {
      type: Array<TFlinkTableComputedColumn>,
      default: [],
    },
  });

  // const columnList = ref(props.columnList?.length ? props.columnList : [getColumn('', '', '')]);
  const columnList = ref([getColumn('', '', '')]);

  const { t } = useI18n();

  watch(
    () => columnList,
    (newVal) => {
      emit('update:value', newVal.value);
    },
    { deep: true },
  );

  const columns = [
    {
      title: '字段名',
      dataIndex: 'columnName',
      scopedSlots: { customRender: 'columnName' },
    },
    {
      title: '计算表达式',
      dataIndex: 'computedColumnExpression',
      scopedSlots: { customRender: 'computedColumnExpression' },
    },
    {
      title: '备注',
      dataIndex: 'comment',
      scopedSlots: { customRender: 'comment' },
    },
    {
      key: 'action',
      title: t('component.table.operation'),
      dataIndex: 'action',
      align: 'center',
      width: 100,
    },
  ];

  const rules = {
    columnName: [{ required: true, message: '请输入字段名', trigger: 'blur' }],
  };

  // 提交申请
  function validSchema() {
    return tableFormRef.value.validate();
  }

  const [registerTable, { reload }] = useTable({
    columns,
    dataSource: columnList,
    rowKey: 'id',
    pagination: false,
    striped: true,
    useSearchForm: false,
    showTableSetting: true,
    bordered: false,
    showIndexColumn: false,
    canResize: true,
  });

  function getColumn(columnName: String, computedColumnExpression: String, comment: String) {
    return {
      columnName,
      computedColumnExpression,
      comment,
    };
  }

  function addColumn() {
    columnList.value.push(getColumn('', '', ''));
  }

  function removeColumn(key: String) {
    columnList.value.forEach((item, index, self) => {
      if (item.columnName === key) {
        self.splice(index, 1);
      }
    });
    if (columnList.value.length === 0) {
      columnList.value.push(getColumn('', '', ''));
    }
  }

  defineExpose({
    validSchema,
  });
</script>
