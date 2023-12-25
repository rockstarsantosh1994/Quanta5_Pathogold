package com.bms.pathogold_bms.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bms.pathogold_bms.R;
import com.bms.pathogold_bms.model.patienttestlist.GetPatientTestListBO;

import java.util.ArrayList;

import gr.escsoft.michaelprimez.searchablespinner.interfaces.ISpinnerSelectedView;
import gr.escsoft.michaelprimez.searchablespinner.tools.UITools;

public class GetPatientTestListAdapter extends ArrayAdapter<GetPatientTestListBO> implements Filterable, ISpinnerSelectedView {

    private final Context mContext;
    private final ArrayList<GetPatientTestListBO> mBackupStrings;
    private ArrayList<GetPatientTestListBO> mStrings;
    private final StringFilter mStringFilter = new StringFilter();
    int tempPosition=0;

    public GetPatientTestListAdapter(Context context, ArrayList<GetPatientTestListBO> strings, int tempPosition) {
        super(context, R.layout.view_list_item);
        mContext = context;
        mStrings = strings;
        this.tempPosition=tempPosition;
        mBackupStrings = strings;
    }

    @Override
    public int getCount() {
        return mStrings.size();
    }

    @Override
    public GetPatientTestListBO getItem(int position) {
        return mStrings.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint({"ViewHolder", "SetTextI18n"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        /*if (position == 0) {
            view = getNoSelectionView();
        } else {

        }*/
        view = View.inflate(mContext, R.layout.view_list_item, null);
        ImageView letters = view.findViewById(R.id.ImgVw_Letters);
        TextView dispalyName = view.findViewById(R.id.TxtVw_DisplayName);
        letters.setImageDrawable(getTextDrawable(mStrings.get(position).getTitle()));
        dispalyName.setText(mStrings.get(position).getTLCode()+". "+mStrings.get(position).getTitle());
        return view;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getSelectedView(int position) {
        View view = null;
        /*if (position == 0) {
           // view = getNoSelectionView();
        } else {

        }*/
        view = View.inflate(mContext, R.layout.view_list_item, null);
        ImageView letters = view.findViewById(R.id.ImgVw_Letters);
        TextView dispalyName = view.findViewById(R.id.TxtVw_DisplayName);
        GetPatientTestListBO item = this.mStrings.get(position);
        letters.setImageDrawable(getTextDrawable(item.getTitle()));
        dispalyName.setText(mStrings.get(position).getTLCode()+". "+mStrings.get(position).getTitle());
        tempPosition=position;
        return view;
    }

    @Override
    public View getNoSelectionView() {
        View view = View.inflate(mContext, R.layout.view_list_no_selection_item, null);
        return view;
    }

    private TextDrawable getTextDrawable(String displayName) {
        TextDrawable drawable = null;
        if (!TextUtils.isEmpty(displayName)) {
            int color2 = ColorGenerator.MATERIAL.getColor(displayName);
            drawable = TextDrawable.builder()
                    .beginConfig()
                    .width(UITools.dpToPx(mContext, 32))
                    .height(UITools.dpToPx(mContext, 32))
                    .textColor(Color.WHITE)
                    .toUpperCase()
                    .endConfig()
                    .round()
                    .build(displayName.substring(0, 1), color2);
        } else {
            drawable = TextDrawable.builder()
                    .beginConfig()
                    .width(UITools.dpToPx(mContext, 32))
                    .height(UITools.dpToPx(mContext, 32))
                    .endConfig()
                    .round()
                    .build("?", Color.GRAY);
        }
        return drawable;
    }

    @Override
    public Filter getFilter() {
        return mStringFilter;
    }

    public class StringFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String charString = constraint.toString();

            final FilterResults filterResults = new FilterResults();
            if (TextUtils.isEmpty(constraint)) {
                filterResults.count = mBackupStrings.size();
                filterResults.values = mBackupStrings;
                return filterResults;
            }
            final ArrayList<GetPatientTestListBO> filterStrings = new ArrayList<>();
            for (GetPatientTestListBO temp : mBackupStrings) {
                if (temp.getTitle().toLowerCase().contains(constraint)) {
                    filterStrings.add(temp);
                }
            }
            filterResults.count = filterStrings.size();
            filterResults.values = filterStrings;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mStrings = (ArrayList) results.values;
            notifyDataSetChanged();
        }
    }
}