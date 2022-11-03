import { addInfo } from '/@/utils/boozsoft/boozsoftDebuggerInfo/pingzhengFillin';
import { useCompanyOperateStoreWidthOut } from '/@/store/modules/operate-company';

export function createKuaJiKeMuDebuggerInfo(props, pingzhengGridParams) {
  // const info = {
  //   tableData,
  //   codeList,
  //   value,
  //   props,
  // };

  addInfo(pingzhengGridParams.columnDefine.rowIndex, {
    getCurrentTenant() {
      return useCompanyOperateStoreWidthOut().getCurrentAccountInfo;
    },
    pingzhengGridParams,
    getFuZhuXiang() {
      const fuZhuXiang = [
        { name: '是否个人核算（1是，0否）', value: props.modelValue.info.bperson == 1 },
        { name: '是否客户核算（1是，0否）', value: props.modelValue.info.bcus == 1 },
        { name: '是否供应商核算（1是，0否）', value: props.modelValue.info.bsup == 1 },
        { name: '是否部门核算（1是，0否）', value: props.modelValue.info.bdept == 1 },
        { name: '是否项目核算（1是，0否）', value: props.modelValue.info.bitem == 1 },
        { name: '是否数量核算（1是，0否）：需要默认值', value: props.modelValue.info.bnum == 1 },
      ];
    },
  });

  // console.log(pingzhengGridParams)
  // console.log(pingzhengGridParams.columnDefine.rowIndex)
  // const fuZhuXiang=[
  //   {name:'是否个人核算（1是，0否）',value:props.modelValue.info.bperson==1},
  //   {name:'是否客户核算（1是，0否）',value:props.modelValue.info.bcus==1},
  //   {name:'是否供应商核算（1是，0否）',value:props.modelValue.info.bsup==1},
  //   {name:'是否部门核算（1是，0否）',value:props.modelValue.info.bdept==1},
  //   {name:'是否项目核算（1是，0否）',value:props.modelValue.info.bitem==1},
  //   {name:'是否数量核算（1是，0否）：需要默认值',value:props.modelValue.info.bnum==1},
  // ]
  // console.log('会计科目debugger', {
  //   fuZhuXiang,
  //   info
  // })
}
