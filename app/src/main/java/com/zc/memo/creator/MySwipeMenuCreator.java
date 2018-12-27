package com.zc.memo.creator;

/**
 * Created by 16957 on 2018/10/31.
 * 主界面列表侧滑菜单
 */

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuItem;

import com.zc.memo.R;

public class MySwipeMenuCreator implements com.baoyz.swipemenulistview.SwipeMenuCreator {
    private Context mContext;

    public MySwipeMenuCreator(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void create(SwipeMenu menu) {

        //编辑菜单
        SwipeMenuItem editItem = new SwipeMenuItem(mContext.getApplicationContext());
        editItem.setBackground(R.color.blue);
        editItem.setWidth((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 55, mContext.getResources().getDisplayMetrics()));
        editItem.setIcon(R.mipmap.pic_edit);
        editItem.setTitleColor(Color.WHITE);
        menu.addMenuItem(editItem);
        //删除菜单
        SwipeMenuItem deleteItem = new SwipeMenuItem(mContext.getApplicationContext());
        deleteItem.setBackground(R.color.blue);
        deleteItem.setWidth((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 55, mContext.getResources().getDisplayMetrics()));
        deleteItem.setIcon(R.mipmap.pic_delete);
        deleteItem.setTitleColor(Color.WHITE);
        menu.addMenuItem(deleteItem);

    }


}
