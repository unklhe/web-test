package cloudtp.com;

/**
 * 工具类
 * @author Administrator
 *
 */
public class Util {
	/**
	 * 判断字符串变量值是否为空
	 * @param source
	 * @return boolean, 字符串变量值为空则返回true
	 */
	public static boolean IsStringNullOrEmpty(String source)
	{
		return (null==source || "".equals(source));
	}
	
	/**
	 * 将以整数表示的html表现格式转换为真实html格式
	 * @param fmt 格式，以整数在内存的各个位来代表格式
	 * @return html语法
	 */
	public static String[] FormatHTML(int fmt)
	{
		String[] style = new String[] {"","", ""};
		if( (fmt&32) ==0) //换行
			style[0] += "<br>";
		if( (fmt&64) >0) //单缩进
			style[1]+= "&nbsp;&nbsp;&nbsp;&nbsp;";
		if( (fmt&128) >0)//双缩进
			style[1] += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
		if( (fmt&256) >0)//3缩进
			style[1] += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
		if( (fmt&1) >0)//粗体
			style[2] += "font-weight: bold";
		if( (fmt&16) >0)//警告信息表现色彩
			style[2] += "color: Goldenrod;";
		if( (fmt&8) >0)//错误信息表现色彩
			style[2] += "color: red;";
		if( (fmt&4) >0)//大字体
			style[2] += "font-size: 16px;";
		if( (fmt&2) >0)//字体带下划线
			style[2] += "text-decoration: underline;";
		return style;
		
	}

}
