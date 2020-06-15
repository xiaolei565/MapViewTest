package com.example.paobujilu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LocationSource, AMapLocationListener {
    private MapView mapView;//实例化地图容器
    private AMap aMap;//实例化AMap
    private MyLocationStyle myLocationStyle;
    private AMapLocationClient mLocationClient;//声明AMapLocationClient类对象
    private AMapLocationClientOption mLocationOption;
    boolean useMoveToLocationWithMapMode = true;
    private OnLocationChangedListener mListener;//定位改变监听器
    private AMapLocation privLocation;
    private Button btnstartrun,btnstoprun,btnpauserun,btngps;
    private Chronometer  mChronometer;//计时器
    private TextView cdtextView;//里程
    private long mRangeTime;
    String strtime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mapView =  findViewById(R.id.map);
        mChronometer = findViewById(R.id.chronometer);
        mChronometer.setFormat("%s");//设置计时格式
        cdtextView = findViewById(R.id.changdu);
        btnstartrun = findViewById(R.id.btn_run);//开始跑步
        btnpauserun = findViewById(R.id.btn_pause);//暂停跑步
        btnstoprun = findViewById(R.id.btn_stop);//停止跑步
        btngps = findViewById(R.id.btn_gps);//打开gps
        // 此方法必须重写，回调该方法
        mapView.onCreate(savedInstanceState);
        //初始化地图
        init();
    }
    /**
     * 初始化AMap对象
     */
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();//显示地图
            setUpMap();//设置地图属性
        }
    }
    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {

        UiSettings uiSettings = aMap.getUiSettings();
        uiSettings.setCompassEnabled(true);// 设置指南针是否显示
        uiSettings.setRotateGesturesEnabled(true);// 设置地图旋转是否可用
        uiSettings.setTiltGesturesEnabled(true);// 设置地图倾斜是否可用
        uiSettings.setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示

        myLocationStyle = new MyLocationStyle();
//        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_on));//设置小蓝点图标
        //设置圆形的边框颜色
        myLocationStyle.strokeColor(Color.argb(50,30,150,180));
        //设置圆形的填充颜色
        myLocationStyle.radiusFillColor(Color.argb(50,30,150,180));
        //设置圆形的边框粗细
        myLocationStyle.strokeWidth(1.0f);
        //定位、且将视角移动到地图中心点,定位点依照设备方向旋转,并且会跟随设备移动。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
        myLocationStyle.showMyLocation(true);
        aMap.setLocationSource(this); // 设置定位监听
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的style
    }
    @Override
    public void onLocationChanged(final AMapLocation aMapLocation) {
        if (mListener != null && aMapLocation != null) {
            if (aMapLocation != null
                    && aMapLocation.getErrorCode() == 0) {
                    LatLng latLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                    // 显示系统小蓝点
                    mListener.onLocationChanged(aMapLocation);
                    //定位,选择移动到地图中心点并修改级别到18级
                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
                    privLocation = aMapLocation;
                    //显示里程
                    strtime=String.valueOf(Distance(aMapLocation));
                    cdtextView.setText(strtime);
                    drawLines(aMapLocation);//绘图

                String errText = "定位成功," + aMapLocation.getLatitude()+ ": " + aMapLocation.getLongitude();
                Log.e("AmapErr",errText);
                } else {
//                    if(useMoveToLocationWithMapMode) {
//                        //二次以后定位，使用sdk中没有的模式，让地图和小蓝点一起移动到中心点（类似导航锁车时的效果）
////                        startMoveLocationAndMap(latLng);
//                    } else {
//                        startChangeLocation(latLng);
//                    }
                }
            } else {
                String errText = "定位失败," + aMapLocation.getErrorCode()+ ": " + aMapLocation.getErrorInfo();
                Log.e("AmapErr",errText);
            }
    }
    /**
     * 绘制运动路线
     *
     * @param curLocation
     */
    public void drawLines(AMapLocation curLocation) {
        if (privLocation==null) {
            return;
        }
            String errText = "定位成功," + curLocation.getLatitude()+ ": " + curLocation.getLongitude()+"" +
                "上次的定位"+privLocation.getLatitude()+":"+privLocation.getLongitude();
            Log.e("AmapErr",errText);
        if (curLocation.getLatitude() != 0.0 && curLocation.getLongitude() != 0.0
                && privLocation.getLongitude() != 0.0 && privLocation.getLatitude() != 0.0) {
            PolylineOptions options = new PolylineOptions();
            //上一个点的经纬度
            options.add(new LatLng(privLocation.getLatitude(), privLocation.getLongitude()));
            //当前的经纬度
            options.add(new LatLng(curLocation.getLatitude(), curLocation.getLongitude()));//画线段
            options.width(15).geodesic(true).color(Color.GREEN);
            aMap.addPolyline(options);
        }
    }

    private double Distance(AMapLocation curLocation) {
        double distance;
        distance = AMapUtils.calculateLineDistance(new LatLng(privLocation.getLatitude(),
                privLocation.getLongitude()), new LatLng(curLocation.getLatitude(),
                curLocation.getLongitude()));
        distance += distance;
        return distance;
    }
    /**
     * 初始化定位函数
     */
    private void initAmapLocation(){

        if (mLocationClient == null) {
            mLocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mLocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //是指定位间隔
            mLocationOption.setInterval(2000);
            //设置定位参数
            mLocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mLocationClient.startLocation();
        }
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        //点击按钮，开始跑步
        btnstartrun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initAmapLocation();//激活定位，开始
                //开始计时
                if(mRangeTime!=0) {
                    mChronometer.setBase(mChronometer.getBase() + (SystemClock.elapsedRealtime() - mRangeTime));
                }else{
                    mChronometer.setBase(SystemClock.elapsedRealtime());
                }
                mChronometer.start();
            }
        });
        btnstoprun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLocationClient.stopLocation();//停止定位
                //@TODO  暂停计时
                mChronometer.stop();

                Toast.makeText(MainActivity.this,mChronometer.getText().toString(),Toast.LENGTH_LONG).show();
                //@TODO 上传数据
            }
        });
        btnpauserun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLocationClient.stopLocation();//停止定位
                //@停止计时
                mChronometer.stop();
                mRangeTime=SystemClock.elapsedRealtime();

            }
        });
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;
    }
    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        useMoveToLocationWithMapMode = true;//暂时没啥用
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
//        deactivate();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if(null != mLocationClient){
            mLocationClient.onDestroy();
        }
    }
}