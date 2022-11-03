package org.springbooz.webflux.holder.context.security.domain.entity;

import lombok.Data;

@Data
public class SystemUser {
  String deptId;
  //  String openid;
  String id;
  String idToken;
  String jigou;
  String name;
  String password;
  String phone;
  String realName;
  String roleList;
  String username;
  String avatar;
  String email;
  String birthday;
  String gender;
  String postId;
}
