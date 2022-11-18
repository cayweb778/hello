
# usePrint
当需要打印时，该API可以调用打印控件
- **类型：** `pinia`

```typescript
type callback = (...args: Array<any>) => any;

export declare class Pinia {

  /** await 返回弹出框数据，第一个参数为获取支持的弹出框列表，后续的参数为传入弹出框的数据 */
  async useNcModalData(fn: (event: string, params: Object<any>) => any): Pinia;
}
```
- **类型：** `( fn: callback, params: object) => EventBus`
- **实例：**
```typescript
 const {print,tableStyle}=useNcBusStoreWidthOut().usePrint()
  const data = print({data: ['l', 'px', 'a4', true]}, (doc) => {
    doc.setFont('fuhuiR')
    let bbb = []
    for (let i = 0; i < 200; i++) {
      bbb.push(['结算客户编码', '结算客户名称', '期初余额', '应收金额', '收款金额', '期末余额'])
    }
    doc.autoTable({
      head: [['客户应收余额表', '', '', '', '', ''],
        ['单位：', '', '期间：', '', '', ''],
        ['结算客户编码', '结算客户名称', '期初余额', '应收金额', '收款金额', '期末余额']
      ],
      body: bbb,
      styles: tableStyle(),
      // startY: 60,
      margin: {
        left: 30,
        top: 20,
      },

      columnStyles: {
        0: {maxHeight: 10, cellWidth: 70, halign: 'left'},
        1: {cellWidth: 130, halign: 'left'},
        2: {cellWidth: 90, halign: 'right'},
        3: {cellWidth: 90, halign: 'right'},
        4: {cellWidth: 90, halign: 'right'},
        5: {cellWidth: 90, halign: 'right'},
      }
    })
    return doc
  }
```
