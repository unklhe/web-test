package cloudtp.com.interceptor;

import java.util.Map;

import javax.servlet.http.Cookie;

import org.apache.struts2.ServletActionContext;

import cloudtp.com.Constants;
import cloudtp.com.action.LoginAction;
import cloudtp.com.user.User;
import cloudtp.com.user.UserInfo;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.*;

public class LoginInterceptor  implements Interceptor
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5226889307507688947L;
	

	public void destroy() {

		
	}

	public void init() {
		
	}

	public String intercept(ActionInvocation actionInvocation) throws Exception {
		
		ActionContext ctx = actionInvocation.getInvocationContext();		
		Map<String, Object> session = ctx.getSession();
		UserInfo userInfo = (UserInfo)session.get(Constants.UserInfo);
		if(userInfo!=null)
			return actionInvocation.invoke();
		
		Object action =  actionInvocation.getAction();
		if(action instanceof LoginAction)
			return actionInvocation.invoke();
		
		if(session.get(":Initialized")==null)
		{
			session.put(":Initialized", true);
			Cookie[] cookieList = ServletActionContext.getRequest().getCookies();
			if(cookieList!=null&&cookieList.length!=0)
			 {
				String userId = "";
				String passwd = "";
			     for(int i=0;i<cookieList.length;i++)
			     {
			         String keyname=  cookieList[i].getName();
			         String value=  cookieList[i].getValue();
			         if("userId".equals(keyname))
			        	 userId = value;
			         if("pwd".equals(keyname))
			        	 passwd = value;
			      }
			     if(!"".equals(userId) && !"".equals(passwd) )
			     {
			    	 if(User.ValidateUser(userId, passwd))
			    	 {
				    	 session.put(Constants.UserInfo, User.GetUserInfo(userId));
				    	 return actionInvocation.invoke();
			    	 }
			     }
			 }		
		}
		return Action.LOGIN;
	}
	
}
