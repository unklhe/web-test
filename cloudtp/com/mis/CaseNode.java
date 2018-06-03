package cloudtp.com.mis;

public class CaseNode {

	      private int _category = -1;
	      private String _id = "";
	      private String _name = "";

	      public CaseNode(int category, String id, String name)
	      {
	         _category = category;
	         _id = id;
	         _name = name;
	      }

	      public int getCategory()
	      {
	         return _category;
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
	            return (_category != Keys.CaseTreeNodeType_Case)?"true":"false";         
	      }
}

