package com.samourai.sentinel.sweep;

import android.content.Context;

import com.samourai.sentinel.util.Web;

public class PushTx {

    private static PushTx instance = null;
    private static Context context = null;

    private PushTx() { ; }

    public static PushTx getInstance(Context ctx) {

        context = ctx;

        if(instance == null) {
            instance = new PushTx();
        }

        return instance;
    }

    public String samourai(String hexString) {

        try {
            String response = null;

            response = Web.postURL("text/plain", Web.BLOCKCHAIN_DOMAIN_API + "pushtx", hexString);

            return response;
        }
        catch(Exception e) {
            return null;
        }

    }

}
