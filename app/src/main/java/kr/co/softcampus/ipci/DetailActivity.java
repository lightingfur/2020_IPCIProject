package kr.co.softcampus.ipci;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DetailActivity extends AppCompatActivity {
    ImageView imageView3;
    TextView textView2,textView3;
    int mobile_idx;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        imageView3 = (ImageView)findViewById(R.id.imageView3);
        textView2 = (TextView)findViewById(R.id.textView2);
        textView3 = (TextView)findViewById(R.id.textView3);

        //글 번호를 가져온다
        Intent intent = getIntent();
        mobile_idx = intent.getIntExtra("mobile_idx",0);

        GetDataThread thread = new GetDataThread();
        thread.start();
    }

    class GetDataThread extends Thread{
        @Override
        public void run() {
            super.run();

            OkHttpClient client = new OkHttpClient();
            Request.Builder builder = new Request.Builder();
            builder = builder.url("http://192.168.226.1:8080/ipcisever/get_data.jsp");

            FormBody.Builder bodyBuilder = new FormBody.Builder();
            bodyBuilder.add("mobile_idx",mobile_idx+"");

            FormBody body = bodyBuilder.build();

            builder = builder.post(body);

            Request request = builder.build();

            Callback1 callback1 = new Callback1();
            Call call = client.newCall(request);
            call.enqueue(callback1);
        }
    }

    class Callback1 implements Callback{
        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {

        }
        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
            try {
                String result = response.body().string();

                JSONObject obj = new JSONObject(result);

                String mobile_image = obj.getString("mobile_image");
                final String mobile_str1 = obj.getString("mobile_str1");
                final String mobile_str2 = obj.getString("mobile_str2");
                final String mobile_see = obj.getString("mobile_see");
                //final String mobile_right = obj.getString("mobile_right");


                ImageNetworkThread thread2 = new ImageNetworkThread(mobile_image);
                thread2.start();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView2.setText(mobile_str1);
                        textView3.setText(mobile_str2);
                    }
                });
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    class ImageNetworkThread extends Thread{
        String fileName;

        ImageNetworkThread(String fileName){
            this.fileName = fileName;
        }

        @Override
        public void run() {
            super.run();
            try {
                URL url = new URL("http://192.168.226.1:8080/ipcisever/upload/"+fileName);

                URLConnection connection = url.openConnection();
                InputStream is = connection.getInputStream();
                final Bitmap bitmap = BitmapFactory.decodeStream(is);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageView3.setImageBitmap(bitmap);
                    }
                });
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}