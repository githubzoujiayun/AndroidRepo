package com.worksum.android;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.worksum.android.controller.UserCoreInfo;

public class InboxFragment extends TitlebarFragment{

	private Button mLoginButton;
	private Button mViewJobsButton;

	@Override
	public int getLayoutId() {
		return R.layout.inbox;
	}

	@Override
	protected void setupView(ViewGroup v, Bundle savedInstanceState) {
		super.setupView(v, savedInstanceState);
		
		setTitle(R.string.title_message);

		View view = findViewById(R.id.inbox_hr_message);
		ImageView icon = (ImageView) view.findViewById(R.id.inbox_item_img);
		TextView title = (TextView) view.findViewById(R.id.inbox_item_title);
		TextView description = (TextView) view.findViewById(R.id.inbox_item_description);

		mLoginButton = (Button) findViewById(R.id.inbox_btn_login);
		mViewJobsButton = (Button) findViewById(R.id.inbox_view_jobs);

		mLoginButton.setOnClickListener(this);
		mViewJobsButton.setOnClickListener(this);

		icon.setImageResource(R.drawable.ic_good_news);
		title.setText(R.string.inbox_title_hr_message);
		description.setText(R.string.inbox_descrption_hr_message);
		view.setOnClickListener(this);

		view = findViewById(R.id.inbox_recommand);
		icon = (ImageView) view.findViewById(R.id.inbox_item_img);
		title = (TextView) view.findViewById(R.id.inbox_item_title);
		description = (TextView) view.findViewById(R.id.inbox_item_description);

		icon.setImageResource(R.drawable.ic_good_jobs);
		title.setText(R.string.inbox_title_recommand);
		description.setText(R.string.inbox_descrptio_recommend);
		view.setOnClickListener(this);

        switchLayout();
	}

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.inbox_hr_message:
                FragmentContainer.showHRMessage(getActivity());
                break;
            case R.id.inbox_recommand:
                FragmentContainer.showRecommand(getActivity());
                break;
			case R.id.inbox_btn_login:
				FragmentContainer.FullScreenContainer.showLoginFragment(getActivity());
				break;
			case R.id.inbox_view_jobs:
				Main main = (Main) getActivity();
				main.setTab(0);
				break;
        }
    }

    private void setupItem(int inbox_hr_message) {
	}

    @Override
    public void onUserStatusChanged(int loginType) {
        super.onUserStatusChanged(loginType);
        switchLayout();
    }

    private void switchLayout() {
        View view = findViewById(R.id.inbox_layout_unlogin);
        view.setVisibility(View.GONE);
        if (!UserCoreInfo.hasLogined()) {
            view.setVisibility(View.VISIBLE);
        }
    }
}
