package com.example.barbara.skytonight.presentation.core;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.barbara.skytonight.R;
import com.example.barbara.skytonight.entity.AstroEvent;
import com.example.barbara.skytonight.entity.NewsHeadline;

import java.util.ArrayList;

public class NewsFragment extends Fragment implements NewsContract.View {

    private OnListFragmentInteractionListener mListener;
    private NewsRecyclerViewAdapter mAdapter;
    private NewsContract.Presenter mPresenter;
    private Context context;
    private RecyclerView recyclerView;
    ArrayList<NewsHeadline> list;

    public NewsFragment() {
        list = new ArrayList<>();
    }

    public static NewsFragment newInstance(String param1, String param2) {
        NewsFragment fragment = new NewsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_news, container, false);
        recyclerView = view.findViewById(R.id.newsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mAdapter = new NewsRecyclerViewAdapter(list, mListener);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void clearList() {
        this.list.clear();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateList(ArrayList<NewsHeadline> list) {
        this.list.clear();
        this.list.addAll(list);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public Activity getCurrentActivity() {
        return getActivity();
    }

    @Override
    public void setPresenter(NewsContract.Presenter presenter) {
        mPresenter = presenter;
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(AstroEvent event);
    }
}