package photoEditor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.app.khaleeji.R;
import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import Constants.AppConstants;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private int index;
    private List<Integer> stickerDrawableList;
    private LayoutInflater inflater;
    private OnImageClickListener onImageClickListener;
    private Context context;
    private boolean isVideoEditorActivity;


    public ImageAdapter(@NonNull Context context, @NonNull List<Integer> stickerDrawableList, int index, boolean isVideoEditorActivity) {
        this.inflater = LayoutInflater.from(context);
        this.stickerDrawableList = stickerDrawableList;
        this.index = index;
        this.context = context;
        this.isVideoEditorActivity = isVideoEditorActivity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.fragment_photo_edit_image_item_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (index == 0 && position == 0) {
                    onImageClickListener.onImageClickListener(R.drawable.sticker_locationn
                            , AppConstants.TYPE_LOCATION);
                } else {
                    if (onImageClickListener != null){
                        if(index == 0 ){
                            onImageClickListener.onImageClickListener(stickerDrawableList.get(position-1)
                                    , AppConstants.TYPE_IMAGE);
                        }else{
                            onImageClickListener.onImageClickListener(stickerDrawableList.get(position)
                                    , AppConstants.TYPE_IMAGE);
                        }
                    }
                }
            }
        });
        
        if (index == 0 && position == 0) {
            Glide.with(context).load(R.drawable.sticker_locationn).into(holder.imageView);
//            holder.imageView.setImageResource(R.drawable.sticker_locationn);
        } else {
            try{
//                File f = new File(stickerDrawableList.get(position));
//                BitmapFactory.Options options = new BitmapFactory.Options();
//                options.inPreferredConfig = Bitmap.Config.RGB_565;
//                Bitmap image = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
//                holder.imageView.setImageBitmap(image);
                try{
//                    holder.imageView.setImageURI(Uri.parse(stickerDrawableList.get(position)));
                    if(index == 0){
                        Glide.with(context).load(stickerDrawableList.get(position-1)).into(holder.imageView);
//                        holder.imageView.setImageResource(stickerDrawableList.get(position-1));
                    }else{
                        Glide.with(context).load(stickerDrawableList.get(position)).into(holder.imageView);
//                        holder.imageView.setImageResource(stickerDrawableList.get(position));
                    }

                }catch (Exception ex){
                    ex.printStackTrace();
                }

            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        if(index == 0){
            return stickerDrawableList.size()+1;
        }
        return stickerDrawableList.size();
    }

    public void setOnImageClickListener(OnImageClickListener onImageClickListener) {
        this.onImageClickListener = onImageClickListener;
    }

    public interface OnImageClickListener {
        void onImageClickListener(int image, String type);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.fragment_photo_edit_image_iv);
        }
    }
}
