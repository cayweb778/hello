// 银行对账
import { createMenu, createModel} from '/@/../pages/rbac/utils/mockMenuUtil';
import {PLATFORM_XIANJINYINHANG, usePlatform} from '../platforms/platformMenus';
const platformId=1006
function createPlatformMenu(params){
  return createMenu({...params,platformId})
}
export const menus = [

  createModel({
    id: platformId,
    isCloud: false,
    isTargetBlank: false,
    isOutLink: false,
    name: '现金银行',
    category: 102,
    sortNo: 2,
    path:'/xianJinYinHang',
  }),
  createPlatformMenu({
    id: 9909,
    path: '/',
    redirect:'/xianJinYinHang/home/welcome',
    component: 'LAYOUT',
    parentId: 0,
    hidden:true,
    name: '根目录'
  }),
  // 期初
  createPlatformMenu({ id: 20100, path: '/xianJinYinHang/qichu', component: 'Layout', parentId: 0, name: '期初' }),
/*  createPlatformMenu({
    id: 20101,
    path: 'ke_mu_qi_chu_yu_e',
    component: '/boozsoft/xian_jin_liu_liang/qichu/ke_mu_qi_chu_yu_e/index',
    parentId: 20100,
    name: '科目期初余额',
  }),*/
  createPlatformMenu({
    id: 20102,
    path: 'yin_hang_dui_zhang_yu_e',
    component: '/boozsoft/xian_jin_liu_liang/qichu/yin_hang_dui_zhang_yu_e/index',
    componentName: 'YinHangDuiZhangYuE',
    parentId: 20100,
    name: '银行对账期初',
  }),
  createPlatformMenu({
    id: 20103,
    path: 'qi_chu_piao_ju',
    component: '/boozsoft/xian_jin_liu_liang/qichu/qi_chu_piao_ju/index',
    parentId: 20100,
    name: '票据期初',
  }),
  createPlatformMenu({
    id: 10000,
    path: 'welcome',
    component: '/dashboard/analysis/index',
    parentId: 10001,
    name: '首页',
  }),
  createPlatformMenu({ id: 10001, path: '/xianJinYinHang/home', component: 'LAYOUT', parentId: 0, name: '工作台' }),

  // 现金日记账
  /*createPlatformMenu({
    id: 22200,
    path: '/xianJinYinHang/xian_jin_ri_ji',
    component: 'Layout',
    parentId: 0,
    name: '账簿',
  }),*/
  createPlatformMenu({
    id: 22201,
    path: 'xian_jin_ri_ji_zhang',
    component: '/boozsoft/xian_jin_liu_liang/xian_jin_ri_ji/xian_jin_ri_ji_zhang/index',
    componentName: 'XianJinRiJiZhang',
    parentId: 22200,
    name: '现金日记账',
  }),
  /*createPlatformMenu({
    id: 22202,
    path: 'xian_jin_yu_e_biao',
    component: '/boozsoft/xian_jin_liu_liang/xian_jin_ri_ji/xian_jin_yu_e_biao/index',
    parentId: 22200,
    name: '现金余额表',
  }),*/
  /*createPlatformMenu({
    id: 22203,
    path: 'xian_jin_liu_liang_biao_ping_zheng',
    component:
      '/boozsoft/xian_jin_liu_liang/xian_jin_ri_ji/xian_jin_liu_liang_biao_ping_zheng/index',
    parentId: 22200,
    name: '现金凭证',
  }),*/
  // 银行日记账
  /*createPlatformMenu({
    id: 20300,
    path: '/xianJinYinHang/yin_hang_ri_ji',
    component: 'Layout',
    parentId: 0,
    name: '银行',
  }),*/
  createPlatformMenu({
    id: 20301,
    path: 'yin_hang_ri_ji_zhang',
    component: '/boozsoft/xian_jin_liu_liang/yin_hang_ri_ji/yin_hang_ri_ji_zhang/index',
    componentName: 'YinHangRiJiZhang',
    parentId: 22200,
    name: '银行日记账',
  }),
  createPlatformMenu({
    id: 20302,
    path: 'yin_hang_yu_e_biao',
    component: '/boozsoft/xian_jin_liu_liang/yin_hang_ri_ji/yin_hang_yu_e_biao/index',
    componentName: 'XianjinYinHangYuEBiao',
    parentId: 22200,
    name: '现金银行余额表',
  }),
  /* createPlatformMenu({
     id: 20303,
     path: 'chang_qi_wei_da_zhang_xiang',
     component: '/boozsoft/xian_jin_liu_liang/yin_hang_ri_ji/chang_qi_wei_da_zhang_xiang/index',
     parentId: 20300,
     name: '长期未达账项',
   }),*/
  /*createPlatformMenu({
    id: 20304,
    path: 'yin_hang_liu_liang_biao_ping_zheng',
    component:
      '/boozsoft/xian_jin_liu_liang/yin_hang_ri_ji/yin_hang_liu_liang_biao_ping_zheng/index',
    parentId: 22200,
    name: '银行凭证',
  }),*/
  // 银行对账
  createPlatformMenu({
    id: 20400,
    path: '/xianJinYinHang/yin_hang_dui_zhang',
    component: 'Layout',
    parentId: 0,
    name: '银行对账',
  }),
  createPlatformMenu({
    id: 20401,
    path: 'yin_hang_dui_zhang_dan',
    component: '/boozsoft/xian_jin_liu_liang/yin_hang_dui_zhang/yin_hang_dui_zhang_dan/index',
    componentName: 'YinHangDuiZhangDan',
    parentId: 20400,
    name: '银行对账单',
  }),
  createPlatformMenu({
    id: 20402,
    path: 'yin_hang_dui_zhang_ji_gou_dui',
    component: '/boozsoft/xian_jin_liu_liang/yin_hang_dui_zhang/yin_hang_dui_zhang_ji_gou_dui/index',
    componentName: 'YinHangDuiZhangJiGouDui',
    parentId: 20400,
    name: '对账及勾对',
  }),
  createPlatformMenu({
    id: 20403,
    path: 'yu_e_tiaojie_biao',
    component: '/boozsoft/xian_jin_liu_liang/yin_hang_dui_zhang/yu_e_tiaojie_biao/index',
    componentName: 'YuETiaoJieBiao',
    parentId: 20400,
    name: '余额调节表',
  }),
  createPlatformMenu({
    id: 20404,
    path: 'chang_qi_wei_da_zhang_shen_ji',
    component: '/boozsoft/xian_jin_liu_liang/yin_hang_dui_zhang/chang_qi_wei_da_zhang_shen_ji/index',
    componentName: 'ChangQiWeiDaZhangShenJi',
    parentId: 20400,
    name: '长期未达账审计',
  }),

  // 出纳凭证签字
  createPlatformMenu({
    id: 20600,
    path: '/xianJinYinHang/chu_na_ping_zheng_qian_zi',
    component: 'Layout',
    parentId: 0,
    name: '出纳签字',
  }),
  createPlatformMenu({
    id: 20601,
    path: 'chu_na_qian_zi',
    component: '/boozsoft/xian_jin_liu_liang/chu_na_ping_zheng_qian_zi/chu_na_qian_zi/index',
    parentId: 20600,
    name: '出纳签字',
  }),
  createPlatformMenu({
    id: 20602,
    path: 'qu_xiao_qian_zi',
    component: '/boozsoft/xian_jin_liu_liang/chu_na_ping_zheng_qian_zi/qu_xiao_qian_zi/index',
    parentId: 20600,
    name: '取消签字',
  }),
  createPlatformMenu({
    id: 20603,
    path: 'ping_zheng_lie_biao',
    component: '/boozsoft/xian_jin_liu_liang/chu_na_ping_zheng_qian_zi/ping_zheng_lie_biao/index',
    parentId: 20600,
    name: '出纳凭证',
  }),

  // 票据管理
  createPlatformMenu({
    id: 20500,
    path: '/xianJinYinHang/piao_ju_guan_li',
    component: 'Layout',
    parentId: 0,
    name: '承兑汇票',
  }),
  createPlatformMenu({
    id: 205010,
    path: 'ui_piao_guan_li',
    component: '/boozsoft/xian_jin_liu_liang/cheng_dui_hui_piao/hui_piao_guan_li/index',
    componentName: 'HuiPiaoGuanLi',
    parentId: 20500,
    name: '汇票管理',
  }),
  createPlatformMenu({
    id: 205011,
    path: 'dao_qi_yu_jing',
    component: '/boozsoft/xian_jin_liu_liang/cheng_dui_hui_piao/dao_qi_yu_jing/index',
    componentName: 'DaoQiYuJing',
    parentId: 20500,
    name: '到期预警',
  }),
  createPlatformMenu({
    id: 205012,
    path: 'ping_zheng_sheng_dan',
    component: '/boozsoft/xian_jin_liu_liang/cheng_dui_hui_piao/dao_qi_yu_jing/index',
    parentId: 20500,
    name: '凭证生单',
  }),
   /*createPlatformMenu({
    id: 20501,
    path: 'piao_ju',
    component: '/boozsoft/xian_jin_liu_liang/piao_ju_guan_li/piao_ju/index',
    parentId: 20500,
    name: '登记',
  }),*/
 /* createPlatformMenu({
    id: 20501,
    path: 'piao_ju',
    component: '/boozsoft/xian_jin_liu_liang/piao_ju_guan_li/piao_ju/index',
    parentId: 20500,
    name: '票据',
  }),*/
  /*createPlatformMenu({
    id: 20502,
    path: 'jie_suan',
    component: '/boozsoft/xian_jin_liu_liang/piao_ju_guan_li/jie_suan/index',
    parentId: 20500,
    name: '结算',
  }),
  createPlatformMenu({
    id: 20503,
    path: 'zhuan_chu',
    component: '/boozsoft/xian_jin_liu_liang/piao_ju_guan_li/zhuan_chu/index',
    parentId: 20500,
    name: '转出',
  }),*/
  createPlatformMenu({
    id: 20504,
    path: 'ji_xi',
    component: '/boozsoft/xian_jin_liu_liang/piao_ju_guan_li/ji_xi/index',
    parentId: 20500,
    name: '计息',
  }),
  /*createPlatformMenu({
    id: 20505,
    path: 'bei_shu',
    component: '/boozsoft/xian_jin_liu_liang/piao_ju_guan_li/bei_shu/index',
    parentId: 20500,
    name: '背书',
  }),
  createPlatformMenu({
    id: 20506,
    path: 'tie_xian',
    component: '/boozsoft/xian_jin_liu_liang/piao_ju_guan_li/tie_xian/index',
    parentId: 20500,
    name: '贴现',
  }),*/

  // 现金日记账
  createPlatformMenu({
    id: 22200,
    path: '/xianJinYinHang/xian_jin_ri_ji',
    component: 'Layout',
    parentId: 0,
    name: '账簿',
  }),

  // 设置
  createPlatformMenu({
    id: 20700,
    path: '/xianJinYinHang/setting',
    component: 'Layout',
    parentId: 0,
    name: '设置'
  }),
  createPlatformMenu({
    id: 20708,
    path: 'xjyh-panel',
    component: '/boozsoft/xian_jin_liu_liang/setting/accpanel/index',
    componentName: 'xjyhPanel',
    parentId: 20700,
    name: '面板设置'
  }),
  createPlatformMenu({
    id: 20707,
    path: 'setting-parm',
    component: '/boozsoft/xian_jin_liu_liang/setting/can_shu_she_zhi/index',
    componentName: 'CanShuSheZhi',
    parentId: 20700,
    name: '参数设置'
  }),
  createPlatformMenu({
    id: 20702,
    path: 'yin_hang_dui_zhang_mo_ban',
    component: '/boozsoft/xian_jin_liu_liang/setting/yin_hang_dui_zhang_mo_ban/index',
    componentName: 'YinHangDuiZhangMoBan',
    parentId: 20700,
    name: '银行对账单模板',
  }),
  /*  createPlatformMenu({
      id: 20701,
      path: 'ke_mu_zhang_hu_she_zhi',
      component: '/boozsoft/xian_jin_liu_liang/setting/ke_mu_zhang_hu_she_zhi/index',
      parentId: 20700,
      name: '科目账户设置',
    }),
    createPlatformMenu({
      id: 20702,
      path: 'yin_hang_dui_zhang_mo_ban',
      component: '/boozsoft/xian_jin_liu_liang/setting/yin_hang_dui_zhang_mo_ban/index',
      parentId: 20700,
      name: '银行对账单模板',
    }),
    createPlatformMenu({
      id: 20703,
      path: 'piao_ju_mo_ban',
      component: '/boozsoft/xian_jin_liu_liang/setting/piao_ju_mo_ban/index',
      parentId: 20700,
      name: '票据模板',
    }),
    createPlatformMenu({
      id: 20704,
      path: 'yin_hang_dang_an',
      component: '/boozsoft/xian_jin_liu_liang/setting/yin_hang_dang_an/index',
      parentId: 20700,
      name: '银行档案',
    }),
    createPlatformMenu({
      id: 20705,
      path: 'zhang_tao_zhang_hu',
      component: '/boozsoft/xian_jin_liu_liang/setting/zhang_tao_zhang_hu/index',
      parentId: 20700,
      name: '账套账户',
    }),
    createPlatformMenu({
      id: 20706,
      path: 'xian_jin_liu_liang_xiang_mu',
      component: '/boozsoft/xian_jin_liu_liang/setting/xian_jin_liu_liang_xiang_mu/index',
      parentId: 20700,
      name: '现金流量项目',
    }),*/
];

export const platformMenuIds = {
  key:'xianJinYinHang',
  id:platformId,
  sort:2,
  menuIds:menus.map(item=>item.id)
};

