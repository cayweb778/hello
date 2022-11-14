// 总账
// @ts-ignore
import {createMenu, createModel} from '/@/../pages/rbac/utils/mockMenuUtil';

const platformId = 1099;
function createPlatformMenu(params){
  return createMenu({...params,platformId})
}
function useId(num) {
  return platformId * 1000 + num
}
export const menus = [
  createModel({id: platformId, isCloud: false, isTargetBlank: false, isOutLink: false, name: '存货管理', category: 102, sortNo: 1,}),

  createPlatformMenu({ id: useId(120), path: '/zhongZhang/home', component: 'LAYOUT', parentId: 0, name: '工作台' }),
  createPlatformMenu({id: useId(121), path: 'welcome', component: '/dashboard/analysis/index', parentId: useId(120), name: '首页',}),
  // createPlatformMenu({id: useId(121), path: 'welcome', component: '/platforms/CunHuoManager/Home/index', parentId: useId(120), name: '首页',}),
  /*  createPlatformMenu({ id: 18, path: 'system', component: '', parentId: 8820, name: '系统管理' }),*/
  createPlatformMenu({
    id: useId(999),
    path: '/',
    redirect:'/zhongZhang/home/welcome',
    component: 'LAYOUT',
    parentId: 0,
    hidden:true,
    name: '根目录'
  }),

  createPlatformMenu({ id: useId(101), path: '/stock-qc', component: 'LAYOUT', parentId: 0, name: '期初' }),
  createPlatformMenu({ id: useId(102), path: '/stock-cg', component: 'LAYOUT', parentId: 0, name: '采购' }),
  createPlatformMenu({ id: useId(103), path: '/stock-xs', component: 'LAYOUT', parentId: 0, name: '销售' }),
  // createPlatformMenu({ id: useId(114), path: '/stock-wanglai', component: 'LAYOUT', parentId: 0, name: '往来' }),
  createPlatformMenu({ id: useId(104), path: '/stock-kc', component: 'LAYOUT', parentId: 0, name: '库存' }),
  createPlatformMenu({ id: useId(105), path: '/stock-wl', component: 'LAYOUT', parentId: 0, name: '物流' }),
  createPlatformMenu({ id: useId(106), path: '/stock-jy', component: 'LAYOUT', parentId: 0, name: '借用' }),
  createPlatformMenu({ id: useId(107), path: '/stock-fy', component: 'LAYOUT', parentId: 0, name: '费用' }),
  createPlatformMenu({ id: useId(108), path: '/stock-cb', component: 'LAYOUT', parentId: 0, name: '成本' }),
  createPlatformMenu({ id: useId(109), path: '/stock-pz', component: 'LAYOUT', parentId: 0, name: '凭证' }),
  createPlatformMenu({ id: useId(111), path: '/stock-zb', component: 'LAYOUT', parentId: 0, name: '账表' }),
  createPlatformMenu({ id: useId(112), path: '/stock-jz', component: 'LAYOUT', parentId: 0, name: '结账' }),
  createPlatformMenu({ id: useId(113), path: '/stock-sz', component: 'LAYOUT', parentId: 0, name: '设置' }),

  createPlatformMenu({ id: useId(1071), path: '/fy-invoice', component: '', parentId: useId(107), name: '业务单据' }),
  createPlatformMenu({ id: useId(1072), path: '/fy-list', component: '', parentId: useId(107), name: '单据列表' }),
  createPlatformMenu({ id: useId(10711), path: '/fyadd', component: '/boozsoft/stock/FeiYongDan/layouts/RouteCache',componentName:'FeiYongAdd', parentId: useId(1071), name: '费用单' }),
  createPlatformMenu({ id: useId(10721), path: '/fylist', component: '/boozsoft/stock/FeiYongDanLieBiao/layouts/RouteCache',componentName:'FeiYongList', parentId: useId(1072), name: '费用单列表' }),

  /*
    createPlatformMenu({ id: useId(601), path: '/cg-invoice', component: '', parentId: useId(114), name: '设置' }),
    createPlatformMenu({ id: useId(602), path: '/cg-manage', component: '', parentId: useId(114), name: '设置' }),

    createPlatformMenu({ id: useId(611), path: '', component: '', parentId: useId(601),componentName: '', name: '科目设置' }),
    createPlatformMenu({ id: useId(612), path: '', component: '', parentId: useId(602),componentName: '', name: '科目设置' }),
  */

  createPlatformMenu({ id: useId(201), path: '/qc-manage', component: '', parentId: useId(101), name: '期初业务' }),
  createPlatformMenu({ id: useId(202), path: '/qc-list', component: '', parentId: useId(101), name: '列表' }),
  createPlatformMenu({ id: useId(301), path: '/stock_begin_balance', component: '/boozsoft/stock/stock_balance/index', parentId: useId(201),componentName: 'StockBeginBalance', name: '存货期初余额' }),
  createPlatformMenu({ id: useId(302), path: '/stock_balance_dh', component: '/boozsoft/stock/stock_balance_dh/index', parentId: useId(201),componentName: 'StockBalanceDH', name: '期初到货单' }),
  createPlatformMenu({ id: useId(303), path: '/stock_sales_balance', component: '/boozsoft/stock/stock_sales_balance/index', parentId: useId(201),componentName: 'QiChuXiaoHuoDan', name: '期初销货单' }),
  // createPlatformMenu({ id: useId(303), path: '/stock_ap_balance', component: '/boozsoft/stock/stock_ap_balance/index', parentId: useId(201),componentName: 'StockApBalance', name: '应付期初销货单' }),
  createPlatformMenu({ id: useId(308), path: '/stock_balance_zg_rk', component: '/boozsoft/stock/stock_balance_zg_rk/index', parentId: useId(201),componentName: 'StockBalanceZGRKD', name: '期初暂估入库单' }),
  createPlatformMenu({ id: useId(306), path: '/stock_balance_fp', component: '/boozsoft/stock/stock_balance_fp/index', parentId: useId(201),componentName: 'StockBalanceCGFP', name: '期初采购发票' }),
  createPlatformMenu({ id: useId(299), path: '/stock_balance_xsfp', component: '/boozsoft/stock/stock_balance_xsfp/index', parentId: useId(201),componentName: 'StockBalanceXSFP', name: '期初销售发票' }),
  createPlatformMenu({id: useId(670),path: 'StockBalanceJZ',componentName: 'StockBalanceJZ',component: '/boozsoft/stock/stock_balance_jz/layouts/RouteCache',parentId: useId(999),name: '结转上年'}),

  createPlatformMenu({ id: useId(305), path: '/stock_balance_dh_list', component: '/boozsoft/stock/stock_balance_dh_list/index', parentId: useId(202),componentName: 'StockBalanceDHList', name: '期初到货单列表' }),
  createPlatformMenu({ id: useId(304), path: '/stock_sales_balance_list', component: '/boozsoft/stock/stock_sales_balance_list/index', parentId: useId(202),componentName: 'QiChuXiaoHuoDanList', name: '期初销货单列表' }),
  createPlatformMenu({ id: useId(309), path: '/stock_balance_zg_rk_list', component: '/boozsoft/stock/stock_balance_zg_rk_list/index', parentId: useId(202),componentName: 'StockBalanceZGRKDList', name: '期初暂估入库单列表' }),
  createPlatformMenu({ id: useId(307), path: '/stock_balance_fp_list', component: '/boozsoft/stock/stock_balance_fp_list/index', parentId: useId(202),componentName: 'StockBalanceCGFPList', name: '期初采购发票列表' }),
  createPlatformMenu({ id: useId(310), path: '/stock_balance_xsfp_list', component: '/boozsoft/stock_balance_xsfp_list/index', parentId: useId(202),componentName: 'StockBalanceXSFPList', name: '期初销售发票列表' }),

  createPlatformMenu({ id: useId(211), path: '/cg-invoice', component: '', parentId: useId(102), name: '业务单据' }),
  createPlatformMenu({ id: useId(212), path: '/cg-list', component: '', parentId: useId(102), name: '单据列表' }),
  createPlatformMenu({ id: useId(213), path: '/cg-manage', component: '', parentId: useId(102), name: '业务表' }),
  createPlatformMenu({ id: useId(214), path: '/cg-analyze', component: '', parentId: useId(102), name: '分析表' }),

  createPlatformMenu({ id: useId(311), path: '/cg-dingdan', component: '/boozsoft/stock/stock-caigou-dingdan/index', componentName:'CaiGouDingDan', parentId: useId(211), name: '采购订单' }),
  createPlatformMenu({ id: useId(312), path: '/cg-arrive', component: '/boozsoft/stock/stock-caigou-dh/index', parentId: useId(211),componentName: 'StockCaiGouDH', name: '采购到货单' }),
  createPlatformMenu({ id: useId(666), path: '/cg-arrive-change', component: '/boozsoft/stock/stock-caigou-dh/index3',hidden:true, parentId: useId(211),componentName: 'StockCaiGouDHBD', name: '采购到货单（变动）' }),
  createPlatformMenu({ id: useId(313), path: '/cg-return', component: '/boozsoft/stock/stock-caigou-th/index', parentId: useId(211),componentName: 'StockCaiGouTuiHuo', name: '采购退货单' }),
  createPlatformMenu({ id: useId(314), path: '/cg-bill', component: '/boozsoft/stock/stock-caigou-fapiao/index', parentId: useId(211),componentName:'CaiGouFaPiao', name: '采购发票' }),
  createPlatformMenu({ id: useId(668), path: '/cg-jiesuan', component: '/boozsoft/stock/stock-caigou-jiesuan/index', parentId: useId(211),componentName:'CaiGouJiesuan', name: '采购核算单' }),

  createPlatformMenu({ id: useId(315), path: '/cg-dingdan-list', component: '/boozsoft/stock/stock-caigou-dingdan-list/index', componentName:'CaiGouDingDanLiat', parentId: useId(212), name: '采购订单列表' }),
  createPlatformMenu({ id: useId(316), path: '/cg-dhList', component: '/boozsoft/stock/stock-caigou-dh-list/index', componentName:'CaigouDhList', parentId: useId(212), name: '采购到货单列表' }),
  createPlatformMenu({ id: useId(318), path: '/cg-fp-list', component: '/boozsoft/stock/stock-caigou-fp-list/index', componentName:'CaigouFpList', parentId: useId(212), name: '发票列表' }),
  createPlatformMenu({ id: useId(669), path: '/cg-js-list', component: '/boozsoft/stock/stock-caigou-jiesuan-list/index', componentName:'CaigouJsList', parentId: useId(212), name: '采购核算单列表' }),

  createPlatformMenu({ id: useId(319), path: '/cg-gjList', component: '/boozsoft/stock/stock-caigou-statis/index',componentName:'CaigouStatisList', parentId: useId(213), name: '采购统计表' }),
  createPlatformMenu({ id: useId(320), path: '/cg-mxList', component: '/boozsoft/stock/stock-caigou-statis-mx/index',componentName:'CaigouStatisListMX', parentId: useId(213), name: '采购明细表' }),

  createPlatformMenu({ id: useId(140), path: '/cg-phb', component: '/boozsoft/stock/stock-caigou-paihang/index',componentName:'CaigouPaiHang', parentId: useId(214), name: '采购排行榜' }),
  createPlatformMenu({ id: useId(141), path: '/cg-gysyf', component: '/boozsoft/stock/stock-supplier-balance/index',componentName:'CaigouSupplierBalance', parentId: useId(214), name: '供应商应付余额表' }),
  createPlatformMenu({ id: useId(142), path: '/cg-dd-detailed', component: '/boozsoft/stock/stock-caigou-dd-detailed/index', componentName:'CgDDDetailed', parentId: useId(214), name: '采购订单执行表' }),
  createPlatformMenu({ id: useId(143), path: '/cg-dh-detailed', component: '/boozsoft/stock/stock-caigou-dh-detailed/index', componentName:'CgDhDetailed', parentId: useId(214), name: '采购到货单执行表' }),

  createPlatformMenu({ id: useId(221), path: '/xs-invoice', component: '', parentId: useId(103), name: '业务单据' }),
  createPlatformMenu({ id: useId(222), path: '/xs-list', component: '', parentId: useId(103), name: '单据列表' }),
  createPlatformMenu({ id: useId(223), path: '/xs-statistics', component: '', parentId: useId(103), name: '业务表' }),
  createPlatformMenu({ id: useId(224), path: '/xs-details', component: '', parentId: useId(103), name: '分析表' }),


  createPlatformMenu({ id: useId(321), path: '/xs-order', component: '/boozsoft/stock/stock_order_add/index', componentName:'XiaoShouDingDan', parentId: useId(221), name: '销售订单' }),
  createPlatformMenu({ id: useId(322), path: '/xs-arrive', component: '/boozsoft/stock/stock_sales_add/index',componentName:'XiaoHuoDan', parentId: useId(221), name: '销货单' }),
  createPlatformMenu({ id: useId(323), path: '/xs-bReturn', component: '/boozsoft/stock/stock_piliang/index',componentName:'PlXiaoHuoDan', parentId: useId(221), name: '批量销货单' }),
  createPlatformMenu({ id: useId(324), path: '/xs-return', component: '/boozsoft/stock/stock_sales_out_add/index',componentName:'XiaoShouTuiHuDan', parentId: useId(221), name: '退货单' }),
  createPlatformMenu({ id: useId(325), path: '/xs-bill', component: '/boozsoft/stock/stock_sales_fp_add/index', componentName:'XiaoShouFaPiao', parentId: useId(221), name: '销售发票' }),
  createPlatformMenu({ id: useId(152), path: '/xs-confirm', component: '/boozsoft/stock/stock_confirm_add/index',componentName:'XiaoShouQueRenDan', parentId: useId(221), name: '销售出库确认单' }),
  // createPlatformMenu({ id: useId(321), path: '/xs-price', component: '/boozsoft/system/blocktable/index', parentId: useId(221), name: '客户价格表' }),
  createPlatformMenu({ id: useId(434), path: '/xs-arrive-change',hidden:true, component: '/boozsoft/stock/stock_sales_add/index3', parentId: useId(221), name: '销货单（变动）' }),

  createPlatformMenu({ id: useId(326), path: '/xs-ddList', component: '/boozsoft/stock/stock-order-list/index', componentName:'XiaoShouDingDanList', parentId: useId(222), name: '销售订单列表' }),
  createPlatformMenu({ id: useId(327), path: '/xs-dhList', component: '/boozsoft/stock/stock-xiaohuo-list/index', componentName:'XiaoHuoDanList', parentId: useId(222), name: '销货单列表' }),
  createPlatformMenu({ id: useId(328), path: '/xs-dhbList', component: '/boozsoft/stock/stock-piliang-list/index', componentName:'PlXiaoHuoDanList', parentId: useId(222), name: '批量销货单列表' }),
  createPlatformMenu({ id: useId(329), path: '/xs-fplist', component: '/boozsoft/stock/stock-xiaohuo-fp-list/index', componentName:'XiaohuoFpList', parentId: useId(222), name: '发票列表' }),
  createPlatformMenu({ id: useId(330), path: '/xs-qrlist', component: '/boozsoft/stock/stock-confirm-list/index', componentName:'XiaohuoQrList', parentId: useId(222), name: '销售出库确认单列表' }),

  createPlatformMenu({ id: useId(131), path: '/xs-gjList', component: '/boozsoft/stock/stock-tongji-list/index',componentName:'XiaoShouTongJiBiao1',  parentId: useId(223), name: '客户销售统计表' }),
  createPlatformMenu({ id: useId(132), path: '/xs-gjgList', component: '/boozsoft/stock/stock-tongji-list/index',componentName:'XiaoShouTongJiBiao2', parentId: useId(223), name: '存货销售统计表' }),
  createPlatformMenu({ id: useId(145), path: '/xs-xsCountUser', component: '/boozsoft/stock/stock-tongji-list/index',componentName:'XiaoShouTongJiBiao3', parentId: useId(223), name: '业务员销售统计表' }),
  createPlatformMenu({ id: useId(146), path: '/xs-xsCountArea', component: '/boozsoft/stock/stock-tongji-list/index',componentName:'XiaoShouTongJiBiao4', parentId: useId(223), name: '地区销售统计表' }),
  createPlatformMenu({ id: useId(147), path: '/xs-mxgList', component: '/boozsoft/stock/stock-details-list/index', componentName:'XiaoShouMingXiBiao2',parentId: useId(223), name: '销售明细表' }),

  createPlatformMenu({ id: useId(148), path: '/xs-phbStock', component: '/boozsoft/stock/stock-leaderboard-list/index',componentName:'XiaoShouPaiHangBang1', parentId: useId(224), name: '存货销售排行榜' }),
  createPlatformMenu({ id: useId(149), path: '/xs-phbCust', component: '/boozsoft/stock/stock-leaderboard-list/index', componentName:'XiaoShouPaiHangBang2', parentId: useId(224), name: '客户销售排行榜' }),
  createPlatformMenu({ id: useId(150), path: '/xs-custye', component: '/boozsoft/stock/stock-receivable-balance-list/index',componentName:'KeHuYingShouYueBiao', parentId: useId(224), name: '客户应收余额表' }),
  createPlatformMenu({ id: useId(151), path: '/xs-mlAnalyze', component: '/boozsoft/stock/stock-mlAnalyze-list/index',componentName:'XiaoShouMaoLiFenXiBiao', parentId: useId(224), name: '销售毛利分析表' }),
  createPlatformMenu({ id: useId(189), path: '/xs-execute-list', component: '/boozsoft/stock/stock-execute-list/index', componentName:'XiaoShouDdExec',parentId: useId(224), name: '销售订单执行表' }),
  createPlatformMenu({ id: useId(190), path: '/xs-execSituation-list', component: '/boozsoft/stock/stock-execSituation-list/index', componentName:'XiaoShouExecSituation',parentId: useId(224), name: '销货单执行表' }),

  // createPlatformMenu({ id: useId(234), path: '/kc-operate', component: '', parentId: useId(104), name: '业务操作' }),
  createPlatformMenu({ id: useId(231), path: '/kc-invoice', component: '', parentId: useId(104), name: '出入库单据' }),
  // createPlatformMenu({ id: useId(232), path: '/kc-invoice-out', component: '', parentId: useId(104), name: '库单据' }),
  createPlatformMenu({ id: useId(232), path: '/kc-list', component: '', parentId: useId(104), name: '出入库单据列表' }),
  createPlatformMenu({ id: useId(233), path: '/kc-invoice-kc', component: '', parentId: useId(104), name: '库存单据' }),
  createPlatformMenu({ id: useId(237), path: '/kc-list', component: '', parentId: useId(104), name: '库存单据列表' }),
  createPlatformMenu({ id: useId(236), path: '/kc-manage', component: '', parentId: useId(104), name: '账表查询' }),

  createPlatformMenu({ id: useId(332), path: '/kc-cgDepot', component: '/boozsoft/stock/stock-caigou-rk/index',componentName:'CaigouRk', parentId: useId(231), name: '采购入库单' }),
  createPlatformMenu({ id: useId(334), path: '/kc-qtDepot', component: '/boozsoft/stock/stock_other_add/index',componentName:'CaigouQTRk', parentId: useId(231), name: '其他入库单' }),
  createPlatformMenu({ id: useId(333), path: '/kc-xsDepot', component: '/boozsoft/stock/stock_out_add/index',componentName:'XiaoShouChuKuDan', parentId: useId(231), name: '销售出库单' }),
  createPlatformMenu({ id: useId(335), path: '/kc-out-qtDepot', component: '/boozsoft/stock/stock_other_out/index',componentName:'QiTaChuKuDan', parentId: useId(231), name: '其他出库单' }),
  createPlatformMenu({ id: useId(336), path: '/kc-out-material', component: '/boozsoft/stock/stock_material_add/index',componentName:'CaiLiaoLingYongDan', parentId: useId(231), name: '领用出库单' }),


  createPlatformMenu({ id: useId(331), path: '/kc-box', component: '/boozsoft/stock/stock_jhzx/index',componentName:'JianHuoZhuangXiangDan', parentId: useId(233), name: '拣货装箱单' }),
  createPlatformMenu({ id: useId(337), path: '/kc-dbd-list', component: '/boozsoft/stock/stock-transfer-list/index', parentId: useId(233), name: '调拨单' }),
  createPlatformMenu({ id: useId(338), path: '/kc-inventory', component: '/boozsoft/stock/stock_taking/index', parentId: useId(233), name: '盘点单' }),
  createPlatformMenu({ id: useId(339), path: '/kc-shape-list', component: '/boozsoft/stock/stock_conversion_list/index', parentId: useId(233), name: '形态转换单' }),
  createPlatformMenu({ id: useId(340), path: '/stock-assem-assemble', component: '/boozsoft/stock/stock-assem-assemble/index',componentName:'stockAssemAssemble', parentId: useId(233), name: '组装拆卸单' }),

  // createPlatformMenu({ id: useId(340), path: '/kc-tidy-xcl', component: '/boozsoft/stock/stock-xcl-make/index', parentId: useId(234), name: '整理现存量' }),

  createPlatformMenu({ id: useId(152), path: '/kc-cgDepot-list', component: '/boozsoft/stock/stock-caigou-rk-list/index', componentName:'CaigouRkList', parentId: useId(232), name: '采购入库单列表' }),
  createPlatformMenu({ id: useId(154), path: '/kc-qtDepot-list', component: '/boozsoft/stock/stock-other-add-list/index', componentName:'QTRukuList', parentId: useId(232), name: '其他入库单列表' }),
  createPlatformMenu({ id: useId(153), path: '/kc-xsDepot-list', component: '/boozsoft/stock/stock-xiaohuo-ck-list/index', componentName:'XiaoShouCkList', parentId: useId(232), name: '销售出库单列表' }),
  createPlatformMenu({ id: useId(155), path: '/kc-out-qtDepot-list', component: '/boozsoft/stock/stock_other_out_list/index', componentName:'QtChuKuDanList', parentId: useId(232), name: '其他出库单列表' }),
  createPlatformMenu({ id: useId(156), path: '/kc-out-material-list', component: '/boozsoft/stock/stock-clly-list/index', componentName:'CllyList', parentId: useId(232), name: '领用出库单列表' }),

  createPlatformMenu({ id: useId(139), path: '/kc-Jgzx-list', component: '/boozsoft/stock/stock-jhzx-list/index', componentName:'JhzxList', parentId: useId(237), name: '拣货装箱列表' }),
  createPlatformMenu({ id: useId(158), path: '/stock-assem-assemble-list', component: '/boozsoft/stock/stock-assem-assemble-list/index', componentName:'stockAssemAssembleList', parentId: useId(237), name: '组装拆卸单列表' }),

  createPlatformMenu({ id: useId(160), path: '/kc-xclQuery', component: '/boozsoft/stock/stock-kc-xcl/index', componentName:'StockKcXcl', parentId: useId(236), name: '存货现存量查询' }),
  createPlatformMenu({ id: useId(161), path: '/kc-pcxclQuery', component: '/boozsoft/stock/stock-kc-pc-xcl/index', componentName:'StockKcPcXcl', parentId: useId(236), name: '批次现存量查询' }),
  createPlatformMenu({ id: useId(168), path: '/kc-output-rk', component: '/boozsoft/stock/stock-outPut-rk-list/index',componentName:'OutPutRk', parentId: useId(236), name: '出入库流水' }),
  createPlatformMenu({ id: useId(165), path: '/kc-numberPool', component: '/boozsoft/stock/stock-number-sfc/index', componentName:'XcNumberPool', parentId: useId(236), name: '数量收发存汇总表' }),
  createPlatformMenu({ id: useId(166), path: '/kc-amountPool', component: '/boozsoft/stock/stock-money-sfc/index', componentName:'XcamountPool', parentId: useId(236), name: '金额收发存汇总表' }),
  createPlatformMenu({ id: useId(167), path: '/kc-stock-warning', component: '/boozsoft/stock/stock-warning-list/index', componentName:'XcStockWarning', parentId: useId(236), name: '存货有效期预警表' }),


  createPlatformMenu({ id: useId(241), path: '/wl-invoice', component: '', parentId: useId(105), name: '单据' }),
  createPlatformMenu({ id: useId(242), path: '/wl-manage', component: '', parentId: useId(105), name: '查询' }),

  createPlatformMenu({ id: useId(341), path: '/wl-cgLogistics', component: '/boozsoft/stock/stock-caigou-wuliu/index',componentName:'CaigouWuLiu', parentId: useId(241), name: '销售物流送货单' }),
  createPlatformMenu({ id: useId(342), path: '/wl-xsLogistics', component: '/boozsoft/system/blocktable/index', parentId: useId(241), name: '采购物流到货单' }),

  createPlatformMenu({ id: useId(343), path: '/wl-cgLogistics-list', component: '/boozsoft/stock/stock-caigou-wuliu-list/index', componentName:'XSWLList', parentId: useId(242), name: '物流送货单列表' }),
  createPlatformMenu({ id: useId(344), path: '/wl-xsLogistics-list', component: '/boozsoft/stock/stock-xiaoshou-ps-list/index', componentName:'XiaoshouPsList', parentId: useId(242), name: '物流到货单列表' }),

  createPlatformMenu({ id: useId(251), path: '/qt-invoice-out', component: '', parentId: useId(106), name: '借出单据' }),
  createPlatformMenu({ id: useId(252), path: '/qt-invoice', component: '', parentId: useId(106), name: '借入单据' }),
  createPlatformMenu({ id: useId(253), path: '/qt-manage', component: '', parentId: useId(106), name: '查询' }),

  createPlatformMenu({ id: useId(351), path: '/jy-oLend', component: '/boozsoft/cun_huo_manager/jy-oLend/index', parentId: useId(251), name: '借出借用单' }),
  createPlatformMenu({ id: useId(352), path: '/jy-out-oLend', component: '/boozsoft/system/blocktable/index', parentId: useId(251), name: '借出归还单' }),
  createPlatformMenu({ id: useId(359), path: '/jy-out-zh', component: '/boozsoft/system/blocktable/index', parentId: useId(251), name: '借出调整单' }),

  createPlatformMenu({ id: useId(353), path: '/jy-iLend', component: '/boozsoft/stock/stock_borrow_jy/index',componentName:'JyILend', parentId: useId(252), name: '借入借用单' }),
  createPlatformMenu({ id: useId(354), path: '/jy-out-iLend', component: '/boozsoft/system/blocktable/index', parentId: useId(252), name: '借入还回单' }),
  createPlatformMenu({ id: useId(360), path: '/jy-in-zh', component: '/boozsoft/system/blocktable/index', parentId: useId(252), name: '借入调整单' }),

  createPlatformMenu({ id: useId(355), path: '/jy-oLend-list', component: '/boozsoft/stock/stock-jrjy-list/index', componentName:'JrjyList', parentId: useId(253), name: '借出借用单列表' }),
  createPlatformMenu({ id: useId(356), path: '/jy-out-oLend-list', component: '/boozsoft/stock/stock-jrhh-list/index', componentName:'JrhhList', parentId: useId(253), name: '借出归还单列表' }),
  createPlatformMenu({ id: useId(357), path: '/jy-iLend-list', component: '/boozsoft/stock/stock_borrow_ly_list/index', componentName:'JcjyList', parentId: useId(253), name: '借入借用单列表' }),
  createPlatformMenu({ id: useId(358), path: '/jy-out-iLend-list', component: '/boozsoft/stock/stock-jchh-list/index', componentName:'JchhList', parentId: useId(253), name: '借入还回单列表' }),
  createPlatformMenu({ id: useId(361), path: '/jy-out-zh-list', component: '/boozsoft/stock/stock-jrhh-list/index', componentName:'JrhhList', parentId: useId(253), name: '借出调整单列表' }),
  createPlatformMenu({ id: useId(362), path: '/jy-in-zh-list', component: '/boozsoft/stock/stock-jchh-list/index', componentName:'JchhList', parentId: useId(253), name: '借入调整单列表' }),


  createPlatformMenu({ id: useId(271), path: '/cb-invoice', component: '', parentId: useId(108), name: '成本核算' }),
  // createPlatformMenu({ id: useId(272), path: '/cb-invoice', component: '', parentId: useId(108), name: '成本调整' }),

  createPlatformMenu({ id: useId(273), path: '/cb-list', component: '', parentId: useId(108), name: '列表' }),

  createPlatformMenu({ id: useId(372), path: '/cb-accounting', component: '/boozsoft/stock/stock-ckcbhs-list/index', componentName:'CkcbhsList', parentId: useId(271), name: '自动成本核算' }),
  createPlatformMenu({ id: useId(373), path: '/cb-depot', component: '/boozsoft/stock/stock-rktzd-add/index', componentName:'Rktzd', parentId: useId(271), name: '入库调整单' }),
  createPlatformMenu({ id: useId(374), path: '/cb-out-depot', component: '/boozsoft/stock/stock-cktzd-add/index', componentName:'Cktzd', parentId: useId(271), name: '出库调整单' }),
  createPlatformMenu({ id: useId(672), path: '/red-hcd', component: '/boozsoft/stock/stock-hlhcd-red/index', componentName:'BlueHCD', parentId: useId(271), name: '红字回冲单' }),
  createPlatformMenu({ id: useId(671), path: '/blue-hcd', component: '/boozsoft/stock/stock-hlhcd-blue/index', componentName:'RedHCD', parentId: useId(271), name: '蓝字回冲单' }),

  createPlatformMenu({ id: useId(375), path: '/cb-sfhz', component: '/boozsoft/system/blocktable/index', parentId: useId(273), name: '金额收发存汇总表' }),
  createPlatformMenu({ id: useId(376), path: '/cb-rktz', component: '/boozsoft/stock/stock-rktzd-list/index', componentName:'RktzdList', parentId: useId(273), name: '入库调整单列表' }),
  createPlatformMenu({ id: useId(377), path: '/cb-cktz', component: '/boozsoft/stock/stock-cktzd-list/index', componentName:'CktzdList', parentId: useId(273), name: '出库调整单列表' }),
  createPlatformMenu({ id: useId(674), path: '/red-hcd-list', component: '/boozsoft/stock/stock-hlhcd-red-list/index', componentName:'BlueHCDList', parentId: useId(273), name: '红字回冲单列表' }),
  createPlatformMenu({ id: useId(673), path: '/blue-hcd-list', component: '/boozsoft/stock/stock-hlhcd-blue-list/index', componentName:'RedHCDList', parentId: useId(273), name: '蓝字回冲单列表' }),


  createPlatformMenu({ id: useId(281), path: '/pz-invoice', component: '', parentId: useId(109), name: '制单' }),
  // createPlatformMenu({ id: useId(282), path: '/pz-invoice-xs', component: '', parentId: useId(109), name: '销售制单' }),
  // createPlatformMenu({ id: useId(283), path: '/pz-invoice-kc', component: '', parentId: useId(109), name: '库存制单' }),
  createPlatformMenu({ id: useId(284), path: '/pz-invoice-list', component: '', parentId: useId(109), name: '列表' }),

  createPlatformMenu({ id: useId(380), path: '/pz-dhZd', component: '/boozsoft/stock/stock-pingzheng-add/index',componentName:'PingZhengAdd', parentId: useId(281), name: '业务单据制单' }),
  /*  createPlatformMenu({ id: useId(381), path: '/pz-cgZd', component: '', parentId: useId(281), name: '采购发票制单' }),

    createPlatformMenu({ id: useId(382), path: '/pz-xhZd', component: '', parentId: useId(282), name: '销货单制单' }),
    createPlatformMenu({ id: useId(383), path: '/pz-xsZd', component: '', parentId: useId(282), name: '销售发票制单' }),

    createPlatformMenu({ id: useId(384), path: '/pz-agrkZd', component: '', parentId: useId(283), name: '采购入库单制单' }),
    createPlatformMenu({ id: useId(385), path: '/pz-qtrkZd', component: '', parentId: useId(283), name: '其他入库单制单' }),
    createPlatformMenu({ id: useId(386), path: '/pz-xsckZd', component: '', parentId: useId(283), name: '销售出库单制单' }),
    createPlatformMenu({ id: useId(387), path: '/pz-cllyZd', component: '', parentId: useId(283), name: '领用出库单制单' }),
    createPlatformMenu({ id: useId(388), path: '/pz-qtckZd', component: '', parentId: useId(283), name: '其他出库单制单' }),
    createPlatformMenu({ id: useId(389), path: '/pz-pdZd', component: '', parentId: useId(283), name: '盘点单制单' }),
    createPlatformMenu({ id: useId(390), path: '/pz-xtzhZd', component: '', parentId: useId(283), name: '形态转换单制单' }),
    createPlatformMenu({ id: useId(191), path: '/pz-redZd', component: '', parentId: useId(283), name: '红字回冲单制单' }),
    createPlatformMenu({ id: useId(192), path: '/pz-blueZd', component: '', parentId: useId(283), name: '蓝字回冲单制单' }),*/

  createPlatformMenu({ id: useId(193), path: '/pz-list', component: '/boozsoft/stock/stock-pingzheng-list/layouts/RouteCache',componentName:'PingZhengList', parentId: useId(284), name: '凭证列表' }),


  /*
    createPlatformMenu({ id: useId(286), path: '/pz-depot-list', component: '/boozsoft/stock/stock-ckpz-list/index', componentName:'RkpzList', parentId: useId(285), name: '入库凭证制单列表' }),
    createPlatformMenu({ id: useId(287), path: '/pz-out-depot-list', component: '/boozsoft/stock/stock-rkpz-list/index', componentName:'CkpzList', parentId: useId(285), name: '出库凭证制单列表' }),
    createPlatformMenu({ id: useId(288), path: '/pz-depot-adjust-list', component: '/boozsoft/stock/stock-crktz-list/index', componentName:'CrktzList', parentId: useId(285), name: '出入库调整单制单列表' }),
  */


  createPlatformMenu({ id: useId(291), path: '/zb-invoice', component: '', parentId: useId(111), name: '采购' }),
  createPlatformMenu({ id: useId(292), path: '/zb-invoice-xs', component: '', parentId: useId(111), name: '销售' }),
  createPlatformMenu({ id: useId(293), path: '/zb-invoice-kc', component: '', parentId: useId(111), name: '库存' }),
  createPlatformMenu({ id: useId(294), path: '/zb-invoice-list', component: '', parentId: useId(111), name: '成本' }),

  createPlatformMenu({ id: useId(391), path: '/cg-gjList', component: '/boozsoft/stock/stock-caigou-statis/index',componentName:'CaigouStatisList', parentId: useId(291), name: '采购统计表' }),
  createPlatformMenu({ id: useId(392), path: '/cg-mxList', component: '/boozsoft/stock/stock-caigou-statis-mx/index',componentName:'CaigouStatisListMX', parentId: useId(291), name: '采购明细表' }),
  createPlatformMenu({ id: useId(393), path: '/cg-phb', component: '/boozsoft/stock/stock-caigou-paihang/index',componentName:'CaigouPaiHang', parentId: useId(291), name: '采购排行榜' }),
  // createPlatformMenu({ id: useId(394), path: '/cg-gysyf', component: '/boozsoft/stock/stock-supplier-balance/index',componentName:'CaigouSupplierBalance', parentId: useId(291), name: '供应商应付余额表' }),
  createPlatformMenu({ id: useId(394), path: '/cg-gysyf', component: '/boozsoft/stock/ying-shou-yu-e-biao/layouts/RouteCache',componentName:'YingFuYuEBiao', parentId: useId(291), name: '供应商应付余额表' }),
  createPlatformMenu({ id: useId(2911), path: '/cg-yfmxb', component: '/boozsoft/stock/ying-fu-ming-xi-biao/layouts/RouteCache',componentName:'YingFuMingXiBiao', parentId: useId(291), name: '供应商应付明细表' }),
  createPlatformMenu({ id: useId(2912), path: '/yfd', component: '/boozsoft/stock/ying-fu-dan/layouts/RouteCache',componentName:'YingFuDan', parentId: useId(999), name: '应付单' }),

  createPlatformMenu({ id: useId(408), path: '/cg-dd-detailed', component: '/boozsoft/stock/stock-caigou-dd-detailed/index', componentName:'CgDdDetailed', parentId: useId(291), name: '采购订单执行表' }),
  createPlatformMenu({ id: useId(409), path: '/cg-dh-detailed', component: '/boozsoft/stock/stock-caigou-dh-detailed/index', componentName:'CgDhDetailed', parentId: useId(291), name: '采购到货单执行表' }),

  createPlatformMenu({ id: useId(394), path: '/zb-gjList',  component: '/boozsoft/stock/stock-tongji-list/index', componentName:'XiaoShouTongJiBiao', parentId: useId(292), name: '销售统计表' }),
  createPlatformMenu({ id: useId(395), path: '/zb-mxList',  component: '/boozsoft/system/blocktable/index', componentName:'MxList', parentId: useId(292), name: '销售明细表' }),
  createPlatformMenu({ id: useId(396), path: '/zb-phbStock', component: '/boozsoft/system/blocktable/index',componentName:'PhbStock', parentId: useId(292), name: '存货销售排行榜' }),
  createPlatformMenu({ id: useId(397), path: '/zb-phbCust', component: '/boozsoft/system/blocktable/index', componentName:'PhbCust', parentId: useId(292), name: '客户销售排行榜' }),
  // createPlatformMenu({ id: useId(398), path: '/zb-ysye', component: '/boozsoft/system/blocktable/index', componentName:'ysyeCust', parentId: useId(292), name: '客户应收余额表' }),
  createPlatformMenu({ id: useId(398), path: '/zb-ysye', component: '/boozsoft/stock/ying-shou-yu-e-biao/layouts/RouteCache', componentName:'YingShouYuEBiao', parentId: useId(292), name: '客户应收余额表' }),
  createPlatformMenu({ id: useId(2921), path: '/zb-ysmxb', component: '/boozsoft/stock/ying-shou-ming-xi-biao/layouts/RouteCache',componentName:'YingShouMingXiBiao', parentId: useId(292), name: '客户应收明细表' }),
  createPlatformMenu({ id: useId(2922), path: '/ysd', component: '/boozsoft/stock/ying-shou-dan/layouts/RouteCache',componentName:'YingShouDan', parentId: useId(999), name: '应收单' }),

  createPlatformMenu({ id: useId(399), path: '/zb-mlAnalyze', component: '/boozsoft/stock/stock-mlAnalyze-list/index',componentName:'XiaoShouMaoLiFenXiBiao2', parentId: useId(292), name: '销售毛利分析表' }),
  createPlatformMenu({ id: useId(400), path: '/zb-execute-list', component: '/boozsoft/stock/stock-execute-list/index', componentName:'XiaoShouDdExec2',parentId: useId(292), name: '销售订单执行表' }),
  createPlatformMenu({ id: useId(457), path: '/zb-execSituation-list', component: '/boozsoft/stock/stock-execSituation-list/index', componentName:'XiaoShouExecSituation2',parentId: useId(292), name: '销货单执行表' }),

  createPlatformMenu({ id: useId(401), path: '/kc-xclQuery', component: '/boozsoft/stock/stock-kc-xcl/index', componentName:'StockKcXcl', parentId: useId(293), name: '存货现存量查询' }),
  createPlatformMenu({ id: useId(402), path: '/kc-pcxclQuery', component: '/boozsoft/stock/stock-kc-pc-xcl/index', componentName:'StockKcPcXcl', parentId: useId(293), name: '存货批次现存量查询' }),
  createPlatformMenu({ id: useId(403), path: '/zb-ledger', component: '/boozsoft/stock/stock-kctz-list/index', componentName:'KctzList', parentId: useId(293), name: '存货台账' }),
  createPlatformMenu({ id: useId(404), path: '/zb-ledger-pc', component: '/boozsoft/stock/stock-kcpctz-list/index', componentName:'KcpctzList', parentId: useId(293), name: '存货批次台账' }),
  createPlatformMenu({ id: useId(408), path: '/kc-output-rk', component: '/boozsoft/stock/stock-outPut-rk-list/index',componentName:'OutPutRk', parentId: useId(293), name: '出入库流水' }),
  createPlatformMenu({ id: useId(405), path: '/kc-numberPool', component: '/boozsoft/stock/stock-number-sfc/index', componentName:'XcNumberPool', parentId: useId(293), name: '数量收发存汇总表' }),
  createPlatformMenu({ id: useId(406), path: '/kc-stock-warning', component: '/boozsoft/stock/stock-warning-list/index',componentName:'XcStockWarning', parentId: useId(293), name: '存货有效期预警表' }),
  createPlatformMenu({ id: useId(407), path: '/kc-amountPool', component: '/boozsoft/stock/stock-money-sfc/index', componentName:'XcamountPool', parentId: useId(294), name: '金额收发存汇总表' }),


  createPlatformMenu({ id: useId(295), path: '/sz-setting', component: '', parentId: useId(113), name: '系统' }),
  /*  createPlatformMenu({ id: useId(296), path: '/sz-stock-manage', component: '', parentId: useId(113), name: '存货档案' }),
    createPlatformMenu({ id: useId(297), path: '/sz-depot-manage', component: '', parentId: useId(113), name: '仓库管理' }),*/
  createPlatformMenu({ id: useId(298), path: '/sz-depot-manage', component: '', parentId: useId(113), name: '价格表' }),

  createPlatformMenu({ id: useId(437), path: '/sz-options', component: '/boozsoft/stock/stock-parameter/index', parentId: useId(295), name: '业务参数' }),
  // createPlatformMenu({ id: useId(402), path: '/sz-process', component: '/boozsoft/system/blocktable/index', parentId: useId(295), name: '业务流程' }),
  createPlatformMenu({ id: useId(438), path: '/sz-period', component: '/boozsoft/stock/stock-period/index', parentId: useId(295), name: '业务期间' }),
  createPlatformMenu({ id: useId(439), path: '/sz-print-template',componentName:'DaYingMoBan', component: '/boozsoft/stock/stock-print-template/index', parentId: useId(295), name: '打印模板' }),
  createPlatformMenu({ id: useId(440), path: '/sz-kemu', component: '/boozsoft/stock/kemu/index', componentName:'Kemu', parentId: useId(295), name: '科目设置' }),

  /*  createPlatformMenu({ id: useId(403), path: '/sz-unit', component: '/boozsoft/system/blocktable/index', parentId: useId(296), name: '计量单位' }),
    createPlatformMenu({ id: useId(404), path: '/stock-class', component: '/boozsoft/stock/stock_class/index', parentId: useId(296), name: '存货分类' }),
    createPlatformMenu({ id: useId(405), path: '/stock_info', component: '/boozsoft/stock/stock_info/index', parentId: useId(296), name: '存货档案' }),
    createPlatformMenu({ id: useId(406), path: '/sz-bom', component: '/boozsoft/system/blocktable/index', parentId: useId(296), name: 'BOM物料清单' }),

    createPlatformMenu({ id: useId(407), path: '/stock_cangku', component: '/boozsoft/stock/stock_cangku/index', parentId: useId(297), name: '仓库' }),
    createPlatformMenu({ id: useId(408), path: '/stock_cangku_level', component: '/boozsoft/stock/stock_cangku_level/index', parentId: useId(297), name: '仓库级次' }),
    createPlatformMenu({ id: useId(409), path: '/stock_cangku_level_record', component: '/boozsoft/stock/stock_cangku_level_record/index', parentId: useId(297), name: '仓库级次档案' }),
    createPlatformMenu({ id: useId(409), path: '/sz-dispatcher', component: '/boozsoft/system/blocktable/index', parentId: useId(297), name: '收发类型' }),*/

  createPlatformMenu({ id: useId(410), path: '/sz-ch-price', component: '/boozsoft/stock/stock_price/index', parentId: useId(298), name: '存货价格表' }),
  createPlatformMenu({ id: useId(412), path: '/sz-kh-price', component: '/boozsoft/stock/stock_price_cust/index', parentId: useId(298), name: '客户价格表' }),
  createPlatformMenu({ id: useId(411), path: '/sz-cx-price', component: '/boozsoft/stock/stock_price_promote/index', parentId: useId(298), name: '客户促销价格表' }),
  createPlatformMenu({ id: useId(413), path: '/sz-gys-price', component: '/boozsoft/stock/stock_price_sup/index', parentId: useId(298), name: '供应商采购价格表' }),

  createPlatformMenu({ id: useId(414), path: '/sz-gys-mx', component: '/boozsoft/stock/stock_price_promote/index3', hidden:true, parentId: useId(298), name: '客户促销价格明细表' }),
  createPlatformMenu({ id: useId(415), path: '/kc-inventory-mx', component: '/boozsoft/stock/stock_taking/index3', hidden:true, parentId: useId(298), name: '盘点单明细' }),
  createPlatformMenu({ id: useId(416), path: '/kc-transfer', component: '/boozsoft/stock/stock_transfer/index', hidden:true, componentName:'DbdMx', parentId: useId(298), name: '调拨单明细' }),
  createPlatformMenu({ id: useId(417), path: '/kc-shape', component: '/boozsoft/stock/stock_conversion_add/index', hidden:true, parentId: useId(298), name: '形态转换单明细' }),

  /* createPlatformMenu({ id: useId(416), path: '/kc-py-mx', component: '/boozsoft/stock/stock-taking-py/index3',  parentId: useId(232), name: '盘盈明细列表' }),
   createPlatformMenu({ id: useId(417), path: '/kc-pk-mx', component: '/boozsoft/stock/stock-taking-pk/index3',  parentId: useId(232), name: '盘亏明细列表' }),*/
  /*createPlatformMenu({ id: useId(421), path: '/maker', component: '', parentId: useId(110), name: '制单' }),
    createPlatformMenu({ id: useId(422), path: '/list', component: '', parentId: useId(110), name: '列表' }),
    createPlatformMenu({ id: useId(431), path: '/pz-zjzd', component: '/gdzc/dashboard/analysis/index', parentId: useId(421), name:'折旧制单'}),
    createPlatformMenu({ id: useId(432), path: '/pz-list', component: '/boozsoft/gdzc/fa-accvoucher/index', parentId: useId(422), name: '凭证列表'}),
    createPlatformMenu({ id: useId(433), path: '/pz-duizhang', component: '/gdzc/dashboard/analysis/index', parentId: useId(422), name: '财务对账'}),*/

  createPlatformMenu({ id: useId(425), path: '/jz-invoice', component: '', parentId: useId(112), name: '业务' }),
  createPlatformMenu({ id: useId(435), path: '/jz-close',componentName:'StockJieZhang', component: '/boozsoft/stock/stock-jie-zhang/index', parentId: useId(425), name: '月末结账'}),
  createPlatformMenu({ id: useId(436), path: '/jz-unclose',componentName:'StockUnJieZhang', component: '/boozsoft/stock/stock-un-jie-zhang/index', parentId: useId(425), name: '取消结账'}),
];


export const platformMenuIds = {
  key:'CuoHuoRecord',
  id:platformId,
  sort:9,
  menuIds:menus.map(item=>item.id)
};

