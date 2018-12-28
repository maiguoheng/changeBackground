package com.example.mjj.selectphotodemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import org.jetbrains.annotations.Nullable;

import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mjj.selectphotodemo.beans.ImageItem;
import com.example.mjj.selectphotodemo.dimens.utils.AppUtils;
import com.example.mjj.selectphotodemo.dimens.utils.Density;
import com.example.mjj.selectphotodemo.utils.Bimp;

import net.steamcrafted.loadtoast.LoadToast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.mjj.selectphotodemo.utils.Bimp.group_name;
import static com.example.mjj.selectphotodemo.utils.Bimp.group_name_forselect;
import static com.example.mjj.selectphotodemo.utils.Bimp.tempSelectBitmap;


public class Main extends Activity implements View.OnClickListener {
    private boolean CanStart = false;
    private int group_number = 0;
    private GridView noScrollgridview;
    private Main.GridAdapter adapter;
    //    private List<Bitmap> mGroupResults = new ArrayList<>();
    private ImageView add_group;
    private static boolean hasInit = false;
    private Spinner spinner;
    private Intent intent1 = null;
    private Switch start_button;


    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Density.setDefault(this);
        if (!hasInit) {
            hasInit = true;
            Bimp.init(this);
        }
        initView();
    }

    private void initView() {

        setContentView(R.layout.activity_for_main);
        add_group = (ImageView) findViewById(R.id.add_group);
        add_group.setOnClickListener(this);
        noScrollgridview = (GridView) findViewById(R.id.noScrollgridview);
        noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));

        adapter = new Main.GridAdapter(this);
        adapter.update();
        noScrollgridview.setAdapter(adapter);

        //设置项目被点击时的事件
        noScrollgridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                finish();

                Intent intent = new Intent(Main.this, FollowWeChatPhotoActivity.class);
                intent.putExtra("group_number", i);
                startActivity(intent);
            }
        });
        Bimp.update_nameforselect();


        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.layout2, Bimp.group_name_forselect);

        //设置下拉样式以后显示的样式
        adapter.setDropDownViewResource(R.layout.layout);

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                if (CanStart) {
                    EditText get_time = findViewById(R.id.get_time);
                    Editable text = get_time.getText();
                    String time = String.valueOf(text);
                    String times[] = time.split(":");
                    int m_time = Integer.parseInt(times[0]) * 3600000 + Integer.parseInt(times[1]) * 60000 + Integer.parseInt(times[2]) * 1000;

                    //Toast.makeText(Main.this, "你点击的是" + String.valueOf(pos + 1) + ":" + group_name[pos] + " 更换时间" + m_time / 1000 + "秒", 2000).show();
                    makeToast("你点击的是" + String.valueOf(pos + 1) + ":" + group_name[pos] + " 更换时间" + m_time / 1000 + "秒", 5000);


                    if (intent1 != null) {
                        stopService(intent1);
                    }

                    begin(pos, m_time);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });

        start_button = findViewById(R.id.isStart_Button);
        start_button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    CanStart = true;

                    //Toast.makeText(Main.this, "开启换壁纸功能", 2000).show();
                    makeToast("开启换壁纸功能", 2000);
                } else {
                    CanStart = false;
                    //Toast.makeText(Main.this, "关闭换壁纸功能", 2000).show();
                    makeToast("关闭换壁纸功能", 2000);
                    if (intent1 != null) {
                        stopService(intent1);
                    }

                }
            }
        });

    }

    private void makeToast(String text, int delay) {
        final LoadToast lt = new LoadToast(Main.this);
        lt.setText(text);

        lt.show();
        new Handler().postDelayed(new Runnable() {
            public void run() {
                lt.success();

            }
        }, delay);

    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(Main.this, FollowWeChatPhotoActivity.class);
        switch (view.getId()) {
            case R.id.add_group:
                finish();
                intent.putExtra("group_number", Bimp.current_group_number);
                startActivity(intent);
        }
    }

    private void begin(int pos, int time) {
        System.out.print("点击");
        intent1 = new Intent(this, changepaper.class);

        Bundle bundle = new Bundle();

        bundle.putInt("pos", pos);
        bundle.putInt("time", time);
        intent1.putExtras(bundle);//发送数据
        startService(intent1);

    }

    public class GridAdapter extends BaseAdapter {

        private LayoutInflater inflater;

        public GridAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        public void update() {
            loading();
        }

        public int getCount() {
            return (Bimp.current_group_number);

        }

        public Object getItem(int arg0) {
            return tempSelectBitmap[arg0].get(0);
//            return mGroupResults.get(arg0);
        }

        public long getItemId(int arg0) {
            return arg0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            Main.GridAdapter.ViewHolder holder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.group_gridview, null);
                holder = new Main.GridAdapter.ViewHolder();
                holder.image = (ImageView) convertView
                        .findViewById(R.id.imageView1);
                holder.tv = (TextView) convertView
                        .findViewById(R.id.name);
                convertView.setTag(holder);
            } else {
                holder = (Main.GridAdapter.ViewHolder) convertView.getTag();
            }

            if (!tempSelectBitmap[position].isEmpty()) {
                holder.tv.setText(String.valueOf(position + 1) + ":" + Bimp.group_name[position]);
                holder.image.setImageBitmap(Bimp.tempSelectBitmap[position].get(0).getBitmap());
            }

            return convertView;
        }

        public class ViewHolder {
            public ImageView image;
            public TextView tv;
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
                        if (Bimp.max[group_number] == tempSelectBitmap[group_number].size()) {

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
