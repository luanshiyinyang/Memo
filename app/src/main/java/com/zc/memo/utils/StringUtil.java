package com.zc.memo.utils;

/**
 * Created by 16957 on 2018/10/31.
 */

public class StringUtil {
    public static boolean isEmpty(String str){
        if(str==null || str.trim().equals("") )
            return true;
        else return false;
    }

}
