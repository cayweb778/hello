package org.boozsoft.domain.entity.stock;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table ( "ar_ap_code" )
@Accessors(chain = true)
@ApiModel(value="应收应付科目表",description="应收应付科目表")
public class ArApCode {

	@Id
	@ApiModelProperty(value = "ID", hidden = true)
	@CreatedBy
	@JsonSerialize(using = ToStringSerializer.class)
	private String id;
	@Transient
	@ApiModelProperty(value = "公司唯一码",hidden = true)
	private String tenantId;
	@ApiModelProperty(value = "类别唯一码", hidden = true)
	private String uniqueId;
	@ApiModelProperty(value = "类型（ar应收、ap应付）", hidden = true)
	private String billStyle;
	@ApiModelProperty(value = "应收应付类别", hidden = true)
	private String stName;
	@ApiModelProperty(value = "科目编码", hidden = true)
	private String ccode;
	@ApiModelProperty(value = "科目名称", hidden = true)
	private String ccodeName;
	@ApiModelProperty(value = "", hidden = true)
	private String cfree1;
	@ApiModelProperty(value = "", hidden = true)
	private String cfree2;
	@ApiModelProperty(value = "", hidden = true)
	private String cfree3;
	@ApiModelProperty(value = "", hidden = true)
	private String cfree4;
	@ApiModelProperty(value = "", hidden = true)
	private String cfree5;
}
