package org.boozsoft.domain.vo.stock;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.boozsoft.domain.entity.stock.StockCustPrice;
import org.boozsoft.domain.entity.stock.StockSupPrice;

import java.io.Serializable;

/**
 * @Description  存货价格表
 * @Author  lz
 * @Date 2022-04-09
 */

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value="存货价格表",description="存货价格表")
public class StockSupPriceVo extends StockSupPrice implements Serializable {

	@ApiModelProperty(value = "添加日期")
	private String createTime;

	@ApiModelProperty(value = "存货名称")
	private String stockName;

	@ApiModelProperty(value = "规格型号")
	private String stockGgxh;

	@ApiModelProperty(value = "存货分类")
	private String stockClass;

	@ApiModelProperty(value = "分类名称")
	private String stockCclassName;

	@ApiModelProperty(value = "计量方式:单计量/多计量")
	private String stockMeasurementType;

	@ApiModelProperty(value = "销售单位")
	private String stockMarketUnit;

	@ApiModelProperty(value = "计量单位id")
	private String stockMeasurementUnit;

	@ApiModelProperty(value = "单计量")
	private String uname;

	@ApiModelProperty(value = "多计量")
	private String unames;

	//默认不是编辑状态
	private Boolean roweditflg = false;
}
