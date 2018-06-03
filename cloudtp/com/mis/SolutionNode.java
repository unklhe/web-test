package cloudtp.com.mis;

public class SolutionNode {

      private String _id = "";
      private String _name = "";
      private String _date = "";
      private String _ownerId = "";
      private String _ownerName = "";
      
      public SolutionNode(String id, String name, String date, String ownerName, String ownerId)
      {
         _id = id;
         _name = name;
         if(ownerName==null || ownerName.length()<1)
        	 _ownerName = ownerId;
         else
        	 _ownerName = ownerName;
         _date = date;
         _ownerId = ownerId;
      }

      public String getId()
      {
          return _id;
      }

      public String getName()
      {
            return _name;	         
      }
      
      public String getDate()
      {
          return _date;
      }

      public String getOwnerId()
      {
            return _ownerId;	         
      }
      
      public String getOwnerName()
      {
            return _ownerName;	         
      }
	      
}

