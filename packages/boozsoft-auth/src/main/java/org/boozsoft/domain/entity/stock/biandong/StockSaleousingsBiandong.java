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

import java.math.BigDecimal;

@Data
@Table ( "stock_saleousings_biandong" )
@Accessors(chain = true)
@ApiModel(value="出库单子表",description="出库单子表")
public class StockSaleousingsBiandong {

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
	@ApiModelProperty(value = "行次唯一码",hidden = true)
	private String lineCode;
	@ApiModelProperty(value = "行次(从1开始记数并排序）",hidden = true)
	private String lineId;
	@ApiModelProperty(value = "单据类型id （XHD销货单、XSCKD销售出库单、QTCKD其他出库单、DBCKD调拨出库单、XTZHCKD形态转换出库单、JYJCD借用借出单）",hidden = true)
	private String billStyle;
	@ApiModelProperty(value = "入库类别（收发方式中的收方向编码）",hidden = true)
	private String bstyle;
	@ApiModelProperty(value = "单据日期",hidden = true)
	private String ddate;
	@ApiModelProperty(value = "单据编号",hidden = true)
	private String ccode;
	@ApiModelProperty(value = "销售客户供应商唯一码",hidden = true)
	private String cvencode;
	@ApiModelProperty(value = "结算客户供应商唯一码",hidden = true)
	private String cvencodeJs;
	@ApiModelProperty(value = "仓库唯一码",hidden = true)
	private String cwhcode;
	@ApiModelProperty(value = "存货唯一码",hidden = true)
	private String cinvode;
	@ApiModelProperty(value = "是否独立仓库（ 1是 0否）",hidden = true)
	private String cangkuDuli;
	@ApiModelProperty(value = "批次",hidden = true)
	private String batchId;
	@ApiModelProperty(value = "制单人（操作员）",hidden = true)
	private String cmaker;
	@ApiModelProperty(value = "主计量单位编码",hidden = true)
	private String cunitid;
	@ApiModelProperty(value = "辅计量单位一编码",hidden = true)
	private String cunitidF1;
	@ApiModelProperty(value = "辅计量单位二编码",hidden = true)
	private String cunitidF2;
	@ApiModelProperty(value = "计量单位编码",hidden = true)
	private String unitId;
	@ApiModelProperty(value = "主计量数 （10位小数点）",hidden = true)
	private String baseQuantity;
	@ApiModelProperty(value = "计量一数 （10位小数点）",hidden = true)
	private String subQuantity1;
	@ApiModelProperty(value = "计量二数 （10位小数点）",hidden = true)
	private String subQuantity2;
	@ApiModelProperty(value = "项目大类唯一码",hidden = true)
	private String citemClass;
	@ApiModelProperty(value = "项目唯一码",hidden = true)
	private String citemcode;
	@ApiModelProperty(value = "仓库级次一名称",hidden = true)
	private String cwhcode1;
	@ApiModelProperty(value = "仓库级次二名称",hidden = true)
	private String cwhcode2;
	@ApiModelProperty(value = "仓库级次三名称",hidden = true)
	private String cwhcode3;
	@ApiModelProperty(value = "仓库级次四名称",hidden = true)
	private String cwhcode4;
	@ApiModelProperty(value = "仓库级次五名称",hidden = true)
	private String cwhcode5;
	@ApiModelProperty(value = "仓库级次六名称",hidden = true)
	private String cwhcode6;
	@ApiModelProperty(value = "税率（6位小数点）",hidden = true)
	private String itaxrate;
	@ApiModelProperty(value = "无税单价（10位小数点）",hidden = true)
	private String price;
	@ApiModelProperty(value = "含税单价（10位小数点）",hidden = true)
	private String taxprice;
	@ApiModelProperty(value = "本币税额（4位小数点）",hidden = true)
	private String itaxprice;
	@ApiModelProperty(value = "本币无税金额（4位小数点）",hidden = true)
	private String icost;
	@ApiModelProperty(value = "本币价税合计（4位小数点）",hidden = true)
	private String isum;
	@ApiModelProperty(value = "累计结算数量",hidden = true)
	private String isumJiesuan;
	@ApiModelProperty(value = "累计开票数量",hidden = true)
	private String isumFapiao;
	@ApiModelProperty(value = "累计入库数量",hidden = true)
	private String isumXiaohuo;
	@ApiModelProperty(value = "累计质检数量",hidden = true)
	private String isumJijian;
	@ApiModelProperty(value = "累计物流数量",hidden = true)
	private String isumWuliu;
	@ApiModelProperty(value = "累计出库数量",hidden = true)
	private String isumChuku;
	@ApiModelProperty(value = "累计退货数量",hidden = true)
	private String isumTuiHuo;
	@ApiModelProperty(value = "部门唯一码",hidden = true)
	private String cdepcode;
	@ApiModelProperty(value = "成本结算标志",hidden = true)
	private String baccFlag;
	@ApiModelProperty(value = "结算操作员id",hidden = true)
	private String baccUser;
	@ApiModelProperty(value = "结算时间",hidden = true)
	private String baccDate;
	@ApiModelProperty(value = "结算回冲单来源单号",hidden = true)
	private String baccId;
	@ApiModelProperty(value = "备注",hidden = true)
	private String cmemo;
	@ApiModelProperty(value = "是否增品",hidden = true)
	private String isGive;
	@ApiModelProperty(value = "质检状态",hidden = true)
	private String cgspstate;
	@ApiModelProperty(value = "质检日期",hidden = true)
	private String sgspdate;
	@ApiModelProperty(value = "质检人（操作员）",hidden = true)
	private String sgspperson;
	@ApiModelProperty(value = "生产日期",hidden = true)
	private String dpdate;
	@ApiModelProperty(value = "失效日期",hidden = true)
	private String dvdate;
	@ApiModelProperty(value = "采购订单编号",hidden = true)
	private String cordercode;
	@ApiModelProperty(value = "单据来源",hidden = true)
	private String csource;
	@ApiModelProperty(value = "审核状态（1审核，0或其他未审）",hidden = true)
	private String bcheck;
	@ApiModelProperty(value = "审核时间",hidden = true)
	private String bcheckTime;
	@ApiModelProperty(value = "审核人（操作员）",hidden = true)
	private String bcheckUser;
	@ApiModelProperty(value = "关闭状态（1关闭、0或其他未关闭）",hidden = true)
	private String bcloser;
	@ApiModelProperty(value = "关闭时间",hidden = true)
	private String bcloserTime;
	@ApiModelProperty(value = "关闭人（操作员）",hidden = true)
	private String bcloserUser;
	@ApiModelProperty(value = "变更人（操作员）",hidden = true)
	private String changer;
	@ApiModelProperty(value = "变更日期",hidden = true)
	private String changedate;
	@ApiModelProperty(value = "批量生单id",hidden = true)
	private String totalsourceid;
	@ApiModelProperty(value = "来源单据类型id （CGFP采购发票、XSFP销售发票、CGDD采购订单、XSDD销售订单、CGDHD采购到货单、XSFHD销售发货单、CGRKG采购入库单、XSCKD销售出库单、PDD盘点单、DBD调拨单、XTZHD形态转换单、WLQRD物流确认单、QTRKD其他入库单、QTCKD其他出库单、SCJGD生产加工单）",hidden = true)
	private String sourcetype;
	@ApiModelProperty(value = "来源单据编码",hidden = true)
	private String sourcecode;
	@ApiModelProperty(value = "来源单据行次唯一码",hidden = true)
	private String sourcedetailId;
	@ApiModelProperty(value = "来源单据日期",hidden = true)
	private String sourcedate;
	@ApiModelProperty(value = "核销状态（1核销完成，0或其他核销未完成）")
	private String hxFlag;
	@ApiModelProperty(value = "累计核销金额")
	private String hxIsum;
	@ApiModelProperty(value = "盘点主数量 （10位小数点）",hidden = true)
	private BigDecimal quantityPd;
	@ApiModelProperty(value = "盘点1数量 （10位小数点）",hidden = true)
	private BigDecimal subPandian1;
	@ApiModelProperty(value = "计盘点2数量 （10位小数点）",hidden = true)
	private BigDecimal subPandian2;
	@ApiModelProperty(value = "盈亏主数量(正数表示盘盈，负数表示盘亏)",hidden = true)
	private BigDecimal quantityUk;
	@ApiModelProperty(value = "盈亏数量1(正数表示盘盈，负数表示盘亏)",hidden = true)
	private BigDecimal quantityUk1;
	@ApiModelProperty(value = "盈亏数量2(正数表示盘盈，负数表示盘亏)",hidden = true)
	private BigDecimal quantityUk2;
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
	@ApiModelProperty(value = "数量",hidden = true)
	private String quantity;
	@ApiModelProperty(value = "销售计量单位编码",hidden = true)
	private String xsUnitId;
	@ApiModelProperty(value = "是否变动，0否1是")
	private String biandong;

	@ApiModelProperty(value = "变动人")
	private String biandongUser;

	@ApiModelProperty(value = "变动时间")
	private String baindongDate;

	@ApiModelProperty(value = "预计出库时间",hidden = true)
	private String estimatedTime;
	@ApiModelProperty(value = "制单时间",hidden = true)
	private String cmakerTime;
}
