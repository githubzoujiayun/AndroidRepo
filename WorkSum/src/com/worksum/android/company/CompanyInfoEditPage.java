package com.worksum.android.company;

import android.os.Bundle;
import android.view.ViewGroup;

import com.worksum.android.R;
import com.worksum.android.TitlebarFragment;
import com.worksum.android.annotations.LayoutID;
import com.worksum.android.annotations.Titlebar;

/**
 *
 */

@LayoutID(R.layout.company_info_edit_page)
@Titlebar(titleId = R.string.company_info_account_title, leftDrawableId = R.drawable.common_nav_arrow, rightTextId = R.string.action_save)
public class CompanyInfoEditPage extends TitlebarFragment {

    @Override
    protected void setupView(ViewGroup vg, Bundle savedInstanceState) {
        super.setupView(vg, savedInstanceState);
    }
}
