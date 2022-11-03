import {computed, nextTick, ref, unref} from 'vue';
import {apiDataToShowModel} from '/@/components/pingzheng-fillin/hooks/models/datas/model';
import {creatApiRow} from '/@/components/pingzheng-fillin/hooks/models/apiData';
import {usePingZhengFillinStore} from '/@/store/modules/boozsoft/pingzheng-fillin';
import {
  clearGridData,
  rowClear,
  supplementRow,
  replaceNewData,
} from '/@/store/modules/boozsoft/account/PingzhengEditor/funs/usePingzhengFuns';
import {defaultInoidApi} from '/@/api/pingzheng/pingzheng';
import {createPinia} from 'pinia';
import {message} from 'ant-design-vue';
import {useCompanyOperateStoreWidthOut} from '/@/store/modules/operate-company';
import {defineRecrodData} from '/@/utils/boozsoft/record/recordUtils';
import {defineColumnModels} from '/@/components/pingzheng-fillin/hooks/columnModels';

// 检查是否渲染完了凭证控件，用vue的话说就是，凭证控件onMounted了么 注：需要优化更好的方式去替换以下函数，后期可做优化
export function usePingzhengReloadFun() {
  function hasTableRender() {
    return document.getElementsByClassName('helloTextTest').length > 0
  }
  return {
    onReloaded(fun) {
      let count=1
      const interval=setInterval(()=>{
        console.log(1)
        if(hasTableRender()){
          window.clearInterval(interval)
          fun()
        }
        count++
        if(count==100){
          console.error('可能不完善，可以debugger修改')
        }
      },100)
    }
  }
}
const {onReloaded}=usePingzhengReloadFun()

function createNewPingzhengData() {
  const apiData = [];
  for (let i = 0; i < 16; i++) {
    apiData.push(creatApiRow());
  }
  return apiDataToShowModel(apiData, {settings: {titleName: '记账凭证', typeLabel: '填制'}});
}

function getFirstPingzhengData() {
  return usePingZhengFillinStore().getApiDataModels[0].apiData;
}

function creatPingzhengData(apiData, params) {
  return apiDataToShowModel(apiData, params);
}

function supplementOptionAndSettings(pingzhengData, data) {
  Object.assign(pingzhengData.value.settings, data.settings);
  Object.assign(pingzhengData.value.options, data.options);
}

function hasPingzhengData() {
  return usePingZhengFillinStore().getApiDataModels[0].title != null;
}

function getExistPingzhengData() {
  return creatPingzhengData(getFirstPingzhengData(), {
    settings: {
      titleName: '记账凭证',
      onlyShow: true,
      typeLabel: '查看',
      ...getFirstPingzhengData()[0].settings,
    },
  });
}

function isEmptyPingzhengModel(pingzhengDataRef) {
  return pingzhengDataRef.value == null;
}

function useAccountSetting() {
  const openContro = false;
  const state = ref({
    pre: {
      date: null,
      inoId: null,
      optionInoId: null,
    },
  });
  return {
    state,
    $pingZhengDate(date) {
      message.error('序时控制:凭证日期不能小于最后凭证日期');
      return !openContro;
    },

    $pingZhengInoId(date) {
      message.error('序时控制:凭证日期不能小于最后凭证日期');
      return !openContro;
    },
  };
}

function useAbc(columnModels) {
  return {
    toRowModelVerfiyData(itemxxx) {
      function a(item) {
        const resultMergeEvents = item.requireModels.map(
          (requireModelName) =>
            columnModels.filter((item3) => item3.key == requireModelName)[0].resultMergeEvent,
        );
        return resultMergeEvents;
      }

      const resultMergeEvents = a(itemxxx);

      function b(item, resultMergeEvents) {
        // 校验后的数据
        const rowModelVerfiyData = resultMergeEvents.flatMap((mergeEvent) =>
          mergeEvent(item.rowData, item.rowIndex),
        );

        // 错误处理
        const errorRow = rowModelVerfiyData
          .filter((item) => item.isError)
          .map((item2) => {
            return '行' + (item.rowIndex + 1) + '：' + item2.msg;
          });

        if (errorRow.length > 0) {
          // errorRows.push(errorRow);
        }
        return rowModelVerfiyData.flatMap((item) => item.data);
      }

      return b(itemxxx, resultMergeEvents);
    },
  };
}

function defineStore(params) {
  function createInstane(instance) {
    const newParams = {
      instance,
      ...params.state(),
      ...params.actions,
    };
    return {instance, value: newParams};
  }

  const instances = [];

  return (store) => {
    const filterInstance = instances.filter((it) => it.instance === store);
    if (filterInstance.length == 0) {
      instances.push(createInstane(store));
    }
    return instances.filter((it) => it.instance === store)[0].value;
  };
}

export const usePingZhengStore = defineStore({
  state: () => ({
    data: null,
    pingzhengData: ref(),
    pingzhengEditorViewModel: ref(),
    columnModels: ref(),
    toRowModelVerfiyData: null,
  }),
  actions: {
    reloadPingZheng(data, $params) {
      const that = this;
      nextTick(() => {
        // 补充行数据
        supplementRow(that.pingzhengData, data);
        that.pingzhengData.value.rowDefines.forEach(rowClear);

        // 替换为新数据
        replaceNewData(that.pingzhengData, data);

        // 补充options,settings
        supplementOptionAndSettings(that.pingzhengData, data);

        that.columnModels.value.resetRequireModels();
      });
      if ($params != null) {
        const {type} = $params;
        if (type == '新增') {
          this.pingzhengEditorViewModel.value.setNewPingZhengDefaultData(this.pingzhengData.value);
        }
      }
    },

    async getCodeCache() {
      const codeList = await this.pingzhengData.value.repository.kuaiJiKeMu.findModelAll({
        iyear: '2021',
        bend: '1',
      });
      return codeList;
    },
    resetPingZhengData() {
      this.reloadPingZheng(createNewPingzhengData(), null);
    },
  },
});

export function createPingzhengStore(instance,data){
    const store=usePingZhengStore(instance)
    createPinia();
    store.data = data;
    const that = store;
    // 赋值基础数据
    store.pingzhengData.value =
      store.data.pingzhengData == null ? createNewPingzhengData() : store.data.pingzhengData;


    store.pingzhengData.value.reloadPingZheng = store.reloadPingZheng.bind(store);
    store.columnModels.value = defineColumnModels(store.pingzhengData, {
      getCodeCache: store.getCodeCache.bind(store),
    });
    store.pingzhengData.value.toRowModelVerfiyData = useAbc(
      store.columnModels.value.columnModels,
    ).toRowModelVerfiyData;
    store.pingzhengEditorViewModel.value = defineRecrodData({
      state() {
        return {
          accountSetting: useAccountSetting(),
          async findDefaultInoId(date) {
            const yearMonth = date.split('-')[0] + date.split('-')[1];
            return (await defaultInoidApi({yearMonth}, null)).inoId;
          },
          toInoIdText(inoId) {
            const accountInfo = computed(
              () => useCompanyOperateStoreWidthOut().getCurrentAccountInfo,
            );
            return (parseInt(inoId) + 1)
              .toString()
              .padStart(parseInt(accountInfo.value.accvouchDec), '0');
          },
          instances: [
            {
              store: createPinia(),
              params: {
                pingzhengData: that.pingzhengData,
              },
            },
          ],
          reloadPingZheng: that.reloadPingZheng.bind(that),
          triggerDoublePingZheng() {
            if (that.pingzhengEditorViewModel.value.instances.length == 1) {
              that.pingzhengEditorViewModel.value.instances.push({
                store: createPinia(),
                params: {
                  pingzhengData: that.pingzhengData,
                },
              });
            } else {
              that.pingzhengEditorViewModel.value.instances.splice(1, 2);
            }
          },
        };
      },
      crud: {},
      columns: [],
      crudFuns: [],
      action: {},
    });
    nextTick(() => {


      onReloaded(()=>{
        function hasTableRender() {
          return document.getElementsByClassName('zhaiYaoTextarea').length > 0
        }
        store.columnModels.value.rowDefines[0].target.zhaiYao.focus()
        const interval=setInterval(()=>{
          console.log(1)
          if(hasTableRender()){
            window.clearInterval(interval)

            store.columnModels.value.rowDefines[0].target.zhaiYao.focus()
          }

        },100)

      })

    })
  return store
}

export function usePingzhengCreator() {
  return {
    hasPingzhengData,
    createNewPingzhengData,
    getExistPingzhengData,
  };
}
