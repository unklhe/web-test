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
 * Webserver和TaskCenter的消息传输类
 * 
 * @author Hetao
 * 
 */
public class TaskSender {

	// ActiveMQ的消息处理对象定义
	public static ActiveMQConnectionFactory ConnectionFactory;
	public static Connection Conn;
	public static ActiveMQSession MQSession;
	//消息接收队列
	public static Queue MyQueue;
	//消息发送队列
	public static Queue TaskQueue;

	/**
	 * 初始化消息处理对象, 注意：只能调用一次
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
	 * 消息发送函数, 消息封装成TextMessage进行发送
	 * 
	 * @param taskMsg
	 *            消息字符串
	 * @param guid
	 *            JMSGroupId,消息和发送线程的匹配标志,将在消息体中发送该标志给消费者
	 * @return 发送结果标志, true 成功，false 失败
	 * @throws JMSException
	 *             异常抛出
	 */
	public static boolean send(String taskMsg, String guid) throws JMSException {
		
		//创建MyQueue的消费者,并只处理JMSXGroupID为guid的消息
		//生产者在生产时应使用该JMSXGroupID已使此处消费者能唯一获取
		MessageConsumer consumer = TaskSender.MQSession.createConsumer(
				TaskSender.MyQueue, String.format("JMSXGroupID='%s'", guid));
		//指定消费者对生产的处理程序
		TaskMessageListener listener = new TaskMessageListener();
		consumer.setMessageListener(listener);

		//创建消息, 指定消息回应位置
		TextMessage message = TaskSender.MQSession.createTextMessage(taskMsg);
		message.setJMSReplyTo(TaskSender.MyQueue);

		//创建生产者,并生产消息到TaskQueue
		MessageProducer producer = TaskSender.MQSession
				.createProducer(TaskSender.TaskQueue);
		producer.send(message);
		producer.close();

		//循环等待消息响应处理结束
		int count = 0;
		//超时定义
		int timeOut = 60;
		timeOut = (Integer) PropertiesUtil.ActiveMQProperties
				.get(Constants.ActiveMQTimeout);
		int interval = 10;
		timeOut = timeOut * 1000 / interval;
		while (true) {
			//处理结束或超时时结束等待
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

		//超时日志，管理员应对此日志进行系统检查找出异常原因恢复系统
		if (!listener.isDone() && count > timeOut) {
			LoggerFactory.WriteError("向任务服务器发送任务响应超时.");
		}
		return listener.isDone();
	}

	/**
	 * 想指定JMSXGroupID的消费者发送一个ok信息
	 * @param guid 消费者的JMSXGroupID
	 * @throws JMSException 抛出异常
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
 * 消息处理类
 * @author Administrator
 *
 */
class TaskMessageListener implements MessageListener {
	private boolean _isDone = false;

	/**
	 * 重写消息接收函数
	 * @param msgIn 传入消息
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
				//任务生产者应该受到<taskConfirm>received</taskConfirm>
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
	 * 获取消息响应处理结果
	 * @return boolean值, 消息响应处理结果, true 处理成功
	 */
	public boolean isDone() {
		return _isDone;
	}
}