import {defRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";


//获取存货分类
export const getStockClassTree = defRouteApi( async (params) => {
    return {
      url:'/stockclass/treeStockClass',
      method: 'GET',
      timeout: 100000000000,
      params
    };
  }
);
//获取客户
export const getAllCustTree = defRouteApi( async (id) => {
    return {
      url:'/stockTaking/getAllCustTree/'+id,
      method: 'GET',
      timeout: 100000000000,
    };
  }
);
//list
export const findAll = defRouteApi(async (params) => {
  return {
    url: '/stockTaking/findAll',
    timeout: 100000000000,
    method: 'POST',
    params
  };
});

export const savePrice = defRouteApi(async (params) => {
  return {
    url: '/stockTaking/save',
    timeout: 100000000000,
    method: 'POST',
    params
  };
});
export const saveMx = defRouteApi(async (params) => {
  return {
    url: '/stockTaking/saveMx',
    timeout: 100000000000,
    method: 'POST',
    params
  };
});
export const delPrice = defRouteApi(async (id) => {
  return {
    url: '/stockTaking/del/'+id,
    timeout: 100000000000,
    method: 'GET',
  };
});
export const delCust = defRouteApi(async (id) => {
  return {
    url: '/stockTaking/delCust/'+id,
    timeout: 100000000000,
    method: 'GET',
  };
});

export const addCusts = defRouteApi(async (params) => {
  return {
    url: '/stockTaking/addCusts',
    timeout: 100000000000,
    method: 'POST',
    params
  };
});



export const findSettModesAll = defRouteApi(()=> {
  return {
    url: '/stockTaking/findAllTaking',
    method: 'GET',
  }
})

export const findSettModesByFlag = defRouteApi(()=> {
  return {
    url: '/stockTaking/findAllTaking',
    method: 'GET',
    headers: {
      ignoreCancelToken: true
    }
  }
})

export const saveSettModes = defRouteApi((params)=> {
  return {
    url: '/stockTaking',
    method: 'POST',
    params
  }
})

export const deleteSettModes = defRouteApi((params)=> {
  return {
    url: '/stockTaking',
    method: 'DELETE',
    params
  }
})

export const findBySettModes = defRouteApi((settModesCode:any)=> {
  return {
    url: '/stockTaking/findByCode?settModesCode='+settModesCode,
    method: 'GET',
    headers: {
      ignoreCancelToken: true
    }
  }
})
export const findByBsName = defRouteApi((settModesCode:any)=> {
  return {
    url: '/stockTaking/findByBsName?settModesCode='+settModesCode,
    method: 'GET',
    headers: {
      ignoreCancelToken: true
    }
  }
})

export const editFlag = defRouteApi((params:any)=> {
  return {
    url: '/stockTaking/editFlag',
    method: 'POST',
    headers: {
      ignoreCancelToken: true
    },
    params
  }
})

export const excelVoucherType = defRouteApi((params:any)=> {
  return {
    url: '/stockTaking/excel',
    method: 'POST',
    headers: {
      ignoreCancelToken: true
    },
    params
  }
})

export const findDeptList = defRouteApi((params:any)=> {
  return {
    url: '/sys/department/all',
    method: 'GET',
    headers: {
      ignoreCancelToken: true
    },
    params
  }
})
export const findUserList = defRouteApi(()=> {
  return {
    url: '/sys/psn/getAll',
    method: 'GET',
    headers: {
      ignoreCancelToken: true
    },
  }
})
export const findSearchAll = defRouteApi( async (search)=>{
  return  {
    url: '/cangku/findSearchAll?search='+search,
    method: 'POST',timeout: 10000000,
  }
})

export const findAllStockClass = defRouteApi(async () => {
  return {
    url: '/stockTaking/findAllStockClass',
    timeout: 100000000000,
    method: 'GET',
  };
});

export const findAllStock = defRouteApi(async () => {
  return {
    url: '/stockTaking/findAllStock',
    timeout: 100000000000,
    method: 'GET',
  };
});

export const checkAdd = defRouteApi((params:any)=> {
  return {
    url: '/stockTaking/checkAdd',
    method: 'POST',
    headers: {
      ignoreCancelToken: true
    },
    params
  }
})
export const clearPd = defRouteApi( async (ccode)=>{
  return  {
    url: '/stockTaking/clearPd?ccode='+ccode,
    method: 'GET',
    timeout: 10000000,
  }
})
export const audit = defRouteApi( async (ccode)=>{
  return  {
    url: '/stockTaking/audit?ccode='+ccode,
    method: 'GET',
    timeout: 10000000,
  }
})
export const auditBack = defRouteApi( async (ccode)=>{
  return  {
    url: '/stockTaking/auditBack?ccode='+ccode,
    method: 'GET',
    timeout: 10000000,
  }
})
