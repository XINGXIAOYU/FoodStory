package com.example.xingxiaoyu.fdstory;


import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by xingxiaoyu on 17/4/29.
 */

public class MyFootPrintFragment extends Fragment {
    private String urls[] = {
            "http://farm8.staticflickr.com/7232/6913504132_a0fce67a0e_c.jpg",
            "http://farm8.staticflickr.com/7232/6913504132_a0fce67a0e_c.jpg",
    };
    @Bind(R.id.bmapView)
    MapView mMapView;
    @Bind(R.id.record)
    TextView mRecord;
    private View popView = null;//弹出框视图
    private BaiduMap mBaiduMap;
    private LatLng[] latLngs;//坐标集合
    private Marker[] Markers;//mark 集合
    private InfoWindow mInfoWindow;
    BitmapDescriptor bd = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_gcoding);

    public static MyFootPrintFragment newInstance() {
        MyFootPrintFragment fragment = new MyFootPrintFragment();
        return fragment;
    }

    public MyFootPrintFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myfootprint, container, false);
        ButterKnife.bind(this, view);
        mRecord.setBackgroundColor(Color.WHITE);
        mRecord.getBackground().setAlpha(180);
        popView = LayoutInflater.from(this.getActivity()).inflate(R.layout.infopopup, null);
        mMapView.getChildAt(2).setPadding(0, 0, 0, 200);//这是控制缩放控件的位置

        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        LatLng cenpt = new LatLng(31.2455045690,121.5064839346);//默认中心点 东方明珠
        //定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder()
                .target(cenpt)
                .zoom(18)
                .build();
        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化


        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        mBaiduMap.setMapStatus(mMapStatusUpdate);
        mRecord.setText("已在15个地方留下美食足迹");
        initOverlay();
        return view;
    }

    public void initOverlay() {
        // add marker overlay
        //连接数据库 获得总共的位置数量 假设为1
        int num = 2;
        double[][] locations = new double[num][2];
        locations[0][0] = 31.2455045690;
        locations[0][1] = 121.5064839346;
        locations[1][0] = 39.942821;
        locations[1][1] = 116.369199;
        latLngs = new LatLng[num];
        Markers = new Marker[num];
        for (int i = 0; i < num; i++) {
            double v1 = locations[i][0];
            double v2 = locations[i][1];
            latLngs[i] = new LatLng(v1, v2);//设置位置
            MarkerOptions ooA = new MarkerOptions().position(latLngs[i]).icon(bd)
                    .zIndex(9).draggable(false);
            Markers[i] = (Marker) (mBaiduMap.addOverlay(ooA));
            ImageView popImageView = (ImageView) popView.findViewById(R.id.pop_imageView);
            popImageView.setImageURI(Uri.parse(urls[i]));
            TextView title = (TextView) popView.findViewById(R.id.article_title);
            TextView simple_content = (TextView) popView.findViewById(R.id.simple_content);
            title.setText("小小蛋糕");
            simple_content.setText("2016/05/10");
            mInfoWindow = new InfoWindow(popView, latLngs[i], -47);
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
        View hide = (View) popView.findViewById(R.id.info);
        hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBaiduMap.hideInfoWindow();
            }
        });
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            public boolean onMarkerClick(final Marker marker) {
                LatLng ll = marker.getPosition();
                mInfoWindow = new InfoWindow(popView, ll, -47);
                mBaiduMap.showInfoWindow(mInfoWindow);
                return true;
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


}
