package cn.edu.zafu.stethodemo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.facebook.stetho.okhttp.StethoInterceptor;
import com.jayfeng.lesscode.core.SharedPreferenceLess;
import com.jayfeng.lesscode.core.ToastLess;
import com.jayfeng.lesscode.core.ViewLess;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import cn.edu.zafu.stethodemo.model.Person;

public class MainActivity extends AppCompatActivity {
    private Button btnNetwork;
    private Button btnSharedPreference;
    private Button btnSqlite;
    private MyOnClickListener listener;
    private static final String TAG = "StethDemo";
    private static final int NETWORK = 0x01;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case NETWORK:
                    ToastLess.$(getApplicationContext(), (String)msg.obj);

                    break;
                default:
                    super.handleMessage(msg);
            }

        }
    };
    private class MyOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_network:
                    getFromNetwork();
                    break;
                case R.id.btn_sharedPreference:
                    writeToSharedPreference();
                    break;
                case R.id.btn_sqlite:
                    writeToSqlite();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initListener();

    }


    private void initView() {
        btnNetwork = ViewLess.$(this, R.id.btn_network);
        btnSharedPreference = ViewLess.$(this, R.id.btn_sharedPreference);
        btnSqlite = ViewLess.$(this, R.id.btn_sqlite);
    }

    private void initListener() {
        listener = new MyOnClickListener();
        btnNetwork.setOnClickListener(listener);
        btnSharedPreference.setOnClickListener(listener);
        btnSqlite.setOnClickListener(listener);

    }

    private void writeToSharedPreference() {
        SharedPreferenceLess.$put(getApplicationContext(), "name", "StethoDemo");
        SharedPreferenceLess.$put(getApplicationContext(), "version", "v1.0.0");
        ToastLess.$(this, "save to SharedPreference successfully!");
    }

    private void writeToSqlite() {
        Person person = new Person();
        person.setName("Jake");
        person.setAge(19);
        person.save();

        ToastLess.$(this, "save to Sqlite successfully!");
    }

    private void getFromNetwork() {
        OkHttpClient client = new OkHttpClient();
        client.networkInterceptors().add(new StethoInterceptor());
        Request request = new Request.Builder()
                .url("http://www.baidu.com")
                .build();
        Response response = null;

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }
            @Override
            public void onResponse(Response response) throws IOException {
                String body = response.body().string();
                Message message = handler.obtainMessage();
                message.what=NETWORK;
                message.obj=body;
                handler.sendMessage(message);
            }
        });
    }

}
