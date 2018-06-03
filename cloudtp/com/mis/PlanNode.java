package cloudtp.com.mis;

public class PlanNode {

	      private int _type = -1;
	      private String _id = "";
	      private String _name = "";

	      public PlanNode(int type, String id, String name)
	      {
	    	  _type = type;
	         _id = id;
	         _name = name;
	      }

	      public int getType()
	      {
	         return _type;
	      }

	      public String getId()
	      {
	          return _id;
	      }

	      public String getName()
	      {
	            return _name;	         
	      }
	      
	      public String getHasSub()
	      {
	            return (_type != Keys.PlanTreeNodeType_Plan)?"true":"false";         
	      }
}

