package cloudtp.com.action;


import java.util.ArrayList;
import java.util.List;

import org.apache.struts2.ServletActionContext;


import com.opensymphony.xwork2.ActionSupport;
import cloudtp.com.Constants;
import cloudtp.com.mis.CaseDAO;
import cloudtp.com.mis.MisService;
import cloudtp.com.mis.SolutionNode;
import cloudtp.com.user.UserInfo;

public class SolutionAction  extends ActionSupport{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5296853850022895397L;
	private String _index = "";
	private String _name = "";
	private String _createDate = "";
	private String _xml;
	private String _xmlFromDb = "";
	private String _ownerId = "";
	private String _ownerName = "";
	private int _returnCode;
	private String _message;
	private String _filter = "";
	private boolean _showAll = false;
	private List<SolutionNode> _list = new ArrayList<SolutionNode>();
	
	public String save()
	{
		try {
			UserInfo userInfo = (UserInfo)ServletActionContext.getRequest().getSession().getAttribute(Constants.UserInfo);
								
			String xmlString = java.net.URLDecoder.decode(_xml, "utf-8");
			StringBuilder sbIndex = new StringBuilder();
			sbIndex.append(_index);
			StringBuilder sbResult = new StringBuilder();
			if( MisService.SaveExecution(userInfo.getUserId(), _name, xmlString, sbIndex,sbResult))
			{
				_returnCode = 1;
				_index = sbIndex.toString();
				_message = "保存成功";
			}
			else
			{
				_returnCode = 0;
				_message = sbResult.toString();
			}
		} catch (Exception e) {
			_returnCode = 0;
			_message = "保存失败:" + e.getMessage();
		}
		return SUCCESS;
	}
	
	public String delete()
	{
		try {
			//UserInfo userInfo = (UserInfo)ServletActionContext.getRequest().getSession().getAttribute(Constants.UserInfo);								
			StringBuilder sbResult = new StringBuilder();
			if( MisService.DeleteExecution(_index, sbResult))
			{
				_returnCode = 1;
			}
			else
			{
				_returnCode = 0;
				_message = sbResult.toString();
			}
		} catch (Exception e) {
			_returnCode = 0;
			_message = "保存失败:" + e.getMessage();
		}
		return SUCCESS;
	}
	
	public String list()
	{
		UserInfo userInfo = (UserInfo)ServletActionContext.getRequest().getSession().getAttribute(Constants.UserInfo);
		String[][] list = CaseDAO.GetSolutionsList(userInfo.getUserId(), _filter, _showAll);
		if(list!=null)
		{
			for(int i=0;i<list.length;i++)
				_list.add(new SolutionNode(list[i][0], list[i][1], list[i][2], list[i][3], list[i][4]));
		}
		return SUCCESS;
	}
	
	public String obtainInfo()
	{
		String[] result = CaseDAO.GetSolutions(_index);
		_name = result[0];
		_ownerId = result[1];
		_ownerName = result[2];
		if(_ownerName==null || _ownerName.length()<1)
			_ownerName = _ownerId;
		_createDate = result[3];
		_xmlFromDb = result[4];
		return SUCCESS;
	}
	public void setXml(String xml)
	{
		_xml = xml;
	}	
	
	public String getSolution()
	{
		return _xmlFromDb;
	}
	
	public void setSluId(String sluId)
	{
		_index = sluId;
	}	
	
	public void setSluName(String name)
	{
		_name = name;
	}	
	
	public void setFilter(String filter)
	{
		_filter = filter;
	}	
	
	public int getCode()
	{
		return _returnCode;
	}
	
	public String getIndex()
	{
		return _index;
	}
	
	public void setShowAll(String showAll)
	{
		_showAll = "true".equalsIgnoreCase(showAll);
	}
	
	public String getName()
	{
		return _name;
	}
	
	
	public String getMessage()
	{
		return _message;
	}
	
	public String getOwnerId()
	{
		return _ownerId;
	}
	
	public String getOwnerName()
	{
		return _ownerName;
	}
	
	public String getCreateDate()
	{
		return _createDate;
	}
	
	public List<SolutionNode> getList()
	{
		return _list;
	}
	
}
