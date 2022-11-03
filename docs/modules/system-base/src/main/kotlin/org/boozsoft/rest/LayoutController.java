package org.boozsoft.rest;//package org.springblade.system.controller;
//
//import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import lombok.AllArgsConstructor;
//import org.springblade.core.mp.support.Condition;
//import org.springblade.core.secure.AuthInfo;
//import org.springblade.core.secure.annotation.PreAuth;
//import org.springblade.core.tool.api.R;
//import org.springblade.core.tool.constant.RoleConstant;
//import org.springblade.system.entity.Menu;
//import org.springblade.system.user.entity.UserInfo;
//import org.springblade.system.vo.MenuVO;
//import org.springblade.system.wrapper.MenuWrapper;
//import org.springframework.web.bind.annotation.*;
//import springfox.documentation.annotations.ApiIgnore;
//
//import javax.servlet.http.HttpServletRequest;
//import java.util.Arrays;
//import java.util.Map;
//import java.util.Optional;
//
///**
// * 控制器
// *
// * @author 木子桉易洋
// * @version 1.0
// * @company 财税达软件科技
// * @date 2020/12/18 12:46 下午
// */
//@RestController
//@AllArgsConstructor
//@RequestMapping("/layout")
//@Api(value = "菜单", tags = "菜单")
//public class LayoutController {
//
//	@GetMapping
//	@ApiOperationSupport(order = 1)
//	@ApiOperation(value = "获取布局类型", notes = "传入userinfo")
//	public R<String> detail(AuthInfo info) {
//		return R.data(
//			Arrays.stream(info.getAuthority().split(","))
//				.filter(authName -> authName.equals("administrator")).findFirst().isPresent()
//				? "admin" : "tenant");
//	}
//
//}
