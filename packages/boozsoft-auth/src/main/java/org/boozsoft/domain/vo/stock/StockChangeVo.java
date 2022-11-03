package org.boozsoft.domain.vo.stock;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.boozsoft.domain.entity.stock.StockChange;

import java.io.Serializable;


@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class StockChangeVo extends StockChange implements Serializable {

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
	@ApiModelProperty(value = "制单人")
	private String cmakerName;
	@ApiModelProperty(value = "部门名称")
	private String deptName;
	@ApiModelProperty(value = "业务人名")
	private String cpersonName;
	private String custName;
	private String psnName;
	private String ckName;


}
