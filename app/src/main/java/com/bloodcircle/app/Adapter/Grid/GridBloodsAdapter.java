package com.bloodcircle.app.Adapter.Grid;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bloodcircle.app.Model.Grid.GridBloodsItem;
import com.bloodcircle.app.R;

import java.util.ArrayList;

public class GridBloodsAdapter extends ArrayAdapter<GridBloodsItem> {

    public GridBloodsAdapter(@NonNull Context context, ArrayList<GridBloodsItem> gridBloodsItems) {
        super(context, 0, gridBloodsItems);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listitemView = convertView;
        if (listitemView == null) {
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.item_grid_bloods, parent, false);
        }

        GridBloodsItem gridBloodsItem = getItem(position);
        TextView title = listitemView.findViewById(R.id.tv_title);
        ImageView icon = listitemView.findViewById(R.id.iv_icon);

        title.setText(gridBloodsItem.getTitle());
        icon.setBackgroundColor(Color.parseColor(gridBloodsItem.getColor()));
        return listitemView;
    }
}