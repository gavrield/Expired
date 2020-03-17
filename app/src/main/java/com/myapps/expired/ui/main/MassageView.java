package com.myapps.expired.ui.main;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.myapps.expired.R;

public class MassageView extends View {

    private TextView massage;
    private Button eraseButton;
    private View v = this;
    private final ViewGroup parent;

    public MassageView(Context context) {
        super(context);
        inflate(context, R.layout.massage_view, null);
        massage = findViewById(R.id.main_massage);
        eraseButton = findViewById(R.id.erase_button);
        parent = (ViewGroup) getParent();
        eraseButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                parent.removeView(v);
            }
        });
    }

    public TextView getMassage() {
        return massage;
    }

    public void setMassage(String massage) {
        this.massage.setText(massage);
    }
}
