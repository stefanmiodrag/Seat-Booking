package com.university.soa.bus.SeatClass;

/**
 * Created by pkumar on 5/6/18.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.university.soa.bus.R;
import com.university.soa.bus.SeatClass.SelectableAdapter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AirplaneAdapter extends SelectableAdapter<RecyclerView.ViewHolder> {

    private OnSeatSelected mOnSeatSelected;
    Set<Integer> selected=new HashSet<Integer>();

    private static class EdgeViewHolder extends RecyclerView.ViewHolder {

        ImageView imgSeat;
        private final ImageView imgSeatSelected;


        public EdgeViewHolder(View itemView) {
            super(itemView);
            imgSeat = (ImageView) itemView.findViewById(R.id.img_seat);
            imgSeatSelected = (ImageView) itemView.findViewById(R.id.img_seat_selected);

        }

    }

    private static class CenterViewHolder extends RecyclerView.ViewHolder {

        ImageView imgSeat;
        private final ImageView imgSeatSelected;

        public CenterViewHolder(View itemView) {
            super(itemView);
            imgSeat = (ImageView) itemView.findViewById(R.id.img_seat);
            imgSeatSelected = (ImageView) itemView.findViewById(R.id.img_seat_selected);


        }

    }

    private static class EmptyViewHolder extends RecyclerView.ViewHolder {

        public EmptyViewHolder(View itemView) {
            super(itemView);
        }

    }

    private Context mContext;
    private LayoutInflater mLayoutInflater;

    private List<AbstractItem> mItems;

    public AirplaneAdapter(Context context, List<AbstractItem> items) {
        mOnSeatSelected = (OnSeatSelected) context;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mItems = items;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mItems.get(position).getType();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == AbstractItem.TYPE_CENTER) {
            View itemView = mLayoutInflater.inflate(R.layout.list_item_seat, parent, false);
            return new CenterViewHolder(itemView);
        } else if (viewType == AbstractItem.TYPE_EDGE) {
            View itemView = mLayoutInflater.inflate(R.layout.list_item_seat, parent, false);
            return new EdgeViewHolder(itemView);
        } else {
            View itemView = new View(mContext);
            return new EmptyViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        int type = mItems.get(position).getType();
        if (type == AbstractItem.TYPE_CENTER) {
            final CenterItem item = (CenterItem) mItems.get(position);
            CenterViewHolder holder = (CenterViewHolder) viewHolder;
            if(SeatSelection.positions.contains(String.valueOf(position)) && !selected.contains(position) ) {
                item.setSelectable(false);
                holder.imgSeatSelected.setVisibility(View.VISIBLE);

            }


            holder.imgSeat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(item.isSelectable()) {
                        toggleSelection(position);
                        if (isSelected(position)) {
                            SeatSelection.positions.add(String.valueOf(position));
                            selected.add(position);
                        } else if (SeatSelection.positions.contains(String.valueOf(position))) {
                            SeatSelection.positions.remove(String.valueOf(position));
                            selected.remove(position);
                        }
                    }
                    else {
                        Toast.makeText(mContext, "Seat already booked", Toast.LENGTH_SHORT).show();
                    }
                    mOnSeatSelected.onSeatSelected(getSelectedItemCount());
                }
            });


            holder.imgSeatSelected.setVisibility(isSelected(position)||!item.isSelectable() ? View.VISIBLE : View.GONE);

        } else if (type == AbstractItem.TYPE_EDGE) {
            final EdgeItem item = (EdgeItem) mItems.get(position);
            EdgeViewHolder holder = (EdgeViewHolder) viewHolder;
            if(SeatSelection.positions.contains(String.valueOf(position)) && !selected.contains(position) ) {
                item.setSelectable(false);
                holder.imgSeatSelected.setVisibility(View.VISIBLE);

            }


            holder.imgSeat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(item.isSelectable()) {
                        toggleSelection(position);
                        if (isSelected(position)) {
                            SeatSelection.positions.add(String.valueOf(position));
                            selected.add(position);
                        } else if (SeatSelection.positions.contains(String.valueOf(position))) {
                            SeatSelection.positions.remove(String.valueOf(position));
                            selected.remove(position);
                        }
                    }
                    else {
                        Toast.makeText(mContext, "Seat already booked", Toast.LENGTH_SHORT).show();

                    }
                    mOnSeatSelected.onSeatSelected(getSelectedItemCount());
                }
            });

            holder.imgSeatSelected.setVisibility(isSelected(position)||!item.isSelectable() ? View.VISIBLE : View.GONE);

        }
    }

}