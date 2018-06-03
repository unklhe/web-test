package cloudtp.com.cloud;

import java.io.*;
import java.sql.*;
import java.util.*;

import cloudtp.com.Guid;
import cloudtp.com.LoggerFactory;
import cloudtp.com.dao.DataAccessObject;

/**
 * 云计算的业务逻辑中数据库层处理类
 * 
 * @author Hetao
 * 
 */
public class CloudDAO {

	/**
	 * 从数据库中获取工具/工具集列表
	 * 
	 * @param type
	 *            请求类型, 工具集/工具
	 * @param id
	 *            父节点编号
	 * @return 存放了工具或工具集的二维数组
	 */
	public static String[][] getTools(int type, String id) {
		String sql = String
				.format("select category,id,name,icon,'' from cloud.toolset where parentid=%1$d",
						id);
		String[][] result = DataAccessObject.GetArray(sql);
		String hasChild = "true";
		if (type == 2)
			hasChild = "false";
		if (result != null) {
			for (int i = 0; i < result.length; i++) {
				result[i][4] = hasChild;
			}
		}
		return result;
	}

	/**
	 * 从数据库中获取云功能树的节点
	 * 
	 * @param type
	 *            节点类型
	 * @param id
	 *            父节点编号
	 * @return 存放了节点的二维数组
	 */
	public static String[][] getNodes(int type, String id) {
		
		String hasChild = "true";
		if (type == 2 )
			hasChild = "false";
		String sql ;
		if(type==4)
		{			
			sql = "select distinct 101, to_char(createtime, 'yyyy') as year, to_char(createtime, 'yyyy') || '年','folder.icon.png','', '' from cloud.policy where type=1 order by year desc";
		}
		else if(type==101)
		{			
			sql = String.format("select distinct 102, to_char(createtime, 'yyyy-mm') as month, to_char(createtime, 'mm') || '月','folder.icon.png','', '' from cloud.policy where type=1 and createtime>=to_date('%1$s-01-01 00:00:00','yyyy-mm-dd hh24:mi:ss') and createtime<to_date('%2$d-01-01 00:00:00','yyyy-mm-dd hh24:mi:ss') order by month desc", id, Integer.parseInt(id)+1);			
		}
		else if(type==102)
		{
			hasChild = "false";
			 Calendar month1 = Calendar.getInstance();
	         month1.set(Calendar.YEAR, Integer.parseInt(id.substring(0, 4)));
	         month1.set(Calendar.MONTH, Integer.parseInt(id.substring(5, 7)));
	         month1.set(Calendar.DAY_OF_MONTH, 1);
	         Calendar month2 = (Calendar)month1.clone();
	         month2.add(Calendar.MONTH, 1);
	         sql = String.format("select 103, guid,name,'policy.icon.png','', 'fnOpenPolicy(''' || guid ||''')' from cloud.policy where type=1 and createtime>=to_date('%1$s-01 00:00:00','yyyy-mm-dd hh24:mi:ss') and createtime<to_date('%2$s-01 00:00:00','yyyy-mm-dd hh24:mi:ss') order by createtime asc",
	                     String.format("%d-%02d", month1.get(Calendar.YEAR), month1.get(Calendar.MONTH)),
	                     String.format("%d-%02d", month2.get(Calendar.YEAR), month2.get(Calendar.MONTH)));			
		}		
		else
			sql = String.format("select category, id, name,icon,'', link from cloud.nodes where parentId='%1$s'",id);
			
		String[][] result = DataAccessObject.GetArray(sql);
		if (result != null) {
			for (int i = 0; i < result.length; i++) {
				result[i][4] = hasChild;
			}
		}
		return result;
	}

	/**
	 * 获取报告保存记录列表
	 * 
	 * @return 存放了报告的二维数组
	 */
	public static String[][] getReports(int toolId, String filter, boolean showALl, String ownerId)
	{
		String sql = "select a.id,a.caption,to_char(a.reportdate,'yyyy-mm-dd'),b.name,c.name from cloud.reports a,cloud.toolset b,users c where a.toolid=b.id and a.ownerid=c.id(+)";
		if(toolId>0)
			sql += String.format(" and a.toolid=%d", toolId);
		if(null!=filter && filter.length()>0)
			sql += String.format(" and lower(a.caption) like '%%%1$s%%'", filter.toLowerCase());
		if(showALl)
			sql += String.format(" and a.ownerid='%1$s'", ownerId);
		sql += " order by reportdate asc";
		String[][] result = DataAccessObject.GetArray(sql);
		return result;
	}

	/**
	 * 获取报告信息
	 * 
	 * @param id
	 *            报告编号
	 * @return 报告信息一维数组
	 */
	public static String[] getReport(String id) {
		String reportId = id.replace("'", "''");
		String sql = String
				.format("select a.id,a.caption,utl_raw.cast_to_varchar2(a.content),to_char(a.reportdate,'yyyy-mm-dd'),b.name from cloud.reports a,cloud.toolset b where a.toolid=b.id and a.id='%1$s' order by reportdate asc",
						reportId);
		String[] report = DataAccessObject.GetSingleRowValue(sql);
		String[] data = null;
		if (report != null) {
			sql = String
					.format("select filename from cloud.reportfiles where reportid='%1$s' order by orderno asc",
							reportId);
			String[] fileNames = DataAccessObject.GetList(sql);
			int count = 5;
			if (fileNames != null)
				count += fileNames.length;
			data = new String[count];
			data[0] = report[0];
			data[1] = report[1];
			data[2] = report[2];
			data[3] = report[3];
			data[4] = report[4];
			for (int i = 5; i < count; i++)
				data[i] = fileNames[i - 5];
		}
		return data;
	}

	/**
	 * 保存测试报告
	 * @param toolId
	 * @param reportId
	 * @param reportName
	 * @param reportDate
	 * @param reportDescr
	 * @param removedFiles
	 * @param files
	 * @return
	 */
	public static String saveReport(String ownerId, String toolId, String reportId,
			String reportName, String reportDate, String reportDescr,
			String removedFiles, List<File> files, List<String> fileNames) {

		Connection conn = DataAccessObject.getConnection();
		try {

			conn.setAutoCommit(false);
			Statement sm = conn.createStatement();
			String folder = "";
			ResultSet rs;			
			if (reportId != null && reportId.length() > 0) {

				if (files != null && files.size() > 0) {
					rs = sm.executeQuery(String.format(
							"select folder from cloud.reports where id='%1$s'",
							reportId));
					if (rs.next())
						folder = rs.getString(1);
					if (folder == "") {
						Calendar calendar = Calendar.getInstance();
						folder = String.valueOf(calendar.getTimeInMillis());
					}
				}
				sm.addBatch(String
						.format("update cloud.reports set caption='%1$s',reportdate=to_date('%2$s','yyyy-MM-dd'),content=EMPTY_BLOB(),toolid=%3$s where id='%4$s'",
								reportName, reportDate, toolId, reportId));
				if (removedFiles != null && removedFiles.length() > 0) {
					String[] fileList = removedFiles.split(":");
					for (int i = 0; i < fileList.length; i++) {
						String removedFile = fileList[i].trim();
						sm.addBatch(String
								.format("delete from cloud.reportfiles where reportid='%1$s' and filename='%2$s'",
										reportId, removedFile));
					}
				}
			} else {
				if (files != null && files.size() > 0) {
					Calendar calendar = Calendar.getInstance();
					folder = String.valueOf(calendar.getTimeInMillis());
				}
				Guid guid = new Guid();
				reportId = guid.toString();
				sm.addBatch(String
						.format("insert into cloud.reports(id,caption,content,reportdate, toolid,ownerId) values('%1$s','%2$s',EMPTY_BLOB(),to_date('%3$s','yyyy-MM-dd'),%4$s,'%5$s' )",
								reportId, reportName.replace("'", "''"),
								reportDate.replace("'", "''"), toolId, ownerId));
			}

			String filePath = "d:/data/cloudreport/uploads/" + folder;

			if (files != null) {
				// 保存文件记录
				int count = 0;
				for (int i=0;i<files.size();i++)
				{					
					java.io.File fileInfo = new java.io.File(filePath);
					if (!fileInfo.exists()) {
						fileInfo.mkdirs();
						sm.addBatch(String
								.format("update cloud.reports set folder='%1$s' where id='%2$s'",
										folder, reportId));
					}
					sm.addBatch(String
							.format("delete from cloud.reportfiles where reportid='%1$s' and lower(fileName)='%2$s'",
									reportId, fileNames.get(i).replace("'", "''").toLowerCase()));
					sm.addBatch(String
							.format("insert into cloud.reportfiles(reportid,orderNo,fileName) values('%1$s',%2$s,'%3$s')",
									reportId, count++, fileNames.get(i).replace("'", "''")));

				}
			}
			sm.executeBatch();
			rs = sm.executeQuery("select content from cloud.reports where id='"
					+ reportId + "' FOR UPDATE ");
			if (rs.next()) {
				oracle.sql.BLOB blob = (oracle.sql.BLOB) rs.getBlob(1);
				OutputStream stream = blob.setBinaryStream(0);
				stream.write(reportDescr.getBytes());
				stream.flush();
				stream.close();
			}
			// 删除已有文件
			if (files != null) {
				for (int i=0;i<files.size();i++)
				{
					java.io.File fileInfo = new java.io.File(filePath, fileNames.get(i));
					if (fileInfo.exists()) {
						fileInfo.delete();
					}
				}
				// 将上传文件全部保存到指定目录
				for (int i=0;i<files.size();i++)
				{
					File file = files.get(i);
					InputStream is = new FileInputStream(file);
					File destFile = new File(filePath, fileNames.get(i));
					OutputStream os = new FileOutputStream(destFile);
					byte[] buffer = new byte[400];

					int length = 0;

					while ((length = is.read(buffer)) > 0) {
						os.write(buffer, 0, length);
					}
					is.close();
					os.close();
				}
			}

			conn.commit();
		} catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
			}
			LoggerFactory.Write(e);
		} catch (IOException e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
			}
			LoggerFactory.Write(e);
		} 
		return reportId;
		/*
		 * PreparedStatement conentStmt =
		 * conn.prepareStatement("update reports set content=? where jbxx_bh='"
		 * + reportId + "'"); Blob blobData = new Blob(); blobData.setBytes(0,
		 * sessionDescr.getBytes()); conentStmt.setBlob(1, blobData);
		 * conentStmt.executeUpdate(); conentStmt.close(); conn.commit();
		 */
	}
	
	public static String[] getReportFiles(String reportId)
	{		
		String sql = String.format("select filename from cloud.reportfiles where reportid='%1$s'", reportId);
		return DataAccessObject.GetList(sql);
	}
	
	public static boolean savePolicy(String ownerId, int type, String policyId, String policyName, String[] testTypes, String[] testSolutions, StringBuilder result)
	{
		if(policyId==null || policyId.length()<1)
		{
			String sql = String.format("select guid from cloud.policy where lower(name)='%1$s' and ownerId='%2$s'", policyName.replace("'","''").toLowerCase(),ownerId );
			int count = DataAccessObject.GetCount(sql);
			if(count>0)
			{
				result.append("保存失败: 策略名称已经存在!");
				return false;
			}
			policyId= (new Guid()).toString();
			count = 1;
			if(testTypes!=null)
				count += testTypes.length;
			String[] sqlList = new String[count];			
			sqlList[0] = String.format("insert into cloud.policy(type, guid,name,createtime,ownerid) values('%1$d','%2$s','%3$s',sysdate,'%4$s')", type, policyId, policyName.replace("'","''"), ownerId);
			if(testTypes!=null)
			{		
				for(int i=0;i<testTypes.length;i++)
				{
					sqlList[i+1] = String.format("insert into cloud.policysolutions(policyid,type,sluid, sortno) values('%1$s',%2$s,'%3$s',%4$d)", policyId, testTypes[i], testSolutions[i], i+1);
				}
			}
			try {
				DataAccessObject.ExecuteSQL(sqlList);
			} catch (SQLException e) {
				LoggerFactory.Write(e);
				result.append("保存失败,请联系系统管理员!");
				return false;
			}
			result.append(policyId);
		}
		else
		{
			String sql = String.format("select guid from cloud.policy where guid!='%1$s' and lower(name)='%2$s' and ownerId='%3$s'", policyId, policyName.replace("'","''").toLowerCase(),ownerId );
			int count = DataAccessObject.GetCount(sql);
			if(count>0)
			{
				result.append("保存失败: 策略名称已经存在!");
				return false;
			}
			count = 2;
			if(testTypes!=null)
				count += testTypes.length;
			String[] sqlList = new String[count];
			sqlList[0] = String.format("delete from cloud.policysolutions where policyid='%1$s'", policyId);
			sqlList[1] = String.format("update cloud.policy set name='%1$s' where guid='%2$s'", policyName.replace("'","''"), policyId);
			if(testTypes!=null)
			{		
				for(int i=0;i<testTypes.length;i++)
				{
					sqlList[i+2] = String.format("insert into cloud.policysolutions(policyid,type,sluid, sortno) values('%1$s',%2$s,'%3$s',%4$d)", policyId, testTypes[i], testSolutions[i], i+1);
				}
			}
			try {
				DataAccessObject.ExecuteSQL(sqlList);
			} catch (SQLException e) {
				LoggerFactory.Write(e);
				result.append("保存失败,请联系系统管理员!");
				return false;
			}
			result.append(policyId);
		}
		return true;
	}
	
	public static String[][] getPolicySolutions(String policyId)
	{
		String sql = String.format("select a.type, a.sluid,b.name from cloud.policysolutions a, ide.solutions b where a.policyid='%1$s' and a.sluid=b.guid order by sortno", policyId);
		return DataAccessObject.GetArray(sql);
	}
	
	public static String getSolutionName(String sluId)
	{
		String sql = String.format("select name from ide.solutions where sluId='%1$s'", sluId);
		return DataAccessObject.GetString(sql);
	}
	
	public static String[] getPolicy(String policyId)
	{
		String sql = String.format("select guid,name,type,to_char(createtime, 'yyyy-mm-dd') from cloud.policy where guid='%1$s'", policyId);
		return DataAccessObject.GetSingleRowValue(sql);
	}
	
	public static boolean deletePolicy(String policyId, String ownerId, StringBuilder result)
	{
		String sql = String.format("select ownerid from cloud.policy where guid='%1$s'" , policyId);
		String ownerId1 =DataAccessObject.GetString(sql);
		if(ownerId1==null)
		{
			result.append("删除失败：记录不存在!");
			return false;
		}
		if(!ownerId.equals(ownerId1))
		{
			result.append("删除失败：不能删除别人创建的策略!");
			return false;
		}
		sql = String.format("select guid from cloud.executions where policyId='%1$s'" , policyId);
		int count = DataAccessObject.GetCount(sql);		
		if(count>0)
		{
			result.append("删除失败：策略已经被执行!");
			return false;
		}
		String[] sqlList = new String[2];
		sqlList[0] = String.format("delete from cloud.policysolutions where policyid='%1$s'", policyId);
		sqlList[1] = String.format("delete from cloud.policy where guid='%1$s'", policyId);		
		try {
			DataAccessObject.ExecuteSQL(sqlList);
		} catch (SQLException e) {
			LoggerFactory.Write(e);
			result.append("删除失败,请联系系统管理员!");
			return false;
		}
		return true;
	}
	
	public static String[][] getPolicyes(String filter, boolean showAll, String ownerId )
	{
		String sql = String.format("select a.guid,a.name,to_char(a.createtime, 'yyyy-mm-dd'),b.name,a.ownerid from cloud.policy a, users b where a.ownerid=b.id(+)", ownerId);
		if(!showAll)
			sql += String.format("and a.ownerid='%s'", ownerId);
		if(null!=filter && filter.length()>0)
			sql += String.format(" and lower(a.name) like '%%%s%%'" , filter.replace("'", "''").toLowerCase());
		sql += " order by createtime desc";
		return DataAccessObject.GetArray(sql);
	}

}
