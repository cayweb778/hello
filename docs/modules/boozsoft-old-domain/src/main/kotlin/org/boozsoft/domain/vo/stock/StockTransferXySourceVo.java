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
public class StockTransferXySourceVo implements Serializable {

	private String xyBillStyle;
	private String syccode;
	private String ccodeDate;
	private String psnName;
	private String deptName;
	private String cmemo;
	private String ckName;
}
