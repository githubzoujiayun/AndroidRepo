package com.worksum.android;

import android.app.DatePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.DatePicker;
import android.widget.NumberPicker;

/**
 * @author chao.qin
 *
 *
 * 去掉DataPickerDialog年月日中的日，只显示年月
 */

public class YearsPickerDialog extends DatePickerDialog {

    public YearsPickerDialog(Context context, OnDateSetListener callBack, int year, int monthOfYear) {
        this(context, callBack, year, monthOfYear, 0);
    }

    private YearsPickerDialog(Context context, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
        super(context, callBack, year, monthOfYear, dayOfMonth);
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int month, int day) {
        super.onDateChanged(view, year, month, day);
    }

    @Override
    public void show() {
        super.show();
        int daySpinnerId = getContext().getResources().getIdentifier("day","id","android");
        NumberPicker dayPicker = (NumberPicker) findViewById(daySpinnerId);
        if (dayPicker != null) {
            dayPicker.setVisibility(View.GONE);
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        title = getContext().getString(R.string.date_picker_title);
        super.setTitle(title);
    }
}
