package com.hh.blerssi;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaopeng on 2017/7/29.
 */

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder>  {

    private static final String TAG = MainActivity.class.getSimpleName();

    public interface OnItemClickLitener
    {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener)
    {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    private LayoutInflater mInflater;
    List<NSDevice> mDeviceList = new ArrayList<>();

    public MainAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final MainViewHolder holder = new MainViewHolder(mInflater.inflate(R.layout.item_main, parent, false));

        // 如果设置了回调，则设置点击事件
        if (mOnItemClickLitener != null)
        {
            holder.itemView.setOnClickListener(v -> {
                int pos = holder.getLayoutPosition();
                mOnItemClickLitener.onItemClick(holder.itemView, pos);
            });

            holder.itemView.setOnLongClickListener(v -> {
                int pos = holder.getLayoutPosition();
                mOnItemClickLitener.onItemLongClick(holder.itemView, pos);
                return false;
            });
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(MainViewHolder holder, int position) {
        String name = mDeviceList.get(position).getName();
        String address = mDeviceList.get(position).getAddress();
        int rssi = mDeviceList.get(position).getRssi();

        if(name == null){
            name = "N/A";
        }
        holder.tv_name.setText(name);
        holder.tv_address.setText(address);
        holder.tv_rssi.setText("rssi: " + String.valueOf(rssi));

        double distance = RssiUtil.getDistance(rssi);
        holder.tv_distance.setText("距离" + String.format("%.2f", distance));
    }

    @Override
    public int getItemCount() {
        return mDeviceList.size();
    }

    static class MainViewHolder extends RecyclerView.ViewHolder
    {
        TextView tv_name;
        TextView tv_address;
        TextView tv_rssi;
        TextView tv_distance;


        public MainViewHolder(View view)
        {
            super(view);
            tv_name = view.findViewById(R.id.holder_name);
            tv_address = view.findViewById(R.id.holder_address);
            tv_rssi = view.findViewById(R.id.tv_rssi);
            tv_distance = view.findViewById(R.id.tv_distance);

        }
    }

    public void setData(List<NSDevice> devices){
        mDeviceList = devices;
    }
}
