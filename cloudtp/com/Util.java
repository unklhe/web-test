package cloudtp.com;

/**
 * ������
 * @author Administrator
 *
 */
public class Util {
	/**
	 * �ж��ַ�������ֵ�Ƿ�Ϊ��
	 * @param source
	 * @return boolean, �ַ�������ֵΪ���򷵻�true
	 */
	public static boolean IsStringNullOrEmpty(String source)
	{
		return (null==source || "".equals(source));
	}
	
	/**
	 * ����������ʾ��html���ָ�ʽת��Ϊ��ʵhtml��ʽ
	 * @param fmt ��ʽ�����������ڴ�ĸ���λ�������ʽ
	 * @return html�﷨
	 */
	public static String[] FormatHTML(int fmt)
	{
		String[] style = new String[] {"","", ""};
		if( (fmt&32) ==0) //����
			style[0] += "<br>";
		if( (fmt&64) >0) //������
			style[1]+= "&nbsp;&nbsp;&nbsp;&nbsp;";
		if( (fmt&128) >0)//˫����
			style[1] += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
		if( (fmt&256) >0)//3����
			style[1] += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
		if( (fmt&1) >0)//����
			style[2] += "font-weight: bold";
		if( (fmt&16) >0)//������Ϣ����ɫ��
			style[2] += "color: Goldenrod;";
		if( (fmt&8) >0)//������Ϣ����ɫ��
			style[2] += "color: red;";
		if( (fmt&4) >0)//������
			style[2] += "font-size: 16px;";
		if( (fmt&2) >0)//������»���
			style[2] += "text-decoration: underline;";
		return style;
		
	}

}
