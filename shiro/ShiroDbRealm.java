package com.hz.wf.admin.common.shiro;

import java.util.ArrayList;
import java.util.List;

import org.apache.shiro.authc.AccountException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;


/**
 * shiro认证Real
 */
public class ShiroDbRealm extends AuthorizingRealm {

	private static Log log = Log.getLog(ShiroDbRealm.class);

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(
			AuthenticationToken token) throws AuthenticationException {

		UsernamePasswordToken upToken = (UsernamePasswordToken) token;
		String username = upToken.getUsername();

		if (username == null) {
			log.warn("用户名不能为空");
			throw new AccountException("用户名不能为空");
		}

		//User是model
		User user = null;
		try {
			//数据库查询user
			user = UserService.me.findByLoginName(username);
		} catch (Exception e) {
			log.error("error to doGetAuthenticationInfo", e);
		}

		if (user == null) {
			log.warn("用户不存在");
			throw new UnknownAccountException("用户不存在!");
		}

		ShiroUser principal = new ShiroUser(username);

		//可以往凭证中加入其他user的信息
		principal.setName(user.getName());
		
		//用户注册的时候就用salt对密码进行md5加密
		//loginController的时候 password --> HashKit.md5(password + salt) 对密码进行加密
		//这里就不需要进行加密了，token中的密码就是已经加密过了的
		return new SimpleAuthenticationInfo(principal, user.getStr("password"),getName());
	}

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(
			PrincipalCollection principals) {
		ShiroUser principal = (ShiroUser) getAvailablePrincipal(principals);
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		// 基于Role的权限信息
		// info.addRole("admin");
		// 基于Permission的权限信息
		List<String> permissions = new ArrayList<String>();

		// 获取用户的角色
		String loginName = principal.getLoginName();
		User loginUser = UserService.me.findByLoginName(loginName);
		// 获取角色菜单
		List<Menu> userMenus = MenuService.me.findUserMenus(loginUser.getId());

		// 获取菜单的权限
		if (CollectionKit.isNotEmpty(userMenus)) {
			for (Menu menu : userMenus) {
				// 从数据库取权限字段 wf:dict:[edit|view...] Chenglong
				String permission = menu.getPermission();
				if (StrKit.notBlank(permission)) {
					// 添加授权
					permissions.add(permission);
				}
			}
		}
		info.addStringPermissions(permissions);
		return info;
	}

}
