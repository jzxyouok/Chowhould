package com.cwenhui.chowhound.widget;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cwenhui.chowhound.bean.LoginActionItem;
import com.example.chowhound.R;

public class LoginQuickAction extends CustomQuickAction<LoginActionItem> {
	private static final String TAG = "LoginQuickAction";

	public LoginQuickAction(Context context) {
		super(context);
	}

	@Override
	public void addQuickActionItem(LoginActionItem item) {
		mItems.add(item);

		View container = mInflater.inflate(R.layout.item_login_quick_action, null);

		TextView titleView = (TextView) container
				.findViewById(R.id.tv_quick_action_title);

		String title = item.getTitle();
		if (title != null) {
			titleView.setText(title);
		} else {
			titleView.setVisibility(View.GONE);
		}

		final int actionId = item.getActionId();

		container.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (mClickListener != null) {
					mClickListener.OnClickQuickAction(actionId);
					dismiss();
				}
			}
		});
		mQuickActionLayout.addView(container);
	}

	@Override
	public void initQuickAction(Context context) {
		mRootView = (ViewGroup) mInflater.inflate(R.layout.layout_login_quickaction, null);
		mQuickActionLayout = (LinearLayout) mRootView.findViewById(R.id.login_layout_quickaction);

		mRootView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

		setContentView(mRootView);
	}

}
