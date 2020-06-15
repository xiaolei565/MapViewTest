package com.example.paobujilu.DbFlow;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * 文件名：DataBase
 * 描述：采用DBflow数据库
 */
@Database(name=DataBase.NAME,version = DataBase.VERSION)
public class DataBase {
    //数据库名称
    public static final String NAME = "MyDataBase";
    //数据库版本号
    public static final int VERSION = 1;

}
