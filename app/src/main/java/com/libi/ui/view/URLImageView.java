package com.libi.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by surface on 2018/9/13.
 */

@SuppressLint("AppCompatCustomView")
public class URLImageView extends ImageView {
//    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(
//                    5,1000,0,
//                    TimeUnit.MILLISECONDS,new ArrayBlockingQueue<Runnable>(5)
//    );

    private static final int GET_DATA_SUCCESS = 100;
    private static final int NETWORK_ERROR = 101;
    private static final int SERVER_ERROR = 102;
    private static final String LOG = "网络请求图片";

    private GetUrlImageHandler handler = new GetUrlImageHandler(this);

    public URLImageView(Context context) {
        super(context);
    }

    public URLImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public URLImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressLint("NewApi")
    public URLImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setImageUrl(final String res) {
        new Thread(new GetUrlImageRunnable(res, this, handler)).start();
    }

    //压缩
    private Bitmap compress(Bitmap bitmap) {
        bitmap = clip(bitmap);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] b = baos.toByteArray();
        return BitmapFactory.decodeByteArray(b, 0, b.length);
    }

    private Bitmap clip(Bitmap bitmap) {
//        if (bitmap.getHeight() > getHeight() && bitmap.getWidth() > getWidth()) {
//            bitmap = Bitmap.createBitmap(bitmap,0,0,getWidth()+1,getHeight()+1);
//        }
        return bitmap;

    }


    static class GetUrlImageHandler extends Handler {
        //TODO 现在是强引用
        private URLImageView imageViewWeakReference;

        public GetUrlImageHandler(URLImageView view) {
            imageViewWeakReference = view;
        }

        @Override
        public void handleMessage(Message msg) {
            URLImageView imageView = imageViewWeakReference;
            switch (msg.what) {
                case GET_DATA_SUCCESS:
                    Bitmap bitmap = (Bitmap) msg.obj;
                    imageView.setImageBitmap(bitmap);
                    break;
                case NETWORK_ERROR:
                    Log.e(LOG, "网络连接失败");
                    break;
                case SERVER_ERROR:
                    Log.e(LOG, "请求服务器发生错误");
                    break;
                default:
                    Log.e(LOG, "发生了未知错误");
                    break;
            }
        }
    }

    private static boolean isSave(String name, Context context) {
        String sdcardPath = context.getExternalCacheDir().getPath();
        String dir = sdcardPath + "新闻缓存/" + name;
        File file = new File(dir);
        return file.exists();
    }

    private static boolean saveImg(Bitmap bitmap, String name, Context context) {
        try {
            String sdcardPath = context.getExternalCacheDir().getPath();
            //"Android/data/"+ context.getPackageName()+"/files/";
            //Environment.getExternalStorageDirectory().getPath()
                    /*System.getenv("EXTERNAL_STORAGE")*/
            ;      //获得sd卡路径
            Log.w("包名", context.getPackageName());
            Log.w("路径", sdcardPath);
            String dir = sdcardPath + "新闻缓存/";                    //图片保存的文件夹名
            File file = new File(dir);                                 //已File来构建
            if (!file.exists()) {                                     //如果不存在  就mkdirs()创建此文件夹
                file.mkdir();
                Log.w("保存图片", "创建路径:" + file.getPath());
            }
            Log.w("保存图片", "file uri==>" + dir);
            File mFile = new File(dir + name);                        //将要保存的图片文件
            if (mFile.exists()) {
                Log.w("保存图片", "图片存在" + dir);
                return false;
            }
            mFile.getParentFile().mkdirs();
            mFile.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(mFile);     //构建输出流
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);  //compress到输出outputStream
//            Uri uri = Uri.fromFile(mFile);                                  //获得图片的uri
            outputStream.flush();
            outputStream.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static Bitmap getDiskBitmap(String pathString, Context context) {
        Log.w("读取图片", "正在读取图片");
        String sdcardPath = context.getExternalCacheDir().getPath();
        String dir = sdcardPath + "新闻缓存/";                    //图片保存的文件夹名
        pathString = dir + pathString;
        Bitmap bitmap = null;
        try {
            File file = new File(pathString);
            if (file.exists()) {
                bitmap = BitmapFactory.decodeFile(pathString);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }
        return bitmap;
    }

    static class GetUrlImageRunnable implements Runnable {
        private WeakReference<String> resWeekReference;
        private URLImageView viewWeakReference;
        private WeakReference<GetUrlImageHandler> handlerWeakReference;

        public GetUrlImageRunnable(String res, URLImageView view, GetUrlImageHandler handler) {
            resWeekReference = new WeakReference<String>(res);
            viewWeakReference = view;
            handlerWeakReference = new WeakReference<GetUrlImageHandler>(handler);
        }

        @Override
        public void run() {
            try {
                String res = resWeekReference.get();
                Message message = Message.obtain();
                if (isSave(res, viewWeakReference.getContext())) {
                    message.obj = getDiskBitmap(res, viewWeakReference.getContext());
                    message.what = GET_DATA_SUCCESS;
                } else {
                    URL url = new URL(res);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(30000);
                    connection.connect();
                    int code = connection.getResponseCode();
                    switch (code) {
                        case HttpURLConnection.HTTP_OK:
                            InputStream inputStream = connection.getInputStream();
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            bitmap = viewWeakReference.compress(bitmap);

                            //储存bitmap
                            saveImg(bitmap, res, viewWeakReference.getContext());

                            message.what = GET_DATA_SUCCESS;
                            message.obj = bitmap;
                            inputStream.close();
                            break;
                        default:
                            message.what = SERVER_ERROR;
                            handlerWeakReference.get().sendMessage(message);
                    }
                }
                handlerWeakReference.get().sendMessage(message);
            } catch (IOException e) {
                handlerWeakReference.get().sendEmptyMessage(NETWORK_ERROR);
                e.printStackTrace();
            }
        }
    }

}
