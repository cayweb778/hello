package org.boozsoft.domain.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.boozsoft.repo.entity.Jigou;
import org.boozsoft.repo.entity.Role;
import org.boozsoft.repo.entity.User;

import java.util.List;

/**
 * @author 木子桉易洋
 * @version 1.0
 * @company 财税达软件科技
 * @date 2021/1/29 12:49 下午
 */
@Data
@EqualsAndHashCode(callSuper=false)
@Accessors(chain = true)
@ApiModel(value = "UserVO对象", description = "UserVO对象")
public class UserVO extends User {
    // 所属机构
    Jigou jigou;
    String jigouName;
    // 所属角色
    List<Role> roleList;
}

