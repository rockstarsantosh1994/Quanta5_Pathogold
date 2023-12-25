package com.bms.pathogold_bms.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bms.pathogold_bms.R;
import com.bms.pathogold_bms.fragment.VitalsFragment;
import com.bms.pathogold_bms.model.emrcheckboxlist.GetEMRCheckBoxListBO;

import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class GetEMRCheckBoxAdapter extends RecyclerView.Adapter<GetEMRCheckBoxAdapter.GetEMRViewHolder>{

    private final Context context;
    private final Object[] key;
    private final Map<String, ArrayList<GetEMRCheckBoxListBO>> getEMRCheckboxMap;
    public VitalsFragment vitalsFragment;

    public GetEMRCheckBoxAdapter(Context context, Object[] key, Map<String, ArrayList<GetEMRCheckBoxListBO>> getEMRCheckboxMap, VitalsFragment vitalsFragment) {
        this.context = context;
        this.key = key;
        this.getEMRCheckboxMap = getEMRCheckboxMap;
        this.vitalsFragment = vitalsFragment;
    }

    @NonNull
    @NotNull
    @Override
    public GetEMRViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.row_getemercheckbox,parent,false);
        return new GetEMRViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull @NotNull GetEMRViewHolder holder, int position) {
        holder.tvKey.setText(""+key[position]);

        if(getEMRCheckboxMap.get(key[position])!=null){
           GetEMRCheckBoxDetailsAdapter getEMRCheckBoxDetailsAdapter=new GetEMRCheckBoxDetailsAdapter(context, Objects.requireNonNull(getEMRCheckboxMap.get(key[position])), vitalsFragment);
           holder.rvKey.setAdapter(getEMRCheckBoxDetailsAdapter);
        }
    }

    @Override
    public int getItemCount() {
        return key.length;
    }

    public class GetEMRViewHolder extends RecyclerView.ViewHolder{

        private final TextView tvKey;
        private final RecyclerView rvKey;

        public GetEMRViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            tvKey=itemView.findViewById(R.id.tv_key);
            rvKey=itemView.findViewById(R.id.rv_key_data);
            GridLayoutManager mLayoutManager = new GridLayoutManager(context, 2) {
                public boolean canScrollVertically() {
                    return false;
                }
            };
            rvKey.setLayoutManager(mLayoutManager);
        }
    }
}
