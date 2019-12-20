package com.pratham.foundation.ui.admin_panel.fragment_admin_panel.tab_usage;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pratham.foundation.R;
import com.pratham.foundation.modalclasses.Modal_ResourcePlayedByGroups;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.pratham.foundation.database.AppDatabase.appDatabase;

public class ResourcesAdapter extends RecyclerView.Adapter<ResourcesAdapter.ViewHolder> {

    private List<Modal_ResourcePlayedByGroups> datalist;

    public ResourcesAdapter(List<Modal_ResourcePlayedByGroups> datalist) {
        this.datalist = datalist;
    }

    @NonNull
    @Override
    public ResourcesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_stat_resource, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ResourcesAdapter.ViewHolder viewHolder, int i) {
        viewHolder.stat_res_name.setText(appDatabase.getContentTableDao().
                getContentTitleById(datalist.get(viewHolder.getAdapterPosition()).getResourceID()));
    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.stat_res_name)
        TextView stat_res_name;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
