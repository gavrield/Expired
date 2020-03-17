package com.myapps.expired.ui.main;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.myapps.expired.DAL.data.NotificationEntity;
import com.myapps.expired.R;

public class RecyclerViewAdapter extends RecyclerView.Adapter {

    private String[] mDataSet;

    public static class MassageHolder extends RecyclerView.ViewHolder{
        MassageView massageView;
        public MassageHolder(@NonNull MassageView massage) {
            super(massage);
            massageView = massage;
        }
    }
    public RecyclerViewAdapter(String[] dataSet){
        this.mDataSet = dataSet;
    }
    @NonNull
    @Override
    public RecyclerViewAdapter.MassageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        MassageView mv = (MassageView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.massage_view, parent, false);
        return new MassageHolder(mv);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ((MassageHolder)holder).massageView.setMassage(mDataSet[position]);
    }

    @Override
    public int getItemCount() {
        return mDataSet.length;
    }
}
