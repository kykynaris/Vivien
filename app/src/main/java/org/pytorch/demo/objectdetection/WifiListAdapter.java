package org.pytorch.demo.objectdetection;

import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WifiListAdapter extends RecyclerView.Adapter<WifiListAdapter.WifiListViewHolder> {
    private List<ScanResult> wifiList;

    public WifiListAdapter(List<ScanResult> wifiList) {
        this.wifiList = wifiList;
    }

    @NonNull
    @Override
    public WifiListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wifi_list_item, parent, false);
        return new WifiListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WifiListViewHolder holder, int position) {
        ScanResult wifi = wifiList.get(position);
        holder.wifiSsid.setText(wifi.SSID);
        holder.wifiBssid.setText(wifi.BSSID);
        holder.wifiRssi.setText(String.format("%d dBm", wifi.level));
    }

    @Override
    public int getItemCount() {
        return wifiList.size();
    }

    public void updateWifiList(List<ScanResult> newWifiList) {
        wifiList.clear();
        wifiList.addAll(newWifiList);
        notifyDataSetChanged();
    }

    public static class WifiListViewHolder extends RecyclerView.ViewHolder {
        private TextView wifiSsid;
        private TextView wifiBssid;
        private TextView wifiRssi;

        public WifiListViewHolder(@NonNull View itemView) {
            super(itemView);
            wifiSsid = itemView.findViewById(R.id.wifi_ssid);
            wifiBssid = itemView.findViewById(R.id.wifi_bssid);
            wifiRssi = itemView.findViewById(R.id.wifi_rssi);
        }
    }

}

