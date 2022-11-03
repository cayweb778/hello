import { findKeyLabelAll } from '/@/api/record/system/fuZhuHeSuan';
import { computed, ref } from 'vue';

export function defineFuZhuXiangColumnModels(requireFuZhuColumns, props) {
  const row = [
    {
      target: [],
    },
  ];
  const aa = {
    row,
    models: [
      {
        key: 'jieSuanFangShi',
        label: '结算方式',
        enable: false,
        columns: [
          {
            key: 'jieSuanFangShi',
            style: { width: '110px' },
            label: '结算方式',
            enable: true,
            state: [],
            left() {
              row[0].target[0].focus();
            },

            next() {
              row[0].target[1].focus();
            },
          },
          {
            key: 'piaoJuHao',
            label: '票据号',
            style: { width: '70px' },
            enable: true,
            state: [],
            left() {
              row[0].target[0].focus();
            },
            next() {
              row[0].target[2].focus();
            },
          },
          {
            key: 'piaoJuDate',
            label: '票据日期',
            style: { width: '130px' },
            enable: true,
            state: {},
            left() {
              row[0].target[1].focus();
            },
            next() {
              setTimeout(() => {
                row[0].target[3].focus();
              }, 100);
            },
          },
          {
            key: 'jieSuanDanWei',
            label: '结算单位',
            style: { width: '130px' },
            enable: true,
            state: {},
            list: async function () {
              const apiResult = await findKeyLabelAll(
                {
                  require: ['fzCustom', 'fzGys'].join(','),
                  toTarget: 'true',
                },
                null,
              );
              return apiResult;
            },
            left() {
              row[0].target[2].focus();
            },
            next() {
              setTimeout(() => {
                row[0].target[4].focus();
              }, 100);
            },
          },
        ],
      },
      {
        key: 'fuZhuHeSuan',
        label: '辅助核算',
        enable: true,
        columns: requireFuZhuColumns.value.map((item, fuzhuIndex) => {
          function isLast() {
            return requireFuZhuColumns.value.length - 1 == fuzhuIndex;
          }

          item.enable = true;
          item.style = 'width:100px';

          Object.assign(item, {
            left: (i) => {
              const length = row[0].target.length - requireFuZhuColumns.value.length;
              row[0].target[length + fuzhuIndex - 1].focus();
            },
            right: (i) => {
              if (isLast()) {
                // closeRef.value.$el.setAttribute('autofocus', 'autofocus');
                // closeRef.value.$el.focus();
              } else {
                const length = row[0].target.length - requireFuZhuColumns.value.length;
                const nextTarget = row[0].target[length + fuzhuIndex + 1];
                setTimeout(() => {
                  nextTarget.focus();
                }, 10);
              }
            },
          });

          item.next = item.right;

          item.domain = 'fuZhuHeSuan';
          return item;
        }),
      },
    ],
  };
  if (props.modelValue.jieSuanMode !== null) {
    aa.models[0].enable = true;
  }
  return ref(aa);
}
function isNotRequireJieSuanDanWei(requireFuZhuColumns, columnModels) {
  if (isNotEmptyRequireFuZhuColumns(requireFuZhuColumns)) {
    const fuZhuHeSuanKeys = getFuZhuHeSuanKeys(columnModels);
    return fuZhuHeSuanKeys.indexOf('fzEmp') || fuZhuHeSuanKeys.indexOf('fzGys');
  }
}
function disableJieSuanDanWeiColumn(columnModels) {
  columnModels.value.models[0].columns[3].enable = false;
}
function getFuZhuHeSuanKeys(columnModels) {
  return columnModels.value.models[1].columns.map((item) => item.key);
}
function isNotEmptyRequireFuZhuColumns(requireFuZhuColumns) {
  return requireFuZhuColumns.value.length != 0;
}
export function defineFuZhuXiangModel(requireFuZhuColumns, props) {
  const columnModels = defineFuZhuXiangColumnModels(requireFuZhuColumns, props);

  isNotRequireJieSuanDanWei(requireFuZhuColumns, columnModels) &&
    disableJieSuanDanWeiColumn(columnModels);

  return ref({
    columns: columnModels.value.models
      .filter((it) => it.enable)
      .flatMap((it) => it.columns)
      .filter((it) => it.enable),
    row: columnModels.value.row,
    columnModels,
  });
}

export function useFuZhuXiangText(props, jieSuanFangshiList, columnModelsComputeds) {
  function findJieSuanFangShi(key) {
    return jieSuanFangshiList.value.filter((item) => item.key == key)[0];
  }
  function useFuZhuText(modelValue) {
    let jieSuanFangShiText = '';
    let fuZhuHeSuanText = '';
    const fun = {
      hasNecessaryJieSuanFangShi() {
        return !this.hasJieSuanModeEmpty() && isEnableJieSuanFangShi();
      },
      hasJieSuanModeEmpty() {
        return modelValue.jieSuanMode === undefined;
      },
      getJieSuanFangShiText() {
        return `${this.getJieSuanMode()} ${this.getPiaoJuNumber()} ${this.getPiaoJuDate()} ${this.getJieSuanCompany()} `;
      },
      getJieSuanMode() {
        let jieSuanMode = '';
        if (props.modelValue.jieSuanMode !== '') {
          jieSuanMode = findJieSuanFangShi(props.modelValue.jieSuanMode).label;
        }
        return jieSuanMode == null ? '' : jieSuanMode;
      },
      getPiaoJuNumber() {
        const piaoJuNumber = modelValue.piaoJuNumber;
        return piaoJuNumber == null ? '' : piaoJuNumber;
      },
      getPiaoJuDate() {
        const piaoJuDate = modelValue.piaoJuDate;
        return piaoJuDate == null ? '' : piaoJuDate;
      },
      getJieSuanCompany() {
        const jieSuanCompany = modelValue.jieSuanCompany;
        return jieSuanCompany == null ? '' : jieSuanCompany;
      },
      setJieSuanFangShiText(text) {
        jieSuanFangShiText = text;
      },
      setFuZhuHeSuanText(text) {
        fuZhuHeSuanText = text;
      },
      getAllText() {
        return jieSuanFangShiText + fuZhuHeSuanText;
      },
    };
    fun.setJieSuanFangShiText(fun.hasNecessaryJieSuanFangShi() ? fun.getJieSuanFangShiText() : '');

    return fun;
  }
  function isEnableJieSuanFangShi() {
    return columnModelsComputeds.jieSuanFangShi.value.enable;
  }
  function getLabel(list, value) {
    const filterList = list.filter((listItem) => listItem.key == value);
    if (filterList.length == 0) {
      return '';
    } else {
      return filterList[0].label;
    }
  }
  function getFuZhuHeSuanLabels(columnModelsComputeds) {
    return columnModelsComputeds.fuZhuXiang.value.columns.map((it) =>
      getLabel(it.list, it.from.value.value),
    );
  }
  return {
    showText: computed(() => {
      const { setFuZhuHeSuanText, getAllText } = useFuZhuText(props.modelValue);

      setFuZhuHeSuanText(getFuZhuHeSuanLabels(columnModelsComputeds).join(' '));

      return getAllText();
    }),

    showHtml: computed(() => {
      let text = '';
      if (props.modelValue.jieSuanMode !== undefined) {
        let jieSuanMode = '';
        if (columnModelsComputeds.fuZhuXiang.value.enable) {
          if (props.modelValue.jieSuanMode !== '') {
            jieSuanMode = jieSuanFangshiList.value.filter(
              (item) => item.key == props.modelValue.jieSuanMode,
            )[0].label;
          }
          jieSuanMode = jieSuanMode == null ? '' : jieSuanMode;
          let piaoJuNumber = props.modelValue.piaoJuNumber;
          let piaoJuDate = props.modelValue.piaoJuDate;
          let jieSuanCompany = props.modelValue.jieSuanCompany;
          piaoJuNumber = piaoJuNumber == null ? '' : piaoJuNumber;
          piaoJuDate = piaoJuDate == null ? '' : piaoJuDate;
          jieSuanCompany = jieSuanCompany == null ? '' : jieSuanCompany;
          text = `${jieSuanMode} ${piaoJuNumber} ${piaoJuDate} ${jieSuanCompany} `;
        }
      }

      text += columnModelsComputeds.fuZhuXiang.value.columns
        .map((fuZhuHeSuanitem) => {
          const aaa = fuZhuHeSuanitem.list.filter(
            (listItem) => listItem.key == fuZhuHeSuanitem.from.value.value,
          );
          if (aaa.length == 0) {
            return '';
          } else {
            return aaa[0].label;
          }
        })
        .join('<br/>');
      return text;
    }),
  };
}
