import {
  createColumnDefine,
  useGetRef,
} from '/@/components/pingzheng-fillin/hooks/funs/columnModelsFuns';
import { nextTick, ref, shallowRef } from 'vue';
import zhaiYao from '/@/components/pingzheng-fillin/layouts/table/columns/zhaiYao.vue';

export function createZhaiYaoColumn(rowDefines, tableData) {
  return createColumnDefine('zhaiYao', {
    columns: [
      {
        key: 'zhaiYao',
        label: '摘要',
        width: '215',
        component: shallowRef(zhaiYao),
        register({ target, row, rowIndex }) {
          rowDefines.value[rowIndex].target.zhaiYao = target;
          target.next = () => {
            rowDefines.value[rowIndex].target.kuaiJiKeMu.focus();
          };

          const { getRef } = useGetRef(rowDefines);

          Object.assign(target, {
            up: () => rowIndex != 0 && getRef(rowIndex - 1, 'zhaiYao').focus(),
            left: () => rowIndex != 0 && getRef(rowIndex - 1, 'daiMoney').focus(),
            right: () => getRef(rowIndex, 'kuaiJiKeMu').focus(),
            down: () => {
              if (rowDefines.value[rowIndex + 1] != null) {
                if (rowDefines.value[rowIndex + 1].rowData.isEmpty) {
                  rowDefines.value[rowIndex + 1].rowData.isEmpty = false;
                  getRef(rowIndex + 1, 'zhaiYao').focus();
                  nextTick(() => {
                    rowDefines.value[rowIndex + 1].target.zhaiYao.setValue(
                      rowDefines.value[rowIndex].rowData.zhaiYao.value,
                    );
                  });
                } else {
                  getRef(rowIndex + 1, 'zhaiYao').focus();
                }
                // rowDefines.value[rowIndex + 1].target.()
              }
            },
          });
        },
      },
    ],
    defineData: () => {
      return {
        value: '',
        columnDatas: {
          zhaiYao: {
            text: '',
            readonly: false,
          },
        },
        ASASD: 'SADSA',
        isShowText222: ref(true),
        showText: ref(),
      };
    },
    resultMergeEvent(row, rowIndex) {
      const isError = row.zhaiYao.value == '';

      function getFuZhuHeSuanResultMergeEvent() {
        function getFuZhuHeSuanValue(key) {
          const list = row.fuZhuHeSuan.value.filter((item) => item.key == key);
          if (list == null || list.length == 0) {
            return null;
          } else {
            return list[0].value.value;
          }
        }

        const errors = [];
        if (row.fuZhuHeSuan.jieSuanMode == '') {
          errors.push('未填写结算方式');
        }
        if (row.fuZhuHeSuan.value.filter((item) => item == '').length > 0) {
          errors.push('辅助核算不全');
        }
        const res = {
          isError: errors.length > 0,
        };
        if (res.isError) {
          res.msg = errors.join(' ');
          return [];
        } else {
          const data = [
            // 结算方式编码
            ['pjCsettle', row.fuZhuHeSuan.jieSuanMode],
            // 票据号
            ['pjId', row.fuZhuHeSuan.piaoJuNumber],
            // 票据日期
            ['pjDate', row.fuZhuHeSuan.piaoJuDate],
            // 结算单位全称
            ['pjUnitName', row.fuZhuHeSuan.jieSuanCompany],
          ];
          data.push(
            ...[
              // 人员辅助核算
              ['cdeptId', getFuZhuHeSuanValue('fzDept')],
              // 部门辅助核算
              ['cpersonId', getFuZhuHeSuanValue('fzEmp')],
              // 客户辅助核算
              ['ccusId', getFuZhuHeSuanValue('fzCustom')],
              // 供应商辅助核算
              ['csupId', getFuZhuHeSuanValue('fzGys')],
              // 项目大类辅助核算
              ['projectClassId', getFuZhuHeSuanValue('fzPrjectClass')],
              // 项目辅助核算
              ['projectId', getFuZhuHeSuanValue('fzPrject')],
              ['cdfine1', getFuZhuHeSuanValue('beiyong1')],
              ['cdfine2', getFuZhuHeSuanValue('beiyong2')],
              ['cdfine3', getFuZhuHeSuanValue('beiyong3')],
              ['cdfine4', getFuZhuHeSuanValue('beiyong4')],
              ['cdfine5', getFuZhuHeSuanValue('beiyong5')],
              ['cdfine6', getFuZhuHeSuanValue('beiyong6')],
              ['cdfine7', getFuZhuHeSuanValue('beiyong7')],
              ['cdfine8', getFuZhuHeSuanValue('beiyong8')],
              ['cdfine9', getFuZhuHeSuanValue('beiyong9')],
              ['cdfine10', getFuZhuHeSuanValue('beiyong10')],
              ['cdfine11', getFuZhuHeSuanValue('beiyong11')],
              ['cdfine12', getFuZhuHeSuanValue('beiyong12')],
              ['cdfine13', getFuZhuHeSuanValue('beiyong13')],
              ['cdfine14', getFuZhuHeSuanValue('beiyong14')],
              ['cdfine15', getFuZhuHeSuanValue('beiyong15')],
              ['cdfine16', getFuZhuHeSuanValue('beiyong16')],
              ['cdfine17', getFuZhuHeSuanValue('beiyong17')],
              ['cdfine18', getFuZhuHeSuanValue('beiyong18')],
              ['cdfine19', getFuZhuHeSuanValue('beiyong19')],
              ['cdfine20', getFuZhuHeSuanValue('beiyong20')],
              ['cdfine21', getFuZhuHeSuanValue('beiyong21')],
              ['cdfine22', getFuZhuHeSuanValue('beiyong22')],
              ['cdfine23', getFuZhuHeSuanValue('beiyong23')],
              ['cdfine24', getFuZhuHeSuanValue('beiyong24')],
              ['cdfine25', getFuZhuHeSuanValue('beiyong25')],
              ['cdfine26', getFuZhuHeSuanValue('beiyong26')],
              ['cdfine27', getFuZhuHeSuanValue('beiyong27')],
              ['cdfine28', getFuZhuHeSuanValue('beiyong28')],
              ['cdfine29', getFuZhuHeSuanValue('beiyong29')],
              ['cdfine30', getFuZhuHeSuanValue('beiyong30')],
            ],
          );
          return data;
        }
      }

      return {
        isError,
        msg: isError ? '摘要为空' : null,
        data: [
          ...getFuZhuHeSuanResultMergeEvent(),
          ['cdigest', row.zhaiYao.value],
          ['inid', rowIndex + 1],
          ['vouchUnCode', row.zhaiYao.thisInoid],
        ],
      };
    },
  });
}
