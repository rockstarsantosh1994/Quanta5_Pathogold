package com.bms.pathogold_bms.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bms.pathogold_bms.R;
import com.bms.pathogold_bms.model.getcomplain.GetComplainBO;

import java.util.ArrayList;

public class GetComplainMasterAdapter extends ArrayAdapter<GetComplainBO> {

    Context context;
    private final ArrayList<GetComplainBO> ownerDataArrayList;
    private final ArrayList<GetComplainBO> suggestions;
    private final ArrayList<GetComplainBO> tempItems;
    private LayoutInflater inflater = null;

    public GetComplainMasterAdapter(@NonNull Context context, @NonNull ArrayList<GetComplainBO> objects) {
        super(context, 0,objects);
        this.context=context;
        this.ownerDataArrayList=objects;
        tempItems = new ArrayList<>(ownerDataArrayList);
        suggestions=new ArrayList<>();
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return fruitFilter;
    }

    @Override
    public int getCount() {
        /*return (ownerDataArrayList != null && ownerDataArrayList.size() > 0)
                ? ownerDataArrayList.size() : 0;*/
        return ownerDataArrayList.size();
    }

    @Nullable
    @Override
    public GetComplainBO getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getPosition(@Nullable GetComplainBO item) {
        return super.getPosition(item);
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.row_get_drug_master, parent, false
            );
        }

        TextView textViewName = convertView.findViewById(R.id.autoText);

        GetComplainBO getDoseMasterBO = getItem(position);

        //patientPosition=suggestions.size();

       /* try{
                ((AllInOneActivity) context).showAnimalInEditext(suggestions.size(),tempItems.size());
        }catch (NullPointerException e){
            e.printStackTrace();
        }
*/
        if (getDoseMasterBO != null) {
            textViewName.setText(getDoseMasterBO.getComName());
        }
        return convertView;
    }

    private final Filter fruitFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            GetComplainBO getDoseMasterBO = (GetComplainBO) resultValue;
            return getDoseMasterBO.getComName();
        }

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            if (charSequence != null) {
                suggestions.clear();
                for (GetComplainBO getDoseMasterBO: tempItems) {
                    if (getDoseMasterBO.getComName().toLowerCase().contains(charSequence.toString().toLowerCase())) {
                        suggestions.add(getDoseMasterBO);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            try{
                ArrayList<GetComplainBO> tempValues = (ArrayList<GetComplainBO>) filterResults.values;
                if (filterResults.count > 0) {
                    clear();
                    for (GetComplainBO fruitObj : tempValues) {
                        add(fruitObj);
                    }
                    notifyDataSetChanged();
                } else {
                    clear();
                    notifyDataSetChanged();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };
}

