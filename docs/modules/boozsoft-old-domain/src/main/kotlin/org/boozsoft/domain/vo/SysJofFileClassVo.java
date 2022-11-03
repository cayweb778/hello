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
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.boozsoft.domain.entity.SysJobFile;
import org.boozsoft.domain.entity.group.GroupExpenditureClass;
import org.springbooz.core.tool.node.INode;

import java.util.ArrayList;
import java.util.List;

/**
 * 视图实体类
 *
 * @author lz
 */
@Data
@ApiModel(value = "SysJobFileVo对象", description = "SysJobFileVo对象")
public class SysJofFileClassVo extends SysJobFile{

	@ApiModelProperty(value = "类别名称", hidden = true)
	private String zfname;

	@ApiModelProperty(value = "上级名称", hidden = true)
	private String pname;
}
