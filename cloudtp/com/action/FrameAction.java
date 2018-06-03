package cloudtp.com.action;

import com.opensymphony.xwork2.ActionSupport;

public class FrameAction extends ActionSupport{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1587027172402202435L;

	
	private String _destination = "success";
	public String execute() throws Exception
	{
	     return _destination;
	}
	public void setDestination(String destination)
	{
		_destination = destination;
	}
}
