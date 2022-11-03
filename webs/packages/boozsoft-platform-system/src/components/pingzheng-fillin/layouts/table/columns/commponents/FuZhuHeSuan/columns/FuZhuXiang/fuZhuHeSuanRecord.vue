<template>
  <div style="z-index: 10000000000">
    <teleport :to="pageRootDom">
      <div
        v-show="visible222"
        id="hello222222"
        ref="hello222"
        style="
          position: fixed;
          left: 0px;
          top: 0px;
          width: 100%;
          height: 100%;
          z-index: 100000000;
          background: #0000004d;
        "
      ></div>
    </teleport>

    <div>
      <Drawer
        v-model:visible="visible222"
        :closable="false"
        :style="{ position: 'absolute' }"
        placement="right"
        :get-container="hello222"
        title="科目辅助档案管理"
        width="500px"
      >
        <Drawer
          v-model:visible="visible22212321"
          :closable="false"
          :style="{ position: 'absolute' }"
          placement="right"
          title="会计科目"
          width="180px"
        >
          <div
            style="
              padding: 20px 20px 10px 20px;
              border-bottom: 1px solid #e5e5e5;
              width: 95%;
              margin-left: 12px;
            "
          >
            <a-button type="text">上张</a-button>
            <a-button type="text">下张</a-button>
            <a-button type="text">保存</a-button>
            <a-button type="text">保存新增</a-button>
            <a-button type="text">复制</a-button>
            <a-button type="text">设置</a-button>
            <a-button type="text">退出</a-button>
          </div>
          <div class="left">
            <span>科目编码:<Input v-model:value="value" placeholder="1001" /></span>
            <span>科目名称:<Input v-model:value="value" placeholder="库存现金" /></span>
            <span>助记码:<Input v-model:value="value" placeholder="KCXJ" /></span>
            <span>
              科目类型:
              <Select
                v-model:value="value"
                mode="tags"
                style="width: 80px; text-align: center"
                placeholder="资产"
                :options="options"
                @change="handleChange"
              />
            </span>
            <span
              >余额方向:
              <Select
                v-model:value="value"
                mode="tags"
                style="width: 80px; text-align: center"
                placeholder="借方"
                :options="options"
                @change="handleChange"
            /></span>
            辅助核算
          </div>
        </Drawer>
        <div>
          <div style="margin-bottom: 15px">
            当前科目：1003 存放中央银行款项
            <Button type="primary" style="" @click="openEditPage()"> 属性调整 </Button>
          </div>
          <Tabs v-model:activeKey="abcasdsa" tab-position="left">
            <template v-for="item in helloList" :key="item.key">
              <TabPane :tab="item.label">
                <div style="margin-bottom: 10px">
                  <div style="color: green; margin-bottom: 10px">已选择：1 销售部</div>
                  <Input style="width: 100px" />
                  <Button style="margin-left: 10px">检索</Button>
                  <Button style="margin-left: 2px">
                    <SettingFilled />
                  </Button>
                  <br />
                </div>
                <table style="width: 100%">
                  <tbody>
                    <tr>
                      <td>选择</td>
                      <td>代号</td>
                      <td>名称</td>
                      <td>操作</td>
                    </tr>
                    <tr v-for="item2 in item.list">
                      <!--                  <td>{{ item2.key }}</td>-->
                      <td>
                        <Radio value="1" />
                      </td>
                      <td>{{ item2.code }}</td>
                      <td>{{ item2.label }}</td>
                      <td>
                        <Button>调整</Button>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </TabPane>
            </template>
            <template v-for="it in otherList" :key="it.id">
              <TabPane :tab="it.cname">
                <table style="width: 100%">
                  <tbody>
                    <tr>
                      <td>key{{ it.key }}</td>
                      <td>代号</td>
                      <td>名称</td>
                    </tr>
                    <tr v-for="it1 in it.list">
                      <td>{{ it1.key }}</td>
                      <td>{{ it1.code }}</td>
                      <td>{{ it1.label }}</td>
                    </tr>
                  </tbody>
                </table>
              </TabPane>
            </template>
          </Tabs>
        </div>
      </Drawer>
    </div>
  </div>
</template>
<script setup>
  import { inject, onMounted, ref } from 'vue';
  import { findKeyLabelAll, findKeyLabelOtherAll } from '/@/api/record/system/fuZhuHeSuan';

  const abcasdsa = ref('fzDept');
  const visible222 = ref(false);
  const visible22212321 = ref(false);
  const helloList = ref([]);
  import { Input, Select, Radio, Tabs, Button, Drawer } from 'ant-design-vue';
  import { SettingFilled } from '@ant-design/icons-vue';

  const TabPane = Tabs.TabPane;
  const otherList = ref([]);
  const hello222 = ref();
  const pageRootDom = inject('pageRootDom').value;
  const options = [...Array(25)].map((_, i) => ({ value: (i + 10).toString(36) + (i + 1) }));
  const value = ref([]);

  async function hello123() {
    const apiResult = await findKeyLabelAll(
      {
        require: '1,2,3,6,fzDept,fzEmp,fzCustom,fzGys,fzItem,fzItemClass',
        toTarget: 'true',
      },
      null,
    );
    otherList.value = (await findKeyLabelOtherAll()).map((it) => {
      const aa = apiResult.filter((item) => item.key == it.cdfine);
      if (aa.length > 0) {
        it.list = aa[0].list;
      } else {
        it.list = [];
      }
      return it;
    });
    const aaa = [
      {
        key: 'fzDept',
        label: '部门',
        list: apiResult.filter((item) => item.key == 'fzDept')[0].list,
      },
      {
        key: 'fzEmp',
        label: '员工',
        list: apiResult.filter((item) => item.key == 'fzEmp')[0].list,
      },
      {
        key: 'fzGys',
        label: '供应商',
        list: apiResult.filter((item) => item.key == 'fzGys')[0].list,
      },
      {
        key: 'fzCustom',
        label: '客户',
        list: apiResult.filter((item) => item.key == 'fzCustom')[0].list,
      },
      {
        key: 'fzItem',
        label: '项目',
        list: apiResult.filter((item) => item.key == 'fzItem')[0].list,
      },
      {
        key: 'fzItemClass',
        label: '项目（大类）',
        list: apiResult.filter((item) => item.key == 'fzItemClass')[0].list,
      },

      // fzDeptList.value = apiResult.filter(item => item.key == 'fzDept')[0].list
      // fzEmpList.value = apiResult.filter(item => item.key == 'fzEmp')[0].list
      // fzItemClassList.value = apiResult.filter(item => item.key == 'fzItemClass')[0].list
      // fzGysList.value = apiResult.filter(item => item.key == 'fzGys')[0].list
      // fzCustomList.value = apiResult.filter(item => item.key == 'fzCustom')[0].list
      // list2.value = apiResult.filter(item => item.key == '2')[0].list
      // list3.value = apiResult.filter(item => item.key == '3')[0].list
      // list1.value = apiResult.filter(item => item.key == '1')[0].list
      // list6.value = apiResult.filter(item => item.key == '6')[0].list
      // fzItemList.value = apiResult.filter(item => item.key == 'fzItem')[0].list
    ];
    helloList.value = [...aaa];
    return apiResult;
  }

  onMounted(() => {
    visible222.value = true;
    console.log(22);
    hello123();
  });
  const handleChange = (value) => {
    console.log(`selected ${value}`);
  };
</script>
