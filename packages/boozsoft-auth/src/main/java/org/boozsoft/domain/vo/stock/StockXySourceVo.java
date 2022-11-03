package org.boozsoft.domain.vo.stock;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;


@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value="下游单据",description="下游单据")
public class StockXySourceVo implements Serializable {

	@ApiModelProperty(value = "下游单据类型")
	private String xyBillStyle;
	@ApiModelProperty(value = "下游单据")
	private String xyccode;
	private String billStyle;
	private String ccode;
	@ApiModelProperty(value = "上游单据日期",hidden = true)
	private String ccodeDate;
	@ApiModelProperty(value = "下游单据日期")
	private String xyccodeDate;
	@ApiModelProperty(value = "业务人")
	private String psnName;
	@ApiModelProperty(value = "业务部门")
	private String deptName;

	private String stockName;
	private String stockNum;
	private String stockGgxh;
	private String baseQuantity;
	private String isum;
	@ApiModelProperty(value = "备注")
	private String cmemo;
	@ApiModelProperty(value = "主及量")
	private String cunitid;
	private String cunitName;
	@ApiModelProperty(value = "仓库")
	private String cwhcode;
	private String cwhcodeName;
	private String bdocumStyle;
}
