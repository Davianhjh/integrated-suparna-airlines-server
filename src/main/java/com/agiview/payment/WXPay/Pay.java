package com.agiview.payment.WXPay;

import com.github.wxpay.sdk.WXPay;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.Map;

@Path("/pay")
public class Pay {
    @POST
    @Path("/unifiedorder")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, String> unifiedorder(@Context HttpHeaders hh, Map<String,String> map) {
        MyWXPayConfig config = null;
        try {
            config = new MyWXPayConfig();
            WXPay wxpay = new WXPay(config);
            Map<String, String> resp = wxpay.unifiedOrder(map);
            return resp;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @POST
    @Path("/orderquery")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, String> orderquery(@Context HttpHeaders hh, Map<String,String> map) {
        MyWXPayConfig config = null;
        try {
            config = new MyWXPayConfig();
            WXPay wxpay = new WXPay(config);
            Map<String, String> resp = wxpay.orderQuery(map);
            return resp;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
