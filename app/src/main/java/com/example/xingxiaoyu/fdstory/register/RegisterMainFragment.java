package com.example.xingxiaoyu.fdstory.register;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xingxiaoyu.fdstory.R;
import com.example.xingxiaoyu.fdstory.WelcomeActivity;


/**
 * Created by xingxiaoyu on 17/4/19.
 */

public class RegisterMainFragment extends Fragment {
    private UserRegisterTask mAuthTask = null;
    private EditText mNameView;
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private EditText mConfirmPasswordView;
    private Button mEmailRegisterInButton;
    private RegisterActivity registerActivity;
    private View mProgressView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_register_fragment, container, false);
        return rootView;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        registerActivity = (RegisterActivity) this.getActivity();
        mNameView = (EditText)getView().findViewById(R.id.nickname);
        mEmailView = (AutoCompleteTextView) getView().findViewById(R.id.email);
        mPasswordView = (EditText)getView().findViewById(R.id.password);
        mConfirmPasswordView = (EditText)getView().findViewById(R.id.confirm_password);
        mConfirmPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptRegister();
                    return true;
                }
                return false;
            }
        });
        mEmailRegisterInButton = (Button)getView().findViewById(R.id.email_register_in_button);
        mEmailRegisterInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });
    }

    private void attemptRegister() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mNameView.setError(null);
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mConfirmPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String name = mNameView.getText().toString();
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String confirmPassword = mConfirmPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid name
        if (TextUtils.isEmpty(name)) {
            mNameView.setError(getString(R.string.error_field_required));
            focusView = mNameView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid confirm password
        if (TextUtils.isEmpty(confirmPassword)) {
            mConfirmPasswordView.setError(getString(R.string.error_field_required));
            focusView = mConfirmPasswordView;
            cancel = true;
        } else if (!isConfirmPasswordValid(password,confirmPassword)) {
            mConfirmPasswordView.setError(getString(R.string.error_incorrect_password2));
            focusView = mConfirmPasswordView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            registerActivity.setContentView(R.layout.load);
            mProgressView = registerActivity.findViewById(R.id.load_progress);
            showProgress(true);
            mAuthTask = new UserRegisterTask(name, email, password);
            mAuthTask.execute((Void) null);
        }
    }
    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 5;
    }

    private boolean isConfirmPasswordValid(String password,String password2) {
        //TODO: Replace this with your own logic
        return password.equals(password2);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int mediumAnimTime = getResources().getInteger(android.R.integer.config_mediumAnimTime);
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(mediumAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {
        private final String mName;
        private final String mEmail;
        private final String mPassword;

        UserRegisterTask(String name, String email, String password) {
            mName = name;
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
           /* // TODO: attempt authentication against a network service.
            final  String SERVICE_NS = "http://tempuri.org/"; //Webservice所在命名空间
            final  String SERVICE_URL = "http://192.168.1.213:9006/WS_Base.asmx";//Webservice服务地址
            final  String methodName = "AuthenticateRegister";//要使用的接口函数final 注册账号
            final String methodName2 = "Register";//注册
            HttpTransportSE ht; //该对象用于调用WebService操作
            SoapSerializationEnvelope envelope;//上一个类信息的载体
            SoapObject soapObject; //将参数传递给WebService
            ht = new HttpTransportSE(SERVICE_URL);
            ht.debug = true;
            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapObject = new SoapObject(SERVICE_NS,methodName);
            soapObject.addProperty("userName",params[0]);
            soapObject.addProperty("userEmail",params[1]);
            soapObject.addProperty("password",params[2]);
            envelope.bodyOut = soapObject;
            envelope.dotNet = true;
            try{
                //调用远程web service 注册账号
                ht.call(SERVICE_NS+methodName,envelope);
                if(envelope.getResponse()!=null) {
                    SoapObject result = (SoapObject) envelope.bodyIn;
                    Object details1 = (Object) result.equals("true");
                    return details1.toString().equals("true");
                }
            }catch (IOException e){
                e.printStackTrace();
            }catch (XmlPullParserException e){
                e.printStackTrace();
            }
            return false;*/
            try {
                // Simulate network access.
                Thread.sleep(200);
            } catch (InterruptedException e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                Intent intent = new Intent(registerActivity, WelcomeActivity.class);
                registerActivity.startActivity(intent);
                registerActivity.finish();
            } else {
                Toast.makeText(registerActivity, "该邮箱已被注册", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }


}
