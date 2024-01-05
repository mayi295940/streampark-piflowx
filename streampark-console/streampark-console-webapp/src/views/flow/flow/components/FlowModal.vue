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
  <BasicModal :width="600" :show-cancel-btn="false" @register="registerModal" @ok="closeModal">
    <template #title>
      <Icon icon="ant-design:partition-outlined" />
      {{ t('system.user.userInfo') }}
    </template>
    <Description :column="1" :data="flowInfo" :schema="flowColumn" />
  </BasicModal>
</template>
<script lang="ts">
  export default defineComponent({
    name: 'FlowModal',
  });
</script>

<script setup lang="ts">
  import { defineComponent, h, ref } from 'vue';
  import { DescItem, Description } from '/@/components/Description';
  import Icon from '/@/components/Icon';
  import { useModalInner, BasicModal } from '/@/components/Modal';
  import { useI18n } from '/@/hooks/web/useI18n';

  const flowInfo = ref<Recordable>({});

  const { t } = useI18n();
  const [registerModal, { closeModal }] = useModalInner((data: Recordable) => {
    data && onReceiveModalData(data);
  });
  function onReceiveModalData(data: Recordable) {
    flowInfo.value = Object.assign({}, flowInfo.value, data);
  }
  // Dynamically generate label icons
  const generatedLabelIcon = (icon: string, label: string) => {
    return h('div', null, [
      h(Icon, { icon: `ant-design:${icon}-outlined` }),
      h('span', { class: 'px-5px' }, label),
    ]);
  };
  const flowColumn: DescItem[] = [
    { label: generatedLabelIcon('user', t('flow.flow.flow_columns.name')), field: 'name' },
    {
      label: generatedLabelIcon(`clock-circle`, t('common.createTime')),
      field: 'engineType',
    },
    {
      label: generatedLabelIcon(`clock-circle`, t('common.createTime')),
      field: 'createTime',
    },
    {
      label: generatedLabelIcon(`message`, t('common.description')),
      field: 'description',
    },
  ];
</script>
