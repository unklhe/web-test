package cloudtp.com.mis;

import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import cloudtp.com.LoggerFactory;

public class MisService {
	public static List<CaseNode> QueryNodes(String userId,
			int parentNodeType, String parentNodeId, boolean expandAllChild) {
		List<CaseNode> nodeList = new ArrayList<CaseNode>();
		try {
			String[][] childNodes = null;
			if (parentNodeType == Keys.CaseTreeNodeType_SharedRoot) {
				childNodes = new String[5][];
				childNodes[0] = new String[] { "", "产品",
						Keys.CaseTreeNodeType_SharedProduct + "" };
				childNodes[1] = new String[] { "", "项目",
						Keys.CaseTreeNodeType_SharedProject + "" };
				childNodes[2] = new String[] { "", "业务",
						Keys.CaseTreeNodeType_SharedBusiness + "" };
				childNodes[3] = new String[] { "", "版本",
						Keys.CaseTreeNodeType_SharedVersion + "" };
				childNodes[4] = new String[] { "", "用例集",
						Keys.CaseTreeNodeType_SharedCaseset + "" };
			} else if (parentNodeType == Keys.CaseTreeNodeType_Root) {
				if (expandAllChild) {
					childNodes = CaseDAO.GetProductList();
				} else {
					childNodes = CaseDAO.GetProductList(userId);
				}
			} else if (parentNodeType == Keys.CaseTreeNodeType_Caseset) {
				childNodes = MisService.GetCaseList(parentNodeId);
				/*
				 * String[][] fileList =
				 * CaseDAO.GetCasesetAttachments(parentNodeId); if (fileList !=
				 * null) { for (int i = 0; i < fileList.length; i++) {
				 * nodeList.Add( new CaseNode(parentNode, fileList[i, 0],
				 * fileList[i, 1], Keys.CaseTreeNodeType_File)); } }
				 */
			} else if (parentNodeType == Keys.CaseTreeNodeType_SharedProduct
					|| parentNodeType == Keys.CaseTreeNodeType_SharedProject
					|| parentNodeType == Keys.CaseTreeNodeType_SharedBusiness
					|| parentNodeType == Keys.CaseTreeNodeType_SharedVersion) {
				childNodes = CaseDAO.GetMyNodeList(userId, parentNodeType - 10);
			} else if (parentNodeType == Keys.CaseTreeNodeType_SharedCaseset) {
				childNodes = CaseDAO.GetMyCasesetList(userId);
			} else if (parentNodeType != Keys.CaseTreeNodeType_CommonCasesets) {
				if (expandAllChild) {
					childNodes = CaseDAO.GetNodeList(Integer
							.parseInt(parentNodeId));
				} else {
					childNodes = CaseDAO.GetSharedNodesForUser(userId,
							Integer.parseInt(parentNodeId));
				}
			}
			if (parentNodeType == Keys.CaseTreeNodeType_Business) {
				nodeList.add(new CaseNode(Keys.CaseTreeNodeType_CommonCasesets,
						parentNodeId, "基本用例集"));
			}
			if (childNodes != null) {
				for (int i = 0; i < childNodes.length; i++) {
					nodeList.add(new CaseNode(Integer
							.parseInt(childNodes[i][2]), childNodes[i][0],
							childNodes[i][1]));
				}
			}
			if (parentNodeType == Keys.CaseTreeNodeType_Version
					|| parentNodeType == Keys.CaseTreeNodeType_CommonCasesets) {
				if (expandAllChild) {
					childNodes = CaseDAO.GetCasesetList(Integer
							.parseInt(parentNodeId));
				} else {
					childNodes = CaseDAO.GetSharedCasesetsForUser(userId,
							Integer.parseInt(parentNodeId));
				}
				if (childNodes != null) {
					for (int i = 0; i < childNodes.length; i++) {
						nodeList.add(new CaseNode(
								Keys.CaseTreeNodeType_Caseset,
								childNodes[i][0], childNodes[i][1]));
					}
				}
			}
		} catch (Exception ex) {
			LoggerFactory.Write(ex);
		}
		return nodeList;
	}

	public static ArrayList<StatNode> CountCases(int category, String id,
			Calendar from, Calendar to) {
		ArrayList<StatNode> nodeArrayList = new ArrayList<StatNode>();
		try {
			int count = 1;
			to.add(Calendar.DAY_OF_MONTH, 1);
			for (Calendar date = from; date.before(to); date.add(
					Calendar.DAY_OF_MONTH, 1), count++) {
				String[] caseInfo = CaseDAO.CountCases(category, id, date);
				StatNode caseNode = new StatNode(count,
						Double.parseDouble(caseInfo[0]),
						date.get(Calendar.MONTH) + "-"
								+ date.get(Calendar.DAY_OF_MONTH));
				// caseNode.DataContext = date;
				caseNode.setTip(caseInfo[1]);
				nodeArrayList.add(caseNode);
			}
		} catch (Exception ex) {
			LoggerFactory.Write(ex);
		}
		return nodeArrayList;
	}

	public static int CountCases(int category, String id, int year, int month) {
		try {
			return CaseDAO.CountCases(category, id, year, month);
		} catch (Exception ex) {
			LoggerFactory.Write(ex);
		}
		return 0;
	}

	public static int CountExecCases(int category, String id, int year,
			int month) {
		try {
			return CaseDAO.CountExecCases(category, id, year, month);
		} catch (Exception ex) {
			LoggerFactory.Write(ex);
		}
		return 0;
	}

	public static int CountExecBaseCases(int category, String id, int year,
			int month) {
		try {
			return CaseDAO.CountExecBaseCases(category, id, year, month);
		} catch (Exception ex) {
			LoggerFactory.Write(ex);
		}
		return 0;
	}

	public static int CountBaseCases(int category, String id, int year,
			int month) {
		try {
			return CaseDAO.CountBaseCases(category, id, year, month);
		} catch (Exception ex) {
			LoggerFactory.Write(ex);
		}
		return 0;
	}

	public static String[][] GetCaseList(String casesetIndex) {
		try {
			byte[] data = CaseDAO.DownloadCase(casesetIndex);

			if(null!=data)
			{
				ByteArrayInputStream caseStream = new ByteArrayInputStream(data);
				InputStreamReader caseReader = new InputStreamReader(caseStream,
						"gb2312");
				SAXReader reader = new SAXReader();
				//String s = new String(data);
				
				Document doc = reader.read(caseReader);
				caseReader.close();
				caseStream.close();
				Element root = doc.getRootElement();
				@SuppressWarnings("unchecked")
				List<Element> cases = (List<Element>) root.element("cases").elements("case");
	
				if (cases != null && cases.size() > 0) {
					String[][] caseList = new String[cases.size()][];
					int count = 0;
					for (Element ele : cases) {
						caseList[count] = new String[3];						
						caseList[count][0] = ele.attributeValue("index");
						caseList[count][1] = ele.attributeValue("name");
						caseList[count++][2] = String.valueOf(Keys.CaseTreeNodeType_Case);
					}
					return caseList;
				}
			}
		} catch (Exception ex) {
			LoggerFactory.Write(ex);
		}
		return null;
	}

	public static String[][] GetCaseExecutionLogs(String caseIndex) {
		try {
			return CaseDAO.GetCaseLogs(caseIndex);
		} catch (Exception ex) {
			LoggerFactory.Write(ex);
		}
		return null;
	}

	public static String[][] GetLogList(int taskId) {
		try {
			String[][] logList = TaskDAO.GetLogList(taskId);
			return logList;
		} catch (Exception Exception) {
			LoggerFactory.Write(Exception);
		}
		return null;
	}

	public static ArrayList<String[]> GetTaskLogContent(String execId,
			boolean filterNormal) {
		try {
			ArrayList<String[]> logList = new ArrayList<String[]>();
			String[][] tipList = TaskDAO.GetLogTips(execId);
			if (tipList != null) {
				for (int i = 0; i < tipList.length; i++) {
					if (tipList[i][2] != "4") {
						logList.add(new String[] { tipList[i][1],
								tipList[i][2], tipList[i][3] });
					} else {
						String[][] caseList = TaskDAO.GetLogCaseDetail(execId,
								Integer.parseInt(tipList[i][0]), filterNormal);
						if (caseList != null) {
							for (int j = 0; j < caseList.length; j++) {
								logList.add(new String[] { caseList[j][0],
										caseList[j][1], "    " + caseList[j][2] });
							}
						}
					}
				}
			}
			return logList;
		} catch (Exception ex) {
			LoggerFactory.Write(ex);
		}
		return null;
	}

	public static ArrayList<String[]> CountCaseExecutions(int category,
			String id, Calendar from, Calendar to) {
		ArrayList<String[]> nodeArrayList = new ArrayList<String[]>();
		try {
			to.add(Calendar.DAY_OF_MONTH, 1);
			for (Calendar date = from; date.before(to); date.add(
					Calendar.DAY_OF_MONTH, 1)) {
				int[] caseExecuteInfo = CaseDAO.CountExecutedCases(category,
						id, date);
				nodeArrayList.add(new String[] {
						String.format("%d-%02d-%02d", date.get(Calendar.YEAR),
								date.get(Calendar.MONTH),
								date.get(Calendar.DAY_OF_MONTH)),
						caseExecuteInfo[0] + "", caseExecuteInfo[1] + "",
						caseExecuteInfo[2] + "" });
			}
		} catch (Exception ex) {
			LoggerFactory.Write(ex);
		}
		return nodeArrayList;
	}

	public static boolean SaveMonthReport(Object[] reportParameter, String error) {
		try {
			CaseDAO.SaveMonthReport((String) reportParameter[0],
					(Integer) reportParameter[1], (Integer) reportParameter[2],
					(Integer) reportParameter[3], (Integer) reportParameter[4],
					(Integer) reportParameter[5], (Integer) reportParameter[6],
					(Integer) reportParameter[7]);
			error = null;
			return true;
		} catch (Exception ex) {
			LoggerFactory.Write(ex);
			error = "请联系系统管理员";
		}
		return false;
	}

	public static String[][] GetMonthReport(int year, String nodeId,
			int category) {
		String[][] list = CaseDAO.GetMonthReport(year, nodeId, category);
		if (list == null)
			return null;
		String[][] result = new String[list.length][11];
		for (int i = 0; i < list.length; i++) {
			result[i][0] = list[i][0];
			result[i][1] = list[i][1] + "-" + list[i][2];
			if (list[i][4] == Keys.CaseTreeNodeType_CommonCasesets + "")
				result[i][2] = list[i][5] + "\\基本用例集";
			else if (list[i][4] == Keys.CaseTreeNodeType_Caseset + "")
				result[i][2] = list[i][6];
			else
				result[i][2] = list[i][5];
			int caseNum = Integer.parseInt(list[i][7]);
			int baseCaseNum = Integer.parseInt(list[i][8]);
			result[i][3] = list[i][7];
			result[i][4] = list[i][8];
			result[i][5] = String.format("%.1f%%", baseCaseNum * 100.0
					/ caseNum);
			int caseExecNum = Integer.parseInt(list[i][9]);
			int baseCaseExecNum = Integer.parseInt(list[i][10]);
			result[i][6] = list[i][9];
			result[i][7] = list[i][10];
			if (caseExecNum == 0) {
				if (baseCaseExecNum == 0)
					result[i][8] = "0";
				else
					result[i][8] = "100%";
			} else
				result[i][8] = String.format("%.1f%%", baseCaseExecNum * 100.0
						/ caseExecNum);

			int commonCaseNum = caseNum - baseCaseNum;
			if (commonCaseNum > 0)
				result[i][9] = String.format("%.2f%%",
						((double) caseExecNum - baseCaseExecNum)
								/ commonCaseNum);
			else
				result[i][9] = "0";

			if (baseCaseNum > 0)
				result[i][10] = String.format("%.2f%%",
						((double) baseCaseExecNum / baseCaseNum));
			else
				result[i][10] = "0";
		}
		return result;
	}

	public static boolean MonthReportExists(int year, int month, String nodeId,
			int nodeCategory) {
		if (CaseDAO.GetMonthReportId(year, month, nodeId, nodeCategory) > 0)
			return true;
		return false;
	}

	 public static boolean SaveExecution( String ownerId, String solutionName, String content, StringBuilder sbIndex, StringBuilder sbResult)
     {
        return CaseDAO.SaveSolution(ownerId, solutionName, content,sbIndex, sbResult);
     }
	 
	 public static boolean DeleteExecution( String index, StringBuilder sbResult)
     {
        return CaseDAO.DeleteSolution(index, sbResult);
     }
	 
	 public static ArrayList<PlanNode> QueryPlans(String ownerId, int category, String id)
     {
		ArrayList<PlanNode> resultList = new ArrayList<PlanNode>();
		if(category == Keys.PlanTreeNodeType_MyOwnPlanFolder)
		{
	        String[] list =  TaskDAO.GetOwnerYearList(ownerId);       
	        if (list != null)
	        {
	           for (String year : list)
	           {
	              resultList.add(new PlanNode(Keys.PlanTreeNodeType_MyOwnPlanYear, year, String.format("%s年", year)));
	           }
	        }
		}
		else if (category == Keys.PlanTreeNodeType_MyOwnPlanYear) {
			String[] list = TaskDAO.GetOwnerMonthList(ownerId, id);
			if (list != null)
	        {
	           for (String month : list)
	           {
	        	   resultList.add(new PlanNode(Keys.PlanTreeNodeType_MyOwnPlanMonth, month, String.format("%d月份", Integer.parseInt(month.substring(5)))));
	           }
	        }
		}
		else if (category == Keys.PlanTreeNodeType_MyOwnPlanMonth) {
			String[][] list = TaskDAO.GetOwnerTaskList(ownerId, id);
			if (list != null)
			{
				if (list != null)
		        {
		           for (String[] task : list)
		           {
		              resultList.add(new PlanNode(Keys.PlanTreeNodeType_Plan, task[0], task[1]));
		           }
		        }
			}
		}
		else if(category == Keys.PlanTreeNodeType_MyPlanFolder)
		{
	        String[] list =  TaskDAO.GetScheduleYearList(ownerId);       
	        if (list != null)
	        {
	           for (String year : list)
	           {
	              resultList.add(new PlanNode(Keys.PlanTreeNodeType_MyPlanYear, year, String.format("%s年", year)));
	           }
	        }
		}
		else if (category == Keys.PlanTreeNodeType_MyPlanYear) {
			String[] list = TaskDAO.GetScheduleMonthList(ownerId, id);
			if (list != null)
	        {
	           for (String month : list)
	           {
	        	   resultList.add(new PlanNode(Keys.PlanTreeNodeType_MyPlanMonth, month, String.format("%d月份", Integer.parseInt(month.substring(5)))));
	           }
	        }
		}
		else if (category == Keys.PlanTreeNodeType_MyPlanMonth) {
			String[][] list = TaskDAO.GetScheduleTaskList(ownerId, id);
			if (list != null)
			{
				if (list != null)
		        {
		           for (String[] task : list)
		           {
		              resultList.add(new PlanNode(Keys.PlanTreeNodeType_Plan, task[0], task[1]));
		           }
		        }
			}
		}
        return resultList;
     }
}
