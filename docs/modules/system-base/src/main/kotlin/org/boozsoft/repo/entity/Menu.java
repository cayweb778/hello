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
import org.springbooz.core.tool.result.R;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.util.Date;

/**
 * 实体类
 *
 * @author Chill
 */
@Data
@Table("_app_group_sys_menu")
@Accessors(chain = true)
@ApiModel(value = "Menu对象", description = "Menu对象")
public class Menu implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@CreatedBy
	private String id;

	@ApiModelProperty(value = "平台id")
	private String platformId;

	@ApiModelProperty(value = "父id")
	private String parentId;

	@ApiModelProperty(value = "菜单名称")
	private String name;

	@ApiModelProperty(value = "菜单权限编码",notes = "sys:schedule:list,sys:schedule:info")
	private String perms;

	@ApiModelProperty(value = "权限策略",notes = "1显示2禁用")
	private String permsType;

	@ApiModelProperty(value = "菜单图标")
	private String icon;

	@ApiModelProperty(value = "组件")
	private String component;

	@ApiModelProperty(value = "组件名字")
	private String componentName;

	@ApiModelProperty(value = "路径")
	private String path;

	@ApiModelProperty(value = "一级菜单跳转地址")
	private String redirect;

	@ApiModelProperty(value = "菜单排序")
	private Double sortNo;

	@ApiModelProperty(value = "类型",notes = "（0：一级菜单；1：子菜单 ；2：按钮权限）")
	private Integer category;

	@ApiModelProperty(value = "是否叶子节点",notes = " 1:是  0:不是")
	private boolean isLeaf;

	@ApiModelProperty(value = "是否路由菜单",notes = "0:不是  1:是（默认值1）")
	private boolean isRoute;


	@ApiModelProperty(value = "是否缓存页面",notes = " 0:不是  1:是（默认值1")
	private boolean keepAlive;

	@ApiModelProperty(value = "描述")
	private String description;

	@ApiModelProperty(value = "创建人")
	private String createBy;

	@ApiModelProperty(value = "删除状态",notes = "删除状态 0正常 1已删除")
	private Integer delFlag;

	@ApiModelProperty(value = "是否配置菜单的数据权限",notes = "1是0否 默认0")
	private Integer ruleFlag;

	@ApiModelProperty(value = "是否隐藏路由菜单",notes = "0否,1是（默认值0）")
	private boolean hidden;

	@ApiModelProperty(value = "创建时间")
	private Date createTime;

	@ApiModelProperty(value = "更新人")
	private String updateBy;

	@ApiModelProperty(value = "更新时间")
	private Date updateTime;

	@ApiModelProperty(value = "按钮权限状态",notes = "(0无效1有效)")
	private String status;

	@ApiModelProperty(value = "是否打开新页面")
	private boolean alwaysShow;

	@ApiModelProperty(value = "外链菜单打开方式",notes = "0/内部打开 1/外部打开 ")
	private boolean internalOrExternal;


//	public SysPermission(boolean index) {
//		if(index) {
//			this.id = "9502685863ab87f0ad1134142788a385";
//			this.name="首页";
//			this.component="dashboard/Analysis";
//			this.componentName="dashboard-analysis";
//			this.url="/dashboard/analysis";
//			this.icon="home";
//			this.menuType=0;
//			this.sortNo=0.0;
//			this.ruleFlag=0;
//			this.delFlag=0;
//			this.alwaysShow=false;
//			this.route=true;
//			this.keepAlive=true;
//			this.leaf=true;
//			this.hidden=false;
//		}
//
//	}


}
