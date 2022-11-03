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
package org.boozsoft.service;


import lombok.RequiredArgsConstructor;
import org.boozsoft.repo.UserRepository;
import org.boozsoft.repo.UserRoleRepository;
import org.boozsoft.repo.entity.User;
import org.boozsoft.repo.entity.UserRole;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@Service
public class UserServiceImplement {

    private final UserRepository repository;
    private final UserRoleRepository userRoleRepository;


//    public Mono<List<UserVO>> tree() {
//        return repository.findAll().map(user -> {
//            UserVO UserVO2 = new UserVO();
//            BeanUtil.copyProperties(user, UserVO2);
//            return UserVO2;
//        }).collectList().map(list -> ForestNodeMerger.merge((List<UserVO>)list));
//    }
    public Flux<User> findAllUserByRoleId(String roleId){
        Flux<String> userIdFlux=userRoleRepository.findByRoleId(roleId).map(UserRole::getUserId);
        return repository.findAllById(userIdFlux);
    }


}
