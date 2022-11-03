import { createMenu, createModel} from '/@/../pages/rbac/utils/mockMenuUtil';
import {
  PLATFORM_CAISHUIDA, PLATFORM_GUDINGZICHAN, usePlatform
} from '../../platforms/platformMenus';
const platformId=20001
function createPlatformMenu(params){
  return createMenu({...params,platformId})
}
export const menus = [

  createModel({
    id: platformId,
    isCloud: false,
    isTargetBlank: false,
    isOutLink: false,
    name: '固定资产',
    category: 102,
    sortNo: 500,
  }),
  createPlatformMenu({
    id: 9912,
    path: '/',
    redirect:'/guDingZiChan/home/welcome',
    component: 'LAYOUT',
    hidden:true,
    parentId: 0,
    name: '根目录'
  }),

  createPlatformMenu({ id: 210100, path: '/card', component: 'LAYOUT', parentId: 0, name: '资产卡片' }),

  createPlatformMenu({ id: 210200, path: '/alteration', component: 'LAYOUT', parentId: 0, name: '变动' }),
  createPlatformMenu({ id: 210250, path: '/minus', component: 'LAYOUT', parentId: 0, name: '处置' }),
  createPlatformMenu({ id: 210300, path: '/inventory', component: 'LAYOUT', parentId: 0, name: '盘点' }),
  createPlatformMenu({ id: 210350, path: '/evaluate', component: 'LAYOUT', parentId: 0, name: '评估' }),

  createPlatformMenu({ id: 210400, path: '/depreciation', component: 'LAYOUT', parentId: 0, name: '计提折旧' }),
  createPlatformMenu({ id: 210500, path: '/voucher-handle', component: 'LAYOUT', parentId: 0, name: '凭证处理' }),
  createPlatformMenu({ id: 210600, path: '/accounts', component: 'LAYOUT', parentId: 0, name: '账表' }),
  createPlatformMenu({ id: 210700, path: '/qi-mo', component: 'LAYOUT', parentId: 0, name: '期末' }),
  createPlatformMenu({ id: 210800, path: '/gdzc-setting', component: 'LAYOUT', parentId: 0, name: '设置' }),

  createPlatformMenu({ id: 110000, path: '/guDingZiChan/home', component: 'LAYOUT', parentId: 0, name: '工作台' }),
  createPlatformMenu({id: 110001, path: 'welcome', component: '/dashboard/analysis/index', parentId: 110000, name: '首页'}),

  createPlatformMenu({ id: 210110, path: '/handle', component: '', parentId: 210100, name: '资产处理' }),
  createPlatformMenu({ id: 210111, path: 'gd-incr', component: '/boozsoft/gdzc/gu-ding-zi-chan-add/index', componentName:'GuDingZiChanAdd', parentId: 210110, name:   '固定资产新增'}),
  createPlatformMenu({ id: 210112, path: 'wx-incr', component: '/gdzc/dashboard/analysis/index', parentId: 210110, name:   '无形资产新增'}),

  createPlatformMenu({ id: 210114, path: 'zc-import', component: '/gdzc/dashboard/analysis/index', parentId: 210110, name: '资产转入'}),
  createPlatformMenu({ id: 210115, path: 'zc-export', component: '/gdzc/dashboard/analysis/index', parentId: 210110, name: '资产转出'}),

  createPlatformMenu({ id: 210120, path: '/management', component: '', parentId: 210100, name: '资产管理' }),
  createPlatformMenu({ id: 210121, path: 'gd-list', component: '/boozsoft/gdzc/gu-ding-zi-chan-list/index', parentId: 210120, name:   '固定资产列表'}),
  createPlatformMenu({ id: 210122, path: 'wx-list', component: '/gdzc/dashboard/analysis/index', parentId: 210120, name:   '无形资产列表'}),
  createPlatformMenu({ id: 210123, path: 'ylzc', component: '/gdzc/dashboard/analysis/index', parentId: 210120, name:      '逾龄资产'}),


  createPlatformMenu({ id: 210210, path: '/combination', component: '', parentId: 210200, name: '资产变动' }),
  createPlatformMenu({ id: 210221, path: 'yz-change', component: '/boozsoft/gdzc/zi-chan-bian-dong-add/index', parentId: 210210, name: '资产变动单'}),
  createPlatformMenu({ id: 210211, path: 'zc-split', component: '/boozsoft/gdzc/zi-chan-chai-fen-add/index', componentName:'ZiChanChaiFenAdd', parentId: 210210, name:   '资产拆分单'}),

  createPlatformMenu({ id: 210220, path: '/changes', component: '', parentId: 210200, name: '变动管理' }),
  createPlatformMenu({ id: 210222, path: 'ljzj-change', component: '/boozsoft/gdzc/zi-chan-bian-dong-list/index', parentId: 210220, name:   '资产变动单列表'}),
  createPlatformMenu({ id: 210212, path: 'zc-merge', component: '/boozsoft/gdzc/zi-chan-chai-fen-list/index', parentId: 210220, name:   '资产拆分单列表'}),
  /*createPlatformMenu({ id: 210223, path: 'dept-change', component: '/gdzc/dashboard/analysis/index', parentId: 210220, name:   '部门调整'}),
  createPlatformMenu({ id: 210224, path: 'exec-change', component: '/gdzc/dashboard/analysis/index', parentId: 210220, name:   '使用年限调整'}),
  createPlatformMenu({ id: 210225, path: 'jcz-change', component: '/gdzc/dashboard/analysis/index', parentId: 210220, name:    '净残值调整'}),
  createPlatformMenu({ id: 210226, path: 'worksum-change', component: '/gdzc/dashboard/analysis/index', parentId: 210220, name:'工作总量调整'}),
  createPlatformMenu({ id: 210227, path: 'zjfun-change', component: '/gdzc/dashboard/analysis/index', parentId: 210220, name:  '折旧方法调整'}),
  createPlatformMenu({ id: 210228, path: 'jxse-change', component: '/gdzc/dashboard/analysis/index', parentId: 210220, name:   '进项税额调整'}),
  createPlatformMenu({ id: 210229, path: 'zclb-change', component: '/gdzc/dashboard/analysis/index', parentId: 210220, name:   '资产类别调整'}),*/


  createPlatformMenu({ id: 210310, path: '/depreciation-content', component: '', parentId: 210400, name: '计提' }),
  createPlatformMenu({ id: 210311, path: 'jtzj', component: '/boozsoft/gdzc/ji-ti-zhe-jiu/index',componentName:'JiTiZheJiu', parentId: 210310, name:   '计提折旧'}),
  createPlatformMenu({ id: 210312, path: 'jttx', component: '/gdzc/dashboard/analysis/index', parentId: 210310, name:   '计提摊销'}),
  createPlatformMenu({ id: 210313, path: 'sumlr', component: '/gdzc/dashboard/analysis/index', parentId: 210310, name:    '工作量录入'}),


  createPlatformMenu({ id: 210320, path: '/inventory', component: '', parentId: 210400, name: '清单' }),
  createPlatformMenu({ id: 210321, path: 'zjqd', component: '/boozsoft/gdzc/ji-ti-zhe-jiu/index',componentName:'JiTiZheJiu', parentId: 210320, name:   '折旧清单'}),
  createPlatformMenu({ id: 210322, path: 'zjfp-dept', component: '/gdzc/dashboard/analysis/index', parentId: 210320, name:   '部门折旧分配表'}),
  createPlatformMenu({ id: 210323, path: 'zjfp-type', component: '/gdzc/dashboard/analysis/index', parentId: 210320, name:  '类别折旧分配表'}),

  createPlatformMenu({ id: 210460, path: '/handle', component: '', parentId: 210300, name: '盘点处理' }),
  createPlatformMenu({ id: 210124, path: 'save', component: '/boozsoft/gdzc/zi-chan-pan-dian-add/index', parentId: 210460, name: '新增盘点单'}),
  createPlatformMenu({ id: 210126, path: 'surplus', component: '/boozsoft/gdzc/zi-chan-pan-dian-add/index', parentId: 210460, name: '盘盈处理'}),
  createPlatformMenu({ id: 210127, path: 'lose-money', component: '/boozsoft/gdzc/zi-chan-pan-dian-list/index', parentId: 210460, name: '盘亏处理'}),

  createPlatformMenu({ id: 210470, path: '/management', component: '', parentId: 210300, name: '盘点管理' }),
  createPlatformMenu({ id: 210125, path: 'list', component: '/boozsoft/gdzc/zi-chan-pan-dian-list/index', parentId: 210470, name: '盘点单列表'}),

  createPlatformMenu({ id: 210251, path: '/handle', component: '', parentId: 210250, name: '处置处理' }),
  createPlatformMenu({ id: 210260, path: '/management', component: '', parentId: 210250, name: '处置管理' }),
  createPlatformMenu({ id: 210261, path: 'zc-handle-add', component: '/boozsoft/gdzc/zi-chan-chu-zhi-add/index', parentId: 210251, name: '资产处置'}),
  createPlatformMenu({ id: 210262, path: 'zc-handle-list', component: '/boozsoft/gdzc/zi-chan-chu-zhi-list/index', parentId: 210260, name: '资产处置列表'}),

  createPlatformMenu({ id: 210351, path: '/handle', component: '', parentId: 210350, name: '评估处理' }),
  createPlatformMenu({ id: 210360, path: '/management', component: '', parentId: 210350, name: '评估管理' }),
  createPlatformMenu({ id: 210361, path: 'zc-assess-add', component: '/boozsoft/gdzc/zi-chan-ping-gu-add/index', parentId: 210351, name: '资产评估'}),
  createPlatformMenu({ id: 210362, path: 'zc-assess-list', component: '/boozsoft/gdzc/zi-chan-ping-gu-list/index', parentId: 210360, name: '资产评估列表'}),


  createPlatformMenu({ id: 210410, path: '/maker', component: '', parentId: 210500, name: '制单' }),
  createPlatformMenu({ id: 210411, path: 'zjzd', component: '/gdzc/dashboard/analysis/index', parentId: 210410, name:        '折旧制单'}),
  createPlatformMenu({ id: 210412, path: 'zc-change', component: '/gdzc/dashboard/analysis/index', parentId: 210410, name:   '资产变动制单'}),
  createPlatformMenu({ id: 210413, path: 'zc-handle', component: '/gdzc/dashboard/analysis/index', parentId: 210410, name:   '资产处理制单'}),
  createPlatformMenu({ id: 210414, path: 'surplus', component: '/gdzc/dashboard/analysis/index', parentId: 210410, name:   '凭证盘盈制单'}),
  createPlatformMenu({ id: 210415, path: 'lose-money', component: '/gdzc/dashboard/analysis/index', parentId: 210410, name:   '凭证盘亏制单'}),

  createPlatformMenu({ id: 210420, path: '/pz-list', component: '', parentId: 210500, name: '列表' }),
  createPlatformMenu({ id: 210411, path: 'pz-list-content', component: '/boozsoft/gdzc/fa-accvoucher/index', parentId: 210420, name: '凭证列表'}),
  createPlatformMenu({ id: 210412, path: 'cw-list', component: '/gdzc/dashboard/analysis/index', parentId: 210420, name: '财务对账'}),

  createPlatformMenu({ id: 210510, path: '/statistics', component: '', parentId: 210600, name: '统计分析表' }),
  createPlatformMenu({ id: 210511, path: 'gdzc-yuanzhi-table', component: '/gdzc/dashboard/analysis/index', parentId:   210510, name:     '固定资产原值一览表'}),
  createPlatformMenu({ id: 210512, path: 'gdzc-tongji-table', component: '/gdzc/dashboard/analysis/index', parentId: 210510, name:     '固定资产统计表'}),
  createPlatformMenu({ id: 210513, path: 'gdzc-change-table', component: '/gdzc/dashboard/analysis/index', parentId: 210510, name:     '固定资产变动情况表'}),
  createPlatformMenu({ id: 210514, path: 'gdzc-date-table', component: '/gdzc/dashboard/analysis/index', parentId: 210510, name:     '固定资产到期情况表'}),
  createPlatformMenu({ id: 210515, path: 'gdzc-kuisun-table', component: '/gdzc/dashboard/analysis/index', parentId:  210510, name:     '固定资产盘盈盘亏表'}),

  createPlatformMenu({ id: 210520, path: '/accounts-content', component: '', parentId: 210600, name: '账表' }),
  createPlatformMenu({ id: 210521, path: 'zc-total', component: '/boozsoft/gdzc/general-ledger/index', parentId: 210520, name: '资产总账'}),
  createPlatformMenu({ id: 210522, path: 'zc-mingxi', component: '/gdzc/dashboard/analysis/index', parentId: 210520, name: '资产明细账'}),


  createPlatformMenu({ id: 210610, path: '/qm-handle', component: '', parentId: 210700, name: '处理' }),
  createPlatformMenu({ id: 210611, path: 'qm-close', component: '/boozsoft/gdzc/zi-chan-jie-zhang/index', parentId: 210610, name: '月末结账'}),
  createPlatformMenu({ id: 210612, path: 'qm-unclose', component: '/boozsoft/gdzc/zi-chan-un-jie-zhang/index', parentId: 210610, name: '取消结账'}),

  createPlatformMenu({ id: 210710, path: '/setting-qichu', component: '', parentId: 210800, name: '期初' }),
  createPlatformMenu({ id: 210711, path: 'qc-card', component: '/gdzc/dashboard/analysis/index', parentId: 210710, name:   '期初原始卡片'}),
  createPlatformMenu({ id: 210712, path: 'qc-kemu-set', component: '/gdzc/dashboard/analysis/index', parentId: 210710, name: '折旧科目设置'}),

  createPlatformMenu({ id: 210720, path: '/setting-file', component: '', parentId: 210800, name: '档案' }),

  createPlatformMenu({ id: 210731, path: 'xt-set', component: '/gdzc/dashboard/analysis/index',   parentId: 210720, name:     '系统选项'}),
  createPlatformMenu({ id: 210732, path: 'handle-reason', component: '/gdzc/dashboard/analysis/index',parentId: 210720, name:     '处置原因'}),

  createPlatformMenu({id:210722,path:'assets-property',component:'/boozsoft/system/faProperty/index',parentId:210720,name:'资产属性'}),
  createPlatformMenu({id:210723,path:'assets-type',component:'/boozsoft/system/fa-asset-type/index',parentId:210720,name:'资产类别'}),
  createPlatformMenu({id:210724,path:'assets-depreciation',component:'/boozsoft/system/fa-zj-method/index',parentId:210720,name:'折旧方法'}),
  createPlatformMenu({id:210725,path:'assets-increase-decrease',component:'/boozsoft/system/fa-add-cut-mode/index',parentId:210720,name:'增减方式'}),
  createPlatformMenu({id:210726,path:'assets-situation',component:'/boozsoft/system/fa-usage-status/index',parentId:210720,name:'使用状况'}),
  createPlatformMenu({id:210727,path:'assets-use',component:'/boozsoft/system/fa-economy-use/index',parentId:210720,name:'经济用途'}),
  createPlatformMenu({id:210728,path:'assets-column-setting',component:'/boozsoft/system/fa-location/index',parentId:210720,name:'存放设置'}),
  createPlatformMenu({id:210729,path:'assets-group',component:'/boozsoft/system/fa-asset-group/index',parentId:210720,name:'资产组'}),
  createPlatformMenu({id:210732,path:'card-column-setting',component:'/boozsoft/system/fa-card-column/index',parentId:210720,name:'卡片栏目设置'}),

  createPlatformMenu({ id: 210730, path: '/zc-options', component: '', parentId: 210800, name: '资产选项'}),
  createPlatformMenu({ id: 210736, path: 'zco-parm', component: '/boozsoft/gdzc/zc-options/index', parentId: 210730, name: '参数设置'}),
  createPlatformMenu({ id: 210737, path: 'zco-authorize', component: '/gdzc/dashboard/analysis/index', parentId: 210730, name: '管理类别授权'}),
  createPlatformMenu({ id: 210738, path: 'fa-style', component: '/boozsoft/gdzc/gu-ding-zi-chan-yang-shi/index', componentName: 'GuDingZiChanYangShi', parentId: 210730, name: '卡片样式列表'}),

  createPlatformMenu({ id: 210731, path: 'zc-km-setting', component: '/boozsoft/gdzc/zc-km-setting/index', parentId: 210800, name: '对应科目设置'}),

];

export const platformMenuIds = {
  key:'guDingZiChan',
  id:platformId,
  sort:6,
  menuIds:menus.map(item=>item.id)
};

