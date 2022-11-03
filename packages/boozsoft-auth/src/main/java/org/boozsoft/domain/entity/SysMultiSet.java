package org.boozsoft.domain.entity;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table ("sys_multi_set" )
@ApiModel(value="国家表",description="国家表")
public class SysMultiSet {

	@Id
	@CreatedBy
	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty(value = "主键", position = 0)
	private String id;

	@ApiModelProperty(value = "序号", position = 1)
	private String num;

	@ApiModelProperty(value = "科目编码", position = 2)
	private String ccode;

	@ApiModelProperty(value = "栏目名称", position = 3)
	private String name;

	@ApiModelProperty(value = "方向 1借 2贷 3双向", position = 4)
	private String fx;

	@ApiModelProperty(value = "核算编码", position = 2)
	private String km;

}
