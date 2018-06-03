package cloudtp.com.action;


import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;

import cloudtp.com.Constants;
import cloudtp.com.cloud.CloudDAO;
import cloudtp.com.user.UserInfo;


/**
 * 云模块UI功能的控制和业务接口调用类
 * @author Administrator
 *
 */
public class CloudAction  extends ActionSupport{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5296853850022895397L;
	private int _category;
	private String _id;
	private String _filter;
	private boolean _showAll;
	private String[][] _twoDimData;
	private String[] _oneDimData;
	
	/**
	 * 获取云测试能力功能树节点
	 * @return
	 */
	public String queryTools()
	{
		_twoDimData = CloudDAO.getNodes(_category, _id);		
		return SUCCESS;
	}
	
	/**
	 * 获取云测试能力功能树节点, 此方法用户后期根据需要扩展, 目前功能同queryTools
	 * @return
	 */
	public String queryNodes()
	{
		_twoDimData = CloudDAO.getNodes(_category, _id);		
		return SUCCESS;
	}
	
	/**
	 * 获取云测试报告列表
	 * @return
	 */
	public String queryReports()
	{
		UserInfo userInfo = (UserInfo)ServletActionContext.getRequest().getSession().getAttribute(Constants.UserInfo);
		
		_twoDimData = CloudDAO.getReports(_category, _filter, _showAll, userInfo.getUserId());		
		return SUCCESS;
	}
	
	/**
	 * 获取云测试报告信息
	 * @return
	 */
	public String queryReport()
	{
		_oneDimData = CloudDAO.getReport(_id);		
		return SUCCESS;
	}
	
	/**
	 * 获取云测试定制策略列表
	 * @return
	 */
	public String queryPolicyes()
	{
		UserInfo userInfo = (UserInfo)ServletActionContext.getRequest().getSession().getAttribute(Constants.UserInfo);
		
		_twoDimData = CloudDAO.getPolicyes(_filter, _showAll, userInfo.getUserId());		
		return SUCCESS;
	}
	
	public void setCategory(int category)
	{
		_category = category;
	}
	
	public void setId(String id)
	{
		_id = id;
	}
	
	public void setFilter(String filter)
	{
		_filter = filter;
	}
	
	public void setShowAll(String value)
	{
		_showAll = "true".equals(value);
		
	}
	
	public String[][] getTools()
	{
		return _twoDimData;
	}
	
	public String[][] getNodes()
	{
		return _twoDimData;
	}
	
	public String[][] getPolicyes()
	{
		return _twoDimData;
	}
	
	public String[][] getReports()
	{
		return _twoDimData;
	}
	
	public String[] getReport()
	{
		return _oneDimData;
	}	
	
	
}
