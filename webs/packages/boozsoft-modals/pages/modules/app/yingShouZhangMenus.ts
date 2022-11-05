// 应收账
import { toRoleMenuList } from '../../utils/_sys_role_menu_util';
import { createMenu, createModel} from '/@/../pages/rbac/utils/mockMenuUtil';
import {
  PLATFORM_DevComponents,
  PLATFORM_YINGSHOUZHANG, usePlatform
} from '../platforms/platformMenus';
const platformId=1007
function createPlatformMenu(params){
  return createMenu({...params,platformId})
}
export const menus = [
  createModel({
    id: platformId,
    isCloud: false,
    isTargetBlank: false,
    isOutLink: false,
    name: '应收账',
    category: 102,
    sortNo: 5,
  }),
  createPlatformMenu({
    id: 3000000,
    path: '/',
    redirect:'/YingShouZhang/home/welcome',
    component: 'LAYOUT',
    parentId: 0,
    hidden:true,
    name: '根目录'
  }),
  createPlatformMenu({
    id: 3000011,
    path: 'welcome',
    // component: '/platforms/YingShouZhang/Home/layouts/RouteCache',
    component: '/dashboard/analysis/index',
    parentId: 3000010,
    name: '首页',
  }),
  createPlatformMenu({ id: 3000010, path: '/YingShouZhang/home', component: 'LAYOUT', parentId: 0, name: '工作台' }),
  /** 一级目录 **/
  createPlatformMenu({
    id: 3100000,
    component: 'LAYOUT',
    path: '/YingShouZhang/QiChu',
    parentId: 0,
    name: '期初'
  }),


  createPlatformMenu({
    id: 3200000,
    component: 'LAYOUT',
    path: '/YingShouZhang/YingShou',
    parentId: 0,
    name: '应收'
  }),


  createPlatformMenu({
    id: 3300000,
    component: 'LAYOUT',
    path: '/YingShouZhang/ShouKuan',
    parentId: 0,
    name: '收款'
  }),


  createPlatformMenu({
    id: 3400000,
    component: 'LAYOUT',
    path: '/YingShouZhang/HeXiao',
    parentId: 0,
    name: '核销'
  }),


  createPlatformMenu({
    id: 3500000,
    component: 'LAYOUT',
    path: '/YingShouZhang/ChongXiao',
    parentId: 0,
    name: '冲销'
  }),


  createPlatformMenu({
    id: 3600000,
    component: 'LAYOUT',
    path: '/YingShouZhang/PingZheng',
    parentId: 0,
    name: '凭证'
  }),


  createPlatformMenu({
    id: 3700000,
    component: 'LAYOUT',
    path: '/YingShouZhang/ZhangBiao',
    parentId: 0,
    name: '账表'
  }),


  createPlatformMenu({
    id: 3800000,
    component: 'LAYOUT',
    path: '/YingShouZhang/JieZhang',
    parentId: 0,
    name: '结账'
  }),


  createPlatformMenu({
    id: 3900000,
    component: 'LAYOUT',
    path: '/YingShouZhang/SheZhi',
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
    name: '应收复核',
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
    name: '操作'
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
    componentName: 'YingShouZhangXXYingShouKuanQiChuYuE',
    component: '/platforms/YingShouZhang/YingShouKuanQiChuYuE/layouts/RouteCache',
    parentId: 3101000,
    name: '期初余额',
  }),

  createPlatformMenu({
    id: 3101002,
    path: 'YingShouKuanQiChuYuEJieZhuan',
    componentName: 'YingShouZhangXXYingShouKuanQiChuYuEJieZhuan',
    component: '/platforms/YingShouZhang/YingShouKuanQiChuYuEJieZhuan/layouts/RouteCache',
    parentId: 3000000,
    name: '结转上年',
  }),

  createPlatformMenu({
    id: 3203001,
    path: 'XiaoHuoDanFuHe',
    component: '/platforms/YingShouZhang/XiaoHuoDanFuHe/layouts/RouteCache',
    parentId: 3201003,
    name: '销货单复核'
  }),
  createPlatformMenu({
    id: 3203002,
    path: 'XiaoShouFaPiaoFuHe',
    component: '/platforms/YingShouZhang/XiaoShouFaPiaoFuHe/layouts/RouteCache',
    parentId: 3201003,
    name: '销售发票复核'
  }),

  createPlatformMenu({
    id: 3201001,
    path: 'YingShouDan',
    componentName: 'YingShouZhangXXYingShouDan',
    component: '/platforms/YingShouZhang/YingShouDan/layouts/RouteCache',
    parentId: 3200001,
    name: '应收单',
  }),


  /*createPlatformMenu({
    id: 3201002,
    path: 'XiaoShouFaPiao',
    componentName: 'YingShouZhangXXXiaoShouFaPiao',
    component: '/platforms/YingShouZhang/XiaoShouFaPiao/layouts/RouteCache',
    parentId: 3200001,
    name: '销售发票',
  }),*/


  createPlatformMenu({
    id: 3201003,
    path: 'FeiYongDan',
    componentName: 'YingShouZhangXXFeiYongDan',
    component: '/platforms/YingShouZhang/FeiYongDan/layouts/RouteCache',
    parentId: 3200001,
    name: '费用单',
  }),


  createPlatformMenu({
    id: 3202001,
    path: 'YingShouDanLieBiao',
    componentName: 'YingShouZhangXXYingShouDanLieBiao',
    component: '/platforms/YingShouZhang/YingShouDanLieBiao/layouts/RouteCache',
    parentId: 3200002,
    name: '应收单列表',
  }),


  /*createPlatformMenu({
    id: 3202002,
    path: 'XiaoShouFaPiaoLieBiao',
    componentName: 'YingShouZhangXXXiaoShouFaPiaoLieBiao',
    component: '/platforms/YingShouZhang/XiaoShouFaPiaoLieBiao/layouts/RouteCache',
    parentId: 3200002,
    name: '销售发票列表',
  }),*/


  createPlatformMenu({
    id: 3202003,
    path: 'FeiYongDanLieBiao',
    componentName: 'YingShouZhangXXFeiYongDanLieBiao',
    component: '/platforms/YingShouZhang/FeiYongDanLieBiao/layouts/RouteCache',
    parentId: 3200002,
    name: '费用单列表',
  }),


 /* createPlatformMenu({
    id: 3203001,
    path: 'YingShouKuanTongJiBiao',
    componentName: 'YingShouZhangXXYingShouKuanTongJiBiao',
    component: '/platforms/YingShouZhang/YingShouKuanTongJiBiao/layouts/RouteCache',
    parentId: 3200003,
    name: '应收款统计表',
  }),


  createPlatformMenu({
    id: 3203002,
    path: 'XiaoShouFaPiaoTongJiBiao',
    componentName: 'YingShouZhangXXXiaoShouFaPiaoTongJiBiao',
    component: '/platforms/YingShouZhang/XiaoShouFaPiaoTongJiBiao/layouts/RouteCache',
    parentId: 3200003,
    name: '销售发票统计表',
  }),


  createPlatformMenu({
    id: 3203003,
    path: 'FeiYongTongJiBiao',
    componentName: 'YingShouZhangXXFeiYongTongJiBiao',
    component: '/platforms/YingShouZhang/FeiYongTongJiBiao/layouts/RouteCache',
    parentId: 3200003,
    name: '费用统计表',
  }),


  createPlatformMenu({
    id: 3203004,
    path: 'YingShouYuEBiao',
    componentName: 'YingShouZhangXXYingShouYuEBiao',
    component: '/platforms/YingShouZhang/YingShouYuEBiao/layouts/RouteCache',
    parentId: 3200003,
    name: '应收余额表',
  }),*/

/*

  createPlatformMenu({
    id: 3301001,
    path: 'XuanZeShouKuan',
    componentName: 'YingShouZhangXXXuanZeShouKuan',
    component: '/platforms/YingShouZhang/XuanZeShouKuan/layouts/RouteCache',
    parentId: 3300001,
    name: '选择收款',
  }),
*/


  createPlatformMenu({
    id: 3301002,
    path: 'YingShouShouKuanDan',
    componentName: 'YingShouZhangXXYingShouShouKuanDan',
    component: '/platforms/YingShouZhang/YingShouShouKuanDan/layouts/RouteCache',
    parentId: 3300001,
    name: '收款单',
  }),


  createPlatformMenu({
    id: 3301003,
    path: 'YingShouTuiKuanDan',
    componentName: 'YingShouZhangXXYingShouTuiKuanDan',
    component: '/platforms/YingShouZhang/YingShouTuiKuanDan/layouts/RouteCache',
    parentId: 3300001,
    name: '退款单',
  }),


  createPlatformMenu({
    id: 3302001,
    path: 'ShouKuanDanLieBiao',
    componentName: 'YingShouZhangXXShouKuanDanLieBiao',
    component: '/platforms/YingShouZhang/ShouKuanDanLieBiao/layouts/RouteCache',
    parentId: 3300002,
    name: '收(退)款单列表',
  }),


  /*createPlatformMenu({
    id: 3302002,
    path: 'TuiKuanDanLieBiao',
    componentName: 'YingShouZhangXXTuiKuanDanLieBiao',
    component: '/platforms/YingShouZhang/TuiKuanDanLieBiao/layouts/RouteCache',
    parentId: 3300002,
    name: '退款单列表',
  }),*/


  /*createPlatformMenu({
    id: 3303002,
    path: 'ShouKuanMingXiBiao',
    componentName: 'YingShouZhangXXShouKuanMingXiBiao',
    component: '/platforms/YingShouZhang/ShouKuanMingXiBiao/layouts/RouteCache',
    parentId: 3300003,
    name: '收款明细表',
  }),


  createPlatformMenu({
    id: 3303001,
    path: 'ShouKuanTongJiBiao',
    componentName: 'YingShouZhangXXShouKuanTongJiBiao',
    component: '/platforms/YingShouZhang/ShouKuanTongJiBiao/layouts/RouteCache',
    parentId: 3300003,
    name: '收款统计表',
  }),
  createPlatformMenu({
    id: 3303003,
    path: 'YingShouHeXiaoMxBiao',
    componentName: 'YingShouZhangXXYingShouHeXiaoMingXiBiao',
    component: '/platforms/YingShouZhang/YingShouHeXiaoMingXiBiao/layouts/RouteCache',
    parentId: 3300003,
    name: '应收核销明细表',
  }),*/


  createPlatformMenu({
    id: 3401001,
    path: 'ShouGongHeXiao',
    componentName: 'YingShouZhangXXShouGongHeXiao',
    component: '/platforms/YingShouZhang/ShouGongHeXiao/layouts/RouteCache',
    parentId: 3400001,
    name: '手工核销',
  }),

  createPlatformMenu({
    id: 3401003,
    path: 'ZiDongHeXiao',
    componentName: 'YingShouZhangXXZiDongHeXiao',
    component: '/platforms/YingShouZhang/ZiDongHeXiao/layouts/RouteCache',
    parentId: 3400001,
    name: '自动核销',
  }),

  createPlatformMenu({
    id: 3401002,
    path: 'QuXiaoHeXiao',
    componentName: 'YingShouZhangXXQuXiaoHeXiao',
    component: '/platforms/YingShouZhang/QuXiaoHeXiao/layouts/RouteCache',
    parentId: 3400003,
    name: '取消核销',
  }),

/*  createPlatformMenu({
    id: 3401003,
    path: 'HongZiHuiChong',
    componentName: 'YingShouZhangXXHongZiHuiChong',
    component: '/platforms/YingShouZhang/QuXiaoHeXiao/layouts/RouteCache',
    parentId: 3400001,
    name: '红字回冲单',
  }),*/

/*  createPlatformMenu({
    id: 3402001,
    path: 'HongZiHuiChongList',
    componentName: 'YingShouZhangXXHongZiHuiChongList',
    component: '/platforms/YingShouZhang/YingShouHeXiaoMingXiBiao/layouts/RouteCache',
    parentId: 3400002,
    name: '红字回冲单列表',
  }),*/
  /*createPlatformMenu({
    id: 3403001,
    path: 'YingShouHeXiaoMingXiBiao',
    componentName: 'YingShouZhangXXYingShouHeXiaoMingXiBiao',
    component: '/platforms/YingShouZhang/YingShouHeXiaoMingXiBiao/layouts/RouteCache',
    parentId: 3400003,
    name: '应收核销明细表',
  }),*/


  createPlatformMenu({
    id: 3501001,
    path: 'YuShouChongYingShou',
    componentName: 'YingShouZhangXXYuShouChongYingShou',
    component: '/platforms/YingShouZhang/YuShouChongYingShou/layouts/RouteCache',
    parentId: 3500001,
    name: '预收冲应收',
  }),


  createPlatformMenu({
    id: 3501002,
    path: 'YingShouChongYingShou',
    componentName: 'YingShouZhangXXYingShouChongYingShou',
    component: '/platforms/YingShouZhang/YingShouChongYingShou/layouts/RouteCache',
    parentId: 3500001,
    name: '应收冲应收',
  }),


  createPlatformMenu({
    id: 3501003,
    path: 'YingShouChongShouFu',
    componentName: 'YingShouZhangXXYingShouChongShouFu',
    component: '/platforms/YingShouZhang/YingShouChongShouFu/layouts/RouteCache',
    parentId: 3500001,
    name: '应收冲应付',
  }),
  createPlatformMenu({
    id: 3501004,
    path: 'CxHongZiHuiChong',
    componentName: 'YingShouZhangXXCXHongZiHuiChong',
    component: '/platforms/YingShouZhang/HongZiHuiChongDan/layouts/RouteCache',
    parentId: 3500001,
    name: '红字回冲单',
  }),

  createPlatformMenu({
    id: 3502001,
    path: 'YuShouChongYingShouLieBiao',
    componentName: 'YingShouZhangXXYuShouChongYingShouLieBiao',
    component: '/platforms/YingShouZhang/YuShouChongYingShouLieBiao/layouts/RouteCache',
    parentId: 3500002,
    name: '预收冲应收列表',
  }),


  createPlatformMenu({
    id: 3502002,
    path: 'YingShouChongYingShouLieBiao',
    componentName: 'YingShouZhangXXYingShouChongYingShouLieBiao',
    component: '/platforms/YingShouZhang/YingShouChongYingShouLieBiao/layouts/RouteCache',
    parentId: 3500002,
    name: '应收冲应收列表',
  }),


  createPlatformMenu({
    id: 3502003,
    path: 'YingShouChongShouFuLieBiao',
    componentName: 'YingShouZhangXXYingShouChongShouFuLieBiao',
    component: '/platforms/YingShouZhang/YingShouChongShouFuLieBiao/layouts/RouteCache',
    parentId: 3500002,
    name: '应收冲应付列表',
  }),
  createPlatformMenu({
    id: 3502004,
    path: 'CxHongZiHuiChongList',
    componentName: 'YingShouZhangXXCxHongZiHuiChongList',
    component: '/platforms/YingShouZhang/HongZiHuiChongDanLieBiao/layouts/RouteCache',
    parentId: 3500002,
    name: '红字回冲单列表',
  }),


  /*createPlatformMenu({
    id: 3601001,
    path: 'YingShouKuanZhiDan',
    componentName: 'YingShouZhangXXYingShouKuanZhiDan',
    component: '/platforms/YingShouZhang/YingShouKuanZhiDan/layouts/RouteCache',
    parentId: 3600001,
    name: '应收款制单',
  }),


  createPlatformMenu({
    id: 3601002,
    path: 'ShouKuanDanZhiDan',
    componentName: 'YingShouZhangXXShouKuanDanZhiDan',
    component: '/platforms/YingShouZhang/ShouKuanDanZhiDan/layouts/RouteCache',
    parentId: 3600001,
    name: '收款单制单',
  }),
  createPlatformMenu({
    id: 3601003,
    path: 'HeXiaoZhiDan',
    componentName: 'YingShouZhangXXHeXiaoZhiDan',
    component: '/platforms/YingShouZhang/ShouKuanDanZhiDan/layouts/RouteCache',
    parentId: 3600001,
    name: '核销制单',
  }),


  createPlatformMenu({
    id: 3601004,
    path: 'HongZiHuiChongZhiDan',
    componentName: 'YingShouZhangXXHongZiHuiChongZhiDan',
    component: '/platforms/YingShouZhang/ShouKuanDanZhiDan/layouts/RouteCache',
    parentId: 3600001,
    name: '红字回冲单制单',
  }),*/
  createPlatformMenu({
    id: 3601001,
    path: 'YingShouKuanZhiDan',
    componentName: 'YingShouZhangXXYingShouKuanZhiDan',
    component: '/platforms/YingShouZhang/YingShouKuanZhiDan/layouts/RouteCache',
    parentId: 3600001,
    name: '业务单据制单',
  }),

  createPlatformMenu({
    id: 3602001,
    path: 'PingZhengLieBiao',
    componentName: 'YingShouZhangXXPingZhengLieBiao',
    component: '/platforms/YingShouZhang/PingZhengLieBiao/layouts/RouteCache',
    parentId: 3600002,
    name: '凭证列表',
  }),

  /*createPlatformMenu({
    id: 3603001,
    path: 'YingShouZhongZhang',
    componentName: 'YingShouZhangXXYingShouZhongZhang',
    component: '/platforms/YingShouZhang/XiaoShouWuLiuDanLieBiao/layouts/RouteCache',
    parentId: 3600003,
    name: '应收总账',
  }),
  createPlatformMenu({
    id: 3603002,
    path: 'YingShouMingXiZhang',
    componentName: 'YingShouZhangXXXiaoShouWuLiuDanLieBiao',
    component: '/platforms/YingShouZhang/XiaoShouWuLiuDanLieBiao/layouts/RouteCache',
    parentId: 3600003,
    name: '应收明细账',
  }),*/




  createPlatformMenu({
    id: 3701001,
    path: 'YingShouTongJiBiao',
    componentName: 'YingShouZhangXXYingShouKuanTongJiBiao',
    component: '/platforms/YingShouZhang/YingShouKuanTongJiBiao/layouts/RouteCache',
    parentId: 3700001,
    name: '应收款统计表',
  }),
  createPlatformMenu({
    id: 3701002,
    path: 'ShouTongJiBiao',
    componentName: 'YingShouZhangXXShouKuanTongJiBiao',
    component: '/platforms/YingShouZhang/ShouKuanTongJiBiao/layouts/RouteCache',
    parentId: 3700001,
    name: '收款统计表',
  }),
  createPlatformMenu({
    id: 3701003,
    path: 'XiaoShouFaPTongJiBiao',
    componentName: 'XiaoShouFaPTongJiBiao',
    component: '/platforms/YingShouZhang/YingShouYuEBiao/layouts/RouteCache',
    parentId: 3700001,
    name: '销售发票统计表',
  }),
  createPlatformMenu({
    id: 3701004,
    path: 'FeiYongTongJiBiao',
    componentName: 'YingShouZhangXXFeiYongTongJiBiao',
    component: '/platforms/YingShouZhang/YingShouYuEBiao/layouts/RouteCache',
    parentId: 3700001,
    name: '费用统计表',
  }),

  createPlatformMenu({
    id: 3702001,
    path: 'YingShouMingXiBiao',
    componentName: 'YingShouZhangXXYingShouMingXiBiao',
    component: '/platforms/YingShouZhang/YingShouMingXiBiao/layouts/RouteCache',
    parentId: 3700002,
    name: '应收明细表',
  }),

  createPlatformMenu({
    id: 3702002,
    path: 'ShouKuanMingXiZhang',
    componentName: 'YingShouZhangXXShouKuanMingXiZhang',
    component: '/platforms/YingShouZhang/ShouKuanMingXiZhang/layouts/RouteCache',
    parentId: 3700002,
    name: '收款明细表',
  }),

  createPlatformMenu({
    id: 3703002,
    path: 'YingShouZongZhang',
    componentName: 'YingShouZhangXXYingShouZongZhang',
    component: '/platforms/YingShouZhang/YingShouZongZhang/layouts/RouteCache',
    parentId: 3700003,
    name: '应收总账',
  }),
  createPlatformMenu({
    id: 3703003,
    path: 'YingShouMingXiZhang',
    componentName: 'YingShouZhangXXYingShouMingXiZhang',
    component: '/platforms/YingShouZhang/YingShouMingXiZhang/layouts/RouteCache',
    parentId: 3700003,
    name: '应收明细账',
  }),

  createPlatformMenu({
    id: 3702003,
    path: 'YingShouHeXiMingXiZhang',
    componentName: 'YingShouZhangXXYingShouHeXiMingXiZhang',
    component: '/platforms/YingShouZhang/YingShouHeXiaoMingXiBiao/layouts/RouteCache',
    parentId: 3700003,
    name: '应收核销情况表',
  }),

  createPlatformMenu({
    id: 3703004,
    path: 'ShouKuanZhiXingBiao',
    componentName: 'YingShouZhangXXShouKuanZhiXingBiao',
    component: '/platforms/YingShouZhang/ShouKuanZhiXingBiao/layouts/RouteCache',
    parentId: 3700003,
    name: '应收收款执行表',
  }),


  createPlatformMenu({
    id: 3704001,
    path: 'YingShouZhangLingFenXi',
    componentName: 'YingShouZhangXXYingShouZhangLingFenXi',
    component: '/platforms/YingShouZhang/YingShouZhangLingFenXi/layouts/RouteCache',
    parentId: 3700004,
    name: '应收账龄分析',
  }),


  createPlatformMenu({
    id: 3704002,
    path: 'ZiJinYuCeFenXi',
    componentName: 'YingShouZhangXXZiJinYuCeFenXi',
    component: '/platforms/YingShouZhang/ZiJinYuCeFenXi/layouts/RouteCache',
    parentId: 3700004,
    name: '资金预测分析',
  }),


  createPlatformMenu({
    id: 3801001,
    path: 'YueMoJieZhang',
    componentName: 'YingShouZhangXXYueMoJieZhang',
    component: '/platforms/YingShouZhang/YueMoJieZhang/layouts/RouteCache',
    parentId: 3800001,
    name: '月末结账',
  }),


  createPlatformMenu({
    id: 3801002,
    path: 'QuXiaoJieZhang',
    componentName: 'YingShouZhangXXQuXiaoJieZhang',
    component: '/platforms/YingShouZhang/QuXiaoJieZhang/layouts/RouteCache',
    parentId: 3800002,
    name: '取消结账',
  }),


  createPlatformMenu({
    id: 3901001,
    path: 'XiTongXuanXiang',
    componentName: 'YingShouZhangXXXiTongXuanXiang',
    component: '/platforms/YingShouZhang/XiTongXuanXiang/layouts/RouteCache',
    parentId: 3900001,
    name: '系统参数',
  }),

/*

  createPlatformMenu({
    id: 3901002,
    path: 'YeWuLiuCheng',
    componentName: 'YingShouZhangXXYeWuLiuCheng',
    component: '/platforms/YingShouZhang/YeWuLiuCheng/layouts/RouteCache',
    parentId: 3900001,
    name: '业务流程',
  }),
*/


  createPlatformMenu({
    id: 3901002,
    path: 'ShouKuanZhangHu',
    componentName: 'YingShouZhangXXShouKuanZhangHu',
    component: '/platforms/YingShouZhang/ShouKuanZhangHu/layouts/RouteCache',
    parentId: 3900002,
    name: '科目设置',
  }),

/*
  createPlatformMenu({
    id: 3902002,
    path: 'YeWuLiuCheng',
    componentName: 'YingShouZhangXXYeWuLiuCheng',
    component: '/platforms/YingShouZhang/JieSuanFangShi/layouts/RouteCache',
    parentId: 3900002,
    name: '业务流程',
  }),*/


  /*createPlatformMenu({
    id: 3901003,
    path: 'FeiYongDangAn',
    componentName: 'YingShouZhangXXFeiYongDangAn',
    component: '/platforms/YingShouZhang/FeiYongDangAn/layouts/RouteCache',
    parentId: 3900002,
    name: '费用档案',
  }),


  createPlatformMenu({
    id: 3903001,
    path: 'YingShouKeMuSheZhi',
    componentName: 'YingShouZhangXXYingShouKeMuSheZhi',
    component: '/platforms/YingShouZhang/YingShouKeMuSheZhi/layouts/RouteCache',
    parentId: 3900003,
    name: '应收科目设置',
  }),

  createPlatformMenu({
    id: 3903002,
    path: 'YuShouKeMuSheZhi',
    componentName: 'YingShouZhangXXYuShouKeMuSheZhi',
    component: '/platforms/YingShouZhang/YingShouKeMuSheZhi/layouts/RouteCache',
    parentId: 3900003,
    name: '预收科目设置',
  }),


  createPlatformMenu({
    id: 3901003,
    path: 'ShouKuanKeMuSheZhi',
    componentName: 'YingShouZhangXXShouKuanKeMuSheZhi',
    component: '/platforms/YingShouZhang/ShouKuanKeMuSheZhi/layouts/RouteCache',
    parentId: 3900003,
    name: '收款科目设置',
  }),


  createPlatformMenu({
    id: 3901004,
    path: 'HeXiaoKeMuSheZhi',
    componentName: 'YingShouZhangXXHeXiaoKeMuSheZhi',
    component: '/platforms/YingShouZhang/HeXiaoKeMuSheZhi/layouts/RouteCache',
    parentId: 3900003,
    name: '核销科目设置',
  }),

  createPlatformMenu({
    id: 3901005,
    path: 'XianJinKeMuSheZhi',
    componentName: 'YingShouZhangXXXianJinKeMuSheZhi',
    component: '/platforms/YingShouZhang/HeXiaoKeMuSheZhi/layouts/RouteCache',
    parentId: 3900003,
    name: '现金科目设置',
  }),
  createPlatformMenu({
    id: 3901006,
    path: 'YingHangKeMuSheZhi',
    componentName: 'YingShouZhangXXYingHangKeMuSheZhi',
    component: '/platforms/YingShouZhang/HeXiaoKeMuSheZhi/layouts/RouteCache',
    parentId: 3900003,
    name: '银行科目设置',
  }),
  createPlatformMenu({
    id: 3901007,
    path: 'ZheKouKeMuSheZhi',
    componentName: 'YingShouZhangXXZheKouKeMuSheZhi',
    component: '/platforms/YingShouZhang/HeXiaoKeMuSheZhi/layouts/RouteCache',
    parentId: 3900003,
    name: '折扣科目设置',
  }),*/

];

export const platformMenuIds = {
  key:'yingShouZhang',
  id:platformId,
  sort:7,
  menuIds:menus.map(item=>item.id)
};

