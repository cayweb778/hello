package org.boozsoft.domain.vo.stock;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.boozsoft.domain.entity.stock.StockJhzx;

import java.io.Serializable;


@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value="库存台账",description="库存台账")
public class StockKctzVo implements Serializable {

	@ApiModelProperty(value = "审核状态（1审核，0或其他未审）",hidden = true)
	private String bcheck;
	@ApiModelProperty(value = "单据日期",hidden = true)
	private String ddate;
	@ApiModelProperty(value = "制单时间",hidden = true)
	private String cmakerTime;
	@ApiModelProperty(value = "单据类型id （CGDHD采购到货单、CGRKD采购入库单、DBRKD调拨入库单、XTZHRKD形态转换单、QTRKD其他入库单,PYRKD盘盈入库单）",hidden = true)
	private String billStyle;
	@ApiModelProperty(value = "单据编号",hidden = true)
	private String ccode;
	@ApiModelProperty(value = "'往来单位'",hidden = true)
	private String comcode;
	@ApiModelProperty(value = "'往来单位'",hidden = true)
	private String comcode2;
	@ApiModelProperty(value = "'往来单位'",hidden = true)
	private String comcode3;
	@ApiModelProperty(value = "项目",hidden = true)
	private String citemcode;
	@ApiModelProperty(value = "批号",hidden = true)
	private String batchId;
	@ApiModelProperty(value = "生产日期",hidden = true)
	private String dpdate;
	@ApiModelProperty(value = "失效日期",hidden = true)
	private String dvdate;

	@ApiModelProperty(value = "仓库唯一码",hidden = true)
	private String cwhcode;

	@ApiModelProperty(value = "数量(主单位)",hidden = true)
	private String bq;
	@ApiModelProperty(value = "数量(主单位)",hidden = true)
	private String bq1;
	@ApiModelProperty(value = "数量(主单位)",hidden = true)
	private String bq2;

	@ApiModelProperty(value = "单价",hidden = true)
	private String price;
	@ApiModelProperty(value = "单价",hidden = true)
	private String price1;
	@ApiModelProperty(value = "单价",hidden = true)
	private String price2;

	@ApiModelProperty(value = "本币无税金额（4位小数点）",hidden = true)
	private String icost;
	@ApiModelProperty(value = "本币无税金额（4位小数点）",hidden = true)
	private String icost1;
	@ApiModelProperty(value = "本币无税金额（4位小数点）",hidden = true)
	private String icost2;
}
