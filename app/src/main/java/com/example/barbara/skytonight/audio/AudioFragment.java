package com.example.barbara.skytonight.audio;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.barbara.skytonight.R;
import com.example.barbara.skytonight.photos.MyPhotoRecyclerViewAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

public class AudioFragment extends Fragment implements AudioContract.View {

    private AudioContract.Presenter mPresenter;
    private MyAudioRecyclerViewAdapter mAdapter;
    private RecyclerView recyclerView;
    private ArrayList<File> fileList;
    private Calendar selectedDate;
    private View view;
    private Integer selectedYear = null;
    private Integer selectedMonth = null;

    @Override
    public void setPresenter(com.example.barbara.skytonight.audio.AudioContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (fileList.isEmpty())
            mPresenter.start();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_audio, container, false);
        fileList = new ArrayList<>();
        FloatingActionButton button = view.findViewById(R.id.floatingActionButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //mPresenter.dispatchTakePhotoIntent();
            }
        });
        mAdapter = new MyAudioRecyclerViewAdapter(fileList);
        recyclerView = view.findViewById(R.id.audioRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public Context getContext() { return view.getContext(); }

    @Override
    public ArrayList<File> getFileList() { return fileList; }

    @Override
    public Calendar getSelectedDate() { return selectedDate; }

    @Override
    public Integer getSelectedMonth() {
        return selectedMonth;
    }

    @Override
    public Integer getSelectedYear() {
        return selectedYear;
    }

    public void setSelectedDate(Calendar selectedDate) {
        this.selectedDate = selectedDate;
    }

    public void setSelectedMonthYear(int month, int year) {
        selectedDate = null;
        selectedMonth = month;
        selectedYear = year;
    }

    @Override
    public Activity getViewActivity() { return getActivity(); }

    @Override
    public void clearListInView() {
        fileList.clear();
    }

    @Override
    public void refreshListInView() {
        mAdapter.notifyDataSetChanged();
    }

}