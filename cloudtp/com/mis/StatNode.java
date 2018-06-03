package cloudtp.com.mis;

import java.util.ArrayList;

public class StatNode {

	private double _xCoord = 0;
	private double _yCoord = 0;
	private String _name ="";
	private String _tip = "";
	@SuppressWarnings("rawtypes")
	private ArrayList _entityList = new ArrayList();

	public StatNode() {
	}

	public StatNode(double xCoord, double yCoord) {
		_xCoord = xCoord;
		_yCoord = yCoord;
	}

	public StatNode(double xCoord, double yCoord, String name) {
		_xCoord = xCoord;
		_yCoord = yCoord;
		_name = name;
	}

	public double getXCoord() {

		return _xCoord;
	}

	public void setXCoord(double value) {
		_xCoord = value;
	}

	public double getYCoord() {

		return _yCoord;
	}

	public void setYCoord(double value) {
		_yCoord = value;
	}

	@SuppressWarnings("unchecked")
	public void AddEntity(Object entity) {
		_entityList.add(entity);
	}

	public Object[] getEntityList() {

		Object[] entityList = new Object[_entityList.size()];
		for (int i = 0; i < _entityList.size(); i++) {
			entityList[i] = _entityList.get(i);
		}
		return entityList;
	}

	public int getEntityNum() {
		return _entityList.size();
	}

	public String getName() {
		return _name;
	}

	public void setName(String value) {
		_name = value;
	}

	public String getTip() {
		return _tip;
	}

	public void setTip(String value) {
		_tip = value;
	}

}
