package cloudtp.com.user;

public class UserInfo {

	private String _userId; 
	private String _userName;
	private String _descr = "";
	
	public UserInfo(String userId)
	{
		_userId = userId;
		_userName = userId;
		_descr = "该员工没有注册!";
	}
	
	public UserInfo(String userId, String userName, String descr)
	{
		_userId = userId;
		_userName = userName;
		_descr = descr;
	}
	
	public String getUserId()
	{
		return _userId;
	}
	
	public String getUserName()
	{
		return _userName;
	}
	
	public String getDescription()
	{
		return _descr;
	}
}
