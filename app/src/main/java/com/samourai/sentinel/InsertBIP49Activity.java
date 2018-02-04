package com.samourai.sentinel;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.samourai.sentinel.util.AppUtil;
import com.samourai.sentinel.util.PrefsUtil;
import com.samourai.sentinel.util.Web;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class InsertBIP49Activity extends Activity {

    private ProgressDialog progress = null;
    private Handler handler = null;
    private BIP49Task bip49Task = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String xpub = null;
        String label = null;

        Bundle extras = getIntent().getExtras();
        if(extras != null && extras.containsKey("xpub"))	{
            xpub = extras.getString("xpub");
            label = extras.getString("label");
        }

        bip49Task = new BIP49Task();
        bip49Task.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, new String[] { xpub, label });

    }

    @Override
    public void onResume() {
        super.onResume();

        AppUtil.getInstance(InsertBIP49Activity.this).checkTimeOut();

    }

    private class BIP49Task extends AsyncTask<String, Void, String> {

        private Handler handler = null;

        @Override
        protected void onPreExecute() {
            handler = new Handler();

            progress = new ProgressDialog(InsertBIP49Activity.this);
            progress.setCancelable(false);
            progress.setTitle(R.string.app_name);
            progress.setMessage(getString(R.string.please_wait));
            progress.show();
        }

        @Override
        protected String doInBackground(String... params) {

            String response = null;
            try {
                StringBuilder args = new StringBuilder();
                args.append("xpub=");
                args.append(params[0]);
                args.append("&type=restore");
                args.append("&segwit=bip49");
                response = Web.postURL(Web.BLOCKCHAIN_DOMAIN_API + "xpub2&", args.toString());

                Log.d("InsertBIP49Activity", "BIP49:" + response);

                JSONObject obj = new JSONObject(response);
                if(obj != null && obj.has("status") && obj.getString("status").equals("ok"))    {
                    if(progress != null && progress.isShowing())    {
                        progress.dismiss();
                        progress = null;
                    }

                    SamouraiSentinel.getInstance(InsertBIP49Activity.this).getBIP49().put(params[0], params[1]);
                    try {
                        SamouraiSentinel.getInstance(InsertBIP49Activity.this).serialize(SamouraiSentinel.getInstance(InsertBIP49Activity.this).toJSON(), null);
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    } catch (JSONException je) {
                        je.printStackTrace();
                    }

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("xpub", params[0]);
                    resultIntent.putExtra("label", params[1]);
                    setResult(Activity.RESULT_OK, resultIntent);
                    InsertBIP49Activity.this.finish();

                }
                else    {
                    Intent resultIntent = new Intent();
                    setResult(Activity.RESULT_CANCELED, resultIntent);
                    InsertBIP49Activity.this.finish();
                }

            }
            catch(Exception e) {
                e.printStackTrace();
                Intent resultIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, resultIntent);
                InsertBIP49Activity.this.finish();
            }
            finally {
                ;
            }

            return "OK";
        }

        @Override
        protected void onPostExecute(String result) {
            if(progress != null && progress.isShowing())    {
                progress.dismiss();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            ;
        }

    }

}
