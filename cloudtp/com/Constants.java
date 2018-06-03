package cloudtp.com;

/**
 * 常量定义
 * @author 何涛
 * 
 */
public interface Constants {
	
	//结果类型定义
	public int Result_Info = 1;
	public int Result_Error = 1;
	public int Result_Warning = 1;
	
	//系统注册变量Key值定义
	public String Result = "ResultKey";
	public String UserInfo = "userInfoKey";
	
	//Db 连接参数key
	public String DBUrl = "DbUrlKey";
	public String DBUser = "DbUserKey";
	public String DBPassword = "DbPasswordKey";
	public String DBDriverClassName = "DBDriverClassNameKey";
	
	//消息中间件参数定义
	public String ActiveMQUrl = "ActiveMQUrlKey";
	public String ActiveMQName = "ActiveMQNameKey";
	public String ActiveMQTaskDealer = "ActiveMQTaskDealerKey";
	public String ActiveMQTimeout = "ActiveMQTimeoutKey";
	
	//tech+ parameters
	public String TechPlusRpcUrl = "TechPlusRpcUrlKey";
	
	
	
}
