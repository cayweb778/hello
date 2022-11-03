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
package org.boozsoft.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.boozsoft.domain.entity.account.SysDepartment;
import org.boozsoft.repo.entity.Menu;
import org.springbooz.core.tool.node.INode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 视图实体类
 *
 * @author Chill
 */
@Data
@EqualsAndHashCode(callSuper=false)
@Accessors(chain = true)
@ApiModel(value = "SysDepartmentVo对象", description = "SysDepartmentVo对象")
public class SysDepartmentVo extends SysDepartment implements INode {
	private static final long serialVersionUID = 1L;
	/**
	 * 子孙节点
	 */
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private List<INode> children=new ArrayList<>();
}
