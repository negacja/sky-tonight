package com.example.barbara.skytonight.core;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.barbara.skytonight.R;
import com.example.barbara.skytonight.data.AstroObject;
import com.example.barbara.skytonight.data.TodayRepository;

import java.util.ArrayList;

public class TodayFragment extends Fragment implements TodayContract.View {

    private TodayContract.Presenter mPresenter;

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private MyTodayRecyclerViewAdapter mAdapter;
    ArrayList<AstroObject> list;

    public TodayFragment() {
        list = new ArrayList<AstroObject>();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        mAdapter = new MyTodayRecyclerViewAdapter(list, mListener, -27.104671, -109.360481);
        mPresenter.getUserLocation(new TodayContract.GetUserLocationCallback(){
            @Override
            public void onDataLoaded(Location location) {
                refreshLocation(location);
            }

            @Override
            public void onDataNotAvailable() {
            }
        });
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_today_list, container, false);
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        }
        return view;
    }

    public void refreshLocation(Location location) {
        mAdapter.setLatLng(location.getLatitude(), location.getLongitude());
        updateList(getList());
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void updateList(ArrayList<AstroObject> list) {
        this.list.clear();
        this.list.addAll(list);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public ArrayList<AstroObject> getList() {
        ArrayList<AstroObject> copyList = new ArrayList<AstroObject>();
        copyList.addAll(this.list);
        return copyList;
    }

    @Override
    public void clearList() {
        this.list.clear();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateList(AstroObject object) {
        this.list.add(object);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public Activity getCurrentActivity(){
        return getActivity();
    }

    @Override
    public void setPresenter(TodayContract.Presenter presenter) {
        mPresenter = presenter;
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(String item);
    }

    public static TodayFragment newInstance(int columnCount) {
        TodayFragment fragment = new TodayFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }
}
