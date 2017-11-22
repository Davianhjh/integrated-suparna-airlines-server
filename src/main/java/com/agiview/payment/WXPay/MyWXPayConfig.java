package com.agiview.payment.WXPay;

import com.github.wxpay.sdk.WXPayConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class MyWXPayConfig implements WXPayConfig{

    private byte[] certData;

    public MyWXPayConfig() throws Exception{
        // TODO: 2017/11/3 证书路径，从商户后台获取
        String certPath = "/path/to/apiclient_cert.p12";
        File file = new File(certPath);
        InputStream certStream = new FileInputStream(file);
        this.certData = new byte[(int) file.length()];
        certStream.read(this.certData);
        certStream.close();
    }

    @Override
    public String getAppID() {
        return null;
    }

    @Override
    public String getMchID() {
        return null;
    }

    @Override
    public String getKey() {
        return null;
    }

    @Override
    public InputStream getCertStream() {
        return null;
    }

    @Override
    public int getHttpConnectTimeoutMs() {
        return 8000;
    }

    @Override
    public int getHttpReadTimeoutMs() {
        return 10000;
    }
}
