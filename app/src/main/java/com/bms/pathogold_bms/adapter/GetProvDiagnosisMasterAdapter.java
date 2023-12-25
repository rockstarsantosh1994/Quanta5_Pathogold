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
import com.bms.pathogold_bms.model.getprovisionaldiagnosis.GetProvDiagnosisBO;

import java.util.ArrayList;

public class GetProvDiagnosisMasterAdapter extends ArrayAdapter<GetProvDiagnosisBO> {

    Context context;
    private final ArrayList<GetProvDiagnosisBO> ownerDataArrayList;
    private final ArrayList<GetProvDiagnosisBO> suggestions;
    private final ArrayList<GetProvDiagnosisBO> tempItems;
    private LayoutInflater inflater = null;

    public GetProvDiagnosisMasterAdapter(@NonNull Context context, @NonNull ArrayList<GetProvDiagnosisBO> objects) {
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
    public GetProvDiagnosisBO getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getPosition(@Nullable GetProvDiagnosisBO item) {
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

        GetProvDiagnosisBO getDoseMasterBO = getItem(position);

        //patientPosition=suggestions.size();

       /* try{
                ((AllInOneActivity) context).showAnimalInEditext(suggestions.size(),tempItems.size());
        }catch (NullPointerException e){
            e.printStackTrace();
        }
*/
        if (getDoseMasterBO != null) {
            textViewName.setText(getDoseMasterBO.getDiagno_name());
        }
        return convertView;
    }

    private final Filter fruitFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            GetProvDiagnosisBO getDoseMasterBO = (GetProvDiagnosisBO) resultValue;
            return getDoseMasterBO.getDiagno_name();
        }

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            if (charSequence != null) {
                suggestions.clear();
                for (GetProvDiagnosisBO getDoseMasterBO: tempItems) {
                    if (getDoseMasterBO.getDiagno_name().toLowerCase().contains(charSequence.toString().toLowerCase())) {
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
                ArrayList<GetProvDiagnosisBO> tempValues = (ArrayList<GetProvDiagnosisBO>) filterResults.values;
                if (filterResults.count > 0) {
                    clear();
                    for (GetProvDiagnosisBO fruitObj : tempValues) {
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

