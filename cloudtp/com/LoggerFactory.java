package cloudtp.com;

import org.apache.commons.logging.*;

/*
 * 日志类,全局日志处理功能类
 */
public class LoggerFactory{

	//日志类实例
	 private static Log log = null;

	 /**
	  * 记录一个异常
	  * @param e 异常
	  */
	 public static void Write(Exception e)
	 {
		 if( null == log)
        {
            log = LogFactory.getLog("cloudtp.log");
        }
		 log.error(e.getMessage());
	 }
    
	 /**
	  * 记录一个异常
	  * @param title 异常消息标题
	  * @param e 异常
	  */
	 public static void Write(String title, Exception e)
	 {
		 if( null == log)
        {
            log = LogFactory.getLog("cloudtp.log");
        }
		 log.error(title + "\r\n" + e.getMessage());
	 }
	 
	 
	 /**
	  * 记录一个消息日志
	  * @param message 消息字符串
	  */
	 public static void Write(String message)
	 {
		 if( null == log)
        {
            log = LogFactory.getLog("cloudtp.log");
        }
		 log.info(message);
	 }
	 
	 /**
	  * 记录一个异常消息日志
	  * @param message 消息字符串
	  */
	 public static void WriteError(String message)
	 {
		 if( null == log)
        {
            log = LogFactory.getLog("cloudtp.log");
        }
		 log.error(message);
	 }
}
