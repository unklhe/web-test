package cloudtp.com.action;

import java.util.List;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;
import cloudtp.com.Constants;
import cloudtp.com.mis.CaseNode;
import cloudtp.com.mis.MisService;
import cloudtp.com.user.UserInfo;

/**
 * ��������Action, �����������UI���ܵĿ��ƺ�ҵ��ӿڵ�����
 * @author Administrator
 *
 */
public class CasesAction  extends ActionSupport{

	/**
	 * ���л��汾��
	 */
	private static final long serialVersionUID = 5296853850022895397L;
	
	//�����������ṹ�ڵ�����
	private int _category;
	//�ڵ���
	private String _id;
	//�ڵ��б�
	private List<CaseNode> _list;
	
	/**
	 * Ĭ��Action, ������ȡָ���ڵ��µ��ӽڵ�
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
