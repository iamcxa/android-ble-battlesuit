package com.geodoer.battlesuitcontroller.uiCard;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

import it.gmariotti.cardslib.library.internal.CardThumbnail;

/**
 * Created by kuyen on 2015/6/24.
 */
class SuggestedCardThumb extends CardThumbnail {

    public SuggestedCardThumb(Context context) {
        super(context);
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View viewImage) {
        if (viewImage != null) {

            if (parent!=null && parent.getResources()!=null){
                DisplayMetrics metrics=parent.getResources().getDisplayMetrics();

                int base = 100;

                if (metrics!=null){
                    viewImage.getLayoutParams().width = (int)(base*metrics.density);
                    viewImage.getLayoutParams().height = (int)(base*metrics.density);
                }else{
                    viewImage.getLayoutParams().width = 200;
                    viewImage.getLayoutParams().height = 200;
                }
            }
        }
    }
}
