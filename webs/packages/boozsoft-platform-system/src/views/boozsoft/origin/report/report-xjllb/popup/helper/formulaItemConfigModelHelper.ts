function createBaseRow(){
  return {
    // id: '',
    templateId:'',
    columnId: '',
    ccode: '',
    fuhao: '+',
    fangxiang: ''
  }
}
function formulaItemConfigModelHelper(){

  return  {
    createBaseRow,
    createFormulaItemConfig:function(){
      return {
        count: 0,
        base: {
          // id: '',
          templateId:'',
          xuhao: '',
          columnShowName: '',
          jici: '1',
          hangci: '',
          columnType: '',
          formulaMethod: '',
          valueMethod: '',
          formulaCount: '',
          formulaTable: []
        }
      }
    }
  }
}
export default formulaItemConfigModelHelper
