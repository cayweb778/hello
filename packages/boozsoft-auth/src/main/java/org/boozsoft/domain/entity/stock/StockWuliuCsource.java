package org.boozsoft.domain.entity.stock;

import lombok.Data;
import java.io.Serializable;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.Accessors;
import io.swagger.annotations.ApiModel;
import org.springframework.data.annotation.Id;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.relational.core.mapping.Table;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * @Description  
 * @Author  myh
 * @Date 2022-05-16 
 */

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table ( "stock_wuliu_csource" )
@ApiModel(value="物流下游表",description="")
public class StockWuliuCsource  implements Serializable {


	@Id
	@CreatedBy
	@JsonSerialize(using = ToStringSerializer.class)
	private String id;

	private String tenantId;

	private String iyear;

	@ApiModelProperty(value = "本单号")
	private String ccode;

	@ApiModelProperty(value = "本单据类型")
	private String billStyle;

	@ApiModelProperty(value = "本单日期")
	private String ccodeDate;

	@ApiModelProperty(value = "下游单据类型")
	private String xyBillStyle;

	@ApiModelProperty(value = "下游单号")
	private String xyCcode;

	@ApiModelProperty(value = "下游单号日期")
	private String xyCcodeDate;
}
