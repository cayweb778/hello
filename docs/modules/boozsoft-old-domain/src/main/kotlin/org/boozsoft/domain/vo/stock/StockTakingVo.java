package org.boozsoft.domain.vo.stock;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.boozsoft.domain.entity.stock.StockPrice;
import org.boozsoft.domain.entity.stock.StockTaking;

import java.io.Serializable;

/**
 * @Description  盘点单
 * @Author  lz
 * @Date 2022-04-09
 */

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value="盘点单",description="盘点单")
public class StockTakingVo extends StockTaking implements Serializable {
	@ApiModelProperty(value = "盘点仓库")
	private String cname;
	@ApiModelProperty(value = "盘点部门")
	private String dname;
	@ApiModelProperty(value = "盘点人")
	private String puname;
	@ApiModelProperty(value = "库管员")
	private String kuname;
	@ApiModelProperty(value = "审核人")
	private String buname;
}
