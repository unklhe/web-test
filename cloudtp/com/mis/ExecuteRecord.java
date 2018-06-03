package cloudtp.com.mis;

public class ExecuteRecord {
	
	private String _execId;
	private String _startDate;
	private boolean _isSchedule;
	private String _scheduleDate;
	private String _execDate;
	private String _endDate;
	private String _ownerName;
	private String _status;
	
	
	public ExecuteRecord(String execId, String startDate, boolean isSchedule,String  scheduleDate, String execDate, String endDate, String ownerName, String status)
	{
		_execId =  execId; 
		_startDate =  startDate;
		_isSchedule =  isSchedule;
		_scheduleDate =  scheduleDate;
		_execDate =  execDate;
		_endDate =  endDate;
		
		_ownerName =  ownerName;
		_status = status ;
	}
	
	public String getExecId()
	{
		return _execId;
	}
	
	public String getStartDate()
	{
		return _startDate;
	}
	
	public String getSchedule()
	{
		if(!_isSchedule)
			return "";
		else
			return _scheduleDate;
	}
	
	public String getExecDate()
	{
		return _execDate;
	}
	
	public String getEndDate()
	{
		return _endDate;
	}
	
	public String getOwnerName()
	{
		return _ownerName;
	}
	
	public String getStatus()
	{
		return _status;
	}
	
}
