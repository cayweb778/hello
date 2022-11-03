function createBaseRow(){
  return {
    id:'',
    cname: '',
    ctitle: '',
    isflag: '1'
  }
}
function BaseInfoItemConfigModelHelper(){

  return  {
    createBaseRow,
    createBaseInfoItemConfig:function(){
      return {
        count: 0,
        base: {
          id:'',
          baseTable:'',
          baseName:'',
          table: [
            createBaseRow(),
            createBaseRow()
          ]
        }
      }
    }
  }
}
export default BaseInfoItemConfigModelHelper
