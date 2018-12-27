package com.example.mjj.selectphotodemo.beans;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.mjj.selectphotodemo.utils.Bimp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

/**
 * Description：单个图片实体类
 * <p>
 * Created by Mjj on 2016/12/2.
 */

public class ImageItem implements Serializable {

    public String imageId;
    public String thumbnailPath;
    public String imagePath;
    public boolean isSelected = false;
    private Bitmap bitmap;
    private byte[] byteDraw;

//    尝试使用Parcelable传数据的代码
//    public ImageItem(Parcel in)
//    {
//        imageId=in.readString();
//        thumbnailPath=in.readString();
//        imagePath=in.readString();
//        isSelected = in.readByte() != 0;
//
//        in.readByteArray(byteDraw);
//        bitmap = BitmapFactory.decodeByteArray(byteDraw, 0, byteDraw.length);
//    }

//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel parcel, int i) {
//
//        parcel.writeString(imageId);
//        parcel.writeString(thumbnailPath);
//        parcel.writeString(imagePath);
//        parcel.writeByte((byte) (isSelected ? 1 : 0));
//
//        ByteArrayOutputStream baops = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 0, baops);
//        byteDraw = baops.toByteArray();
//        parcel.writeByteArray(byteDraw);
//    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public Bitmap getBitmap() {
        if (bitmap == null) {
            try {
                bitmap = Bimp.revitionImageSize(imagePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

}
