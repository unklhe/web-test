package cloudtp.com.action;

import java.util.List;

import cloudtp.com.user.User;
import cloudtp.com.user.UserInfo;

import com.opensymphony.xwork2.ActionSupport;

public class UserAction extends ActionSupport
{	

	/**
	 * 
	 */
	private static final long serialVersionUID = -5234531760372616233L;
    private List<UserInfo> _list = null;
    private String _filter = "";
	public String list() throws Exception
	{
		_list = User.GetUserList(_filter);	
	    return "success";
	}
	
	public List<UserInfo> getList()
	{
		return _list;
	}
	
	public void setFilter(String filter){
		_filter = filter;
	}
}
