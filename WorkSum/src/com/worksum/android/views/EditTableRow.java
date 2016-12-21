package com.worksum.android.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.widget.TableRow;
import android.widget.TextView;

import com.worksum.android.R;
import com.worksum.android.annotations.LayoutID;

/**
 * @author chao.qin
 *         <p/>
 *         16/10/28
 */
@LayoutID(R.layout.edit_tab_raw)
public class EditTableRow extends TableRow {

    protected TextView mTitle;
    protected TextView mContent;
    private boolean mNecessary = false;

    private String mMessage = "";

    public EditTableRow(Context context) {
        this(context, null);
    }

    public EditTableRow(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public class NecessaryException extends Exception {

        private String titleText;

        public NecessaryException(String _titleText) {
            titleText = _titleText;
        }

        @Override
        public String getMessage() {
            return getResources().getString(R.string.resume_edit_necessary_row_not_empty,titleText);
        }
    }

    protected void initView(Context context,AttributeSet attrs) {

        int defaultTextSize = context.getResources().getDimensionPixelSize(R.dimen.default_text_size);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.jobpedia);
        String title = a.getString(R.styleable.jobpedia_rowTitle);
        int titleSize = a.getDimensionPixelSize(R.styleable.jobpedia_rowTitleSize, defaultTextSize);
        ColorStateList titleColor = a.getColorStateList(R.styleable.jobpedia_rowTitleColor);

        String text = a.getString(R.styleable.jobpedia_rowText);
        int textSize = a.getDimensionPixelSize(R.styleable.jobpedia_rowTextSize, defaultTextSize);
        ColorStateList textColor = a.getColorStateList(R.styleable.jobpedia_rowTextColor);
        String hint = a.getString(R.styleable.jobpedia_rowHint);

        mMessage = a.getString(R.styleable.jobpedia_rowMessage);

        mNecessary = a.getBoolean(R.styleable.jobpedia_rowNecessary, false);

        int inputType = a.getInt(R.styleable.jobpedia_inputType, InputType.TYPE_CLASS_TEXT);

        a.recycle();

        inflate(context, getLayoutId(), this);

        mTitle = (TextView) findViewById(R.id.edit_table_title);
        mContent = (TextView) findViewById(R.id.edit_table_text);


        mContent.setHint(hint);
        mContent.setText(text);
        mContent.setInputType(inputType);
        mContent.getPaint().setTextSize(textSize);

        if(textColor != null){
            mContent.setTextColor(textColor);
        }

        if (title == null) {
            title = "";
        }

        SpannableStringBuilder builder = new SpannableStringBuilder(title);
        if (mNecessary) {
            builder.append("*");
            ImageSpan span = new ImageSpan(context,R.drawable.ic_necessary,ImageSpan.ALIGN_BASELINE);

            builder.setSpan(span, builder.length() - 1, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        builder.append(":");

        mTitle.setText(builder);
        if (titleColor != null) {
            mTitle.setTextColor(titleColor);
        }
        mTitle.getPaint().setTextSize(titleSize);


    }

    protected int getLayoutId() {
        return getLayoutIdFromAnnotation();
    }

    public boolean necessary() {
        return mNecessary;
    }

    /**
     * 推荐使用注解指定Fragment的LayoutID
     */
    private int getLayoutIdFromAnnotation() {
        int layoutId = 0;
        LayoutID annoId = getClass().getAnnotation(LayoutID.class);
        if (annoId != null) {
            layoutId = annoId.value();
        }
        return layoutId;
    }

    private void setHint(int hint) {
        mContent.setHint(getString(hint));
    }

    public void setText(int text) {
        mContent.setText(getString(text));
    }

    public void setText(String text) {
        mContent.setText(text);
    }

    public String getText() throws NecessaryException{
        String text = mContent.getText().toString();
        if (mNecessary && TextUtils.isEmpty(text)) {
            throw new NecessaryException(getTitle());
        }
        return text;
    }

    public void setTitle(int title) {
        setTitle(getString(title));
    }

    private void setTitle(String title) {
        mTitle.setText(title);
    }

    private String getString(int resId) {
        return getContext().getString(resId);
    }

    public void setNecessary(boolean necessary) {
        mNecessary = necessary;
    }

    public String getMessage() {
        return mMessage;
    }

    public String getTitle() {
        return mTitle.getText().toString();
    }
}
