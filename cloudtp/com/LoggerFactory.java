package cloudtp.com;

import org.apache.commons.logging.*;

/*
 * ��־��,ȫ����־��������
 */
public class LoggerFactory{

	//��־��ʵ��
	 private static Log log = null;

	 /**
	  * ��¼һ���쳣
	  * @param e �쳣
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
	  * ��¼һ���쳣
	  * @param title �쳣��Ϣ����
	  * @param e �쳣
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
	  * ��¼һ����Ϣ��־
	  * @param message ��Ϣ�ַ���
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
	  * ��¼һ���쳣��Ϣ��־
	  * @param message ��Ϣ�ַ���
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
