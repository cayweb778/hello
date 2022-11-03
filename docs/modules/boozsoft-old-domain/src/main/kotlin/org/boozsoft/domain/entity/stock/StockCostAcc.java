package org.boozsoft.domain.entity.stock;

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
@Table ( "stock_cost_acc" )
@Accessors(chain = true)
@ApiModel(value="存货成本核算表",description="存货成本核算表")
public class StockCostAcc {

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
	@ApiModelProperty(value = "月份",hidden = true)
	private String imonth;

	@ApiModelProperty(value = "期初主数量（10位小数点）",hidden = true)
	private String baseQuantityQc;
	@ApiModelProperty(value = "期初数量1（10位小数点）",hidden = true)
	private String baseQuantity1Qc;
	@ApiModelProperty(value = "期初数量2（10位小数点）",hidden = true)
	private String baseQuantity2Qc;
	@ApiModelProperty(value = "期初无税金额（4位小数点）",hidden = true)
	private String icostQc;

	@ApiModelProperty(value = "本期主数量（10位小数点）",hidden = true)
	private String baseQuantityBq;
	@ApiModelProperty(value = "本期数量1（10位小数点）",hidden = true)
	private String baseQuantity1Bq;
	@ApiModelProperty(value = "本期数量2（10位小数点）",hidden = true)
	private String baseQuantity2Bq;
	@ApiModelProperty(value = "本期无税金额（4位小数点）",hidden = true)
	private String icostBq;

	@ApiModelProperty(value = "本月主数量（10位小数点）",hidden = true)
	private String baseQuantityTotal;
	@ApiModelProperty(value = "本月数量1（10位小数点）",hidden = true)
	private String baseQuantity1Total;
	@ApiModelProperty(value = "本月数量2（10位小数点）",hidden = true)
	private String baseQuantity2Total;
	@ApiModelProperty(value = "本月无税单价（4位小数点）",hidden = true)
	private String price;
	@ApiModelProperty(value = "本月无税金额（4位小数点）",hidden = true)
	private String icostTotal;

	@ApiModelProperty(value = "存货唯一码",hidden = true)
	private String cinvode;
	@ApiModelProperty(value = "批次号",hidden = true)
	private String batchId;
	@ApiModelProperty(value = "生产日期",hidden = true)
	private String dpdate;
	@ApiModelProperty(value = "失效日期",hidden = true)
	private String dvdate;
	@ApiModelProperty(value = "项目唯一码",hidden = true)
	private String citemcode;
	@ApiModelProperty(value = "仓库唯一码",hidden = true)
	private String cwhcode;

	@ApiModelProperty(value = "本月出库主数量（10位小数点）",hidden = true)
	private String baseQuantityCk;
	@ApiModelProperty(value = "本月出库数量1（10位小数点）",hidden = true)
	private String baseQuantity1Ck;
	@ApiModelProperty(value = "本月出库数量2（10位小数点）",hidden = true)
	private String baseQuantity2Ck;
	@ApiModelProperty(value = "出库无税单价（4位小数点）",hidden = true)
	private String priceCk;
	@ApiModelProperty(value = "本月出库无税金额（4位小数点）",hidden = true)
	private String icostCk;

	@ApiModelProperty(value = "本月结存主数量（10位小数点）",hidden = true)
	private String baseQuantityJc;
	@ApiModelProperty(value = "本月结存数量1（10位小数点）",hidden = true)
	private String baseQuantity1Jc;
	@ApiModelProperty(value = "本月结存数量2（10位小数点）",hidden = true)
	private String baseQuantity2Jc;
	@ApiModelProperty(value = "本月结存无税单价（4位小数点）",hidden = true)
	private String priceJc;
	@ApiModelProperty(value = "本月结存出库无税金额（4位小数点）",hidden = true)
	private String icostJc;

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

}
