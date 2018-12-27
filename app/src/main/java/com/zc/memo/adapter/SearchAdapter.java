package com.zc.memo.adapter;

/**
 * Created by 16957 on 2018/10/31.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.zc.memo.item.Note;
import com.zc.memo.R;

import java.util.List;


public class SearchAdapter extends ArrayAdapter<Note> {

    private Context mContext;
    public SearchAdapter(Context context, List<Note> data){
        super (context, R.layout.item_search,data);
        this.mContext=context;
    }

    @Override
    public View getView(int position , View convertView , ViewGroup parent) {
        Note note = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_search,parent,false);
        TextView title = (TextView) view.findViewById(R.id.tv_search_result_title);
        TextView date = (TextView) view.findViewById(R.id.tv_search_result_date);
        TextView content = (TextView)view.findViewById(R.id.tv_search_result_content);
        if(title!=null)title.setText(note.getTitle());
        if(date!=null)date.setText(note.getCreate_date());
        String simpleContent = note.getText();
        if (simpleContent.length()<50){
            content.setText(simpleContent);
        }else{
            content.setText(simpleContent.substring(0,49));
        }
        return view;

    }
}



