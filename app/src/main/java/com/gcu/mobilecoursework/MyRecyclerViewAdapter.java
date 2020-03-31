package com.gcu.mobilecoursework;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.gcu.mobilecoursework.Model.TrafficFeedModel;

import java.util.List;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private List<TrafficFeedModel> trafficData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;


    // data is passed into the constructor
    MyRecyclerViewAdapter(Context context, List<TrafficFeedModel> data) {
        this.mInflater = LayoutInflater.from(context);
        this.trafficData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.traffic_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TrafficFeedModel trafficItem = trafficData.get(position);
        holder.title.setText(trafficItem.getTitle());
        holder.description.setText(trafficItem.getDescription());
        holder.link.setText(trafficItem.getLink());
        holder.geoPoint.setText(trafficItem.getGeoPoint());
        holder.author.setText(trafficItem.getAuthor());
        holder.pubDate.setText(trafficItem.getPubDate().toString());

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return trafficData.size();
    }

    public void setRecyclerViewData(List<TrafficFeedModel> trafficFeedModels) {
        this.trafficData = trafficFeedModels;
    }
    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title, description, link, geoPoint, author, comments, pubDate;

        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            link = itemView.findViewById(R.id.link);
            geoPoint = itemView.findViewById(R.id.geoPoint);
            author = itemView.findViewById(R.id.author);
            comments = itemView.findViewById(R.id.comments);
            pubDate = itemView.findViewById(R.id.pubDate);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }



    // convenience method for getting data at click position
    String getItem(int id) {
        return trafficData.get(id).getTitle();
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}