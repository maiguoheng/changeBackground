package com.example.mjj.selectphotodemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mjj.selectphotodemo.beans.ImageItem;
import com.example.mjj.selectphotodemo.utils.Bimp;
import com.example.mjj.selectphotodemo.zoom.PhotoView;
import com.example.mjj.selectphotodemo.zoom.ViewPagerFixed;

import java.util.ArrayList;

/**
 * Description：进行图片浏览,可删除
 * <p>
 * Created by Mjj on 2016/12/2.
 */

public class PreviewPhotoActivity extends Activity {
//  操作的图集
    private int group_number;
    private Intent intent;
    // 返回图标
    private TextView back_bt;
    private ImageView btn_back;
    //删除图标
    private ImageView del_bt;

    //获取前一个activity传过来的position
    private int position;
    //当前的位置
    private int location = 0;

    private ArrayList<View> listViews = null;
    private ViewPagerFixed pager;
    private MyPageAdapter adapter;

    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plugin_camera_gallery);// 切屏到主界面
        mContext = this;
        btn_back=(ImageView) findViewById(R.id.btn_back);
        back_bt = (TextView) findViewById(R.id.gallery_back);
        del_bt = (ImageView) findViewById(R.id.gallery_del);
        // 返回键监听
        back_bt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btn_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        del_bt.setOnClickListener(new DelListener());
        intent = getIntent();
        position = intent.getIntExtra("position",-1);

        pager = (ViewPagerFixed) findViewById(R.id.gallery01);
        pager.addOnPageChangeListener(pageChangeListener);

        int id = intent.getIntExtra("ID", 0);
        group_number=intent.getIntExtra("group_number",0);
        back_bt.setText(String.valueOf(position+1)+"/"+Bimp.tempSelectBitmap[group_number].size());

        for (int i = 0; i < Bimp.tempSelectBitmap[group_number].size(); i++) {
            Bitmap tempBit = Bimp.tempSelectBitmap[group_number].get(i).getBitmap();
            initListViews(tempBit);
        }

        adapter = new MyPageAdapter(listViews);
        pager.setAdapter(adapter);
        pager.setCurrentItem(id);
        pager.setPageMargin(10);
        pager.setOffscreenPageLimit(9);


    }

    private OnPageChangeListener pageChangeListener = new OnPageChangeListener() {

        public void onPageSelected(int arg0) {
            back_bt.setText(String.valueOf(arg0+1)+"/"+Bimp.tempSelectBitmap[group_number].size());
            location = arg0;
        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        public void onPageScrollStateChanged(int arg0) {

        }
    };

    private void initListViews(Bitmap bm) {
        if (listViews == null)
            listViews = new ArrayList<View>();
        PhotoView img = new PhotoView(this);
        img.setImageBitmap(bm);
        img.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        listViews.add(img);
    }



    // 删除按钮添加的监听器
    private class DelListener implements OnClickListener {

        public void onClick(View v) {
            if (listViews.size() == 1) {
                showDialogDeleteGroup();
            } else {

                Bimp.deletePic(PreviewPhotoActivity.this,group_number,location);
                Toast.makeText(PreviewPhotoActivity.this,"照片已删除",Toast.LENGTH_SHORT).show();
                pager.removeAllViews();
                listViews.remove(location);
                adapter.setListViews(listViews);
                back_bt.setText(location+1+"/"+Bimp.tempSelectBitmap[group_number].size());
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void showDialogDeleteGroup() {
        new AlertDialog.Builder(this)
                .setTitle("删除壁纸集")
                .setMessage("这是该壁纸集最后一张壁纸。删除后将同时删除该壁纸集，是否继续？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Bimp.deleteGroup(group_number);
                        back_bt.setText("全部删除");
                        Intent intent = new Intent("data.broadcast.action");
                        sendBroadcast(intent);
                        Intent intent1 = new Intent(PreviewPhotoActivity.this, Main.class);
                        startActivity(intent1);
                        //这里是确定后的步骤
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //点击取消
                        finish();
                    }
                }).setCancelable(false).show();
    }



    class MyPageAdapter extends PagerAdapter {

        private ArrayList<View> listViews;

        private int size;

        public MyPageAdapter(ArrayList<View> listViews) {
            this.listViews = listViews;
            size = listViews == null ? 0 : listViews.size();
        }

        public void setListViews(ArrayList<View> listViews) {
            this.listViews = listViews;
            size = listViews == null ? 0 : listViews.size();
        }

        public int getCount() {
            return size;
        }

        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(listViews.get(position % size));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            try {
                container.addView(listViews.get(position % size), 0);
            } catch (Exception e) {
            }
            return listViews.get(position %size);
        }

        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }
    }

}
