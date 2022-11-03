function createBaseRow(){
  return {
    // id: '',
    templateId:'',
    xuhao: '',
    columnShowName: '',
    jici: '',
    hangci: '',
    columnType: '',
    formulaMethod: '',
    valueMethod: '',
    formulaCount: '',
    formulaTable: []
  }
}
function columnItemConfigModelHelper(){

  return  {
    createBaseRow,
    createColumnItemConfig:function(){
      return {
        count: 0,
        base: {
          // id: '',
          reportName: '',
          templateName:'',
          accStandard: '',
          kemuTemplateId: '',
          titleName: '',
          flag: '',
          menu1: '',
          menu2: '',
          menu3: '',
          menu4: '',
          menu5: '',
          zcTable: [],
          fzTable: [],
          table:[]
        }
      }
    }
  }
}
export default columnItemConfigModelHelper
