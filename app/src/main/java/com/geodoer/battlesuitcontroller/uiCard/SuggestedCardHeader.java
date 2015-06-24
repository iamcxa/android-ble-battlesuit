package com.geodoer.battlesuitcontroller.uiCard;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.geodoer.battlesuitcontroller.R;

import it.gmariotti.cardslib.library.internal.CardHeader;

public class SuggestedCardHeader extends CardHeader {

    public int color;
    public String sHeaderText = "text";

    public SuggestedCardHeader(Context context,String sHeaderText) {
        this(context, R.layout.carddemo_suggested_header_inner, sHeaderText);
    }

    public SuggestedCardHeader(Context context, int innerLayout,String sHeaderText) {
        super(context, innerLayout);
        this.sHeaderText = sHeaderText;
    }

    public void setsHeaderText(String sHeaderText){
        this.sHeaderText = sHeaderText;
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {

        if (view != null) {

            view.setBackgroundColor(color);

            TextView textView = (TextView) view.findViewById(R.id.text_suggested_card1);

            if (textView != null) {
                //textView.setText(R.string.demo_suggested_header);
                textView.setText(sHeaderText);
            }
        }
    }

    public void setHeaderBackground(int color){
        this.color = color;
    }
}
