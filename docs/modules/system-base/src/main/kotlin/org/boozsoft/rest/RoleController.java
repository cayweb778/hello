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
package org.boozsoft.rest;



import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.boozsoft.repo.RoleRepository;
import org.boozsoft.repo.entity.Role;
import org.springbooz.core.tool.result.R;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


@RestController
@RequiredArgsConstructor
@RequestMapping("/sys/role")
@Api(value = "角色表", tags = "系统：角色表")
public class RoleController {
    private final RoleRepository repository;
    @PostMapping
    @ApiOperation(value = "新增或修改", notes = "传入code")
    public Mono save(@RequestBody Role object){
        return repository.save(object).map(o->R.ok().setResult(o));
    }
    @DeleteMapping
    @ApiOperation(value = "删除", notes = "传入code")
    public Mono delete(String id){
        return repository.deleteById(id).then(Mono.just(R.ok()));
    }
    @GetMapping
    @ApiOperation(value = "查询", notes = "传入code")
    public Mono get(String id){
        return repository.findById(id);
    }

    @GetMapping("/list")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> list(){
        return repository.findAllBy().collectList().map(R::page);
    }
//	@ApiImplicitParams({
//		@ApiImplicitParam(name = "roleName", value = "参数名称", paramType = "query", dataType = "string"),
//		@ApiImplicitParam(name = "roleAlias", value = "角色别名", paramType = "query", dataType = "string")
//	})
//	@ApiOperationSupport(order = 2)
//	@ApiOperation(value = "列表", notes = "传入role")
//	private IRoleService roleService;
//
//	/**
//	 * 详情
//	 */
//	@GetMapping("/detail")
//	@ApiOperationSupport(order = 1)
//	@ApiOperation(value = "详情", notes = "传入role")
//	public R<RoleVO> detail(Role role) {
//		Role detail = roleService.getOne(Condition.getQueryWrapper(role));
//		return R.data(RoleWrapper.build().entityVO(detail));
//	}
//

//	/**
//	 * 获取角色树形结构
//	 */
//	@GetMapping("/tree")
//	@ApiOperationSupport(order = 3)
//	@ApiOperation(value = "树形结构", notes = "树形结构")
//	public R<List<RoleVO>> tree(String tenantId, BladeUser bladeUser) {
//		List<RoleVO> tree = roleService.tree(Func.toStr(tenantId, bladeUser.getTenantId()));
//		return R.data(tree);
//	}
//
//	/**
//	 * 新增或修改
//	 */
//	@PostMapping("/submit")
//	@ApiOperationSupport(order = 4)
//	@ApiOperation(value = "新增或修改", notes = "传入role")
//	public R submit(@Valid @RequestBody Role role, BladeUser user) {
//		if (Func.isEmpty(role.getId())) {
//			role.setTenantId(user.getTenantId());
//		}
//		return R.status(roleService.saveOrUpdate(role));
//	}
//
//
//	/**
//	 * 删除
//	 */
//	@PostMapping("/remove")
//	@ApiOperationSupport(order = 5)
//	@ApiOperation(value = "删除", notes = "传入ids")
//	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
//		return R.status(roleService.removeByIds(Func.toLongList(ids)));
//	}
//
//	/**
//	 * 设置菜单权限
//	 */
//	@PostMapping("/grant")
//	@ApiOperationSupport(order = 6)
//	@ApiOperation(value = "权限设置", notes = "传入roleId集合以及menuId集合")
//	public R grant(@RequestBody GrantVO grantVO) {
//		boolean temp = roleService.grant(grantVO.getRoleIds(), grantVO.getMenuIds());
//		return R.status(temp);
//	}

}
