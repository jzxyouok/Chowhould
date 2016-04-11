package com.cwenhui.chowhound.ui;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.cwenhui.chowhound.adapter.OrderConfirmAdapter;
import com.cwenhui.chowhound.bean.GoodsBean;
import com.example.chowhound.R;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class OrderTakingActivity extends Activity implements OnRefreshListener2<ScrollView>{
	private static final String TAG = "OrderTakingActivity";
	private TextView orderAmount, realAmount;		//订单金额和实付金额
	private TextView tvTimer;
	private ListView lvOrderedGoods;				//显示已下订单商品的listview
	private PullToRefreshScrollView scrollVew;
	private List<GoodsBean> orderedGoods;			//已下订单的商品
	private OrderConfirmAdapter adapter;			
	private int sum = 0;							//总额
	private int minute = 10, second = 0;	
	private Handler mHandler;
	private Timer timer;
	private TimerTask timerTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_taking);
		
		initData();
		initView();
		initEvent();
	}
	
	@SuppressWarnings("unchecked")
	private void initData() {
		orderedGoods = (List<GoodsBean>) getIntent().getExtras().get("selectedGoods");
		adapter = new OrderConfirmAdapter(this, orderedGoods, R.layout.item_activity_order_confirm_selected_goods);
		for (GoodsBean bean : orderedGoods) {
			sum += bean.getSelectedNum()*bean.getPrice();
		}
		
		setTimerTextView();
		timerTask = new TimerTask() {  
            
            @Override  
            public void run() {  
                Message msg = new Message();  
                msg.what = 1;  
                mHandler.sendMessage(msg);  
            }  
        };  
        timer = new Timer();  
        timer.schedule(timerTask,0,1000);  
	}

	private void initView() {
		orderAmount = (TextView) findViewById(R.id.tv_activity_order_taking_order_amount);
		realAmount = (TextView) findViewById(R.id.tv_activity_order_taking_real_amount);
		tvTimer = (TextView) findViewById(R.id.tv_activity_order_taking_timer);
		lvOrderedGoods = (ListView) findViewById(R.id.lv_activity_order_taking_ordered_goods);
		scrollVew = (PullToRefreshScrollView) findViewById(R.id.pull_refresh_scrollview);

		scrollVew.setMode(Mode.PULL_FROM_START);
		orderAmount.setText(sum+"元");
		realAmount.setText(sum+"元");
		lvOrderedGoods.setAdapter(adapter);
		setListViewHeightBasedOnChildren(lvOrderedGoods);
	}

	private void initEvent() {
		scrollVew.setOnRefreshListener(this);
	}

	/**
	 * 根据item的高度来决定listview的高度,解决scrollview内嵌listview的问题
	 * @param listView
	 */
	public void setListViewHeightBasedOnChildren(ListView listView) {
	    if(listView == null) return;
	    ListAdapter listAdapter = listView.getAdapter();
	    if (listAdapter == null) {
	        return;
	    }
	    int totalHeight = 0;
	    for (int i = 0; i < listAdapter.getCount(); i++) {
	        View listItem = listAdapter.getView(i, null, listView);
	        listItem.measure(0, 0);
	        totalHeight += listItem.getMeasuredHeight();
	    }
	    ViewGroup.LayoutParams params = listView.getLayoutParams();
	    params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
	    listView.setLayoutParams(params);
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
		Toast.makeText(OrderTakingActivity.this, "pull down", 0).show();
		refreshView.onRefreshComplete();
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
	}
	

	private void setTimerTextView() {
		mHandler = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case 1:
					if(minute != 0 && second != 0){
						second--;  
	                    if (second >= 10) {  
	                        if (minute >= 10) {  
	                        	tvTimer.setText(minute + ":" + second);  
	                        }else {  
	                        	tvTimer.setText("0"+minute + ":" + second);  
	                        }  
	                    }else {  
	                        if (minute >= 10) {  
	                        	tvTimer.setText(minute + ":0" + second);  
	                        }else {  
	                        	tvTimer.setText("0"+minute + ":0" + second);  
	                        }  
	                    }  
					}else if(minute != 0 && second == 0){
						second =59;  
	                    minute--;  
	                    if (minute >= 10) {  
	                    	tvTimer.setText(minute + ":" + second);  
	                    }else {  
	                    	tvTimer.setText("0"+minute + ":" + second);  
	                    }  
					}else if(minute==0 && second != 0){
						second--;  
	                    if (second >= 10) {  
	                    	tvTimer.setText("0"+minute + ":" + second);  
	                    }else {  
	                    	tvTimer.setText("0"+minute + ":0" + second);  
	                    } 
					}else if(minute == 0 && second == 0){
						tvTimer.setText("00:00");
						if (timer != null) {  
	                        timer.cancel();  
	                        timer = null;  
	                    }  
	                    if (timerTask != null) {  
	                        timerTask = null;  
	                    }  
					}
					break;

				default:
					break;
				}
			}
			
		};
	}

}
