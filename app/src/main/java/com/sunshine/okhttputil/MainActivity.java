package com.sunshine.okhttputil;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.squareup.okhttp.Request;
import com.sunshine.okhttputillibrary.OkHttpUtil;
import com.sunshine.okhttputillibrary.callback.ResultCallback;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         new OkHttpUtil().get("http://139.196.149.10:8080/ft/app/activity/appNewActivitylistPage.do?", new ResultCallback<String>() {
             @Override
             public void onError(Request request, Exception e) {
                 Log.e("tag","");
             }

             @Override
             public void onSuccess(String response) {
                 Log.e("tag",response);
             }

         });
    }
}
