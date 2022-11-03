import pinyin from "js-pinyin";

export function searchFilter(codeList,searchStr){
  return codeList.value
    .filter(item => {
      if (searchStr.value == null) {
        return true
        // throw Error('错误')
      }


      if (searchStr.value == '') {
        return true
      }

      if (searchStr.value.indexOf(' ') != -1) {
        return true
      }

      if (!isNaN(searchStr.value)) {
        // 纯数字，右模糊
        return item.key.substr(0, searchStr.value.length) == searchStr.value
      } else {
        return item.key.indexOf(searchStr.value) != -1 || item.label.indexOf(searchStr.value) != -1 || pinyin.getCamelChars(item.label).indexOf(searchStr.value.toUpperCase()) != -1
      }
    })
}

