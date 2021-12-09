package com.tulsi.filecopy;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.apache.cordova.CordovaResourceApi;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import android.content.Context;

import org.apache.commons.io.FileUtils;

import android.database.Cursor;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.util.Log;
import android.net.Uri;
import java.io.InputStream;

/**
 * This class echoes a string called from JavaScript.
 */
public class filecopy extends CordovaPlugin {
    boolean finished = false;
    CallbackContext callback;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("coolMethod")) {
            String uri = args.getString(0);
            String path = args.getString(1);
            this.coolMethod(uri, path, callbackContext);
            return true;
        }
        return false;
    }

    private void coolMethod(String uri,String path, CallbackContext callbackContext) {
        if (uri != null && uri.length() > 0 && path != null && path.length() > 0) {
            Context appContext = this.cordova.getActivity().getApplicationContext();
            Uri myUri = Uri.parse(uri);

            Cursor returnCursor = this.cordova.getActivity().getApplicationContext().getContentResolver().query(myUri, null,null, null,null);

            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            returnCursor.moveToFirst();
            String name = returnCursor.getString(nameIndex);



            CordovaResourceApi cordovaResourceApi = webView.getResourceApi();

            PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
            pluginResult.setKeepCallback(true);
            callback = callbackContext;
            callbackContext.sendPluginResult(pluginResult);

            InputStream inputStream;

            try{
                inputStream = appContext.getContentResolver().openInputStream(myUri);

                cordova.getThreadPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            FileUtils.copyInputStreamToFile(inputStream, new File(Uri.parse(path).getPath(),name));
                            returnCursor.close();
                            callbackContext.success(name);
                        }catch (IOException e){
                            returnCursor.close();
                            e.printStackTrace();
                            callbackContext.error("Error: "+e.getMessage());
                        }
                    }
                });

            }catch (IOException e){
                callbackContext.error("Error: "+e.getMessage());
            }


        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }

}
