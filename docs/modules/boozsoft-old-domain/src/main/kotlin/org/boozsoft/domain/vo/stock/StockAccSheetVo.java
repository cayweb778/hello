package org.boozsoft.domain.vo.stock;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.boozsoft.domain.entity.stock.StockPrice;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value="存货信息表",description="存货信息表")
public class StockAccSheetVo implements Serializable {

	@ApiModelProperty(value = "存货档案表ID")
	private String cinvode;
	@ApiModelProperty(value = "批号")
	private String batchid;
	@ApiModelProperty(value = "类型0期初1入库2出库")
	private String types;
	@ApiModelProperty(value = "数量(主单位)")
	private String bq;
	@ApiModelProperty(value = "数量(主单位)")
	private String bq1;
	@ApiModelProperty(value = "数量(主单位)")
	private String bq2;

	@ApiModelProperty(value = "累计入库/销货数量",hidden = true)
	private String isum;

	@ApiModelProperty(value = "单据编号")
	private String ccode;
	@ApiModelProperty(value = "来源单据编码")
	private String sourcecode;
	@ApiModelProperty(value = "来源单据类型id （CGFP采购发票、XSFP销售发票、CGDD采购订单、XSDD销售订单、CGDHD采购到货单、XSFHD销售发货单、CGRKG采购入库单、XSCKD销售出库单、PDD盘点单、DBD调拨单、XTZHD形态转换单、WLQRD物流确认单、QTRKD其他入库单、QTCKD其他出库单、SCJGD生产加工单）",hidden = true)
	private String sourcetype;
	@ApiModelProperty(value = "单据类型id （CGDHD采购到货单、HZHCD红字回冲单、CGRKD采购入库单、DBRKD调拨入库单、XTZHRKD形态转换单、QTRKD其他入库单,PYRKD盘盈入库单）",hidden = true)
	private String billStyle;

	@ApiModelProperty(value = "生产日期",hidden = true)
	private String dpdate;
	@ApiModelProperty(value = "失效日期",hidden = true)
	private String dvdate;
	@ApiModelProperty(value = "审核状态（1审核，0或其他未审）",hidden = true)
	private String bcheck;


	@ApiModelProperty(value = "年度标志",hidden = true)
	private String iyear;

	@ApiModelProperty(value = "计量单位类型；单计量||多计量")
	private String cunitidType;

	@ApiModelProperty(value = "主计量",hidden = true)
	private String cunitid;
	@ApiModelProperty(value = "辅计量一",hidden = true)
	private String cunitidF1;
	@ApiModelProperty(value = "辅计量二",hidden = true)
	private String cunitidF2;

	@ApiModelProperty(value = "仓库唯一码【期初存储仓库】",hidden = true)
	private String cwhcode;
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

	@ApiModelProperty(value = "本币无税金额（4位小数点）",hidden = true)
	private String icost;

}
