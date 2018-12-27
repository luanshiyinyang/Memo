package com.zc.memo.activity;

/**
 * Created by 16957 on 2018/10/31.
 */

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.zc.memo.R;
import com.zc.memo.item.Note;
import com.zc.memo.manager.NoteManager;

public class ContentActivity extends AppCompatActivity {
    private Context mContext;

    private TextView tvTitle;
    private TextView tvDate;
    private EditText etContent;
    private ImageView ivEdit;
    private ImageView ivDelete;

    private Note note;
    private String title;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        mContext = this;
        intent = this.getIntent();
        bindViews();
        title = intent.getStringExtra("title");
    }
    @Override
    protected void onResume() {
        super.onResume();
        initViews();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            title = data.getStringExtra("now_title");
            onResume();
        }
        catch (Exception e1){
            title = intent.getStringExtra("title");
        }
    }

    private void bindViews() {
        tvTitle = (TextView) findViewById(R.id.tv_tb_title);
        tvDate = (TextView) findViewById(R.id.date_content);
        etContent = (EditText) findViewById(R.id.editor);
        ivEdit = (ImageView) findViewById(R.id.iv_bb_edit);
        ivDelete = (ImageView) findViewById(R.id.iv_bb_delete);
    }
    private void initViews(){
        note = new NoteManager(mContext).get(title);
        tvTitle.setText(note.getTitle());
        tvDate.setText(note.getCreate_date());
        etContent.setText(note.getText());
        etContent.setFocusable(false);
        etContent.setFocusableInTouchMode(false);
        initBottom();
    }

    private void initBottom() {
        //编辑
        ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,CreateActivity.class);
                intent.putExtra("title",title);
                startActivityForResult(intent,1);
                onResume();
            }
        });
        //删除
        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("确认删除")
                        .setMessage("将要删除"+title)
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
                                new NoteManager(mContext).delete(title);
                                finish();
                            }
                        })
                        .create().show();
            }
        });
    }

}
