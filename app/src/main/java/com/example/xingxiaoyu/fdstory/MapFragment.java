package com.example.xingxiaoyu.fdstory;


import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.facebook.drawee.backends.pipeline.Fresco;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.content.Context.SENSOR_SERVICE;

/**
 * Created by xingxiaoyu on 17/4/26.
 */

public class MapFragment extends Fragment implements SensorEventListener {

    private String urls[] = {
            "http://farm8.staticflickr.com/7232/6913504132_a0fce67a0e_c.jpg",
    };


    @Bind(R.id.bmapView)
    MapView mMapView;
    //    private View overlayview = null;//mark 视图
    private View popView = null;//弹出框视图
    private BaiduMap mBaiduMap;
    private LatLng[] latLngs;//坐标集合
    private Marker[] Markers;//mark 集合
    private InfoWindow mInfoWindow;
    private BitmapDescriptor bd;

    //定位相关
    LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    private MyLocationConfiguration.LocationMode mCurrentMode;
    private SensorManager mSensorManager;
    private Double lastX = 0.0;
    private int mCurrentDirection = 0;
    private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;
    private float mCurrentAccracy;
    @Bind(R.id.request_location)
    Button requestLocButton;
    boolean isFirstLoc = true; // 是否首次定位
    private MyLocationData locData;

    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
        return fragment;
    }

    public MapFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this.getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.bind(this, view);
        mSensorManager = (SensorManager) this.getActivity().getSystemService(SENSOR_SERVICE);//获取传感器管理服务
        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
        popView = LayoutInflater.from(this.getActivity()).inflate(R.layout.infopopup, null);
        mMapView.getChildAt(2).setPadding(0, 0, 0, 200);//这是控制缩放控件的位置
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setMyLocationEnabled(true);
        LatLng cenpt = new LatLng(31.2455045690, 121.5064839346);//默认中心点 东方明珠
        //定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder()
                .target(cenpt)
                .zoom(18)
                .build();
        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        mBaiduMap.setMapStatus(mMapStatusUpdate);
        initOverlay();
        requestLocButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLocation();
            }
        });
        return view;
    }

    private void startLocation() {
        mLocClient = new LocationClient(this.getActivity());
        mBaiduMap.setMyLocationEnabled(true);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();
    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            mCurrentLat = location.getLatitude();
            mCurrentLon = location.getLongitude();
            mCurrentAccracy = location.getRadius();
            locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }

    public void initOverlay() {
        // add marker overlay
        //连接数据库 获得总共的位置数量 假设为1
        int num = 1;
        double[][] locations = new double[num][2];
        locations[0][0] = 31.2455045690;
        locations[0][1] = 121.5064839346;
        latLngs = new LatLng[num];
        Markers = new Marker[num];
        for (int i = 0; i < num; i++) {
            double v1 = locations[i][0];
            double v2 = locations[i][1];
            latLngs[i] = new LatLng(v1, v2);//设置位置
            ImageView popImageView = (ImageView) popView.findViewById(R.id.pop_imageView);
            popImageView.setImageURI(Uri.parse(urls[i]));
            TextView title = (TextView) popView.findViewById(R.id.article_title);
            TextView simple_content = (TextView) popView.findViewById(R.id.simple_content);
            title.setText("小小蛋糕");
            simple_content.setText("2016/05/10");
            mInfoWindow = new InfoWindow(popView, latLngs[i], -47);
            mBaiduMap.showInfoWindow(mInfoWindow);
        }
        Button more = (Button) popView.findViewById(R.id.button_more);
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ArticleActivity.class);
                i.putExtra("article_name", "这篇文章的ID");
                startActivity(i);
            }
        });
    }


    public Bitmap getRes(String name) {
        ApplicationInfo appInfo = this.getActivity().getApplicationInfo();
        int resID = getResources().getIdentifier(name, "drawable", appInfo.packageName);
        return BitmapFactory.decodeResource(getResources(), resID);
    }

    private Bitmap getViewBitmap(View addViewContent) {

        addViewContent.setDrawingCacheEnabled(true);

        addViewContent.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        addViewContent.layout(0, 0,
                addViewContent.getMeasuredWidth(),
                addViewContent.getMeasuredHeight());

        addViewContent.buildDrawingCache();
        Bitmap cacheBitmap = addViewContent.getDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);

        return bitmap;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}


