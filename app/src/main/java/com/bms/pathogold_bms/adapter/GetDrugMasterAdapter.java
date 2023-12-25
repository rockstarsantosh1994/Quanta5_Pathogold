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
import com.bms.pathogold_bms.model.getdrug.GetDrugBO;

import java.util.ArrayList;

public class GetDrugMasterAdapter extends ArrayAdapter<GetDrugBO> {

    Context context;
    private final ArrayList<GetDrugBO> ownerDataArrayList;
    private final ArrayList<GetDrugBO> suggestions;
    private final ArrayList<GetDrugBO> tempItems;
    private LayoutInflater inflater = null;

    public GetDrugMasterAdapter(@NonNull Context context, @NonNull ArrayList<GetDrugBO> objects) {
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
    public GetDrugBO getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getPosition(@Nullable GetDrugBO item) {
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

        GetDrugBO getDrugBO = getItem(position);

        //patientPosition=suggestions.size();

       /* try{
                ((AllInOneActivity) context).showAnimalInEditext(suggestions.size(),tempItems.size());
        }catch (NullPointerException e){
            e.printStackTrace();
        }
*/
        if (getDrugBO != null) {
            textViewName.setText(getDrugBO.getDrugId()+" "+getDrugBO.getDrugName());
        }
        return convertView;
    }

    private final Filter fruitFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            GetDrugBO getDrugBO = (GetDrugBO) resultValue;
            return getDrugBO.getDrugName();
        }

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            if (charSequence != null) {
                suggestions.clear();
                for (GetDrugBO getDrugBO: tempItems) {
                    if (getDrugBO.getDrugName().toLowerCase().contains(charSequence.toString().toLowerCase())) {
                        suggestions.add(getDrugBO);
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
                ArrayList<GetDrugBO> tempValues = (ArrayList<GetDrugBO>) filterResults.values;
                if (filterResults != null && filterResults.count > 0) {
                    clear();
                    for (GetDrugBO fruitObj : tempValues) {
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

