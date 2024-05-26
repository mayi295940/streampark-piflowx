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
    name: 'OnlineProgram',
    components: { SvgIcon },
  };
</script>

<script setup lang="ts" name="OnlineProgram">
  import { computed, reactive, ref, watchEffect } from 'vue';
  import { Tooltip } from 'ant-design-vue';
  import { FullscreenExitOutlined } from '@ant-design/icons-vue';
  import { getMonacoOptions } from '/@/views/flink/app/data';
  import { SvgIcon } from '/@/components/Icon';
  import { useMonaco } from '/@/hooks/web/useMonaco';
  import { useI18n } from '/@/hooks/web/useI18n';
  import { useFullContent } from '/@/hooks/event/useFullscreen';
  const { t } = useI18n();

  const programContent = ref();
  const vertifyRes = reactive({
    errorMsg: '',
    verified: false,
    errorStart: 0,
    errorEnd: 0,
  });

  const { toggle, fullContentClass, fullEditorClass, fullScreenStatus } = useFullContent();
  const emit = defineEmits(['update:value', 'preview']);

  const props = defineProps({
    value: {
      type: String,
      default: '',
    },
    language: {
      type: String,
      default: 'java',
    },
    versionId: {
      type: String as PropType<Nullable<string>>,
    },
    suggestions: {
      type: Array as PropType<Array<{ text: string; description: string }>>,
      default: () => [],
    },
  });

  const defaultValue = '';

  const { onChange, setContent, getInstance, getMonacoInstance, setMonacoSuggest } = useMonaco(
    programContent,
    {
      language: props.language,
      code: props.value || defaultValue,
      options: {
        minimap: { enabled: true },
        ...(getMonacoOptions(false) as any),
        autoClosingBrackets: 'never',
      },
    },
  );

  watchEffect(() => {
    if (props.suggestions.length > 0) {
      setMonacoSuggest(props.suggestions);
    }
  });
  const flinkEditorClass = computed(() => {
    return {
      ...fullEditorClass.value,
      ['syntax-' + (vertifyRes.errorMsg ? 'false' : 'true')]: true,
    };
  });

  onChange((data) => {
    emit('update:value', data);
  });

  defineExpose({ setContent });
</script>

<template>
  <div style="height: 550px" class="w-full" :class="fullContentClass">
    <div
      class="full-content-tool flex justify-between px-20px pb-10px mb-10px"
      v-if="fullScreenStatus"
    >
      <div class="flex items-center">
        <SvgIcon name="fql" />
        <div class="basic-title ml-10px">Flink Sql</div>
      </div>
      <Tooltip :title="t('component.modal.restore')" placement="bottom">
        <FullscreenExitOutlined role="full" @click="toggle" style="font-size: 18px" />
      </Tooltip>
    </div>

    <div ref="programContent" class="overflow-hidden w-full mt-5px" :class="flinkEditorClass"></div>
  </div>
</template>
