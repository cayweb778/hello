
package org.boozsoft.domain.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * @author 财税达软件科技 木子桉易洋 2021/1/14 12:39 下午
 * @version 1.0
 */
@Data
@Table("sys_user")
public class User {
    private static final long serialVersionUID = 1L;
    @Id
    @ApiModelProperty(value = "主键id")
    private Long id;
    @ApiModelProperty(value = "编号")
    private String code;
    @ApiModelProperty(value = "openid")
    private String openid;
    @ApiModelProperty(value = "用户名")
    private String username;
    @ApiModelProperty(value = "密码")
    private String password;
    @ApiModelProperty(value = "昵称")
    private String name;
    @ApiModelProperty(value = "真名")
    private String realName;
    @ApiModelProperty(value = "头像")
    private String avatar;
    @ApiModelProperty(value = "邮箱")
    private String email;
    @ApiModelProperty(value = "手机")
    private String phone;
    @ApiModelProperty(value = "生日")
    private LocalDateTime birthday;
    @ApiModelProperty(value = "性别")
    private Integer sex;
    @ApiModelProperty(value = "部门id")
    private String deptId;
    @ApiModelProperty(value = "部门id")
    private String postId;


}
