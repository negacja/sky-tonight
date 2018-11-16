package com.example.barbara.skytonight.presentation.core;

import android.location.Location;
import android.util.Log;

import com.example.barbara.skytonight.entity.AstroObject;
import com.example.barbara.skytonight.data.AstroObjectsDataSource;
import com.example.barbara.skytonight.data.CoreDataSource;
import com.example.barbara.skytonight.data.CoreRepository;
import com.example.barbara.skytonight.data.AstroObjectRepository;
import com.example.barbara.skytonight.data.ISSDataSource;
import com.example.barbara.skytonight.entity.ISSObject;
import com.example.barbara.skytonight.data.ISSRepository;
import com.example.barbara.skytonight.data.WeatherDataSource;
import com.example.barbara.skytonight.entity.WeatherObject;
import com.example.barbara.skytonight.data.WeatherRepository;
import com.example.barbara.skytonight.entity.AstroConstants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TodayPresenter implements TodayContract.Presenter {

    private final AstroObjectRepository mAstroObjectRepository;
    private final CoreRepository mCoreRepository;
    private final WeatherRepository mWeatherRepository;
    private final ISSRepository mISSRepository;
    private final TodayContract.View mTodayView;
    private ArrayList<WeatherObject> weatherObjects;

    public TodayPresenter(AstroObjectRepository mAstroObjectRepository, CoreRepository mCoreRepository, WeatherRepository mWeatherRepository, ISSRepository mISSRepository, TodayContract.View mTodayView) {
        this.mAstroObjectRepository = mAstroObjectRepository;
        this.mCoreRepository = mCoreRepository;
        this.mWeatherRepository = mWeatherRepository;
        this.mISSRepository = mISSRepository;
        this.mTodayView = mTodayView;
    }

    @Override
    public void start() {
        mCoreRepository.getUserLocation(mTodayView.getCurrentActivity(), new CoreDataSource.GetUserLocationCallback() {
            @Override
            public void onDataLoaded(Location location) {
                Log.e("TodayPresenter", "onDataLoaded mFusedLocationClient success " + location.getLatitude() + " " + location.getLongitude());
                mTodayView.refreshLocationInAdapter(location);
                loadISS(location);
                loadWeather(location);
                showObjects(); //objects are shown asynchronously
            }

            @Override
            public void onRequestForPermission() {
                Log.e("TodayPresenter", "Waiting for response to request for permission @ CoreActivity");
            }

            @Override
            public void onDataNotAvailable() {
                Log.e("TodayPresenter", "onDataNotAvailable mFusedLocationClient failure");
                showObjects();
            }
        });
    }

    private void loadISS(Location location){
        final Calendar time = Calendar.getInstance();
        int overhead = mTodayView.getTimeOverhead();
        time.add(Calendar.HOUR, overhead);
        mISSRepository.getISSObject(time, location.getLatitude(), location.getLongitude(), new ISSDataSource.GetISSObject() {
            @Override
            public void onDataLoaded(ISSObject issObject) {
                mTodayView.updateList(issObject);
            }

            @Override
            public void onDataNotAvailable() {
                Log.e("TodayPresenter", "ISS station not available");
            }
        });
    }

    private void loadWeather(Location location){
        final Calendar time = Calendar.getInstance();
        int overhead = mTodayView.getTimeOverhead();
        time.add(Calendar.HOUR, overhead);
        mWeatherRepository.getWeatherObjects(location.getLatitude(), location.getLongitude(), new WeatherDataSource.GetWeatherObjectsCallback() {
            @Override
            public void onDataLoaded(List<WeatherObject> weatherObjectList) {
                weatherObjects = (ArrayList<WeatherObject>) weatherObjectList;
                int index = 0;
                for (int i = 0; i < weatherObjectList.size() && index == 0; i++){
                    if (weatherObjectList.get(i).getTime().getTimeInMillis() >= time.getTimeInMillis())
                        index = i;
                }
                WeatherObject next = weatherObjectList.get(index);
                mTodayView.updateWeatherView(next);
            }

            @Override
            public void onDataNotAvailable() {
                Log.e("TodayPresenter", "Weather not available");
            }
        });
    }

    private void showObjects(){
        final Calendar time = Calendar.getInstance();
        int overhead = mTodayView.getTimeOverhead();
        time.add(Calendar.HOUR, overhead);
        int [] objectIds = AstroConstants.ASTRO_OBJECT_IDS;
        mTodayView.clearList();
        for (int id: objectIds){
            mAstroObjectRepository.getAstroObject(time, id, new AstroObjectsDataSource.GetAstroObjectsCallback() {
                @Override
                public void onDataLoaded(AstroObject object) {
                    mTodayView.updateList(object);
                }
                @Override
                public void onDataNotAvailable() {
                    Log.e("TodayPresenter", "AstroObject not available");
                }
            });
        }
    }
}