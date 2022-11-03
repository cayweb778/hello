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

import java.io.Serializable;

/**
 * @Description  
 * @Author  myh
 * @Date 2022-02-18 
 */

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table ( "_app_group_supplier_class_account" )
@ApiModel(value="供应商分类分配表",description="供应商分类分配表")
public class GroupSupplierClassAccount implements Serializable {


	@Id
	@CreatedBy
	@JsonSerialize(using = ToStringSerializer.class)
	private String id;

	@ApiModelProperty(value = "档案唯一码")
	private String dataUnique;

	@ApiModelProperty(value = "分配类型(1组织、2账套)")
	private String ctype;

	@ApiModelProperty(value = "组织唯一码")
	private String orgUnique;

	@ApiModelProperty(value = "租户id")
	private String tenantId;

	@ApiModelProperty(value = "分配人id")
	private String assignUserId;

	@ApiModelProperty(value = "分配时间")
	private String assignDate;

	@ApiModelProperty(value = "引入状态；1已引入，0")
	private String flag;

	@ApiModelProperty(value = "引入人id")
	private String acceptUserId;

	@ApiModelProperty(value = "引入时间")
	private String acceptDate;
}
