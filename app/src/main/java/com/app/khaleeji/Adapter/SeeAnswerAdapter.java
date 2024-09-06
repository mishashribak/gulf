package com.app.khaleeji.Adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.app.khaleeji.R;
import com.app.khaleeji.Response.fetchHotspotAndFrndsDetail.HotspotDetailData;

import java.util.List;

import CustomView.CustomTextView;
import Model.ChatUserData;
import Model.QAData;

public class SeeAnswerAdapter extends RecyclerView.Adapter<SeeAnswerAdapter.MyViewHolder> {

    private Context context;
    private List<QAData> listQAData;
    private OnCheckListener onCheckListener;

    public SeeAnswerAdapter(Context context, List<QAData>  listChatUser, OnCheckListener onCheckListener) {
        this.context = context;
        this.listQAData = listChatUser;
        this.onCheckListener = onCheckListener;
    }

    @NonNull
    @Override
    public SeeAnswerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_see_answer, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final SeeAnswerAdapter.MyViewHolder holder, final int position) {
        holder.txtQuestion.setText(listQAData.get(position).getQuestion());
        holder.txtAnswer.setText(listQAData.get(position).getAnswer());
        holder.check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean on = ((ToggleButton) view).isChecked();
                onCheckListener.onCheck(position, on);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listQAData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CustomTextView txtQuestion;
        private CustomTextView txtAnswer;
        private ToggleButton check;
        public MyViewHolder(View itemView) {
            super(itemView);
            txtQuestion = itemView.findViewById(R.id.txtQuestion);
            txtAnswer = itemView.findViewById(R.id.txtAnswer);
            check = itemView.findViewById(R.id.check);
        }
    }

    public interface  OnCheckListener{
        void onCheck(int index, boolean isChecked);
    }
}
