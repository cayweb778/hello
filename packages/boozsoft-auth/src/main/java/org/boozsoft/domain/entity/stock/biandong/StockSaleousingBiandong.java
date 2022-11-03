package org.boozsoft.domain.entity.stock.biandong;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table ( "stock_saleousing_biandong" )
@Accessors(chain = true)
@ApiModel(value="出库单主表",description="出库单主表")
public class StockSaleousingBiandong {
	@Id
	@ApiModelProperty(value = "ID", hidden = true)
	@CreatedBy
	@JsonSerialize(using = ToStringSerializer.class)
	private String id;
	@Transient
	@ApiModelProperty(value = "公司唯一码",hidden = true)
	private String tenantId;
	@ApiModelProperty(value = "年度标志",hidden = true)
	private String iyear;
	@ApiModelProperty(value = "单据编号",hidden = true)
	private String ccode;
	@ApiModelProperty(value = "批量单号",hidden = true)
	private String ccodePl;
	@ApiModelProperty(value = "制单人",hidden = true)
	private String cmaker;
	@ApiModelProperty(value = "制单时间",hidden = true)
	private String cmakerTime;
	@ApiModelProperty(value = "单据日期",hidden = true)
	private String ddate;
	@ApiModelProperty(value = "入库类别（收发方式中的收方向编码）",hidden = true)
	private String bstyle;
	@ApiModelProperty(value = "销售客户供应商唯一码",hidden = true)
	private String cvencode;
	@ApiModelProperty(value = "结算客户供应商唯一码",hidden = true)
	private String cvencodeJs;
	@ApiModelProperty(value = "往来转账类型（1:采购；0:销售；空是无转账；2红字回冲）",hidden = true)
	private String wlzzType;
	@ApiModelProperty(value = "往来转账客户唯一码",hidden = true)
	private String cvencodeWl;
	@ApiModelProperty(value = "仓库唯一码",hidden = true)
	private String cwhcode;
	@ApiModelProperty(value = "库管员编码唯一码（员工档案）",hidden = true)
	private String cwhcodeUser;
	@ApiModelProperty(value = "部门编码",hidden = true)
	private String cdepcode;
	@ApiModelProperty(value = "审核状态（1审核，0或其他未审）",hidden = true)
	private String bcheck;
	@ApiModelProperty(value = "审核时间",hidden = true)
	private String bcheckTime;
	@ApiModelProperty(value = "审核人",hidden = true)
	private String bcheckUser;
	@ApiModelProperty(value = "备注",hidden = true)
	private String cmemo;
	@ApiModelProperty(value = "业务员编码",hidden = true)
	private String cpersoncode;
	@ApiModelProperty(value = "关闭状态（1关闭、0或其他未关闭）",hidden = true)
	private String bcloser;
	@ApiModelProperty(value = "关闭时间",hidden = true)
	private String bcloserTime;
	@ApiModelProperty(value = "关闭人",hidden = true)
	private String bcloserUser;
	@ApiModelProperty(value = "主数量合计",hidden = true)
	private String squantity;
	@ApiModelProperty(value = "计量一合计",hidden = true)
	private String squantity1;
	@ApiModelProperty(value = "计量一合计",hidden = true)
	private String squantity2;
	@ApiModelProperty(value = "金额合计",hidden = true)
	private String icost;
	@ApiModelProperty(value = "价税合计",hidden = true)
	private String isum;
	@ApiModelProperty(value = "单据类型id （XHD销货单、PLXHD批量销货单、XSCKD销售出库单、QTCKD其他出库单、DBCKD调拨出库单、XTZHCKD形态转换出库单、JYJCD借用借出单、XSDD销售订单）",hidden = true)
	private String billStyle;
	@ApiModelProperty(value = "单据属性(0:或空蓝字入库单据，1:红字入库单据)",hidden = true)
	private String bdocumStyle;
	@ApiModelProperty(value = "税额合计",hidden = true)
	private String taxAmount;
	@ApiModelProperty(value = "核销状态（1核销完成，0或其他核销未完成）")
	private String hxFlag;
	@ApiModelProperty(value = "累计核销金额")
	private String hxIsum;
	@ApiModelProperty(value = "来源单据类型id （CGFP采购发票、XSFP销售发票、CGDD采购订单、XSDD销售订单、CGDHD采购到货单、XSFHD销售发货单、CGRKG采购入库单、XSCKD销售出库单、PDD盘点单、DBD调拨单、XTZHD形态转换单、WLQRD物流确认单、QTRKD其他入库单、QTCKD其他出库单、SCJGD生产加工单）",hidden = true)
	private String sourcetype;
	@ApiModelProperty(value = "来源单据编码",hidden = true)
	private String sourcecode;
	@ApiModelProperty(value = "送货日期")
	private String deliveryDate;
	@ApiModelProperty(value = "送货人")
	private String deliveryUser;
	@ApiModelProperty(value = "送货物流单号")
	private String deliveryId;
	@ApiModelProperty(value = "备注")
	private String deliveryExplain;

	@ApiModelProperty(value = "拣货日期")
	private String pickingDate;
	@ApiModelProperty(value = "拣货单号")
	private String pickingId;

	@ApiModelProperty(value = "单位类型")
	private String unitType;
	@ApiModelProperty(value = "业务单位")
	private String unitValue;

	@ApiModelProperty(value = "成本核算状态 1已核算")
	private String costStatus;
	@ApiModelProperty(value = "成本核算销人")
	private String costCode;
	@ApiModelProperty(value = "成本核算时间")
	private String costDate;
	@ApiModelProperty(value = "红字回冲次数",hidden = true)
	private String hzhcNum;
	@ApiModelProperty(value = "记账凭证 1是 0否",hidden = true)
	private String isVoucher;
	@ApiModelProperty(value = "",hidden = true)
	private String cfree1;
	@ApiModelProperty(value = "",hidden = true)
	private String cfree2;
	@ApiModelProperty(value = "",hidden = true)
	private String cfree3;
	@ApiModelProperty(value = "",hidden = true)
	private String cfree4;
	@ApiModelProperty(value = "",hidden = true)
	private String cfree5;
	@ApiModelProperty(value = "",hidden = true)
	private String cfree6;
	@ApiModelProperty(value = "",hidden = true)
	private String cfree7;
	@ApiModelProperty(value = "",hidden = true)
	private String cfree8;
	@ApiModelProperty(value = "",hidden = true)
	private String cfree9;
	@ApiModelProperty(value = "",hidden = true)
	private String cfree10;
	@ApiModelProperty(value = "",hidden = true)
	private String cfree11;
	@ApiModelProperty(value = "",hidden = true)
	private String cfree12;


	@ApiModelProperty(value = "是否变动，0否1是")
	private String biandong;

	@ApiModelProperty(value = "变动人")
	private String biandongUser;

	@ApiModelProperty(value = "变动时间")
	private String baindongDate;
	@Transient
	private String entryList;

	@ApiModelProperty(value = "完成结算数量",hidden = true)
	private String jiesuanStatus;
	@ApiModelProperty(value = "完成开票数量",hidden = true)
	private String fapiaoStatus;
	@ApiModelProperty(value = "完成入库数量",hidden = true)
	private String xiaohuoStatus;
	@ApiModelProperty(value = "完成质检数量",hidden = true)
	private String jijianStatus;
	@ApiModelProperty(value = "完成物流数量",hidden = true)
	private String wuliuStatus;
	@ApiModelProperty(value = "完成出库数量",hidden = true)
	private String chukuStatus;
	@ApiModelProperty(value = "完成退货数量",hidden = true)
	private String tuihuoStatus;

	@ApiModelProperty(value = "发票类型（zyfp专用发票,ptfp普通发票,nfcpfp农副产品发票,sj收据）",hidden = true)
	private String invoiceStyle;

	@ApiModelProperty(value = "完成拣货装箱1",hidden = true)
	private String jhzxStatus;
	@ApiModelProperty(value = "完成出库确认1",hidden = true)
	private String querenStatus;

	@ApiModelProperty(value = "来源单据日期",hidden = true)
	private String sourcedate;
	@ApiModelProperty(value = "复核状态（1复核，其他未核）",hidden = true)
	private String bworkable;
	@ApiModelProperty(value = "审核时间",hidden = true)
	private String bworkableTime;
	@ApiModelProperty(value = "关闭人",hidden = true)
	private String bworkableUser;


	@ApiModelProperty(value = "协议收款日期",hidden = true)
	private String payDate;
	@ApiModelProperty(value = "收款方式",hidden = true)
	private String methodPay;
	@ApiModelProperty(value = "定金",hidden = true)
	private String theDeposit;

	@ApiModelProperty(value = "发票代码")
	private String billCode;
	@ApiModelProperty(value = "发票号码")
	private String billNumber;
	@ApiModelProperty(value = "发票日期")
	private String billDate;
}
