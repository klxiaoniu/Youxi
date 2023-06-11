package com.glittering.youxi.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.glittering.youxi.R;
import com.glittering.youxi.utils.DarkUtil;

public class IconTextItem extends ConstraintLayout {
    private ImageView iv;
    private TextView tv;
    private CardView cv;

    public IconTextItem(@NonNull Context context) {
        super(context);
        init(context);
    }

    public IconTextItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.IconTextItem);
        String title = typedArray.getString(R.styleable.IconTextItem_title);
        int id = typedArray.getResourceId(R.styleable.IconTextItem_img, R.drawable.ic_default_avatar);
        boolean round = typedArray.getBoolean(R.styleable.IconTextItem_cornerRound, true);
        int wh = typedArray.getDimensionPixelSize(R.styleable.IconTextItem_widthHeight, -1);
        typedArray.recycle();
        //if (wh == 40) wh = (int) (getResources().getDisplayMetrics().density * 50);
        if (wh != -1) {
            iv.getLayoutParams().width = wh;
            iv.getLayoutParams().height = wh;
            cv.getLayoutParams().width = wh;
            cv.getLayoutParams().height = wh;
            cv.setRadius((float) (wh / 2.0));   //圆角值
        }
        iv.setImageResource(id);
        tv.setText(title);
        if (!round) cv.setRadius(20);   //圆角值
        DarkUtil.Companion.addMaskIfDark(cv);
    }

    private void init(Context context) {
        View view = View.inflate(context, R.layout.item_icon_text, this);
        iv = view.findViewById(R.id.icontext_iv);
        tv = view.findViewById(R.id.icontext_tv);
        cv = view.findViewById(R.id.cardView_wx);
    }

}
