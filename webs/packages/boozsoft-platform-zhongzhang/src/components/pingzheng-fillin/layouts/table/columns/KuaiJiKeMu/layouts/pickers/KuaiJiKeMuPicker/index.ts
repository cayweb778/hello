import KuaiJiKeMuPicker from './layouts/index.vue';

export function useKuaiJiKeMuPicker({ change }) {
  let params = null;
  const fun = {
    KuaiJiKeMuPicker,
    register($params) {
      params = $params;
      params.changeFun.value = change;
      Object.assign(fun, params);
    },
  };
  return fun;
}
