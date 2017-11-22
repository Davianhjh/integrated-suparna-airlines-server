package com.hujinhua;

import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

@Path("/test")  
public class RestfulTest {  
    @GET  
    @Produces(MediaType.TEXT_PLAIN)  
    public String printTesting() {  
        return "testing Jersy Restful API";  
    }  
  
    @GET  
    @Path("/getUser")  
    @Produces(MediaType.APPLICATION_JSON)  
    public Response printUser(@QueryParam("username") String username, @QueryParam("id") int i) {  
        User user = new User();  
        user.setId(i);  
        user.setName(username);  
    	Response response = Response.status(200).
                entity(user).
                header("Access-Control-Allow-Origin", "*").build();
        return response;  
    }
    
    @POST
    @Path("/postUser")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public int sendUser(@Context HttpHeaders hh, User user) {
    	MultivaluedMap<String, String> header = hh.getRequestHeaders();
    	String token = header.getFirst("agi-token");
    	Map<String,Cookie> tmp = hh.getCookies();
    	for(Cookie item : tmp.values()) {
    		System.out.println(item.getValue());
    	}
    	System.out.println(user.getId());
    	System.out.println(user.getName());
    	System.out.println(token);
    	return 0;
    }
}  
