package com.ibm.emergencyapp;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Nigel on 06/25/2018.
 *
 * Modified by Chao on 01/03/2019.
 */

public class ThreatHazards extends CustomActionBarActivity {


    private static final String TITLE_LABEL = "title";
    private static final String DESCRIPTION_LABEL = "description";
    private static final String ICON_LABEL = "icon";

    private static final String EVACUATE_POPUP_TAG = "evacuate_tag";
    private static final String SHELTER_POPUP_TAG = "shelter_tag";
    private static final String LOCKDOWN_POPUP_TAG = "lockdown_tag";

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_threat_hazards;
    }

    public void evacuate(View view) {
        showEvacuatePopup();
    }

    public void shelter(View view) {
        ShowTakeShelterPopup();
    }

    public void lockdown(View view) {
        showLockDownPopup();
    }

    public void mentalHealth(View view) {
        Intent intent = new Intent(ThreatHazards.this, MentalHealth.class);
        startActivity(intent);
    }

    public void medical(View view) {
        Intent intent = new Intent(ThreatHazards.this, Medical.class);
        startActivity(intent);
    }

    public void bombThreats(View view) {
        Intent intent = new Intent(ThreatHazards.this, BombThreats.class);
        startActivity(intent);
    }

    public void fire(View view) {
        Intent intent = new Intent(ThreatHazards.this, Fire.class);
        startActivity(intent);
    }

    public void powerLoss(View view) {
        Intent intent = new Intent(ThreatHazards.this, PowerLoss.class);
        startActivity(intent);
    }

    public void collision(View view) {
        Intent intent = new Intent(ThreatHazards.this, Collision.class);
        startActivity(intent);
    }

    public void usualOdours(View view) {
        Intent intent = new Intent(ThreatHazards.this, Odours.class);
        startActivity(intent);
    }

    public void workplaceViolence(View view) {
        Intent intent = new Intent(ThreatHazards.this, WorkplaceViolence.class);
        startActivity(intent);
    }

    public void civilUnrest(View view) {
        Intent intent = new Intent(ThreatHazards.this, CivilUnrest.class);
        startActivity(intent);
    }

    private void showEvacuatePopup() {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag(EVACUATE_POPUP_TAG);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        int title = R.string.evacuate;
        int description = R.string.evacuate_description;
        int icon = R.drawable.evacuateldpi;

        final ThreatPopup evacuatePopup = ThreatPopup.newInstance(title, description, icon);
        evacuatePopup.show(ft, EVACUATE_POPUP_TAG);
    }

    private void ShowTakeShelterPopup() {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag(SHELTER_POPUP_TAG);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        int title = R.string.shelter;
        int description = R.string.shelter_description;
        int icon = R.drawable.takeshelterldpi;

        final ThreatPopup shelterPopup = ThreatPopup.newInstance(title, description, icon);
        shelterPopup.show(ft, SHELTER_POPUP_TAG);
    }

    private void showLockDownPopup() {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag(LOCKDOWN_POPUP_TAG);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        int title = R.string.lockdown;
        int description = R.string.lockdown_description;
        int icon = R.drawable.lockdownldpi;

        final ThreatPopup lockdownPopup = ThreatPopup.newInstance(title, description, icon);
        lockdownPopup.show(ft, LOCKDOWN_POPUP_TAG);
    }

    /**
     * created by chao 12/12/2018
     *
     * inner fragment class extends DialogFragment. When create new instance, pass layout
     * resource ID.
     *
     */
    public static class ThreatPopup extends DialogFragment {

        public ThreatPopup() {
            //empty public constructor
        }

        /*static newInstance constructor
         *
         * @param title
         * @param description
         * @param icon
         * @return ThreatPopup instance
         */
        public static ThreatPopup newInstance(int title, int description, int icon) {
            ThreatPopup frag = new ThreatPopup();
            Bundle args = new Bundle();
            args.putInt(TITLE_LABEL, title);
            args.putInt(DESCRIPTION_LABEL, description);
            args.putInt(ICON_LABEL, icon);
            frag.setArguments(args);
            return frag;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){

            final Dialog dialog = new Dialog(getContext());
            dialog.setContentView(R.layout.fragment_hazard_popup);

            TextView titleTv = dialog.findViewById(R.id.tv_hazard_popup_title);
            TextView descriptionTv = dialog.findViewById(R.id.tv_hazard_popup_description);
            ImageView iconIv = dialog.findViewById(R.id.iv_hazard_popup_icon);
            ImageButton closeBtn = dialog.findViewById(R.id.ib_hazard_popup_close);

            titleTv.setText(getArguments().getInt(TITLE_LABEL));
            descriptionTv.setText(getArguments().getInt(DESCRIPTION_LABEL));
            iconIv.setImageResource(getArguments().getInt(ICON_LABEL));
            closeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            //set background to transparent so that corner radius can be shown correctly
            //for some reason it did not work (worked in other projects)
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            return dialog;

            //using AlertDialog.Builder is an alternative.
            /*
            return new AlertDialog.Builder(getActivity())
                    .setView(getActivity().getLayoutInflater()
                            .inflate(getArguments().getInt(LAYOUT_LABEL), null))
                    .create();
            */
        }

    }
}
