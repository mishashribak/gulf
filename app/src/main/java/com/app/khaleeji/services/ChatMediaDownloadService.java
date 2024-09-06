package com.app.khaleeji.services;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.app.khaleeji.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class ChatMediaDownloadService extends AsyncTask<String, String, String> {

    private String media_url;
    private Context mContext;
    private String mExtension;

    public ChatMediaDownloadService(){

    }

    public ChatMediaDownloadService(String media_url, String mediaType, Context context) {
        mContext = context;
        this.media_url = media_url;
        if(mediaType.equalsIgnoreCase("image")){
            mExtension = ".jpeg";
        }else{
            mExtension = ".mp4";
        }
        Log.e("MediaDownloadService", media_url);
    }

    @Override
    protected String doInBackground(String... f_url) {
        int count;
        try {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(mContext, mContext.getString(R.string.post_media_download), Toast.LENGTH_SHORT).show();
                }
            });
            URL url = new URL(this.media_url);
            URLConnection conection = url.openConnection();
            conection.connect();
            int lenghtOfFile = conection.getContentLength();
            InputStream input = new BufferedInputStream(url.openStream());
            File storageDir = new File(Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/Camera/");
            if (!storageDir.exists())
                storageDir.mkdirs();
            int index = this.media_url.lastIndexOf('.');
            String extension;
            if( index != -1 ){
                extension = this.media_url.substring(index);
            }else{
                extension = this.mExtension;
            }

            OutputStream output = new FileOutputStream(Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/Camera/"+System.currentTimeMillis() + extension);

            byte data[] = new byte[1024];
            long total = 0;
            while ((count = input.read(data)) != -1) {
                total += count;
                publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                output.write(data, 0, count);
            }
            output.flush();
            output.close();
            input.close();

        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
        }

        return null;
    }

    protected void onProgressUpdate(String... progress) {
        Log.e("MediaDownProgress", progress[0]);
    }

    @Override
    protected void onPostExecute(String file_url) {
        Log.e("MediaDownloadService", "done");
    }
}
