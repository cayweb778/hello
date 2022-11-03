package org.boozsoft.domain.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.boozsoft.repo.entity.Jigou;
import org.boozsoft.repo.entity.Role;
import org.boozsoft.repo.entity.User;
import org.springframework.beans.BeanUtils;
//import org.springframework.security.oauth2.core.oidc.OidcIdToken;

import java.util.Set;

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
public class UserDTO extends User {
    // 所属机构
    Jigou jigou;
    // 所属角色
    Set<Role> roleList;
    // token令牌
//    OidcIdToken idToken;
    public static UserDTO from(User user){
        UserDTO userDTO=new UserDTO();
        BeanUtils.copyProperties(user,userDTO);
        return userDTO;
    }

}

