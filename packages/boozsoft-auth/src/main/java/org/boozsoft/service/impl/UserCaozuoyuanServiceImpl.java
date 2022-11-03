//package org.boozsoft.service.impl;
//
//import cn.hutool.core.bean.BeanUtil;
//import org.boozsoft.domain.entity.UserCaozuoyuan;
//import org.boozsoft.domain.entity.UserJiGou;
//import org.boozsoft.domain.vo.UserCaozuoyuanVo;
//import org.boozsoft.repo.UserCaozuoyuanRepository;
//import org.boozsoft.repo.UserJiGouRepository;
//import org.boozsoft.repo.UserRepository;
//import org.boozsoft.repo.UserRoleRepository;
//import org.boozsoft.repo.entity.User;
//import org.boozsoft.repo.entity.UserRole;
//import org.boozsoft.service.UserCaozuoyuanService;
//
//import org.springbooz.datasource.r2dbc.annotation.SCHEMA_TYPE;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//
//import java.util.*;
//
///**
// * @author LZ
// * @version 1.0
// * @title 操作人员
// * @company 财税达软件科技
// * @date 2021/4/1 10:47 上午
// */
//@Service
//public class UserCaozuoyuanServiceImpl implements UserCaozuoyuanService {
//  // 超级管理员
//  public static final String SUPER_ADMIN_TYPE = "1000";
//  // 集团管理员
//  public static final String GROUP_MANAGER_TYPE = "1";
//  // 组织管理员
//  public static final String ORIGIN_MANAGER_TYPE = "2";
//  // 公司操作员
//  public static final String ACCOUNT_MANAGER_TYPE = "3";
//
//
//  public static final Long GROUP_MANAGER_ROLE_ID = 1340925547478441986l;
//  public static final Long ACCOUNT_MANAGER_ROLE_ID = 1340925547478441987l;
//  public static final Long ORIGIN_MANAGER_ROLE_ID = 1339804920791662594l;
//  public static final Long SUPER_ADMIN_ROLE_ID = 1123598816738675201l;
//  @Autowired
//  UserCaozuoyuanRepository userCaozuoyuanRepository;
//  @Autowired
//  UserJiGouRepository userJiGouRepository;
//  @Autowired
//  UserRepository userRepository;
//  @Autowired
//  UserRoleRepository userRoleRepository;
//
//
//  public Mono<User> saveAccount(UserCaozuoyuan object) {
//    return Mono.just(object).map(item -> userCaozuoyuanCastSystemUser(item))
//        .flatMap(item -> item)
//        .flatMap(userRepository::save);
//  }
//
//  @Override
//
//  public Mono updateFlagNc(String id, String flag) {
//    return userCaozuoyuanRepository.findById(id)
//        .map(item -> {
//          item.setFlag(flag);
//          return item;
//        })
//        .flatMap(userCaozuoyuanRepository::save)
//        .map(item -> {
//          return userRepository.findByOpenid(item.getUniqueCode()).map(var -> {
//            var.setStatus(Integer.valueOf(flag));
//            return var;
//          });
//        })
//        .flatMap(item -> item)
//        .flatMap(userRepository::save);
//  }
//
//  @Override
//
//  public Mono updateFlagAccount(String openid, String flag) {
//    return userRepository.findByOpenid(openid)
//        .map(var -> {
//          var.setStatus(Integer.valueOf(flag));
//          return var;
//        })
//        .flatMap(userRepository::save);
//  }
//
//  private Mono<User> userCaozuoyuanCastSystemUser(UserCaozuoyuan item) {
//    User user = BeanUtil.copyProperties(item, User.class);
//
////    Long roleId=getRoleId(item.getUserType());
//
//
//    user.setId(null);
//    user.setUsername(item.getAccount());
//    user.setDeptId("001");
////    user.setRoleId(roleId);
//    user.setPostId("400");
//    user.setOpenid(item.getUniqueCode());
//    user.setIsDeleted(0);
//    user.setStatus(1);
//    //基础信息
//    user.setName(item.getUserName());
//    return userRepository.findByOpenid(item.getUniqueCode())
//        .map(v -> {
//          v.setPassword(item.getPassword());
//          //基础信息
////          v.setRoleId(roleId);
//          v.setName(item.getUserName());
//          v.setPhone(item.getPhone());
//          v.setEmail(item.getEmail());
//          v.setStatus(1);
//          return v;
//        }).defaultIfEmpty(user);
//  }
//
//  @Override
//
//  public Mono<UserCaozuoyuanVo> saveNc(UserCaozuoyuanVo object) {
//    List<User> userList=new ArrayList();
//    return Mono.just(object)
//        // 保存到操作员表
//        .flatMap(userCaozuoyuanRepository::save)
//        .flatMap(item -> userCaozuoyuanCastSystemUser(item))
//        // 保存到sys_user表
//        .flatMap(userRepository::save)
//        .map(item->{
//          userList.add(item);
//          return userList;
//        })
//
//        // 保存角色
//        .map(userList1 ->
//            Flux.fromIterable(object.getUserType())
//                .map(typeCode -> getRoleId(typeCode))
//                .map(roleId -> {
//                  UserRole userRole = new UserRole();
//                  userRole.setRoleId(roleId);
//                  userRole.setUserId(userRole.getUserId());
//                  return userRole;
//                })
//                .flatMap(userRole -> {
//                  return userRoleRepository
//                      .findByUserIdAndRoleId(userRole.getUserId(), userRole.getRoleId())
//                      .defaultIfEmpty(userRole);
//                })
//        )
//        .flatMapMany(userRoleRepository::saveAll)
//        .collectList()
//        // 保存默认机构
//        .map(item -> {
//          UserJiGou userJiGou = new UserJiGou();
//          userJiGou.setUserId(userList.get(0).getId());
//          userJiGou.setJigouId("1340925547646214145");
//          return userJiGou;
//        })
//        .flatMap(userJiGouRepository::save)
//        // 保存默认Layout
//        .map(item -> object);
//
//  }
//
//  public String getRoleId(String userType) {
//    if (userType.equals(GROUP_MANAGER_TYPE)) {
//      return GROUP_MANAGER_ROLE_ID.toString();
//    }
//    if (userType.equals(ORIGIN_MANAGER_TYPE)) {
//      return ORIGIN_MANAGER_ROLE_ID.toString();
//
//    }
//    if (userType.equals(ACCOUNT_MANAGER_TYPE)) {
//      return ACCOUNT_MANAGER_ROLE_ID.toString();
//
//    }
//    if (userType.equals(SUPER_ADMIN_TYPE)) {
//      return SUPER_ADMIN_ROLE_ID.toString();
//    }
//
//    throw new RuntimeException("出错");
//  }
//}
