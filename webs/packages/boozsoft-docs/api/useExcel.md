# useExcel
当需要excel时，该API可以调用Excel控件
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
  let aaa =  useNcBusStoreWidthOut().useExcel().excel(()=>{

  })
```
