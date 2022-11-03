export function useLabelSlice(length) {
  return {
    getLabel(label) {
      return this.getRealLength(label) > length ? this.subStr(label, length)+'...' : label;
    },

    // 截取中文长度
    subStr(str, n) {
      const r = /[^\x00-\xff]/g;
      if (str.replace(r, 'mm').length <= n) {
        return str;
      }
      const m = Math.floor(n / 2);
      for (let i = m; i < str.length; i++) {
        if (str.substr(0, i).replace(r, 'mm').length >= n) {
          return str.substr(0, i);
        }
      }
      return str;
    },
    getRealLength(str) {
      const l = str.length;
      let blen = 0;
      for (let i = 0; i < l; i++) {
        if ((str.charCodeAt(i) & 0xff00) != 0) {
          blen++;
        }
        blen++;
      }
      return blen;
    },
    hasGtLength(label) {
      return this.getRealLength(label) >= length;
    },
  };
}
