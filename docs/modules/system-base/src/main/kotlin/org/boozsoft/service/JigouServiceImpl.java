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


import cn.hutool.core.bean.BeanUtil;
import lombok.RequiredArgsConstructor;
import org.boozsoft.domain.vo.JigouVO;
import org.boozsoft.repo.JigouRepository;
import org.boozsoft.repo.entity.Jigou;
import org.springbooz.core.tool.node.ForestNodeMerger;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
@Service
public class JigouServiceImpl {

    private final JigouRepository repository;

    public Mono<List<JigouVO>> tree() {
        return repository.findAll().map(menu -> {
            JigouVO vo = new JigouVO();
            BeanUtil.copyProperties(menu, vo);
            return vo;
        }).collectList().map(list -> ForestNodeMerger.merge((List<JigouVO>)list));
    }


}
