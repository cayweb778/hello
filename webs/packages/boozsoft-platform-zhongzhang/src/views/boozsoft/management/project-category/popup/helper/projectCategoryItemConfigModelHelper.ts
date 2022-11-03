function createBaseRow(){
  return {
    cname: '',
    ctitle:'',
    ctype: '1',
    clength: '',
    sourceType: '1',
    sourceName: '',
    sourceColumn: '',
    islist:'1'
  }
}
function createBaseRow1(){
  return [{
    cname: '项目编号',
    ctitle:'project_code',
    ctype: '1',
    clength: '60',
    sourceType: '1',
    sourceName: '',
    sourceColumn: '',
    islist:'1'
  },{
    cname: '项目名称',
    ctitle:'project_name',
    ctype: '1',
    clength: '60',
    sourceType: '1',
    sourceName: '',
    sourceColumn: '',
    islist:'1'
  },{
    cname: '所属分类码',
    ctitle:'project_class_code',
    ctype: '1',
    clength: '60',
    sourceType: '2',
    sourceName: 'project_class',
    sourceColumn: 'project_class_name',
    islist:'1'
  },{
    cname: '是否结算',
    ctitle:'jiesuan',
    ctype: '5',
    clength: '',
    sourceType: '1',
    sourceName: '',
    sourceColumn: '',
    islist:'1'
  },{
    cname: '开始日期',
    ctitle:'start_date',
    ctype: '4',
    clength: '',
    sourceType: '1',
    sourceName: '',
    sourceColumn: '',
    islist:'1'
  },{
    cname: '结束日期',
    ctitle:'end_date',
    ctype: '4',
    clength: '',
    sourceType: '1',
    sourceName: '',
    sourceColumn: '',
    islist:'1'
  },{
    cname: '项目负责人',
    ctitle:'psn_in_charge',
    ctype: '1',
    clength: '60',
    sourceType: '2',
    sourceName: 'sys_psn',
    sourceColumn: 'psn_name',
    islist:'1'
  },{
    cname: '所属部门',
    ctitle:'dept_code',
    ctype: '1',
    clength: '60',
    sourceType: '2',
    sourceName: 'sys_department',
    sourceColumn: 'dept_name',
    islist:'1'
  }]
}
function projectCategoryItemConfigModelHelper(){

  return  {
    createBaseRow1,
    createBaseRow,
    createProjectCategoryItemConfig:function(){
      return {
        count: 0,
        base: {
          // id:'',
          projectCateCode:'',
          projectCateName:'',
          projectTable:'',
          table: []
        }
      }
    }
  }
}
export default projectCategoryItemConfigModelHelper
