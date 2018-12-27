package com.zc.memo.manager;

/**
 * Created by 16957 on 2018/10/31.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.widget.Toast;

import com.zc.memo.R;
import com.zc.memo.activity.ContentActivity;
import com.zc.memo.activity.CreateActivity;
import com.zc.memo.item.Note;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NoteManager {

    private Context mContext;
    private List<Note> list;
    private String root_file;
    public NoteManager(Context context){
        root_file = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"NoteList";
        this.mContext = context;
        list = getNoteList();
    }

    public void ItemClick(int position){
        final Note select_item = list.get(position);
        ItemClick(select_item);
    }

    public void ItemClick(Note select_item){
        Intent intent = new Intent(mContext,ContentActivity.class);
        intent.putExtra("title", select_item.getTitle());
        mContext.startActivity(intent);
        ((Activity) mContext).overridePendingTransition(R.anim.in_from_right, R.anim.out_from_left);
    }


    public void editClick(int position){
        final Note select_item = new NoteManager(mContext).getNoteList().get(position);
        Intent intent = new Intent(mContext, CreateActivity.class);
        intent.putExtra("title", select_item.getTitle());
        mContext.startActivity(intent);
        Toast.makeText(mContext,"edit",Toast.LENGTH_SHORT).show();
    }
    public void editClick(String noteName){
        final Note select_item = new NoteManager(mContext).get(noteName);
        Intent intent = new Intent(mContext, CreateActivity.class);
        intent.putExtra("title", select_item.getTitle());
        mContext.startActivity(intent);
        Toast.makeText(mContext,"edit",Toast.LENGTH_SHORT).show();
    }
    public void deleteClick(int position){
        Note select_item = new NoteManager(mContext).getNoteList().get(position);
        delete(select_item.getTitle());
        Toast.makeText(mContext,"已删除",Toast.LENGTH_SHORT).show();
    }
    //新建note
    public boolean add(Note note){
            List<Note> current_list = getNoteList();
            boolean flag = false;
            //遍历列表去找寻想要添加的是否主键重复
            for(int i=0;i<current_list.size();i++){
                if(current_list.get(i).getTitle().equals(note.getTitle())){
                    flag = true;
                    break;
                }
            }
            if (flag==false){
                //如果主键不重复，那么正常落地到本地文件中
                current_list.add(note);
                updateNoteList(current_list);
            }
            return flag;
    }

    public Note get(String noteName){
        Note note = new Note("未命名","空");
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(root_file));
            List<Note> currentList = (List<Note>) ois.readObject();
            ois.close();
            for (int i=0;i<currentList.size();i++){
                if (currentList.get(i).getTitle().equals(noteName)){
                    note = currentList.get(i);
                }
            }
            return note;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean update(Note raw_Note, Note new_Note){
        NoteManager noteManager = new NoteManager(mContext);
        //删除原来的，把现在的加进去
        noteManager.delete(raw_Note.getTitle());
        boolean flag = noteManager.add(new_Note);
        return flag;
    }

    public void delete(String filename){
        List<Note> current_list = getNoteList();
        //遍历列表去找寻想要修改的
        Iterator<Note> it = current_list.iterator();
        while(it.hasNext()){
            Note item = it.next();
            if (item.getTitle().equals(filename))
            {
                it.remove();
            }
        }
        updateNoteList(current_list);
    }
    //获得当前的notes列表
    public List<Note> getNoteList(){
        //尝试获取列表文件
        List<Note> note_list;
        try {
            File file = new File(root_file);
            if (!file.exists()){
                file.createNewFile();
                //保证文件绝对存在
            }
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(root_file));
            note_list = (List<Note>) ois.readObject();
            ois.close();
        }
        catch (Exception e){
            note_list = new ArrayList<>();
        }
        return note_list;
    }
    //更新当前notes列表
    public void updateNoteList(List<Note> now_data){
        try {
            File file = new File(root_file);
            if (!file.exists()){
                file.createNewFile();
            }
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(root_file));
            oos.writeObject(now_data);
            oos.close();
        }
        catch (Exception e1){
            e1.printStackTrace();
        }
    }

}
