package com.example.nursinghomeapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.PatientViewHolder> {
    private final RecyclerViewInterface mRecyclerViewInterface;

    private Context mCtx;
    private List<Patient> patientList;

    public PatientAdapter(Context mCtx, List<Patient> patientList,RecyclerViewInterface mRecyclerViewInterface) {
        this.mRecyclerViewInterface=mRecyclerViewInterface;
        this.mCtx = mCtx;
        this.patientList=patientList;
    }

    public void filterList(ArrayList<Patient> filterlist) {
        // below line is to add our filtered
        // list in our course array list.
        patientList = filterlist;
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged();
    }


    @Override
    public PatientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.cell_normal,parent,false);
        return new PatientViewHolder(view,mRecyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(PatientViewHolder holder, int position) {
        Patient patient = patientList.get(position);

        holder.textIC.setText("IC:"+String.valueOf(patient.getIC()));
        holder.textID.setText(String.valueOf(patient.getId()));
        holder.textName.setText(String.valueOf(patient.getName()));
    }

    @Override
    public int getItemCount() {
        return patientList.size();
    }

    public class PatientViewHolder extends RecyclerView.ViewHolder {

        TextView textIC, textID, textName;
        public PatientViewHolder(View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);

            textIC = itemView.findViewById(R.id.textIC);
            textID = itemView.findViewById(R.id.textID);
            textName = itemView.findViewById(R.id.textName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(recyclerViewInterface!=null){
                        int pos=getAdapterPosition();

                        if(pos!=RecyclerView.NO_POSITION){
                            recyclerViewInterface.onPatientClick(pos);
                        }
                    }
                }
            });
        }

    }
}
