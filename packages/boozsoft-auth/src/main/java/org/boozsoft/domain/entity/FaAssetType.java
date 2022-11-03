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
@Table ("fa_asset_type" )
@ApiModel(value="资产类别",description="资产类别")
public class FaAssetType {

	@ApiModelProperty(value = "id", position = 0)
	@Id	@CreatedBy	@JsonSerialize(using = ToStringSerializer.class)
	private String id;

	@ApiModelProperty(value = "唯一标识", hidden = true)
	private String uniqueCode;

	@ApiModelProperty(value = "编码（不允许重复）", hidden = true)
	private String ecCode;

	@ApiModelProperty(value = "名称", hidden = true)
	private String ecName;

	@ApiModelProperty(value = "是否末级", hidden = true)
	private String bend;

	@ApiModelProperty(value = "级次", hidden = true)
	private String leave;

	@ApiModelProperty(value = "上级ID", hidden = true)
	@JsonSerialize(using = ToStringSerializer.class)
	private String parentId;

	@ApiModelProperty(value = "员工唯一编码", hidden = true)
	private String uniqueCodeUser;

	@ApiModelProperty(value = "创建日期", hidden = true)
	private String createDate;

	@ApiModelProperty(value = "是否停用(1.启用;0停用)", hidden = true)
	private String flag;

	@ApiModelProperty(value = "使用年限", hidden = true)
	private String serviceLife;

	@ApiModelProperty(value = "净残值率", hidden = true)
	private String netSalvage;

	@ApiModelProperty(value = "资产属性", hidden = true)
	private String assetAttId;

	@ApiModelProperty(value = "折旧方法", hidden = true)
	private String depMethodId;

	@ApiModelProperty(value = "计量单位", hidden = true)
	private String unitId;

	@ApiModelProperty(value = "进项税率", hidden = true)
	private String inputTax;

	@ApiModelProperty(value = "卡片样式", hidden = true)
	private String cardStyle;

	@ApiModelProperty(value = "计提折旧(1.计提折旧;2不计提)", hidden = true)
	private String isAccrual;

	@ApiModelProperty(value = "类型(1.系统;自定义)", hidden = true)
	private String zjType;
}
