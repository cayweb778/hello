import {findAccvoucherByIperiod, findBankStatementByKemuAndQichu} from "/@/api/record/system/bank-statement";

export const CrudApi = {
  list: findBankStatementByKemuAndQichu,
  columns: [
    /*{
      title: 'ID',
      dataIndex: 'id',
      defaultHidden: true,
      ellipsis: true
    },*/
    {
      title: '日期',
      dataIndex: 'statementDate',
      width: 100,
      ellipsis: true
    },
    {
      title: '结算方式',
      dataIndex: 'settModes',
      width: 100,
      ellipsis: true
    },
    {
      title: '票号',
      dataIndex: 'piaohao',
      width: 100,
      ellipsis: true
    },
    {
      title: '对方单位',
      dataIndex: 'duifangUnit',
      customCell: () => {		// 在此处可以修改单元格中的样式
        return { style: { 'text-align': 'left'}} // return 想要设置的样式
      },
      ellipsis: true
    },
    {
      title: '摘要',
      dataIndex: 'remarks',
      customCell: () => {		// 在此处可以修改单元格中的样式
        return { style: { 'text-align': 'left'}} // return 想要设置的样式
      },
      ellipsis: true
    },
    {
      title: '借方金额',
      dataIndex: 'jie',
      width: 150,
      customCell: () => {		// 在此处可以修改单元格中的样式
        return {style: {'text-align': 'right', '-webkit-line-clamp': 2}} // return 想要设置的样式
      },
      ellipsis: true,
      slots: {customRender: 'jie'}
    },
    {
      title: '贷方金额',
      dataIndex: 'dai',
      width: 150,
      customCell: () => {		// 在此处可以修改单元格中的样式
        return {style: {'text-align': 'right', '-webkit-line-clamp': 2}} // return 想要设置的样式
      },
      ellipsis: true,
      slots: {customRender: 'dai'}
    },
    /*{
      title: '余额',
      dataIndex: '',
      width: 100,
      customCell: () => {		// 在此处可以修改单元格中的样式
        return { style: { 'text-align': 'right', '-webkit-line-clamp': 2 }} // return 想要设置的样式
      },
      ellipsis: true
    },
    {
      title: '币种',
      dataIndex: 'currencyId',
      defaultHidden: true,
      width: 100,
      customCell: () => {		// 在此处可以修改单元格中的样式
        return { style: { 'text-align': 'right', '-webkit-line-clamp': 2 }} // return 想要设置的样式
      },
      ellipsis: true
    }*/
  ]
}

export const CrudDayApi = {
  list: findAccvoucherByIperiod,
  columns: [
    {
      title: '制单日期',
      dataIndex: 'dbillDate',
      width: 100,
      ellipsis: true
    },
    {
      title: '凭证类别',
      dataIndex: 'csign',
      defaultHidden: true,
      ellipsis: true
    },
    {
      title: '凭证编号',
      dataIndex: 'inoId',
      // defaultHidden: true,
      ellipsis: true
    },
    {
      title: '摘要',
      dataIndex: 'cdigest',
      customCell: () => {		// 在此处可以修改单元格中的样式
        return { style: { 'text-align': 'left'}} // return 想要设置的样式
      },
      ellipsis: true
    },
    {
      title: '科目编码',
      dataIndex: 'ccode',
      defaultHidden: true,
      ellipsis: true
    },
    {
      title: '科目名称',
      dataIndex: 'ccodeName',
      defaultHidden: true,
      ellipsis: true
    },
    {
      title: '结算方式',
      dataIndex: 'pjCsettle',
      width: 120,
      ellipsis: true
    },
    {
      title: '票据日期',
      dataIndex: 'pjDate',
      customCell: () => {		// 在此处可以修改单元格中的样式
        return { style: { 'text-align': 'left'}} // return 想要设置的样式
      },
      ellipsis: true
    },
    {
      title: '票号',
      dataIndex: 'pjId',
      ellipsis: true
    },
    {
      title: '对方单位',
      dataIndex: 'pjUnitName',
      // defaultHidden: true,
      ellipsis: true
    },
    {
      title: '借方金额',
      dataIndex: 'md',
      width: 150,
      customCell: () => {		// 在此处可以修改单元格中的样式
        return {style: {'text-align': 'right', '-webkit-line-clamp': 2}} // return 想要设置的样式
      },
      ellipsis: true,
      slots: {customRender: 'md'}
    },
    {
      title: '贷方金额',
      dataIndex: 'mc',
      width: 150,
      customCell: () => {		// 在此处可以修改单元格中的样式
        return {style: {'text-align': 'right', '-webkit-line-clamp': 2}} // return 想要设置的样式
      },
      ellipsis: true,
      slots: {customRender: 'mc'}
    },
  ]
}
