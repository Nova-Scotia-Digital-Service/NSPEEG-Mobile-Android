package com.ibm.emergencyapp;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NotificationView extends RelativeLayout {
    private TextView header;
    private TextView description;
    private ImageView thumbnail;

    public NotificationView(Context context) {
        super(context);
        init();
    }

    public NotificationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NotificationView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.notification_view, this);
        this.header = findViewById(R.id.notification_title);
        this.description = findViewById(R.id.notification_description);
        this.thumbnail = findViewById(R.id.notification_icon);
    }

}