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
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
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
@ApiModel(value = "Permission对象", description = "Permission对象")
public class MenuVO implements INode {
	private static final long serialVersionUID = 1L;
	private String parentId;
	private String path;
	private String name;
	private String component;
	private String redirect;
	private Map meta;

	@ApiModelProperty(value = "是否隐藏路由菜单",notes = "0否,1是（默认值0）")
	private boolean hideMenu;



	@ApiModelProperty(value = "菜单权限编码",notes = "sys:schedule:list,sys:schedule:info")
	private String perms;

	@ApiModelProperty(value = "权限策略",notes = "1显示2禁用")
	private String permsType;

	@ApiModelProperty(value = "菜单排序")
	private Double sortNo;

	@ApiModelProperty(value = "类型",notes = "（0：一级菜单；1：子菜单 ；2：按钮权限）")
	private Integer category;

	/**
	 * 主键ID
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	private String id;

	/**
	 * 子孙节点
	 */
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private List<INode> children=new ArrayList<>();

	public static MenuVO from(Menu menu) {
		return new MenuVO();
	}

	/**
	 * 上级部门
	 */
	private String parentName;

	@Override
	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getComponent() {
		return component;
	}

	public void setComponent(String component) {
		this.component = component;
	}

	public Map getMeta() {
		return meta;
	}

	public void setMeta(Map meta) {
		this.meta = meta;
	}

	public String getPerms() {
		return perms;
	}

	public void setPerms(String perms) {
		this.perms = perms;
	}

	public String getPermsType() {
		return permsType;
	}

	public void setPermsType(String permsType) {
		this.permsType = permsType;
	}

	public Double getSortNo() {
		return sortNo;
	}

	public void setSortNo(Double sortNo) {
		this.sortNo = sortNo;
	}

	public Integer getCategory() {
		return category;
	}

	public void setCategory(Integer category) {
		this.category = category;
	}

	@Override
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public List<INode> getChildren() {
		return children;
	}

	public void setChildren(List<INode> children) {
		this.children = children;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public String getRedirect() {
		return redirect;
	}

	public void setRedirect(String redirect) {
		this.redirect = redirect;
	}

	public boolean isHideMenu() {
		return hideMenu;
	}

	public void setHideMenu(boolean hideMenu) {
		this.hideMenu = hideMenu;
	}
}
