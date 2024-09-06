package com.app.khaleeji.Adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.khaleeji.R;
import com.app.khaleeji.Response.fetchHotspotAndFrndsDetail.HotspotDetailData;

import java.util.List;

import CustomView.CustomEditText;
import CustomView.CustomTextView;
import Model.ChatUserData;
import Model.QAData;

public class SendAnswerAdapter extends RecyclerView.Adapter<SendAnswerAdapter.MyViewHolder> {

    private Context context;
    private List<QAData> listQAData;
    private OnKeypressListener mKeyPressListener;

    public SendAnswerAdapter(Context context, List<QAData>  listChatUser, OnKeypressListener onKeypressListener) {
        this.context = context;
        this.listQAData = listChatUser;
        mKeyPressListener = onKeypressListener;
    }

    @NonNull
    @Override
    public SendAnswerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_send_answer, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final SendAnswerAdapter.MyViewHolder holder, final int position) {
        holder.txtQuestion.setText(listQAData.get(position).getQuestion());
        holder.etAnswer.setText(listQAData.get(position).getAnswer());
        holder.etAnswer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() != 0)
                    mKeyPressListener.onKeyPress(position, s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return listQAData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CustomTextView txtQuestion;
        private CustomEditText etAnswer;
        public MyViewHolder(View itemView) {
            super(itemView);
            txtQuestion = itemView.findViewById(R.id.txtQuestion);
            etAnswer = itemView.findViewById(R.id.etAnswer);
        }
    }

    public interface OnKeypressListener{
        void onKeyPress(int index, String str);
    }
}
