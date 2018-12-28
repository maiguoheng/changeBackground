package com.example.mjj.selectphotodemo;

import android.app.Service;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.mjj.selectphotodemo.beans.ImageItem;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.mjj.selectphotodemo.utils.Bimp.tempSelectBitmap;

/**
 * Created by MA on 2018/12/6.
 */

public class changepaper extends Service {
    ArrayList<Bitmap> Wallpapers = new ArrayList<>();
    int ChangeTime = 2000;
    boolean start = false;

    boolean addNewWallpaper(Bitmap bitmap) {
        if (bitmap != null) {
            Wallpapers.add(bitmap);
            return true;

        }
        return false;


    }

    public static void setCurrent(int current) {
        changepaper.current = current;
    }

//    public void setWallpapers(ArrayList<Bitmap> wallpapers) {
//        Wallpapers = wallpapers;
//    }

    public void setChangeTime(int changeTime) {
        ChangeTime = changeTime;

    }

    boolean ok = false;
    WallpaperManager wallpaperManager;
    static int current = 0;

    @Override
    public void onCreate() {
        super.onCreate();

        // changepaper a=new changepaper();
//            InputStream stream = getResources().openRawResource(R.raw.test);
//            Bitmap bmp= BitmapFactory.decodeStream(stream);
//            InputStream stream1 = getResources().openRawResource(R.raw.test1);
//            Bitmap bmp1=BitmapFactory.decodeStream(stream1);
//        Wallpapers.add(bmp);
//        Wallpapers.add(bmp1);
        wallpaperManager = WallpaperManager.getInstance(this);

         task= new TimerTask() {
            @Override
            public void run() {
                try {

                    //如果到了最后一张，系统重新开始
                    if (current >= Wallpapers.size()) {
                        current = 0;
                    }


if(Wallpapers.size()>0) {
    System.out.println("正在更换壁纸");
    wallpaperManager.setBitmap(Wallpapers.get(current));
    current++;
}
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };


    }

    Timer timer = new Timer();

     TimerTask task;




    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //Intent intent = this.getIntent();
       // Wallpapers = (ArrayList<Bitmap>) intent.getSerializableExtra("list");//获取list方式
        int pos=intent.getIntExtra("pos",0);


        System.out.println("获取到数据");
                ArrayList<Bitmap> test=new ArrayList<>();
        for (ImageItem temp : tempSelectBitmap[pos]) {


            test.add(temp.getBitmap());


        }
        Wallpapers=test;
       ChangeTime=intent.getIntExtra("time",2000);

        timer.schedule(task,0,ChangeTime);
        return super.onStartCommand(intent, flags, startId);
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        timer.cancel();
        super.onDestroy();
    }
}
