export function useJieSuanFangShi(key) {
  return {
    isEmptyJieSuanFangShi() {
      const arr = jieSuanFangshiList.value.filter((item) => item.key == key);
      return arr.length == 0;
    },
    findJieSuanFangShi() {
      const arr = jieSuanFangshiList.value.filter((item) => item.key == key);
      if (arr.length > 0) {
        return arr[0];
      }
      return arr;
    },
  };
}

export function useJieSuanFangShiList(tableData, jieSuanFangshiList) {
  function hasJieSuanFangShiListEmpty() {
    return tableData.value.jieSuanFangshiList == null;
  }
  return {
    async resetJieSuanFangShi() {
      if (hasJieSuanFangShiListEmpty()) {
        jieSuanFangshiList.value =
          await tableData.value.repository.fuZhuHeSuan.findJieSuanFangShiAll();
        tableData.value.jieSuanFangshiList = jieSuanFangshiList.value;
      } else {
        jieSuanFangshiList.value = tableData.value.jieSuanFangshiList;
      }
    },
  };
}
