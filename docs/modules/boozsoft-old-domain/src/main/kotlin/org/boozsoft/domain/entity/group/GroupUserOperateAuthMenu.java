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
package org.boozsoft.domain.entity.group;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
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
@Table("_group_user_operat_auth_menu")
@Accessors(chain = true)
@ApiModel(value = "Menu对象", description = "Menu对象")
public class GroupUserOperateAuthMenu implements Serializable {

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
	@ApiModelProperty(value = "菜单排序")
	private Double sortNo;
	@ApiModelProperty(value = "查看（1是，0否）")
	private String  isSee;
	@ApiModelProperty(value = "编辑（1是，0否）")
	private String  isEdit;
	@ApiModelProperty(value = "新增（1是，0否）")
	private String  isAdd;
	@ApiModelProperty(value = "修改（1是，0否）")
	private String  isChange;
	@ApiModelProperty(value = "删除（1是，0否）")
	private String  isDel;
	@ApiModelProperty(value = "操作（1是，0否）")
	private String  isOprat;
	@ApiModelProperty(value = "插入（1是，0否）")
	private String  isInsert;
	@ApiModelProperty(value = "审核/签字（1是，0否）")
	private String  isSign;
	@ApiModelProperty(value = "弃审/取消（1是，0否）")
	private String  isCancel;
	@ApiModelProperty(value = "作废（1是，0否）")
	private String  isTovoid;
	@ApiModelProperty(value = "生单（1是，0否）")
	private String  isMake;
	@ApiModelProperty(value = "导入（1是，0否）")
	private String  isImport;
	@ApiModelProperty(value = "导出（1是，0否）")
	private String  isExport;
	@ApiModelProperty(value = "打印（1是，0否）")
	private String  isPrint;
	@ApiModelProperty(value = "发送邮件（1是，0否）")
	private String  isEmail;
	@ApiModelProperty(value = "栏目设置（1是，0否）")
	private String  isColumn;
	@ApiModelProperty(value = "启用/生效（1是，0否）")
	private String  isStart;
	@ApiModelProperty(value = "停用/失效（1是，0否）")
	private String  isStop;
	@ApiModelProperty(value = "设置（1是，0否）")
	private String  isSetting;
	@ApiModelProperty(value = "附件查看（1是，0否）")
	private String  isAnnexSee;
	@ApiModelProperty(value = "附件修改（1是，0否）")
	private String  isAnnexEdit;
	@ApiModelProperty(value = "附件变更（1是，0否）")
	private String  isAnnexChange;
	@ApiModelProperty(value = "回收（1是，0否）")
	private String  isRecycle;
	@ApiModelProperty(value = "保存（1是，0否）")
	private String  isSave;

	@ApiModelProperty(value = "出纳签字（1是，0否）")
	private String  isCashierSign;
	@ApiModelProperty(value = "出纳签字（1是，0否）")
	private String  isCashierCancel;

	@ApiModelProperty(value = "主管签字（1是，0否）")
	private String  isSupervisorSign;
	@ApiModelProperty(value = "出纳签字（1是，0否）")
	private String  isSupervisorCancel;

	@ApiModelProperty(value = "记账（1是，0否）")
	private String  isBook;

	// 针对存货进行补充
	@ApiModelProperty(value = "联查（1是，0否）")
	private String  isJoin;

	@ApiModelProperty(value = "结转（1是，0否）")
	private String  isCarryOver;

	@ApiModelProperty(value = "校验（1是，0否）")
	private String  isCheck;

	@ApiModelProperty(value = "金额式（1是，0否）")
	private String  isAmount;

	@ApiModelProperty(value = "复制（1是，0否）")
	private String  isCopy;

	private String  beiyong1;
	private String  beiyong2;
	private String  beiyong3;
	private String  beiyong4;
	private String  beiyong5;



}
