package cloudtp.com.mis;

public interface Keys {
	public String DatabaseConnectionKey = "DatabaseConnectionString";
	public String GlobalDaoKeys = "GlobalDaoKey";
	public String UserInfoKey = "UserInfoKey";
	public String UserIdKey = "id";
	public String UserPasswdKey = "pwd";
	public String RequestUrlKey = "RequestUrlKey";
	public String UserCookieKey = "UserCookieKey";

	public int CaseTreeNodeType_Root = 1;
	public int CaseTreeNodeType_Product = 2;
	public int CaseTreeNodeType_Project = 3;
	public int CaseTreeNodeType_Business = 4;
	public int CaseTreeNodeType_Version = 5;
	public int CaseTreeNodeType_Caseset = 6;
	public int CaseTreeNodeType_CommonCasesets = 7;
	public int CaseTreeNodeType_File = 8;
	public int CaseTreeNodeType_Case = 9;


	public int CaseTreeNodeType_SharedRoot = 11;
	public int CaseTreeNodeType_SharedProduct = 12;
	public int CaseTreeNodeType_SharedProject = 13;
	public int CaseTreeNodeType_SharedBusiness = 14;
	public int CaseTreeNodeType_SharedVersion = 15;
	public int CaseTreeNodeType_SharedCaseset = 16;

	public int CaseShareLevel_Inherited = 0;
	public int CaseShareLevel_Public = 1;
	public int CaseShareLevel_Protect = 2;
	public int CaseShareLevel_Private = 3;

	public int Role_Admin = 1;
	public int Role_Manager = 2;
	public int Role_Common = 3;

	public int PlanTreeNodeType_MyOwnPlanFolder = 10;
	public int PlanTreeNodeType_MyOwnPlanYear = 11;
	public int PlanTreeNodeType_MyOwnPlanMonth = 12;
	public int PlanTreeNodeType_MyPlanFolder = 20;
	public int PlanTreeNodeType_MyPlanYear = 21;
	public int PlanTreeNodeType_MyPlanMonth = 22;
	public int PlanTreeNodeType_MyExecutedPlanFolder = 30;
	public int PlanTreeNodeType_MyExecutedPlanYear = 31;
	public int PlanTreeNodeType_MyExecutedPlanMonth = 32;
	public int PlanTreeNodeType_Plan = 1;
	public int PlanTreeNodeType_Solution = 2;
}
