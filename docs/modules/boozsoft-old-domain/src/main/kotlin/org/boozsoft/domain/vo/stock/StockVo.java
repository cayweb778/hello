package org.boozsoft.domain.vo.stock;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.boozsoft.domain.entity.stock.Stock;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Description  存货档案
 * @Author  myh
 * @Date 2021-12-29 
 */

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value="存货档案",description="存货档案")
public class StockVo extends Stock {
	private String stockFlag;
	private String stockCangkuName;
	@ApiModelProperty(value = "分类名称")
	private String stockCclassName;
	@ApiModelProperty(value = "主计量数 （10位小数点）",hidden = true)
	private BigDecimal xcl;
	private String stockMeasurementName;
	private String supName;
	@ApiModelProperty(value = "批次",hidden = true)
	private String batchId;
	@ApiModelProperty(value = "无税单价（10位小数点）",hidden = true)
	private String price;
	@ApiModelProperty(value = "生产日期",hidden = true)
	private String dpdate;
	@ApiModelProperty(value = "失效日期",hidden = true)
	private String dvdate;
}
