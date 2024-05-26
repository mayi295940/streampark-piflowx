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
    <Form
      ref="tableFormRef"
      :model="columnList"
      :label-col="{ style: { width: '10px' } }"
      :wrapper-col="{ span: 0 }"
      :rules="rules"
    >
      <BasicTable @register="registerTable">
        <template #bodyCell="{ column, record, index }">
          <template v-if="column.dataIndex === 'filedName'">
            <a-form-item label="" :name="[index, 'filedName']" :rules="rules.filedName">
              <Input v-model:value="record.filedName" allowClear />
            </a-form-item>
          </template>
          <template v-if="column.dataIndex === 'filedType'">
            <a-form-item label="" :name="[index, 'filedType']" :rules="rules.filedType">
              <Select
                v-model:value="record.filedType"
                allowClear
                @change="(value: string[]) => (record.filedType = value)"
              >
                <SelectOption
                  v-for="(item, v) in colunmTypeList"
                  :key="`kind_${v}`"
                  :value="item.value"
                >
                  {{ item.name }}
                </SelectOption>
              </Select>
            </a-form-item>
          </template>
          <template v-if="column.dataIndex === 'kind'">
            <a-form-item label="" :name="[index, 'kind']">
              <Select
                v-model:value="record.kind"
                allowClear
                @change="(value: string) => (record.kind = value)"
              >
                <SelectOption v-for="(item, v) in kindList" :key="`kind_${v}`" :value="item.value">
                  {{ item.name }}
                </SelectOption>
              </Select>
            </a-form-item>
          </template>
          <template v-if="column.dataIndex === 'min'">
            <a-form-item label="" :name="[index, 'min']">
              <Input v-model:value="record.min" allowClear />
            </a-form-item>
          </template>
          <template v-if="column.dataIndex === 'max'">
            <a-form-item label="" :name="[index, 'max']">
              <Input v-model:value="record.max" allowClear />
            </a-form-item>
          </template>
          <template v-if="column.dataIndex === 'length'">
            <a-form-item label="" :name="[index, 'length']">
              <Input v-model:value="record.length" allowClear />
            </a-form-item>
          </template>
          <template v-if="column.dataIndex === 'start'">
            <a-form-item label="" :name="[index, 'start']">
              <Input v-model:value="record.start" allowClear />
            </a-form-item>
          </template>
          <template v-if="column.dataIndex === 'end'">
            <a-form-item label="" :name="[index, 'end']">
              <Input v-model:value="record.end" allowClear />
            </a-form-item>
          </template>
          <template v-if="column.dataIndex === 'maxPast'">
            <a-form-item label="" :name="[index, 'maxPast']">
              <Input v-model:value="record.maxPast" allowClear />
            </a-form-item>
          </template>
          <template v-if="column.dataIndex === 'action'">
            <Icon
              icon="ant-design:plus-outlined"
              :title="t('common.add')"
              v-if="hasAdd(index)"
              @click="addColumn"
            />

            <Divider type="vertical" class="action-divider" v-if="hasAdd(index)" />

            <a-popconfirm
              title="确定要删除么？"
              @confirm="removeColumn(record.filedName)"
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
    name: 'DataGenSchema',
  };
</script>
<script setup lang="ts" name="DataGenSchema">
  import { defineEmits, watch, ref } from 'vue';
  import { BasicTable, useTable } from '/@/components/Table';
  import { useI18n } from '/@/hooks/web/useI18n';
  import { Icon } from '/@/components/Icon';
  import { Select, Input, Divider, Popconfirm, Form } from 'ant-design-vue';
  import { colunmTypeList } from './draw.data';

  const SelectOption = Select.Option;
  const APopconfirm = Popconfirm;
  const AFormItem = Form.Item;

  const tableFormRef = ref();

  const emit = defineEmits(['update:value']);

  const props = defineProps({
    value: {
      type: String,
      default: '',
    },
  });

  const { t } = useI18n();

  let columnList = ref(
    JSON.parse(props.value) || [
      {
        filedName: '',
        filedType: '',
        kind: '',
      },
    ],
  );

  watch(
    () => columnList,
    (newVal, oldVal) => {
      emit('update:value', JSON.stringify(newVal.value));
    },
    { deep: true },
  );

  const kindList = [
    {
      name: '序列',
      value: 'sequence',
    },
    {
      name: '随机',
      value: 'random',
    },
  ];

  const columns = [
    {
      title: '字段名',
      dataIndex: 'filedName',
      scopedSlots: { customRender: 'filedName' },
    },
    {
      title: '字段类型',
      dataIndex: 'filedType',
      scopedSlots: { customRender: 'filedType' },
    },
    {
      title: '生成器类型',
      dataIndex: 'kind',
      scopedSlots: { customRender: 'kind' },
    },
    {
      title: '最小值',
      dataIndex: 'min',
      scopedSlots: { customRender: 'min' },
    },
    {
      title: '最大值',
      dataIndex: 'max',
      scopedSlots: { customRender: 'max' },
    },
    {
      title: '数据长度',
      dataIndex: 'length',
      scopedSlots: { customRender: 'length' },
    },
    {
      title: '起始值',
      dataIndex: 'start',
      scopedSlots: { customRender: 'start' },
    },
    {
      title: '结束值',
      dataIndex: 'end',
      scopedSlots: { customRender: 'end' },
    },
    {
      title: '最大偏移',
      dataIndex: 'maxPast',
      scopedSlots: { customRender: 'maxPast' },
    },
    {
      key: 'action',
      title: t('component.table.operation'),
      dataIndex: 'action',
      fixed: 'right',
      align: 'right',
      width: 80,
    },
  ];

  const rules = {
    blur: [{ required: true, message: '请输入', trigger: 'blur' }],
    change: [{ required: true, message: '请选择', trigger: 'change' }],
    filedName: [{ required: true, message: '请输入字段名', trigger: 'blur' }],
    filedType: [{ required: true, message: '请选择字段类型', trigger: 'change' }],
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

  function getColumn(filedName: String, filedType: String, kind: String) {
    return {
      filedName,
      filedType,
      kind,
    };
  }

  function addColumn() {
    columnList.value.push(getColumn('', 'String', 'random'));
    handleEvent();
  }

  function removeColumn(key: String) {
    columnList.value.forEach((item, index, self) => {
      if (item.filedName === key) {
        self.splice(index, 1);
      }
    });
    if (columnList.value.length === 0) {
      columnList.value.push(getColumn('', '', ''));
    }
    handleEvent();
  }

  function hasAdd(index: Number) {
    return columnList.value.length - 1 === index;
  }

  function handleEvent() {
    emit('update:value', JSON.stringify(columnList.value));
  }

  defineExpose({
    validSchema,
  });
</script>
