package cloudtp.com.mis;

public class ExecLogNode {

	 private String _logId;
     private String _sluName;
     private String _owner;
     private String _result;
     private String _startTime;
     private String _execTime;

     public ExecLogNode(String logid, String sluName, String owner, int total, int failed, String startTime, String execTime)
     {
        _logId = logid;
        _sluName = sluName;
        _owner = owner;
        _result = String.format("共计执行了 %1$d 个用例，失败了 %1$d 个", total, failed);
        _startTime = startTime;
        _execTime = execTime;
     }

     public String getLogId()
     {
        return _logId;
     }


     public String getSolutionName()
     {
        return _sluName;        
     }

     public String getOwner()
     {
        return _owner;
     }

     public String getResult()
     {
        return _result;
     }

     public String getStartTime()
     {
        return _startTime;//.ToString("yyyy-MM-dd HH:mm:ss");
       }

     public String getExecTime()
     {
        return _execTime;//.ToString("yyyy-MM-dd HH:mm:ss");        
     }
}
