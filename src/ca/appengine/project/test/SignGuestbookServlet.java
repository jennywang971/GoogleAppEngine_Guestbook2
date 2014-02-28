package ca.appengine.project.test;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SignGuestbookServlet extends HttpServlet {
    //@Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
                throws IOException {
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();

        // We have one entity group per Guestbook with all Greetings residing
        // in the same entity group as the Guestbook to which they belong.
        // This lets us run a transactional ancestor query to retrieve all
        // Greetings for a given Guestbook.  However, the write rate to each
        // Guestbook should be limited to ~1/second.
        
        String guestbookName = req.getParameter("guestbookName");
        String postMsg = req.getParameter("postMsg");
        String markerID = req.getParameter("markerID");
       
        Key guestbookKey = KeyFactory.createKey("UBCEECE4172014MapGuestbook", guestbookName);
        Date date = new Date();
        Entity greeting = new Entity("UBCEECE4172014MapGreeting", guestbookKey);
        
        greeting.setProperty("user", user);
        greeting.setProperty("date", date);
        greeting.setProperty("postMsg", postMsg);
        greeting.setProperty("markerID", markerID);
       
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(greeting);	
        
        // Test
        String htmlString = "<div>" + user + " " + markerID + " " + guestbookName + " " + postMsg + "</div>";      
        System.out.println(htmlString);  
        //resp.setContentType("text/html");
        //resp.getWriter().println(htmlString);
        // Test
        
        resp.sendRedirect("/queryprocessor/?markerID="+markerID+"&guestbookName="+guestbookName);
    }
}