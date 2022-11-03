export function defineFuZhuHeSuan(){
  return [
    {key:'fzDept',label:'部门'},
    {key:'fzEmp',label:'人员'},
    {key:'fzCustom',label:'客户',},
    {key:'fzGys',label:'供应商',},
    {key:'fzItem',label:'项目',},
    {key:'fzItemClass',label:'项目大类',}
  ]
}

export function defineFuZhuHeSuanApiList(){
  return defineFuZhuHeSuan().map(item=>Object.assign({api:null,list:[]},item))
}
export function defineFuZhuHeSuanModel(){
  return defineFuZhuHeSuan().map(item=>Object.assign({value:{key:'1',label:'2'}},item))
}
