package org.boozsoft.domain.entity.stock;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table ( "stock_cg_arrivalvouchs" )
@ApiModel(value="采购到货单子表",description="采购到货单子表")
public class StockCgArrivalvouchs {

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
	@ApiModelProperty(value = "采购类别（0或空：到货单，1：退货单）",hidden = true)
	private String bstyle;
	@ApiModelProperty(value = "单据日期",hidden = true)
	private String ddate;
	@ApiModelProperty(value = "采购到货单编号",hidden = true)
	private String ccode;
	@ApiModelProperty(value = "存货编码",hidden = true)
	private String cinvode;
	@ApiModelProperty(value = "制单人",hidden = true)
	private String cmaker;
	@ApiModelProperty(value = "主计量单位编码",hidden = true)
	private String cunitid;
	@ApiModelProperty(value = "辅计量单位一编码",hidden = true)
	private String cunitidF1;
	@ApiModelProperty(value = "辅计量单位二编码",hidden = true)
	private String cunitidF2;
	@ApiModelProperty(value = "主计量数量 （10位小数点）",hidden = true)
	private String baseQuantity;
	@ApiModelProperty(value = "辅计量一数量 （10位小数点）",hidden = true)
	private String subQuantity1;
	@ApiModelProperty(value = "辅计量二数量 （10位小数点）",hidden = true)
	private String subQuantity2;
	@ApiModelProperty(value = "项目大类唯一码",hidden = true)
	private String citemClass;
	@ApiModelProperty(value = "项目唯一码",hidden = true)
	private String citemcode;
	@ApiModelProperty(value = "仓库唯一码",hidden = true)
	private String cwhcode;
	@ApiModelProperty(value = "仓库级次一编码",hidden = true)
	private String cwhcode1;
	@ApiModelProperty(value = "仓库级次二编码",hidden = true)
	private String cwhcode2;
	@ApiModelProperty(value = "仓库级次三编码",hidden = true)
	private String cwhcode3;
	@ApiModelProperty(value = "仓库级次四编码",hidden = true)
	private String cwhcode4;
	@ApiModelProperty(value = "仓库级次五编码",hidden = true)
	private String cwhcode5;
	@ApiModelProperty(value = "仓库级次六编码",hidden = true)
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
	@ApiModelProperty(value = "部门编码",hidden = true)
	private String cdepcode;
	@ApiModelProperty(value = "审核状态（1审核，0或其他未审）",hidden = true)
	private String bcheck;
	@ApiModelProperty(value = "审核时间",hidden = true)
	private String bcheckTime;
	@ApiModelProperty(value = "审核人唯一码",hidden = true)
	private String bcheckUser;
	@ApiModelProperty(value = "关闭状态（1关闭、0或其他未关闭）",hidden = true)
	private String bcloser;
	@ApiModelProperty(value = "关闭时间",hidden = true)
	private String bcloserTime;
	@ApiModelProperty(value = "关闭人唯一码",hidden = true)
	private String bcloserUser;
	@ApiModelProperty(value = "备注",hidden = true)
	private String cmemo;
	@ApiModelProperty(value = "是否赠品",hidden = true)
	private String isGive;
	@ApiModelProperty(value = "质检状态",hidden = true)
	private String cgspstate;
	@ApiModelProperty(value = "质检日期",hidden = true)
	private String sgspdate;
	@ApiModelProperty(value = "质检人",hidden = true)
	private String sgspperson;
	@ApiModelProperty(value = "生产日期",hidden = true)
	private String dpdate;
	@ApiModelProperty(value = "失效日期",hidden = true)
	private String dvdate;
	@ApiModelProperty(value = "采购订单编号",hidden = true)
	private String cordercode;
	@ApiModelProperty(value = "单据来源",hidden = true)
	private String csource;
	@ApiModelProperty(value = "变更人",hidden = true)
	private String changer;
	@ApiModelProperty(value = "变更日期",hidden = true)
	private String changedate;
	@ApiModelProperty(value = "批量生单id",hidden = true)
	private String totalsourceid;
	@ApiModelProperty(value = "来源单据类型id （CGFP发票、XSFP发票、CGDD采购订单、XSDD销售订单、CGDHD采购到货单、XSFHD销售发货单、CGRKG采购入库单、XSCKD销售出库单、PDD盘点单、DBD调拨单、XTZHD形态转换单、WLQRD物流确认单、QTRKD其他入库单、QTCKD其他出库单、SCJGD生产加工单）",hidden = true)
	private String sourcetype;
	@ApiModelProperty(value = "来源单据编码",hidden = true)
	private String sourcecode;
	@ApiModelProperty(value = "来源单据行次唯一码",hidden = true)
	private String sourcedetailId;
	@ApiModelProperty(value = "来源单据日期",hidden = true)
	private String sourcedate;
	@ApiModelProperty(value = "采购类别（0或空：到货单，1：退货单）",hidden = true)
	private String invoiceStyle;
	@ApiModelProperty(value = "批次",hidden = true)
	private String batchId;
	@ApiModelProperty(value = "累计开票数量",hidden = true)
	private String isumFapiao;
	@ApiModelProperty(value = "累计入库数量",hidden = true)
	private String isumRuku;
	@ApiModelProperty(value = "累计质检数量",hidden = true)
	private String isumZhijian;
	@ApiModelProperty(value = "累计付款数量",hidden = true)
	private String isumFukuan;
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

}
