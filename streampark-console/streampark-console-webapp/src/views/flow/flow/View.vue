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
    <BasicTable @register="registerTable">
      <template #toolbar>
        <a-button type="primary" @click="handleCreate" v-auth="'flow:add'">
          <Icon icon="ant-design:plus-outlined" />
          {{ t('common.add') }}
        </a-button>
      </template>
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'action'">
          <TableAction :actions="getFlowAction(record)" />
        </template>
      </template>
    </BasicTable>
    <FlowDrawer @register="registerDrawer" @success="handleSuccess" />
    <FlowModal @register="registerModal" />
  </div>
</template>
<script lang="ts">
  import { computed, defineComponent } from 'vue';

  import { BasicTable, useTable, TableAction, ActionItem } from '/@/components/Table';
  import FlowDrawer from './components/FlowDrawer.vue';
  import FlowModal from './components/FlowModal.vue';
  import { useDrawer } from '/@/components/Drawer';
  import { fetchFlowDelete, fetchFlowList } from '/@/api/flow/flow';
  import { columns, searchFormSchema } from './flow.data';
  import { FormTypeEnum } from '/@/enums/formEnum';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { useUserStoreWithOut } from '/@/store/modules/user';
  import { useModal } from '/@/components/Modal';
  import { UserListRecord } from '/@/api/system/model/userModel';
  import { useI18n } from '/@/hooks/web/useI18n';
  import Icon from '/@/components/Icon';

  export default defineComponent({
    name: 'User',
    components: { BasicTable, FlowModal, FlowDrawer, TableAction, Icon },
    setup() {
      const { t } = useI18n();
      const userStore = useUserStoreWithOut();
      const userName = computed(() => {
        return userStore.getUserInfo?.username;
      });
      const [registerDrawer, { openDrawer }] = useDrawer();
      const [registerModal, { openModal }] = useModal();
      const { createMessage } = useMessage();

      const [registerTable, { reload }] = useTable({
        title: t('flow.flow.sidebar.flow'),
        api: fetchFlowList,
        columns,
        formConfig: {
          labelWidth: 120,
          baseColProps: { style: { paddingRight: '30px' } },
          schemas: searchFormSchema,
          fieldMapToTime: [['createTime', ['createTimeFrom', 'createTimeTo'], 'YYYY-MM-DD']],
        },
        rowKey: 'userId',
        pagination: true,
        striped: false,
        useSearchForm: true,
        showTableSetting: true,
        bordered: false,
        showIndexColumn: false,
        canResize: false,
        actionColumn: {
          width: 150,
          title: t('component.table.operation'),
          dataIndex: 'action',
        },
      });
      function getFlowAction(record: UserListRecord): ActionItem[] {
        return [
          {
            icon: 'clarity:note-edit-line',
            tooltip: t('system.user.table.modify'),
            auth: 'user:update',
            ifShow: () => record.username !== 'admin' || userName.value === 'admin',
            onClick: handleEdit.bind(null, record),
          },
          {
            icon: 'carbon:data-view-alt',
            tooltip: t('common.detail'),
            onClick: handleView.bind(null, record),
          },
        ];
      }
      // user create
      function handleCreate() {
        openDrawer(true, { formType: FormTypeEnum.Create });
      }
      // edit user
      function handleEdit(record: UserListRecord) {
        openDrawer(true, {
          record,
          formType: FormTypeEnum.Edit,
        });
      }

      // see detail
      function handleView(record: UserListRecord) {
        openModal(true, record);
      }

      // add/edit user success
      function handleSuccess() {
        createMessage.success('success');
        reload();
      }

      return {
        t,
        userName,
        registerTable,
        registerDrawer,
        registerModal,
        handleCreate,
        handleEdit,
        handleSuccess,
        handleView,
        getFlowAction,
      };
    },
  });
</script>
