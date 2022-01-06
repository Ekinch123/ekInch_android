package com.skinfotech.ekinch.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.skinfotech.ekinch.R;
import com.skinfotech.ekinch.models.responses.Video;

import java.util.List;

public class CertificateVideoListAdapter extends RecyclerView.Adapter<CertificateVideoListAdapter.CertificateViewHolder> {

    private final Activity activity;
    private final List<Video> certificateResponces;

    public CertificateVideoListAdapter(Activity activity, List<Video> certificateResponces) {
        this.activity = activity;
        this.certificateResponces = certificateResponces;

    }

    @NonNull
    @Override
    public CertificateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.certificate_item, parent, false);
        return new CertificateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CertificateViewHolder holder, int position) {
        Video data = certificateResponces.get(position);

        holder.videoNameTV.setText(data.getVideoName());

        if (data.getViews() >= 5)
        {
            holder.tick1.setVisibility(View.VISIBLE);
            holder.tick2.setVisibility(View.VISIBLE);
            holder.tick3.setVisibility(View.VISIBLE);
            holder.tick4.setVisibility(View.VISIBLE);
            holder.tick5.setVisibility(View.VISIBLE);
        }else if((data.getViews() == 4)){
            holder.tick1.setVisibility(View.VISIBLE);
            holder.tick2.setVisibility(View.VISIBLE);
            holder.tick3.setVisibility(View.VISIBLE);
            holder.tick4.setVisibility(View.VISIBLE);
            holder.tick5.setVisibility(View.GONE);
        }else if((data.getViews() == 3)){
            holder.tick1.setVisibility(View.VISIBLE);
            holder.tick2.setVisibility(View.VISIBLE);
            holder.tick3.setVisibility(View.VISIBLE);
            holder.tick4.setVisibility(View.GONE);
            holder.tick5.setVisibility(View.GONE);
        }else if((data.getViews() == 2)){
            holder.tick1.setVisibility(View.VISIBLE);
            holder.tick2.setVisibility(View.VISIBLE);
            holder.tick3.setVisibility(View.GONE);
            holder.tick4.setVisibility(View.GONE);
            holder.tick5.setVisibility(View.GONE);
        }else if((data.getViews() == 1)){
            holder.tick1.setVisibility(View.VISIBLE);
            holder.tick2.setVisibility(View.GONE);
            holder.tick3.setVisibility(View.GONE);
            holder.tick4.setVisibility(View.GONE);
            holder.tick5.setVisibility(View.GONE);
        }else if((data.getViews() <0)){
            holder.tick1.setVisibility(View.GONE);
            holder.tick2.setVisibility(View.GONE);
            holder.tick3.setVisibility(View.GONE);
            holder.tick4.setVisibility(View.GONE);
            holder.tick5.setVisibility(View.GONE);
        }


    }


    @Override
    public int getItemCount() {
        return certificateResponces.size();
    }

    public class CertificateViewHolder extends RecyclerView.ViewHolder {

        TextView videoNameTV;
        ImageView tick5, tick4, tick3, tick2, tick1;

        public CertificateViewHolder(@NonNull View itemView) {
            super(itemView);

            videoNameTV = itemView.findViewById(R.id.videoNameTV);
            tick5 = itemView.findViewById(R.id.tick5);
            tick4 = itemView.findViewById(R.id.tick4);
            tick3 = itemView.findViewById(R.id.tick3);
            tick2 = itemView.findViewById(R.id.tick2);
            tick1 = itemView.findViewById(R.id.tick1);
        }
    }
}
