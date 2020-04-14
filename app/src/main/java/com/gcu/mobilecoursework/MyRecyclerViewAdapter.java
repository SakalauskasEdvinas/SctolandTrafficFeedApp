package com.gcu.mobilecoursework;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.gcu.mobilecoursework.model.TrafficFeedModel;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// By Edvinas Sakalauskas - S1627176
public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> implements Filterable {

    private List<TrafficFeedModel> trafficData;
    private List<TrafficFeedModel> trafficDataFiltered;

    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Date selectedDate;
    private int size;
    private WeakReference<TextView> textViewWeakReference;

    // data is passed into the constructor
    public MyRecyclerViewAdapter(Context context, List<TrafficFeedModel> data, TextView infoArea) {
        this.mInflater = LayoutInflater.from(context);
        this.trafficData = data;
        this.trafficDataFiltered = data;
        this.textViewWeakReference = new WeakReference<>(infoArea);

    }



    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.traffic_list_item, parent, false);
        return new ViewHolder(view);
    }


    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TrafficFeedModel trafficItem = trafficDataFiltered.get(position);
        holder.title.setText(trafficItem.getTitle());
        if (trafficItem.getDescription().length() > 50) {
            holder.description.setText("Click to view description");

        } else {
            holder.description.setText(trafficItem.getDescription());
        }
        //if data is an incident it will not have start and end dates, so they will be skipped
        if (trafficItem.getStartDate() != null && trafficItem.getEndDate() != null) {
            holder.endDate.setVisibility(View.VISIBLE);
            holder.startDate.setVisibility(View.VISIBLE);
            holder.length.setVisibility(View.VISIBLE);
            holder.startDate.setText(holder.startDate.getContext().getString(R.string.start_date, trafficItem.getStartDateString()));
            holder.endDate.setText(holder.endDate.getContext().getString(R.string.end_date, trafficItem.getEndDateString()));
            long days = trafficItem.getRoadworkDayLength();
            holder.length.setText(holder.length.getContext().getString(R.string.duration_days, days));

            if (days < 7) {
                holder.timeImage.setImageResource(R.drawable.ic_clock_green);

            } else if (trafficItem.getRoadworkDayLength() < 30) {
                holder.timeImage.setImageResource(R.drawable.ic_clock_yellow);

            } else if (trafficItem.getRoadworkDayLength() > 30) {
                holder.timeImage.setImageResource(R.drawable.ic_clock_red);

            }
        } else {
            holder.endDate.setVisibility(View.GONE);
            holder.startDate.setVisibility(View.GONE);
            holder.timeImage.setImageResource(R.drawable.ic_accident);
            holder.length.setVisibility(View.GONE);

        }

    }

    // total number of rows
    @Override
    public int getItemCount() {

        return trafficDataFiltered.size();
    }



    public void setRecyclerViewData(List<TrafficFeedModel> trafficFeedModels) {
        this.trafficData = trafficFeedModels;
        this.trafficDataFiltered = trafficFeedModels;

    }

    public void setSelectedDate(Date selectedDate) {
        this.selectedDate = selectedDate;
    }

    // convenience method for getting data at click position
    public TrafficFeedModel getItem(int id) {
        return trafficDataFiltered.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty() && selectedDate == null) {
                    trafficDataFiltered = trafficData;
                } else {
                    List<TrafficFeedModel> filteredList = new ArrayList<TrafficFeedModel>();


                    for (TrafficFeedModel row : trafficData) {

                        // here we are looking for title or or description match
                        if (row.matchesTitleOrDescription(charString)) {

                            if (selectedDate != null) {
                                Date startDate = row.getStartDate();
                                Date endDate = row.getEndDate();


                                if ((selectedDate.after(startDate) || selectedDate.equals(startDate)) && (selectedDate.before(endDate) || selectedDate.equals(endDate))) {
                                    filteredList.add(row);
                                }
                            } else {
                                filteredList.add(row);
                            }

                        }
                    }

                    trafficDataFiltered = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = trafficDataFiltered;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                trafficDataFiltered = (ArrayList<TrafficFeedModel>) filterResults.values;
                notifyDataSetChanged();
                if(trafficDataFiltered.size() <=0 ) {
                    textViewWeakReference.get().setVisibility(View.VISIBLE);
                } else {
                    textViewWeakReference.get().setVisibility(View.INVISIBLE);

                }
            }
        };
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title, description, startDate, endDate, length;
        ImageView timeImage;

        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            startDate = itemView.findViewById(R.id.startDate);
            endDate = itemView.findViewById(R.id.endDate);
            length = itemView.findViewById(R.id.length_indicator);
            timeImage = itemView.findViewById(R.id.time_image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }



}