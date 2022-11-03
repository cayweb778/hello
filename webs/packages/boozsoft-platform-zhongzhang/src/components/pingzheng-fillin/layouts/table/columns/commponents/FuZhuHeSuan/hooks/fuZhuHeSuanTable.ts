import { findKeyLabelAll } from '/@/api/record/system/fuZhuHeSuan';

const defineFuZhuHeSuan = {
  default: async (apiResult) => {
    return apiResult.map((item) => {
      return {
        key: item.key,
        label: item.label,
        value: '1',
        api: async function () {
          return item.list;
        },
      };
    });
  },
  getCustomModel: async (requireKeys, apiResult) => {
    function getLabel(key) {
      const abc = apiResult.filter((item) => item.key == key.replace('beiyong', ''));
      return abc[0].label;
    }

    const arr = apiResult.map((item) => {
      return {
        key: 'beiyong' + item.key,
        value: '1',
        api: () => item.list,
      };
    });

    const dd = await Promise.all(
      arr
        .filter((item) => requireKeys.indexOf(item.key) != -1)
        .map(async (item) => {
          item.label = await getLabel(item.key);
          return item;
        }),
    );
    return dd;
  },
  jieSuanFangshi: () => [
    {
      key: 'jieSuanMode',
      label: '结算方式',
      value: '1',
      api: async function () {
        return [
          {
            key: '1',
            label: '技术部',
          },
        ];
      },
    },
  ],
};

function getRequireKeys(fuZhuHeSuanData) {
  return fuZhuHeSuanData.map((it) => {
    if (it.indexOf('beiyong') != -1) {
      return it.replace('beiyong', '');
    }
    return it;
  });
}

function isEmptyFuZhuHeSuanData(fuZhuHeSuanData) {
  return Object.keys(fuZhuHeSuanData).length == 0;
}

async function useModels(fuZhuHeSuanData, isProjectCtr) {
  // const projectCtr = isProjectCtr();
  const apiResults = await findKeyLabelAll(
    {
      require: getRequireKeys(fuZhuHeSuanData.map((it) => it.key)).join(','),
      toTarget: 'true',
    },
    null,
  );

  return {
    async getCustomModels(key) {
      const customApiResults = apiResults.filter((item) => item.key.toString().indexOf('fz') == -1);
      const customFuZhuHeSuanData = fuZhuHeSuanData.filter((item) => item.custom);
      const models = await defineFuZhuHeSuan.getCustomModel(
        customFuZhuHeSuanData.map((item) => item.key),
        customApiResults,
      );

      return models.filter((it) => it.key == key)[0];
    },
    async getDefineFuZhuHeSuans(key) {
      const defaultApiResults = apiResults.filter(
        (item) => item.key.toString().indexOf('fz') != -1,
      );
      const models = await defineFuZhuHeSuan.default(defaultApiResults);

      return models.filter((it) => it.key == key)[0];
    },
  };
}
async function createModel(model, item) {
  model.list = await model.api();
  model.from = item;
  return model;
}
function isCustom(item) {
  return item.custom !== undefined;
}
export async function getFuZhuHeSuanDefines(fuZhuHeSuanData, isProjectCtr) {
  console.log(fuZhuHeSuanData);
  if (isEmptyFuZhuHeSuanData(fuZhuHeSuanData)) {
    return [];
  }

  const { getCustomModels, getDefineFuZhuHeSuans } = await useModels(fuZhuHeSuanData, isProjectCtr);

  const asyncList = fuZhuHeSuanData.map(async (it) =>
    createModel(
      isCustom(it) ? await getCustomModels(it.key) : await getDefineFuZhuHeSuans(it.key),
      it,
    ),
  );

  const list = await Promise.all(asyncList);
  return list;
}
