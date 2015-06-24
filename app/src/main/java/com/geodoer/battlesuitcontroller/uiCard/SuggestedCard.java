/*
 * ******************************************************************************
 *   Copyright (c) 2013-2014 Gabriele Mariotti.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *  *****************************************************************************
 */

package com.geodoer.battlesuitcontroller.uiCard;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.geodoer.battlesuitcontroller.R;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardThumbnail;

/**
 * @author Gabriele Mariotti (gabri.mariotti@gmail.com)
 */
public class SuggestedCard extends Card
{

    private String
            sTittle,
            sDescription,
            sSubTitle,
            sPurpose,
            sHeaderText;

    private CardThumbnail
            thumb;

    private static SuggestedCard.whenClicked
            whenClicked;
    /*

     */

    public SuggestedCard(Context context) {
        this(context, R.layout.carddemo_suggested_inner_content);
    }

    public SuggestedCard(Context context, int innerLayout) {
        super(context, innerLayout);
        init();
    }

    private void init() {

        sTittle = "aTitle";
        sDescription = "aDescription";
        sSubTitle = "aSubTitle";
        sPurpose = "aPurpose words";
        sHeaderText = " sHeaderText";

        // set long-click listener
        setOnLongClickListener(new OnLongCardClickListener() {
            @Override
            public boolean onLongClick(Card card, View view) {

                whenClicked.onLongClicked();

                return false;
            }
        });

        //Set click listener
        setOnClickListener(new OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                //
                Toast.makeText(getContext(), "Click listener", Toast.LENGTH_LONG).show();
                whenClicked.onClicked();
            }
        });

        //Set swipe on
        setSwipeable(false);

        setShadow(true);

        setCardElevation(getContext().getResources().getDimension(R.dimen.carddemo_shadow_elevation));

        //Add thumbnail
        CardThumbnail thumb = new SuggestedCardThumb(getContext());
        thumb.setUrlResource("https://lh5.googleusercontent.com/-N8bz9q4Kz0I/AAAAAAAAAAI/AAAAAAAAAAs/Icl2bQMyK7c/s265-c-k-no/photo.jpg");
        thumb.setErrorResource(R.drawable.ic_error_loadingorangesmall);
        addCardThumbnail(thumb);


        //Add a header
        SuggestedCardHeader header =
                new SuggestedCardHeader(getContext(),sHeaderText);
        header.setHeaderBackground(R.color.c_brick_red);
        header.setsHeaderText(sHeaderText);

        addCardHeader(header);

    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {

        if (view != null) {


            TextView title = (TextView) view.findViewById(R.id.carddemo_suggested_title);
            TextView member = (TextView) view.findViewById(R.id.carddemo_suggested_memeber);
            TextView subtitle = (TextView) view.findViewById(R.id.carddemo_suggested_subtitle);
            TextView community = (TextView) view.findViewById(R.id.carddemo_suggested_community);

            if (title != null)
                //title.setText(R.string.demo_suggested_title);
                title.setText(sTittle);

            if (member != null)
                // member.setText(R.string.demo_suggested_member);
                member.setText(sDescription);

            if (subtitle != null)
                // subtitle.setText(R.string.demo_suggested_subtitle);
                subtitle.setText(sSubTitle);

            if (community != null)
                // community.setText(R.string.demo_suggested_community);
                community.setText(sPurpose);
        }
    }

    /*

     */

    public void setCardThumbDrawableResource(int res){
        if(thumb!=null)
            this.thumb.setDrawableResource(res);
    }

    public String getsTittle() {
        return sTittle;
    }

    public void setsTittle(String sTittle) {
        this.sTittle = sTittle;
    }

    public String getsDescription() {
        return sDescription;
    }

    public void setsDescription(String sDescription) {
        this.sDescription = sDescription;
    }

    public String getsSubTitle() {
        return sSubTitle;
    }

    public void setsSubTitle(String sSubTitle) {
        this.sSubTitle = sSubTitle;
    }

    public String getsPurpose() {
        return sPurpose;
    }

    public void setsPurpose(String sPurpose) {
        this.sPurpose = sPurpose;
    }

    public void setsHeaderText(String sHeaderText){
        this.sHeaderText = sHeaderText;
    }

    public static void setWhenClcikedTarget(whenClicked whenClcikedTarget){
        whenClicked = whenClcikedTarget;
    }

    public interface whenClicked{
        void onClicked();

        void onLongClicked();
    }
}

