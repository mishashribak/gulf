package com.app.khaleeji.Fragments;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.app.khaleeji.Activity.MainActivity;
import com.app.khaleeji.R;

public class VideoViewFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private VideoView mVideoView;
    private View view;
    private ImageView mCancelImg;
    private ProgressBar mProgressBar;

    public VideoViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VideoViewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VideoViewFragment newInstance(String param1, String param2) {
        VideoViewFragment fragment = new VideoViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_video_view, container, false);

        //init view
        initView();

        playVideo(mParam1);

        return view;
    }

    private void initView() {

        mCancelImg = view.findViewById(R.id.frag_video_view_cancel_img);
        mVideoView = view.findViewById(R.id.frag_video_view);
        mProgressBar = view.findViewById(R.id.frag_video_progress_bar);

        mCancelImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getContext()).onBackPressed();
            }
        });
    }

    private void playVideo(String videoUrl) {
        mVideoView.setVideoURI(Uri.parse(videoUrl + "#VideoPlay"));
        mVideoView.setMediaController(null);
        mVideoView.setScaleX(-1);
        mVideoView.requestFocus();
        mProgressBar.setVisibility(View.VISIBLE);
        mVideoView.start();
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                mProgressBar.setVisibility(View.GONE);
                int width = mp.getVideoWidth();
                int height = mp.getVideoHeight();
            }
        });
    }
}
