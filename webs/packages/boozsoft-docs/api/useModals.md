# useModals 
当需要获取主数据时，调用NC弹出框控件，返回对应数据
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
  const {useNcModalData} =  useNcBusStoreWidthOut().useModals()
  const params={}
  const data = await useNcModalData(({To}) => To.SYSTEM_MESSAGE, params)
```

