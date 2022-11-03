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
import org.springframework.data.annotation.Transient;
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
@Table ( "stock_wulius" )
@ApiModel(value="物流子表",description="")
public class StockWulius  implements Serializable {


	@Id
	@CreatedBy
	@JsonSerialize(using = ToStringSerializer.class)
	private String id;

	private String tenantId;

	private String iyear;

	@ApiModelProperty(value = "物流单号")
	private String ccode;

	@ApiModelProperty(value = "单据日期")
	private String ddate;

	@ApiModelProperty(value = "存货唯一码")
	private String cinvode;

	@ApiModelProperty(value = "仓库唯一码")
	private String cwhcode;

	@ApiModelProperty(value = "仓库级次一ID")
	private String cwhcode1;

	private String cwhcode2;

	private String cwhcode3;

	private String cwhcode4;

	private String cwhcode5;

	@ApiModelProperty(value = "批次号")
	private String batchId;

	@ApiModelProperty(value = "生产日期")
	private String dpdate;

	@ApiModelProperty(value = "失效日期")
	private String dvdate;

	@ApiModelProperty(value = "主数量 （10位小数点）")
	private String baseQuantity;

	@ApiModelProperty(value = "数量1 （10位小数点）")
	private String subQuantity1;

	@ApiModelProperty(value = "数量2 （10位小数点）")
	private String subQuantity2;

	@ApiModelProperty(value = "备注")
	private String cmemo;

	@ApiModelProperty(value = "[统一出库]来源单号")
	private String sourceCode;
	@ApiModelProperty(value = "[统一出库]来源行唯一码")
	private String sourceLineCode;
	@ApiModelProperty(value = "[统一出库]来源类型")
	private String sourceType;
	@ApiModelProperty(value = "[统一出库]来源时间")
	private String sourceDate;
	private String unitId;
	private String cunitid;
	private String cnumber;
	private String cgUnitId;






	@Transient
	private String billStyle;
	@Transient
	private String lineCode;
}
