package org.boozsoft.domain.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.boozsoft.repo.entity.Jigou;
import org.boozsoft.repo.entity.Role;
import org.boozsoft.repo.entity.User;

/**
 * @author 木子桉易洋
 * @version 1.0
 * @company 财税达软件科技
 * @date 2021/1/29 12:49 下午
 */
@Data
@EqualsAndHashCode(callSuper=false)
@Accessors(chain = true)
@ApiModel(value = "RoleDTO对象", description = "RoleDTO对象")
public class RoleDTO  {
//    Long id;
    String alias;
    String name;

    public static RoleDTO from(Role role) {
        RoleDTO roleDTO = new RoleDTO();
//        roleDTO.setId(role.getId());
        roleDTO.setAlias(role.getRoleAlias());
        roleDTO.setName(role.getRoleName());
        return roleDTO;
    }
}

