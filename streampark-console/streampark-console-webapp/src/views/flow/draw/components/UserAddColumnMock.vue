<template>
  <a-table rowKey="key" :data-source="list" :columns="columns" :pagination="false" bordered>
    <template slot="name" slot-scope="text, record">
      <a-input v-model.trim="record.name" placeholder="请输入" @change="handleEvent" />
    </template>
    <template slot="type" slot-scope="text, record">
      <b-select :data-source="typeList" v-model="record.type" @change="handleEvent" />
    </template>
    <template slot="mockType" slot-scope="text, record">
      <b-select :data-source="mockTypeList" v-model="record.mockType" @change="handleEvent" />
    </template>
    <template slot="action" slot-scope="text, record, index">
      <b-icon v-if="hasAdd(index)" type="plus" title="添加" @click="add" />
      <b-icon type="delete" title="删除" @click="del(record.key)" />
    </template>
  </a-table>
</template>

<script>
  function getInstance(name, type, mockType) {
    return {
      key: Math.random().toString().slice(2),
      name,
      type,
      mockType,
    };
  }
  export default {
    name: 'UserAddColumnMock',
    props: {
      value: [String, Array],
      options: {
        type: Array,
        default: () => [],
      },
    },
    data() {
      return {
        list: [getInstance()],
        typeList: ['String', 'Integer', 'Double', 'Float', 'Long', 'Boolean', 'Date'],
        columns: [
          {
            title: '列名',
            dataIndex: 'name',
            scopedSlots: { customRender: 'name' },
            width: '25%',
          },
          {
            title: '数据类型',
            dataIndex: 'type',
            scopedSlots: { customRender: 'type' },
            width: '25%',
          },
          {
            title: '类型',
            dataIndex: 'mockType',
            scopedSlots: { customRender: 'mockType' },
            width: '25%',
          },

          {
            dataIndex: 'action',
            scopedSlots: { customRender: 'action' },
            width: '25%',
          },
        ],
      };
    },
    computed: {
      mockTypeList() {
        return this.options.map((item) => {
          const [label, value] = item.split(':');
          return { label, value };
        });
      },
    },
    created() {
      if (this.value) {
        try {
          const arr = JSON.parse(this.value);
          this.list = arr.map(({ name, type, mockType }) => {
            return getInstance(name, type, mockType);
          });
        } catch (error) {}
      }
    },
    methods: {
      add() {
        this.list.push(getInstance());
        this.handleEvent();
      },
      del(key) {
        this.list.forEach((item, index, self) => {
          if (item.key === key) {
            self.splice(index, 1);
          }
        });
        if (this.list.length === 0) {
          this.list.push(getInstance());
        }
        this.handleEvent();
      },
      hasAdd(index) {
        return this.list.length - 1 === index;
      },
      handleEvent() {
        const value = this.list.map(({ name, type, mockType }) => {
          return { name, type, mockType };
        });
        const output = this.list.map(({ name, type }) => {
          return { name, columnType: type };
        });
        this.$emit('change', () => ({
          value: JSON.stringify(value),
          output,
        }));
      },
    },
  };
</script>
