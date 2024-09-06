package com.app.khaleeji.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.khaleeji.R;

public class FontPickerAdapter extends RecyclerView.Adapter<FontPickerAdapter.MyViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private String[] fontArray;
    private OnFontPickerClickListener onFontPickerClickListener;


    public FontPickerAdapter(@NonNull Context context, @NonNull String[] fontArray) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.fontArray = fontArray;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.font_picker_item_list, parent, false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Typeface newFont = Typeface.createFromAsset(context.getAssets(), fontArray[position]);
        holder.fontPickerView.setTypeface(newFont);

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onFontPickerClickListener != null)
                    onFontPickerClickListener.onFontPickerClickListener(position);
            }
        });

        if (position >= 13)
        {
            holder.fontPickerView.setText("خليجي");
        }
        else
        {
            holder.fontPickerView.setText("Khaleeji");
        }

    }

    @Override
    public int getItemCount() {
        return fontArray.length;
    }

    private void buildColorPickerView(View view, int colorCode) {

        view.setVisibility(View.VISIBLE);

        ShapeDrawable biggerCircle = new ShapeDrawable(new OvalShape());
        biggerCircle.setIntrinsicHeight(20);
        biggerCircle.setIntrinsicWidth(20);
        biggerCircle.setBounds(new Rect(0, 0, 20, 20));
        biggerCircle.getPaint().setColor(colorCode);

        ShapeDrawable smallerCircle = new ShapeDrawable(new OvalShape());
        smallerCircle.setIntrinsicHeight(5);
        smallerCircle.setIntrinsicWidth(5);
        smallerCircle.setBounds(new Rect(0, 0, 5, 5));
        smallerCircle.getPaint().setColor(Color.WHITE);
        smallerCircle.setPadding(10, 10, 10, 10);
        Drawable[] drawables = {smallerCircle, biggerCircle};

        LayerDrawable layerDrawable = new LayerDrawable(drawables);

        view.setBackgroundDrawable(layerDrawable);
    }

    public void setOnFontPickerClickListener(OnFontPickerClickListener onFontPickerClickListener) {
        this.onFontPickerClickListener = onFontPickerClickListener;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView fontPickerView;
        LinearLayout parentLayout;

        public MyViewHolder(View itemView) {
            super(itemView);
            fontPickerView = itemView.findViewById(R.id.fontPickerView);
            parentLayout = itemView.findViewById(R.id.parentLayout);
        }
    }

    public interface OnFontPickerClickListener {
        void onFontPickerClickListener(int colorCode);
    }
}

