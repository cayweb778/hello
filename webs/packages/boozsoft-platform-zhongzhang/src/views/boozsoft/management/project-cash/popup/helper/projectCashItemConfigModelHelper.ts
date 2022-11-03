function createBaseRow(){
  return {
    iyear: '',
    projectCode:'',
    jieCode: '',
    daiCode: ''
  }
}
function ProjectCashItemConfigModelHelper(){

  return  {
    createBaseRow,
    createProjectCashItemConfig:function(){
      return {
        count: 0,
        base: {
          projectCode:'',
          projectName:'',
          projectType:'',
          fangxiang:'',
          flag:'',
          table: [
            createBaseRow()
          ]
        }
      }
    }
  }
}
export default ProjectCashItemConfigModelHelper
