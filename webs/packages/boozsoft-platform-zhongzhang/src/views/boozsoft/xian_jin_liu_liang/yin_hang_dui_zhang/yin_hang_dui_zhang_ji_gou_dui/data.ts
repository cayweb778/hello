import {
  findAccountByKemuAndDate,
  findByBankByStatementNum,
  findByKemuAndDate,
  findByKemuAndDateByStatementNum
} from "/@/api/record/system/bank-statement";

export const CrudApi = {
  list: findByKemuAndDate,
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
      title: '对方单位',
      dataIndex: 'duifangUnit',
      customCell: () => {		// 在此处可以修改单元格中的样式
        return { style: { 'text-align': 'left'}} // return 想要设置的样式
      },
      align: 'left',
      ellipsis: true
    },
    {
      title: '摘要',
      dataIndex: 'remarks',
      customCell: () => {		// 在此处可以修改单元格中的样式
        return { style: { 'text-align': 'left'}} // return 想要设置的样式
      },
      align: 'left',
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
      title: '借方金额',
      dataIndex: 'jie',
      width: 120,
      customCell: () => {		// 在此处可以修改单元格中的样式
        return { style: { 'text-align': 'right', '-webkit-line-clamp': 2 }} // return 想要设置的样式
      },
      align: 'right',
      ellipsis: true,
      slots: { customRender: 'jie' }
    },
    {
      title: '贷方金额',
      dataIndex: 'dai',
      width: 120,
      customCell: () => {		// 在此处可以修改单元格中的样式
        return { style: { 'text-align': 'right', '-webkit-line-clamp': 2 }} // return 想要设置的样式
      },
      align: 'right',
      ellipsis: true,
      slots: { customRender: 'dai' }
    },
    {
      title: '对账状态',
      dataIndex: 'flag',
      width: 100,
      ellipsis: true,
      slots: { customRender: 'flag' }
    },
    {
      title: '对账号',
      dataIndex: 'statementNum',
      width: 160,
      ellipsis: true,
      slots: { customRender: 'statementNum' }
    }
  ]
}

export const CrudDayApi = {
  list: findAccountByKemuAndDate,
  columns: [
    {
      title: '日期',
      dataIndex: 'dbillDate',
      width: 100,
      ellipsis: true
    },
    {
      title: '对方单位',
      dataIndex: 'ccusId',//客户供应商档案，任意一个
      customCell: () => {		// 在此处可以修改单元格中的样式
        return {style: {'text-align': 'left'}} // return 想要设置的样式
      },
      align: 'left',
      ellipsis: true,
      slots: {customRender: 'ccusId'}
    },
    {
      title: '凭证号',
      dataIndex: 'inoId',
      width: 100,
      ellipsis: true
    },
    {
      title: '摘要',
      dataIndex: 'cdigest',
      customCell: () => {		// 在此处可以修改单元格中的样式
        return {style: {'text-align': 'left'}} // return 想要设置的样式
      },
      align: 'left',
      ellipsis: true
    },
    {
      title: '结算方式',
      dataIndex: 'pjCsettle',
      width: 100,
      ellipsis: true
    },
    {
      title: '票据日期',
      dataIndex: 'pjDate',
      width: 100,
      customCell: () => {		// 在此处可以修改单元格中的样式
        return { style: { 'text-align': 'left'}} // return 想要设置的样式
      },
      align: 'left',
      ellipsis: true
    },
    {
      title: '票号',
      dataIndex: 'pjId',
      width: 100,
      ellipsis: true
    },
    {
      title: '借方金额',
      dataIndex: 'md',
      width: 120,
      customCell: () => {		// 在此处可以修改单元格中的样式
        return { style: { 'text-align': 'right', '-webkit-line-clamp': 2 }} // return 想要设置的样式
      },
      align: 'right',
      ellipsis: true,
      slots: { customRender: 'md' }
    },
    {
      title: '贷方金额',
      dataIndex: 'mc',
      width: 120,
      customCell: () => {		// 在此处可以修改单元格中的样式
        return { style: { 'text-align': 'right', '-webkit-line-clamp': 2 }} // return 想要设置的样式
      },
      align: 'right',
      ellipsis: true,
      slots: { customRender: 'mc' }
    },
    {
      title: '对账号',
      dataIndex: 'statementNum',
      width: 160,
      ellipsis: true,
      slots: { customRender: 'statementNum' }
    }
  ]
}
