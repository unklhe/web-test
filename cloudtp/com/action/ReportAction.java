package cloudtp.com.action;

import java.io.File;
import java.util.List;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;

import cloudtp.com.Constants;
import cloudtp.com.cloud.CloudDAO;
import cloudtp.com.user.UserInfo;

public class ReportAction extends ActionSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3914143301362433957L;

	private String _reportId;
	private String _toolId;
	private String _reportName;
	private String _reportDate;
	private String _reportDescr;
	private String _removedFiles ;
	private List<File> _files;                // 上传文件列表
	private List<String> _fileFileName;        // 上传文件名
	
	public String execute()
	{
		UserInfo userInfo = (UserInfo)ServletActionContext.getRequest().getSession().getAttribute(Constants.UserInfo);
		
		_reportId = CloudDAO.saveReport(userInfo.getUserId(), _toolId, _reportId, _reportName, _reportDate, _reportDescr, _removedFiles, _files, _fileFileName);		
		return SUCCESS;
	}
	
	public void setToolId(String value) {
		_toolId = value;
	}
	
	public void setReportId(String value) {
		_reportId = value;
	}

	public void setReportName(String reportName) {
		_reportName = reportName;
	}

	public void setReportDate(String value) {
		_reportDate = value;
	}

	public void setReportDescr(String value) {
		_reportDescr = value;
	}
	
	public void setRemovedFiles(String value) {
		_removedFiles = value;
	}

	public void setFile(List<File> file) {
		_files = file;
	}
	
	public void setFileFileName(List<String> fileFileName) {	
	     _fileFileName = fileFileName;	
	}
	
	public String getReportId()
	{
		return _reportId;
	}
	
	public String getToolId() {
		return _toolId;
	}

	public String getReportName() {
		return _reportName;
	}

	public String getReportDate() {
		return _reportDate;
	}

	public String getReportDescr() {
		return _reportDescr;
	}

	public String[] getFiles() {
		return CloudDAO.getReportFiles(_reportId);
	}
	


}
