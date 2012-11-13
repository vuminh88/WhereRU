package com.ntu.whereRU.UI;

import com.ntu.whereRU.R;

import android.app.ActivityGroup;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class HomeActivity extends ActivityGroup {
	/** Called when the activity is first created. */
	private TabHost mTabHost;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		setupTabHost();
		mTabHost.getTabWidget().setDividerDrawable(R.drawable.tab_divider);
		setupTab("Friends", new Intent(this, NearbyFriendsListActivity.class));
		setupTab("Explore", new Intent(this, ExploreListAtivity.class));
		setupTab("I Me Mine", new Intent(this, ProfileActivity.class));
	}

	private void setupTabHost() {
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup(getLocalActivityManager());
	}

	private void setupTab(final String tag, final Intent intent) {
		View tabView = createTabView(mTabHost.getContext(), tag);
		TabSpec tabContent = mTabHost.newTabSpec(tag).setIndicator(tabView)
				.setContent(intent);
		mTabHost.addTab(tabContent);
	}

	private View createTabView(Context context, String tag) {
		View view = LayoutInflater.from(context).inflate(R.layout.tab_bg, null);
		TextView tv = (TextView) view.findViewById(R.id.tabsText);
		tv.setText(tag);
		return view;
	}

}