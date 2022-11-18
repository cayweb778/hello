- **打印控件示例：**

```typescript
const {print, tableStyle} = useNcBusStoreWidthOut().usePrint()
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

- **主数据弹窗示例：**

```typescript
const {useNcModalData, To} = useNcBusStoreWidthOut().useModals()
const params = {}
// To:   DEPT, PSN, CUSTOM, GYS, Project, SYSTEM_MESSAGE
const result = await useNcModalData(To.SYSTEM_MESSAGE, params)
console.log(result)
```

- **excel控件示例：**

```typescript

const {excel} = useNcBusStoreWidthOut().useExcel()
excel((apiData) => {
    const {
        XLSX,
        defaultV,
        sheet_from_array_of_arrays,
        Workbook,
        writeExcel
    } = apiData
}) 
```

