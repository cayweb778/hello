import {apiDataToShowModel} from '/@/components/pingzheng-fillin/hooks/models/datas/model';
import {creatApiRow} from '/@/components/pingzheng-fillin/hooks/models/apiData';
import {delTempData, queryDefaultPingZhengDate} from '/@/api/pingzheng/pingzheng';
import {usePingZhengResult} from '/@/components/pingzheng-fillin/hooks/okFun';
import {message, Modal} from 'ant-design-vue';
import {createVNode, nextTick} from 'vue';
import {ExclamationCircleOutlined} from '@ant-design/icons-vue';
import {breakNumTidy, setAccvoucherRevision} from '/@/api/record/system/accvoucher';
import {usePingzhengReloadFun} from "/@/components/pingzheng-fillin/hooks/usePingzheng";

function toApiDataModel(apiData, params) {
  const a = apiDataToShowModel(apiData, params);
  return a;
}

function useTypePage(params, tableData, viewModel) {
  return {
    goInsert() {
      const tableData2 = toApiDataModel([creatApiRow()], {
        options: {
          originPingZheng: params.options.originPingZheng,
          optionInoId: params.options.optionInoId,
          optionDate: params.options.optionDate,
        },
        settings: {titleName: '记账凭证', typeLabel: '插入'},
      });
      tableData.reloadPingZheng(tableData2);
    },
    async goChongXiao() {
      tableData.settings.onlyShow = false;
      tableData.settings.typeLabel = params.settings.typeLabel;
      if (params.options != null) {
        tableData.options.originPingZheng = params.options.originPingZheng;
      }
      tableData.options.optionId2 = null;
      tableData.options.optionPzId = null;
      tableData.rowDefines.forEach((it) => {
        if (it.rowData.zhaiYao.value != '') {
          it.target.zhaiYao.setValue(`[冲销]${it.rowData.zhaiYao.value}`);
        }
        it.rowData.jieMoney.value = (parseFloat(it.rowData.jieMoney.value) * -1).toFixed(2);
        it.rowData.daiMoney.value = (parseFloat(it.rowData.daiMoney.value) * -1).toFixed(2);
      });

      const aa = await queryDefaultPingZhengDate();
      const defaultInoid = viewModel.value.findDefaultInoId(aa.dbillDate);
      tableData.options.optionInoId = viewModel.value.toInoIdText(defaultInoid);
    },

    async goCopy() {
      const aa = await queryDefaultPingZhengDate();
      const defaultInoid = viewModel.value.findDefaultInoId(aa.dbillDate);
      tableData.options.optionInoId = viewModel.value.toInoIdText(defaultInoid);

      tableData.settings.onlyShow = false;
      tableData.settings.typeLabel = params.settings.typeLabel;
      if (params.options != null) {
        tableData.options.originPingZheng = params.options.originPingZheng;
      }
      tableData.options.optionId2 = null;
      tableData.options.optionPzId = null;
    },
    goOther() {
      tableData.settings.onlyShow = false;
      tableData.settings.typeLabel = params.settings.typeLabel;
      if (params.options != null) {
        tableData.options.originPingZheng = params.options.originPingZheng;
      }
    },
  };
}

function confirmDelete(fun) {
  Modal.confirm({
    title: '删除凭证后不能恢复，确认删除吗？',
    icon: createVNode(ExclamationCircleOutlined),
    async onOk() {
      fun()
    },
    onCancel() {
    },
    class: 'test',
  });
}

function confirmbreakNum() {
  Modal.confirm({
    title: '是否整理凭证断号吗?',
    icon: createVNode(ExclamationCircleOutlined),
    async onOk() {
      await breakNumTidy({
        periodStart: '2021-12',
        periodEnd: '2021-12',
      });
      // 整理断号
    },
    onCancel() {
    },
    class: 'test',
  });
}


export function useButtonEvents(params, tableData, viewModel, pingzhengData) {

  function pageLengthNot1() {
    return tableData.pageStore.inoIdIn.length != 1
  }


  function pageNumberNotEquals1() {
    return tableData.pageStore.page != 1
  }


  const {goInsert, goChongXiao, goCopy, goOther} = useTypePage(params, tableData, viewModel);
  const fun = {
    openEditPage() {
      function createConfirmModify(fun) {
        Modal.confirm({
          title: () => '这张凭证正在被修改，是否强制修改？',
          icon: () => createVNode(ExclamationCircleOutlined),
          okType: 'danger',
          okText: () => '强制修改',
          cancelText: () => '放弃修改',
          // icon: () => createVNode(ExclamationCircleOutlined),
          // content: () => createVNode('div', { style: 'color:red;' }, 'Some descriptions'),
          async onOk() {

            fun()

          },
          async onCancel() {
          },
          class: 'test',
        });
      }

      function goEdit() {
        tableData.settings.onlyShow = false;
        tableData.settings.typeLabel = '修改';
      }

      // 检查修改状态
      function hasModify() {
        return tableData.options.apiData[0].revision=="1"
      }

      if (hasModify()) {
        // 单据被标记为修改状态，确认强制进入修改页
        createConfirmModify(() => {
          goEdit()
        })
      } else {
        // 单据未被标记为修改状态，直接进入修改页
        goEdit()

        // 单据设置乐观锁
        async function setRevision({uniqueCode}) {
          await setAccvoucherRevision({
            uniqueCode,
            revision: "1"
          })
        }

        setRevision({uniqueCode: tableData.options.apiData[0].uniqueCode})

      }

    },
    backEdit() {
      function toApiDataModel(apiData, params) {
        tableData.pageStore.page = tableData.pageStore.inoIdIn.length + 1;
        const a = apiDataToShowModel(apiData, params);
        return a;
      }

      const apiData = [creatApiRow()];
      apiData[0].inoId = tableData.defaultInoid;
      const tableData2 = toApiDataModel(apiData, {
        settings: {titleName: '记账凭证', typeLabel: '填制', onlyShow: false},
      });
      tableData2.options.optionDate = tableData.optionDate;
      tableData.reloadPingZheng(tableData2, {type: '新增'});
      const {onReloaded}=usePingzhengReloadFun()
      onReloaded(()=>{
        function hasTableRender() {
          return document.getElementsByClassName('zhaiYaoTextarea').length > 0
        }
        tableData.rowDefines[0].target.zhaiYao.focus()
        const interval=setInterval(()=>{
          console.log(1)
          if(hasTableRender()){
            window.clearInterval(interval)

            tableData.rowDefines[0].target.zhaiYao.focus()
          }

        },100)

      })
    },
    async openAddPage(params) {
      switch (params.settings.typeLabel) {
        case '插入':
          goInsert();
          break;
        case '冲销':
          await goChongXiao();
          break;
        case '复制':
          await goCopy();
          break;
        default:
          goOther();
      }
    },
    async deletePingzheng() {
      const pingZhengResultFun = usePingZhengResult({
        value: pingzhengData,
      });
      const {deletePingZhengByUniqueCode} = pingZhengResultFun;
      await deletePingZhengByUniqueCode(tableData.options.optionPzId);

      // 删除操作
      confirmDelete(() => goDelete())


    },
    openInsertPingZheng() {
      this.openAddPage({
        options: {
          originPingZheng: tableData.options.apiData,
          optionInoId: tableData.options.optionInoId,
          optionDate: tableData.options.optionDate,
        },
        settings: {typeLabel: '插入'},
      });
    },
  };

  function goDelete() {
    // 判断是否有人修改此凭证

    // 判断当月是否结账

    // 判断凭证是否记账

    // 判断凭证是否审核

    // 判断凭证是否主管签字

    // 判断凭证是否出纳签字

    // 判断凭证是否为当月最后一张凭证

    //
    if (pageLengthNot1()) {
      // 确认删除
      confirmbreakNum()
    } else if (pageNumberNotEquals1()) {
      // 返回上一页
      tableData.pageStore.pre();
    } else {
      // 返回编辑器
      fun.backEdit();
    }

    message.info('删除成功');
  }

  return fun
}

export function useKeyEvents(showAddLoading, showSaveLoading, emit) {
  function hideAddLoading() {
    showAddLoading.value = false;
  }

  return {
    thisOkFun() {
      showAddLoading.value = true;
      // okFun(hideAddLoading);
    },

    thisSaveFun() {
      // saveFun();
    },
    thisTempSave() {
      emit('saveTempDanJu');
    },

    saveFun() {
      showSaveLoading.value = true;
      nextTick(() => {
        // okAndShowFun(() => {});
      });
      // emit('okAndShow',hideLoading)
    },
  };
}
