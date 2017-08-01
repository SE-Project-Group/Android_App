package com.example.android.testaviary;

import android.app.Application;

import com.adobe.creativesdk.foundation.AdobeCSDKFoundation;
import com.adobe.creativesdk.foundation.auth.IAdobeAuthClientCredentials;

/**
 * Created by thor on 2017/8/1.
 */

public class MainApplication extends Application implements IAdobeAuthClientCredentials {

    /* Be sure to fill in the two strings below. */
    private static final String CREATIVE_SDK_CLIENT_ID      = "5c7de475a1a449d2b3c366259a71ccd9";
    private static final String CREATIVE_SDK_CLIENT_SECRET  = "3762c36d-2b5a-46ac-9281-266e037d0ed1";
    private static final String CREATIVE_SDK_REDIRECT_URI   = "ams+37a109c271525d2c2e89c8acc25b0836a27462a8://adobeid/5c7de475a1a449d2b3c366259a71ccd9";
    private static final String[] CREATIVE_SDK_SCOPES       = {"email", "profile", "address"};

    @Override
    public void onCreate() {
        super.onCreate();
        AdobeCSDKFoundation.initializeCSDKFoundation(getApplicationContext());
    }

    @Override
    public String getClientID() {
        return CREATIVE_SDK_CLIENT_ID;
    }

    @Override
    public String getClientSecret() {
        return CREATIVE_SDK_CLIENT_SECRET;
    }

    @Override
    public String[] getAdditionalScopesList() {
        return CREATIVE_SDK_SCOPES;
    }

    @Override
    public String getRedirectURI() {
        return CREATIVE_SDK_REDIRECT_URI;
    }
}