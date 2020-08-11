package kr.co.softcampus.ipci;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WriteActivity extends AppCompatActivity {
    //저장될 경로
    String dirPath;
    //이미지 파일에 접근할 수 있느 uri
    Uri contentUri;
    //이미지 파일명을 포함한 경로
    String picPath;

    //Activity를 구분하기 위한 값
    final int CAMERA_ACTIVITY = 1;
    final int GALLERY_ACTIVITY = 2;

    ImageView imageView2;
    EditText editText,editText2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        imageView2 = (ImageView)findViewById(R.id.imageView2);
        editText=(EditText)findViewById(R.id.editText);
        editText2=(EditText)findViewById(R.id.editText2);

        //경로를 구한다
        String tempPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        dirPath = tempPath + "/Android/data/" + getPackageName();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.write_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.menu_camera:
                Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                String fileName = "temp_" + System.currentTimeMillis() + ".jpg";
                picPath = dirPath + "/" + fileName;

                File file = new File(picPath);

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                    contentUri = FileProvider.getUriForFile(this,"kr.co.softcampus.ipci.file_provider",file);//?
                }else {
                    contentUri = Uri.fromFile(file);
                }

                camera_intent.putExtra(MediaStore.EXTRA_OUTPUT,contentUri);
                startActivityForResult(camera_intent,CAMERA_ACTIVITY);

                break;
            case R.id.menu_gallery:
                Intent gallery_intent = new Intent(Intent.ACTION_PICK);
                gallery_intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(gallery_intent,GALLERY_ACTIVITY);
                break;
            case R.id.menu_upload:
                UploadThread thread = new UploadThread();
                thread.start();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CAMERA_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                Bitmap bitmap = BitmapFactory.decodeFile(picPath);
                //사이즈를 줄인 이미지 객체를 가져온다
                Bitmap bitmap2 = resizeBitmap(1024,bitmap);

                //이미지의 회전 각도값을 가져온다
                float degree = getDegree(picPath);
                //이미지를 재 구성한다
                Bitmap bitmap3 = rebuildBitmap(bitmap2,picPath,degree);

                imageView2.setImageBitmap(bitmap3);
            }
        }else if (requestCode == GALLERY_ACTIVITY){
            if (resultCode == RESULT_OK){
                //이미지에 접근할 수 있는 uri 객체를 추출한다
                ContentResolver resolver = getContentResolver();
                //ContentProvier로 부터 이미지의 경로를 추출한다
                Cursor cursor = resolver.query(data.getData(),null,null,null,null);
                cursor.moveToNext();

                int idx = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                String sourcePath = cursor.getString(idx);

                Bitmap bitmap = BitmapFactory.decodeFile(sourcePath);

                Bitmap bitmap2 = resizeBitmap(1024,bitmap);
                float degree = getDegree(sourcePath);

                String fileName = "temp_" + System.currentTimeMillis() + ".jpg";
                picPath = dirPath + "/" + fileName;

                Bitmap bitmap3 = rebuildBitmap(bitmap2,picPath,degree);
                imageView2.setImageBitmap(bitmap3);
            }
        }
    }

    public Bitmap resizeBitmap(int targeWidth,Bitmap source){
        //비율을 계산한다
        double ratio = (double)targeWidth / (double)source.getWidth();
        //새로운 높이를 계산한다
        int targeHeight = (int)(source.getHeight() * ratio);
        //주어진 사이즈로 이미지의 크기를 조정한다
        Bitmap result = Bitmap.createScaledBitmap(source,targeWidth,targeHeight,false);
        //원본 이미지 객체를 소멸한다
        if(result != source){
            source.recycle();
        }
        return result;
    }
    //이미지의 회전 각도를 구하는 메서드
    public float getDegree(String path){
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int degree = 0;

            //회전 각도값을 가져온다
            int ori = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,-1);
            switch (ori){
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
            return (float)degree;
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }
    //이미지 회전을 적용하여 새롭게 저장한다
    public Bitmap rebuildBitmap(Bitmap bitmap, String path, float degree){
        try {
            //원본 이미지의 가로, 세로 길이를 구한다
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            //새롭게 만들 이미지의 정보를 담을 객체를 생성한다
            Matrix matrix = new Matrix();
            matrix.postRotate(degree);
            //새로운 비트맵을 만든다
            Bitmap resizeBitmap = Bitmap.createBitmap(bitmap,0,0,width,height,matrix,true);

            //이미지를 파일로 저장한다
            FileOutputStream fos = new FileOutputStream(path);
            resizeBitmap.compress(Bitmap.CompressFormat.JPEG,100,fos);
            fos.flush();
            fos.close();

            //원본 이미지 객체를 소멸한다
            bitmap.recycle();
            //새롭게 만든 이미지를 반환한다
            return resizeBitmap;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    //여기서 부터 네트워크 작업
    class  UploadThread extends Thread{
        @Override
        public void run() {
            super.run();
            try {
                OkHttpClient client = new OkHttpClient();
                Request.Builder builder = new Request.Builder();
                builder = builder.url("http://192.168.226.1:8080/ipciServer/upload.jsp");

                String mobile_str1 = editText.getText().toString();
                String mobile_str2 = editText2.getText().toString();

                MultipartBody.Builder builder3 = new MultipartBody.Builder();
                builder3.setType(MultipartBody.FORM);

                //서버로 보낼 문자열 데이터
                builder3.addFormDataPart("mobile_str1",mobile_str1);
                builder3.addFormDataPart("mobile_str2",mobile_str2);

                //서버로 보낼 파일 데이터터
                File file = new File(picPath);
                RequestBody body = RequestBody.create(MultipartBody.FORM,file);
                builder3.addFormDataPart("mobile_image",file.getName(),body);

                MultipartBody body2 = builder3.build();

                builder = builder.post(body2);
                Request request = builder.build();
                Call call =client.newCall(request);
                //call.execute();
                NetworkCallback callback = new NetworkCallback();
                call.enqueue(callback);

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    class NetworkCallback implements Callback{
        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {

        }
        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
            try {
                String result = response.body().string();

                if(result.trim().equals("OK")){
                    finish();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}