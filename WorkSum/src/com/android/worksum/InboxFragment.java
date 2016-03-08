package com.android.worksum;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class InboxFragment extends TitlebarFragment{



	@Override
	public int getLayoutId() {
		return R.layout.inbox;
	}

	@Override
	void setupView(ViewGroup v, Bundle savedInstanceState) {
		super.setupView(v, savedInstanceState);
		
		setTitle(R.string.title_message);

		View view = findViewById(R.id.inbox_hr_message);
		ImageView icon = (ImageView) view.findViewById(R.id.inbox_item_img);
		TextView title = (TextView) view.findViewById(R.id.inbox_item_title);
		TextView description = (TextView) view.findViewById(R.id.inbox_item_description);

		icon.setImageResource(R.drawable.inbox_hr_message_icon);
		title.setText(R.string.inbox_title_hr_message);
		description.setText(R.string.inbox_descrption_hr_message);
		view.setOnClickListener(this);

		view = findViewById(R.id.inbox_recommand);
		icon = (ImageView) view.findViewById(R.id.inbox_item_img);
		title = (TextView) view.findViewById(R.id.inbox_item_title);
		description = (TextView) view.findViewById(R.id.inbox_item_description);

		icon.setImageResource(R.drawable.inbox_recommand_icon);
		title.setText(R.string.inbox_title_recommand);
		description.setText(R.string.inbox_descrptio_recommand);
		view.setOnClickListener(this);

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
        }
    }

    private void setupItem(int inbox_hr_message) {
	}
}
