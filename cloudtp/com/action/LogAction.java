package cloudtp.com.action;

import java.util.ArrayList;
import java.util.List;

import com.opensymphony.xwork2.ActionSupport;

import cloudtp.com.Util;
import cloudtp.com.mis.ExecLogNode;
import cloudtp.com.mis.Log;

public class LogAction extends ActionSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4922753511944822338L;

	private String _execId;
	private String _logId;
	private List<ExecLogNode> _sluList;
	private List<String[]> _logList;

	public String execute() {
		_sluList = Log.GetSolutionList(_execId);
		return SUCCESS;
	}

	public String view() {
		_sluList = Log.GetSolutionList(_execId);
		_logList = new ArrayList<String[]>();
		String[][] tipList = Log.GetLogTips(_logId);
		if (tipList != null) {
			for (int i = 0; i < tipList.length; i++) {
				if (!"4".equals(tipList[i][2])) {
					int type1 = Integer.parseInt(tipList[i][1]);
					int type2 = Integer.parseInt(tipList[i][2]);					
					if(type2==2)
						type1 = type1 | 16;
					else if (type2==3)
						type1 = type1 | 8;
					
					String[] line = new String[4];
					String[] format = Util.FormatHTML(type1);
					line[0] = format[0];
					line[1] = format[1];
					line[2] = format[2];
					while (tipList[i][2] != null && tipList[i][2] != ""
							&& tipList[i][2].subSequence(0, 1) == "") {
						line[1] += "&nbsp;";
						tipList[i][3] = tipList[i][3].substring(1);
					}
					line[3] = tipList[i][3];

					_logList.add(line);
				} else {

					String[][] caseList = Log.GetLogCaseDetail(_logId,
							Integer.parseInt(tipList[i][0]), false);
					if (caseList != null) {
						for (int j = 0; j < caseList.length; j++) {
							int type1 = Integer.parseInt(caseList[j][0]);
							int type2 = Integer.parseInt(caseList[j][1]);					
							if(type2==2)
								type1 = type1 | 16;
							else if (type2==3)
								type1 = type1 | 8;
							
							String[] line = new String[4];
							String[] format = Util.FormatHTML(type1);							
							line[0] = format[0];
							line[1] = format[1];
							line[2] = format[2];
							line[1] += "&nbsp;&nbsp;&nbsp;&nbsp;";
							while (caseList[j][2] != null
									&& caseList[j][2] != ""
									&& caseList[j][2].subSequence(0, 1) == " ") {
								line[1] += "&nbsp;";
								caseList[j][2] = caseList[j][2].substring(1);
							}
							line[3] = caseList[j][2];
							_logList.add(line);
						}
					}
				}
			}
		}
		return SUCCESS;
	}

	public void setExecId(String execId) {
		_execId = execId;
	}

	public String getExecId() {
		return _execId;
	}

	public void setLogId(String logId) {
		_logId = logId;
	}

	public List<ExecLogNode> getSolutionList() {
		return _sluList;
	}

	public List<String[]> getLogList() {
		return _logList;
	}

	public String getLogId() {
		return _logId;
	}
}
