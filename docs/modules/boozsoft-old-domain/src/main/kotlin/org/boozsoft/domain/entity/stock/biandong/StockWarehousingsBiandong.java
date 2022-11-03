package org.boozsoft.domain.entity.stock.biandong;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.Accessors;
import io.swagger.annotations.ApiModel;
import org.springframework.data.annotation.Id;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.relational.core.mapping.Table;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * @Description  
 * @Author  myh
 * @Date 2022-06-16 
 */

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table ( "stock_warehousings_biandong" )
@ApiModel(value="",description="")
public class StockWarehousingsBiandong  implements Serializable {


	@ApiModelProperty(value = "主键")
	@Id
	@CreatedBy
	@JsonSerialize(using = ToStringSerializer.class)
	private String id;

	@ApiModelProperty(value = "公司唯一码")
	private String tenantId;
	@ApiModelProperty(value = "累计开票金额",hidden = true)
	private String isumFapiaoMoney;
	@ApiModelProperty(value = "年度标识")
	private String iyear;

	@ApiModelProperty(value = "行次唯一码")
	private String lineCode;

	@ApiModelProperty(value = "行次(从1开始记数并排序）")
	private String lineId;

	@ApiModelProperty(value = "入库类别（收发方式中的收方向编码）")
	private String bstyle;

	@ApiModelProperty(value = "单据日期")
	private String ddate;

	@ApiModelProperty(value = "单据编号")
	private String ccode;

	@ApiModelProperty(value = "客户供应商唯一码")
	private String cvencode;

	@ApiModelProperty(value = "存货编码")
	private String cinvode;

	@ApiModelProperty(value = "制单人")
	private String cmaker;

	@ApiModelProperty(value = "主计量单位编码")
	private String cunitid;

	@ApiModelProperty(value = "辅计量单位一编码")
	private String cunitidF1;

	@ApiModelProperty(value = "辅计量单位二编码")
	private String cunitidF2;

	@ApiModelProperty(value = "主计量数量 （10位小数点）")
	private String baseQuantity;

	@ApiModelProperty(value = "辅计量一数量 （10位小数点）")
	private String subQuantity1;

	@ApiModelProperty(value = "辅计量二数量 （10位小数点）")
	private String subQuantity2;

	@ApiModelProperty(value = "项目大类唯一码")
	private String citemClass;

	@ApiModelProperty(value = "项目唯一码")
	private String citemcode;

	@ApiModelProperty(value = "仓库级次一编码")
	private String cwhcode1;

	@ApiModelProperty(value = "仓库级次二编码")
	private String cwhcode2;

	@ApiModelProperty(value = "仓库级次三编码")
	private String cwhcode3;

	@ApiModelProperty(value = "仓库级次四编码")
	private String cwhcode4;

	@ApiModelProperty(value = "仓库级次五编码")
	private String cwhcode5;

	@ApiModelProperty(value = "仓库级次六编码")
	private String cwhcode6;

	@ApiModelProperty(value = "税率（6位小数点）")
	private String itaxrate;

	@ApiModelProperty(value = "无税单价（10位小数点）")
	private String price;

	@ApiModelProperty(value = "含税单价（10位小数点）")
	private String taxprice;

	@ApiModelProperty(value = "本币税额（4位小数点）")
	private String itaxprice;

	@ApiModelProperty(value = "本币无税金额（4位小数点）")
	private String icost;

	@ApiModelProperty(value = "本币价税合计（4位小数点）")
	private String isum;

	@ApiModelProperty(value = "部门编码")
	private String cdepcode;

	@ApiModelProperty(value = "审核人")
	private String ccheck;

	@ApiModelProperty(value = "审核日期")
	private String ccheckdate;

	@ApiModelProperty(value = "备注")
	private String cmemo;

	@ApiModelProperty(value = "是否赠品")
	private String isGive;

	@ApiModelProperty(value = "质检状态")
	private String cgspstate;

	@ApiModelProperty(value = "质检日期")
	private String sgspdate;

	@ApiModelProperty(value = "质检人")
	private String sgspperson;

	@ApiModelProperty(value = "生产日期")
	private String dbdate;

	@ApiModelProperty(value = "失效日期")
	private String dvdate;

	@ApiModelProperty(value = "采购订单编号")
	private String cordercode;

	@ApiModelProperty(value = "单据来源")
	private String csource;

	@ApiModelProperty(value = "审核状态（1审核，0或其他未审）")
	private String bcheck;

	@ApiModelProperty(value = "审核时间")
	private String bcheckTime;

	@ApiModelProperty(value = "审核人")
	private String bcheckUser;

	@ApiModelProperty(value = "关闭状态（1关闭、0或其他未关闭）")
	private String bcloser;

	@ApiModelProperty(value = "关闭时间")
	private String bcloserTime;

	@ApiModelProperty(value = "关闭人")
	private String bcloserUser;

	@ApiModelProperty(value = "变更人")
	private String changer;

	@ApiModelProperty(value = "变更日期")
	private String changedate;

	@ApiModelProperty(value = "批量生单id")
	private String totalsourceid;

	@ApiModelProperty(value = "来源单据类型（CGFP采购发票、销售XSFP发票、CGDD采购订单、XSDD销售订单、CGDHD采购到货单、XSFHD销售发货单、CGRKG采购入库单、XSCKD销售出库单、PDD盘点单、DBD调拨单、XTZHD形态转换单、WLQRD物流确认单、QTRKD其他入库单、QTCKD其他出库单、SCJGD生产加工单）")
	private String sourcetype;

	@ApiModelProperty(value = "来源单据编码")
	private String sourcecode;

	@ApiModelProperty(value = "来源单据行次唯一码")
	private String sourcedetailId;

	@ApiModelProperty(value = "来源单据日期")
	private String sourcedate;

	private String cfree1;

	private String cfree2;

	private String cfree3;

	private String cfree4;

	private String cfree5;

	private String cfree6;

	private String cfree7;

	private String cfree8;

	private String cfree9;

	private String cfree10;

	private String cfree11;

	private String cfree12;

	@ApiModelProperty(value = "批次")
	private String batchId;

	@ApiModelProperty(value = "累计结算数量")
	private String isumJiesuan;

	@ApiModelProperty(value = "累计开票数量")
	private String isumFapiao;

	@ApiModelProperty(value = "累计入库数量")
	private String isumXiaohuo;

	@ApiModelProperty(value = "累计质检数量")
	private String isumJijian;

	@ApiModelProperty(value = "累计物流数量")
	private String isumWuliu;

	@ApiModelProperty(value = "成本结算标志")
	private String baccFlag;

	@ApiModelProperty(value = "结算操作员id")
	private String baccUser;

	@ApiModelProperty(value = "结算时间")
	private String baccDate;

	@ApiModelProperty(value = "结算回冲单来源单号")
	private String baccId;

	@ApiModelProperty(value = "单据类型id （CGDHD采购到货单、HZHCD红字回冲单、CGRKD采购入库单、DBRKD调拨入库单、XTZHRKD形态转换单、QTRKD其他入库单）")
	private String billStyle;

	@ApiModelProperty(value = "仓库唯一码")
	private String cwhcode;

	@ApiModelProperty(value = "是否独立仓库（ 1是 0否）")
	private String cangkuDuli;

	@ApiModelProperty(value = "计量单位编码")
	private String unitId;

	@ApiModelProperty(value = "累计出库数量")
	private String isumChuku;

	private String dpdate;

	@ApiModelProperty(value = "到货日期")
	private String arrivalDate;

	@ApiModelProperty(value = "折扣金额（4位小数点）")
	private String idiscount;

	@ApiModelProperty(value = "核销状态（1核销完成，0或其他核销未完成）")
	private String hxFlag;

	@ApiModelProperty(value = "累计核销金额")
	private String hxIsum;

	@ApiModelProperty(value = "盈亏主数量(正数表示盘盈，负数表示盘亏)")
	private String quantityUk;

	@ApiModelProperty(value = "盈亏数量1(正数表示盘盈，负数表示盘亏)")
	private String quantityUk1;

	@ApiModelProperty(value = "盈亏数量2(正数表示盘盈，负数表示盘亏)")
	private String quantityUk2;

	@ApiModelProperty(value = "盘点主数量 （10位小数点）")
	private String quantityPd;

	@ApiModelProperty(value = "盘点1数量 （10位小数点）")
	private String subPandian1;

	@ApiModelProperty(value = "盘点2数量 （10位小数点）")
	private String subPandian2;

	private String bdocumStyle;

	@ApiModelProperty(value = "是否变动,0否1是")
	private String biandong;

	private String biandongUser;

	private String biandongDate;

	@ApiModelProperty(value = "数量",hidden = true)
	private String cnumber;
	@ApiModelProperty(value = "采购计量单位编码",hidden = true)
	private String cgUnitId;
	@ApiModelProperty(value = "调后单价",hidden = true)
	private BigDecimal thprice;
	@ApiModelProperty(value = "调后金额",hidden = true)
	private BigDecimal thicost;
	@ApiModelProperty(value = "调整金额",hidden = true)
	private BigDecimal thmoney;
	@ApiModelProperty(value = "暂估无税单价",hidden = true)
	private String priceZangu;
	@ApiModelProperty(value = "暂估无税金额",hidden = true)
	private String icostZangu;
	@ApiModelProperty(value = "系统时间",hidden = true)
	private String cmakerTime;
	@ApiModelProperty(value = "费用金额",hidden = true)
	private String feiyongMoney;
}
