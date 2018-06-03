package cloudtp.com.filter;

import java.io.IOException;  

import javax.servlet.Filter;  
import javax.servlet.FilterChain;  
import javax.servlet.FilterConfig;  
import javax.servlet.ServletException;  
import javax.servlet.ServletRequest;  
import javax.servlet.ServletResponse;  
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;  
import javax.servlet.http.HttpServletResponse;  
import javax.servlet.http.HttpSession;  

import org.apache.struts2.ServletActionContext;

import cloudtp.com.Constants;
import cloudtp.com.user.User;
  
public class SessionFilter implements Filter {  
  
    public void destroy() {  
    }  
  
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain fc)  
            throws IOException, ServletException {  
        HttpServletRequest request = (HttpServletRequest) req;  
        HttpServletResponse response = (HttpServletResponse) res;  
        String url = request.getRequestURI();
        if("/cloudtp/login.jsp".equalsIgnoreCase(url))
        {
        	response.sendRedirect(request.getContextPath() + "/login.action");
        	return;
        }
        HttpSession session = request.getSession(); 
        if(session.getAttribute(":Initialized")==null)
        {
        	session.setAttribute(":Initialized", true);
        	Cookie[] cookieList = ServletActionContext.getRequest().getCookies();
    		if(cookieList!=null&&cookieList.length!=0)
    		 {
    			String userId = "";
    			String passwd = "";
    		     for(int i=0;i<cookieList.length;i++)
    		     {
    		         String keyname=  cookieList[i].getName();
    		         String value=  cookieList[i].getValue();
    		         if("userId".equals(keyname))
    		        	 userId = value;
    		         if("pwd".equals(keyname))
    		        	 passwd = value;
    		      }
    		     if(!"".equals(userId) && !"".equals(passwd) )
    		     {
    		    	 if(User.ValidateUser(userId, passwd))
    		    	 {
    			    	 session.setAttribute(Constants.UserInfo, User.GetUserInfo(userId));
    		    	 }
    		     }
    		 }		        	
        }
        if (null == session.getAttribute(Constants.UserInfo)) {  
            response.sendRedirect(request.getContextPath() + "/login.jsp");
        }  
        else
        	fc.doFilter(request, response);  
  
    }  
  
    public void init(FilterConfig arg0) throws ServletException {  
    }  
  
}  
