package org.boozsoft.domain.bean;

import lombok.Data;
//import org.springframework.security.oauth2.core.oidc.OidcIdToken;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

@Data
public class UserInfo implements Serializable {
    private String userId;
    private String username;
    private Set roleList;
//    private OidcIdToken token;
}
