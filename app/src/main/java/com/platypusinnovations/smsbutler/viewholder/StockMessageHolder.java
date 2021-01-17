package com.platypusinnovations.smsbutler.viewholder;

import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.platypusinnovations.smsbutler.R;
import com.platypusinnovations.smsbutler.events.BusProvider;
import com.platypusinnovations.smsbutler.events.EditMessageRequest;
import com.platypusinnovations.smsbutler.events.MessageChosen;
import com.platypusinnovations.smsbutler.realm.StockMessage;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;

public class StockMessageHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    
    private static final String TAG = "StockMessageHolder";
    private StockMessage mMessage;
    private TextView mTextView;
    private FrameLayout mSettingsLayout;
    private ImageView mSettingsIcon;

    public StockMessageHolder(View itemView) {
        super(itemView);
        
        mTextView = (TextView)itemView.findViewById(R.id.message_text);
        mSettingsLayout = (FrameLayout)itemView.findViewById(R.id.settings_container);
        mSettingsIcon = (ImageView)itemView.findViewById(R.id.settings_icon);

        mTextView.setOnClickListener(this);
        mSettingsLayout.setOnClickListener(this);
    }

    public void bindStockMessage(StockMessage message) {
        mMessage = message;
        mTextView.setText(mMessage.getMessage());

        IconDrawable settings = new IconDrawable(mTextView.getContext(), Iconify.IconValue.fa_pencil)
                .colorRes(R.color.settings_label).sizeDp(20);
        mSettingsIcon.setImageDrawable(settings);
    }

    @Override
    public void onClick(View view) {
        Log.i(TAG, String.valueOf(view.getId()));
        if(view.getId() == R.id.message_text) {
            BusProvider.instance().post(new MessageChosen(mMessage.getMessage()));
        } else if(view.getId() == R.id.settings_container) {
            BusProvider.instance().post(new EditMessageRequest(getPosition(), mMessage.getMessage()));
        }
    }
}
