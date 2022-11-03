package org.boozsoft.domain.entity.group;


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
@Table ("_app_group_allot_record" )
@ApiModel(value="集团数据分配记录表",description="集团数据分配记录表")
public class GroupAllotRecord {

	@ApiModelProperty(value = "账套编码")
	private String accId;

	@ApiModelProperty(value = "数据唯一码")
	private String dataUnique;
	@ApiModelProperty(value = "是否已引入；1是0否")
	private String allotType;
	@ApiModelProperty(value = "数据类型")
	private String dataType;

	@Id
	@ApiModelProperty(value = "ID", hidden = true)
	@CreatedBy
	@JsonSerialize(using = ToStringSerializer.class)
	private String id;

}
