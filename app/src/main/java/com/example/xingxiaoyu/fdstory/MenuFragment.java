package com.example.xingxiaoyu.fdstory;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.xingxiaoyu.fdstory.entity.MenuItemInfo;
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
 * Created by xingxiaoyu on 17/5/6.
 */

public class MenuFragment extends Fragment {
    @Bind(R.id.menu)
    ListView menu;

    List<MenuItemInfo> menuItemList = new ArrayList<MenuItemInfo>();
    private ReadInfoTask task;

    //adapter
    private BaseAdapter mBaseAdapter;

    public static MenuFragment newInstance(int id) {
        MenuFragment fragment = new MenuFragment();
        Bundle args = new Bundle();
        args.putInt("article_id", id);
        return fragment;
    }

    public MenuFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        ButterKnife.bind(this, view);
        task = new ReadInfoTask();
        task.execute((Void) null);
        return view;
    }

    //根据用户的ID获取我的收藏相关信息
    public class ReadInfoTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            HttpURLConnection conn = null;
            InputStream is = null;
            try {
                String path = "http://" + WebIP.IP + "/FDStoryServer/getMenuInfo";
                path = path + "?article_id=" + getArguments().getInt("article_id");
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
                        String image = WebIP.PATH + jsonObject.getString("menuItemImage");
                        String text = jsonObject.getString("menuItemText");
                        menuItemList.add(new MenuItemInfo(image, text));

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
                initAdapter();
            }
        }
    }

    private void initAdapter() {
        mBaseAdapter = new MenuAdapter(getActivity(), menuItemList);
        menu.setAdapter(mBaseAdapter);
    }
}
