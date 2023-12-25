package com.bms.pathogold_bms.adapter;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.bms.pathogold_bms.fragment.ChatFragment;
import com.bms.pathogold_bms.fragment.ContactsFragment;
import com.bms.pathogold_bms.model.getconsultation.ConsultationBO;

import java.util.ArrayList;

public class PagerAdapter extends FragmentStatePagerAdapter {

    private static final String TAG = "PagerAdapter";
    private final Context context;
    private final int tabcount;
    private final ArrayList<ConsultationBO> consultationBOArrayList;
    private final ArrayList<ConsultationBO> phelboBOArrayList;

    public PagerAdapter(@NonNull FragmentManager fm, Context context, int behavior, ArrayList<ConsultationBO>consultationBOArrayList , ArrayList<ConsultationBO>phelboBOArrayList) {
        super(fm, behavior);
        tabcount=behavior;
        this.context=context;
        this.consultationBOArrayList=consultationBOArrayList;
        this.phelboBOArrayList=phelboBOArrayList;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                return new ChatFragment();

            case 1:
                Log.e(TAG, "getItem: "+consultationBOArrayList.size() );
                Log.e(TAG, "getItem: "+phelboBOArrayList.size() );
                Fragment contactFragment = new ContactsFragment();
                Bundle args = new Bundle();
                args.putParcelableArrayList("consultation_list",consultationBOArrayList);
                args.putParcelableArrayList("phlebo_list",phelboBOArrayList);
                contactFragment.setArguments(args);
                return contactFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabcount;
    }
}
