package com.example.xingxiaoyu.fdstory;


import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
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
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.example.xingxiaoyu.fdstory.entity.MarkerInfo;
import com.example.xingxiaoyu.fdstory.entity.UserInfo;
import com.example.xingxiaoyu.fdstory.util.ParseInput;
import com.example.xingxiaoyu.fdstory.util.WebIP;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by xingxiaoyu on 17/4/29.
 */

public class MyFootPrintFragment extends Fragment {
    @Bind(R.id.bmapView)
    MapView mMapView;
    @Bind(R.id.record)
    TextView mRecord;
    private View popView = null;//弹出框视图
    private BaiduMap mBaiduMap;
    private List<MarkerInfo> myMarkerInfos = new ArrayList<MarkerInfo>();
    private BitmapDescriptor bd = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_gcoding);
    private ReadInfoTask task;


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
        task = new ReadInfoTask();
        task.execute((Void) null);
        return view;
    }

    public class ReadInfoTask extends AsyncTask<Void, Void, Boolean> {
        HttpURLConnection conn = null;
        InputStream is = null;

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                String path = "http://" + WebIP.IP + "/FDStoryServer/getMyMarkerInfo";
                path = path + "?userEmail=" + UserInfo.email;
                conn = (HttpURLConnection) new URL(path).openConnection();
                conn.setConnectTimeout(3000); // 设置超时时间
                conn.setReadTimeout(3000);
                conn.setDoInput(true);
                conn.setRequestMethod("GET"); // 设置获取信息方式
                conn.setRequestProperty("Charset", "UTF-8"); // 设置接收数据编码格式
                if (conn.getResponseCode() == 200) {
                    is = conn.getInputStream();
                    String responseData = ParseInput.parseInfo(is);
                    //转换成json数据处理
                    JSONArray jsonArray = new JSONArray(responseData);
                    for (int i = 0; i < jsonArray.length(); i++) {       //一个循环代表一个对象
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int id = jsonObject.getInt("myMarkerID");
                        double latitude = jsonObject.getDouble("myMarkerLatitude");
                        double longitude = jsonObject.getDouble("myMarkerLongitude");
                        String image = WebIP.PATH+jsonObject.getString("myMarkerImage");
                        String title = jsonObject.getString("myMarkerTitle");
                        String date = jsonObject.getString("myMarkerDate");
                        myMarkerInfos.add(new MarkerInfo(id, latitude, longitude, image, title, date));

                    }
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // 意外退出时进行连接关闭保护
                if (conn != null) {
                    conn.disconnect();
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            task = null;
            if (success) {
                mRecord.setText("已在" + myMarkerInfos.size() + "个地方留下美食足迹");
                addInfoOverLay(myMarkerInfos);
                initMarkClick();
            }
        }
    }

    public void addInfoOverLay(List<MarkerInfo> infos) {
        mBaiduMap.clear();
        LatLng latLng = null;
        OverlayOptions overlayOptions = null;
        Marker marker = null;
        for (MarkerInfo info : infos) {
            latLng = new LatLng(info.getLatitude(), info.getLongitude());
            overlayOptions = new MarkerOptions().position(latLng).icon(bd)
                    .zIndex(9).draggable(false);
            marker = (Marker) (mBaiduMap.addOverlay(overlayOptions));
            Bundle bundle = new Bundle();
            bundle.putSerializable("info", info);
            marker.setExtraInfo(bundle);
        }
    }

    public void initMarkClick() {
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(final Marker marker) {
                final MarkerInfo info = (MarkerInfo) marker.getExtraInfo().getSerializable("info");
                InfoWindow infoWindow = null;
                View popView = LayoutInflater.from(getActivity()).inflate(R.layout.infopopup, null);
                ImageView popImageView = (ImageView) popView.findViewById(R.id.pop_imageView);
                TextView title = (TextView) popView.findViewById(R.id.article_title);
                TextView simple_content = (TextView) popView.findViewById(R.id.simple_content);
                popImageView.setImageURI(Uri.parse(info.getImage()));
                title.setText(info.getArticleName());
                simple_content.setText(info.getDate());
                LatLng ll = marker.getPosition();
                infoWindow = new InfoWindow(popView, ll, -47);
                mBaiduMap.showInfoWindow(infoWindow);
                View hide = (View) popView.findViewById(R.id.info);
                hide.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBaiduMap.hideInfoWindow();
                    }
                });
                Button more = (Button) popView.findViewById(R.id.button_more);
                more.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent i = new Intent(getActivity(), ArticleActivity.class);
                        i.putExtra("article_id", info.getId());
                        startActivity(i);
                    }
                });
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
