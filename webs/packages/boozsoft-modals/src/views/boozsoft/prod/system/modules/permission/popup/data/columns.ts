import {FormSchema} from "/@/components/Form";
function getCategorySchema(){
  return  {
    field: 'category',
    component: 'RadioGroup',
    label: '菜单类型',
    defaultValue: 2,
    // itemProps: {
    //   extra: '客户、邀评人默认被分享'
    // },
    componentProps: {
      options: [
        {
          label: '目录',
          value: 0
        },
        {
          label: '菜单',
          value: 1
        },
        {
          label: '按钮/权限',
          value: 2
        }
      ]
    }
  }
}
function getDirectorySchemas(){
  return [
    getCategorySchema(),
    {
      field: 'parentId',
      component: 'Input',
      label: '菜单上级',
      ifShow: false,
      defaultValue:'LAYOUT',
      // show:false,
      required: true
    },
    {
      field: 'parentId',
      component: 'Input',
      label: '菜单上级',
      ifShow: false,
      // show:false,
      required: true
    },
    {
      field: 'name',
      component: 'Input',
      label: '菜单名称',
      dynamicDisabled:false,
      componentProps: {
        placeholder: '给菜单起个名字',
        autocomplete: 'off',
      },
      required: true
    },
    {
      field: 'component',
      component: 'Input',
      label: '组件',
      defaultValue: 'LAYOUT',
      ifShow:false,
      required: true
    },
    {
      field: 'path',
      component: 'Input',
      label: '路径',
      componentProps: {
        placeholder: '给目标起个名字'
      },
      required: true
    },
    {
      field: 'redirect',
      component: 'Input',
      label: '默认跳转地址',
    }, {
      field: 'icon',
      component: 'Input',
      label: '菜单图标',
    }, {
      field: 'sortNo',
      component: 'Input',
      label: '排序',
    },
    {
      field: 'isRoute', component: 'Switch',
      label: '是否路由菜单'

    },
    {
      field: 'hidden', component: 'Switch',
      label: '隐藏路由'

    },
    {
      field: 'keepAlive', component: 'Switch',
      label: '是否缓存路由'

    },
    {
      field: 'alwaysShow', component: 'Switch',
      label: '聚合路由'

    },
    {
      field: 'internalOrExternal', component: 'Switch',
      label: '打开方式'
    }
  ]
}
function getMenuSchemas(){
  return [
    getCategorySchema(),
    {
      field: 'parentId',
      component: 'Input',
      label: '菜单上级',
      ifShow: false,
      defaultValue:'LAYOUT',
      // show:false,
      required: true
    },
    {
      field: 'parentId',
      component: 'Input',
      label: '菜单上级',
      ifShow: false,
      // show:false,
      required: true
    },
    {
      field: 'name',
      component: 'Input',
      label: '菜单名称',
      dynamicDisabled:false,
      componentProps: {
        placeholder: '给菜单起个名字',
        autocomplete: 'off',
      },
      required: true
    },
    {
      field: 'path',
      component: 'Input',
      label: '菜单路径',
      componentProps: {
        placeholder: '路径'
      },
      required: true
    },
    {
      field: 'component',
      component: 'Input',
      label: '组件',
      componentProps: {
        placeholder: 'view组件路径'
      },
      required: true
    },

    {
      field: 'redirect',
      component: 'Input',
      label: '默认跳转地址',
      componentProps: {
        placeholder: '请输入路由参数 redirect'
      },
    }, {
      field: 'icon',
      component: 'Input',
      label: '菜单图标',
    }, {
      field: 'sortNo',
      component: 'Input',
      label: '排序',
    },
    {
      field: 'isRoute', component: 'Switch',
      label: '是否路由菜单'

    },
    {
      field: 'hidden', component: 'Switch',
      label: '隐藏路由'

    },
    {
      field: 'keepAlive', component: 'Switch',
      label: '是否缓存路由'

    },
    {
      field: 'alwaysShow', component: 'Switch',
      label: '聚合路由'

    },
    {
      field: 'internalOrExternal', component: 'Switch',
      label: '打开方式'
    }
  ]
}
function getButtonSchemas(){
  return [
    getCategorySchema(),
    {
      field: 'parentId',
      component: 'Input',
      label: '菜单上级',
      ifShow: false,
      defaultValue:'LAYOUT',
      // show:false,
      required: true
    },
    {
      field: 'parentId',
      component: 'Input',
      label: '菜单上级',
      ifShow: false,
      // show:false,
      required: true
    },
    {
      field: 'name',
      component: 'Input',
      label: '菜单名称',
      dynamicDisabled:false,
      componentProps: {
        placeholder: '给菜单起个名字',
        autocomplete: 'off',
      },
      required: true
    },
    {
      field: 'path',
      component: 'Input',
      label: '菜单路径',
      componentProps: {
        placeholder: '给目标起个名字'
      },
      required: true
    }, {
      field: 'component',
      component: 'Input',
      label: '菜单组件',
      componentProps: {
        placeholder: '给目标起个名字'
      },
      required: true
    },
    {
      field: 'redirect',
      component: 'InputTextArea',
      label: '默认跳转地址',
      componentProps: {
        placeholder: '默认跳转地址',
        rows: 4
      },
    }, {
      field: 'icon',
      component: 'Input',
      label: '菜单图标',
      componentProps: {
        placeholder: '给目标起个名字'
      },
      required: true
    }, {
      field: 'sortNo',
      component: 'Input',
      label: '排序',
      componentProps: {
        placeholder: '给目标起个名字'
      },
      required: true
    },
    {
      field: 'isRoute', component: 'Switch',
      label: '是否路由菜单'

    },
    {
      field: 'hidden', component: 'Switch',
      label: '隐藏路由'

    },
    {
      field: 'keepAlive', component: 'Switch',
      label: '是否缓存路由'

    },
    {
      field: 'alwaysShow', component: 'Switch',
      label: '聚合路由'

    },
    {
      field: 'internalOrExternal', component: 'Switch',
      label: '打开方式'
    }
  ]
}

export let DirectorySchemas: FormSchema[] = getDirectorySchemas()
export let MenuSchemas: FormSchema[] = getMenuSchemas()
export let ButtonSchemas: FormSchema[] = getButtonSchemas()