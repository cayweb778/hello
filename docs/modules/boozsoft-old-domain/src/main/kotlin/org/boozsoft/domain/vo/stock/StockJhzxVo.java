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
@ApiModel(value="下游单据",description="下游单据")
public class StockJhzxVo extends StockJhzx {

	@ApiModelProperty(value = "客户档案",hidden = true)
	private String cvencode;
	@ApiModelProperty(value = "出库单号",hidden = true)
	private String outCcode;
	@ApiModelProperty(value = "出库单日期",hidden = true)
	private String outDate;


}
