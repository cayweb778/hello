import { findAllApi } from '/@/api/boozsoft/account/AccvoucherCdigest';

export async function getZhaiYaoList(usePageRouterApi) {
  const apiData = await usePageRouterApi(findAllApi)();
  return apiData.items.map((it) => ({
    key: it.ccode,
    value: it.content,
    label: it.content,
    target:it
  }));
}
