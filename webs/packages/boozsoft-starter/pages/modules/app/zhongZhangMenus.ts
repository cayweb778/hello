import {createMenu, createModel} from '/@/../pages/rbac/utils/mockMenuUtil';

const platformId = 1001;

function createPlatformMenu(params) {
    return createMenu({...params, platformId})
}
export const menus = [

    createModel({
        id: platformId,
        isCloud: false,
        isTargetBlank: false,
        isOutLink: false,
        name: '总账',
        category: 102,
        sortNo: 1,
    }),
    createPlatformMenu({
        id: 8800,
        path: '/',
        redirect: '/zhongZhang/home/welcome',
        component: 'LAYOUT',
        parentId: 0,
        hidden: true,
        name: '根目录'
    }),
    createPlatformMenu({
        id: 10000,
        path: 'welcome',
        component: '/platforms/account/Home/index',
        parentId: 10001,
        name: '首页',
    }),
    createPlatformMenu({
        id: 10001,
        path: '/zhongZhang/home',
        component: 'LAYOUT',
        parentId: 0,
        name: '工作台'
    }),
    /*************** 菜单编排区 ****************/

    /********** 一级目录 ***********/
    createPlatformMenu({
        id: 8801,
        path: '/zhongZhang/qichus',
        component: 'LAYOUT',
        parentId: 0,
        name: '期初余额'
    }),
    createPlatformMenu({
        id: 8802,
        path: '/zhongZhang/vouchers',
        component: 'LAYOUT',
        parentId: 0,
        name: '记账凭证'
    }),
    createPlatformMenu({
        id: 8803,
        path: '/zhongZhang/electronic-invoice',
        component: 'LAYOUT',
        parentId: 0,
        name: '发票管理',
    }),
    createPlatformMenu({
        id: 8805,
        path: '/zhongZhang/cashiers',
        component: 'LAYOUT',
        parentId: 0,
        name: '现金流量'
    }),
    createPlatformMenu({
        id: 8806,
        path: '/zhongZhang/account-kemu',
        component: 'LAYOUT',
        parentId: 0,
        name: '科目账'
    }),
    createPlatformMenu({
        id: 8807,
        path: '/zhongZhang/account-wanglai',
        component: 'LAYOUT',
        parentId: 0,
        name: '往来账'
    }),
    createPlatformMenu({
        id: 8808,
        path: '/zhongZhang/account-fuzhu',
        component: 'LAYOUT',
        parentId: 0,
        name: '辅助账'
    }),
    createPlatformMenu({
        id: 8809,
        path: '/zhongZhang/transfer',
        component: 'LAYOUT',
        parentId: 0,
        name: '转账业务'
    }),
    createPlatformMenu({
        id: 8810,
        path: '/zhongZhang/ends',
        component: 'LAYOUT',
        parentId: 0,
        name: '期末处理'
    }),
    createPlatformMenu({
        id: 8811,
        path: '/zhongZhang/setting',
        component: 'LAYOUT',
        parentId: 0,
        name: '设置'
    }),
    /********** 一级目录 ***********/
    
    /******** 二级目录 *********/
    createPlatformMenu({id: 8901, path: 'qichu-menu', component: '', parentId: 8801, name: '期初'}),
    
    createPlatformMenu({id: 8905, path: 'handle-voucher', component: '', parentId: 8802, name: '业务处理'}),
    createPlatformMenu({id: 8906, path: 'manage-voucher', component: '', parentId: 8802, name: '凭证管理'}),
    createPlatformMenu({id: 8907, path: 'book-bus', component: '', parentId: 8802, name: '平行记账'}),

    createPlatformMenu({id: 8910, path: 'ei-exec', component: '', parentId: 8803, name: '业务处理'}),
    createPlatformMenu({id: 8911, path: 'ei-mange', component: '', parentId: 8803, name: '发票管理'}),

    createPlatformMenu({id: 8920, path: 'ab-xjll', component: '', parentId: 8805, name: '业务处理'}),
    createPlatformMenu({id: 8921, path: 'ab-xjll-gl', component: '', parentId: 8805, name: '管理'}),

    createPlatformMenu({id: 8930, path: 'ab-kemuzhang-hz', component: '', parentId: 8806, name: '汇总'}),
    createPlatformMenu({id: 8931, path: 'ab-kemuzhang-mx', component: '', parentId: 8806, name: '明细'}),

    createPlatformMenu({id: 8940, path: 'kehu-contacts', component: '', parentId: 8807, name: '客户往来'}),
    createPlatformMenu({id: 8941, path: 'gys-contacts', component: '', parentId: 8807, name: '供应商往来'}),
    createPlatformMenu({id: 8942, path: 'geren-contacts', component: '', parentId: 8807, name: '个人往来'}),
    createPlatformMenu({id: 8943, path: 'kehu-contacts-hx', component: '', parentId: 8807, name: '客户往来核销'}),
    createPlatformMenu({id: 8944, path: 'gys-contacts-hx', component: '', parentId: 8807, name: '供应商往来核销'}),
    createPlatformMenu({id: 8945, path: 'geren-contacts-hx', component: '', parentId: 8807, name: '个人往来核销'}),

    createPlatformMenu({id: 8950, path: 'project-assist', component: '', parentId: 8808, name: '项目辅助账'}),
    createPlatformMenu({id: 8951, path: 'dept-assist', component: '', parentId: 8808, name: '部门辅助账'}),
    createPlatformMenu({id: 8952, path: 'synthesize-assist', component: '', parentId: 8808, name: '综合辅助账'}),

    createPlatformMenu({id: 8960, path: 'handle-transfer', component: '', parentId: 8809, name: '业务处理'}),
    createPlatformMenu({id: 8961, path: 'manage-transfer', component: '', parentId: 8809, name: '模版设置'}),

    createPlatformMenu({id: 8970, path: 'ends-amort-with', component: '', parentId: 8810, name: '业务处理'}),
    createPlatformMenu({id: 8971, path: 'ends-em-bill', component: '', parentId: 8810, name: '结账'}),
    createPlatformMenu({id: 8972, path: 'ends-synthesize', component: '', parentId: 8810, name: '综合'}),


    createPlatformMenu({id: 8980, path: 'settings0', component: '', parentId: 8811, name: '设置'}),
    createPlatformMenu({id: 8981, path: 'settings1', component: '', parentId: 8811, name: '财务'}),
    createPlatformMenu({id: 8982, path: 'settings2', component: '', parentId: 8811, name: '平行记账'}),
    createPlatformMenu({id: 8983, path: 'settings3', component: '', parentId: 8811, name: '模版'}),
    /******** 二级目录 *********/


    /******* 三级目录 *******/
    createPlatformMenu({
        id: 9001,
        path: 'kemu-qichu-list',
        componentName: 'SubjectInitialBalance',
        component: '/boozsoft/system/subjectInitialBalance/index',
        parentId: 8901,
        name: '科目期初余额',
    }),
    /* createPlatformMenu({
       id: 9011,
       path: 'kemu-qichu-ssph-list',
       componentName: 'SubjectInitialBalanceSsph',
       component: '/boozsoft/system/subjectInitialBalanceSsph/index',
       parentId: 8901,
       name: '期初余额试算平衡',
     }),*/
    createPlatformMenu({
        id: 9002,
        path: 'fuzhu-qichu-list',
        componentName: 'SubjectInitialBalanceFuZhu',
        component: '/boozsoft/system/subjectInitialBalanceFuZhu/index',
        parentId: 8901,
        name: '辅助核算期初',
    }),
    createPlatformMenu({
        id: 9003,
        path: 'xj-qichu-list',
        componentName: 'XJLLQC',
        component: '/boozsoft/xian_jin_liu_liang/xian_jin_liu_liang_qc/index',
        parentId: 8901,
        name: '现金流量期初',
    }),

    createPlatformMenu({
        id: 9010,
        path: 'tinazhi-voucher-two',
        component: '/platforms/account/PingzhengFillinTwo/index',
        componentName: 'TianZhiPingZhengTwo',
        parentId: 8905,
        name: '填制财务凭证',
        hidden: false
    }),

    createPlatformMenu({
        id: 9011,
        path: 'cashier-signature',
        component: '/boozsoft/system/accvoucher-cashier/index',
        parentId: 8905,
        name: '出纳签字'
    }),
    createPlatformMenu({
        id: 9012,
        path: 'super-signature',
        component: '/boozsoft/system/accvoucher-super-sign/index',
        parentId: 8905,
        name: '主管签字'
    }),
    createPlatformMenu({
        id: 9013,
        path: 'review-voucher',
        component: '/boozsoft/system/accvoucher-review/index',
        parentId: 8905,
        name: '凭证审核'
    }),
    createPlatformMenu({
        id: 9014,
        path: 'bookkeeping',
        component: '/boozsoft/system/accvoucher-book/index',
        parentId: 8905,
        name: '凭证记账'
    }),

    createPlatformMenu({
        id: 9015,
        path: 'list-voucher',
        component: '/boozsoft/system/accvoucher/index',
        componentName: 'PingZhengList',
        parentId: 8906,
        name: '凭证列表',
    }),
    createPlatformMenu({
        id: 9016,
        path: 'print-voucher',
        component: '/boozsoft/system/blocktable/index',
        parentId: 8906,
        name: '凭证打印'
    }),


    createPlatformMenu({
        id: 9017,
        path: 'budget-voucher-association-add',
        component: '/boozsoft/system/fuzhu-hesuan/index',
        parentId: 8907,
        name: '填制预算凭证'
    }),
    createPlatformMenu({
        id: 9018,
        path: 'budget-voucher-association',
        component: '/boozsoft/system/fuzhu-hesuan/index',
        parentId: 8907,
        name: '预算凭证关联'
    }),
    createPlatformMenu({
        id: 9019,
        path: 'budget-voucher-table',
        component: '/boozsoft/system/fuzhu-hesuan/index',
        parentId: 8907,
        name: '预算凭证列表'
    }),
    createPlatformMenu({
        id: 9020,
        path: 'difference-analysis-table',
        component: '/boozsoft/system/fuzhu-hesuan/index',
        parentId: 8907,
        name: '差异分析明细表'
    }),

    createPlatformMenu({
        id: 9021,
        path: 'ei-add-invoice',
        component: '/boozsoft/system/electronic-invoice/popup/edit',
        parentId: 8910,
        name: '拍照识别',
    }),
    createPlatformMenu({
        id: 9022,
        path: 'ei-add-invoice',
        component: '/boozsoft/system/electronic-invoice/popup/edit',
        parentId: 8910,
        name: '发票导入',
    }),

    createPlatformMenu({
        id: 9023,
        path: 'ei-invoice-like',
        component: '/boozsoft/system/electronic-invoice/index',
        parentId: 8911,
        name: '发票列表',
    }),
    createPlatformMenu({
        id: 9024,
        path: 'ei-invoice-like-gl',
        component: '/boozsoft/system/electronic-invoice/index',
        parentId: 8911,
        name: '关联凭证',
    }),

    createPlatformMenu({
        id: 9031,
        path: 'xj-ll-add',
        component: '/boozsoft/xian_jin_liu_liang/xian_jin_liu_liang/index',
        parentId: 8920,
        name: '现金流量录入',
    }),
    createPlatformMenu({
        id: 9032,
        path: 'abx-xjll-mxtable',
        component: '/boozsoft/xian_jin_liu_liang/xian_jin_liu_liang_mx/index',
        parentId: 8921,
        name: '现金流量明细表',
    }),
    createPlatformMenu({
        id: 9033,
        path: 'abx-xjll-sumtable',
        component: '/boozsoft/xian_jin_liu_liang/xian_jin_liu_liang_tj/index',
        parentId: 8921,
        name: '现金流量统计表',
    }),

    createPlatformMenu({
        id: 9041,
        path: 'abk-total',
        component: '/boozsoft/system/general-ledger/index',
        parentId: 8930,
        name: '总账',
    }),
    createPlatformMenu({
        id: 9042,
        path: 'abk-yetable',
        component: '/boozsoft/system/book-balance/index',
        parentId: 8930,
        name: '余额表',
    }),

    createPlatformMenu({
        id: 9043,
        path: 'abk-manytable',
        component: '/boozsoft/system/km-multi-generalledger/index',
        parentId: 8930,
        name: '多栏账',
    }),
    createPlatformMenu({
        id: 9044,
        path: 'kemu-total',
        component: '/boozsoft/system/km-huizong/index',
        parentId: 8930,
        name: '科目汇总表',
    }),
    createPlatformMenu({
        id: 9045,
        path: 'abk-mxtable',
        component: '/boozsoft/system/kmmingxi/index',
        parentId: 8931,
        name: '明细表',
    }),
    createPlatformMenu({
        id: 9046,
        path: 'abk-rjtable',
        component: '/boozsoft/system/kemu-day-book/index',
        componentName: 'RiJiZhang',
        parentId: 8931,
        name: '日记账',
    }),
    createPlatformMenu({
        id: 9047,
        path: 'abk-xstable',
        component: '/boozsoft/system/accvoucher-xushizhang/index',
        parentId: 8931,
        name: '序时账',
    }),

    createPlatformMenu({
        id: 9051,
        path: 'kehu-contacts-kmye',
        component: '/boozsoft/system/blocktable/index',
        parentId: 8940,
        name: '客户科目余额表',
    }),
    createPlatformMenu({
        id: 9052,
        path: 'kehu-contacts-kmye',
        component: '/boozsoft/system/blocktable/index',
        parentId: 8940,
        name: '客户科目余额表',
    }),
    createPlatformMenu({
        id: 9053,
        path: 'kehu-contacts-kmmx',
        component: '/boozsoft/system/blocktable/index',
        parentId: 8940,
        name: '客户科目明细表',
    }),
    createPlatformMenu({
        id: 9054,
        path: 'kehu-contacts-khye',
        component: '/boozsoft/system/blocktable/index',
        parentId: 8940,
        name: '科目客户余额表',
    }),
    createPlatformMenu({
        id: 9055,
        path: 'kehu-contacts-khmx',
        component: '/boozsoft/system/blocktable/index',
        parentId: 8940,
        name: '科目客户明细表',
    }),
    createPlatformMenu({
        id: 9056,
        path: 'kehu-contacts-bmye',
        component: '/boozsoft/system/blocktable/index',
        parentId: 8940,
        name: '客户部门余额表',
    }),
    createPlatformMenu({
        id: 9057,
        path: 'kehu-contacts-xmye',
        component: '/boozsoft/system/blocktable/index',
        parentId: 8940,
        name: '客户项目余额表',
    }),
    createPlatformMenu({
        id: 9058,
        path: 'kehu-contacts-flye',
        component: '/boozsoft/system/blocktable/index',
        parentId: 8940,
        name: '客户分类余额表',
    }),

    createPlatformMenu({
        id: 9059,
        path: 'gys-contacts-kmye',
        component: '/boozsoft/system/blocktable/index',
        parentId: 8941,
        name: '供应商科目余额表',
    }),
    createPlatformMenu({
        id: 9060,
        path: 'gys-contacts-kmmx',
        component: '/boozsoft/system/blocktable/index',
        parentId: 8941,
        name: '供应商科目明细表',
    }),
    createPlatformMenu({
        id: 9061,
        path: 'gys-contacts-khye',
        component: '/boozsoft/system/blocktable/index',
        parentId: 8941,
        name: '科目供应商余额表',
    }),
    createPlatformMenu({
        id: 9062,
        path: 'gys-contacts-khmx',
        component: '/boozsoft/system/blocktable/index',
        parentId: 8941,
        name: '科目供应商明细表',
    }),
    createPlatformMenu({
        id: 9063,
        path: 'gys-contacts-bmye',
        component: '/boozsoft/system/blocktable/index',
        parentId: 8941,
        name: '供应商部门余额表',
    }),
    createPlatformMenu({
        id: 9064,
        path: 'gys-contacts-xmye',
        component: '/boozsoft/system/blocktable/index',
        parentId: 8941,
        name: '供应商项目余额表',
    }),
    createPlatformMenu({
        id: 9065,
        path: 'gys-contacts-flye',
        component: '/boozsoft/system/blocktable/index',
        parentId: 8941,
        name: '供应商分类余额表',
    }),

    createPlatformMenu({
        id: 9066,
        path: 'geren-contacts-kmye',
        component: '/boozsoft/system/blocktable/index',
        parentId: 8942,
        name: '个人科目余额表',
    }),
    createPlatformMenu({
        id: 9067,
        path: 'geren-contacts-kmmx',
        component: '/boozsoft/system/blocktable/index',
        parentId: 8942,
        name: '个人科目明细表',
    }),
    createPlatformMenu({
        id: 9068,
        path: 'geren-contacts-khye',
        component: '/boozsoft/system/blocktable/index',
        parentId: 8942,
        name: '科目个人余额表',
    }),
    createPlatformMenu({
        id: 9069,
        path: 'geren-contacts-khmx',
        component: '/boozsoft/system/blocktable/index',
        parentId: 8942,
        name: '科目个人明细表',
    }),
    createPlatformMenu({
        id: 9070,
        path: 'geren-contacts-bmmx',
        component: '/boozsoft/system/blocktable/index',
        parentId: 8942,
        name: '个人部门明细表',
    }),

    createPlatformMenu({
        id: 9071,
        path: 'kehu-contacts-dunning',
        component: '/boozsoft/system/aging-range/index',
        parentId: 8943,
        name: '账龄区间设置',
    }),
    createPlatformMenu({
        id: 9072,
        path: 'write-kehu-history',
        component: '/boozsoft/system/write-history/index',
        parentId: 8943,
        name: '催款单',
    }),
    createPlatformMenu({
        id: 9073,
        path: 'write-kehu',
        component: '/boozsoft/system/write-off/index',
        parentId: 8943,
        name: '往来核销两清',
    }),
    createPlatformMenu({
        id: 9074,
        path: 'kehu-contacts-analysis',
        component: '/boozsoft/system/blocktable/index',
        parentId: 8943,
        name: '账龄分析',
    }),

    createPlatformMenu({
        id: 9075,
        path: 'gys-contacts-dunning',
        component: '/boozsoft/system/aging-range/index',
        parentId: 8944,
        name: '账龄区间设置',
    }),
    createPlatformMenu({
        id: 9076,
        path: 'gys-contacts-two',
        component: '/boozsoft/system/write-gys/index',
        parentId: 8944,
        name: '往来核销两清',
    }),
    createPlatformMenu({
        id: 9077,
        path: 'gys-contacts-history',
        component: '/boozsoft/system/write-history/index',
        parentId: 8944,
        name: '催款单',
    }),
    createPlatformMenu({
        id: 9078,
        path: 'gys-contacts-analysis',
        component: '/boozsoft/system/blocktable/index',
        parentId: 8944,
        name: '账龄分析',
    }),

    createPlatformMenu({
        id: 9079,
        path: 'geren-contacts-dunning',
        component: '/boozsoft/system/aging-range/index',
        parentId: 8945,
        name: '账龄区间设置',
    }),
    createPlatformMenu({
        id: 9080,
        path: 'write-geren',
        component: '/boozsoft/system/write-geren/index',
        parentId: 8945,
        name: '往来两清核销',
    }),
    createPlatformMenu({
        id: 9081,
        path: 'write-geren-history',
        component: '/boozsoft/system/write-history/index',
        parentId: 8945,
        name: '个人核销历史',
    }),
    createPlatformMenu({
        id: 9082,
        path: 'geren-contacts-analysis',
        component: '/boozsoft/system/blocktable/index',
        parentId: 8945,
        name: '账龄分析',
    }),

    createPlatformMenu({
        id: 9091,
        path: 'abfpc-sumtable',
        component: '/boozsoft/system/projcet-generalledger/index',
        parentId: 8950,
        name: '项目科目总账',
    }),

    createPlatformMenu({
        id: 9092,
        path: 'abfpc-kumu-sumtable',
        component: '/boozsoft/system/projcet-km-generalledger/index',
        parentId: 8950,
        name: '科目项目总账',
    }),
    createPlatformMenu({
        id: 9093,
        path: 'abfpc-three-sumtable',
        component: '/boozsoft/system/blocktable/index',
        parentId: 8950,
        name: '部门项目总账',
    }),
    createPlatformMenu({
        id: 9094,
        path: 'abfpc-dept-sumtable',
        component: '/boozsoft/system/project-dept-generalledger/index',
        parentId: 8950,
        name: '项目部门总账',
    }),

    createPlatformMenu({
        id: 9095,
        path: 'abfpc-mxtable',
        component: '/boozsoft/system/project-km-mx/index',
        parentId: 8950,
        componentName: 'ProKmMx',
        name: '项目科目明细表',
    }),
    createPlatformMenu({
        id: 9096,
        path: 'abfpc-kemu-mxtable',
        component: '/boozsoft/system/km-pro-mx/index',
        parentId: 8950,
        componentName: 'KmProMx',
        name: '科目项目明细表',
    }),
    createPlatformMenu({
        id: 9097,
        path: 'abfpc-deptp-mtable',
        component: '/boozsoft/system/blocktable/index',
        parentId: 8950,
        name: '部门项目明细表',
    }),
    createPlatformMenu({
        id: 9098,
        path: 'abfpc-dept-mtable',
        component: '/boozsoft/system/blocktable/index',
        parentId: 8950,
        name: '项目部门明细表',
    }),

    createPlatformMenu({
        id: 9099,
        path: 'abfpc-types-mxtable',
        component: '/boozsoft/system/blocktable/index',
        parentId: 8950,
        name: '项目分类明细表',
    }),
    createPlatformMenu({
        id: 9100,
        path: 'abfpc-many-mxtable',
        component: '/boozsoft/system/blocktable/index',
        parentId: 8950,
        name: '项目多栏明细表',
    }),

    createPlatformMenu({
        id: 9110,
        path: 'abfdc-sumtable',
        component: '/boozsoft/system/dept-generalledger/index',
        parentId: 8951,
        name: '部门科目总账',
    }),
    createPlatformMenu({
        id: 9111,
        path: 'abfdc-kemu-mxtable',
        component: '/boozsoft/system/dept-km-mx/index',
        parentId: 8951,
        componentName: 'DeptKmMx',
        name: '部门科目明细账',
    }),
    createPlatformMenu({
        id: 9112,
        path: 'abfdc-kumu-sumtable',
        component: '/boozsoft/system/km-dept-generalledger/index',
        parentId: 8951,
        name: '科目部门总账',
    }),
    createPlatformMenu({
        id: 9113,
        path: 'abfdc-mxtable',
        component: '/boozsoft/system/km-dept-mx/index',
        parentId: 8951,
        componentName: 'KmDeptMx',
        name: '科目部门明细表',
    }),
    createPlatformMenu({
        id: 9114,
        path: 'abfdc-many-sumtable',
        component: '/boozsoft/system/blocktable/index',
        parentId: 8951,
        name: '部门多栏总账',
    }),
    createPlatformMenu({
        id: 9115,
        path: 'abfdc-many-mxtable',
        component: '/boozsoft/system/blocktable/index',
        parentId: 8951,
        name: '部门多栏明细表',
    }),


    createPlatformMenu({
      id: 9116,
      path: 'abfzc-manyfuzhu-yueable',
      component: '/boozsoft/system/blocktable/index',
      parentId: 8952,
      name: '多辅助月表',
    }),
    createPlatformMenu({
        id: 9117,
        path: 'abfzc-manyfuzhu-mxtable',
        component: '/boozsoft/system/blocktable/index',
        parentId: 8952,
        name: '多辅助明细表',
    }),
    createPlatformMenu({
      id: 9118,
      path: 'abfzc-manyfuzhu-mxtable',
      component: '/boozsoft/system/blocktable/index',
      parentId: 8952,
      name: '多辅助明细表',
    }),

    createPlatformMenu({
        id: 9121,
        path: 'tg-carry-forward',
        componentName: 'QIJIANSUANYIJIEZHUAN',
        component: '/boozsoft/system/definition-carry-forward/index',
        parentId: 8960,
        name: '期间损益结转',
    }),
    createPlatformMenu({
        id: 9122,
        path: 'tg-correspond',
        component: '/boozsoft/system/definition-xscb/index',
        parentId: 8960,
        name: '销售成本结转',
    }),
    createPlatformMenu({
        id: 9123,
        path: 'tg-correspond',
        component: '/boozsoft/system/definition-sccb/index',
        parentId: 8960,
        name: '生产成本结转',
    }),
    createPlatformMenu({
        id: 9124,
        path: 'tg-exchange-status',
        component: '/boozsoft/system/definition-exchange-status/index',
        parentId: 8960,
        name: '汇兑损益结转',
    }),

    createPlatformMenu({
        id: 9125,
        path: 'tg-customize',
        component: '/boozsoft/system/definition-customize/index',
        parentId: 8960,
        name: '自定义结转',
    }),

    createPlatformMenu({
        id: 9126,
        path: 'abfgc-sj-one',
        component: '/boozsoft/system/report-template-system/index',
        componentName: 'ReportTemplateSystem',
        parentId: 8961,
        name: '系统模板',
    }),
    createPlatformMenu({
        id: 9127,
        path: 'abfgc-sj-two',
        component: '/boozsoft/system/report-template-custom/index',
        componentName: 'ReportTemplateCustom',
        parentId: 8961,
        name: '自定义模板',
    }),

    createPlatformMenu({
        id: 9131,
        path: 'cost-amortize',
        component: '/boozsoft/system/blocktable/index',
        parentId: 8970,
        name: '费用摊销',
    }),
    createPlatformMenu({
        id: 9132,
        path: 'cost-accruals',
        component: '/boozsoft/system/blocktable/index',
        parentId: 8970,
        name: '费用预提',
    }),


    createPlatformMenu({
        id: 9133,
        path: 'bill-list',
        component: '/boozsoft/system/qi-mo-jie-zhang/index',
        parentId: 8971,
        name: '期末结账',
    }),
    createPlatformMenu({
        id: 9134,
        path: 'un-bill-list',
        component: '/boozsoft/system/qi-mo-jie-zhang/index',
        parentId: 8971,
        name: '取消结账',
    }),

    createPlatformMenu({
        id: 9135,
        path: 'work-check',
        component: '/boozsoft/system/gong-zuo-jian-cha/index',
        parentId: 8972,
        name: '工作检查',
    }),

    createPlatformMenu({
        id: 9136,
        path: 'bill-year',
        component: '/boozsoft/system/blocktable/index',
        parentId: 8973,
        name: '年度结转',
    }),


    createPlatformMenu({
        id: 9141,
        path: 'setting-finance-info',
        component: '/boozsoft/system/financial-info/index',
        parentId: 8980,
        name: '系统参数',
    }),
    createPlatformMenu({
        id: 9142,
        path: 'account-panel-info',
        component: '/boozsoft/system/accpanel/index',
        parentId: 8980,
        name: '总账面板',
    }),


    createPlatformMenu({
        id: 9143,
        path: 'setting-basis-info',
        component: '/boozsoft/system/acccode2/index',
        parentId: 8981,
        name: '会计科目',
    }),

    createPlatformMenu({
        id: 9144,
        path: 'parameter-settings',
        component: '/boozsoft/system/fuzhu-hesuan/index',
        parentId: 8981,
        name: '辅助核算定义'
    }),

    createPlatformMenu({
        id: 9145,
        path: 'project_class',
        component: '/boozsoft/system/project_item_zd/index',
        componentName: 'projectItemZd',
        parentId: 8981,
        name: '项目大类科目'
    }),
    createPlatformMenu({
        id: 9146,
        path: 'xj-km-add',
        component: '/boozsoft/xian_jin_liu_liang/xian_jin_km/index',
        parentId: 8981,
        name: '指定现金流量科目',
    }),


    createPlatformMenu({
        id: 9147,
        path: 'relationship-maintenance',
        component: '/boozsoft/system/parallel-jz/index',
        componentName: 'parallelJz',
        parentId: 8982,
        name: '平行记账科目'
    }),
    createPlatformMenu({
        id: 9148,
        path: 'difference-analysis-code',
        component: '/boozsoft/system/difference-fx/index',
        componentName: 'differenceFx',
        parentId: 8982,
        name: '差异分析科目'
    }),
    createPlatformMenu({
        id: 9149,
        path: 'subject-comparison',
        component: '/boozsoft/system/code-compare/index',
        componentName: 'codeCompare',
        parentId: 8982,
        name: '科目对照设置'
    }),

    createPlatformMenu({
        id: 9150,
        path: 'accvoucher-template',
        component: '/boozsoft/system/accvoucher-template/index',
        parentId: 8983,
        name: '凭证导入模板',
    }),
    createPlatformMenu({
        id: 9151,
        path: 'code-template',
        component: '/boozsoft/system/code_template/index',
        parentId: 8983,
        name: '会计科目导入模板',
    }),



    /******* 三级目录 *******/

    /*************** 菜单编排区 ****************/

 /*   createPlatformMenu({
        id: 890301,
        parentId: 8903,
        perms: 'zhongzhang:pingzheng:save',
        permsType: 2,
        name: '保存按钮',
        category: 2
    }),

    createPlatformMenu({
        id: 890302,
        parentId: 8903,
        perms: 'zhongzhang:pingzheng:chongxiaoandother',
        permsType: 2,
        name: '冲销复制插入按钮',
        category: 2
    }),
    createPlatformMenu({
        id: 890303,
        parentId: 8903,
        perms: 'zhongzhang:pingzheng:excel,zhongzhang:pingzheng:print',
        permsType: 2,
        name: '打印导出按钮',
        category: 2
    }),
    createPlatformMenu({
        id: 890304,
        parentId: 8903,
        perms: 'zhongzhang:pingzheng:switchaccount',
        permsType: 2,
        name: '切换账套按钮',
        category: 2
    }),*/

];


export const platformMenuIds = {
    key: 'zhongZhang',
    id: platformId,
    sort: 1,
    menuIds: menus.map(item => item.id)
};

