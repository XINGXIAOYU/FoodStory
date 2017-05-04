package com.example.xingxiaoyu.fdstory;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.xingxiaoyu.fdstory.entity.Comment;
import com.example.xingxiaoyu.fdstory.entity.UserInfo;
import com.example.xingxiaoyu.fdstory.util.ParseInput;
import com.example.xingxiaoyu.fdstory.util.WebIP;
import com.facebook.drawee.backends.pipeline.Fresco;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by xingxiaoyu on 17/4/26.
 */

public class CommentListFragment extends Fragment {
    @Bind(R.id.comments)
    ListView commentList;
    @Bind(R.id.message)
    EditText message;
    @Bind(R.id.liuyan)
    LinearLayout liuYan;
    @Bind(R.id.cancel)
    Button cancelButton;
    @Bind(R.id.submit)
    Button submitButton;

    //列表数据
    List<Comment> mCommentList = new ArrayList<Comment>();
    //回复的内容
    String info = "";
    //adapter
    BaseAdapter mBaseAdapter;

    public static CommentListFragment newInstance(int param1) {
        //获取文章ID 获取评论
        CommentListFragment fragment = new CommentListFragment();
        Bundle args = new Bundle();
        args.putInt("article_id", param1);
        fragment.setArguments(args);
        return fragment;
    }

    public CommentListFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this.getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comment_list, container, false);
        ButterKnife.bind(this, view);
        initData();
        initAdapter();
        return view;
    }

    private void initData() {
        HttpURLConnection conn = null;
        InputStream is = null;
        try {
            String path = "http://" + WebIP.IP + "/FDStoryServer/getCommentInfo";
            path = path + "?articleID=" + getArguments().getInt("article_id");
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
                    int id = jsonObject.getInt("commentID");
                    String commenter = jsonObject.getString("commenter");
                    String image = jsonObject.getString("commenterImage");
                    String content = jsonObject.getString("commentContent");
                    String date = jsonObject.getString("commentDate");
                    mCommentList.add(new Comment(id, commenter, image, content, date));

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 意外退出时进行连接关闭保护
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private void initAdapter() {
        mBaseAdapter = new CommentAdapter(getActivity(), mCommentList);
        commentList.setAdapter(mBaseAdapter);
    }


    @OnClick({R.id.message, R.id.liuyan, R.id.cancel, R.id.submit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.message:
                break;
            case R.id.liuyan:
                break;
            case R.id.cancel:
                comment(false);
                break;
            case R.id.submit:
                saveComment();
                break;
        }
    }

    private void saveComment() {
        if (!TextUtils.isEmpty(message.getText())) {
            info = message.getText().toString();
            Comment comment = new Comment(getNextCommentID(), UserInfo.name, UserInfo.image, info, getCurrentTime());
            HttpURLConnection conn = null;
            InputStream is = null;
            try {
                String path = "http://" + WebIP.IP + "/FDStoryServer/saveComment";
                conn = (HttpURLConnection) new URL(path).openConnection();
                conn.setConnectTimeout(3000); // 设置超时时间
                conn.setReadTimeout(3000);
                conn.setDoInput(true);
                conn.setRequestMethod("POST"); // 设置获取信息方式
                String data = "commentID" + comment.getId() + "&commenter" + comment.getNickName()
                        + "&image" + comment.getImgUrl() + "&content" + comment.getContent()
                        + "&time" + comment.getTime() + "&articleID" + getArguments().getInt("article_id");
                conn.setRequestProperty("Charset", "UTF-8"); // 设置接收数据编码格式
                conn.setRequestProperty("Content-Length", data.length() + "");
                conn.setDoOutput(true);
                OutputStream outputStream = conn.getOutputStream();
                outputStream.write(data.getBytes());
                outputStream.flush();
                outputStream.close();
                if (conn.getResponseCode() == 200) {
                    updateComment(comment);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // 意外退出时进行连接关闭保护
                if (conn != null) {
                    conn.disconnect();
                }
            }
        } else {
            Toast.makeText(getActivity(), "请输入内容", Toast.LENGTH_SHORT).show();
        }
    }


    private void updateComment(Comment comment) {
        mCommentList.add(0, comment);
        mBaseAdapter.notifyDataSetChanged();
        comment(false);
    }

    private int getNextCommentID() {
        HttpURLConnection conn = null;
        InputStream is = null;
        try {
            String path = "http://" + WebIP.IP + "/FDStoryServer/getNextCommentID";
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
                    int id = jsonObject.getInt("nextCommentID");
                    return id;

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 意外退出时进行连接关闭保护
            if (conn != null) {
                conn.disconnect();
            }
        }
        return 0;
    }

    private String getCurrentTime() {
        HttpURLConnection conn = null;
        InputStream is = null;
        try {
            String path = "http://" + WebIP.IP + "/FDStoryServer/getCurrentTime";
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
                    String time = jsonObject.getString("currentTime");
                    return time;

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 意外退出时进行连接关闭保护
            if (conn != null) {
                conn.disconnect();
            }
        }
        return null;
    }


    private void comment(boolean flag) {
        if (!flag) {
            message.setText("");
            onFocusChange(flag);
        }
    }

    /**
     * 显示或隐藏输入法
     */
    private void onFocusChange(boolean hasFocus) {
        final boolean isFocus = hasFocus;
        (new Handler()).postDelayed(new Runnable() {
            public void run() {
                InputMethodManager imm = (InputMethodManager)
                        getActivity().getSystemService(INPUT_METHOD_SERVICE);
                if (isFocus) {
                    //显示输入法
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    message.setFocusable(true);
                    message.requestFocus();
                } else {
                    //隐藏输入法
                    imm.hideSoftInputFromWindow(message.getWindowToken(), 0);
                }
            }
        }, 100);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
