package com.worksum.android;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.jobs.lib_v1.data.DataItemResult;
import com.jobs.lib_v1.task.BasicTask;
import com.worksum.android.utils.AssetsUtil;

/**
 * @author chao.qin
 *         <p>
 *         16/10/18
 */
public class TermsFragment extends TitlebarFragment {

    private WebView mWebView;

    public static void showTerms(Context context) {
        Bundle extras = new Bundle();
        Intent intent = new Intent(context,FragmentContainer.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(FragmentContainer.KEY_FRAGMENT, TermsFragment.class);
        intent.putExtras(extras);
        context.startActivity(intent);
    }

    @Override
    protected void setupView(ViewGroup v, Bundle savedInstanceState) {
        super.setupView(v, savedInstanceState);
        setTitle(R.string.terms_title);
        setActionLeftDrawable(R.drawable.common_nav_arrow);
        mWebView = (WebView) findViewById(R.id.web_view);
        mWebView.getSettings().setDefaultTextEncodingName("UTF-8");
        new AsyncTask<String,String,String>() {

            @Override
            protected String doInBackground(String... params) {
                return AssetsUtil.readFile(getActivity(),"terms.html");
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                mWebView.loadData(s,"text/html; charset=UTF-8",null);
            }
        }.execute();

    }

    @Override
    public int getLayoutId() {
        return R.layout.terms;
    }
}
