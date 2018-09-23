package com.aptron.multithreading;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    ListView listView;
    EditText et;
    String[] url;
    LinearLayout l;
    String [] img={"Image 1","Image 2","Image 3","Image 4","Image 5"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView= findViewById(R.id.lv);
        et= findViewById(R.id.editText);
        url= getResources().getStringArray(R.array.url);
        ArrayAdapter<String> adapter=new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item,img);
        listView.setAdapter(adapter);

        l= findViewById(R.id.linearLayout);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                et.setText(url[i]);
            }
        });
        isWriteStoragePermissionGranted();


    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        et.setText(url[i]);
    }
    public void downloadImage(View view) {
        Thread thread = new Thread(new MyThread());
        thread.start();
    }

    public void getDownloadImage(String url)
    {
        HttpURLConnection connection=null;
        InputStream stream = null;
        FileOutputStream outputStream =null;
        try {
            URL url1 = new URL(url);
            connection = (HttpURLConnection) url1.openConnection();
            stream=connection.getInputStream();
            File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            Uri uri =Uri.parse(url);
            File myFile = new File(folder,uri.getLastPathSegment());
            outputStream= new FileOutputStream(myFile);
            byte[] arr = new byte[1024];
            int r =-1;
            while (( r = stream.read(arr))!=-1)
            {
                outputStream.write(arr,0,r);
            }
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    l.setVisibility(View.GONE);
                }
            });

            try {
                stream.close();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            connection.disconnect();

        }
    }

    public  boolean isWriteStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("yami","Permission is granted2");
                return true;
            } else {

                Log.v("yami","Permission is revoked2");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("yami","Permission is granted2");
            return true;
        }
    }




    class MyThread implements Runnable
{
    @Override
    public void run() {

        getDownloadImage(et.getText().toString());

        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                l.setVisibility(View.VISIBLE);
            }
        });
    }
}
}