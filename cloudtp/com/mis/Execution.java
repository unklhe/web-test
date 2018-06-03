package cloudtp.com.mis;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.jms.JMSException;

import cloudtp.com.Guid;
import cloudtp.com.LoggerFactory;
import cloudtp.com.TaskSender;
import cloudtp.com.dao.DataAccessObject;


public class Execution {
		 
	public static boolean CreateExecution(int planId, boolean isSchedule, Calendar scheduleDate, String creator, StringBuilder sbResult)
	{
		String guid = (new Guid()).toString();
		Connection conn = DataAccessObject.getConnection();
		try
		{
			conn.setAutoCommit(false);
			Statement st = conn.createStatement();
			
			st.addBatch( String.format("insert into taskexecutions(taskid,guid,startdate,isschedule,scheduledate,status, ownerid) values(%d,'%s',sysdate,'%d',%s,'1','%s')",
							planId, guid, isSchedule?1:0,
							scheduleDate==null?"null":String.format("to_date('%s', 'yyyy-mm-dd hh24:mi:ss')", 
	                   		  String.format("%d-%d-%d %d:%d:%d", 
	                   				scheduleDate.get(Calendar.YEAR), 
	                   				scheduleDate.get(Calendar.MONTH), 
	                   				scheduleDate.get(Calendar.DAY_OF_MONTH),
	                   				scheduleDate.get(Calendar.HOUR_OF_DAY),
	                   				scheduleDate.get(Calendar.MINUTE),
	                   				scheduleDate.get(Calendar.SECOND))), 
	                   				creator) );
			st.addBatch( String.format("insert into ExecuteSolutions(executionid, sluid, sortno) (select '%s',b.sluid,b.sortno from tasksolutions b where b.taskid=%d)",
					guid, planId));
			st.executeBatch();
			String msgId = (new Guid()).toString();
			String msg = String.format("<TaskReq><guid>%1$s</guid><TaskInfo><TaskId>%2$s</TaskId><Isschedule>%3$d</Isschedule><ExecuteDate>%4$s</ExecuteDate></TaskInfo></TaskReq>",
								msgId,
								guid, isSchedule?1:0,
					 			String.format("%d-%d-%d %d:%d:%d", 
                				scheduleDate.get(Calendar.YEAR), 
                				scheduleDate.get(Calendar.MONTH), 
                				scheduleDate.get(Calendar.DAY_OF_MONTH),
                				scheduleDate.get(Calendar.HOUR_OF_DAY),
                				scheduleDate.get(Calendar.MINUTE),
                				scheduleDate.get(Calendar.SECOND))); 
			
			if(!TaskSender.send(msg, msgId))
			{
				conn.rollback();
				sbResult.append("创建执行任务失败，请联系系统管理员!");
				return false;
			}
			TaskSender.sendOk(msgId);
			conn.commit();
			sbResult.append(guid);
			return true;
		}
		catch(SQLException e)
		{
			try
			{
				conn.rollback();
			}
			catch(SQLException e1){}
			LoggerFactory.Write(e);
			sbResult.append("创建执行任务失败，请联系系统管理员!");
		}
		catch (JMSException e) {
			try
			{
				conn.rollback();
			}
			catch(SQLException e1){}
			LoggerFactory.Write(e);
			sbResult.append("创建执行任务失败，请联系系统管理员!");
		}
		
		return false;
	}
	
	
	public static List<ExecuteRecord> QueryExecuteRecord(int planId)
	{
		ArrayList<ExecuteRecord> resultList = new ArrayList<ExecuteRecord>();
		String sql = String.format("select guid,to_char(startdate,'yyyy-mm-dd hh24:mi:ss'),isSchedule, to_char(scheduledate,'yyyy-mm-dd hh24:mi:ss'), to_char(executedate,'yyyy-mm-dd hh24:mi:ss'), to_char(enddate,'yyyy-mm-dd hh24:mi:ss'), ownerid,status, b.name from taskexecutions a,users b where a.taskid='%s' and a.ownerid=b.id", planId);
		String[][] list = DataAccessObject.GetArray(sql);
		if (list != null)
		{
			if (list != null)
	        {
	           for (String[] execInfo : list)
	           {
	              resultList.add(new ExecuteRecord(execInfo[0], execInfo[1], "1".equals(execInfo[2]), execInfo[3], execInfo[4], execInfo[5], (execInfo[6]!=null && !"".equals(execInfo[8]))?execInfo[8]:execInfo[6], execInfo[7]));
	           }
	        }
		}
	     return resultList;
	}
}

