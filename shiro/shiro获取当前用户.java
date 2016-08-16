import org.apache.shiro.SecurityUtils;
import org.apache.shiro.UnavailableSecurityManagerException;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.subject.Subject;


public class UserUtils {

	/**
	 * ��ȡ��ǰ��½�û���ID
	 * 
	 * @return
	 */
	public static Long getCurrentUserId() {
		return getCurrentUser().getLong("id");
	}

	/**
	 * ��ȡ��ǰ�û�
	 * User��ʵ��model
	 * @return ȡ�������� new User()
	 */
	public static User getCurrentUser() {
		ShiroUser principal = getPrincipal();
		if (principal != null) {
			
			//����ͨ���û�ƾ֤��Ϣ�е����ϣ�һ�����û���������ѯ���ݿ⣬�Ӷ���ȡ�û�����
			//User user = UserService.me.findByLoginName(principal.getLoginName());
			
			if (user != null) {
				return user;
			}
			return new User();
		}
		// ���û�е�¼���򷵻�ʵ�����յ�User����
		return new User();
	}

	/**
	 * ��ȡ��ǰ��¼�߶���
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
	//ʡ��get/set �͹��췽��
*/

