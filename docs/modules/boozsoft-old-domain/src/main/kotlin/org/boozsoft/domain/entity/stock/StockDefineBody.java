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
 * @Date 2022-01-10 
 */

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table ( "stock_define_body" )
@ApiModel(value="存货档案自定义项表体",description="")
public class StockDefineBody  implements Serializable {


	@Id
	@CreatedBy
	@JsonSerialize(using = ToStringSerializer.class)
	private String id;

	@Transient
	@ApiModelProperty(value = "公司唯一码",hidden = true)
	private String tenantId;
	@ApiModelProperty(value = "自定义项编码",hidden = true)
	private String defineCode;
	@ApiModelProperty(value = "档案编码",hidden = true)
	private String ccode;
	@ApiModelProperty(value = "档案名称",hidden = true)
	private String cname;
	@ApiModelProperty(value = "状态(1正常;0停用)",hidden = true)
	private String flag;

}
