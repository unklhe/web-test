package cloudtp.com.action;

import java.util.List;

import com.opensymphony.xwork2.ActionSupport;
import cloudtp.com.mis.ExecuteRecord;
import cloudtp.com.mis.Execution;

public class ExecutionAction  extends ActionSupport{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int _id;

	
	private List<ExecuteRecord> _list;
	
	
	
	public String list()
	{
		_list = Execution.QueryExecuteRecord(_id);
		return SUCCESS;
	}
	
	public void setPlanId(int id)
	{
		_id = id;
	}
	
	public List<ExecuteRecord> getList()
	{
		return _list;
	}	
	
}
