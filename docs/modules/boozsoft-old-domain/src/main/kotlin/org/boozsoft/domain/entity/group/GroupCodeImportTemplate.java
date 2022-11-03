package org.boozsoft.domain.entity.group;

import lombok.Data;
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



@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Table("_app_group_code_import_template")
@ApiModel(value="会计科目导入模板")
public class GroupCodeImportTemplate {

  	@Id	@CreatedBy	@JsonSerialize(using = ToStringSerializer.class)
	private String id;
	/**
 	* 模板名称
 	*/
	private String tname;
	/**
 	* 说明
 	*/
	private String remark;
	/**
 	* 1正常
 	*/
	private Integer flag;
	/**
 	* 类型：0系统，1自定义
 	*/
	private Integer ttype;

	/**
	 * 字段名称：系统:其他软件字段名称
	 */
	private String tjson;
}
