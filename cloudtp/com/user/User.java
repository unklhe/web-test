package cloudtp.com.user;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import java.net.URL;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.xwiki.xmlrpc.XWikiXmlRpcClient;

import com.zte.jos.tech.xmlrpc.model.RpcUserInfoModel;

import cloudtp.com.Constants;
import cloudtp.com.LoggerFactory;
import cloudtp.com.PropertiesUtil;
import cloudtp.com.dao.DataAccessObject;


public class User {
	
	private static XWikiXmlRpcClient _rpc ;
	
	
	public static boolean ValidateUser(String userId, String passwd)
	{
		if(userId.equals("10000000"))
		{
			String sql  = String.format("select count(*) from superuser where encryptcol=f_md5('%s')", passwd);
			int count = Integer.parseInt(DataAccessObject.GetString(sql));
			return count==1;
		}
		try
		{
			/*
			OneTest_M00_UserLogonSrvStub stub = new OneTest_M00_UserLogonSrvStub();
			UserLogon userLogon = new UserLogon();
			UserLogonSrvRequest request = new UserLogonSrvRequest();
			MsgHeader header = new MsgHeader ();
			header.setUserId(userId);
			request.setMsgHeader(header);
			UserLogonSrvInput input = new UserLogonSrvInput ();
			header.setSubmitDate(Calendar.getInstance());
			input.setUserId(userId);
			input.setPassword(passwd);
			request.setInput(input);
			userLogon.setRequest(request);
			UserLogonResponse response = stub.userLogon(userLogon);
			if("S".equals(response.getUserLogonResult().getProcessStatus()) && null!=response.getUserLogonResult().getOutCollection())
			{
				return true;
			}*/
			String url = PropertiesUtil.TechPlusProperties.getProperty(Constants.TechPlusRpcUrl);
	        
	        /**
	         * 请参考：http://ws.apache.org/xmlrpc/apidocs/org/apache/xmlrpc/common/XmlRpcHttpRequestConfigImpl.html
	         */
	        XmlRpcClientConfigImpl xmlRpcConfig = new XmlRpcClientConfigImpl();
	        xmlRpcConfig.setServerURL(new URL(url));
	        xmlRpcConfig.setConnectionTimeout(30000);  //单位为毫秒，这里的30000是一个测试用的值，实际环境，请设置为一个合适的值。ConnectionTimeout也可不设置
	        xmlRpcConfig.setReplyTimeout(30000);       //单位为毫秒，这里的30000是一个测试用的值，实际环境，请设置为一个合适的值。ReplyTimeout也可不设置
	        
	        XmlRpcClient xmlRpcClient = new XmlRpcClient();
	        xmlRpcClient.setConfig(xmlRpcConfig);
	        
	        _rpc = new XWikiXmlRpcClient(xmlRpcClient);
	        _rpc.login(userId, passwd);
	        	       
	        //取得token
	        //String token=rpc.getToken();
	        //System.out.println(token);
	        return true;
		}
		catch(Exception e){
		}
		return false;
	}
	
	public static UserInfo GetUserInfo(String userId)
	{
		try
		{
			if(userId.equals("10000000"))
			{			
				String sql  = String.format("select name,descr from users where id='%s'", userId);
				String[] info = DataAccessObject.GetSingleRowValue(sql);				
				return new UserInfo(userId, info[0], info[1]);
			}
			 RpcUserInfoModel userinfo = _rpc.getUserInfo();
			 UserInfo userInfo = new UserInfo(userId, userinfo.getName(), "");
			 String sql  = String.format("select name,descr from users where id='%s'", userInfo.getUserId());
			 String[] info = DataAccessObject.GetSingleRowValue(sql);
			 if(info==null)
			 {
				 User.RegisterUser(userInfo.getUserId(), userInfo.getUserName());
			 }
			 return userInfo;
		        
		}
		catch(Exception e){
		}
		return null;
	}
	
	public static boolean RegisterUser(String userId, String userName)
	{
		try
		{
			String sql = String.format("select id from users where id='%s'", userId.replace("'", "''"));
			int count = DataAccessObject.GetCount(sql);
			if(count>0)
				return false;
			String[] sqlList  = new String[2];
			sqlList[0] = String.format("insert into users(id, name) values('%1$s','%2$s')", userId.replace("'","''"), userName.replace("'","''"));
			sqlList[1] = String.format("insert into user_roles(userid, roleid,allowview) values('%1$s',3, 0)", userId.replace("'","''"));
			DataAccessObject.ExecuteSQL(sqlList);
		}
		catch(SQLException e){
			LoggerFactory.Write(e);
			return false;
		}
		return true;
	}
	
	public static List<UserInfo> GetUserList()
	{
		return GetUserList(null);
	}
	
	public static List<UserInfo> GetUserList(String filter)
	{
		String sql  = "select id, name, descr from users where id!='10000000'";
		if(null!=filter && filter.length()>0)
		{
			sql += String.format(" and (id like '%%%1$s%%' or name like '%%%1$s%%')", filter.replace("'","''"));
		}
		String[][] list = DataAccessObject.GetArray(sql);
		if(list!=null)
		{
			ArrayList<UserInfo> userList = new ArrayList<UserInfo>();
			for(String[] user:list )
			{
				userList.add(new UserInfo(user[0], user[1], user[2]));
			}
			return userList;
		}
		return null;
	}
}
