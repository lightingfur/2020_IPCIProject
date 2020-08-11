package kr.co.softcampus.ipci;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    //리스트뷰 구성을 위해서 필요한 데이터가 담겨 있는 ArrayList
    ArrayList<HashMap<String ,Object>> listData = new ArrayList<HashMap<String, Object>>();
    //서버로 부터 받아온 이미지를 담을 HashMap
    HashMap<String, Bitmap> imageMap = new HashMap<String, Bitmap>();

    ListView main_list;

    //확인할 권한 목록
    String [] permssion_list = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        main_list = (ListView)findViewById(R.id.main_list);

        ListAdapter adapter = new ListAdapter();
        main_list.setAdapter(adapter);

        ListListener listListener = new ListListener();
        main_list.setOnItemClickListener(listListener);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            requestPermissions(permssion_list,0);
        }else {
            init();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for(int result : grantResults){
            if(result == PackageManager.PERMISSION_DENIED){
                return;
            }
        }
        init();
    }

    //데이터 초기화 메서드
    public void init(){

        String tempPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String dirPath = tempPath + "/Android/data/" + getPackageName();

        File file = new File(dirPath);
        if(file.exists() == false){
            file.mkdir();
        }
        //임시데이터
//        HashMap<String,Object> map1 = new HashMap<String, Object>();
//        HashMap<String,Object> map2 = new HashMap<String, Object>();
//        HashMap<String,Object> map3 = new HashMap<String, Object>();
//
//        map1.put("mobile_image",android.R.drawable.ic_menu_add);
//        map1.put("mobile_str1","항목1");
//
//        map2.put("mobile_image",android.R.drawable.ic_menu_agenda);
//        map2.put("mobile_str1","항목2");
//
//        map3.put("mobile_image",android.R.drawable.ic_menu_camera);
//        map3.put("mobile_str1","항목3");
//
//        listData.add(map1);
//        listData.add(map2);
//        listData.add(map3);
        //리스트뷰르 갱신한다
        ListAdapter adapter = (ListAdapter)main_list.getAdapter();
        adapter.notifyDataSetChanged();
    }

    //리스트뷰에 적용할 어뎁터 클래스
    class ListAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return listData.size();
        }
        @Override
        public Object getItem(int position) {
            return null;
        }
        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //재사용가능한 뷰가 없다면 만들어준다
            if(convertView == null){
                LayoutInflater inflater = getLayoutInflater();
                convertView = inflater.inflate(R.layout.row,null);
            }
            //항목 내부의 뷰를 추출한다
            ImageView imageView = (ImageView)convertView.findViewById(R.id.imageView);
            TextView textView = (TextView)convertView.findViewById(R.id.textView);

            //position번째 HashMap을 추출한다
            HashMap<String,Object> map = listData.get(position);

            //뷰에 데이터를 셋팅한다
            String mobile_img = (String) map.get("mobile_image");
            String mobile_str1 = (String)map.get("mobile_str1");

            //HashMap에서 이미지를 추출한다
            Bitmap bitmap = imageMap.get(mobile_img);
            if (bitmap == null){
                ImageNetworkThread thread2 = new ImageNetworkThread(mobile_img);
                thread2.start();
            }else{
                imageView.setImageBitmap(bitmap);
            }

            //imageView.setImageResource(mobile_img);
            textView.setText(mobile_str1);
            return convertView;
        }
    }

    class ListListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent detail_intent = new Intent(MainActivity.this,DetailActivity.class);

            //항목 번째 해시맵을 추출한다
            HashMap<String,Object> map = (HashMap<String, Object>)listData.get(position);
            //글의 인덱스 번호를 가져온다
            int mobile_idx = (Integer)map.get("mobile_idx");
            detail_intent.putExtra("mobile_idx",mobile_idx);

            startActivity(detail_intent);
        }
    }


    //메인 메뉴
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.menu_write: //글작성 액티비티 실행
                Intent write_intent = new Intent(this,WriteActivity.class);
                startActivity(write_intent);
                break;
            case R.id.menu_reload: //새로 고침
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //현재 액티비티가 다시 가동되면 호출되는 메소드
    @Override
    protected void onResume() {
        super.onResume();

        GetDataThread thread = new GetDataThread();
        thread.start();
    }

    class GetDataThread extends Thread{
        @Override
        public void run() {
            super.run();
            OkHttpClient client = new OkHttpClient();
            Request.Builder builder = new Request.Builder();
            builder = builder.url("http://192.168.226.1:8080/ipcisever/get_list.jsp");
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

                listData.clear();

                JSONArray root = new JSONArray(result);

                for(int i = 0;i< root.length();i++){
                    JSONObject obj = root.getJSONObject(i);

                    int mobile_idx = obj.getInt("mobile_idx");
                    String mobile_str1 = obj.getString("mobile_str1");
                    String mobile_image = obj.getString("mobile_image");

                    HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put("mobile_idx",mobile_idx);
                    map.put("mobile_str1",mobile_str1);
                    map.put("mobile_image",mobile_image);

                    listData.add(map);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ListAdapter adapter = (ListAdapter)main_list.getAdapter();
                        adapter.notifyDataSetChanged();
                    }
                });
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    //이미지데이터를 받아오는 쓰래드
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
                Bitmap bitmap = BitmapFactory.decodeStream(is);

                imageMap.put(fileName,bitmap);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ListAdapter adapter = (ListAdapter)main_list.getAdapter();
                        adapter.notifyDataSetChanged();
                    }
                });
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}