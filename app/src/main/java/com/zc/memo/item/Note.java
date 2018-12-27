package com.zc.memo.item;

/**
 * Created by 16957 on 2018/10/31.
 */

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

//序列化便于本地存储
public class Note  implements Serializable,Comparable{


    private String title;
    private String create_date;
    private String update_date;
    private String text;

    public Note(String title,String text) {
        this.title = title;
        this.create_date = new SimpleDateFormat("yyyy-MM-dd HH-mm").format(new Date());
        this.update_date = this.create_date;
        this.text = text;
    }

    public Note(String title,String date,String text) {
        this.title = title;
        this.create_date = date;
        this.update_date = this.create_date;
        this.text = text;
    }

    public Note get_copy(){
        return  new Note(getTitle(), getCreate_date(), getText());
    }

    //比较两个对象的创建时间
    @Override
    public int compareTo(Object o) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d_first = null;
        Date d_last = null;
        try {
            d_first = sdf.parse(getCreate_date());
            Note note = (Note) o;
            d_last = sdf.parse(note.getCreate_date());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(d_first.before(d_last))
            return  -1;
        else
            return 1;

    }

    //get和set方法，编辑器生成
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreate_date() {
        return create_date;
    }

    public void setCreate_date(String create_date) {
        this.create_date = create_date;
    }

    public String getUpdate_date() {
        return update_date;
    }

    public void setUpdate_date(String update_date) {
        this.update_date = update_date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}

