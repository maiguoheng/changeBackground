package com.example.mjj.selectphotodemo.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import com.example.mjj.selectphotodemo.beans.ImageItem;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import static android.content.Context.MODE_APPEND;
import static android.content.Context.MODE_PRIVATE;

/**
 * Description：出来临时图片数据
 * <p>
 * Created by Mjj on 2016/12/2.
 */
//mchange
//public class Bimp  {

public class Bimp implements Serializable {
    //max:记录各相册集的数量
    public static int max_groupnumber = 30;
    public static String[] group_name = new String[max_groupnumber];
    public static String group_name_forselect[];
    public  static  ArrayList<String> names=new ArrayList<>();
    public static int[] max = new int[max_groupnumber];
    public static int current_group_number = 0;
    public static ArrayList<ImageItem>[] tempSelectBitmap = new ArrayList[max_groupnumber];   // 图片临时列表
    public static String filepath;

    public static void update_nameforselect(){
        group_name_forselect=new String[current_group_number];
        for(int i=0;i<current_group_number;i++){
            group_name_forselect[i]=String.valueOf(i+1)+":"+group_name[i];

        }
    }

    public static void init(Context context) {
        filepath = context.getFilesDir().getPath();

        String fileName;
        FileInputStream fis;
        File file;
        for (int i = 0; i < max_groupnumber; i++) {
            tempSelectBitmap[i] = new ArrayList<>();

            //对相册命名操作
            group_name[i] = "未命名";
            File fileForname=new File(filepath+"//name_group_"+String.valueOf(i)+".txt");
            if(fileForname.exists()){

                try {
                    fis = context.openFileInput("name_group_" + String.valueOf(i) + ".txt");
                    byte[] buffer = new byte[fis.available()];
                    fis.read(buffer);
                    String result = new String(buffer);
                    group_name[i] =result;
                }catch (IOException e){
                    e.printStackTrace();
                }
            }

            fileName = "group_" + String.valueOf(i) + "_imagePath.txt";
            file = new File(filepath + "//" + fileName);
            if (file.exists()) {
                try {
                    fis = context.openFileInput(fileName);
                    byte[] buffer = new byte[fis.available()];
                    fis.read(buffer);
                    String[] result = new String(buffer).split("\r");
                    max[i] = result.length;
                    for (int m = 0; m < result.length; m++) {
                        Bitmap bitmap = BitmapUtils.decodeSampledBitmapFromFd(result[m], 400, 500);
                        ImageItem takePhoto = new ImageItem();
                        takePhoto.setBitmap(bitmap);
                        Bimp.tempSelectBitmap[i].add(takePhoto);
                    }
                    current_group_number++;
                    fis.close();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }

        }
    }


    public static void deletePic(Context context, int group_number, int location) {
        tempSelectBitmap[group_number].remove(location);
        max[group_number]--;
        String fileName = "group_" + String.valueOf(group_number) + "_imagePath.txt";
        try {
            FileInputStream fis = context.openFileInput(fileName);
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            String[] result = new String(buffer).split("\r");

            File file = new File(filepath + "//" + fileName);
            if (file.exists()) {
                file.delete();
            }


            //重写该文件
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_APPEND);
            for (int i = 0; i < result.length; i++) {
                if (i != location)
                    fos.write((result[i] + "\r").getBytes());
            }
            fis.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static void deleteGroup(int group_number) {
        tempSelectBitmap[group_number].clear();
        for (int i = group_number; i < current_group_number - 1; i++) {
            tempSelectBitmap[i] = (ArrayList<ImageItem>)
                    tempSelectBitmap[i + 1].clone();
            max[i] = max[i + 1];
        }
        //对文件操作
        String fileName = "group_" + String.valueOf(group_number) + "_imagePath.txt";
        File file = new File(filepath + "//" + fileName);
        file.delete();
        file=new File("name_group_" + String.valueOf(group_number) + ".txt");
        file.delete();
        for (int i = group_number + 1; i < current_group_number; i++) {
            fileName = "group_" + String.valueOf(i) + "_imagePath.txt";
            file = new File(filepath + "//" + fileName);
            if (file.exists()) {
                file.renameTo(new File(filepath + "//group_" + String.valueOf(i - 1) + "_imagePath.txt"));
            }
        }

        current_group_number--;
        tempSelectBitmap[Bimp.current_group_number].clear();
        max[current_group_number] = 0;


    }

    public static void savePath(Context context, ArrayList<String> path, int group_number) {
        String fileName = "group_" + String.valueOf(group_number) + "_imagePath.txt";
        try {
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_APPEND);
            for (int i = 0; i < path.size(); i++) {
                fos.write((path.get(i) + "\r").getBytes());
            }
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveName(Context context, String name, int group_number) {
        String fileName = "name_group_"+String.valueOf(group_number)+".txt";
        try {
            group_name[group_number]=name;
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
                fos.write((name).getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Bitmap revitionImageSize(String path) throws IOException {
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(
                new File(path)));
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(in, null, options);
        in.close();
        int i = 0;
        Bitmap bitmap = null;
        while (true) {
            if ((options.outWidth >> i <= 1000)
                    && (options.outHeight >> i <= 1000)) {
                in = new BufferedInputStream(
                        new FileInputStream(new File(path)));
                options.inSampleSize = (int) Math.pow(2.0D, i);
                options.inJustDecodeBounds = false;
                bitmap = BitmapFactory.decodeStream(in, null, options);
                break;
            }
            i += 1;
        }
        return bitmap;
    }
}
