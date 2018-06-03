package cloudtp.com.action;

import java.util.Calendar;
import java.util.List;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;
import cloudtp.com.Constants;
import cloudtp.com.mis.ExecuteRecord;
import cloudtp.com.mis.Execution;
import cloudtp.com.mis.PlanNode;
import cloudtp.com.mis.MisService;
import cloudtp.com.mis.TaskDAO;
import cloudtp.com.user.UserInfo;

public class PlanAction  extends ActionSupport{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5296853850022895397L;
	private int _type;
	private String _id;
	private String _name;
	private String _descr;
	private String _ownerId;	
	private String _ownerName;
	private String _executors = "";
	private String _solutions = "";
	private String _planInfo = "";
	private String _resultPage ="";
	private String _createDate ="今天";
	private String _execDate ="";
	private boolean _isSchedule = false;
	private List<PlanNode> _list;
	private List<ExecuteRecord> _recordList;
	private int _resultCode;
	
	public String list()
	{
		UserInfo userInfo = (UserInfo)ServletActionContext.getRequest().getSession().getAttribute(Constants.UserInfo);
		_list = MisService.QueryPlans(userInfo.getUserId(), _type, _id);
		return SUCCESS;
	}
	
	public String save()
	{
		UserInfo userInfo = (UserInfo)ServletActionContext.getRequest().getSession().getAttribute(Constants.UserInfo);
		StringBuilder sbResult = new StringBuilder();
		Calendar execDate = Calendar.getInstance();
		try
		{
			String[] list = _execDate.split(" ");
			String[] part1 = list[0].split("-");
			String[] part2 = list[1].split(":");
			execDate.set(Calendar.YEAR, Integer.parseInt(part1[0]));
			execDate.set(Calendar.MONTH, Integer.parseInt(part1[1]));
			execDate.set(Calendar.DAY_OF_MONTH, Integer.parseInt(part1[2]));
			execDate.set(Calendar.HOUR_OF_DAY, Integer.parseInt(part2[0]));
			execDate.set(Calendar.MINUTE, Integer.parseInt(part2[1]));
			if(part2.length>2)
				execDate.set(Calendar.SECOND, Integer.parseInt(part2[2]));
		}
		catch(Exception e)
		{
			if(_isSchedule)
			{
				_planInfo = "<span class=error>保存失败! 执行日期格式不正确.</span>";
				return SUCCESS;
			}
		}
		if(!TaskDAO.SavePlan(userInfo.getUserId(), _id, _name, _descr, execDate, _isSchedule, _executors, _solutions, sbResult))
		{
			_planInfo = String.format("<span class=error>%s</span>", sbResult.toString());
		}
		else
		{
			_id = sbResult.toString();
			String[] planInfo = TaskDAO.GetPlan( Integer.parseInt(_id));
			_ownerId = planInfo[3];
			_ownerName = planInfo[4];
			_createDate = planInfo[7];
			_planInfo = String.format("<span class=success>%s</span><script type='text/javascript' language='javascript'>dataChanged=false;</script>", "保存成功");
			
		}
		return SUCCESS;
	}
	
	public String execute()
	{
		UserInfo userInfo = (UserInfo)ServletActionContext.getRequest().getSession().getAttribute(Constants.UserInfo);
		StringBuilder sbResult = new StringBuilder();
		Calendar execDate = Calendar.getInstance();
		try
		{
			String[] list = _execDate.split(" ");
			String[] part1 = list[0].split("-");
			String[] part2 = list[1].split(":");
			execDate.set(Calendar.YEAR, Integer.parseInt(part1[0]));
			execDate.set(Calendar.MONTH, Integer.parseInt(part1[1]));
			execDate.set(Calendar.DAY_OF_MONTH, Integer.parseInt(part1[2]));
			execDate.set(Calendar.HOUR_OF_DAY, Integer.parseInt(part2[0]));
			execDate.set(Calendar.MINUTE, Integer.parseInt(part2[1]));
			if(part2.length>2)
				execDate.set(Calendar.SECOND, Integer.parseInt(part2[2]));
		}
		catch(Exception e)
		{
			if(_isSchedule)
			{
				_planInfo = "创建执行任务失败： 执行日期格式不正确";
				_resultCode = 0;
				return SUCCESS;
			}
		}
		if(!Execution.CreateExecution(Integer.parseInt(_id), _isSchedule, execDate, userInfo.getUserId(), sbResult))
		{
			_resultCode = 0;
			_planInfo = sbResult.toString();
		}
		else
		{
			_resultCode = 1;
		}
		
		return SUCCESS;
	}	
	
	public String saveSolution()
	{
		if(!TaskDAO.SavePlanSolution( Integer.parseInt(_id), _solutions))
		{
			_planInfo = String.format("<span class=error>%s</span>", "保存失败,请联系系统管理员");
		}
		else
		{			
			String[] planInfo = TaskDAO.GetPlan( Integer.parseInt(_id));
			_ownerId = planInfo[3];
			_ownerName = planInfo[4];
			_planInfo = String.format("<span class=success>%s</span>", "保存成功");
			
		}
		return "execution";
	}
	
	public String delete()
	{
		UserInfo userInfo = (UserInfo)ServletActionContext.getRequest().getSession().getAttribute(Constants.UserInfo);
		StringBuilder sbResult = new StringBuilder();
		if(!TaskDAO.RemovePlan(userInfo.getUserId(), Integer.parseInt(_id), sbResult))
		{
			_planInfo = String.format("<span class=error>%s</span>", sbResult.toString());
		}
		else
		{
			_id = "";
			_name = "";
			_descr = "";
			_planInfo = String.format("<span class=success>%s</span>", "删除成功");
			_executors = "";
			
		}
		return SUCCESS;
	}
	
	public String open()
	{
		//UserInfo userInfo = (UserInfo)ServletActionContext.getRequest().getSession().getAttribute(Constants.UserInfo);
		String[] planInfo = TaskDAO.GetPlan( Integer.parseInt(_id));
		if(planInfo==null)
		{
			_planInfo = String.format("<span class=error>%s</span>", "计划不存在!");
			return "plandummy";
		}
		_name = planInfo[1];
		_descr = planInfo[2];
		_ownerId = planInfo[3];
		_ownerName = planInfo[4];
		_isSchedule = "1".equals(planInfo[5]);
		_execDate = planInfo[6];
		_createDate = planInfo[7];
		String[][] list = TaskDAO.GetPlanExecutors(Integer.parseInt(_id));
		
		if(list!=null)
		{
			_executors = list[0][0] +"/" + list[0][1];
			for(int i=1;i<list.length;i++)
			{
				_executors += "," + list[i][0] +"/" + list[i][1];
			}
		}
		else
		{
			_executors = "";
		}
		list = TaskDAO.GetPlanSolutions(Integer.parseInt(_id));
		
		if(list!=null)
		{
			_solutions = list[0][0] +"/" + list[0][1] +"/";
			if(list[0][3]!=null && list[0][3].length()>0)
				_solutions += list[0][3];
			else
				_solutions += list[0][4];
			_solutions += "/" + list[0][2];
			for(int i=1;i<list.length;i++)
			{
				_solutions += "," + list[i][0] +"/" + list[i][1] +"/";
				if(list[i][3]!=null && list[i][3].length()>0)
					_solutions += list[i][3];
				else
					_solutions += list[i][4];
				_solutions += "/" + list[i][2];
			}
		}
		else
		{
			_solutions = "";
		}
		
		if(!"".equals(_resultPage) && _resultPage.length()>0)
			return _resultPage;
		return SUCCESS;
	}
	
	public String queryExecuteRecord()
	{
		_recordList = Execution.QueryExecuteRecord(Integer.parseInt(_id));
		return SUCCESS;
	}
	
	public void setType(int type)
	{
		_type = type;
	}
	
	public void setPlanId(String id)
	{
		_id = id;
	}
	
	public void setResultPage(String page)
	{
		_resultPage = page;
	}
	
	public void setPlanName(String name)
	{
		_name = name;
	}
	
	public void setPlanDescr(String descr)
	{
		_descr = descr;
	}
	
	public void setExecutors(String executors)
	{
		_executors = executors;
	}
	
	public void setExecDate(String date)
	{
		_execDate = date;
	}
	
	public String getExecDate()
	{
		return _execDate;
		/*
		if(_execDate!=null)
		{
			return  String.format("%d-%d-%d %d:%d:%d", 
						_execDate.get(Calendar.YEAR), 
						_execDate.get(Calendar.MONTH), 
						_execDate.get(Calendar.DAY_OF_MONTH),
						_execDate.get(Calendar.HOUR),
						_execDate.get(Calendar.MINUTE),
						_execDate.get(Calendar.SECOND));
		}*/
	}
	
	public void setIsSchedule(String checked)
	{
		if(checked!=null && "on".equalsIgnoreCase(checked))
		{
			_isSchedule = true;
		}
		else
			_isSchedule = false;		
	}
	
	public String getIsSchedule()
	{
		return _isSchedule?" checked":"";		
	}
	
	public void setSolutions(String solutions)
	{
		_solutions = solutions;
	}
	
	public List<PlanNode> getList()
	{
		return _list;
	}
	
	public List<ExecuteRecord> getExecList()
	{
		return _recordList;
	}
	
	public String getPlanInfo()
	{
		return _planInfo;
	}
	
	public int getResultCode()
	{
		return _resultCode;
	}
	public String getName()
	{
		return _name;
	}
	public String getOwnerId()
	{
		return _ownerId;
	}
	public String getOwnerName()
	{
		return _ownerName;
	}
	
	public String getId()
	{
		return _id;
	}
	
	public String getDescr()
	{
		return _descr;
	}
	
	public String getExecutors()
	{
		return _executors;
	}	
	
	public String getSolutions()
	{
		return _solutions;
	}
	
	public String getCreateDate()
	{
		return _createDate;
	}
	
}
