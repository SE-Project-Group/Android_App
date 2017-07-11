package com.example.android.android_app.Class;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;

/**
 * Created by thor on 2017/7/10.
 */

public class OssInit {

    private static final String stsServer = "http://10.182.29.246:7080";
    private static final String endpoint = "http://oss-cn-shanghai.aliyuncs.com";
    private static final String bucket = "sjtutest";

    //初始化一个OssService用来上传下载
    public OssService initOSS(Context context, Handler handler, int success_msg) {
        //如果希望直接使用accessKey来访问的时候，可以直接使用OSSPlainTextAKSKCredentialProvider来鉴权。
        //OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider(accessKeyId, accessKeySecret);

        OSSCredentialProvider credentialProvider;
        //使用自己的获取STSToken的类
        credentialProvider = new STSGetter(stsServer);

        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(5); // 最大并发请求书，默认5个
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
        OSS oss = new OSSClient(context, endpoint, credentialProvider, conf);
        return new OssService(oss, bucket, handler, success_msg);
    }
}
