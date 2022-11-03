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
import org.boozsoft.domain.vo.DeptVO;
import org.boozsoft.repo.DeptRepository;
import org.springbooz.core.tool.node.ForestNodeMerger;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
@Service
public class DeptServiceImpl {

	private final DeptRepository deptRepository;
	public Mono<List<DeptVO>> tree() {
		/**
         * select id, parent_id, dept_name as title, id as 'value', id as 'key' from blade_dept where is_deleted = 0
		 *         <if test="_parameter!=null">
		 *             and tenant_id = #{_parameter}
		 *         </if>
		 */

		return deptRepository.findAll().map(dept->{
			DeptVO deptVO = new DeptVO();
			return deptVO;
		}).collectList().map(list->{
			return ForestNodeMerger.merge(list);
		});
	}


}
