function createBaseRow(){
  return {
    templateId: '',
    systemField: '',
    tableField: '',
    billField: '',
    billType: '',
    billStyle: '',
    flag: ''
  }
}
function createBaseRow1(){
  return [{
    templateId: '',
    systemField: '日期',
    tableField: 'statementDate',
    billField: '日期',
    billType: '日期',
    billStyle: '',
    flag: ''
  },
    {
      templateId: '',
      systemField: '对方单位',
      tableField: 'duifangUnit',
      billField: '对方单位',
      billType: '文本',
      billStyle: '',
      flag: ''
    },
    {
      templateId: '',
      systemField: '结算方式',
      tableField: 'settModes',
      billField: '结算方式',
      billType: '文本',
      billStyle: '',
      flag: ''
    },
    {
      templateId: '',
      systemField: '票号',
      tableField: 'piaohao',
      billField: '票号',
      billType: '文本',
      billStyle: '',
      flag: ''
    },{
      templateId: '',
      systemField: '借方金额',
      tableField: 'jie',
      billField: '借方金额',
      billType: '金额',
      billStyle: '',
      flag: ''
    },{
      templateId: '',
      systemField: '贷方金额',
      tableField: 'dai',
      billField: '贷方金额',
      billType: '金额',
      billStyle: '',
      flag: ''
    },{
      templateId: '',
      systemField: '摘要',
      tableField: 'remarks',
      billField: '摘要',
      billType: '文本',
      billStyle: '',
      flag: ''
    },{
      templateId: '',
      systemField: '币种',
      tableField: 'currencyId',
      billField: '币种',
      billType: '文本',
      billStyle: '',
      flag: ''
    }
    ]
}
function BankTemplateItemConfigModelHelper(){

  return  {
    createBaseRow,
    createBaseRow1,
    createBankTemplateItemConfig: function () {
        return {
          count: 0,
          base: {
            templateName: '',
            templateEnName: '',
            templateTitleStart: '',
            flag: '',
            table: createBaseRow1()
          }
        }
    }
  }
}
export default BankTemplateItemConfigModelHelper
