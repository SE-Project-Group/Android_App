package com.example.android.android_app.Class;

import com.alibaba.sdk.android.oss.common.auth.OSSFederationToken;
import com.aliyun.oss.OSSClient;

import java.net.URL;
import java.util.Date;

import static android.R.attr.key;

/**
 * Created by thor on 2017/7/14.
 */

public class SignedUrlFactory {
    // STS server
    private static final String stsServer = "http://192.168.1.104:7080";

    // ak, aks, st, ep, bk
    private String accessKeyId = "<accessKeyId>";
    private String accessKeySecret = "<accessKeySecret>";
    private String securityToken = "<securityToken>";
    // do not use STS for temperate
    private String endpoint = "http://oss-cn-hangzhou.aliyuncs.com";
    private String bucket = "<bucket>";

    private static final int expir_time  = 900; // 15min


    public String getSignedUrl(String object_key){
        getToken();

        OSSClient client = new OSSClient(endpoint, accessKeyId, accessKeySecret, securityToken);
        // set expiration time to 15 min
        Date expiration = new Date(new Date().getTime() + expir_time);
        // generate url
        URL url = client.generatePresignedUrl(bucket, object_key, expiration);
        return url.toString();
    }

    private void getToken(){
        STSGetter stsGetter = new STSGetter(stsServer);
        OSSFederationToken ossFederationToken = stsGetter.getFederationToken();
        accessKeyId = ossFederationToken.getTempAK();
        accessKeySecret = ossFederationToken.getTempSK();
        securityToken = ossFederationToken.getSecurityToken();
    }
}
