package com.zc.memo.activity;

/**
 * Created by 16957 on 2018/10/31.
 */

//安卓原生库
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.Toast;
//第三方库
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
//个人库
import com.zc.memo.R;
import com.zc.memo.adapter.NoteAdapter;
import com.zc.memo.creator.MySwipeMenuCreator;
import com.zc.memo.item.Note;
import com.zc.memo.manager.NoteManager;
import com.zc.memo.utils.ListViewUtil;
import com.zc.memo.view.MyScrollView;
//java原生库
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //控件部分
    private Context context;
    private Toolbar tb;
    private FloatingActionButton fabAdd;
    private FloatingActionButton fabExit;
    private FloatingActionsMenu fabMenu;
    private MyScrollView svMain;
    private SwipeMenuListView smlvMain;
    //数据
    private List<Note> data;
    //请求访问外部存储
    private static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 103;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        data = new ArrayList<>();
        //绑定控件
        bindViews();
        initTb();
        initFab();
        //权限申请
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions((Activity)context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_REQUEST_CODE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
    }


    @Override
    protected void onStart() {
        super.onStart();
        fabMenu.bringToFront();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    //组件初始化
    private void initView() {
        data = new NoteManager(context).getNoteList();
        smlvMain.setAdapter(new NoteAdapter(context,data));
        smlvMain.setMenuCreator(new MySwipeMenuCreator(context));
        smlvMain.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
        new ListViewUtil().setListViewHeightBasedOnChildren(smlvMain);
        svMain.setOnScrollListener(new MyScrollView.ScrollViewListener() {
            @Override
            public void onScroll(int dy) {
                if (dy > 0) {
                    //下滑
                    showOrHideFab(false);
                } else if (dy <= 0 ) {
                    //上滑
                    showOrHideFab(true);
                }
            }
        });
        viewListener();
        emptyListCheck();
    }

    private void initTb() {
        tb.setTitle("Memo");
        tb.setTitleTextColor(Color.BLACK);
        tb.inflateMenu(R.menu.menu_search);
        //搜索的点击事件
        tb.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.item_search:
                        Intent intent1 = new Intent(context, SearchActivity.class);
                        startActivity(intent1);
                        overridePendingTransition(R.anim.in_from_right, R.anim.out_from_left);
                        break;
                }
                return false;
            }
        });
        //设置支持的ActionBar
        //setSupportActionBar(tb);
    }

    private void initFab() {
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FloatingActionsMenu menu = (FloatingActionsMenu)findViewById(R.id.fab_menu);
                menu.collapse();
                Intent intent = new Intent(MainActivity.this,CreateActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_from_left);
                findViewById(R.id.fab_menu).bringToFront();
            }
        });
        fabExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.exit(0);
            }
        });
    }

    private void showOrHideFab(boolean show){
        if(show){
            fabMenu.setVisibility(View.VISIBLE);
        }else{
            fabMenu.setVisibility(View.GONE);
        }

    }

    public void viewListener() {
        //点击监听
        smlvMain.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent,View view,int position,long id){
                new NoteManager(context).ItemClick(position);
            }
        });

        smlvMain.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener(){
            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                //具体实现
                switch (index){
                    case 0:
                        //编辑
                        new NoteManager(context).editClick(position);
                        onResume();
                        break;
                    case 1:
                        //删除
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("确认删除")
                                .setMessage("将要删除"+data.get(position).getTitle())
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        onResume();

                                    }
                                })
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        new NoteManager(context).deleteClick(position);
                                        onResume();
                                    }
                                })
                                .create().show();
                        emptyListCheck();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

    }
    //检查数据列表是否为空，如果为空，那么渲染blank_View
    public void emptyListCheck(){
        int number = 0;
        if(data!=null){
            number=data.size();
        }
        if(number == 0) {
            smlvMain.setVisibility(View.GONE);
            RelativeLayout empty = (RelativeLayout) findViewById(R.id.blank_view);
            empty.setVisibility(View.VISIBLE);
            fabMenu.setVisibility(View.VISIBLE);
        }else{
            smlvMain.setVisibility(View.VISIBLE);
            RelativeLayout empty = (RelativeLayout) findViewById(R.id.blank_view);
            empty.setVisibility(View.GONE);
        }
    }

    private void bindViews() {
        tb = (Toolbar) findViewById(R.id.tb);
        fabAdd = (FloatingActionButton) findViewById(R.id.fab_add);
        fabExit = (FloatingActionButton) findViewById(R.id.fab_exit);
        fabMenu = (FloatingActionsMenu) findViewById(R.id.fab_menu);
        svMain = (MyScrollView) findViewById(R.id.sv_main);
        smlvMain = (SwipeMenuListView) findViewById(R.id.smlv_main);
    }

    //模拟双击退出
    long waitTime = 2000;
    long touchTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(event.getAction() == KeyEvent.ACTION_DOWN && KeyEvent.KEYCODE_BACK == keyCode) {
            long currentTime = System.currentTimeMillis();
            if((currentTime-touchTime) >= waitTime) {
                //让Toast的显示时间和等待时间相同
                Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
                touchTime = currentTime;
            }else {
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
