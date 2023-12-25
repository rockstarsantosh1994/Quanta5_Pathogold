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
import com.bms.pathogold_bms.model.getsysexams.GetSysExamsBO;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class GetSysExamCheckBoxAdapter extends RecyclerView.Adapter<GetSysExamCheckBoxAdapter.GetEMRViewHolder>{

    private final Context context;
    private final Object[] key;
    private final Map<String, ArrayList<GetSysExamsBO>> getSysExamCheckBoxMap;

    public GetSysExamCheckBoxAdapter(Context context, Object[] key, Map<String, ArrayList<GetSysExamsBO>> getSysExamCheckBoxMap) {
        this.context = context;
        this.key = key;
        this.getSysExamCheckBoxMap = getSysExamCheckBoxMap;
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

        if(getSysExamCheckBoxMap.get(key[position])!=null){
           GetSysExamCheckBoxDetailsAdapter getSysExamCheckBoxDetailsAdapter=new GetSysExamCheckBoxDetailsAdapter(context, Objects.requireNonNull(getSysExamCheckBoxMap.get(key[position])));
           holder.rvKey.setAdapter(getSysExamCheckBoxDetailsAdapter);
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
