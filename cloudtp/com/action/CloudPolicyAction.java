package cloudtp.com.action;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;

import cloudtp.com.Constants;
import cloudtp.com.cloud.CloudDAO;
import cloudtp.com.user.UserInfo;

public class CloudPolicyAction extends ActionSupport {

	

	/**
	 * 
	 */
	private static final long serialVersionUID = 8037262787634602653L;
	private String _policyId;
	private int _type = 1;
	private String _policyName;
	private String[] _testTypes ;
	private String[] _testSolutionIds ;
	private String[][] _testSolutions ;
	private String _createDate = "今天";
	private String _pageInfo;	
	public String execute()
	{
		UserInfo userInfo = (UserInfo)ServletActionContext.getRequest().getSession().getAttribute(Constants.UserInfo);
		StringBuilder result = new StringBuilder();
		if(!CloudDAO.savePolicy(userInfo.getUserId(), _type, _policyId, _policyName, _testTypes, _testSolutionIds, result))
		{
			if(_testTypes!=null && _testTypes.length>0)
			{
				_testSolutions = new String[_testTypes.length][];
				for(int i=0;i<_testTypes.length;i++)
				{
					_testSolutions[i] = new String[3];
					_testSolutions[i][0] = _testTypes[i];
					_testSolutions[i][1] = _testSolutionIds[i];
					_testSolutions[i][2] = CloudDAO.getSolutionName(_testSolutions[i][1]);					
				}
			}
			_pageInfo = String.format("<span class=error>%1$s</span>", result.toString());
		}
		else
		{
			_policyId = result.toString();
			String[] policyInfo = CloudDAO.getPolicy(_policyId);
			_createDate = policyInfo[3];			
			_testSolutions = CloudDAO.getPolicySolutions(_policyId);
			
			_pageInfo = String.format("<span class=success>%1$s</span>", "保存成功");
		}
		return "virtulation";
	}
	
	public String delete()
	{
		UserInfo userInfo = (UserInfo)ServletActionContext.getRequest().getSession().getAttribute(Constants.UserInfo);
		StringBuilder result = new StringBuilder();
		if(!CloudDAO.deletePolicy(_policyId, userInfo.getUserId(), result))
		{
			_pageInfo = String.format("<span class=error>%1$s</span>", result.toString());
		}
		else
		{
			_policyId = null;
			_policyName = "";
			_createDate = "今天";
			_testSolutions = null;
			_pageInfo = String.format("<span class=success>%1$s</span>", "删除成功");
		}
		return "virtulation";
	}
	
	public String open()
	{
		String[] policyInfo = CloudDAO.getPolicy(_policyId);
		if(policyInfo==null)
		{
			_pageInfo = String.format("<span class=error>%1$s</span>", "记录不存在");
		}
		else
		{
			_policyName = policyInfo[1];
			_type = Integer.parseInt(policyInfo[2]);
			_testSolutions = CloudDAO.getPolicySolutions(_policyId);
			_createDate = policyInfo[3];
		}
		return "virtulation";
	}
	
	public String view()
	{
		String[] policyInfo = CloudDAO.getPolicy(_policyId);
		if(policyInfo==null)
		{
			_pageInfo = String.format("<span class=error>%1$s</span>", "记录不存在");
		}
		else
		{
			_policyName = policyInfo[1];
			_type = Integer.parseInt(policyInfo[2]);
			_testSolutions = CloudDAO.getPolicySolutions(_policyId);
			_createDate = policyInfo[3];
		}
		return "executepolicy";
	}	
	
	public void setPolicyId(String value) {
		_policyId = value;
	}
	
	public void setProjectType(int value) {
		_type = value;
	}

	public void setPolicyName(String value) {
		_policyName = value;
	}

	public void setTestType(String value) {
		_testTypes = value.replace(" ","").split(",");
	}

	public void setTestSolution(String value) {
		_testSolutionIds = value.replace(" ","").split(",");
	}
	
	public String getPolicyId()
	{
		return _policyId;
	}
	
	public int getType() {
		return _type;
	}

	public String getPolicyName() {
		return _policyName;
	}

	public String[][] getTestSolutions()
	{
		return _testSolutions;
	}
	
	public String getPageInfo()
	{
		return _pageInfo;
	}
	
	public String getCreateDate()
	{
		return _createDate;
	}
	

}
