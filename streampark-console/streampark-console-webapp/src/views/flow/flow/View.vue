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
  import { computed, defineComponent, inject } from 'vue';

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
  import { FlowListRecord } from '/@/api/flow/flow/flowModel';
  import { useI18n } from '/@/hooks/web/useI18n';
  import Icon from '/@/components/Icon';
  import { useRouter } from 'vue-router';

  export default defineComponent({
    name: 'Flow',
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
      const router = useRouter();

      const global: any = inject('global');

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
        rowKey: 'id',
        pagination: true,
        striped: false,
        useSearchForm: true,
        showTableSetting: true,
        bordered: false,
        showIndexColumn: false,
        canResize: false,
        actionColumn: {
          width: 250,
          title: t('component.table.operation'),
          dataIndex: 'action',
        },
      });
      function getFlowAction(record: FlowListRecord): ActionItem[] {
        return [
          {
            icon: 'clarity:note-edit-line',
            auth: 'flow:update',
            tooltip: t('flow.flow.enter'),
            onClick: handleButtonSelect.bind(null, 1, record),
          },
          {
            icon: 'clarity:note-edit-line',
            tooltip: t('system.user.table.modify'),
            auth: 'flow:update',
            onClick: handleEdit.bind(null, record),
          },
          {
            icon: 'carbon:data-view-alt',
            tooltip: t('common.detail'),
            onClick: handleView.bind(null, record),
          },
          {
            icon: 'ant-design:delete-outlined',
            color: 'error',
            tooltip: t('system.member.deleteMember'),
            auth: 'flow:delete',
            popConfirm: {
              title: t('system.member.deletePopConfirm'),
              confirm: handleDelete.bind(null, record),
            },
          },
        ];
      }
      // flow create
      function handleCreate() {
        openDrawer(true, { formType: FormTypeEnum.Create });
      }
      // edit flow
      function handleEdit(record: FlowListRecord) {
        openDrawer(true, {
          record,
          formType: FormTypeEnum.Edit,
        });
      }

      // see detail
      function handleView(record: FlowListRecord) {
        openModal(true, record);
      }

      async function handleDelete(record: FlowListRecord) {
        const { data } = await fetchFlowDelete({ id: record.id });
        if (data.status === 'success') {
          createMessage.success(t('system.member.deleteMember') + t('system.member.success'));
          reload();
        } else {
          createMessage.error(t('system.member.deleteMember') + t('system.member.fail'));
        }
      }

      function handleButtonSelect(key: Number, record: Recordable) {
        switch (key) {
          case 1:
            global.eventPoll.emit('crumb', [
              { name: 'Flow', path: '/flow' },
              { name: 'drawingBoard', path: '/drawingBoard' },
            ]);
            router.push({
              path: '/flow/drawingBoard',
              query: {
                src: '/drawingBoard/page/flow/mxGraph/index.html?load=' + record.id,
              },
            });
            break;
          case 2:
            this.getRowData(record);
            break;
          case 3:
            this.handleRun(record);
            break;
          case 4:
            this.handleDubug(record);
            break;
          case 5:
            this.handleDeleteRow(record);
            break;
          case 6:
            this.row = record;
            this.isTemplateOpen = true;
            break;

          default:
            break;
        }
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
        handleButtonSelect,
      };
    },
  });
</script>
