package ca.appengine.project.test;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class QueryProcessorServlet extends HttpServlet {
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();
        
        String guestbookName = req.getParameter("guestbookName");
        if (guestbookName == null) {
            guestbookName = "default";
        }        
        String reqMarkerID = req.getParameter("markerID");
                
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Key guestbookKey = KeyFactory.createKey("UBCEECE4172014MapGuestbook", guestbookName);
        
        // Run an ancestor query to ensure we see the most up-to-date
        // view of the Greetings belonging to the selected Guestbook.      
        Query query = new Query("UBCEECE4172014MapGreeting", guestbookKey);
        query.addSort("date", Query.SortDirection.DESCENDING);
        
        // Add a filter that checks the markerID
        Filter markerFilter = new FilterPredicate("markerID", FilterOperator.EQUAL, reqMarkerID);
        query.setFilter(markerFilter);
        
        List<Entity> greetings = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(10));
        
        String responseHTMLString = "";
        
        if (greetings.isEmpty()) {
        	System.out.println(guestbookName + " has no messages");
        	responseHTMLString = "<div>'" + guestbookName + "' has no message for you.</div>";
        } else {
        	 for (Entity greeting : greetings) {
        		 String postMsg = greeting.getProperty("postMsg").toString();
        		 String markerID = greeting.getProperty("markerID").toString();
        		 String date = greeting.getProperty("date").toString();
        		 
        		 
        		 // Test output
        		 System.out.println("reqMakerID: "+ reqMarkerID + " markerID: " 
        		 + markerID + " postMag: " + postMsg);
        		 
        		        		    		 
        		 responseHTMLString+="<div>";
        		 if (greeting.getProperty("user") == null) {
        			 responseHTMLString+="<b>Anonymous</b>";
        		 } else {
        			 String nickname = greeting.getProperty("user").toString().split("@")[0];        			 
        			 responseHTMLString+="<b>"+nickname+"</b>"; //greeting.getProperty("user").toString();        			        			 
        		 } 
        		 responseHTMLString+="("+ date +")";
        		 responseHTMLString+=": "; 
        		 responseHTMLString+=greeting.getProperty("postMsg").toString();
        		 responseHTMLString+="</div>";        		 
        	 }
        }
       
        resp.setContentType("text/html");
        //resp.setCharacterEncoding("UTF-8");
            
        // Test
        //String htmlString = "<div>" + responseHTMLString + "</div>";
        //resp.getWriter().println(htmlString);
        // Test
            
        resp.getWriter().println(responseHTMLString);        
	}    
}
