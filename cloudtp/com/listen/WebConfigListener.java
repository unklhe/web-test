package cloudtp.com.listen;

import java.io.*;
import java.net.URISyntaxException;
import java.util.*;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import cloudtp.com.Constants;
import cloudtp.com.LoggerFactory;
import cloudtp.com.PropertiesUtil;
import cloudtp.com.TaskSender;

public class WebConfigListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
 
		
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
	
		PropertiesUtil.DBProperties = new Properties();
		PropertiesUtil.ActiveMQProperties = new Properties();
		PropertiesUtil.TechPlusProperties = new Properties();
		
		try
		{
			String path = (getClass().getClassLoader().getResource("").toURI()).getPath();
			FileInputStream fis = new FileInputStream(path + "cloud_cfg.xml");
			SAXReader reader = new SAXReader();
			Document doc = reader.read(fis);
			fis.close();
			Element root = doc.getRootElement();				
			Element dbPool = root.element("DBPools");			
			String driveClassName = dbPool.element("driverClassName").getTextTrim();
			String url = 	dbPool.element("connection").element("url").getTextTrim();
			String user = 	dbPool.element("connection").element("user").getTextTrim();
			String passsword = 	dbPool.element("connection").element("password").getTextTrim();
			PropertiesUtil.DBProperties.put(Constants.DBDriverClassName, driveClassName);
			PropertiesUtil.DBProperties.put(Constants.DBUrl, url);
			PropertiesUtil.DBProperties.put(Constants.DBUser, user);
			PropertiesUtil.DBProperties.put(Constants.DBPassword, passsword);			
		
			
			Element activeMQ = root.element("ActiveMQ");
			String serverUrl = activeMQ.element("url").getTextTrim();			
			String myName = activeMQ.element("myname").getTextTrim();
			String messageReceiver = activeMQ.element("messageReceiver").getTextTrim();
			String timeOut = activeMQ.element("timeout").getTextTrim();
			
			PropertiesUtil.ActiveMQProperties.put(Constants.ActiveMQUrl, serverUrl);
			PropertiesUtil.ActiveMQProperties.put(Constants.ActiveMQName, myName);			
			PropertiesUtil.ActiveMQProperties.put(Constants.ActiveMQTaskDealer, messageReceiver);
			PropertiesUtil.ActiveMQProperties.put(Constants.ActiveMQTimeout, Integer.parseInt(timeOut));
			
			
			Element techPlus = root.element("techPlus");
			String rpcUrl = techPlus.element("rpcUrl").getTextTrim();
			PropertiesUtil.TechPlusProperties.put(Constants.TechPlusRpcUrl, rpcUrl);
			
			TaskSender.init();
			
		}		
		catch(URISyntaxException e)
		{
			LoggerFactory.Write(e);
		}
		catch(IOException e)
		{
			LoggerFactory.Write(e);
		}
		catch(DocumentException e)
		{
			LoggerFactory.Write(e);
		}
				
	}

}
