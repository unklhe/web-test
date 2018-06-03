package cloudtp.com.mis;

import java.sql.SQLException;
import java.util.Calendar;

import cloudtp.com.LoggerFactory;
import cloudtp.com.dao.DataAccessObject;

public class TaskDAO {
		 

	      public static String[] GetOwnerYearList(String userId)
	      {
	         String sql = String.format("select unique to_char(createtime,'yyyy') as year from tasks where ownerId='%s' order by year desc", userId);
	         return DataAccessObject.GetList(sql);
	      }

	      public static String[] GetScheduleYearList(String userId)
	      {
	         String sql = String.format("select unique to_char(a.createtime,'yyyy') as year from tasks a,TaskExecutors b where a.id=b.taskid and b.userid='%s' order by year desc",
	                  userId);
	         return DataAccessObject.GetList(sql);
	      }

	      public static String[] GetFinishedYearList(String userId)
	      {
	         String sql = String.format("select unique to_char(a.createtime,'yyyy') as year from tasks a,TaskExecutors b,solutions c,execlogs d where a.id=b.taskid and b.userid='%1$s' and a.id=c.taskid and c.guid=d.sluid and c.ownerid='%1$s' order by year desc",
	                  userId);
	         return DataAccessObject.GetList(sql);
	      }

	      public static String[] GetOwnerMonthList(String userId, String year)
	      {
	         String sql = String.format("select unique to_char(createtime,'yyyy-mm') as month from tasks where ownerId='%1$s' and createtime>=to_date('%2$s-01-01 00:00:00','yyyy-mm-dd hh24:mi:ss') and createtime<to_date('%3$d-01-01 00:00:00','yyyy-mm-dd hh24:mi:ss') order by month desc",
	                  userId, year, Integer.parseInt(year) + 1);
	         return DataAccessObject.GetList(sql);
	      }

	      public static String[] GetScheduleMonthList(String userId, String year)
	      {
	         String sql = String.format("select unique to_char(a.createtime,'yyyy-mm') as month from tasks a,TaskExecutors b where a.id=b.taskid and b.userid='%1$s' and a.createtime>=to_date('%2$s-01-01 00:00:00','yyyy-mm-dd hh24:mi:ss') and a.createtime<to_date('%3$d-01-01 00:00:00','yyyy-mm-dd hh24:mi:ss') order by month desc",
	                  userId, year, Integer.parseInt(year) + 1);
	         return DataAccessObject.GetList(sql);
	      }

	      public static String[] GetFinishedMonthList(String userId, String year)
	      {
	         String sql = String.format("select unique to_char(a.createtime,'yyyy-mm') as month from tasks a,TaskExecutors b,solutions c,execlogs d where a.id=b.taskid and b.userid='%1$s' and a.id=c.taskid and c.guid=d.sluid and c.ownerid='%1$s' and a.createtime>=to_date('%2$s-01-01 00:00:00','yyyy-mm-dd hh24:mi:ss') and a.createtime<to_date('%3$d-01-01 00:00:00','yyyy-mm-dd hh24:mi:ss') order by month desc",
	                  userId, year, Integer.parseInt(year) + 1);
	         return DataAccessObject.GetList(sql);
	      }

	      public static String[][] GetOwnerTaskList(String userId, String month)
	      {
	    	  Calendar month1 = Calendar.getInstance();
	         month1.set(Calendar.YEAR, Integer.parseInt(month.substring(0, 4)));
	         month1.set(Calendar.MONTH, Integer.parseInt(month.substring(5, 7)));
	         month1.set(Calendar.DAY_OF_MONTH, 1);
	         Calendar month2 = (Calendar)month1.clone();
	         month2.add(Calendar.MONTH, 1);
	         String sql = String.format("select id,name from tasks where ownerid='%1$s' and createtime>=to_date('%2$s-01 00:00:00','yyyy-mm-dd hh24:mi:ss') and createtime<to_date('%3$s-01 00:00:00','yyyy-mm-dd hh24:mi:ss')",
	                     userId, 
	                     String.format("%d-%02d", month1.get(Calendar.YEAR), month1.get(Calendar.MONTH)),
	                     String.format("%d-%02d", month2.get(Calendar.YEAR), month2.get(Calendar.MONTH)));
	         return DataAccessObject.GetArray(sql);
	      }

	      public static String[][] GetScheduleTaskList(String userId, String month)
	      {
	         Calendar month1 = Calendar.getInstance();
	         month1.set(Calendar.YEAR, Integer.parseInt(month.substring(0, 4)));
	         month1.set(Calendar.MONTH, Integer.parseInt(month.substring(5, 7)));
	         month1.set(Calendar.DAY_OF_MONTH, 1);
	         Calendar month2 = (Calendar)month1.clone();
	         month2.add(Calendar.MONTH, 1);
	         String sql = String.format("select unique a.id,a.name from tasks a,TaskExecutors b where a.id=b.taskid and b.userid='%1$s' and a.createtime>=to_date('%2$s-01 00:00:00','yyyy-mm-dd hh24:mi:ss') and a.createtime<to_date('%3$s-01 00:00:00','yyyy-mm-dd hh24:mi:ss')",
	                     userId, 
	                     String.format("%d-%02d", month1.get(Calendar.YEAR), month1.get(Calendar.MONTH)),
	                     String.format("%d-%02d", month2.get(Calendar.YEAR), month2.get(Calendar.MONTH)));
	         return DataAccessObject.GetArray(sql);
	      }

	      public static String[][] GetFinishedTaskList(String userId, String month)
	      {
	    	  Calendar month1 = Calendar.getInstance();
		         month1.set(Calendar.YEAR, Integer.parseInt(month.substring(0, 4)));
		         month1.set(Calendar.MONTH, Integer.parseInt(month.substring(5, 7)));
		         month1.set(Calendar.DAY_OF_MONTH, 1);
		         Calendar month2 = (Calendar)month1.clone();
		         month2.add(Calendar.MONTH, 1);
	         String sql = String.format("select unique a.id,a.name from tasks a,TaskExecutors b,solutions c,execlogs d where a.id=b.taskid and b.userid='%1$s' and a.createtime>=to_date('%2$s-01 00:00:00','yyyy-mm-dd hh24:mi:ss') and a.createtime<to_date('%3$s-01 00:00:00','yyyy-mm-dd hh24:mi:ss') and a.id=c.taskid and c.guid=d.sluId and c.ownerid='%1$s'",
	                     userId,
	                     String.format("%d-%02d", month1.get(Calendar.YEAR), month1.get(Calendar.MONTH)),
	                     String.format("%d-%02d", month2.get(Calendar.YEAR), month2.get(Calendar.MONTH)));
	         return DataAccessObject.GetArray(sql);
	      }

	      public static String[][] GetSolutionList(int taskId)
	      {
	         String sql = String.format("select guid,name from solutions where taskid=%d", taskId);
	         return DataAccessObject.GetArray(sql);
	      }

	      public static String[][] GetLogList(int taskId)
	      {
	         String sql = String.format("select a.guid,c.name,b.name,nvl(total,0),nvl(failed,0),to_char(starttime,'yyyy-mm-dd hh24:mi:ss') from execLogs a,users b,solutions c where a.sluid=c.guid and c.ownerid=b.id and c.taskid=%d", taskId);
	         return DataAccessObject.GetArray(sql);
	      }

	      public static String[][] GetExecutionInfo(String execId)
	      {
	         String sql = String.format("select to_char(starttime,'yyyy-mm-dd hh24:mi:ss'),total, failed from execLogs where guid='%s'", execId);
	         return DataAccessObject.GetArray(sql);
	      }

	      public static String[][] GetLogTips(String execId)
	      {
	         String sql = String.format("select sequenceId,fmt,type,log from execLogs_tip where execId='%s' order by sequenceId", execId);
	         return DataAccessObject.GetArray(sql);
	      }

	      public static String[][] GetLogCaseDetail(String execId, int seqId, boolean filterNormal)
	      {
	         String sql = String.format("select b.fmt,b.type,b.log from execLogs_case a, execLogs_case_detail b where a.guid=b.parentId and a.execid='%1$s' and a.sequenceId=%2$d %3$s order by b.sequenceId", execId, seqId, filterNormal ? " and b.type!=1" : "");
	         return DataAccessObject.GetArray(sql);
	      }
	     
	      public static int GenerateTaskId()
	      {
	         String[][] parameters = new String[2][] ;
	         parameters[0] = new String[]{ "in", "num", "entity", "2" };
	         parameters[1] = new String[]{ "out", "num", "entityId", "0" };
	         try {
				DataAccessObject.ExecuteProcedure("generateEntityId", parameters);
			} catch (SQLException e) {
				LoggerFactory.Write(e);
			}
	         int taskId = Integer.parseInt(parameters[1][3]);
	         return taskId;
	      }
	      
	      public static boolean SavePlan(String ownerId, String planId, String planName, String descr, Calendar execDate, boolean isSchedule, String executors, String solutions, StringBuilder sbResult)
	      {
	    	  if(null!=planId && planId.length()>0)
	    	  {
	    		  String sql = String.format("select id from tasks where id!=%s and name='%s' and ownerid='%s'", planId, planName.replace("'","''"), ownerId);
	    		  String id = DataAccessObject.GetString(sql);
	    		  if(null!=id && id.length()>0)
	    		  {	    			  
	    			  sbResult.append("保存失败：修改后的计划名称与数据库中已有记录重复!");
	    			  return false;
	    		  }
	    		  if(!UpdatePlan(Integer.parseInt(planId), planName, descr, execDate, isSchedule, executors, solutions))
	    		  {
	    			  sbResult.append("保存失败,请联系系统管理员.");
	    			  return false;	    		  
	    		  }
	    		  sbResult.append(planId);
	    	  }
	    	  else
	    	  {
	    		  String sql = String.format("select id from tasks where name='%s' and ownerid='%s'", planName.replace("'","''"), ownerId);
	    		  String id = DataAccessObject.GetString(sql);
	    		  if(null!=id && id.length()>0)
	    		  {
	    			  sbResult.append("保存失败：新计划的名称与数据库中已有记录重复!");
	    			  return false;
	    		  }
	    		  int[] newId = new int[1];
	    		  if(SavePlan(ownerId, planName, descr, execDate, isSchedule, executors, solutions, newId)) 
	    		  {
	    			  sbResult.append(String.valueOf(newId[0]));	    			  
	    		  }
	    		  else
	    		  {
	    			  sbResult.append("保存失败,请联系系统管理员.");
	    			  return false;
	    		  }
	    	  }
	    	  return true;
	      }
	      
	      public static boolean SavePlan(String ownerId, String planName, String descr, Calendar execDate, boolean isSchedule,String executors, String solutions, int[] id)
	      {
	         int sqlCount = 2;
	         String[] executorList = null;
	         if (executors != null && executors.length()>0)
	         {
	        	 executorList = executors.split(",");
	        	 sqlCount += executorList.length;
	         }
	         String[] solutionList = null;
	         if (solutions != null && solutions.length()>0)
	         {
	        	 solutionList = solutions.split(",");
	        	 sqlCount += solutionList.length;
	         }
	         String[] sqlList = new String[sqlCount];
	         int newId = GenerateTaskId();
	         sqlList[0] = String.format("insert into tasks(id,name,descr,createtime,scheduletime,isschedule, ownerid) values(%d,'%s','%s',sysdate,%s, '%s','%s')",
	                      newId, planName.replace("'", "''"), descr.replace("'", "''"), 
	                      execDate==null?"null":String.format("to_date('%s', 'yyyy-mm-dd hh24:mi:ss')", 
	                    		  String.format("%d-%d-%d %d:%d:%d", 
	                    				  execDate.get(Calendar.YEAR), 
	                    				  execDate.get(Calendar.MONTH), 
	                    				  execDate.get(Calendar.DAY_OF_MONTH),
	                    				  execDate.get(Calendar.HOUR_OF_DAY),
	                    				  execDate.get(Calendar.MINUTE),
	                    				  execDate.get(Calendar.SECOND))), 
	                      isSchedule?"1":"0", ownerId);
	         sqlList[1] = String.format("insert into TaskExecutors(taskid,userid) values(%d,'%s')", newId, ownerId);
	         sqlCount = 2;
	         if (executorList!=null)
	         {
	            for (int i = 0; i < executorList.length; i++)
	            {
	               sqlList[sqlCount] = String.format("insert into TaskExecutors(taskid,userid) values(%d,'%s')", newId, executorList[i].split("/")[0]);
	               sqlCount ++;
	            }
	         }
	         if (solutionList!=null)
	         {
	            for (int i = 0; i < solutionList.length; i++)
	            {
	               sqlList[sqlCount] = String.format("insert into TaskSolutions(taskid,sluid, sortno) values(%d,'%s', %d)", newId, solutionList[i].split("/")[0], i+1);
	               sqlCount ++;
	            }
	         }
	         try {
				DataAccessObject.ExecuteSQL(sqlList);
				id[0] = newId;
				return true;
			} catch (SQLException e) {
				LoggerFactory.Write(e);
			}
	         return false;
	      }

	      public static boolean UpdatePlan(int planId, String taskName, String descr,Calendar execDate, boolean isSchedule,String executors, String solutions)
	      {
	         int sqlCount = 4;
	         String[] executorList = null;
	         if (executors != null && executors.length()>0)
	         {
	        	 executorList = executors.split(",");
	        	 sqlCount += executorList.length;
	         }
	         String[] solutionList = null;
	         if (solutions != null && solutions.length()>0)
	         {
	        	 solutionList = solutions.split(",");
	        	 sqlCount += solutionList.length;
	         }
	         String[] sqlList = new String[sqlCount];
	         sqlList[0] = String.format("delete from TaskExecutors where taskid=%1$d", planId);
	         sqlList[1] = String.format("delete from TaskSolutions where taskid=%1$d", planId);
	         sqlList[2] = String.format("update tasks set name='%s', descr='%s',scheduletime=%s,isschedule='%s' where id=%d", 
	        		 taskName.replace("'", "''"), descr.replace("'", "''"),
	        		 execDate==null?"null":String.format("to_date('%s', 'yyyy-mm-dd hh24:mi:ss')", 
                   		  String.format("%d-%d-%d %d:%d:%d", 
                   				  execDate.get(Calendar.YEAR), 
                   				  execDate.get(Calendar.MONTH), 
                   				  execDate.get(Calendar.DAY_OF_MONTH),
                   				  execDate.get(Calendar.HOUR_OF_DAY),
                   				  execDate.get(Calendar.MINUTE),
                   				  execDate.get(Calendar.SECOND))), 
                   	isSchedule?"1":"0", 
	        		 planId);
	         sqlList[3] = String.format("insert into TaskExecutors(taskid,userid) values(%1$d,(select ownerid from tasks where id=%1$d))", planId );
	         sqlCount = 4;
	         if (executorList != null)
	         {
	            for (int i = 0; i < executorList.length; i++)
	            {
	               sqlList[sqlCount] = String.format("insert into TaskExecutors(taskid,userid) values(%d,'%s')", planId, executorList[i].split("/")[0]);
	               sqlCount ++;
	            }
	         }
	         if (solutionList!=null)
	         {
	            for (int i = 0; i < solutionList.length; i++)
	            {
	               sqlList[sqlCount] = String.format("insert into TaskSolutions(taskid,sluid, sortno) values(%d,'%s', %d)", planId, solutionList[i].split("/")[0], i+1);
	               sqlCount ++;
	            }
	         }
	         try {
				DataAccessObject.ExecuteSQL(sqlList);
				return true;
			} catch (SQLException e) {
				LoggerFactory.Write(e);
			}
	         return false;
	      }
	      
	      public static boolean SavePlanSolution(int planId, String solutions)
	      {
	         int sqlCount = 1;
	         String[] solutionList = null;
	         if (solutions != null && solutions.length()>0)
	         {
	        	 solutionList = solutions.split(",");
	        	 sqlCount += solutionList.length;
	         }
	         String[] sqlList = new String[sqlCount];
	         sqlList[0] = String.format("delete from TaskSolutions where taskid=%1$d", planId);
	         if (solutionList!=null)
	         {
	            for (int i = 0; i < solutionList.length; i++)
	            {
	               sqlList[i+1] = String.format("insert into TaskSolutions(taskid,sluid, sortno) values(%d,'%s', %d)", planId, solutionList[i].split("/")[0], i+1);
	            }
	         }
	         try {
				DataAccessObject.ExecuteSQL(sqlList);
				return true;
			} catch (SQLException e) {
				LoggerFactory.Write(e);
			}
	         return false;
	      }
	      
	      
	      public static String[] GetPlan(int planId)
	      {
	        String sql = String.format("select a.id,a.name,a.descr,a.ownerId, b.name,a.isschedule,to_char(a.scheduletime,'yyyy-mm-dd hh24:mi:ss'),to_char(a.createtime,'yyyy-mm-dd') from tasks a,users b where a.id=%d and a.ownerid=b.id", planId);
	        return DataAccessObject.GetSingleRowValue(sql);
	      }
	      
	      public static boolean RemovePlan(String ownerId, int planId, StringBuilder sbResult)
	      {
	    	  String sql  = String.format("select ownerId from tasks where id=%d", planId);
	    	  String trueOwnerId = DataAccessObject.GetString(sql);	    	  
	    	  if(null==trueOwnerId || trueOwnerId.length()<1)
	    	  {
	    		  sbResult.append("删除执行计划失败：计划不存在!");
	    		  return false;
	    	  }
	    	  else if(!trueOwnerId.equals(ownerId))
	    	  {
	    		  sbResult.append("删除计划失败：不能删除别人创建的计划!");
	    		  return false;
	    	  }
	    	  
	    	  sql  = String.format("select guid from TaskExecutions where taskId=%d", planId);
	    	  int execCount = DataAccessObject.GetCount(sql);	    	  
	    	  if(execCount>0)
	    	  {
	    		  sbResult.append("删除计划失败：计划已经被执行,不能删除!");
	    		  return false;
	    	  }
	    	 
	    	  try
	    	  {			    	  
		    	  String[] sqlList = new String[3];
		    	  sqlList[0] = String.format("delete from TaskExecutors where taskId=%d", planId);
		    	  sqlList[1] = String.format("delete from TaskSolutions where taskId=%d", planId);
		    	  sqlList[2] = String.format("delete from tasks where id=%d", planId);
		    	  DataAccessObject.ExecuteSQL(sqlList);
		    	  return true;
	    	  }
	    	  catch(SQLException ex)
	    	  {
	    		  LoggerFactory.Write(ex);
	    		  sbResult.append("删除计划失败：请联系系统管理员!");
	    	  }
	    	  return false;
	      }
	      public static String[][] GetPlanExecutors(int planId)
	      {
	    	  String sql = String.format("select a.userid, b.name from TaskExecutors a, users b,tasks c where a.userid=b.id and c.id=%d and a.taskid=c.id and a.userid!=c.ownerid", planId);
		      return DataAccessObject.GetArray(sql);	    	  
	      }
	      
	      public static String[][] GetPlanSolutions(int planId)
	      {
	    	  String sql = String.format("select a.sluid,b.name,to_char( b.createtime, 'yyyy-mm-dd'), c.name,b.ownerid from TaskSolutions a,solutions b, users c where a.sluid=b.guid and b.ownerid=c.id(+) and a.taskid=%d order by sortno", planId);
		      return DataAccessObject.GetArray(sql);	    	  
	      }
	   }

