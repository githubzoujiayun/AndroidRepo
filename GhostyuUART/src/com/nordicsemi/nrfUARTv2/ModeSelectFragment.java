package com.nordicsemi.nrfUARTv2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author chao.qin
 * @since 2016/12/28
 */
@LayoutID(R.layout.mode_select_fragment)
public class ModeSelectFragment extends GeneralFragment implements View.OnClickListener {


    public static void show(Activity activity) {
        Intent intent = new Intent(activity,FragmentContainer.class);
        intent.putExtra(FragmentContainer.KEY_FRAGMENT,ModeSelectFragment.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
    }

    @Override
    protected void setupView(ViewGroup v, Bundle savedInstanceState) {
        super.setupView(v, savedInstanceState);

        findViewById(R.id.simple_mode_button).setOnClickListener(this);
        findViewById(R.id.professional_mode_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.simple_mode_button:
                SettingsActivity.startSettings(getActivity(),true);
                UIMode.setUIMode(UIMode.UI_MODE_SIMPLE);
                break;
            case R.id.professional_mode_button:
                MainActivity.startConnectActivity(getActivity());
                UIMode.setUIMode(UIMode.UI_MODE_PROFESSIONAL);
                break;
        }
        finish();
    }
}
