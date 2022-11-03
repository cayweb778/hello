package org.boozsoft.domain.entity.group;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "数据表：存货管理账套表", description = "存货管理账套表")
@Table("_app_group_stock_account")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class GroupStockAccount {

    @ApiModelProperty(value = "主键id", position = 0)
    @Id
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "公司唯一码", position = 1)
    private String uniqueCode;
    @ApiModelProperty(value = "账套代码", position = 1)
    private String coCode;
    @ApiModelProperty(value = "关联财务账套代码", position = 1)
    private String associateCoCode;
    @ApiModelProperty(value = "账套标识")
    private String stockAccId;
    @ApiModelProperty(value = "账套名称", position = 2)
    private String stockAccName;
    @ApiModelProperty(value = "启用日期", position = 3)
    private String startDate;
    @ApiModelProperty(value = "本位币代码", position = 4)
    private String currency;
    @ApiModelProperty(value = "国家（地区）代码", position = 5)
    private String country;
    @ApiModelProperty(value = "状态", position = 6)
    private String flag;
    @ApiModelProperty(value = "成本计价方式（默认：1实时计价，0或其他为定时计价）", position = 7)
    private String jjfs;
    @ApiModelProperty(value = "建账操作员")
    private String createUser;
    @ApiModelProperty(value = "建账时间")
    private String createTime;
    @ApiModelProperty(value = "是否为独立账套 1 or 0")
    private String independent;

    private String beiyong1;
    private String beiyong2;
    private String beiyong3;
    private String beiyong4;
    private String beiyong5;

    @ApiModelProperty(value = "业务删除")
    private String isDel;
    @ApiModelProperty(value = "删除操作员")
    private String delName;
    @ApiModelProperty(value = "删除时间")
    private String delDate;

    private String cgShDhd;           // 到货单审核生成入库单（默认值1是，0或空否）
    private String cgThDhd;           // 采购退货必须参照到货单（默认值1是，0或空否）
    private String cgOutFapiao;       // 允许超到货单生成发票（1是，默认值0或空否）
    private String cgOutDhd;          // 允许超订单生成到货单（1是，默认值0或空否）
    private String cgSaveCheck;       // 到货单审核时检查可用量（1是，默认值0或空否）
    private String cgPriceIsRate;    // 供应商价格表含税（默认值1是，0或空否）
    private String cgPriceZjyc;       // 若价格表未设置取最近一次售价（默认值1是，0或空否）
    private String cgPriceZgjj;       // 采购是否最高进价控制（1是，默认值0或空否）
    private String cgPriceZgjjPwd;   // 采购最高进价控制密码（不能小于六位任意字符，加密保存）
    private String cgNumWs;           // 采购默认税率（默认六位小数位）
    private String cgPriceWs;         // 采购订单到货单数量小数位显示（0-10）
    private String cgRateWs;          // 采购订单到货单单价小数位显示（0-10）
    private String cgLiucheng;         // 采购订单到货单税率小数位显示（0-12）
    private String cgRate;              // 采购默认税率（默认六位小数位）
    private String cgKzqxSup;   // 控制客户权限 1
    private String cgKzqxSupClass;   // 控制客户权限 1
    private String cgKzqxDepot;   // 控制仓库权限 0
    private String cgKzqxStock;   // 控制存货权限 0
    private String cgKzqxStockClass;   // 控制存货权限 0
    private String cgInputCloseOrder;   // 出库完成自动关闭订单 0


    private String kcIsLck;          // 是否允许零出库(1允许，默认值0或空不允许)
    private String kcKylCg;          // 可用量是否包含采购在途量和销售在途量（默认值1是，0或空否）
    private String kcCgrkCheck;          // 采购入库单审核后修改现存量（默认值1是，0或空否，保存即修改）
    private String kcXsckCheck;          // 销售出库单审核后修改现存量（默认值1是，1或空否）
    private String kcQtcrkCheck;          // 其他出入库单审核后修改现存量（默认值1是，2或空否）
    private String kcNumWs;          // 数量小数位显示（0-10，默认值2）
    private String kcPriceWs;          // 单价小数位显示（0-10，默认值2）
    private String kcJcjrCheck;          // 借入借出库单审核后修改现存量（默认值1是，2或空否）
    private String kcCgrkSaveCheck;          // 采购到货单保存时修改在途入库量（默认值1是，2或空否）
    private String kcXsckSaveCheck;          // 销货单保存时修改在途出库量（默认值1是，2或空否）
    private String kcCostAccounting;          // 成本核算方式
    private String kcEstimated;          // 暂估方式
    private String kcKzqxDepot;   // 控制仓库权限 0
    private String kcKzqxStock;   // 控制存货权限 0
    private String kcKzkcHighBottom;   // 最高最低库存控制 0
    private String kcKzgzAvailability;   // 出库跟踪入库控制可用量 0

    private String xsRate;     // 销售默认税率（默认六位小数位）
    private String xsDhRc;        // 销售单行税额容差（小于等于零的正实数）
    private String xsZdRc;        // 销售整单税额容差（小于等于零的正实数）
    private String xsNumWs;       // 销售订单销货单数量小数位显示（0-10）
    private String xsPriceWs;     // 销售订单销货单单价小数位显示（0-10）
    private String xsRateWs;      // 销售订单销货单税率小数位显示（0-12）
    private String xsShXkd;       // 销货单审核生成销售出库单（默认值1是，0或空否）
    private String xsThXhd;       // 销售退货必须参照销货单（默认值1是，0或空否）
    private String xsByDd;        // 普通销售必有订单（1是，默认值0或空否）
    private String xsKzxyCust;        // 销售控制客户信用（1是，默认值0或空否）
    private String xsKzxyDept;        // 销售控制部门信用（1是，默认值0或空否）
    private String xsKzxyUser;        // 销售控制业务员信用（1是，默认值0或空否）
    private String xsOutFapiao;       // 允许超销货单生成发票（1是，默认值0或空否）
    private String xsOutXhd;      // 允许超订单生成销货单（1是，默认值0或空否）
    private String xsSaveCheck;       // 销货单审核时检查可用量（1是，默认值0或空否）
    private String xsPriceCxkh;       // 促销、客户价格表优先（默认值1是，0或空否）
    private String xsPriceZjyc;       // 若价格表未设置取最近一次售价（默认值1是，0或空否）
    private String xsPriceZdsj;       // 销售是否最低售价控制（1是，默认值0或空否）
    private String xsPriceZdsjPwd;       // 销售最低售价控制密码（不能小于六位任意字符，加密保存）
    private String xsLiucheng;     // 销售业务流程（默认值1先销售后出库，2先出库后销售，3无出库模式）
    private String xsKzqxCust;   // 控制客户权限 1
    private String xsKzqxCustClass;   // 控制客户分类权限 1
    private String xsKzqxDepot;   // 控制仓库权限 0
    private String xsKzqxStock;   // 控制存货权限 0
    private String xsKzqxStockClass;   // 控制存货分类权限 0
    private String xsOutCloseOrder;   // 出库完成自动关闭订单 0

    @ApiModelProperty(value = "应收审核自动核销",hidden = true)
    private String arHexiaoAuto;
    @ApiModelProperty(value = "应付审核自动核销",hidden = true)
    private String apHexiaoAuto;
    @ApiModelProperty(value = "收款单月末核销必须完成",hidden = true)
    private String arShoukuanHxwc;
    @ApiModelProperty(value = "付款单月末核销必须完成",hidden = true)
    private String apFukuanHxwc;
    @ApiModelProperty(value = "应收单控制客户信用",hidden = true)
    private String arYsdXinyong;
    @ApiModelProperty(value = "退款单控制客户信用",hidden = true)
    private String arTkdXinyong;
    @ApiModelProperty(value = "单据审核后立即生成凭证",hidden = true)
    private String arPingzhengAuto;
    @ApiModelProperty(value = "结账前业务单据必须全部生成凭证",hidden = true)
    private String arPingzhengAll;
    @ApiModelProperty(value = "应付单控制供应商信用",hidden = true)
    private String apYfdXinyong;
    @ApiModelProperty(value = "退款单控制供应商信用",hidden = true)
    private String apTkdXinyong;
    @ApiModelProperty(value = "单据审核后立即生成凭证",hidden = true)
    private String apPingzhengAuto;
    @ApiModelProperty(value = "结账前业务单据必须全部生成凭证",hidden = true)
    private String apPingzhengAll;
    @ApiModelProperty(value = "应收确认标志（0或空销货单确认；1发票确认）",hidden = true)
    private String arSourceFlag;
    @ApiModelProperty(value = "应付确认标志（0或空采购到货单确认；1发票确认）",hidden = true)
    private String apSourceFlag;
    @ApiModelProperty(value = "应收单必须进行财务复核",hidden = true)
    private String arCheckFlag;
    @ApiModelProperty(value = "应付单必须进行财务复核",hidden = true)
    private String apCheckFlag;

    private String cgDhRc;        // 采购单行税额容差（小于等于零的正实数）
    private String cgZdRc;        // 采购整单税额容差（小于等于零的正实数）

    private String xsReviewCheck; //  只能弃审自己审核的业务单据
    private String xsOperateCheck; //  只能查看的业务单据
    private String xsRemoveCheck; //  只能修改、删除自己填制的业务单据
    private String cgOperateCheck; //  只能查看的业务单据
    private String cgReviewCheck; //  只能弃审自己审核的业务单据
    private String cgRemoveCheck; //  只能修改、删除自己填制的业务单据
}


