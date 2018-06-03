package cloudtp.com.action;


import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;

import cloudtp.com.Constants;
import cloudtp.com.cloud.CloudDAO;
import cloudtp.com.user.UserInfo;


/**
 * ��ģ��UI���ܵĿ��ƺ�ҵ��ӿڵ�����
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
	 * ��ȡ�Ʋ��������������ڵ�
	 * @return
	 */
	public String queryTools()
	{
		_twoDimData = CloudDAO.getNodes(_category, _id);		
		return SUCCESS;
	}
	
	/**
	 * ��ȡ�Ʋ��������������ڵ�, �˷����û����ڸ�����Ҫ��չ, Ŀǰ����ͬqueryTools
	 * @return
	 */
	public String queryNodes()
	{
		_twoDimData = CloudDAO.getNodes(_category, _id);		
		return SUCCESS;
	}
	
	/**
	 * ��ȡ�Ʋ��Ա����б�
	 * @return
	 */
	public String queryReports()
	{
		UserInfo userInfo = (UserInfo)ServletActionContext.getRequest().getSession().getAttribute(Constants.UserInfo);
		
		_twoDimData = CloudDAO.getReports(_category, _filter, _showAll, userInfo.getUserId());		
		return SUCCESS;
	}
	
	/**
	 * ��ȡ�Ʋ��Ա�����Ϣ
	 * @return
	 */
	public String queryReport()
	{
		_oneDimData = CloudDAO.getReport(_id);		
		return SUCCESS;
	}
	
	/**
	 * ��ȡ�Ʋ��Զ��Ʋ����б�
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
