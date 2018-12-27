package com.zc.memo.activity;

/**
 * Created by 16957 on 2018/10/31.
 */

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zc.memo.item.Note;
import com.zc.memo.R;
import com.zc.memo.manager.NoteManager;
import com.zc.memo.utils.StringUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CreateActivity extends AppCompatActivity implements View.OnClickListener {
    private Context mContext;
    private TextView modelTitle;
    private EditText etTitle;
    private EditText etEditor;
    private TextView tvDate;
   //当前模式，编辑或者新建
    private boolean model;
    private Note editNote;
    private String currentTitle;
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 104;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        mContext = this;
        bindViews();
        init();
        //权限申请
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity)mContext,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
        }

    }

    private void bindViews() {
        modelTitle = (TextView) findViewById(R.id.tv_tb_title);
        etTitle = (EditText) findViewById(R.id.et_create_title);
        tvDate = (TextView) findViewById(R.id.tv_create_date);
        etEditor = (EditText) findViewById(R.id.et_create_editor);
    }

    private void init(){
        Intent intent = this.getIntent();
        currentTitle = "";
        //是否带有标题，有代表编辑模式，否则置为null表示新建模式
        try {
            currentTitle = intent.getStringExtra("title");
        }
        catch (Exception e){
            currentTitle = null;
        }
        if (currentTitle == null){
            //新建模式
            model = false;
        }
        else {
            //编辑模式
            model = true;
            editNote = new NoteManager(mContext).get(currentTitle);
        }
        if (model){
            initEditView();
        }
        else {
            initCreateView();
        }
        initToolbar();
    }

    private void initEditView(){
        modelTitle.setText("编辑备忘录");
        etTitle.setText(editNote.getTitle());
        etEditor.setText(editNote.getText());
        etEditor.setTextSize(14);
        etEditor.setHint("在这里写下内容");
        tvDate.setText(editNote.getUpdate_date());
    }

    private void initCreateView(){
        modelTitle.setText("新建备忘录");
        etEditor.setTextSize(14);
        etEditor.setHint("在这里写下内容");
        tvDate.setText(new SimpleDateFormat("yyyy-MM-dd HH-mm").format(new Date()));
    }

    private void backNoChange(){
        Intent intent = new Intent();
        intent.putExtra("now_title",currentTitle);
        setResult(1,intent);
        finish();
        overridePendingTransition(R.anim.in_from_left, R.anim.out_from_left);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch(keyCode){
            case KeyEvent.KEYCODE_BACK:
                backNoChange();
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

    private void initToolbar(){
        Toolbar toolbar= (Toolbar) findViewById(R.id.tb);
        toolbar.setNavigationIcon(R.mipmap.pic_deleteall);//设置取消图标
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backNoChange();
            }
        });

        toolbar.inflateMenu(R.menu.menu_create);//设置右上角的填充菜单
        if(model) {//编辑模式
            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                        NoteManager noteManager = new NoteManager(mContext);
                        Note newNote = new Note(etTitle.getText().toString(), tvDate.getText().toString(),
                                etEditor.getText().toString());
                        boolean flag = noteManager.update(editNote, newNote);
                        if (flag){
                            //代表想要更新的主键重复，此时等待
                            Toast.makeText(mContext, "标题已经存在，请修改", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(mContext, "已经保存", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();
                            intent.putExtra("now_title",newNote.getTitle());
                            setResult(1,intent);
                            finish();
                            manageKeyBoard();
                        }

                    return false;
                }
            });
        }else {//新建模式
            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                        String title_name = etTitle.getText().toString();
                        if(StringUtil.isEmpty(title_name)){
                            title_name = "未命名";
                        }
                        Note create_note = new Note(title_name, new SimpleDateFormat("yyyy-MM-dd").format(new Date()), etEditor.getText().toString());
                        NoteManager noteManager = new NoteManager(mContext);
                        boolean flag = noteManager.add(create_note);
                        manageKeyBoard();
                        if (flag){
                         //存在主键重复
                            Toast.makeText(mContext, "该标题已存在，不可重复使用，请重新输入标题", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(mContext,"保存了",Toast.LENGTH_SHORT).show();
                            finish();
                            manageKeyBoard();
                        }
                    return false;
                }
            });
        }
    }

    @Override
    public void onClick(View v) {

    }

    private void manageKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
