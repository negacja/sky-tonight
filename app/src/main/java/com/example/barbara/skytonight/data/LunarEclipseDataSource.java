package com.example.barbara.skytonight.data;

import java.util.Calendar;
import java.util.List;

public interface LunarEclipseDataSource {

    interface GetLunarEclipsesCallback {

        void onDataLoaded(List<LunarEclipseEvent> events);
        void onDataNotAvailable();
    }

    void getLunarEclipses(GetLunarEclipsesCallback callback);
}
