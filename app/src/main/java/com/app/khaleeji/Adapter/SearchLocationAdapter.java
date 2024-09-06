package com.app.khaleeji.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.app.khaleeji.R;
import com.app.khaleeji.Response.searchLocationPackage.SearchLocationPrediction;

import java.util.List;

import CustomView.CustomTextView;

/**
 * Created by Dcube on 12-10-2018.
 */

public class SearchLocationAdapter extends RecyclerView.Adapter<SearchLocationAdapter.ViewHolder> {

    //   private List<Bitmap> imageBitmaps;
    private LayoutInflater inflater;
    private OnChoosingSearchLocationListener onChoosingSearchLocationListener;
    String locationName;

    private int[] arLocationBackground = new int[ ]{
            R.drawable.rounded_corner_blue_locn_bg,
            R.drawable.rounded_corner_orange_bg,
            R.drawable.rounded_corner_location_gray_bg,
            R.drawable.rounded_corner_location_gray_bg,
            R.drawable.rounded_corner_location_gray_bg
    };

    String colorOrange = "#f38b00";
    String colorBlue = "#2d2a70";
    String colorWhite = "#ffffff";

    List<SearchLocationPrediction> nearByLocations;

    private String[] arLabelColor = new String[]{
            colorOrange,colorBlue,colorWhite,colorOrange,colorBlue
    };



    public SearchLocationAdapter(@NonNull Context context, List<SearchLocationPrediction> nearByLocations) {
        this.inflater = LayoutInflater.from(context);
        this.nearByLocations = nearByLocations;

        // this.imageBitmaps = imageBitmaps;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_near_by_location, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //holder.imageView.setImageBitmap(imageBitmaps.get(position));

        // holder.relParentLayout.setBackgroundResource(arLocationBackground[position]);

        holder.tvLocation.setText(nearByLocations.get(position).getStructuredFormatting().getMainText());

        holder.tvCityName.setText(nearByLocations.get(position).getStructuredFormatting().getSecondaryText());

        holder.relParentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //getBitmapFromView(relParentLayout);

                if (onChoosingSearchLocationListener != null)
                    onChoosingSearchLocationListener.onChoosingSearchLocationListener(position);


            }
        });

    }

    @Override
    public int getItemCount() {
        return  nearByLocations.size();
    }

    public void setOnImageClickListener(OnChoosingSearchLocationListener onChoosingSearchLocationListener) {
        this.onChoosingSearchLocationListener = onChoosingSearchLocationListener;
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        //  ImageView imageView;
        CustomTextView tvLocation,tvCityName;
        RelativeLayout relParentLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            //  imageView = (ImageView) itemView.findViewById(R.id.fragment_photo_edit_image_iv);
            tvLocation =  itemView.findViewById(R.id.tvLocation);
            tvCityName =  itemView.findViewById(R.id.tvCityName);
            relParentLayout =  itemView.findViewById(R.id.relParentLayout);

        }
    }

    public interface OnChoosingSearchLocationListener {
        void onChoosingSearchLocationListener(int pos);
    }

    public static Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable =view.getBackground();

        if (bgDrawable!=null)
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        else
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }


}

