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
 * @Date 2021-12-31 
 */

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table ( "stock_cangku_level_name" )
@ApiModel(value="仓库级别名称表",description="仓库级别名称表")
public class StockCangkuLevelName  implements Serializable {


	@Id
	@CreatedBy
	@JsonSerialize(using = ToStringSerializer.class)
	private String id;

	private String levelName;

	@ApiModelProperty(value = "类型，1系统2自定义")
	private String levelType;
}
