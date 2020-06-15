package com.example.paobujilu.DbFlow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * 文件名：MyDataBase
 * 描述：运动记录📝 ID、time、distance、duration。
 */

@Table(database = DataBase.class)
public class MyDataBase extends BaseModel {
    @PrimaryKey(autoincrement = true)
    public int id=0;//ID

    public double distance;//距离
    @Column
    public String time;//时间

    @Column
    public long duration;//持续时间


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }


}
