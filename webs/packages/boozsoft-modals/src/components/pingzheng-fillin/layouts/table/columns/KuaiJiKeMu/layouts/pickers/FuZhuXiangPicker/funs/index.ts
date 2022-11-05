import FuZhuXiang from '../index.vue';

export function useFuZhuXiang({ change, closeFun }) {
  let params = null;
  const fun = {
    FuZhuXiang,
    register($params) {
      params = $params;
      params.closeFun.value = closeFun;
      Object.assign(fun, params);
    },
  };
  return fun;
}
