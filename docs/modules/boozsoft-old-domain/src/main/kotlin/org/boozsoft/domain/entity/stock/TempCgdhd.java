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
 * @Date 2022-04-28 
 */

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table ( "temp_cgdhd" )
@ApiModel(value="采购到货单缓存表",description="采购到货单缓存表")
public class TempCgdhd  implements Serializable {


	@Id
	@CreatedBy
	@JsonSerialize(using = ToStringSerializer.class)
	private String id;

	private String tenantId;

	@ApiModelProperty(value = "行次唯一码 （查找）")
	private String lineCode;

	@ApiModelProperty(value = "仓库唯一码  （判断）")
	private String cwhcode;

	@ApiModelProperty(value = "存货唯一码  （判断）")
	private String cinvode;

	@ApiModelProperty(value = "主计量数 （判断） （10位小数点）")
	private String baseQuantity;

	@ApiModelProperty(value = "批次")
	private String batchId;

	@ApiModelProperty(value = "生产日期")
	private String dpdate;

	@ApiModelProperty(value = "失效日期")
	private String dvdate;

	@ApiModelProperty(value = "单据类型")
	private String billStyle;

	@ApiModelProperty(value = "单据编码")
	private String ccode;

}
