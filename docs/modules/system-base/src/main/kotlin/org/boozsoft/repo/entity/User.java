/**
 * Copyright (c) 2018-2028, Chill Zhuang 庄骞 (smallchill@163.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.boozsoft.repo.entity;

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

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 实体类
 *
 * @author Chillss
 */
@Data
@Table("_app_group_sys_user")
@Accessors(chain = true)
@ApiModel(value = "user对象", description = "user对象")
public class User implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@CreatedBy
	@ApiModelProperty(value = "ID", hidden = true)
	private String id;
	@ApiModelProperty(value = "人员姓名", hidden = true)
	private String username;
	@ApiModelProperty(value = "手机号码", hidden = true)
	private String phone;
	@ApiModelProperty(value = "邮箱", hidden = true)
	private String email;
	@ApiModelProperty(value = "是否停用(1.启用;0停用)", hidden = true)
	private String flag;
	//    @ApiModelProperty(value = "1系统操作员 2组织操作员 3公司操作员", hidden = true)
//    private String userType;
	@ApiModelProperty(value = "备用1", hidden = true)
	private String beiyong1;
	@ApiModelProperty(value = "备用2", hidden = true)
	private String beiyong2;
	@ApiModelProperty(value = "备用3", hidden = true)
	private String beiyong3;
	@ApiModelProperty(value = "备用4", hidden = true)
	private String beiyong4;
	@ApiModelProperty(value = "备用5", hidden = true)
	private String beiyong5;
	@ApiModelProperty(value = "备用5", hidden = true)
	private String openid;

}
