<template>
  <div :style="{ height }">
    <iframe :src="src" id="bariframe" style="width: 100%; height: 100%" frameborder="0"></iframe>

    <!--The online programming-->
    <Modal
      :title="programming_Title"
      :visible="showProgrammingModal"
      centered
      :width="1000"
      @cancel="showProgrammingModal = false"
    >
      <div>
        <DataGenSchema
          ref="dataGenSchema"
          v-model:value="editorContent"
          v-if="stopLanguage === 'dataGenSchema'"
          @update-value="handleDataGenSchemaEvent"
        />
        <FlinkTableDefinition
          ref="flinkTableSchema"
          v-model:value="editorContent"
          v-else-if="stopLanguage === 'flinkTableSchema'"
          @update:update-table-definition="handleTableDefinitionEvent"
        />
        <SqlEditor
          ref="flinkSql"
          v-model:value="editorContent"
          v-else-if="stopLanguage === 'sql'"
        />
        <OnlineProgram
          ref="programModal"
          v-model:value="editorContent"
          :language="stopLanguage"
          v-else
        />
      </div>
      <template #footer>
        <a-button @click="previewCreateSql">预览</a-button>
        <a-button @click="showProgrammingModal = false">取消</a-button>
        <a-button @click="updateStopsProperty(false)">暂存</a-button>
        <a-button type="primary" @click="updateStopsProperty(true)">保存</a-button>
      </template>
    </Modal>

    <Modal
      title="预览"
      :visible="showPreviewModal"
      centered
      :width="1000"
      @ok="showPreviewModal = false"
      @cancel="showPreviewModal = false"
    >
      <SqlEditor ref="previewFlinkSql" v-model:value="previewFlinkSql" />
    </Modal>
  </div>
</template>

<script lang="ts">
  import { Modal } from 'ant-design-vue';
  import { defineComponent, inject } from 'vue';
  import OnlineProgram from './components/OnlineProgram.vue';
  import DataGenSchema from './components/DataGenSchema.vue';
  import FlinkTableDefinition from './components/FlinkTableDefinition.vue';
  import { TFlinkTableDefinition } from '/@/api/model/flinkTableDefinition';
  import SqlEditor from './components/SqlEditor.vue';
  import { updateStopsProperty, previewCreateSql } from '/@/api/flow/stop';
  import { useDrawer } from '/@/components/Drawer';
  import { useI18n } from '/@/hooks/web/useI18n';
  const { t } = useI18n();

  export default defineComponent({
    name: 'DrawingBoard',
    components: {
      Modal,
      SqlEditor,
      OnlineProgram,
      DataGenSchema,
      FlinkTableDefinition,
    },
    setup() {
      const [registerReviewDrawer, { openDrawer: openReviewDrawer }] = useDrawer();
    },
    data() {
      return {
        height: '95%',
        src: '',
        parentsId: '',
        editorContent: '',
        readonly: false,
        showProgrammingModal: false,
        stopLanguage: 'text',
        stopsId: '',
        programming_Title: 'Set Data For Each Port',
        showPreviewModal: false,
        previewFlinkSql: '',

        NeedSource_Modal: false,
        tableData: [],
        page: 1,
        limit: 10,
        total: 0,
        single: false,

        queryJson: {
          isRunFollowUp: true,
          stopsId: '',
          ports: [],
          testDataIds: [],
        },

        portsList: [],
        testDataIdList: [],
        list: [],
        tabName: '',

        showDetails: false,
        tableToolbar: {
          perfect: true,
          zoom: true,
          custom: true,
        },
        editableData: [],
        tableColumn: [],

        visualization_Modal: false,
        visualizationData: [],
        visualization_Toolbar: {
          perfect: true,
          zoom: true,
          custom: true,
          export: true,
        },
        tableExport: {
          type: 'csv',
          types: ['csv', 'html', 'xml', 'txt'],
        },
        global: {
          eventPoll: {},
        },
      };
    },
    computed: {
      columns() {
        return [
          {
            title: t('testData_columns.action'),
            slot: 'action',
            width: 100,
            align: 'center',
          },
          {
            title: t('testData_columns.name'),
            key: 'name',
            sortable: true,
          },
          {
            title: t('testData_columns.description'),
            key: 'description',
          },
          {
            title: t('testData_columns.CreateTime'),
            key: 'createTime',
            sortable: true,
          },
        ];
      },
    },
    watch: {
      $route(to, from) {
        let baseUrl = window.location.origin;
        let herf = to.fullPath.split('src=')[1];
        this.src =
          baseUrl +
          herf
            .replace(/\%2F+/g, '/')
            .replace(/\%3F+/g, '?')
            .replace(/\%3D+/g, '=')
            .replace(/\%26+/g, '&');

        this.global.eventPoll.emit('looding', true);

        // let footerName = 'flowGroupList', _this = this;
        // if ( this.src.indexOf("drawingBoardType=GROUP") !== -1 ) {
        // // && this.src.indexOf("drawingBoard/page/flowGroup")!== -1
        //   // GROUP
        //   window.addEventListener('message', function(e){
        //     if ( e.data.parentsId){
        //       if (e.data.parentsId !== 'null'){
        //          this.global.eventPoll.emit("crumb", [
        //           { name: "Group", path: "/group" },
        //           { name: footerName, path:"/flow/drawingBoard?src=" + "/drawingBoard" +"/page/flowGroup/mxGraph/index.html?drawingBoardType=GROUP&parentAccessPath=flowGroupList&load=" + e.data.parentsId},
        //           { name: "drawingBoard", path: "/drawingBoard" },
        //         ]);
        //       }else if(e.data.parentsId === 'null') {
        //          this.global.eventPoll.emit("crumb", [
        //           { name: "Group", path: "/group" },
        //           { name: "drawingBoard", path: "/drawingBoard" },
        //         ]);
        //       }
        //     }
        //   })
        // }else if ( this.src.indexOf("drawingBoardType=TASK") !== -1 ){
        //   // && this.src.indexOf("drawingBoard/page/flow/mxGraph") !== -1
        //   // TASK
        //   window.addEventListener('message', function(e){
        //     if ( e.data.parentsId){
        //       if (e.data.parentsId !== 'null'){
        //          this.global.eventPoll.emit("crumb", [
        //           { name: "Flow", path: "/flow" },
        //           { name: footerName, path:"/drawingBoard?src=" + "/drawingBoard" +"/page/flowGroup/mxGraph/index.html?drawingBoardType=GROUP&parentAccessPath=flowGroupList&load=" + e.data.parentsId},
        //           { name: "drawingBoard", path: "/drawingBoard" },
        //         ]);
        //       }else if(e.data.parentsId === 'null') {
        //          this.global.eventPoll.emit("crumb", [
        //           { name: "Flow", path: "/flow" },
        //           { name: "drawingBoard", path: "/drawingBoard" },
        //         ]);
        //       }
        //     }
        //   })
        // }

        //监听窗口变化 自适应（ 根据需求自行添加 ）
        window.addEventListener('resize', () => {
          this.setSize();
        });
      },
    },
    created() {
      this.global = inject('global');
      // let query = global.router.currentRoute.query;
      if (this.src === '') {
        let baseUrl = window.location.origin;
        let herf = window.location.href.split('src=')[1];
        this.src =
          baseUrl +
          herf
            .replace(/\%2F+/g, '/')
            .replace(/\%3F+/g, '?')
            .replace(/\%3D+/g, '=')
            .replace(/\%26+/g, '&');

        this.global.eventPoll.emit('looding', true);
      }
      //监听窗口变化 自适应（ 根据需求自行添加 ）
      window.addEventListener('resize', () => {
        this.setSize();
      });
    },
    mounted() {
      let oIframe = document.querySelector('iframe');
      let piflowBgc = document.querySelector('#piflow-bgc');
      let _this = this;
      if (piflowBgc) {
        if (oIframe.attachEvent) {
          oIframe.attachEvent('onload', () => {
            _this.global.eventPoll.emit('looding', false);
            piflowBgc.remove();
          });
        } else {
          oIframe.onload = function () {
            _this.global.eventPoll.emit('looding', false);
            piflowBgc.remove();
          };
        }
      }
      // 动态修改footer样式
      // document.querySelector('footer').style.cssText = 'position: fixed; bottom: 0;';
      // document.querySelector('header').style.cssText = 'position: fixed; width: 100%; top: 0;';
      // console.log(document.querySelector('footer'));
      this.setSize();
      window.addEventListener(
        'message',
        function (event) {
          // 通过origin属性判断消息来源地址
          // if (event.origin == 'localhost') {
          _this.global.eventPoll.emit('looding', event.data);
          // console.log(event.source);
          // }
        },
        false,
      );
      //  接收可视化编程data
      window['openRightHelpPage'] = (val, id, language, name) => {
        _this.showProgrammingModal = true;
        _this.editorContent = val;
        _this.stopsId = id;
        _this.programming_Title = name;
        switch (language) {
          case 'Text':
            _this.stopLanguage = 'text';
            break;
          case 'Scala':
            _this.stopLanguage = 'scala';
            break;
          case 'Javascript':
            _this.stopLanguage = 'javascript';
            break;
          case 'Python':
            _this.stopLanguage = 'python';
            break;
          case 'Shell':
            _this.stopLanguage = 'sh';
            break;
          case 'Sql':
            _this.stopLanguage = 'sql';
            break;
          case 'DataGenSchema':
            _this.stopLanguage = 'dataGenSchema';
            break;
          case 'FlinkTableSchema':
            _this.stopLanguage = 'flinkTableSchema';
            break;
          default:
            break;
        }
      };

      //  StopsComponent is neeed source data
      window['StopsComponentIsNeeedSourceData'] = ({ id, isRunFollowUp }) => {
        _this.NeedSource_Modal = true;
        let data = {
          stopsId: id,
        };
        let inputJson = {
          isRunFollowUp: isRunFollowUp,
          stopsId: id,
          ports: [],
          testDataIds: [],
        };
        let isNeedSource = false;

        this.$axios
          .post('/stopsManage/isNeedSource', this.$qs.stringify(data))
          .then((res) => {
            let data = res.data;
            if (data.code === 200) {
              isNeedSource = data.isNeedSource;
              inputJson.ports = data.ports;
              this.queryJson = inputJson;
              this.tabName = this.queryJson.ports[0] + 0;
              if (data.isNeedSource) {
                this.getTableData();
              } else {
                this.queryJson = inputJson;
                this.runStops(isNeedSource, this.queryJson);
              }
            }

            // this.$Modal.success({
            //   title: this.$t("tip.title"),
            //   content: `111 ` + this.$t("tip.add_success_content"),
            //   onOk:()=>{}})
          })
          .catch((error) => {
            console.log(error);
            this.global.eventPoll.emit('looding', false);
            this.$Message.error({
              content: t('tip.fault_content'),
              duration: 3,
            });
          });
      };

      //  visualization
      window['visualizationTable'] = ({ value }) => {
        _this.visualization_Modal = true;
        _this.visualizationData = value;
        _this.getTitle(_this.visualizationData);
        console.log(value, '传递的表格数据');
      };
    },
    beforeUnmount() {
      document.querySelector('header').style.cssText = '';
      document.querySelector('footer').style.cssText = '';
      this.global.eventPoll.emit('crumb', []);
      this.global.eventPoll.emit('looding', false);
    },
    methods: {
      setSize() {
        this.height = document.querySelector('body').offsetHeight - 12 + 'px';
      },
      GetChildValue(val) {
        this.parentsId = val;
      },
      handleDataGenSchemaEvent(schema: String) {
        this.editorContent = schema;
      },
      handleTableDefinitionEvent(table: TFlinkTableDefinition) {
        this.editorContent = JSON.stringify(table);
      },
      // 保存更改的flow配置信息
      async updateStopsProperty(isExit: boolean) {
        let param = {};
        param.id = this.stopsId;
        param.content = this.editorContent;
        console.log('editorContent = ', this.editorContent);
        const { data } = await updateStopsProperty(param);
        if (data.code == 200) {
          document
            .getElementById('bariframe')
            .contentWindow.document.getElementById(`${this.stopsId}`).value = data.value;
          document
            .getElementById('bariframe')
            .contentWindow.document.getElementById(`${this.stopsId}`)
            .setAttribute('data', `${data.value}`);
        }
        this.$refs?.programModal?.setContent('');
        if (isExit) {
          this.showProgrammingModal = false;
        }
      },
      getQueryString(url_string, name) {
        const url = new URL(url_string);
        return url.searchParams.get(name);
      },
      async previewCreateSql() {
        const stopPageId = localStorage.getItem('stopPageId');
        const fid = this.getQueryString(this.src, 'load');
        const { data } = await previewCreateSql(fid, stopPageId);
        if (data.code == 200) {
          this.previewFlinkSql = data.data;
          this.showPreviewModal = true;
        }
      },
      // run parameters
      runParameters() {
        // let data = this.$refs.parameters.getRadioRecord();
        this.list.forEach((item) => {
          Object.keys(item).forEach((key) => {
            switch (key) {
              case 'ports':
                this.portsList.push(item[key]);
                break;
              case 'testDataId':
                this.testDataIdList.push(item[key]);
                break;
            }
          });
        });

        this.queryJson.testDataIds = this.testDataIdList;

        this.runStops(true, this.queryJson);
      },
      // run stops
      runStops(isNeedSource, queryJson) {
        this.global.eventPoll.emit('looding', true);
        let data = {};
        if (isNeedSource) {
          data = queryJson;
        } else {
          data = queryJson;
        }
        data.ports = this.portsList;
        this.testDataIdList = [];
        this.portsList = [];

        this.$axios
          .post('/stopsManage/runStops', this.$qs.stringify(data))
          .then((res) => {
            let data = res.data;
            if (data.code === 200) {
              this.global.eventPoll.emit('looding', false);
              this.JumpToMonitor(data.processId, data.errorMsg);
            }
          })
          .catch((error) => {
            console.log(error);
            this.global.eventPoll.emit('looding', false);
          });
      },
      JumpToMonitor(id, msg) {
        // 判断页面是否加载完毕
        let pageURl = {
          pageURl: id,
          pageMsg: msg,
        };
        document.getElementById('bariframe').contentWindow.postMessage(pageURl, '*');

        // $("#mapIframe")[0].contentWindow.postMessage({
        //   module: "yxtz",
        //   param: "sl_btn_sw"
        // },"*");
      },
      showDetailEvent(row) {
        this.showDetails = true;
        let data = { testDataId: row.id };
        this.$axios
          .post('/testData/testDataSchemaValuesList', this.$qs.stringify(data))
          .then((res) => {
            let data = res.data;
            if (data.code === 200) {
              this.editableData = data.schemaValue;
              this.getTitle(data.schemaValue);
            } else {
              this.$Message.error({
                content: this.$t('tip.request_fail_content'),
                duration: 3,
              });
            }
          })
          .catch((error) => {
            console.log(error);
            this.$Message.error({
              content: this.$t('tip.fault_content'),
              duration: 3,
            });
          });
      },
      getTitle(schemaValuesList) {
        this.tableColumn = [];
        let tableTitle = Object.keys(schemaValuesList[0]);
        for (let i = 0; i < tableTitle.length; i++) {
          if (tableTitle[i] === 'dataRow') {
          } else {
            this.tableColumn.push({
              field: tableTitle[i],
              title: tableTitle[i],
            });
          }
        }
      },
      //获取表格数据
      getTableData() {
        let data = { page: this.page, limit: this.limit };
        if (this.param) {
          data.param = this.param;
        }
        this.$axios
          .post('/testData/testDataListPage', this.$qs.stringify(data))
          .then((res) => {
            if (res.data.code == 200) {
              this.tableData = res.data.data;
              this.total = res.data.count;
            } else {
              this.$Message.error({
                content: this.$t('tip.request_fail_content'),
                duration: 3,
              });
            }
          })
          .catch((error) => {
            console.log(error);
            this.$Message.error({
              content: this.$t('tip.fault_content'),
              duration: 3,
            });
          });
      },
      tabClick(name) {
        this.tabName = name;
      },
      //  选中
      selectChangeEvent({ row }) {
        let obj = {},
          selectPort = '';
        this.queryJson.ports.forEach((item, i) => {
          if (this.tabName === item + i) {
            selectPort = item;
          }
        });

        obj.ports = selectPort;
        obj.testDataId = row.id;

        if (this.list.length === 0) {
          this.list.push(obj);
        } else {
          this.list.forEach((item, index) => {
            if (item.ports === obj.ports) {
              this.list.splice(index, 1);
            }
          });
          this.list.push(obj);
        }
      },
    },
  });
</script>

<style lang="less" scoped>
  #piflow-bgc {
    position: fixed;
    top: 0;
    width: 100%;
    height: 100%;
    display: block;
    z-index: 0;
  }
  ::v-deep .ivu-tabs.ivu-tabs-card > .ivu-tabs-bar .ivu-tabs-tab-active {
    border-color: #dcdee2 !important;
  }
  ::v-deep .ivu-tabs-nav .ivu-tabs-tab:hover {
    color: #009688;
  }
  .mytable-scrollbar ::-webkit-scrollbar {
    width: 5px;
    height: 5px;
  }
  .mytable-scrollbar ::-webkit-scrollbar-track {
    background-color: #ffffff;
  }
  .mytable-scrollbar ::-webkit-scrollbar-thumb {
    background-color: #eeeeee;
    border-radius: 5px;
    border: 1px solid #f1f1f1;
    box-shadow: inset 0 0 6px rgba(66, 65, 65, 0.3);
  }
  .mytable-scrollbar ::-webkit-scrollbar-thumb:hover {
    background-color: #a8a8a8;
  }
  .mytable-scrollbar ::-webkit-scrollbar-thumb:active {
    background-color: #787878;
  }
  .mytable-scrollbar ::-webkit-scrollbar-corner {
    background-color: #ffffff;
  }
  ::v-deep .ant-col-0 {
    display: block;
  }
</style>
