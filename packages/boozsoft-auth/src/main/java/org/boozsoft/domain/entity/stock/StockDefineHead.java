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
@Table ( "stock_define_head" )
@ApiModel(value="存货档案自定义项表头",description="")
public class StockDefineHead  implements Serializable {


	@Id
	@ApiModelProperty(value = "ID", hidden = true)
	@CreatedBy
	@JsonSerialize(using = ToStringSerializer.class)
	private String id;
	@Transient
	@ApiModelProperty(value = "公司唯一码",hidden = true)
	private String tenantId;
	@ApiModelProperty(value = "自定义项编码",hidden = true)
	private String defineCode;
	@ApiModelProperty(value = "自定义项名称",hidden = true)
	private String defineName;
	@ApiModelProperty(value = "状态(1正常;0停用)",hidden = true)
	private String flag;
	@ApiModelProperty(value = "数据模块（1.表头;2.表体）",hidden = true)
	private String model;
	@ApiModelProperty(value = "字段属性（1.文本；2.日期；3.整数；4.小数；5.是否）",hidden = true)
	private String shuxing;
	@ApiModelProperty(value = "数据类型",hidden = true)
	private String ctype;
	@ApiModelProperty(value = "小数位数",hidden = true)
	private String numWeishu;

}
