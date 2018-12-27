package com.zc.memo.activity;

/**
 * Created by 16957 on 2018/10/31.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.quinny898.library.persistentsearch.SearchBox;
import com.quinny898.library.persistentsearch.SearchResult;

import com.zc.memo.R;
import com.zc.memo.adapter.HistoryAdapter;
import com.zc.memo.adapter.SearchAdapter;
import com.zc.memo.item.Note;
import com.zc.memo.manager.NoteManager;
import com.zc.memo.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private Context mContext;
    private SearchBox sbSearch;
    private TextView tvBottom;
    //数据列表
    private List<Note> listSearch;
    //结果列表
    private List<Note> listResult;

    private ListView mSearchResult ;
    private SearchAdapter mResultAdapter ;

    private ListView mHistory ;
    private HistoryAdapter mHistoryAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mContext = this;
        bindViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
        init();
        initSearchView();
        initHistory();
        updateBottom();
    }

    private void initData() {
        //本地可供检索数据获取,每次resume就要重新渲染
        listSearch = new NoteManager(mContext).getNoteList();
    }

    private void bindViews() {
        mSearchResult = (ListView) findViewById(R.id.content_lis_search);
        sbSearch = (SearchBox) findViewById(R.id.searchbox);
        tvBottom = (TextView) findViewById(R.id.bottom_search);
        mHistory = (ListView) findViewById(R.id.history_lis_search);
    }

    public void init(){
        listResult = new ArrayList<>();
        mResultAdapter = new SearchAdapter(SearchActivity.this, listResult);
        mSearchResult.setAdapter(mResultAdapter);
        mSearchResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                manageKeyBoard();
                NoteManager noteManager=new NoteManager(mContext);
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                boolean isOpen = imm.isActive();
                if (isOpen) imm.hideSoftInputFromWindow(view.getWindowToken(), 0); //强制隐藏键盘
                noteManager.ItemClick(listResult.get(position));
            }
        });
    }

    private void initSearchView(){
        sbSearch.enableVoiceRecognition(this);
        sbSearch.setMenuListener(new SearchBox.MenuListener(){
            @Override
            public void onMenuClick() {
                reBack();
            }
        });
        sbSearch.setSearchListener(new SearchBox.SearchListener(){

            @Override
            public void onSearchOpened() {
                //Use this to tint the screen
            }
            @Override
            public void onSearchClosed() {
                //Use this to un-tint the screen
            }
            @Override
            public void onSearchTermChanged(String term) {
                search(term);
                updateBottom();

                if(listResult.size()==0){
                    mHistory.setVisibility(View.VISIBLE);
                }else {
                    mHistory.setVisibility(View.GONE);
                }
            }
            @Override
            public void onSearch(String searchTerm) {
                search(searchTerm);
                saveHistory(searchTerm);
                initHistory();
                updateBottom();
            }
            @Override
            public void onResultClick(SearchResult result) {
                //React to a result being clicked
            }
            @Override
            public void onSearchCleared() {
                //Called when the clear button is clicked
            }

        });
    }

    private void initHistory(){

        final List<String> history = getHistory();


        mHistoryAdapter = new HistoryAdapter(SearchActivity.this,history);

        mHistory.setAdapter(mHistoryAdapter);

        mHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                sbSearch.populateEditText(history.get(position));
                sbSearch.setSearchString(history.get(position));


            }
        });

    }

    private void search(String newText){

        //若搜索内容为空
        if(newText.isEmpty()){
            listResult.clear();
        }
        else{

            for (Note note : listSearch) {//开始搜索

                //搜索内容搜索到相关
                if (note.getTitle().contains(newText) || note.getText().contains(newText)) {

                    if(listResult.indexOf(note)==-1) {//且 结果集内不含有此内容
                        listResult.add(note);
                    }
                }else{
                    //搜索内容搜索不到相关 检测是否之前有加入结果集 有则删除
                    if(listResult.indexOf(note)!=-1) {
                        listResult.remove(note);
                    }
                }
            }
        }
        mResultAdapter.notifyDataSetChanged();
    }



    private ArrayList<String> getHistory() {
        SharedPreferences reader = getSharedPreferences("history", MODE_PRIVATE);
        String data = reader.getString("data_history", "");
        ArrayList<String> history = new ArrayList<>();
        String [] get=  data.split("\\|");
        for( String str:get){
            if(! history.contains(str) && !StringUtil.isEmpty(str)){
                history.add(str);
            }
        }
        return history;
    }

    private void saveHistory(String s){
        StringBuilder sb = new StringBuilder();
        SharedPreferences.Editor editor = getSharedPreferences("history",MODE_PRIVATE).edit();
        for (String str: getHistory()){
            sb.append(str);
            sb.append("|");
        }
        sb.append(s);
        editor.putString("data_history",sb.toString());
        editor.apply();
    }


    private void updateBottom(){
        if(sbSearch.getSearchText().trim().isEmpty()){
            gone_Bottom();
            return;
        }
        tvBottom.setVisibility(View.VISIBLE);
        tvBottom.setText("找到了 "+ listResult.size() +" 条记录");

    }
    private void gone_Bottom(){
        TextView bottom = (TextView) findViewById(R.id.bottom_search);
        bottom.setVisibility(View.GONE);
    }

    private void reBack(){
        finish();
        overridePendingTransition(R.anim.in_from_left, R.anim.out_from_right);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch(keyCode){
            case KeyEvent.KEYCODE_BACK:
                finish();
                overridePendingTransition(R.anim.in_from_left, R.anim.out_from_right);
                break;
        }
        return super.onKeyUp(keyCode, event);
    }
    private void manageKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
