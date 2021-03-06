package com.cwenhui.chowhound.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.http.Header;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.cwenhui.chowhound.bean.LoginActionItem;
import com.cwenhui.chowhound.config.Configs;
import com.cwenhui.chowhound.utils.HttpUtil;
import com.cwenhui.chowhound.utils.SharedPreferencesHelper;
import com.cwenhui.chowhound.widget.CustomQuickAction.OnClickQuickActionListener;
import com.cwenhui.chowhound.widget.LoginQuickAction;
import com.example.chowhound.R;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class LoginActivity extends Activity implements TextWatcher, OnClickListener, OnClickQuickActionListener{
	private static final String TAG = "LoginActivity";
	private EditText username, password;
	private ImageView usernameClean, pwdClean;
	private boolean isShowPwd = false;
	private Button showUsers, back, showPwd, enter, register;
	private SharedPreferencesHelper share;
	private List<String> usernames;
	private LoginQuickAction loginUser;							//历史账号
	private LoginActionItem actionItem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		initData();
		initView();
	}

	private void initData() {
		share = SharedPreferencesHelper.getInstance(this);
		usernames = new ArrayList<String>();
	}

	private void initView() {
		loginUser = new LoginQuickAction(this);
		username = (EditText) findViewById(R.id.et_activity_login_username);
		password = (EditText) findViewById(R.id.et_activity_login_password);
		usernameClean = (ImageView) findViewById(R.id.iv_activity_login_username_clean);
		pwdClean = (ImageView) findViewById(R.id.iv_activity_login_pwd_clean);
		showUsers = (Button) findViewById(R.id.btn_activity_login_toggle_userlist);
		showPwd = (Button) findViewById(R.id.btn_activity_login_show_pwd);
		enter = (Button) findViewById(R.id.btn_activity_login_enter);
		register = (Button) findViewById(R.id.btn_activity_login_regist);
		back = (Button) findViewById(R.id.btn_activity_login_back);
		
		if(share.getStringSetValue(Configs.USERNAMES)!=null){									//用户之前登录过
			usernames.addAll(share.getStringSetValue(Configs.USERNAMES));
			username.setText(share.getStringValue(Configs.CURRENT_USER));						//填写之前用过的账号
			usernameClean.setVisibility(View.VISIBLE);											//显示清除账号按钮
			for (int i = 0; i < usernames.size(); i++) {										//为QuickAction适配内容
				actionItem = new LoginActionItem(this, usernames.get(i), i);
				loginUser.addQuickActionItem(actionItem);
			}
		}
		
		loginUser.setOnClickQuickActionListener(this);
		showUsers.setOnClickListener(this);
		enter.setOnClickListener(this);
		showPwd.setOnClickListener(this);
		usernameClean.setOnClickListener(this);
		pwdClean.setOnClickListener(this);
		register.setOnClickListener(this);
		back.setOnClickListener(this);
		username.addTextChangedListener(this);
		password.addTextChangedListener(this);
	}

	@Override
	public void afterTextChanged(Editable t) {
		if(username.getText().toString().equals("")){
			usernameClean.setVisibility(View.GONE);
		}else{
			usernameClean.setVisibility(View.VISIBLE);
		}
		if(password.getText().toString().equals("")){
			pwdClean.setVisibility(View.GONE);
		}else{
			pwdClean.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { 
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.btn_activity_login_enter:
			String userName = username.getText().toString().trim();
			String pwd = password.getText().toString().trim();
			if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(pwd)) {
				checkUser(userName, pwd);
			}else{
				Toast.makeText(LoginActivity.this, "账号密码不能为空", Toast.LENGTH_SHORT).show();
			}
			break;
			
		case R.id.btn_activity_login_regist:
			intent = new Intent(LoginActivity.this, RegisterActivity.class);
			startActivityForResult(intent, RESULT_FIRST_USER);
			break;

		case R.id.btn_activity_login_toggle_userlist:
			if (null != loginUser) {
				loginUser.show((Button) findViewById(R.id.btn_activity_login_toggle_userlist));
			}
			break;
			
		case R.id.iv_activity_login_username_clean:
			username.setText("");
			break;
			
		case R.id.iv_activity_login_pwd_clean:
			password.setText("");
			break;
			
		case R.id.btn_activity_login_show_pwd:
			if(!isShowPwd){
				isShowPwd = true;
				showPwd.setBackgroundResource(R.drawable.alimember_btn_show_password);
				password.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_NORMAL);
			}else{
				isShowPwd = false;
				showPwd.setBackgroundResource(R.drawable.alimember_btn_hide_password);
				password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
			}
			break;
		case R.id.btn_activity_login_back:
			finish();
		default:
			break;
		}
	}
	
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==RESULT_FIRST_USER && resultCode==RESULT_OK){
			username.setText(data.getStringExtra("username"));
		}
	}

	/**
	 * 检测用户登录情况
	 * @param userName
	 * @param pwd
	 */
	private void checkUser(final String userName, String pwd) {
		// 创建请求参数的封装的对象  
        RequestParams params = new RequestParams();  
        params.put("username", userName); // 设置请求的参数名和参数值  
        params.put("password", pwd);// 设置请求的参数名和参数  
		HttpUtil.post(Configs.APILogin, params, new AsyncHttpResponseHandler() {
			
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] data) {
				if(new String(data).equals("failure")){
					Toast.makeText(LoginActivity.this, "账号或密码错误", Toast.LENGTH_SHORT).show();
				}else{ 
					usernames.add(userName);												//将用户名保存在usernames中
					Set<String> users = new HashSet<String>();
					users.addAll(usernames);												//将usernames转化成集合Set
					share.setStringSetValue(Configs.USERNAMES, users);						//将usernames保存在SharedPreferenced中
					share.setBooleanValue(Configs.LOGIN_STATE, true);						//将登陆状态保存在SharedPreferenced中
					share.setStringValue(Configs.CURRENT_USER_ID, new String(data));		//将uid保存在SharedPreferenced中
					share.setStringValue(Configs.CURRENT_USER, username.getText().toString());	//将当前用户名保存到SharedPreferenced中
					 
					Intent intent = new Intent();
					intent.putExtra("username", username.getText());						//传递用户名到MineFragment中
					setResult(RESULT_OK, intent);
					finish();
				}
			}
			
			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable throwable) {
				Log.v(TAG, "throwable:"+throwable.toString());
			}
		});
	}

	@Override
	public void OnClickQuickAction(int actionId) {
		username.setText(usernames.get(actionId));
	}

}
