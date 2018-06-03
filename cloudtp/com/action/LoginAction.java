package cloudtp.com.action;

import javax.servlet.http.Cookie;

import org.apache.struts2.ServletActionContext;

import cloudtp.com.user.User;
import cloudtp.com.user.UserInfo;

import com.opensymphony.xwork2.ActionSupport;

public class LoginAction extends ActionSupport
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1587027172402202435L;

	private String _userId = "";
	private String _passwd = "";
	private String _userName = "";
    private boolean _rememberMe;
    private String _loginInfo;
	
	
	public String execute() throws Exception
	{
		if("".equals(_userId) && "".equals(_passwd))
			return "login";
		
		if(!User.ValidateUser(_userId, _passwd))
		{
			_loginInfo = "登录失败! 请检查您的用户账号和密码.";
			return "failure";
		}
		UserInfo userInfo = User.GetUserInfo(_userId);
		if(userInfo==null)
		{
			return "register";
		}
		ServletActionContext.getRequest().getSession().setAttribute(cloudtp.com.Constants.UserInfo, userInfo);
		if(_rememberMe)
		{
			Cookie cookie1 = new Cookie("userId", _userId);
			Cookie cookie2 = new Cookie("pwd", _passwd);
			cookie1.setMaxAge(60*60*24*30);
			cookie2.setMaxAge(60*60*24*30);
			ServletActionContext.getResponse().addCookie(cookie1);
			ServletActionContext.getResponse().addCookie(cookie2);
		}
		else
		{
			Cookie cookie1 = new Cookie("userId", "");
			Cookie cookie2 = new Cookie("pwd", "");
			cookie1.setMaxAge(0);			
			cookie2.setMaxAge(0);
			ServletActionContext.getResponse().addCookie(cookie1);
			ServletActionContext.getResponse().addCookie(cookie2);
		}
		ServletActionContext.getResponse().sendRedirect(ServletActionContext.getRequest().getContextPath() + "/top.action");
		_loginInfo = "登录成功!马上跳转到主页面..." +
				"<script>window.location = top.action;</script>";
	     return "success";
	}
	
	public String register() throws Exception
	{
		if("".equals(_userId) && "".equals(_passwd) && "".equals(_userName))
			return "login";
		
		if(!User.ValidateUser(_userId, _passwd))
		{
			_loginInfo = "注册失败! 请检查您的用户账号和密码.";
			return "register";
		}
		if(!User.RegisterUser(_userId, _userName))
		{
			_loginInfo = "注册失败! 请联系系统管理员.";
			return "register";
		}
		UserInfo userInfo = User.GetUserInfo(_userId);		
		ServletActionContext.getRequest().getSession().setAttribute(cloudtp.com.Constants.UserInfo, userInfo);
		if(_rememberMe)
		{
			Cookie cookie1 = new Cookie("userId", _userId);
			Cookie cookie2 = new Cookie("pwd", _passwd);
			cookie1.setMaxAge(60*60*24*30);
			cookie2.setMaxAge(60*60*24*30);
			ServletActionContext.getResponse().addCookie(cookie1);
			ServletActionContext.getResponse().addCookie(cookie2);
		}
		ServletActionContext.getResponse().sendRedirect(ServletActionContext.getRequest().getContextPath() + "/frame.action");
		_loginInfo = "登录成功!马上跳转到主页面..." +
				"<script>window.location = frame.action;</script>";
	     return "success";
	}
	
	public String getUserId()
	{
		return _userId;
	}
	
	public String getPasswd()
	{
		return _passwd;
	}
	
	public void setUserId(String userId)
	{
		_userId = userId;
	}
	
	public void setUserName(String value)
	{
		_userName = value;
	}
	
	public void setPasswd(String passwd)
	{
		_passwd = passwd;
	}

	public void setRememberMe(String rememberMe)
	{
		_rememberMe = "on".equals(rememberMe);
	}
	
	public String getLoginInfo()
	{
		return _loginInfo;
	}
	
}
