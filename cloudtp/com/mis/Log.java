package cloudtp.com.mis;

import java.util.ArrayList;
import java.util.List;

import cloudtp.com.LoggerFactory;
import cloudtp.com.dao.DataAccessObject;

public class Log {

	public static List<ExecLogNode> GetSolutionList(String execId)
    {
       try
       {
    	   String sql = String.format("select (select guid from execlogs f where f.execid=b.guid and f.sluid=e.guid), e.name,c.name,nvl( (select total from execlogs f where f.execid=b.guid and f.sluid=e.guid), 0)," +
       			"nvl( (select failed from execlogs f where f.execid=b.guid and f.sluid=e.guid), 0)," +
       			"to_char(b.startdate,'yyyy-mm-dd hh24:mi:ss')," +
       			"to_char(b.executedate,'yyyy-mm-dd hh24:mi:ss') " + 
       			"from taskexecutions b,users c,executesolutions d,solutions e " +
       			"where b.ownerid=c.id and b.guid=d.executionid and d.sluid=e.guid and b.guid='%1$s' order by b.startdate, d.sortno", execId);
    	   String[][] list = DataAccessObject.GetArray(sql);          
          if (list!=null)
          {
        	  ArrayList<ExecLogNode> logList = new ArrayList<ExecLogNode>();
             for (int i = 0; i < list.length; i++)
             {
                logList.add(new ExecLogNode(list[i][0], list[i][1], list[i][2], Integer.parseInt(list[i][3]), Integer.parseInt(list[i][4]), list[i][5], list[i][6]));
             }
             return logList;
          }
          
       }
       catch(Exception e)
       {
    	   LoggerFactory.Write(e);
       }
       return null;
    }	
	
	 public static String[][] GetLogTips(String logId)
     {
        String sql = String.format("select sequenceId,fmt,type,log from execLogs_tip where logid='%1$s' order by sequenceId", logId);
        return DataAccessObject.GetArray(sql);
     }

     public static String[][] GetLogCaseDetail(String logId, int seqId, boolean filterNormal)
     {
        String sql = String.format("select b.fmt,b.type,b.log from execLogs_case a, execLogs_case_detail b where a.guid=b.parentId and a.logid='%1$s' and a.sequenceId=%2$d %3$s order by b.sequenceId", logId, seqId, filterNormal ? " and b.type!=1" : "");
        return DataAccessObject.GetArray(sql);
     }
	 
}
