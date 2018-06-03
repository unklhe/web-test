package cloudtp.com;

import java.io.ByteArrayInputStream;

import javax.jms.*;
import javax.jms.Message;

import org.apache.activemq.*;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * Webserver��TaskCenter����Ϣ������
 * 
 * @author Hetao
 * 
 */
public class TaskSender {

	// ActiveMQ����Ϣ���������
	public static ActiveMQConnectionFactory ConnectionFactory;
	public static Connection Conn;
	public static ActiveMQSession MQSession;
	//��Ϣ���ն���
	public static Queue MyQueue;
	//��Ϣ���Ͷ���
	public static Queue TaskQueue;

	/**
	 * ��ʼ����Ϣ�������, ע�⣺ֻ�ܵ���һ��
	 * 
	 */
	public static void init() {
		TaskSender.ConnectionFactory = new ActiveMQConnectionFactory(
				PropertiesUtil.ActiveMQProperties
						.getProperty(Constants.ActiveMQUrl));
		try {
			TaskSender.Conn = TaskSender.ConnectionFactory.createConnection();
			TaskSender.Conn.start();
			TaskSender.MQSession = (ActiveMQSession) TaskSender.Conn
					.createSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);
			TaskSender.MyQueue = TaskSender.MQSession
					.createQueue(PropertiesUtil.ActiveMQProperties
							.getProperty(Constants.ActiveMQName));
			TaskSender.TaskQueue = TaskSender.MQSession
					.createQueue(PropertiesUtil.ActiveMQProperties
							.getProperty(Constants.ActiveMQTaskDealer));
		} catch (JMSException e) {
			LoggerFactory.Write(e);
		}
	}

	/**
	 * ��Ϣ���ͺ���, ��Ϣ��װ��TextMessage���з���
	 * 
	 * @param taskMsg
	 *            ��Ϣ�ַ���
	 * @param guid
	 *            JMSGroupId,��Ϣ�ͷ����̵߳�ƥ���־,������Ϣ���з��͸ñ�־��������
	 * @return ���ͽ����־, true �ɹ���false ʧ��
	 * @throws JMSException
	 *             �쳣�׳�
	 */
	public static boolean send(String taskMsg, String guid) throws JMSException {
		
		//����MyQueue��������,��ֻ����JMSXGroupIDΪguid����Ϣ
		//������������ʱӦʹ�ø�JMSXGroupID��ʹ�˴���������Ψһ��ȡ
		MessageConsumer consumer = TaskSender.MQSession.createConsumer(
				TaskSender.MyQueue, String.format("JMSXGroupID='%s'", guid));
		//ָ�������߶������Ĵ������
		TaskMessageListener listener = new TaskMessageListener();
		consumer.setMessageListener(listener);

		//������Ϣ, ָ����Ϣ��Ӧλ��
		TextMessage message = TaskSender.MQSession.createTextMessage(taskMsg);
		message.setJMSReplyTo(TaskSender.MyQueue);

		//����������,��������Ϣ��TaskQueue
		MessageProducer producer = TaskSender.MQSession
				.createProducer(TaskSender.TaskQueue);
		producer.send(message);
		producer.close();

		//ѭ���ȴ���Ϣ��Ӧ�������
		int count = 0;
		//��ʱ����
		int timeOut = 60;
		timeOut = (Integer) PropertiesUtil.ActiveMQProperties
				.get(Constants.ActiveMQTimeout);
		int interval = 10;
		timeOut = timeOut * 1000 / interval;
		while (true) {
			//���������ʱʱ�����ȴ�
			if (listener.isDone() || count > timeOut)
				break;
			try {
				Thread.sleep(interval);
			} catch (InterruptedException e) {
				LoggerFactory.Write(e);
			}
			count++;
		}
		consumer.close();

		//��ʱ��־������ԱӦ�Դ���־����ϵͳ����ҳ��쳣ԭ��ָ�ϵͳ
		if (!listener.isDone() && count > timeOut) {
			LoggerFactory.WriteError("���������������������Ӧ��ʱ.");
		}
		return listener.isDone();
	}

	/**
	 * ��ָ��JMSXGroupID�������߷���һ��ok��Ϣ
	 * @param guid �����ߵ�JMSXGroupID
	 * @throws JMSException �׳��쳣
	 */
	public static void sendOk(String guid) throws JMSException {
		TextMessage confirmMsg = TaskSender.MQSession
				.createTextMessage("<taskConfirm>ok</taskConfirm>");
		confirmMsg.setStringProperty("JMSXGroupID", guid);
		confirmMsg.setJMSReplyTo(TaskSender.MyQueue);

		MessageProducer producer = TaskSender.MQSession
				.createProducer(TaskSender.TaskQueue);
		producer.send(confirmMsg);
	}

}

/**
 * ��Ϣ������
 * @author Administrator
 *
 */
class TaskMessageListener implements MessageListener {
	private boolean _isDone = false;

	/**
	 * ��д��Ϣ���պ���
	 * @param msgIn ������Ϣ
	 */
	@Override
	public void onMessage(Message msgIn) {
		if (msgIn instanceof TextMessage) {
			TextMessage msg = (TextMessage) msgIn;
			try {
				String msgText = msg.getText();
				SAXReader reader = new SAXReader();
				Document doc = reader.read(new ByteArrayInputStream(msgText
						.getBytes()));
				Element root = doc.getRootElement();
				//����������Ӧ���ܵ�<taskConfirm>received</taskConfirm>
				if ("taskConfirm".equalsIgnoreCase(root.getName())
						&& "received".equalsIgnoreCase(root.getText())) {
					_isDone = true;
				}
			} catch (DocumentException e) {
				LoggerFactory.Write(e);

			} catch (JMSException e) {
				LoggerFactory.Write(e);
			}
		}
	}

	/**
	 * ��ȡ��Ϣ��Ӧ������
	 * @return booleanֵ, ��Ϣ��Ӧ������, true ����ɹ�
	 */
	public boolean isDone() {
		return _isDone;
	}
}