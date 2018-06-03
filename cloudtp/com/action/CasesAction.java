package cloudtp.com.action;

import java.util.List;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;
import cloudtp.com.Constants;
import cloudtp.com.mis.CaseNode;
import cloudtp.com.mis.MisService;
import cloudtp.com.user.UserInfo;

/**
 * 测试用例Action, 测试用例相关UI功能的控制和业务接口调用类
 * @author Administrator
 *
 */
public class CasesAction  extends ActionSupport{

	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = 5296853850022895397L;
	
	//测试用例树结构节点类型
	private int _category;
	//节点编号
	private String _id;
	//节点列表
	private List<CaseNode> _list;
	
	/**
	 * 默认Action, 用来获取指定节点下的子节点
	 */
	public String execute()
	{
		UserInfo userInfo = (UserInfo)ServletActionContext.getRequest().getSession().getAttribute(Constants.UserInfo);
		_list = MisService.QueryNodes(userInfo.getUserId(), _category, _id, false);
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
	
	public List<CaseNode> getList()
	{
		return _list;
	}
	
	public void setList(List<CaseNode> list)
	{
		_list = list;
	}
	
}
