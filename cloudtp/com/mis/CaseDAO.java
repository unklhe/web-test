package cloudtp.com.mis;

import java.io.*;
import java.sql.*;
import java.util.Calendar;

import org.apache.commons.net.ftp.*;

import cloudtp.com.Guid;
import cloudtp.com.LoggerFactory;
import cloudtp.com.Util;
import cloudtp.com.dao.*;

/**
 * 
 * @author Hetao
 *
 */
public class CaseDAO {

	/**
	 *  获取用户有权浏览的所有根产品
	 * @param userId 工号
	 * @return 二维数组
	 */
	public static String[][] GetProductList(String userId) {
		try {
			String sql = String
					.format("select id,name,category,sn,descr from nodes where category=%d and nvl(parentid,0)=0",
							Keys.CaseTreeNodeType_Product);
			sql += String
					.format(" and ( nvl(sharemark,0) in ('%1$d','%2$d') or f_isnodeowner( id, '%3$s')=1 )",
							Keys.CaseShareLevel_Public,
							Keys.CaseShareLevel_Protect, userId);
			sql += " order by id";
			return DataAccessObject.GetArray(sql);
		} catch (Exception ex) {
			LoggerFactory.Write(ex);
		}
		return null;
	}

	// / <summary>
	// / 获取所有根产品
	// / </summary>
	// / <returns></returns>
	public static String[][] GetProductList() {
		try {
			String sql = String
					.format("select id,name,category,sn,descr from nodes where category=%1$d and nvl(parentid,0)=0 order by id",
							Keys.CaseTreeNodeType_Product);
			return DataAccessObject.GetArray(sql);
		} catch (Exception ex) {
			LoggerFactory.Write(ex);
		}
		return null;
	}

	// / <summary>
	// / 获取用户有权浏览的所有子节点
	// / </summary>
	// / <param name="userId">工号</param>
	// / <param name="parentId">父节点编号</param>
	// / <returns></returns>
	public static String[][] GetSharedNodesForUser(String userId, int parentId) {
		try {
			String sql = String
					.format("select id,name,category,sn,descr from nodes where nvl(parentid,0)=%1$d",
							parentId);
			sql += String
					.format(" and ( f_getnodesharemark(id) in ('%1$d','%2$d') or f_isnodeowner(id, '%3$s')=1 )",
							Keys.CaseShareLevel_Public,
							Keys.CaseShareLevel_Protect, userId);
			sql += " order by id";
			return DataAccessObject.GetArray(sql);
		} catch (Exception ex) {
			LoggerFactory.Write(ex);
		}
		return null;
	}

	// / <summary>
	// / 获取所有子节点
	// / </summary>
	// / <param name="parentId">父节点编号</param>
	// / <returns></returns>
	public static String[][] GetNodeList(int parentId) {
		try {
			String sql = String
					.format("select id,name,category,sn,descr from nodes where nvl(parentid,0)=%1$d",
							parentId);
			sql += " order by id";

			return DataAccessObject.GetArray(sql);
		} catch (Exception ex) {
			LoggerFactory.Write(ex);
		}
		return null;
	}

	public static String[][] GetMyNodeList(String userId, int category) {
		try {
			String sql = String
					.format("select id,name,category,sn,descr from nodes where category=%1$d and (ownerid='%2$s' or id in (select nodeid from node_users where userid='%2$s'))",
							category, userId);
			sql += " order by id";

			return DataAccessObject.GetArray(sql);
		} catch (Exception ex) {
			LoggerFactory.Write(ex);
		}
		return null;
	}

	public static String[][] GetMyCasesetList(String userId) {
		try {
			String sql = String
					.format("select guid,name,%1$d,'',descr from casesets where ownerid='%2$s' or guid in (select guid from caseset_users where userid='%2$s')",
							Keys.CaseTreeNodeType_Caseset, userId);
			sql += " order by guid";

			return DataAccessObject.GetArray(sql);
		} catch (Exception ex) {
			LoggerFactory.Write(ex);
		}
		return null;
	}

	public static String[][] GetSharedCasesetsForUser(String userId,
			int parentId) {
		try {
			String sql = String.format(
					"select guid,name from casesets where parentId=%1$d",
					parentId);
			sql += String
					.format(" and (ownerId='%1$s' or  guid in (select setguid from caseset_users where userid='%1$s') or f_isnodeowner(%2$d,'%1$s')=1 or f_getnodesharemark(%2$d) in ('%3$d','%4$d'))",
							userId, parentId, Keys.CaseShareLevel_Public,
							Keys.CaseShareLevel_Protect);

			return DataAccessObject.GetArray(sql);
		} catch (Exception ex) {
			LoggerFactory.Write(ex);
		}
		return null;
	}

	public static String[][] GetCasesetList(int parentId) {
		try {
			String sql = String.format(
					"select guid,name from casesets where parentId=%1$d",
					parentId);

			return DataAccessObject.GetArray(sql);
		} catch (Exception ex) {
			LoggerFactory.Write(ex);
		}
		return null;
	}

	public static String[][] GetUserList() {
		try {
			String sql = String
					.format("select id,name,descr from users order by name");

			return DataAccessObject.GetArray(sql);
		} catch (Exception ex) {
			LoggerFactory.Write(ex);
		}
		return null;
	}

	// / <summary>
	// / 查询用户是否拥有指定的节点
	// / </summary>
	// / <param name="category">节点种类</param>
	// / <param name="id">节点编号</param>
	// / <param name="userId">工号</param>
	// / <returns>true:拥有 false:不拥有</returns>
	public static boolean IsOwner(int category, String id, String userId) {
		String sql = null;
		if (category == Keys.CaseTreeNodeType_Caseset) {
			sql = String
					.format("select count(*) from casesets where guid='%1$s' and ownerid='%2$s'",
							id, userId);
		} else if (category == Keys.CaseTreeNodeType_Product
				|| category == Keys.CaseTreeNodeType_Project
				|| category == Keys.CaseTreeNodeType_Business
				|| category == Keys.CaseTreeNodeType_Version) {
			sql = String
					.format("select count(*) from nodes where id=%1$s and ownerid='%2$s'",
							id, userId);
		} else {
			return false;
		}

		int count = Integer.parseInt(DataAccessObject.GetString(sql));
		return count > 0;
	}

	// / <summary>
	// / 查询用户是否可以共享指定的节点
	// / </summary>
	// / <param name="category">节点种类</param>
	// / <param name="id">节点编号</param>
	// / <param name="userId">工号</param>
	// / <returns>true:可以 false:不可以</returns>
	public static boolean IsSharedUser(int category, String nodeId,
			String userId) {
		String sql = null;
		if (category == Keys.CaseTreeNodeType_Root) {
			sql = String
					.format("select count(*) from user_roles where userid='%1$s' and roleid in (%2$d,%3$d)",
							userId, Keys.Role_Admin, Keys.Role_Manager);
		} else if (category == Keys.CaseTreeNodeType_Caseset) {// 用例集共享用户不从父节点继承
			sql = String
					.format("select count(*) from casesets a,caseset_users b where a.guid=b.setguid and a.guid='%1$s' and b.userid='%2$s'",
							nodeId, userId);
		} else {
			sql = String.format("select f_isnodeowner(%1$s, '%2$s') from dual",
					nodeId, userId);
		}

		int count = Integer.parseInt(DataAccessObject.GetString(sql));
		return count > 0;
	}

	// / <summary>
	// / 查询指定的节点是否公开
	// / </summary>
	// / <param name="category"></param>
	// / <param name="id"></param>
	// / <returns></returns>
	public static boolean IsNodePublic(int category, String nodeId) {
		String sql = null;

		if (category == Keys.CaseTreeNodeType_Caseset) {
			sql = String
					.format("select sharemark,parentid from casesets where guid='%1$s'",
							nodeId);
			String[] list = DataAccessObject.GetSingleRowValue(sql);
			if (list != null) {
				if (list[0] == Keys.CaseShareLevel_Public + "")
					return true;
				else if (list[0] == Keys.CaseShareLevel_Inherited + "")
					return IsNodePublic(Keys.CaseTreeNodeType_Version, list[1]);
			}
			return false;

		} else {
			sql = String.format("select f_getnodesharemark(%1$s) from dual",
					nodeId);
			return DataAccessObject.GetString(sql) == Keys.CaseShareLevel_Public
					+ "";
		}
	}

	public static boolean IsNodeDuplicated(int category, int parentId,
			String nodeName) {
		String sql = String
				.format("select id from nodes where category=%1$d and nvl(parentId,0)=%2$d and upper(name)='%3$s'",
						category, parentId,
						nodeName.toUpperCase().replace("'", "''"));
		int count = DataAccessObject.GetCount(sql);
		return count > 0;
	}

	public static boolean IsNodeDuplicated(int nodeId, String nodeName) {
		String sql = String
				.format("select b.id from nodes a,nodes b where nvl(a.parentId,0)=nvl(b.parentId,0) and a.category=b.category and a.id<>b.id and a.id=%1$d and upper(b.name)='%2$s'",
						nodeId, nodeName.toUpperCase().replace("'", "''"));
		int count = DataAccessObject.GetCount(sql);
		return count > 0;
	}

	public static boolean IsCasesetDuplicated(int parentId, String casesetName) {
		String sql = String
				.format("select guid from casesets where parentId=%1$d and upper(name)='%2$s'",
						parentId, casesetName.toUpperCase().replace("'", "''"));
		int count = DataAccessObject.GetCount(sql);
		return count > 0;
	}

	public static boolean IsCasesetDuplicated(String guid, String casesetName) {
		String sql = String
				.format("select b.guid from casesets a,casesets b where a.parentid=b.parentid and a.guid<>b.guid and a.guid='%1$s' and upper(b.name)='%2$s'",
						guid, casesetName.toUpperCase().replace("'", "''"));
		int count = DataAccessObject.GetCount(sql);
		return count > 0;
	}

	public static boolean IsUserExisted(int parentId, String userId) {
		String sql = String
				.format("select count(*) from project_users where projectid=%1$d and userid='%2$s'",
						parentId, userId);
		sql += String
				.format(" union all select count(*) from projects where id=%1$d and ownerid='%2$s'",
						parentId, userId);

		String[] list = DataAccessObject.GetList(sql);
		if (list[0] == "0" && list[1] == "0")
			return false;
		return true;
	}

	public static int AddNode(int category, int parentId, String nodeName,
			String sn, String descr, int shareMark, String[] shareUsers,
			String ownerId) {
		String[] sqlList;
		int newId = GenerateNodeId();
		if (shareUsers != null) {
			sqlList = new String[shareUsers.length + 1];
			for (int i = 0; i < shareUsers.length; i++) {
				sqlList[i + 1] = String
						.format("insert into node_users(nodeid,userid) values(%1$d,'%2$s')",
								newId, shareUsers[i]);
			}
		} else {
			sqlList = new String[2];
		}
		sqlList[0] = String
				.format("insert into nodes(id,name,category,sn,descr,parentid,createdate,ownerid,sharemark) values(%1$d, '%2$s',%3$d,'%4$s','%5$s',%6$d,sysdate,'%7$s','%8$d')",
						newId, nodeName.replace("'", "''"), category,
						sn.replace("'", "''"), descr.replace("'", "''"),
						parentId, ownerId, shareMark);
		try
		{
			DataAccessObject.ExecuteSQL(sqlList);
		}
		catch(SQLException e)
		{
			LoggerFactory.Write(e);
		}
		return newId;
	}

	public static void UpdateNode(int nodeId, String nodeName, String sn,
			String descr, int shareMark, String[] shareUsers) {
		String[] sqlList;
		if (shareUsers != null) {
			sqlList = new String[shareUsers.length + 2];
			for (int i = 0; i < shareUsers.length; i++) {
				sqlList[i + 2] = String
						.format("insert into node_users(nodeid,userid) values(%1$d,'%2$s')",
								nodeId, shareUsers[i]);
			}
		} else {
			sqlList = new String[2];
		}
		sqlList[0] = String
				.format("update nodes set name='%2$s',sn='%3$s',descr='%4$s',sharemark=%5$d where id=%1$d",
						nodeId, nodeName.replace("'", "''"),
						sn.replace("'", "''"), descr.replace("'", "''"),
						shareMark);
		sqlList[1] = String.format("delete from node_users where nodeid=%1$d",
				nodeId);

		try
		{
			DataAccessObject.ExecuteSQL(sqlList);
		}
		catch(SQLException e)
		{
			LoggerFactory.Write(e);
		}
	}

	public static boolean IfNodeHasChild(int category, String nodeId) {

		if (category == Keys.CaseTreeNodeType_Product
				|| category == Keys.CaseTreeNodeType_Project
				|| category == Keys.CaseTreeNodeType_Business
				|| category == Keys.CaseTreeNodeType_Version) {
			String sql = String.format(
					"select count(*) from nodes where nvl(parentid,0)=%1$s",
					nodeId);
			String[] list = DataAccessObject.GetList(sql);
			if (list[0] != "0")
				return true;
		}
		if (category == Keys.CaseTreeNodeType_Business
				|| category == Keys.CaseTreeNodeType_Version) {
			String sql = String.format(
					"select count(*) from casesets where nvl(parentid,0)=%1$s",
					nodeId);
			String[] list = DataAccessObject.GetList(sql);
			if (list[0] != "0")
				return true;
		}
		return false;
	}

	public static String AddCaseset(int parentId, String casesetName,
			String descr, int shareMark, String[] shareUsers, String ownerId) {
		String[] sqlList;
		Guid guidObj = new Guid();
		String guid = guidObj.toString();
		if (shareUsers != null) {
			sqlList = new String[shareUsers.length + 1];
			for (int i = 0; i < shareUsers.length; i++) {
				sqlList[i + 1] = String
						.format("insert into caseset_users(setguid,userid) values('%1$s','%2$s')",
								guid, shareUsers[i]);
			}
		} else {
			sqlList = new String[1];
		}
		sqlList[0] = String
				.format("insert into casesets(guid,name,descr,parentid,createdate,modifydate,ownerid,casenum,sharemark) values('%1$s', '%2$s','%3$s',%4$d,sysdate,sysdate,'%5$s',0,'%6$d')",
						guid, casesetName.replace("'", "''"),
						descr.replace("'", "''"), parentId, ownerId, shareMark);

		try
		{
			DataAccessObject.ExecuteSQL(sqlList);
		}
		catch(SQLException e)
		{
			LoggerFactory.Write(e);
		};
		return guid;
	}

	public static void UpdateCaseset(String guid, String casesetName,
			String descr, int shareMark, String[] shareUsers) {
		String[] sqlList;
		if (shareUsers != null) {
			sqlList = new String[shareUsers.length + 2];
			for (int i = 0; i < shareUsers.length; i++) {
				sqlList[i + 2] = String
						.format("insert into caseset_users(setguid,userid) values('%1$s','%2$s')",
								guid, shareUsers[i]);
			}
		} else {
			sqlList = new String[2];
		}
		sqlList[0] = String
				.format("update casesets set name='%2$s',descr='%3$s',modifydate=sysdate,sharemark=%4$d} where guid='%1$s'",
						guid, casesetName.replace("'", "''"),
						descr.replace("'", "''"), shareMark);
		sqlList[1] = String.format(
				"delete from caseset_users where setguid='%1$s'", guid);

		try
		{
			DataAccessObject.ExecuteSQL(sqlList);
		}
		catch(SQLException e)
		{
			LoggerFactory.Write(e);
		};
	}

	public static int GenerateNodeId() {
		String[][] parameters = new String[2][];
		parameters[0] = new String[] { "in", "num", "entity", "1" };
		parameters[1] = new String[] { "out", "num", "entityId", "0" };
		try
		{
			DataAccessObject.ExecuteProcedure("generateEntityId", parameters);
		}
		catch(SQLException e)
		{
			LoggerFactory.Write(e);
		}		
		int nodeId = Integer.parseInt(parameters[1][3]);
		return nodeId;
	}

	public static String[] CountCases(int category, String id, Calendar date) {

		String where_clause = "";
		if (category == Keys.CaseTreeNodeType_Root) {
		} else if (category == Keys.CaseTreeNodeType_Product) {
			where_clause = String.format(" and productId=%1$s", id);
		} else if (category == Keys.CaseTreeNodeType_Project) {
			where_clause = String.format(" and projectId=%1$s", id);
		} else if (category == Keys.CaseTreeNodeType_Business) {
			where_clause = String.format(" and businessId=%1$s", id);
		} else if (category == Keys.CaseTreeNodeType_Version) {
			where_clause = String.format(" and versionId=%1$s", id);
		} else if (category == Keys.CaseTreeNodeType_Caseset) {
			where_clause = String.format(" and setid='%1$s'", id);
		} else if (category == Keys.CaseTreeNodeType_CommonCasesets) {
			where_clause = String.format(
					" and businessId=%1$s and versionid=0", id);
		}
		Calendar date1 = (Calendar) date.clone();
		date1.add(Calendar.DAY_OF_MONTH, 1);
		String sql = String
				.format("select sum(casenum - oricasenum) from caselogs where logtime>=to_date('%1$s','yyyy-mm-dd') and logtime<to_date('%2$s','yyyy-mm-dd') %3$s",
						String.format("%d-%02d-%02d", date.get(Calendar.YEAR),
								date.get(Calendar.MONTH),
								date.get(Calendar.DAY_OF_MONTH)), String
								.format("%d-%02d-%02d",
										date1.get(Calendar.YEAR),
										date1.get(Calendar.MONTH),
										date1.get(Calendar.DAY_OF_MONTH)),
						where_clause);
		int updated = 0;
		String res = DataAccessObject.GetString(sql);
		if (res != null)
			updated = Integer.parseInt(res);

		sql = String
				.format("select sum(casenum) from caselogs a where logtime=(select max(logtime) from caselogs b where a.setid=b.setid and b.logtime<to_date('%1$s','yyyy-mm-dd')) %2$s ",
						String.format("%d-%02d-%02d", date.get(Calendar.YEAR),
								date.get(Calendar.MONTH),
								date.get(Calendar.DAY_OF_MONTH)), where_clause);
		int total = 0;
		res = DataAccessObject.GetString(sql);
		if (res != null)
			total = Integer.parseInt(res);
		total += updated;
		return (new String[] { updated + "", total + "" });
	}

	public static int CountCases(int category, String id, int year, int month) {

		String where_clause = "";
		if (category == Keys.CaseTreeNodeType_Root) {
		} else if (category == Keys.CaseTreeNodeType_Product) {
			where_clause = String.format(" and productId=%1$s", id);
		} else if (category == Keys.CaseTreeNodeType_Project) {
			where_clause = String.format(" and projectId=%1$s", id);
		} else if (category == Keys.CaseTreeNodeType_Business) {
			where_clause = String.format(" and businessId=%1$s", id);
		} else if (category == Keys.CaseTreeNodeType_Version) {
			where_clause = String.format(" and versionId=%1$s", id);
		} else if (category == Keys.CaseTreeNodeType_Caseset) {
			where_clause = String.format(" and setid='%1$s'", id);
		} else if (category == Keys.CaseTreeNodeType_CommonCasesets) {
			where_clause = String.format(
					" and businessId=%1$s and versionid=0", id);
		}
		Calendar date = Calendar.getInstance();
		date.set(Calendar.YEAR, year);
		date.set(Calendar.MONTH, month);
		date.set(Calendar.DAY_OF_MONTH, 1);
		date.add(Calendar.MONTH, 1);
		String sql = String
				.format("select sum(casenum) from caselogs a where logtime=(select max(logtime) from caselogs b where a.setid=b.setid and b.logtime<to_date('%1$s','yyyy-mm-dd')) %2$s ",
						String.format("%d-%02d-%02d", date.get(Calendar.YEAR),
								date.get(Calendar.MONTH),
								date.get(Calendar.DAY_OF_MONTH)), where_clause);
		int total = 0;
		String res = DataAccessObject.GetString(sql);
		if (res != null)
			total = Integer.parseInt(res);
		return total;
	}

	public static int CountExecCases(int category, String id, int year,
			int month) {

		String where_clause = "";
		if (category == Keys.CaseTreeNodeType_Root) {
		} else if (category == Keys.CaseTreeNodeType_Product) {
			where_clause = String
					.format(" and casesetIndex in (select guid from casesets where parentId in (select a.id from nodes a where a.parentId in (select b.id from nodes b where b.parentId in (select c.id from nodes c where c.parentId=%1$s)) union select e.id from nodes e where e.parentId in (select f.id from nodes f where f.parentId=%1$s) ))",
							id);
		} else if (category == Keys.CaseTreeNodeType_Project) {
			where_clause = String
					.format(" and casesetIndex in (select guid from casesets where parentId in (select a.id from nodes a where a.parentId in (select b.id from nodes b where b.parentId =%1$s) union select e.id from nodes e where e.parentId=%1$s ))",
							id);
		} else if (category == Keys.CaseTreeNodeType_Business) {
			where_clause = String
					.format(" and casesetIndex in (select guid from casesets where parentId in (select a.id from nodes a where a.parentId=%1$s) or parentId=%1$s)",
							id);
		} else if (category == Keys.CaseTreeNodeType_Version) {
			where_clause = String
					.format(" and casesetIndex in (select guid from casesets where parentId=%1$s)",
							id);
		} else if (category == Keys.CaseTreeNodeType_Caseset) {
			where_clause = String.format(" and casesetIndex='%1$s' ", id);
		} else if (category == Keys.CaseTreeNodeType_CommonCasesets) {
			where_clause = String
					.format(" and casesetIndex in (select guid from casesets where parentId=%1$s)",
							id);
		}
		Calendar date = Calendar.getInstance();
		date.set(Calendar.YEAR, year);
		date.set(Calendar.MONTH, month);
		date.set(Calendar.DAY_OF_MONTH, 1);
		date.add(Calendar.MONTH, 1);
		String sql = String
				.format("select count(*) from execlogs_case where endTime<to_date('%1$s','yyyy-mm-dd') and type in (1,3) %2$s",
						String.format("%d-%02d-%02d", date.get(Calendar.YEAR),
								date.get(Calendar.MONTH),
								date.get(Calendar.DAY_OF_MONTH)), where_clause);
		int total = 0;
		String res = DataAccessObject.GetString(sql);
		if (res != null)
			total = Integer.parseInt(res);
		return total;

	}

	public static int CountExecBaseCases(int category, String id, int year,
			int month) {

		String where_clause = "";
		if (category == Keys.CaseTreeNodeType_Root) {
			where_clause = String
					.format(" casesetIndex in (select guid from casesets where parentId in (select id form nodes where category=%1$d))",
							Keys.CaseTreeNodeType_Business);
		} else if (category == Keys.CaseTreeNodeType_Product) {
			where_clause = String
					.format(" and casesetIndex in (select guid from casesets where parentId in (select a.id from nodes a where a.parentId in (select e.id from nodes e where e.parentId=%1$s)))",
							id);
		} else if (category == Keys.CaseTreeNodeType_Project) {
			where_clause = String
					.format(" and casesetIndex in (select guid from casesets where parentId in (select a.id from nodes a where a.parentId=%1$s))",
							id);
		} else if (category == Keys.CaseTreeNodeType_Business) {
			where_clause = String
					.format(" and casesetIndex in (select guid from casesets where parentId=%1$s)",
							id);
		} else if (category == Keys.CaseTreeNodeType_Version) {
			return 0;
		} else if (category == Keys.CaseTreeNodeType_Caseset) {
			where_clause = String
					.format(" and casesetIndex='%1$s' and  exists (select a.guid from casesets a,nodes b where a.guid=casesetIndex and a.parentId=b.id and b.category=%2$d)",
							id, Keys.CaseTreeNodeType_Business);
		} else if (category == Keys.CaseTreeNodeType_CommonCasesets) {
			where_clause = String
					.format(" and casesetIndex in (select guid from casesets where parentId=%1$s)",
							id);
		}
		Calendar date = Calendar.getInstance();
		date.set(Calendar.YEAR, year);
		date.set(Calendar.MONTH, month);
		date.set(Calendar.DAY_OF_MONTH, 1);
		date.add(Calendar.MONTH, 1);
		String sql = String
				.format("select count(*) from execlogs_case where endTime<to_date('%1$s','yyyy-mm-dd') and type in (1,3) %2$s",
						String.format("%d-%02d-%02d", date.get(Calendar.YEAR),
								date.get(Calendar.MONTH),
								date.get(Calendar.DAY_OF_MONTH)), where_clause);
		int total = 0;
		String res = DataAccessObject.GetString(sql);
		if (res != null)
			total = Integer.parseInt(res);
		return total;
	}

	public static int CountBaseCases(int category, String id, int year,
			int month) {

		String where_clause = "";
		if (category == Keys.CaseTreeNodeType_Root) {
		} else if (category == Keys.CaseTreeNodeType_Product) {
			where_clause = String.format(" and productId=%1$s", id);
		} else if (category == Keys.CaseTreeNodeType_Project) {
			where_clause = String.format(" and projectId=%1$s", id);
		} else if (category == Keys.CaseTreeNodeType_Business) {
			where_clause = String.format(" and businessId=%1$s", id);
		} else if (category == Keys.CaseTreeNodeType_Version) {
			return 0;
		} else if (category == Keys.CaseTreeNodeType_Caseset) {
			where_clause = String.format(" and setid='%1$s'", id);
		} else if (category == Keys.CaseTreeNodeType_CommonCasesets) {
			where_clause = String.format(" and businessId=%1$s", id);
		}
		Calendar date = Calendar.getInstance();
		date.set(Calendar.YEAR, year);
		date.set(Calendar.MONTH, month);
		date.set(Calendar.DAY_OF_MONTH, 1);
		date.add(Calendar.MONTH, 1);

		String sql = String
				.format("select sum(casenum) from caselogs a where logtime=(select max(logtime) from caselogs b where a.setid=b.setid and b.logtime<to_date('%1$s','yyyy-mm-dd')) and a.versionid=0 %2$s ",
						String.format("%d-%02d-%02d", date.get(Calendar.YEAR),
								date.get(Calendar.MONTH),
								date.get(Calendar.DAY_OF_MONTH)), where_clause);
		int total = 0;
		String res = DataAccessObject.GetString(sql);
		if (res != null)
			total = Integer.parseInt(res);
		return total;
	}

	public static String[] CountBaseCases(String bizId, Calendar date) {
		Calendar date1 = (Calendar) date.clone();
		date1.add(Calendar.DAY_OF_MONTH, 1);

		String sql = String
				.format("select sum(casenum - oricasenum) from caselogs where logtime>=to_date('%1$s','yyyy-mm-dd') and logtime<to_date('%2$s','yyyy-mm-dd') and businessId=%3$s and versionid=0",
						String.format("%d-%02d-%02d", date.get(Calendar.YEAR),
								date.get(Calendar.MONTH),
								date.get(Calendar.DAY_OF_MONTH)), String
								.format("%d-%02d-%02d",
										date1.get(Calendar.YEAR),
										date1.get(Calendar.MONTH),
										date1.get(Calendar.DAY_OF_MONTH)),
						bizId);
		int updated = 0;
		String res = DataAccessObject.GetString(sql);
		if (res != null)
			updated = Integer.parseInt(res);
		sql = String
				.format("select sum(casenum) from caselogs a where logtime=(select max(logtime) from caselogs b where a.setid=b.setid and b.logtime<to_date('%1$s','yyyy-mm-dd'))  and businessId=%2$s  and versionid=0",
						String.format("%d-%02d-%02d", date.get(Calendar.YEAR),
								date.get(Calendar.MONTH),
								date.get(Calendar.DAY_OF_MONTH)), bizId);
		int total = 0;
		res = DataAccessObject.GetString(sql);
		if (res != null)
			total = Integer.parseInt(res);
		total += updated;
		return (new String[] { updated + "", total + "" });
	}

	public static String[] TryGetCasesetRoute(String productName,
			String projectName, String bizName, String versionName) {
		String[] routeInfo = new String[4];

		if (!Util.IsStringNullOrEmpty(productName)) {
			String sql = String.format(
					"select id from nodes where name='%1$s' and category=%2$d",
					productName, Keys.CaseTreeNodeType_Product);
			routeInfo[0] = DataAccessObject.GetString(sql);
			if (!Util.IsStringNullOrEmpty(routeInfo[0])
					&& !Util.IsStringNullOrEmpty(projectName)) {
				sql = String
						.format("select id from nodes where name='%1$s' and category=%2$d and parentId=%3$s",
								projectName, Keys.CaseTreeNodeType_Project,
								routeInfo[0]);
				routeInfo[1] = DataAccessObject.GetString(sql);
				if (!Util.IsStringNullOrEmpty(routeInfo[1])
						&& !Util.IsStringNullOrEmpty(bizName)) {
					sql = String
							.format("select id from nodes where name='%1$s' and category=%2$d and parentId=%3$s",
									bizName, Keys.CaseTreeNodeType_Business,
									routeInfo[1]);
					routeInfo[2] = DataAccessObject.GetString(sql);
					if (!Util.IsStringNullOrEmpty(routeInfo[2])) {
						if (!Util.IsStringNullOrEmpty(versionName)) {
							sql = String
									.format("select id from nodes where name='%1$s' and category=%2$d and parentId=%3$s",
											versionName,
											Keys.CaseTreeNodeType_Version,
											routeInfo[2]);
							routeInfo[3] = DataAccessObject.GetString(sql);
						} else {// 基本用例集
							routeInfo[3] = routeInfo[2];
						}
					}
				}
			}
		}
		return routeInfo;
	}

	public static String[][] GetCaseLogs(String caseIndex) {

		String sql = String
				.format("select guid, execid, to_char(starttime,'yyyy-mm-dd hh24:mi:ss'), to_char(endtime,'yyyy-mm-dd hh:mi:ss'),type from execlogs_case where caseindex='%1$s}'",
						caseIndex);
		return DataAccessObject.GetArray(sql);
	}

	public static byte[] DownloadCase(String casesetIndex) {
		byte[] data = null;
		String url = "10.47.171.99";
		String projectFile = String.format("%s\\project.xml", casesetIndex);

		FTPClient ftp = new FTPClient();
		try {
			int reply;
			ftp.connect(url);
			// 如果采用默认端口，可以使用ftp.connect(url)的方式直接连接FTP服务器
			ftp.login("IdeClientor", "secretuser");// 登录
			reply = ftp.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
				return null;
			}
			ftp.changeWorkingDirectory("ide");// 转移到FTP服务器目录
			InputStream inputStream = ftp.retrieveFileStream(projectFile);
			if(null!=inputStream)
			{
				data = new byte[4];
				int readLen = 0;
				while(readLen<4)
					readLen += inputStream.read(data, readLen, 4-readLen);
				int len = data[0]&0x0FF;
				len += (data[1]&0x0FF) << 8;
				len += (data[2]&0x0FF) << 16;
				len += (data[3]&0x0FF) << 24;
				data = new byte[len];
				readLen = 0;
				while(readLen<data.length)
					readLen += inputStream.read(data, readLen, data.length - readLen);
				inputStream.close();
			}
			ftp.logout();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (ftp.isConnected()) {
				try {
					ftp.disconnect();
				} catch (IOException ioe) {
				}
			}
		}
		return data;
	}

	public static int[] CountExecutedCases(int category, String id,
			Calendar date) {

		String where_clause = "";
		if (category == Keys.CaseTreeNodeType_Root) {
		} else if (category == Keys.CaseTreeNodeType_Product) {
			where_clause = String
					.format(" and casesetIndex in (select guid from casesets where parentId in (select a.id from nodes a where a.parentId in (select b.id from nodes b where b.parentId in (select b.id from nodes c where c.parentId=%1$s)) union select e.id from nodes e where e.parentId in (select f.id from nodes f where f.parentId=%1$s) ))",
							id);
		} else if (category == Keys.CaseTreeNodeType_Project) {
			where_clause = String
					.format(" and casesetIndex in (select guid from casesets where parentId in (select a.id from nodes a where a.parentId in (select b.id from nodes b where b.parentId =%1$s) union select e.id from nodes e where e.parentId=%1$s ))",
							id);
		} else if (category == Keys.CaseTreeNodeType_Business) {
			where_clause = String
					.format(" and casesetIndex in (select guid from casesets where parentId in (select a.id from nodes a where a.parentId=%1$s) or parentId=%1$s)",
							id);
		} else if (category == Keys.CaseTreeNodeType_Version) {
			where_clause = String
					.format(" and casesetIndex in (select guid from casesets where parentId=%1$s)",
							id);
		} else if (category == Keys.CaseTreeNodeType_Caseset) {
			where_clause = String.format(" and casesetIndex='%1$s'", id);
		} else if (category == Keys.CaseTreeNodeType_CommonCasesets) {
			where_clause = String
					.format(" and casesetIndex in (select guid from casesets where parentId=%1$s)",
							id);
		}
		Calendar date1 = (Calendar) date.clone();
		date1.add(Calendar.DAY_OF_MONTH, 1);
		String sql = String
				.format("select type,count(*) from execlogs_case where startTime>=to_date('%1$s','yyyy-mm-dd') and endTime<to_date('%2$s','yyyy-mm-dd') %3$s group by type order by type",
						String.format("%d-%02d-%02d", date.get(Calendar.YEAR),
								date.get(Calendar.MONTH),
								date.get(Calendar.DAY_OF_MONTH)), String
								.format("%d-%02d-%02d",
										date1.get(Calendar.YEAR),
										date1.get(Calendar.MONTH),
										date1.get(Calendar.DAY_OF_MONTH)),
						where_clause);
		String[][] list = DataAccessObject.GetArray(sql);
		int[] result = new int[] { 0, 0, 0 };
		if (list != null) {
			for (int i = 0; i < list.length; i++) {
				if (list[i][0] == "1") // 成功
					result[0] = Integer.parseInt(list[i][1]);
				else if (list[i][0] == "3") // 失败
					result[1] = Integer.parseInt(list[i][1]);
				else
					continue;
				result[2] = result[2] + Integer.parseInt(list[i][1]);
			}
		}
		return result;
	}

	public static void SaveMonthReport(String nodeId, int nodeCategory,
			int year, int month, int caseNum, int baseCaseNum, int caseExecNum,
			int baseCaseExecNum) {

		int newId = CaseDAO.GetMonthReportId(year, month, nodeId, nodeCategory);
		String sql = "";
		if (newId < 1) {
			sql = "select nvl(max(ReportId),0)+1 from monthreport";
			newId = Integer.parseInt(DataAccessObject.GetString(sql));
			sql = String
					.format("insert into monthreport(reportId, year, month, nodeId, category,caseNum,baseCaseNum,caseExecNum,baseCaseExecNum) values(%1$d,%2$d,%3$d,'%4$s',%5$d,%6$d,%7$d,%8$d,%9$d)",
							newId, year, month, nodeId == "" ? "0" : nodeId,
							nodeCategory, caseNum, baseCaseNum, caseExecNum,
							baseCaseExecNum);
		} else {
			sql = String
					.format("update monthReport set caseNum=%1$d, baseCaseNum=%2$d,caseExecNum=%3$d,baseCaseExecNum=%4$d where reportid=%5$d",
							caseNum, baseCaseNum, caseExecNum, baseCaseExecNum,
							newId);
		}
		try
		{
			DataAccessObject.ExecuteSQL(sql);
		}
		catch(SQLException e)
		{
			LoggerFactory.Write(e);
		};
	}

	public static String[][] GetMonthReport(int year, String nodeId,
			int category) {

		String sql = String
				.format("select a.reportId,a.year, a.month,a.nodeid, a.category, b.name, c.name,a.casenum,a.baseCaseNum, a.caseExecNum, a.baseCaseExecNum from monthreport a,nodes b,casesets c where a.nodeid=b.id(+) and a.nodeid=c.guid(+) and a.year=%1$d and a.nodeid='%2$s' and a.category='%3$d' order by a.year asc, a.month asc",
						year, nodeId, category);
		return DataAccessObject.GetArray(sql);
	}

	public static int GetMonthReportId(int year, int month, String nodeId,
			int nodeCategory) {

		String sql = String
				.format("select reportid from monthreport where year=%1$d and month=%2$d and nodeid='%3$s' and category='%4$d'",
						year, month, nodeId, nodeCategory);
		String strTmp = DataAccessObject.GetString(sql);
		if (!Util.IsStringNullOrEmpty(strTmp))
			return Integer.parseInt(strTmp);
		return -1;
	}
	
	public static boolean SaveSolution(String ownerId, String sluName, String content, StringBuilder sbIndex, StringBuilder sbResult)
	{
		byte[] data = null;
		try {
			data = content.getBytes("utf-8");
		} catch (UnsupportedEncodingException e1) {
			LoggerFactory.Write(e1);
		}
	    Connection conn  = DataAccessObject.getConnection();
	    
	    Statement stat = null;
	    try
	    {
	    	conn.setAutoCommit(false);
	    	stat = conn.createStatement();
	    	
	    	String sql = "";
	    	ResultSet rs;
	    	String index = sbIndex.toString();
	    	if(null==index || index.length()<1)
	    	{
	    		sql = String.format("select guid from solutions where name='%s' and ownerid='%s'", sluName.replace("'","''"), ownerId);
	    		rs = stat.executeQuery(sql);
		    	if(rs.next())
		    	{
		    		sbResult.append("保存失败：方案名称已近存在，请使用另外的方案名称!");
		    		return false;
		    	}
	    	}
	    	
	    	if(null==index || index.length()<1)
	    	{
				index = (new Guid()).toString();
				sql = String.format("insert into solutions(guid,name,content,ownerId,createtime) values('%s','%s', empty_blob(),'%s',sysdate)",
		    		index, sluName.replace("'","''"), ownerId);
	    	}
	    	else
	    	{
	    		sql = String.format("update solutions set name='%s' where guid='%s'", sluName.replace("'","''"), index);
	    	}
	    	stat.execute(sql);
	    	sql = String.format("select content from solutions where guid='%s' for update", index);
	    	rs = stat.executeQuery(sql);
	    	rs.next();
    	    //使用oracle.sql.BLOB类，没办法了，变成专用的了
    	    oracle.sql.BLOB blob = (oracle.sql.BLOB) rs.getBlob(1);
    	    //到数据库的输出流
    	    @SuppressWarnings("deprecation")
			OutputStream outStream = blob.getBinaryOutputStream();    	    
    	    outStream.write(data, 0, data.length);
    	    outStream.flush();
	    	outStream.close();
	    	stat.close();
	    	conn.commit();
	    	sbIndex.append(index);
	    	return true;
	    }
	    catch(Exception e){
	    	try {
				conn.rollback();
			} catch (SQLException e1) {
			}
	    }
	    finally
	    {
	    	if(stat!=null)
	    	{
	    		try{
	    		stat.close();
	    		}catch(Exception e){}
	    	}
	    	try {
				conn.close();
			} catch (SQLException e) {				
			}
	    }
	    return true;
	}
	
	public static boolean DeleteSolution(String index, StringBuilder sbResult)
	{
		
	    Connection conn  = DataAccessObject.getConnection();	    
	    Statement stat = null;
	    try
	    {
	    	conn.setAutoCommit(true);
	    	stat = conn.createStatement();
	    	
	    	
	    	String sql =String.format("select count(*) from execLogs where sluid='%1$s'" +
	    			" union select count(*) from executesolutions where sluid='%1$s'" +
	    			" union select count(*) from tasksolutions where sluid='%1$s'" +
	    			" union select count(*) from cloud.policysolutions where sluid='%1$s'", index);
	    	ResultSet rs = stat.executeQuery(sql);
	    	while(rs.next())
	    	{
	    		if (rs.getInt(1)>0)
		    	{
		    		sbResult.append("删除失败：该方案已经被使用或执行,不能删除!");
		    		return false;
		    	}
	    	}	    	
	    	sql = String.format("delete from solutions where guid='%s'", index);
	    	stat.execute(sql);
	    	stat.close();
	    	conn.commit();	    	
	    	return true;
	    }
	    catch(Exception e){
	    	try {
				conn.rollback();
			} catch (SQLException e1) {
			}
	    }
	    finally
	    {
	    	if(stat!=null)
	    	{
	    		try{
	    		stat.close();
	    		}catch(Exception e){}
	    	}
	    	try {
				conn.close();
			} catch (SQLException e) {				
			}
	    }
	    return true;
	}
	
	public static String[][] GetSolutionsList(String ownerId, String filter, boolean showAll)
	{
		String sql = String.format("select a.guid,a.name,to_char(a.createtime, 'yyyy-mm-dd'),b.name,a.ownerid from solutions a, users b where a.ownerid=b.id(+)", ownerId);
		if(!showAll)
			sql += String.format("and a.ownerid='%s'", ownerId);
		if(null!=filter && filter.length()>0)
			sql += String.format(" and lower(a.name) like '%%%s%%'" , filter.replace("'", "''").toLowerCase());
		sql += " order by createtime desc";
		return DataAccessObject.GetArray(sql);
	}
	
	public static String[] GetSolutions(String sluId)
	{
		String sql = String.format("select content from solutions where guid='%s'", sluId);
		byte[] data = DataAccessObject.GetBlob(sql);
		sql = String.format("select a.name,a.ownerid, b.name,to_char(a.createtime, 'yyyy-mm-dd'),'' from solutions a,users b where a.guid='%s' and a.ownerid=b.id(+)", sluId);
		try {
			String[] result = DataAccessObject.GetSingleRowValue(sql);
			result[4] = new String(data, 0 ,data.length, "utf-8") ;
			return result;
		} catch (UnsupportedEncodingException e) {
			LoggerFactory.Write(e);
		}
		return null;
	}
}
