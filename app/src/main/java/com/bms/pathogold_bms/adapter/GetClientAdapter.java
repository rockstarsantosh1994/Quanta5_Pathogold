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
import com.bms.pathogold_bms.model.getclientuploadedfile.GetClientUploadedBO;
import com.bms.pathogold_bms.model.getpatientlist.GetPatientListBO;
import com.bms.pathogold_bms.services.DigiPath;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class GetClientAdapter extends RecyclerView.Adapter<GetClientAdapter.GetClientViewHolder>{

    private final Context context;
    private final String[] stringArray;
    private final Map<String, ArrayList<GetClientUploadedBO>> getClientMap;
    private final GetPatientListBO getPatientListBO;
    private final DigiPath digiPath;

    public GetClientAdapter(Context context, String[] stringArray, Map<String, ArrayList<GetClientUploadedBO>> getClientMap, GetPatientListBO getPatientListBO1,DigiPath digiPath1) {
        this.context = context;
        this.stringArray = stringArray;
        this.getClientMap = getClientMap;
        this.getPatientListBO = getPatientListBO1;
        this.digiPath = digiPath1;
    }

    @NonNull
    @Override
    public GetClientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater= LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.row_get_client_image,parent,false);
        return new GetClientViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull GetClientViewHolder holder, int position) {
        holder.tvKey.setText(""+stringArray[position]);

        if(getClientMap.get(stringArray[position])!=null){
            GetClientImageDetailsAdapter2 getClientImageDetailsAdapter2 =new GetClientImageDetailsAdapter2(context, Objects.requireNonNull(getClientMap.get(stringArray[position])),getPatientListBO,digiPath);
            holder.rvGetImageDetails.setAdapter(getClientImageDetailsAdapter2);
        }
    }

    @Override
    public int getItemCount() {
        return stringArray.length;
    }

    public class GetClientViewHolder extends RecyclerView.ViewHolder{

        private final TextView tvKey;
        private final RecyclerView rvGetImageDetails;

        public GetClientViewHolder(@NonNull View itemView) {
            super(itemView);
            tvKey=itemView.findViewById(R.id.tv_key);
            rvGetImageDetails=itemView.findViewById(R.id.rv_get_client_image_detail);
            GridLayoutManager gridLayoutManager=new GridLayoutManager(context,2);
            rvGetImageDetails.setLayoutManager(gridLayoutManager);
        }
    }
}
