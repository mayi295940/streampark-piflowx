/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import { FormSchema } from '/@/components/Table';
import { computed, ref, unref, h, Ref, onMounted, reactive } from 'vue';
import { executionModes, k8sRestExposedType, resolveOrder } from '../data';
import optionData from '../data/option';
import {
  getAlertSvgIcon,
  renderDynamicProperties,
  renderInputDropdown,
  renderInputGroup,
  renderIsSetConfig,
  renderOptionsItems,
  renderStreamParkResource,
  renderTotalMemory,
  renderYarnQueue,
} from './useFlinkRender';

import { fetchCheckName } from '/@/api/flink/app';
import { RuleObject } from 'ant-design-vue/lib/form';
import { StoreValue } from 'ant-design-vue/lib/form/interface';
import { useDrawer } from '/@/components/Drawer';
import { Alert } from 'ant-design-vue';
import Icon from '/@/components/Icon';
import { useMessage } from '/@/hooks/web/useMessage';
import { fetchVariableAll } from '/@/api/resource/variable';
import {
  fetchFlinkBaseImages,
  fetchK8sNamespaces,
  fetchSessionClusterIds,
} from '/@/api/flink/flinkHistory';
import { fetchSelect } from '/@/api/resource/project';
import { fetchAlertSetting } from '/@/api/setting/alert';
import { fetchFlinkCluster } from '/@/api/flink/flinkCluster';
import { fetchFlinkEnv, fetchListFlinkEnv } from '/@/api/flink/flinkEnv';
import { FlinkEnv } from '/@/api/flink/flinkEnv.type';
import { AlertSetting } from '/@/api/setting/types/alert.type';
import { FlinkCluster } from '/@/api/flink/flinkCluster.type';
import { AppTypeEnum, ClusterStateEnum, ExecModeEnum, JobTypeEnum } from '/@/enums/flinkEnum';
import { isK8sExecMode } from '../utils';
import { useI18n } from '/@/hooks/web/useI18n';
import { fetchCheckHadoop } from '/@/api/setting';
import { fetchTeamResource } from '/@/api/resource/upload';
const { t } = useI18n();

export interface HistoryRecord {
  k8sNamespace: Array<string>;
  k8sSessionClusterId: Array<string>;
  flinkImage: Array<string>;
}
export const useCreateAndEditSchema = (
  dependencyRef: Ref | null,
  edit?: { appId: string; mode: 'streampark' | 'flink' },
) => {
  const flinkEnvs = ref<FlinkEnv[]>([]);
  const alerts = ref<AlertSetting[]>([]);
  const flinkClusters = ref<FlinkCluster[]>([]);
  const projectList = ref<Array<any>>([]);
  const teamResource = ref<Array<any>>([]);
  const historyRecord = reactive<HistoryRecord>({
    k8sNamespace: [],
    k8sSessionClusterId: [],
    flinkImage: [],
  });

  const { createErrorModal } = useMessage();
  let scalaVersion = '';
  const suggestions = ref<Array<{ text: string; description: string; value: string }>>([]);

  const [registerConfDrawer, { openDrawer: openConfDrawer }] = useDrawer();

  /*
  !The original item is also unassigned
  */
  function getConfigSchemas() {
    return [];
  }

  /* filter cluster */
  const getExecutionCluster = (
    executionMode: number,
    valueKey: string,
  ): Array<{ label: string; value: string }> => {
    return (unref(flinkClusters) || [])
      .filter((o) => {
        // Edit mode has one more filter condition
        if (edit?.mode) {
          return o.executionMode == executionMode && o.clusterState === ClusterStateEnum.RUNNING;
        } else {
          return o.executionMode == executionMode;
        }
      })
      .map((i) => ({ label: i.clusterName, value: i[valueKey] }));
  };

  const getFlinkSqlSchema = computed((): FormSchema[] => {
    return [
      {
        field: 'flinkSql',
        label: 'Flink SQL',
        component: 'Input',
        slot: 'flinkSql',
        ifShow: ({ values }) => values?.jobType == JobTypeEnum.SQL && values?.stepCurrent == 1,
        rules: [{ required: true, message: t('flink.app.addAppTips.flinkSqlIsRequiredMessage') }],
      },
      {
        field: 'teamResource',
        label: t('flink.app.resource'),
        component: 'Select',
        render: ({ model }) => renderStreamParkResource({ model, resources: unref(teamResource) }),
        ifShow: ({ values }) => values.jobType == JobTypeEnum.SQL && values?.stepCurrent == 1,
      },
      {
        field: 'dependency',
        label: t('flink.app.dependency'),
        component: 'Input',
        slot: 'dependency',
        ifShow: ({ values }) => values.jobType != JobTypeEnum.JAR && values?.stepCurrent == 1,
      },
      { field: 'configOverride', label: '', component: 'Input', show: false },
      {
        field: 'isSetConfig',
        label: t('flink.app.appConf'),
        component: 'Switch',
        ifShow: ({ values }) =>
          values?.jobType == JobTypeEnum.SQL &&
          !isK8sExecMode(values.executionMode) &&
          values?.stepCurrent == 1,
        render({ model, field }) {
          return renderIsSetConfig(model, field, registerConfDrawer, openConfDrawer);
        },
      },
    ];
  });

  async function handleFlinkVersion(id: number | string) {
    if (!dependencyRef) return;
    scalaVersion = (await fetchFlinkEnv(id as string))?.scalaVersion;
    checkPomScalaVersion();
  }

  function checkPomScalaVersion() {
    const pom = unref(dependencyRef)?.dependencyRecords;
    if (pom && pom.length > 0) {
      const invalidArtifact: Array<any> = [];
      pom.forEach((v: Recordable) => {
        const artifactId = v.artifactId;
        if (/flink-(.*)_(.*)/.test(artifactId)) {
          const depScalaVersion = artifactId.substring(artifactId.lastIndexOf('_') + 1);
          if (scalaVersion !== depScalaVersion) {
            invalidArtifact.push(artifactId);
          }
        }
      });
      if (invalidArtifact.length > 0) {
        alertInvalidDependency(scalaVersion, invalidArtifact);
      }
    }
  }

  function alertInvalidDependency(scalaVersion: string, invalidArtifact: string[]) {
    let depCode = '';
    invalidArtifact.forEach((dep) => {
      depCode += `<div style="font-size: 1rem;line-height: 1rem;padding-bottom: 0.3rem">${dep}</div>`;
    });
    createErrorModal({
      title: 'Dependencies invalid',
      width: 500,
      content: `
      <div class="text-left;">
       <div style="padding:0.5em;font-size: 1rem">
       current flink scala version: <strong>${scalaVersion}</strong>,some dependencies scala version is invalid,dependencies list:
       </div>
       <div style="color: red;font-size: 1em;padding:0.5em;">
         ${depCode}
       </div>
      </div>`,
    });
  }
  const getFlinkClusterSchemas = computed((): FormSchema[] => {
    return [
      {
        field: 'versionId',
        label: t('flink.app.flinkVersion'),
        component: 'Select',
        componentProps: {
          placeholder: t('flink.app.flinkVersion'),
          options: unref(flinkEnvs),
          fieldNames: { label: 'flinkName', value: 'id', options: 'options' },
          onChange: (value) => handleFlinkVersion(value),
        },
        rules: [
          { required: true, message: t('flink.app.addAppTips.flinkVersionIsRequiredMessage') },
        ],
        show: ({ values }) => values?.stepCurrent == 0,
      },
      {
        field: 'flinkClusterId',
        label: t('flink.app.flinkCluster'),
        component: 'Select',
        componentProps: {
          placeholder: t('flink.app.flinkCluster'),
          options: getExecutionCluster(ExecModeEnum.REMOTE, 'id'),
        },
        ifShow: ({ values }) =>
          values.executionMode == ExecModeEnum.REMOTE && values?.stepCurrent == 0,
        rules: [
          { required: true, message: t('flink.app.addAppTips.flinkClusterIsRequiredMessage') },
        ],
      },
      {
        field: 'yarnSessionClusterId',
        label: t('flink.app.flinkCluster'),
        component: 'Select',
        componentProps: {
          placeholder: t('flink.app.flinkCluster'),
          options: getExecutionCluster(ExecModeEnum.YARN_SESSION, 'id'),
        },
        ifShow: ({ values }) =>
          values.executionMode == ExecModeEnum.YARN_SESSION && values?.stepCurrent == 0,
        rules: [
          { required: true, message: t('flink.app.addAppTips.flinkClusterIsRequiredMessage') },
        ],
      },
      {
        field: 'k8sNamespace',
        label: t('flink.app.kubernetesNamespace'),
        component: 'Input',
        ifShow: ({ values }) => isK8sExecMode(values.executionMode) && values?.stepCurrent == 0,
        render: ({ model, field }) =>
          renderInputDropdown(model, field, {
            placeholder: t('flink.app.addAppTips.kubernetesNamespacePlaceholder'),
            options: unref(historyRecord)?.k8sNamespace || [],
          }),
      },
      {
        field: 'clusterId',
        label: t('flink.app.kubernetesClusterId'),
        component: 'Input',
        componentProps: ({ formModel }) => {
          return {
            placeholder: t('flink.app.addAppTips.kubernetesClusterIdRequire'),
            onChange: (e: ChangeEvent) => (formModel.jobName = e.target.value),
          };
        },
        ifShow: ({ values }) =>
          values.executionMode == ExecModeEnum.KUBERNETES_APPLICATION && values?.stepCurrent == 0,
        rules: [
          {
            required: true,
            message: t('flink.app.addAppTips.kubernetesClusterIdRequire'),
            pattern: /^(?=.{1,45}$)[a-z]([-a-z0-9]*[a-z0-9])$/,
          },
        ],
      },
      {
        field: 'flinkClusterId',
        label: t('flink.app.kubernetesClusterId'),
        component: 'Select',
        ifShow: ({ values }) =>
          values.executionMode == ExecModeEnum.KUBERNETES_SESSION && values?.stepCurrent == 0,
        componentProps: {
          placeholder: t('flink.app.addAppTips.kubernetesClusterIdPlaceholder'),
          options: getExecutionCluster(ExecModeEnum.KUBERNETES_SESSION, 'id'),
        },
        rules: [
          {
            required: true,
            message: t('flink.app.addAppTips.kubernetesClusterIdIsRequiredMessage'),
          },
        ],
      },
      {
        field: 'flinkImage',
        label: t('flink.app.flinkBaseDockerImage'),
        component: 'Input',
        ifShow: ({ values }) =>
          values.executionMode == ExecModeEnum.KUBERNETES_APPLICATION && values?.stepCurrent == 0,
        render: ({ model, field }) =>
          renderInputDropdown(model, field, {
            placeholder: t('flink.app.addAppTips.flinkImagePlaceholder'),
            options: unref(historyRecord)?.k8sSessionClusterId || [],
          }),
        rules: [{ required: true, message: t('flink.app.addAppTips.flinkImageIsRequiredMessage') }],
      },
      {
        field: 'k8sRestExposedType',
        label: t('flink.app.restServiceExposedType'),
        ifShow: ({ values }) =>
          values.executionMode == ExecModeEnum.KUBERNETES_APPLICATION && values?.stepCurrent == 0,
        component: 'Select',
        componentProps: {
          placeholder: t('flink.app.addAppTips.k8sRestExposedTypePlaceholder'),
          options: k8sRestExposedType,
        },
      },
    ];
  });

  /* Detect job name field */
  async function getJobNameCheck(_rule: RuleObject, value: StoreValue) {
    if (value === null || value === undefined || value === '') {
      return Promise.reject(t('flink.app.addAppTips.appNameIsRequiredMessage'));
    } else {
      const params = { jobName: value };
      if (edit?.appId) Object.assign(params, { id: edit.appId });
      const res = await fetchCheckName(params);
      switch (parseInt(res)) {
        case 0:
          return Promise.resolve();
        case 1:
          return Promise.reject(t('flink.app.addAppTips.appNameNotUniqueMessage'));
        case 2:
          return Promise.reject(t('flink.app.addAppTips.appNameExistsInYarnMessage'));
        case 3:
          return Promise.reject(t('flink.app.addAppTips.appNameExistsInK8sMessage'));
        default:
          return Promise.reject(t('flink.app.addAppTips.appNameNotValid'));
      }
    }
  }

  const getFlinkFormOtherSchemas = computed((): FormSchema[] => {
    const commonInputNum = {
      min: 0,
      step: 1,
      class: '!w-full',
    };
    return [
      {
        field: 'jobName',
        label: t('flink.app.appName'),
        component: 'Input',
        componentProps: { placeholder: t('flink.app.addAppTips.appNamePlaceholder') },
        dynamicRules: () => {
          return [{ required: true, trigger: 'blur', validator: getJobNameCheck }];
        },
        ifShow: ({ values }) => values?.stepCurrent == 2,
      },
      {
        field: 'tags',
        label: t('flink.app.tags'),
        component: 'Input',
        componentProps: {
          placeholder: t('flink.app.addAppTips.tagsPlaceholder'),
        },
        ifShow: ({ values }) => values?.stepCurrent == 2,
      },
      {
        field: 'resolveOrder',
        label: t('flink.app.resolveOrder'),
        component: 'Select',
        componentProps: { placeholder: 'classloader.resolve-order', options: resolveOrder },
        rules: [{ required: true, message: 'Resolve Order is required', type: 'number' }],
        ifShow: ({ values }) => values?.stepCurrent == 2,
      },
      {
        field: 'parallelism',
        label: t('flink.app.parallelism'),
        component: 'InputNumber',
        componentProps: {
          placeholder: t('flink.app.addAppTips.parallelismPlaceholder'),
          ...commonInputNum,
        },
        ifShow: ({ values }) => values?.stepCurrent == 2,
      },
      {
        field: 'slot',
        label: t('flink.app.dashboard.taskSlots'),
        component: 'InputNumber',
        componentProps: {
          placeholder: t('flink.app.addAppTips.slotsOfPerTaskManagerPlaceholder'),
          ...commonInputNum,
        },
        ifShow: ({ values }) => values?.stepCurrent == 2,
      },
      {
        field: 'restartSize',
        label: t('flink.app.restartSize'),
        ifShow: ({ values }) =>
          edit?.mode == 'flink'
            ? true
            : !isK8sExecMode(values.executionMode) && values?.stepCurrent == 2,
        component: 'InputNumber',
        componentProps: {
          placeholder: t('flink.app.addAppTips.restartSizePlaceholder'),
          ...commonInputNum,
        },
      },
      {
        field: 'alertId',
        label: t('flink.app.faultAlertTemplate'),
        component: 'Select',
        componentProps: {
          placeholder: t('flink.app.addAppTips.alertTemplatePlaceholder'),
          options: unref(alerts),
          fieldNames: { label: 'alertName', value: 'id', options: 'options' },
        },
        ifShow: ({ values }) => values?.stepCurrent == 2,
      },
      {
        field: 'checkPointFailure',
        label: t('flink.app.checkPointFailureOptions'),
        component: 'InputNumber',
        renderColContent: renderInputGroup,
        show: ({ values }) =>
          (edit?.mode == 'flink' ? true : !isK8sExecMode(values.executionMode)) &&
          values?.stepCurrent == 2,
      },
      ...getConfigSchemas(),
      {
        field: 'totalOptions',
        label: t('flink.app.totalMemoryOptions'),
        component: 'Select',
        render: renderTotalMemory,
        ifShow: ({ values }) => values?.stepCurrent == 2,
      },
      {
        field: 'totalItem',
        label: 'totalItem',
        component: 'Select',
        renderColContent: ({ model, field }) =>
          renderOptionsItems(model, 'totalOptions', field, '.memory', true),
        ifShow: ({ values }) => values?.stepCurrent == 2,
      },
      {
        field: 'jmOptions',
        label: t('flink.app.jmMemoryOptions'),
        component: 'Select',
        componentProps: {
          showSearch: true,
          allowClear: true,
          mode: 'multiple',
          maxTagCount: 2,
          placeholder: t('flink.app.addAppTips.totalMemoryOptionsPlaceholder'),
          fieldNames: { label: 'name', value: 'key', options: 'options' },
          options: optionData.filter((x) => x.group === 'jobmanager-memory'),
        },
        ifShow: ({ values }) => values?.stepCurrent == 2,
      },
      {
        field: 'jmOptionsItem',
        label: 'jmOptionsItem',
        component: 'Select',
        renderColContent: ({ model, field }) =>
          renderOptionsItems(model, 'jmOptions', field, 'jobmanager.memory.'),
        ifShow: ({ values }) => values?.stepCurrent == 2,
      },
      {
        field: 'tmOptions',
        label: t('flink.app.tmMemoryOptions'),
        component: 'Select',
        componentProps: {
          showSearch: true,
          allowClear: true,
          mode: 'multiple',
          maxTagCount: 2,
          placeholder: t('flink.app.addAppTips.tmPlaceholder'),
          fieldNames: { label: 'name', value: 'key', options: 'options' },
          options: optionData.filter((x) => x.group === 'taskmanager-memory'),
        },
        ifShow: ({ values }) => values?.stepCurrent == 2,
      },
      {
        field: 'tmOptionsItem',
        label: 'tmOptionsItem',
        component: 'Select',
        renderColContent: ({ model, field }) =>
          renderOptionsItems(model, 'tmOptions', field, 'taskmanager.memory.'),
        ifShow: ({ values }) => values?.stepCurrent == 2,
      },
      {
        field: 'yarnQueue',
        label: t('flink.app.yarnQueue'),
        component: 'Input',
        ifShow: ({ values }) =>
          (values.executionMode == ExecModeEnum.YARN_APPLICATION ||
            values.executionMode == ExecModeEnum.YARN_PER_JOB) &&
          values?.stepCurrent == 2,
        render: (renderCallbackParams) => renderYarnQueue(renderCallbackParams),
      },
      {
        field: 'podTemplate',
        label: t('flink.app.podTemplate'),
        component: 'Input',
        slot: 'podTemplate',
        ifShow: ({ values }) =>
          values.executionMode == ExecModeEnum.KUBERNETES_APPLICATION && values?.stepCurrent == 2,
      },
      {
        field: 'dynamicProperties',
        label: t('flink.app.dynamicProperties'),
        component: 'Input',
        render: (renderCallbackParams) => renderDynamicProperties(renderCallbackParams),
        ifShow: ({ values }) => values?.stepCurrent == 2,
      },
      {
        field: 'args',
        label: t('flink.app.programArgs'),
        component: 'InputTextArea',
        defaultValue: '',
        slot: 'args',
        ifShow: ({ values }) =>
          (edit?.mode ? true : values.jobType != JobTypeEnum.SQL) && values?.stepCurrent == 2,
      },
      {
        field: 'hadoopUser',
        label: t('flink.app.hadoopUser'),
        component: 'Input',
        ifShow: ({ values }) => values?.stepCurrent == 2,
      },
      {
        field: 'description',
        label: t('common.description'),
        component: 'InputTextArea',
        componentProps: { rows: 4, placeholder: t('flink.app.addAppTips.descriptionPlaceholder') },
        ifShow: ({ values }) => values?.stepCurrent == 2,
      },
    ];
  });

  const getFlinkTypeSchema = computed((): FormSchema[] => {
    return [
      {
        field: 'jobType',
        label: t('flink.app.developmentMode'),
        component: 'Input',
        render: ({ model }) => {
          if (model.jobType == JobTypeEnum.JAR) {
            return h(
              Alert,
              { type: 'info' },
              {
                message: () => [
                  h(Icon, {
                    icon: 'ant-design:code-outlined',
                    style: { color: '#108ee9' },
                  }),
                  h('span', { class: 'pl-8px' }, 'Custom Code'),
                ],
              },
            );
          } else if (model.jobType == JobTypeEnum.SQL) {
            return getAlertSvgIcon('fql', 'Flink SQL');
          } else if (model.jobType == JobTypeEnum.PYFLINK) {
            return getAlertSvgIcon('py', 'Py Flink');
          } else if (model.jobType == JobTypeEnum.PIPELINE) {
            return getAlertSvgIcon('py', 'Flink Pipeline');
          }
          return '';
        },
      },
      {
        field: 'appType',
        label: t('flink.app.appType'),
        component: 'Input',
        render: ({ model }) => {
          if (model.appType == AppTypeEnum.APACHE_FLINK) {
            return getAlertSvgIcon('flink', 'Apache Flink');
          } else if (model.appType == AppTypeEnum.STREAMPARK_FLINK) {
            return getAlertSvgIcon('flink', 'StreamPark Flink');
          } else if (model.appType == AppTypeEnum.APACHE_SPARK) {
            return getAlertSvgIcon('spark', 'Apache Spark');
          } else if (model.appType == AppTypeEnum.STREAMPARK_SPARK) {
            return getAlertSvgIcon('spark', 'StreamPark Spark');
          }
          return '';
        },
      },
    ];
  });
  const getExecutionModeSchema = computed((): FormSchema[] => {
    return [
      {
        field: 'executionMode',
        label: t('flink.app.executionMode'),
        component: 'Select',
        itemProps: {
          autoLink: false, //Resolve multiple trigger validators with null value ·
        },
        componentProps: {
          placeholder: t('flink.app.addAppTips.executionModePlaceholder'),
          options: executionModes,
        },
        rules: [
          {
            required: true,
            validator: async (_rule, value) => {
              if (value === null || value === undefined || value === '') {
                return Promise.reject(t('flink.app.addAppTips.executionModeIsRequiredMessage'));
              } else {
                if (
                  [
                    ExecModeEnum.YARN_PER_JOB,
                    ExecModeEnum.YARN_SESSION,
                    ExecModeEnum.YARN_APPLICATION,
                  ].includes(value)
                ) {
                  const res = await fetchCheckHadoop();
                  if (res) {
                    return Promise.resolve();
                  } else {
                    return Promise.reject(t('flink.app.addAppTips.hadoopEnvInitMessage'));
                  }
                }
                return Promise.resolve();
              }
            },
          },
        ],
        show: ({ values }) => values?.stepCurrent == 0,
      },
    ];
  });
  onMounted(async () => {
    /* Get project data */
    fetchSelect({}).then((res) => {
      projectList.value = res;
    });

    /* Get alert data */
    fetchAlertSetting().then((res) => {
      alerts.value = res;
    });

    //get flinkEnv
    fetchListFlinkEnv().then((res) => {
      flinkEnvs.value = res;
    });
    //get flinkCluster
    fetchFlinkCluster().then((res) => {
      flinkClusters.value = res;
    });
    fetchK8sNamespaces().then((res) => {
      historyRecord.k8sNamespace = res;
    });
    fetchSessionClusterIds({ executionMode: ExecModeEnum.KUBERNETES_SESSION }).then((res) => {
      historyRecord.k8sSessionClusterId = res;
    });
    fetchFlinkBaseImages().then((res) => {
      historyRecord.flinkImage = res;
    });

    fetchVariableAll().then((res) => {
      suggestions.value = res.map((v) => {
        return {
          text: v.variableCode,
          description: v.description,
          value: v.variableValue,
        };
      });
    });

    /* Get team dependencies */
    fetchTeamResource({}).then((res) => {
      teamResource.value = res;
    });
  });
  return {
    projectList,
    alerts,
    flinkEnvs,
    flinkClusters,
    historyRecord,
    suggestions,
    teamResource,
    getFlinkSqlSchema,
    getFlinkClusterSchemas,
    getFlinkFormOtherSchemas,
    getFlinkTypeSchema,
    getExecutionModeSchema,
    openConfDrawer,
  };
};
