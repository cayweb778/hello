// 应付账
import { toRoleMenuList } from '../../utils/_sys_role_menu_util';
import { createMenu, createModel} from '/@/../pages/rbac/utils/mockMenuUtil';
import {
  PLATFORM_DevComponents, PLATFORM_YINGFUZHANG,
  PLATFORM_YingFuZhang, usePlatform
} from '../platforms/platformMenus';
const platformId=20017
function createPlatformMenu(params){
  return createMenu({...params,platformId})
}
export const menus = [
  createModel({
    id: platformId,
    isCloud: false,
    isTargetBlank: false,
    isOutLink: false,
    name: '应付账',
    path:'/YingFuZhang',
    category: 102,
    sortNo: 5,
  }),
  createPlatformMenu({
    id: 3000000,
    path: '/',
    redirect:'/YingFuZhang/home/welcome',
    component: 'LAYOUT',
    parentId: 0,
    hidden:true,
    name: '根目录'
  }),
  createPlatformMenu({
    id: 3000011,
    path: 'welcome',
    // component: '/platforms/YingFuZhang/Home/layouts/RouteCache',
    component: '/dashboard/analysis/index',
    parentId: 3000010,
    name: '首页',
  }),
  createPlatformMenu({ id: 3000010, path: '/YingFuZhang/home', component: 'LAYOUT', parentId: 0, name: '工作台' }),
  /** 一级目录 **/
  createPlatformMenu({
    id: 3100000,
    component: 'LAYOUT',
    path: '/YingFuZhang/QiChu',
    parentId: 0,
    name: '期初'
  }),


  createPlatformMenu({
    id: 3200000,
    component: 'LAYOUT',
    path: '/YingFuZhang/YingShou',
    parentId: 0,
    name: '应付'
  }),


  createPlatformMenu({
    id: 3300000,
    component: 'LAYOUT',
    path: '/YingFuZhang/ShouKuan',
    parentId: 0,
    name: '付款'
  }),


  createPlatformMenu({
    id: 3400000,
    component: 'LAYOUT',
    path: '/YingFuZhang/HeXiao',
    parentId: 0,
    name: '核销'
  }),


  createPlatformMenu({
    id: 3500000,
    component: 'LAYOUT',
    path: '/YingFuZhang/ChongXiao',
    parentId: 0,
    name: '冲销'
  }),


  createPlatformMenu({
    id: 3600000,
    component: 'LAYOUT',
    path: '/YingFuZhang/PingZheng',
    parentId: 0,
    name: '凭证'
  }),


  createPlatformMenu({
    id: 3700000,
    component: 'LAYOUT',
    path: '/YingFuZhang/ZhangBiao',
    parentId: 0,
    name: '账表'
  }),


  createPlatformMenu({
    id: 3800000,
    component: 'LAYOUT',
    path: '/YingFuZhang/JieZhang',
    parentId: 0,
    name: '结账'
  }),


  createPlatformMenu({
    id: 3900000,
    component: 'LAYOUT',
    path: '/YingFuZhang/SheZhi',
    parentId: 0,
    name: '设置'
  }),
  /** 二级目录 **/

  createPlatformMenu({
    id: 3101000,
    path: 'YeWu',
    component: '',
    parentId: 3100000,
    name: '业务'
  }),


  createPlatformMenu({
    id: 3201003,
    path: 'FuHe',
    component: '',
    parentId: 3200000,
    name: '应付复核',
  }),
  createPlatformMenu({
    id: 3200001,
    path: 'DanJu',
    component: '',
    parentId: 3200000,
    name: '单据'
  }),


  createPlatformMenu({
    id: 3200002,
    path: 'ChaXun',
    component: '',
    parentId: 3200000,
    name: '列表'
  }),

  /*createPlatformMenu({
    id: 3200003,
    path: 'ZhangBiao',
    component: '',
    parentId: 3200000,
    name: '账表'
  }),*/
  createPlatformMenu({
    id: 3300001,
    path: 'CaoZuo',
    component: '',
    parentId: 3300000,
    name: '操作'
  }),


  createPlatformMenu({
    id: 3300002,
    path: 'ChaXun',
    component: '',
    parentId: 3300000,
    name: '列表'
  }),
  /*createPlatformMenu({
    id: 3300003,
    path: 'ZhangBiao',
    component: '',
    parentId: 3300000,
    name: '账表'
  }),*/

  createPlatformMenu({
    id: 3400001,
    path: 'CaoZuo',
    component: '',
    parentId: 3400000,
    name: '核销'
  }),


  /*createPlatformMenu({
    id: 3400002,
    path: 'ChaXun',
    component: '',
    parentId: 3400000,
    name: '列表'
  }),*/
  createPlatformMenu({
    id: 3400003,
    path: 'ZhangBiao',
    component: '',
    parentId: 3400000,
    name: '取消'
  }),

  createPlatformMenu({
    id: 3500001,
    path: 'CaoZuo',
    component: '',
    parentId: 3500000,
    name: '操作'
  }),


  createPlatformMenu({
    id: 3500002,
    path: 'ChaXun',
    component: '',
    parentId: 3500000,
    name: '列表'
  }),


  createPlatformMenu({
    id: 3600001,
    path: 'DanJu',
    component: '',
    parentId: 3600000,
    name: '单据'
  }),


  createPlatformMenu({
    id: 3600002,
    path: 'ChaXun',
    component: '',
    parentId: 3600000,
    name: '列表'
  }),

  /*createPlatformMenu({
    id: 3600003,
    path: 'ZhangBiao',
    component: '',
    parentId: 3600000,
    name: '账表'
  }),*/
  createPlatformMenu({
    id: 3700001,
    path: 'ZhangBiaoTongJi',
    component: '',
    parentId: 3700000,
    name: '统计表'
  }),
  createPlatformMenu({
    id: 3700002,
    path: 'ZhangBiaoMingXi',
    component: '',
    parentId: 3700000,
    name: '明细表'
  }),
  createPlatformMenu({
    id: 3700003,
    path: 'ZhangBiaoChaXun',
    component: '',
    parentId: 3700000,
    name: '综合'
  }),

  createPlatformMenu({
    id: 3700004,
    path: 'ZhangBiaoFenXi',
    component: '',
    parentId: 3700000,
    name: '分析'
  }),


  createPlatformMenu({
    id: 3800001,
    path: 'YeWu',
    component: '',
    parentId: 3800000,
    name: '结账'
  }),
  createPlatformMenu({
    id: 3800002,
    path: 'YeWuQuXiao',
    component: '',
    parentId: 3800000,
    name: '取消'
  }),


  createPlatformMenu({
    id: 3900001,
    path: 'CanShu',
    component: '',
    parentId: 3900000,
    name: '参数'
  }),
  createPlatformMenu({
    id: 3900002,
    path: 'ShiZhi',
    component: '',
    parentId: 3900000,
    name: '设置'
  }),


  /*  createPlatformMenu({
      id: 3900002,
      path: 'YingShouDangAn',
      component: '',
      parentId: 3900000,
      name: '档案'
    }),*/


  /*  createPlatformMenu({
      id: 3900003,
      path: 'DuiYingKeMu',
      component: '',
      parentId: 3900000,
      name: '科目'
    }),*/
  /**  三级目录 **/

  createPlatformMenu({
    id: 3101001,
    path: 'YingShouKuanQiChuYuE',
    componentName: 'YingFuZhangXXYingShouKuanQiChuYuE',
    component: '/platforms/YingFuZhang/YingShouKuanQiChuYuE/layouts/RouteCache',
    parentId: 3101000,
    name: '期初余额',
  }),

  createPlatformMenu({
    id: 3203001,
    path: 'XiaoHuoDanFuHe',
    component: '/platforms/YingFuZhang/XiaoHuoDanFuHe/layouts/RouteCache',
    parentId: 3201003,
    name: '采购到货单复核'
  }),
  createPlatformMenu({
    id: 3203002,
    path: 'XiaoShouFaPiaoFuHe',
    component: '/platforms/YingFuZhang/XiaoShouFaPiaoFuHe/layouts/RouteCache',
    parentId: 3201003,
    name: '采购发票复核'
  }),

  createPlatformMenu({
    id: 3201001,
    path: 'YingShouDan',
    componentName: 'YingFuZhangXXYingShouDan',
    component: '/platforms/YingFuZhang/YingShouDan/layouts/RouteCache',
    parentId: 3200001,
    name: '应付单',
  }),


  /*createPlatformMenu({
    id: 3201002,
    path: 'XiaoShouFaPiao',
    componentName: 'YingFuZhangXXXiaoShouFaPiao',
    component: '/platforms/YingFuZhang/XiaoShouFaPiao/layouts/RouteCache',
    parentId: 3200001,
    name: '采购发票',
  }),*/


  createPlatformMenu({
    id: 3201003,
    path: 'FeiYongDan',
    componentName: 'YingFuZhangXXFeiYongDan',
    component: '/platforms/YingFuZhang/FeiYongDan/layouts/RouteCache',
    parentId: 3200001,
    name: '费用单',
  }),


  createPlatformMenu({
    id: 3202001,
    path: 'YingShouDanLieBiao',
    componentName: 'YingFuZhangXXYingShouDanLieBiao',
    component: '/platforms/YingFuZhang/YingShouDanLieBiao/layouts/RouteCache',
    parentId: 3200002,
    name: '应付单列表',
  }),


  /*createPlatformMenu({
    id: 3202002,
    path: 'XiaoShouFaPiaoLieBiao',
    componentName: 'YingFuZhangXXXiaoShouFaPiaoLieBiao',
    component: '/platforms/YingFuZhang/XiaoShouFaPiaoLieBiao/layouts/RouteCache',
    parentId: 3200002,
    name: '采购发票列表',
  }),*/


  createPlatformMenu({
    id: 3202003,
    path: 'FeiYongDanLieBiao',
    componentName: 'YingFuZhangXXFeiYongDanLieBiao',
    component: '/platforms/YingFuZhang/FeiYongDanLieBiao/layouts/RouteCache',
    parentId: 3200002,
    name: '费用单列表',
  }),


  /*createPlatformMenu({
    id: 3203001,
    path: 'YingShouKuanTongJiBiao',
    componentName: 'YingFuZhangXXYingShouKuanTongJiBiao',
    component: '/platforms/YingFuZhang/YingShouKuanTongJiBiao/layouts/RouteCache',
    parentId: 3200003,
    name: '应付款统计表',
  }),


  createPlatformMenu({
    id: 3203002,
    path: 'XiaoShouFaPiaoTongJiBiao',
    componentName: 'YingFuZhangXXXiaoShouFaPiaoTongJiBiao',
    component: '/platforms/YingFuZhang/XiaoShouFaPiaoTongJiBiao/layouts/RouteCache',
    parentId: 3200003,
    name: '采购发票统计表',
  }),


  createPlatformMenu({
    id: 3203003,
    path: 'FeiYongTongJiBiao',
    componentName: 'YingFuZhangXXFeiYongTongJiBiao',
    component: '/platforms/YingFuZhang/FeiYongTongJiBiao/layouts/RouteCache',
    parentId: 3200003,
    name: '费用统计表',
  }),


  createPlatformMenu({
    id: 3203004,
    path: 'YingShouYuEBiao',
    componentName: 'YingFuZhangXXYingShouYuEBiao',
    component: '/platforms/YingFuZhang/YingShouYuEBiao/layouts/RouteCache',
    parentId: 3200003,
    name: '应付余额表',
  }),*/

  /*
  
    createPlatformMenu({
      id: 3301001,
      path: 'XuanZeShouKuan',
      componentName: 'YingFuZhangXXXuanZeShouKuan',
      component: '/platforms/YingFuZhang/XuanZeShouKuan/layouts/RouteCache',
      parentId: 3300001,
      name: '选择付款',
    }),
  */


  createPlatformMenu({
    id: 3301002,
    path: 'YingShouShouKuanDan',
    componentName: 'YingFuZhangXXYingShouShouKuanDan',
    component: '/platforms/YingFuZhang/YingShouShouKuanDan/layouts/RouteCache',
    parentId: 3300001,
    name: '付款单',
  }),


  createPlatformMenu({
    id: 3301003,
    path: 'YingShouTuiKuanDan',
    componentName: 'YingFuZhangXXYingShouTuiKuanDan',
    component: '/platforms/YingFuZhang/YingShouTuiKuanDan/layouts/RouteCache',
    parentId: 3300001,
    name: '退款单',
  }),


  createPlatformMenu({
    id: 3302001,
    path: 'ShouKuanDanLieBiao',
    componentName: 'YingFuZhangXXShouKuanDanLieBiao',
    component: '/platforms/YingFuZhang/ShouKuanDanLieBiao/layouts/RouteCache',
    parentId: 3300002,
    name: '付(退)款单列表',
  }),


  /*createPlatformMenu({
    id: 3302002,
    path: 'TuiKuanDanLieBiao',
    componentName: 'YingFuZhangXXTuiKuanDanLieBiao',
    component: '/platforms/YingFuZhang/TuiKuanDanLieBiao/layouts/RouteCache',
    parentId: 3300002,
    name: '退款单列表',
  }),*/


  /*createPlatformMenu({
    id: 3303002,
    path: 'ShouKuanMingXiBiao',
    componentName: 'YingFuZhangXXShouKuanMingXiBiao',
    component: '/platforms/YingFuZhang/ShouKuanMingXiBiao/layouts/RouteCache',
    parentId: 3300003,
    name: '付款明细表',
  }),


  createPlatformMenu({
    id: 3303001,
    path: 'ShouKuanTongJiBiao',
    componentName: 'YingFuZhangXXShouKuanTongJiBiao',
    component: '/platforms/YingFuZhang/ShouKuanTongJiBiao/layouts/RouteCache',
    parentId: 3300003,
    name: '付款统计表',
  }),
  createPlatformMenu({
    id: 3303003,
    path: 'YingShouHeXiaoMxBiao',
    componentName: 'YingFuZhangXXYingShouHeXiaoMxBiao',
    component: '/platforms/YingFuZhang/ShouKuanTongJiBiao/layouts/RouteCache',
    parentId: 3300003,
    name: '应付核销明细表',
  }),*/



  createPlatformMenu({
    id: 3401001,
    path: 'ShouGongHeXiao',
    componentName: 'YingFuZhangXXShouGongHeXiao',
    component: '/platforms/YingFuZhang/ShouGongHeXiao/layouts/RouteCache',
    parentId: 3400001,
    name: '手工核销',
  }),

  createPlatformMenu({
    id: 3401003,
    path: 'ZiDongHeXiao',
    componentName: 'YingFuZhangXXZiDongHeXiao',
    component: '/platforms/YingFuZhang/ZiDongHeXiao/layouts/RouteCache',
    parentId: 3400001,
    name: '自动核销',
  }),

  createPlatformMenu({
    id: 3401002,
    path: 'QuXiaoHeXiao',
    componentName: 'YingFuZhangXXQuXiaoHeXiao',
    component: '/platforms/YingFuZhang/QuXiaoHeXiao/layouts/RouteCache',
    parentId: 3400003,
    name: '取消核销',
  }),

  /*  createPlatformMenu({
      id: 3401003,
      path: 'HongZiHuiChong',
      componentName: 'YingFuZhangXXHongZiHuiChong',
      component: '/platforms/YingFuZhang/QuXiaoHeXiao/layouts/RouteCache',
      parentId: 3400001,
      name: '红字回冲单',
    }),*/

  /*  createPlatformMenu({
      id: 3402001,
      path: 'HongZiHuiChongList',
      componentName: 'YingFuZhangXXHongZiHuiChongList',
      component: '/platforms/YingFuZhang/YingShouHeXiaoMingXiBiao/layouts/RouteCache',
      parentId: 3400002,
      name: '红字回冲单列表',
    }),*/
  /*createPlatformMenu({
    id: 3403001,
    path: 'YingShouHeXiaoMingXiBiao',
    componentName: 'YingFuZhangXXYingShouHeXiaoMingXiBiao',
    component: '/platforms/YingFuZhang/YingShouHeXiaoMingXiBiao/layouts/RouteCache',
    parentId: 3400003,
    name: '应付核销明细表',
  }),*/


  createPlatformMenu({
    id: 3501001,
    path: 'YuShouChongYingShou',
    componentName: 'YingFuZhangXXYuShouChongYingShou',
    component: '/platforms/YingFuZhang/YuShouChongYingShou/layouts/RouteCache',
    parentId: 3500001,
    name: '预付冲应付',
  }),


  createPlatformMenu({
    id: 3501002,
    path: 'YingShouChongYingShou',
    componentName: 'YingFuZhangXXYingShouChongYingShou',
    component: '/platforms/YingFuZhang/YingShouChongYingShou/layouts/RouteCache',
    parentId: 3500001,
    name: '应付冲应付',
  }),


  createPlatformMenu({
    id: 3501003,
    path: 'YingShouChongShouFu',
    componentName: 'YingFuZhangXXYingShouChongShouFu',
    component: '/platforms/YingFuZhang/YingShouChongShouFu/layouts/RouteCache',
    parentId: 3500001,
    name: '应付冲应收',
  }),
  createPlatformMenu({
    id: 3501004,
    path: 'CxHongZiHuiChong',
    componentName: 'YingFuZhangXXCXHongZiHuiChong',
    component: '/platforms/YingFuZhang/HongZiHuiChongDan/layouts/RouteCache',
    parentId: 3500001,
    name: '红字回冲单',
  }),

  createPlatformMenu({
    id: 3502001,
    path: 'YuShouChongYingShouLieBiao',
    componentName: 'YingFuZhangXXYuShouChongYingShouLieBiao',
    component: '/platforms/YingFuZhang/YuShouChongYingShouLieBiao/layouts/RouteCache',
    parentId: 3500002,
    name: '预付冲应付列表',
  }),


  createPlatformMenu({
    id: 3502002,
    path: 'YingShouChongYingShouLieBiao',
    componentName: 'YingFuZhangXXYingShouChongYingShouLieBiao',
    component: '/platforms/YingFuZhang/YingShouChongYingShouLieBiao/layouts/RouteCache',
    parentId: 3500002,
    name: '应付冲应付列表',
  }),


  createPlatformMenu({
    id: 3502003,
    path: 'YingShouChongShouFuLieBiao',
    componentName: 'YingFuZhangXXYingShouChongShouFuLieBiao',
    component: '/platforms/YingFuZhang/YingShouChongShouFuLieBiao/layouts/RouteCache',
    parentId: 3500002,
    name: '应付冲应收列表',
  }),
  createPlatformMenu({
    id: 3502004,
    path: 'CxHongZiHuiChongList',
    componentName: 'YingFuZhangXXCxHongZiHuiChongList',
    component: '/platforms/YingFuZhang/HongZiHuiChongDanLieBiao/layouts/RouteCache',
    parentId: 3500002,
    name: '红字回冲单列表',
  }),


  createPlatformMenu({
    id: 3601001,
    path: 'YingShouKuanZhiDan',
    componentName: 'YingFuZhangXXYingShouKuanZhiDan',
    component: '/platforms/YingFuZhang/YingShouKuanZhiDan/layouts/RouteCache',
    parentId: 3600001,
    name: '应付款制单',
  }),


  createPlatformMenu({
    id: 3601002,
    path: 'ShouKuanDanZhiDan',
    componentName: 'YingFuZhangXXShouKuanDanZhiDan',
    component: '/platforms/YingFuZhang/ShouKuanDanZhiDan/layouts/RouteCache',
    parentId: 3600001,
    name: '付款单制单',
  }),
  createPlatformMenu({
    id: 3601003,
    path: 'HeXiaoZhiDan',
    componentName: 'YingFuZhangXXHeXiaoZhiDan',
    component: '/platforms/YingFuZhang/ShouKuanDanZhiDan/layouts/RouteCache',
    parentId: 3600001,
    name: '核销制单',
  }),


  createPlatformMenu({
    id: 3601004,
    path: 'HongZiHuiChongZhiDan',
    componentName: 'YingFuZhangXXHongZiHuiChongZhiDan',
    component: '/platforms/YingFuZhang/ShouKuanDanZhiDan/layouts/RouteCache',
    parentId: 3600001,
    name: '红字回冲单制单',
  }),

  createPlatformMenu({
    id: 3602001,
    path: 'PingZhengLieBiao',
    componentName: 'YingFuZhangXXPingZhengLieBiao',
    component: '/platforms/YingFuZhang/PingZhengLieBiao/layouts/RouteCache',
    parentId: 3600002,
    name: '凭证列表',
  }),

  /*createPlatformMenu({
    id: 3603001,
    path: 'YingShouZhongZhang',
    componentName: 'YingFuZhangXXYingShouZhongZhang',
    component: '/platforms/YingFuZhang/XiaoShouWuLiuDanLieBiao/layouts/RouteCache',
    parentId: 3600003,
    name: '应付总账',
  }),
  createPlatformMenu({
    id: 3603002,
    path: 'YingShouMingXiZhang',
    componentName: 'YingFuZhangXXXiaoShouWuLiuDanLieBiao',
    component: '/platforms/YingFuZhang/XiaoShouWuLiuDanLieBiao/layouts/RouteCache',
    parentId: 3600003,
    name: '应付明细账',
  }),*/




  createPlatformMenu({
    id: 3701001,
    path: 'YingShouTongJiBiao',
    componentName: 'YingFuZhangYingShouTongJiBiao',
    component: '/platforms/YingFuZhang/YingShouKuanTongJiBiao/layouts/RouteCache',
    parentId: 3700001,
    name: '应付款统计表',
  }),
  createPlatformMenu({
    id: 3701002,
    path: 'ShouTongJiBiao',
    componentName: 'YingFuZhangShouTongJiBiao',
    component: '/platforms/YingFuZhang/ShouKuanTongJiBiao/layouts/RouteCache',
    parentId: 3700001,
    name: '付款统计表',
  }),
  createPlatformMenu({
    id: 3701003,
    path: 'XiaoShouFaPTongJiBiao',
    componentName: 'XiaoShouFaPTongJiBiao',
    component: '/platforms/YingFuZhang/YingShouYuEBiao/layouts/RouteCache',
    parentId: 3700001,
    name: '采购发票统计表',
  }),
  createPlatformMenu({
    id: 3701004,
    path: 'FeiYongTongJiBiao',
    componentName: 'YingFuZhangXXFeiYongTongJiBiao',
    component: '/platforms/YingFuZhang/YingShouYuEBiao/layouts/RouteCache',
    parentId: 3700001,
    name: '费用统计表',
  }),

  createPlatformMenu({
    id: 3702001,
    path: 'YingShouMingXiBiao',
    componentName: 'YingShouZhangXXYingShouMingXiBiao',
    component: '/platforms/YingFuZhang/YingShouMingXiBiao/layouts/RouteCache',
    parentId: 3700002,
    name: '应付明细表',
  }),

  createPlatformMenu({
    id: 3702002,
    path: 'ShouKuanMingXiZhang',
    componentName: 'YingFuZhangXXShouKuanMingXiZhang',
    component: '/platforms/YingFuZhang/ShouKuanMingXiZhang/layouts/RouteCache',
    parentId: 3700002,
    name: '付款明细表',
  }),

  createPlatformMenu({
    id: 3703002,
    path: 'YingShouZongZhang',
    componentName: 'YingFuZhangXXYingShouZongZhang',
    component: '/platforms/YingFuZhang/YingShouZongZhang/layouts/RouteCache',
    parentId: 3700003,
    name: '应付总账',
  }),
  createPlatformMenu({
    id: 3703003,
    path: 'YingShouMingXiZhang',
    componentName: 'YingShouZhangXXYingShouMingXiZhang',
    component: '/platforms/YingFuZhang/YingShouMingXiZhang/layouts/RouteCache',
    parentId: 3700003,
    name: '应付明细账',
  }),

  createPlatformMenu({
    id: 3702003,
    path: 'YingShouHeXiMingXiZhang',
    componentName: 'YingFuZhangXXYingShouHeXiMingXiZhang',
    component: '/platforms/YingFuZhang/YingShouHeXiaoMingXiBiao/layouts/RouteCache',
    parentId: 3700003,
    name: '应付核销情况表',
  }),

  createPlatformMenu({
    id: 3703004,
    path: 'ShouKuanZhiXingBiao',
    componentName: 'YingFuZhangXXShouKuanZhiXingBiao',
    component: '/platforms/YingFuZhang/ShouKuanZhiXingBiao/layouts/RouteCache',
    parentId: 3700003,
    name: '应付付款执行表',
  }),


  createPlatformMenu({
    id: 3704001,
    path: 'YingFuZhangLingFenXi',
    componentName: 'YingFuZhangXXYingFuZhangLingFenXi',
    component: '/platforms/YingFuZhang/YingShouZhangLingFenXi/layouts/RouteCache',
    parentId: 3700004,
    name: '应付账龄分析',
  }),


  createPlatformMenu({
    id: 3704002,
    path: 'ZiJinYuCeFenXi',
    componentName: 'YingFuZhangXXZiJinYuCeFenXi',
    component: '/platforms/YingFuZhang/ZiJinYuCeFenXi/layouts/RouteCache',
    parentId: 3700004,
    name: '资金预测分析',
  }),


  createPlatformMenu({
    id: 3801001,
    path: 'YueMoJieZhang',
    componentName: 'YingFuZhangXXYueMoJieZhang',
    component: '/platforms/YingFuZhang/YueMoJieZhang/layouts/RouteCache',
    parentId: 3800001,
    name: '月末结账',
  }),


  createPlatformMenu({
    id: 3801002,
    path: 'QuXiaoJieZhang',
    componentName: 'YingFuZhangXXQuXiaoJieZhang',
    component: '/platforms/YingFuZhang/QuXiaoJieZhang/layouts/RouteCache',
    parentId: 3800002,
    name: '取消结账',
  }),


  createPlatformMenu({
    id: 3901001,
    path: 'XiTongXuanXiang',
    componentName: 'YingFuZhangXXXiTongXuanXiang',
    component: '/platforms/YingFuZhang/XiTongXuanXiang/layouts/RouteCache',
    parentId: 3900001,
    name: '系统参数',
  }),

  /*
  
    createPlatformMenu({
      id: 3901002,
      path: 'YeWuLiuCheng',
      componentName: 'YingFuZhangXXYeWuLiuCheng',
      component: '/platforms/YingFuZhang/YeWuLiuCheng/layouts/RouteCache',
      parentId: 3900001,
      name: '业务流程',
    }),
  */


  createPlatformMenu({
    id: 3901002,
    path: 'ShouKuanZhangHu',
    componentName: 'YingFuZhangXXShouKuanZhangHu',
    component: '/platforms/YingFuZhang/ShouKuanZhangHu/layouts/RouteCache',
    parentId: 3900002,
    name: '科目设置',
  }),

  /*
    createPlatformMenu({
      id: 3902002,
      path: 'YeWuLiuCheng',
      componentName: 'YingFuZhangXXYeWuLiuCheng',
      component: '/platforms/YingFuZhang/JieSuanFangShi/layouts/RouteCache',
      parentId: 3900002,
      name: '业务流程',
    }),*/


  /*createPlatformMenu({
    id: 3901003,
    path: 'FeiYongDangAn',
    componentName: 'YingFuZhangXXFeiYongDangAn',
    component: '/platforms/YingFuZhang/FeiYongDangAn/layouts/RouteCache',
    parentId: 3900002,
    name: '费用档案',
  }),


  createPlatformMenu({
    id: 3903001,
    path: 'YingShouKeMuSheZhi',
    componentName: 'YingFuZhangXXYingShouKeMuSheZhi',
    component: '/platforms/YingFuZhang/YingShouKeMuSheZhi/layouts/RouteCache',
    parentId: 3900003,
    name: '应付科目设置',
  }),

  createPlatformMenu({
    id: 3903002,
    path: 'YuShouKeMuSheZhi',
    componentName: 'YingFuZhangXXYuShouKeMuSheZhi',
    component: '/platforms/YingFuZhang/YingShouKeMuSheZhi/layouts/RouteCache',
    parentId: 3900003,
    name: '预付科目设置',
  }),


  createPlatformMenu({
    id: 3901003,
    path: 'ShouKuanKeMuSheZhi',
    componentName: 'YingFuZhangXXShouKuanKeMuSheZhi',
    component: '/platforms/YingFuZhang/ShouKuanKeMuSheZhi/layouts/RouteCache',
    parentId: 3900003,
    name: '付款科目设置',
  }),


  createPlatformMenu({
    id: 3901004,
    path: 'HeXiaoKeMuSheZhi',
    componentName: 'YingFuZhangXXHeXiaoKeMuSheZhi',
    component: '/platforms/YingFuZhang/HeXiaoKeMuSheZhi/layouts/RouteCache',
    parentId: 3900003,
    name: '核销科目设置',
  }),

  createPlatformMenu({
    id: 3901005,
    path: 'XianJinKeMuSheZhi',
    componentName: 'YingFuZhangXXXianJinKeMuSheZhi',
    component: '/platforms/YingFuZhang/HeXiaoKeMuSheZhi/layouts/RouteCache',
    parentId: 3900003,
    name: '现金科目设置',
  }),
  createPlatformMenu({
    id: 3901006,
    path: 'YingHangKeMuSheZhi',
    componentName: 'YingFuZhangXXYingHangKeMuSheZhi',
    component: '/platforms/YingFuZhang/HeXiaoKeMuSheZhi/layouts/RouteCache',
    parentId: 3900003,
    name: '银行科目设置',
  }),
  createPlatformMenu({
    id: 3901007,
    path: 'ZheKouKeMuSheZhi',
    componentName: 'YingFuZhangXXZheKouKeMuSheZhi',
    component: '/platforms/YingFuZhang/HeXiaoKeMuSheZhi/layouts/RouteCache',
    parentId: 3900003,
    name: '折扣科目设置',
  }),*/

];

export const platformMenuIds = {
  key:'yingFuZhang',
  id:platformId,
  sort:8,
  menuIds:menus.map(item=>item.id)
};

