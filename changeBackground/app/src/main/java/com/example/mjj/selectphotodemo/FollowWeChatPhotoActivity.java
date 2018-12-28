package com.example.mjj.selectphotodemo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mjj.selectphotodemo.beans.ImageItem;
import com.example.mjj.selectphotodemo.dimens.utils.AppUtils;
import com.example.mjj.selectphotodemo.dimens.utils.Density;
import com.example.mjj.selectphotodemo.utils.Bimp;
import com.example.mjj.selectphotodemo.utils.BitmapUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Description：仿微信朋友圈图片选择功能
 * <p>
 * Created by Mjj on 2016/12/2.
 */

public class FollowWeChatPhotoActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PICK_PHOTO = 1;
    private int group_number = 0;
    private EditText ed_name;

    private List<Bitmap> mResults = new ArrayList<>();

    //    public static Bitmap bimap;
    private GridView noScrollgridview;
    private GridAdapter adapter;
    private TextView tv_back;
    private ImageView btn_back;
    private ImageView add_photo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        group_number = getIntent().getIntExtra("group_number", -1);
        initView();
    }

    private void initView() {
        Toast.makeText(this, "点击返回后壁纸集会自动命名", Toast.LENGTH_LONG).show();
        setContentView(R.layout.activity_follow_wechat_photo);
        tv_back = (TextView) findViewById(R.id.text_back);
        btn_back = (ImageView) findViewById(R.id.btn_back);
        add_photo = (ImageView) findViewById(R.id.add_photo);
        ed_name = (EditText) findViewById((R.id.group_name));
        tv_back.setOnClickListener(this);
        btn_back.setOnClickListener(this);
        add_photo.setOnClickListener(this);
        noScrollgridview = (GridView) findViewById(R.id.noScrollgridview);
        noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter = new GridAdapter(this);
        adapter.update();
        noScrollgridview.setAdapter(adapter);
        ed_name.setText(Bimp.group_name[group_number]);


        //设置项目被点击时的事件
        noScrollgridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(FollowWeChatPhotoActivity.this,
                        PreviewPhotoActivity.class);
                intent.putExtra("position", i);
                intent.putExtra("ID", i);
                intent.putExtra("group_number", group_number);

                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PHOTO) {
            if (resultCode == RESULT_OK) {
                ArrayList<String> result = data.getStringArrayListExtra(PhotoPickerActivity.KEY_RESULT);
                showResult(result);
            }
        }
    }

    private void showResult(ArrayList<String> paths) {
        if (mResults == null) {
            mResults = new ArrayList<>();
        }

//        if (paths.size() != 0) {
//            mResults.remove(mResults.size() - 1);
//        }
        for (int i = 0; i < paths.size(); i++) {
            // 压缩图片
            Bitmap bitmap = BitmapUtils.decodeSampledBitmapFromFd(paths.get(i), 400, 500);

            mResults.add(bitmap);

            ImageItem takePhoto = new ImageItem();
            takePhoto.setBitmap(bitmap);

            Bimp.tempSelectBitmap[group_number].add(takePhoto);

            Bimp.savePath(this, paths, group_number);


        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            if (!Bimp.tempSelectBitmap[group_number].isEmpty()) {
                Toast.makeText(this,"已保存",Toast.LENGTH_SHORT).show();
                Bimp.saveName(this, String.valueOf(ed_name.getText()), group_number);
            }
            finish();

            Intent i = new Intent(FollowWeChatPhotoActivity.this, Main.class);
            startActivity(i);
        }
        return true;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_back:
            case R.id.text_back:
                if (!Bimp.tempSelectBitmap[group_number].isEmpty()) {
                    Toast.makeText(this,"已保存",Toast.LENGTH_SHORT).show();
                    Bimp.saveName(this, String.valueOf(ed_name.getText()), group_number);
                }
                finish();

                Intent i = new Intent(FollowWeChatPhotoActivity.this, Main.class);
                startActivity(i);
                break;
            case R.id.add_photo:
                //转到相册中选取
                //页面转换

                Intent intent = new Intent(FollowWeChatPhotoActivity.this, PhotoPickerActivity.class);
                //传输信息
                intent.putExtra("group_number", group_number);
                intent.putExtra(PhotoPickerActivity.EXTRA_SHOW_CAMERA, true);
                intent.putExtra(PhotoPickerActivity.EXTRA_SELECT_MODE, PhotoPickerActivity.MODE_SINGLE);
                intent.putExtra(PhotoPickerActivity.EXTRA_MAX_MUN, PhotoPickerActivity.DEFAULT_NUM);
                // 总共选择的图片数量
                intent.putExtra(PhotoPickerActivity.TOTAL_MAX_MUN, Bimp.tempSelectBitmap[group_number].size());
                startActivityForResult(intent, PICK_PHOTO);
                break;

        }
    }

    /**
     * 适配器
     */
    public class GridAdapter extends BaseAdapter {

        private LayoutInflater inflater;

        public GridAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        public void update() {
            loading();
        }

        public int getCount() {
            if (Bimp.tempSelectBitmap[group_number].size() == 9) {
                return 9;
            }
            return Bimp.tempSelectBitmap[group_number].size();

        }

        public Object getItem(int arg0) {
            return Bimp.tempSelectBitmap[group_number].get(arg0);
        }

        public long getItemId(int arg0) {
            return arg0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_gridview, null);
                holder = new ViewHolder();
                holder.image = (ImageView) convertView
                        .findViewById(R.id.imageView1);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (position == Bimp.tempSelectBitmap[group_number].size()) {
                holder.image.setImageBitmap(BitmapFactory.decodeResource(
                        getResources(), R.drawable.icon_addpic_focused));
                if (position == 9) {
                    holder.image.setVisibility(View.GONE);
                }
            } else {
                holder.image.setImageBitmap(Bimp.tempSelectBitmap[group_number].get(position).getBitmap());

            }
            return convertView;
        }

        public class ViewHolder {
            public ImageView image;
        }

        Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        adapter.notifyDataSetChanged();
                        break;
                }
                super.handleMessage(msg);
            }
        };

        public void loading() {
            new Thread(new Runnable() {
                public void run() {
                    while (true) {
                        if (Bimp.max[group_number] == Bimp.tempSelectBitmap[group_number].size()) {

                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                            break;
                        } else {
                            Bimp.max[group_number] += 1;
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                        }
                    }
                }
            }).start();
        }
    }

    protected void onRestart() {
        adapter.update();
        super.onRestart();
    }

}
