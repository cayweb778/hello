function createBaseRow(){
  return {
    id:'',
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
    id:'',
    templateId: '',
    systemField: '日期',
    tableField: 'statementDate',
    billField: '',
    billType: '日期',
    billStyle: '',
    flag: ''
  },
    {
      id:'',
      templateId: '',
      systemField: '对方单位',
      tableField: 'duifangUnit',
      billField: '',
      billType: '文本',
      billStyle: '',
      flag: ''
    },
    {
      id:'',
      templateId: '',
      systemField: '结算方式',
      tableField: 'settModes',
      billField: '',
      billType: '文本',
      billStyle: '',
      flag: ''
    },
    {
      id:'',
      templateId: '',
      systemField: '票号',
      tableField: 'piaohao',
      billField: '',
      billType: '文本',
      billStyle: '',
      flag: ''
    },{
      id:'',
      templateId: '',
      systemField: '借方金额',
      tableField: 'jie',
      billField: '',
      billType: '金额',
      billStyle: '',
      flag: ''
    },{
      id:'',
      templateId: '',
      systemField: '贷方金额',
      tableField: 'dai',
      billField: '',
      billType: '金额',
      billStyle: '',
      flag: ''
    },{
      id:'',
      templateId: '',
      systemField: '摘要',
      tableField: 'remarks',
      billField: '',
      billType: '文本',
      billStyle: '',
      flag: ''
    },{
      id:'',
      templateId: '',
      systemField: '币种',
      tableField: 'currencyId',
      billField: '',
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
    createBankTemplateItemConfig:function (){
      return {
        count: 0,
        base: {
          id: '',
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
