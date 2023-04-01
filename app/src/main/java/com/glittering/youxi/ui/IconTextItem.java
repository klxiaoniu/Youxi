package com.glittering.youxi.ui;

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
        typedArray.recycle();
        iv.setImageResource(id);
        tv.setText(title);
        if (!round) cv.setRadius(20);   //圆角值
    }

    private void init(Context context) {
        View view = View.inflate(context, R.layout.item_icon_text, this);
        iv = view.findViewById(R.id.icontext_iv);
        tv = view.findViewById(R.id.icontext_tv);
        cv = view.findViewById(R.id.cardView);
    }

}
