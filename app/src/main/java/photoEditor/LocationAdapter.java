package photoEditor;

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

import CustomView.CustomTextView;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {

    String locationName;
    String colorOrange = "#f38b00";
    String colorBlue = "#2d2a70";
    String colorWhite = "#ffffff";
    //   private List<Bitmap> imageBitmaps;
    private LayoutInflater inflater;
    private OnImageClickListener onImageClickListener;
    private int[] arLocationBackground = new int[]{
            R.drawable.rounded_corner_location_gray_bg,
            R.drawable.rounded_corner_blue_locn_bg,
            R.drawable.rounded_corner_location_gray_bg,
            R.drawable.rounded_corner_orange_bg,
            R.drawable.rounded_corner_location_gray_bg
    };
    private String[] arLabelColor = new String[]{
            colorWhite, colorOrange, colorOrange, colorBlue, colorBlue
    };

    public LocationAdapter(@NonNull Context context, String locationName) {
        this.inflater = LayoutInflater.from(context);
        this.locationName = locationName;
    }

    public static Bitmap getBitmapFromView(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable = view.getBackground();

        if (bgDrawable != null)
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

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.fragment_photo_edit_location_item_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.relParentLayout.setBackgroundResource(arLocationBackground[position]);
        holder.tvLocation.setText("#"+locationName);
//        holder.tvHash.setTextColor(Color.parseColor(arLabelColor[position]));
        holder.tvLocation.setTextColor(Color.parseColor(arLabelColor[position]));
        holder.tvLocation.setBackgroundResource(arLocationBackground[position]);

        holder.relParentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onImageClickListener != null){
//                    holder.tvLocation.setText("#"+locationName);
                    onImageClickListener.onImageClickListener(getBitmapFromView(holder.tvLocation));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return arLocationBackground.length;  //imageBitmaps.size()
    }

    public void setOnImageClickListener(OnImageClickListener onImageClickListener) {
        this.onImageClickListener = onImageClickListener;
    }

    public interface OnImageClickListener {
        void onImageClickListener(Bitmap image);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        CustomTextView tvLocation, tvHash;
        RelativeLayout relParentLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            tvLocation = itemView.findViewById(R.id.tvLocation);
//            tvHash = itemView.findViewById(R.id.tvHash);
            relParentLayout = itemView.findViewById(R.id.relParentLayout);
        }
    }
}

