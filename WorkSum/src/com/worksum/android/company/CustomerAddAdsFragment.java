package com.worksum.android.company;

import com.worksum.android.R;
import com.worksum.android.TitlebarFragment;
import com.worksum.android.annotations.DataManagerReg;
import com.worksum.android.annotations.LayoutID;
import com.worksum.android.annotations.Titlebar;

/**
 */

@LayoutID(R.layout.customer_ads)
@DataManagerReg(actions = {})
@Titlebar(titleId = R.string.customer_ads, leftDrawableId = R.drawable.common_nav_arrow,rightTextId = R.string.action_save)
public class CustomerAddAdsFragment extends TitlebarFragment {

}
