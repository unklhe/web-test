package cloudtp.com.action;


import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;

public class LogoutAction extends ActionSupport
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6067471379089984302L;

	public String execute() throws Exception
	{
		ServletActionContext.getRequest().getSession().removeAttribute(cloudtp.com.Constants.UserInfo);		
	    return "success";
	}
}
