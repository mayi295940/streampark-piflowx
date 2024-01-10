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
          <template v-if="column.dataIndex === 'columnType'">
            <a-form-item label="" :name="[index, 'columnType']" :rules="rules.columnType">
              <Select
                v-model:value="record.columnType"
                allowClear
                @change="(value: string[]) => (record.columnType = value)"
              >
                <SelectOption v-for="(v, k) in colunmTypeList" :key="`kind_${k}`" :value="v.value">
                  {{ v.name }}
                </SelectOption>
              </Select>
            </a-form-item>
          </template>
          <template v-if="column.dataIndex === 'length'">
            <a-form-item label="" :name="[index, 'length']">
              <InputNumber v-model:value="record.length" name="length" allow-clear />
            </a-form-item>
          </template>
          <template v-if="column.dataIndex === 'precision'">
            <a-form-item label="" :name="[index, 'precision']">
              <InputNumber v-model:value="record.precision" name="precision" allowClear />
            </a-form-item>
          </template>
          <template v-if="column.dataIndex === 'scale'">
            <a-form-item label="" :name="[index, 'scale']">
              <InputNumber v-model:value="record.scale" name="scale" allowClear />
            </a-form-item>
          </template>
          <template v-if="column.dataIndex === 'nullable'">
            <a-form-item label="" :name="[index, 'nullable']">
              <Switch
                checked-children="是"
                un-checked-children="否"
                v-model:checked="record.nullable"
              />
            </a-form-item>
          </template>
          <template v-if="column.dataIndex === 'primaryKey'">
            <a-form-item label="" :name="[index, 'primaryKey']">
              <Switch
                checked-children="是"
                un-checked-children="否"
                v-model:checked="record.primaryKey"
              />
            </a-form-item>
          </template>
          <template v-if="column.dataIndex === 'partitionKey'">
            <a-form-item label="" :name="[index, 'partitionKey']">
              <Switch
                checked-children="是"
                un-checked-children="否"
                v-model:checked="record.partitionKey"
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
    name: 'FlinkTablePhysicalColumn',
  };
</script>
<script setup lang="ts" name="FlinkTablePhysicalColumn">
  import { defineEmits, watch, ref } from 'vue';
  import { BasicTable, useTable } from '/@/components/Table';
  import { useI18n } from '/@/hooks/web/useI18n';
  import { Icon } from '/@/components/Icon';
  import { Select, Input, Popconfirm, Form, Switch, InputNumber } from 'ant-design-vue';
  import { colunmTypeList } from './draw.data';
  import { TFlinkTablePhysicalColumn } from '/@/api/model/flinkTableDefinition';

  const SelectOption = Select.Option;
  const APopconfirm = Popconfirm;
  const AFormItem = Form.Item;

  const tableFormRef = ref();

  const props = defineProps({
    modelValue: {
      type: Array<TFlinkTablePhysicalColumn>,
      default: [],
    },
  });
  const emits = defineEmits(['update:value']);

  //const columnList = ref(props.modelValue?.length ? props.modelValue : [getColumn('', '')]);
  const columnList = ref([getColumn('', '')]);

  const { t } = useI18n();

  const columns = [
    {
      title: '字段名',
      dataIndex: 'columnName',
      scopedSlots: { customRender: 'columnName' },
    },
    {
      title: '字段类型',
      dataIndex: 'columnType',
      scopedSlots: { customRender: 'columnType' },
    },
    {
      title: '长度',
      dataIndex: 'length',
      scopedSlots: { customRender: 'length' },
    },
    {
      title: '精度',
      dataIndex: 'precision',
      scopedSlots: { customRender: 'precision' },
    },
    {
      title: '小数位数',
      dataIndex: 'scale',
      scopedSlots: { customRender: 'scale' },
    },
    {
      title: '可为空',
      dataIndex: 'nullable',
      scopedSlots: { customRender: 'nullable' },
    },
    {
      title: '主键',
      dataIndex: 'primaryKey',
      scopedSlots: { customRender: 'primaryKey' },
    },
    {
      title: '分区键',
      dataIndex: 'partitionKey',
      scopedSlots: { customRender: 'partitionKey' },
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
      width: 80,
    },
  ];

  const rules = {
    blur: [{ required: true, message: '请输入', trigger: 'blur' }],
    change: [{ required: true, message: '请选择', trigger: 'change' }],
    columnName: [{ required: true, message: '请输入字段名', trigger: 'blur' }],
    columnType: [{ required: true, message: '请选择字段类型', trigger: 'change' }],
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

  function getColumn(columnName: String, columnType: String) {
    return {
      columnName,
      columnType,
    };
  }

  function addColumn() {
    columnList.value.push(getColumn('', ''));
  }

  function removeColumn(key: String) {
    columnList.value.forEach((item, index, self) => {
      if (item.columnName === key) {
        self.splice(index, 1);
      }
    });
    if (columnList.value.length === 0) {
      columnList.value.push(getColumn('', ''));
    }
  }

  watch(
    () => columnList,
    (newValue) => {
      emits('update:value', newValue.value);
    },
    { deep: true, immediate: true },
  );

  function handleEvent() {
    const value = columnList.value.map(({ name, type, mockType }) => {
      return { name, type, mockType };
    });
    const output = columnList.value.map(({ name, type }) => {
      return { name, columnType: type };
    });
    emits('update:value', () => ({
      value: JSON.stringify(value),
      output,
    }));
  }

  defineExpose({
    validSchema,
    handleEvent,
  });
</script>
