package org.boozsoft.domain.entity.accstyle;


import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.experimental.Accessors;
import lombok.NoArgsConstructor;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table ("_app_group_acc_style" )
@ApiModel(value="科目类型",description="acc_style: table")
public class AccStyle {

	@ApiModelProperty(value = "主键id", position = 0)
	@Id
	private String id;

	@ApiModelProperty(value = "唯一码", position = 1)
	private String unique;

	@ApiModelProperty(value = "类型名称", position = 2)
	private String cclass;

	@ApiModelProperty(value = "类型英文名称", position = 3)
	private String classEngl;

	@ApiModelProperty(value = "类型编码（排序码）", position = 4)
	private String order;

	@ApiModelProperty(value = "是否预算会计1是", position = 4)
	private String flagYusuan;

}
