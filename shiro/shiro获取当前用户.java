import org.apache.shiro.SecurityUtils;
import org.apache.shiro.UnavailableSecurityManagerException;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.subject.Subject;


public class UserUtils {

	/**
	 * 获取当前登陆用户的ID
	 * 
	 * @return
	 */
	public static Long getCurrentUserId() {
		return getCurrentUser().getLong("id");
	}

	/**
	 * 获取当前用户
	 * User是实体model
	 * @return 取不到返回 new User()
	 */
	public static User getCurrentUser() {
		ShiroUser principal = getPrincipal();
		if (principal != null) {
			
			//这里通过用户凭证信息中的资料（一般是用户名）来查询数据库，从而获取用户对象
			//User user = UserService.me.findByLoginName(principal.getLoginName());
			
			if (user != null) {
				return user;
			}
			return new User();
		}
		// 如果没有登录，则返回实例化空的User对象。
		return new User();
	}

	/**
	 * 获取当前登录者对象
	 */
	public static ShiroUser getPrincipal() {
		try {
			Subject subject = SecurityUtils.getSubject();
			ShiroUser principal = (ShiroUser) subject.getPrincipal();
			if (principal != null) {
				return principal;
			}
			// subject.logout();
		} catch (UnavailableSecurityManagerException e) {

		} catch (InvalidSessionException e) {

		}
		return null;
	}

	public static void loginOut() {
		Subject subject = SecurityUtils.getSubject();
		subject.logout();
	}

}


/*
public class ShiroUser implements Serializable {
    private static final long serialVersionUID = 993537992205330748L;

    public String loginName;
    public String name;
	//省略get/set 和构造方法
*/

