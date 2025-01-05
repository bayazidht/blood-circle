package com.bloodcircle.app.Adapter.Grid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bloodcircle.app.Model.Grid.GridMenuItem;
import com.bloodcircle.app.R;

import java.util.ArrayList;

public class GridMenuAdapter extends ArrayAdapter<GridMenuItem> {

    public GridMenuAdapter(@NonNull Context context, ArrayList<GridMenuItem> gridMenuItems) {
        super(context, 0, gridMenuItems);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listitemView = convertView;
        if (listitemView == null) {
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.item_grid_menu, parent, false);
        }

        GridMenuItem gridMenuItem = getItem(position);
        TextView title = listitemView.findViewById(R.id.tv_title);
        ImageView icon = listitemView.findViewById(R.id.iv_icon);

        title.setText(gridMenuItem.getTitle());
        icon.setImageResource(gridMenuItem.getIcon());
        return listitemView;
    }
}