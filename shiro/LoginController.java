import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.session.SessionException;
import org.apache.shiro.subject.Subject;



/**
 * 
 * 登录的Controller
 *
 */
public class LoginController extends BaseController {

    private static final String LOGIN_URL = "login.html";

    @Clear
    @Before(LoginValidator.class)
    public void login() {
        if (RequestMethod.POST.equalsIgnoreCase(getRequest().getMethod())) {
            doPost();
            return;
        }
        render("login.html");
    }

    @Clear
    private void doPost() {
        try {
            String username = getPara("username");
            String password = getPara("password");

            Subject subject = SecurityUtils.getSubject();
            User user = UserService.me.findByLoginName(username);
            if (user == null) {
                setMessage("用户 不存在");
                return;
            }
            String salt = user.getStr("salt");
            
			//这里就需要对密码进行md5的加密，shiroReam就不需要配置加密的代码了，直接获取加密后的密码进行匹配
            UsernamePasswordToken token = new UsernamePasswordToken(username, HashKit.md5(password + salt));

            // 进行用户名和密码验证
            subject.login(token);
            redirect(adminPath);
            return;
        } catch (LockedAccountException e) {
            LOG.error(e.getMessage());
            // 账号已被锁定
            setMessage("账号已被锁定！");
        } catch (AuthenticationException e) {
            LOG.error(e.getMessage());
            // 用户名或者密码错误
            setMessage("用户名或者密码错误！");
        } catch (Exception e) {
            LOG.error(e.getMessage());
            // 系统异常
            setMessage("系统异常！");
        }
        render("login.html");
    }

    @Clear
    @RequiresAuthentication
    public void logout() {
        Subject currentUser = SecurityUtils.getSubject();
        try {
            currentUser.logout();
            this.removeSessionAttr("user");
            this.redirect(LOGIN_URL);
            redirect(adminPath);
        } catch (SessionException ise) {
            LOG.debug("Encountered session exception during logout.  This can generally safely be ignored.", ise);
        } catch (Exception e) {
            LOG.debug("登出发生错误", e);
        }
    }
    
    @Clear
    public void unauthorized(){
    	render("unauthorized.html");
    }
}
