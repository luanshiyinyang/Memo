package com.zc.memo.view;

/**
 * Created by 16957 on 2018/10/31.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class MyScrollView extends ScrollView {
   private ScrollViewListener scrollViewListener = null;

    public MyScrollView(Context context) {
        super(context);
    }

    public MyScrollView(Context context, AttributeSet attrs,
                        int defStyle) {
        super(context, attrs, defStyle);
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setOnScrollListener(ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        if (scrollViewListener != null) {
            if (oldy < y && ((y - oldy) > 15)) {
                scrollViewListener.onScroll(y - oldy);
            } else if (oldy > y && (oldy - y) > 15) {
                scrollViewListener.onScroll(y - oldy);
            }
        }
    }
//这一部分参考了github某个开源项目
    public  interface ScrollViewListener{//dy Y轴滑动距离
        void onScroll(int dy);
    }
}