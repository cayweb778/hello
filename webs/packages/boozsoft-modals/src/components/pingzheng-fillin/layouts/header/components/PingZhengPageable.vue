<template>
  <!--      最上页 上页 下页 最下页-->
  <div
    class="pingZhengPageable"
    style="position: fixed; z-index: 1111112; left: 55px; top: -7px"
    v-if="pageStore != null"
  >
    <div
      @mouseenter="pageVisible = true"
      @mouseleave="pageVisible = false"
      style="display: flex; flex-direction: row; align-items: center; margin-top: 20px"
    >
      <VerticalRightOutlined
        :style="{ fontSize: '30px', color: pageStore.page === 1 ? '#cccccc' : '#333333' }"
        @click="pageStore.page === 1 ? '' : pageStore.first()"
      />
      <LeftOutlined
        :style="{ fontSize: '30px', color: pageStore.page === 1 ? '#cccccc' : '#333333' }"
        @click="pageStore.page === 1 ? '' : pageStore.pre()"
      />
      <RightOutlined
        :style="{
          fontSize: '30px',
          color: pageStore.page >= pageStore.total ? '#cccccc' : '#333333',
        }"
        @click="pageStore.page >= pageStore.total ? '' : pageStore.next()"
      />
      <VerticalLeftOutlined
        :style="{
          fontSize: '30px',
          color: pageStore.page >= pageStore.total ? '#cccccc' : '#333333',
        }"
        @click="pageStore.page >= pageStore.total ? '' : pageStore.last()"
      />
    </div>
    <Popover placement="bottomLeft" v-model:visible="pageVisible2">
      <template #content>
        <div style="display: flex; flex-direction: row; align-items: center; margin-top: 20px">
          <span style="margin-right: 10px"
            >{{ pingzhengDate.split('-')[1] }}月总 {{ pageStore.total }} 张，正在<span
              style="color: blue"
              v-text="pageStore.page > pageStore.total ? '[填制]' : '[查看]'"
            ></span
            ><span
              >第{{ pageStore.page }}张单据[正常<Checkbox :checked="true" />作废<Checkbox
                :checked="true"
              />,标错<Checkbox :checked="true" />,暂存<Checkbox :checked="true" />]</span
            >
          </span>
        </div>
      </template>
    </Popover>
  </div>
</template>
<script setup>
  import { oneMonth, queryByYearMonthInoIdApi2 } from '/@/api/pingzheng/pingzheng';
  import { computed, inject, provide, ref, watch } from 'vue';
  import { Popover, Checkbox } from 'ant-design-vue';
  import {
    LeftOutlined,
    RightOutlined,
    VerticalLeftOutlined,
    VerticalRightOutlined,
  } from '@ant-design/icons-vue';
  import { useCompanyOperateStoreWidthOut } from '/@/store/modules/operate-company';
  import { apiDataToShowModelAsync } from '/@/components/pingzheng-fillin/hooks/models/datas/model';
  import { toAccountNumLength } from '/@/components/pingzheng-fillin/utils/accountUtils';
  import { getFuZhuXiangModelValues } from '/@/components/pingzheng-fillin/layouts/header/components/funs/toFuZhuXiangModel';

  const currentLocation = ref();

  const pageVisible = ref(false);
  const pageVisible2 = ref(false);
  const viewModel = inject('viewModel');
  const reloadPingZheng = viewModel.value.reloadPingZheng;
  const tableData = computed(() => viewModel.value.instances[0].params.pingzhengData);

  const accountInfo = computed(() => useCompanyOperateStoreWidthOut().getCurrentAccountInfo);
  const pingzhengDate = computed(() => tableData.value.options.optionDate);

  const yearMonth = computed(
    () => pingzhengDate.value.split('-')[0] + pingzhengDate.value.split('-')[1],
  );

  // 翻页相关逻辑
  const pageStore = ref();

  function toAccountInoidNumLengths(apiData) {
    apiData.forEach((it) => {
      it.inoId = toAccountNumLength(it.inoId, accountInfo.value.accvouchDec);
    });
  }

  function getPingzhengParams(apiData) {
    if (apiData[0].ifrag == 1) {
      return {
        settings: {
          titleName: '记账凭证',
          onlyShow: true,
          typeLabel: '作废',
        },
      };
    } else if (apiData[0].ifrag == 2) {
      return {
        settings: { titleName: '记账凭证', typeLabel: '暂存' },
      };
    } else if (apiData[0].ifrag == 3) {
      return {
        settings: {
          titleName: '记账凭证',
          onlyShow: true,
          typeLabel: '标错',
        },
      };
    } else {
      return {
        settings: {
          titleName: '记账凭证',
          onlyShow: true,
          typeLabel: '查看',
        },
      };
    }
  }

  watch(
    yearMonth,
    async () => {
      const yearMonth = pingzhengDate.value.split('-')[0] + pingzhengDate.value.split('-')[1];

      async function userPageStore(yearMonth) {
        const object = {
          state: {
            // 年月
            yearMonth,
            // 当前页
            page: 1,
            // 总数
            total: 10,
            // 当前月ino集合
            inoIdIn: [],
          },
          actions: {
            async setQueryAllInInoId() {
              this.inoIdIn = (await oneMonth({ yearMonth: this.yearMonth }, null))
                .map((item) => item.inoId)
                .map((item) => parseInt(item))
                .sort((a, b) => a - b);
            },
            async queryByYearMonthInoIdApi({ yearMonth, inoId }) {
              return await queryByYearMonthInoIdApi2({
                yearMonth,
                inoId,
              });
            },
            async goShowData(inoId) {
              const apiData = await this.queryByYearMonthInoIdApi({
                yearMonth: this.yearMonth,
                inoId,
              });

              const fuZhuXiangModelValues = await getFuZhuXiangModelValues(
                tableData.value.repository.kuaiJiKeMu.findModelAll,
                apiData,
              );
              toAccountInoidNumLengths(apiData);

              const apiDataModel = await apiDataToShowModelAsync(apiData, {
                ...getPingzhengParams(apiData),
                options: { apiData },
                fuZhuXiangModelValues,
              });
              reloadPingZheng(apiDataModel);
            },
            async first() {
              this.page = 1;
              return this.goShowData(this.inoIdIn[0]);
            },
            async reload() {
              await this.setup();
            },
            async pre() {
              if (this.page == 1) {
                alert('错误');
              }
              if (this.page > this.total) {
                this.page--;

                return this.goShowData(this.inoIdIn[this.inoIdIn.length - 1]);
              }

              const aaa = this.goShowData(this.inoIdIn[this.page - 1 - 1]);
              this.page--;
              return aaa;
            },
            async next() {
              if (this.page == this.inoIdIn.length) {
                alert('错误');
              }

              const aaa = this.goShowData(this.inoIdIn[this.page + 1 - 1]);
              this.page++;
              return aaa;
            },
            async last() {
              this.page = this.inoIdIn.length;
              return this.goShowData(this.inoIdIn[this.inoIdIn.length - 1]);
            },
          },
          async setup() {
            await this.setQueryAllInInoId();
            this.total = this.inoIdIn.length;
            // console.log(this.total)
            if (tableData.value.settings.typeLabel == '填制') {
              this.page = this.total + 1;
            } else {
              this.page = this.inoIdIn.indexOf(parseInt(tableData.value.options.optionInoId)) + 1;
            }
            return this;
          },
        };
        const newObject = { ...object.state, ...object.actions, setup: object.setup };
        await newObject.setup.bind(newObject)();
        return newObject;
      }

      async function pageToAdd() {
        const yearMonth = pingzhengDate.value.split('-')[0] + pingzhengDate.value.split('-')[1];
        pageStore.value = userPageStore(yearMonth);

        // setTimeout(() => {
        //   console.log('所有inoId', allInoId)
        //   console.log('当前inoId', tableData.value.options.optionInoId)
        //   console.log('当前位置', currentLocation.value)
        //   console.log('总数', total.value)
        //
        // })
      }

      async function pageToShow() {
        const oneMonths = await oneMonth({
          yearMonth: pingzhengDate.value.split('-')[0] + pingzhengDate.value.split('-')[1],
        });
        const allInoId = oneMonths.map((item) => parseInt(item.inoId)).sort((a, b) => a - b);

        // total.value = allInoId.length
        currentLocation.value = total.value;
        setTimeout(() => {
          console.log('所有inoId', allInoId);
          console.log('当前inoId', tableData.value.options.optionInoId);
          console.log('当前位置', currentLocation.value);
          console.log('总数', total.value);
        });
      }

      pageStore.value = await userPageStore(yearMonth);
    },
    { immediate: true },
  );
  const registerPageData = inject('registerPageData');
  registerPageData(pageStore);
  tableData.value.pageStore = pageStore;
</script>
