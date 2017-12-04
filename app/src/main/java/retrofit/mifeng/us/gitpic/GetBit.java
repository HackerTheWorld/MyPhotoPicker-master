package com.wgm.scaneqinfo.common;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.wgm.scaneqinfo.operate.BaseOperate;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

/**
 * Created by apple on 2017/11/14.
 */

//获取图片，图片需要能通过get访问
public class GetBit {

    private static Bitmap bitmap;
    protected static Bitmap getHttpBitmap(String url) {
        URL myFileUrl = null;
        try {
            Log.d(TAG, url);
            myFileUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.connect();
            Log.d(TAG, String.valueOf(conn.getResponseCode()));
            if(conn.getResponseCode()==HttpURLConnection.HTTP_OK){
                InputStream is = conn.getInputStream();
                bitmap = BitmapFactory.decodeStream(is);
                is.close();
            }else {
                Log.d(TAG, String.valueOf(conn.getResponseCode()));
            }
            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }


    public static Bitmap asyncRequest(final String url) throws ExecutionException, InterruptedException {
        ExecutorService threadPool = Executors.newSingleThreadExecutor();
        Future<Bitmap> future = threadPool.submit(new Callable<Bitmap>() {

            @Override
            public Bitmap call() throws Exception {
                return getHttpBitmap(url);
            }

        });
        return future.get();
    }

}
