package com.zc.memo.adapter;

/**
 * Created by 16957 on 2018/10/31.
 */

import android.content.Context;
import android.widget.BaseAdapter;

import java.util.List;

public abstract class MyBaseAdapter<T> extends BaseAdapter {
    Context context;
    List<T> data;

    public MyBaseAdapter(Context context, List<T> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }
    @Override
    public int getCount() {
        if(data==null) return 0;
        return data.size();
    }
}


