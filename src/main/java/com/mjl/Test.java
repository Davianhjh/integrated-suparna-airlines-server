package com.mjl;

import com.hujinhua.User;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.xml.ws.spi.http.HttpContext;
import java.util.HashMap;
import java.util.Map;

@Path("/mytest")
public class Test {
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String printTesting() {
        return "testing Jersy Restful API";
    }

    @GET
    @Path("/getUser")
    @Produces(MediaType.APPLICATION_JSON)
    public User printUser(@QueryParam("username") String username, @QueryParam("id") int i) {
        User user = new User();
        user.setId(123);
        user.setName("hdjas");
        return user;
    }

    @POST
    @Path("/postUser")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public int sendUser(@Context HttpHeaders hh, User user) {
        MultivaluedMap<String, String> header = hh.getRequestHeaders();
        String token = header.getFirst("agi-token");
        System.out.println(user.getId());
        System.out.println(user.getName());
        System.out.println(token);
        return 0;
    }

    @POST
    @Path("/map")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Map testMap(@Context HttpHeaders hh ,Map map) {
        Map res=new HashMap<String,String>();
        System.out.println(map.get("name"));
        res.put("r1",map.get("name"));
        return res;
    }

    @POST
    @Path("/hello")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String,String> test(){
        HashMap<String,String > res=new HashMap<String, String>();
        res.put("r1","r1");
        res.put("r2","r2");
        return res;
    }
}
