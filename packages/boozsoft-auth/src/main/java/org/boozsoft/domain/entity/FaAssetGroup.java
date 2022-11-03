package org.boozsoft.domain.entity;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table ("fa_asset_group" )
@ApiModel(value="资产组",description="资产组")
public class FaAssetGroup {

	@ApiModelProperty(value = "id", position = 0)
	@Id	@CreatedBy	@JsonSerialize(using = ToStringSerializer.class)
	private String id;

	@ApiModelProperty(value = "代码", position = 1)
	private String bsCode;

	@ApiModelProperty(value = "名称", position = 2)
	private String bsName;

	@ApiModelProperty(value = "是否停用(1.启用;0.停用)", position = 3)
	private String flag;

	@ApiModelProperty(value = "添加人", position = 4)
	private String createCode;

	@ApiModelProperty(value = "创建日期", hidden = true)
	private String createDate;

	@ApiModelProperty(value = "停用日期", hidden = true)
	private String stopDate;
}
