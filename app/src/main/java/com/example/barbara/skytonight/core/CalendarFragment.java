package com.example.barbara.skytonight.core;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.barbara.skytonight.R;
import com.example.barbara.skytonight.notes.NotesActivity;
import com.example.barbara.skytonight.photos.PhotoGalleryActivity;
import com.ramotion.circlemenu.CircleMenuView;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.Calendar;

public class CalendarFragment extends Fragment implements CalendarContract.View {

    private CalendarContract.Presenter mPresenter;
    private OnFragmentInteractionListener mListener;
    private Calendar currentlySelectedDate = Calendar.getInstance();
    private ExpandableLayout expandableLayout;
    private ExpandableLayout expandableLayout2;
    private TextView monthTextView;
    private int currentlyDisplayedMonth;
    private int currentlyDisplayedYear;
    private View view;
    private Context context;

    public CalendarFragment() { }

    public static CalendarFragment newInstance() {
        CalendarFragment fragment = new CalendarFragment();
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
    public void setPresenter(CalendarContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public Activity getViewActivity() {
        return getActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_calendar, container, false);
        context = view.getContext();
        monthTextView = view.findViewById(R.id.monthTextView);
        expandableLayout = view.findViewById(R.id.expandableLayout);
        expandableLayout.collapse();
        expandableLayout2 = view.findViewById(R.id.expandableLayout2);
        expandableLayout2.collapse();
        final CircleMenuView circleMenuView = view.findViewById(R.id.circleMenu);
        circleMenuView.setDurationRing(200);
        circleMenuView.setEventListener(new CircleMenuView.EventListener() {
            @Override
            public void onButtonClickAnimationEnd(@NonNull CircleMenuView view, int index) {
                switch (index) {
                    case 0:
                        break;
                    case 1: onPhotosButtonClick();
                        break;
                    case 2: onNotesButtonClick();
                        break;
                    default:
                        break;
                }
            }
        });
        hideButton();
        final FloatingActionButton floatingActionButton = view.findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandableLayout.collapse();
                floatingActionButton.hide();
                circleMenuView.setVisibility(View.VISIBLE);
            }
        });
        TabLayout tabLayout = view.findViewById(R.id.tabs);
        setUpTabLayout(tabLayout);
        CalendarView calendarView = view.findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(year, month, dayOfMonth);
                updateDayInfoText(selectedDate);
                currentlySelectedDate = selectedDate;
            }
        });
        setPreviousNextButtons();
        return view;
    }

    private void setPreviousNextButtons(){
        AppCompatImageButton nextMonthButton = view.findViewById(R.id.nextMonthButton);
        nextMonthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goForwards();
                setCurrentMonthTextView();
            }
        });
        AppCompatImageButton previousMonthButton = view.findViewById(R.id.previousMonthButton);
        previousMonthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackwards();
                setCurrentMonthTextView();
            }
        });
        setCurrentMonthTextView();
    }

    private void hideCircleMenu() {
        CircleMenuView circleMenuView = view.findViewById(R.id.circleMenu);
        circleMenuView.setVisibility(View.INVISIBLE);
        Log.e("CalendarFragment", "hide");
    }

    private void showCircleMenu() {
        final CircleMenuView circleMenuView = view.findViewById(R.id.circleMenu);
        circleMenuView.setVisibility(View.VISIBLE);
    }

    private void showButton() {
        FloatingActionButton floatingActionButton = view.findViewById(R.id.floatingActionButton);
        floatingActionButton.show();
    }

    private void hideButton() {
        FloatingActionButton floatingActionButton = view.findViewById(R.id.floatingActionButton);
        floatingActionButton.hide();
    }

    private void setUpTabLayout(TabLayout tabLayout) {
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_day));
        TabLayout.Tab weekTab = tabLayout.newTab().setText(R.string.tab_week);
        tabLayout.addTab(weekTab);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_month));
        weekTab.select();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    hideCircleMenu();
                    expandableLayout2.collapse();
                    expandableLayout.expand();
                    showButton();
                }
                else if (tab.getPosition() == 2) {
                    showCircleMenu();
                    expandableLayout.collapse();
                    expandableLayout2.expand();
                    hideButton();
                }
                else {
                    showCircleMenu();
                    expandableLayout.collapse();
                    expandableLayout2.collapse();
                    hideButton();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    hideCircleMenu();
                    expandableLayout.expand();
                    showButton();
                }
            }
        });
    }

    private void goForwards() {
        if (currentlyDisplayedMonth < 11) {
            currentlyDisplayedMonth++;
        }
        else {
            currentlyDisplayedMonth = 0;
            currentlyDisplayedYear++;
        }
    }

    private void goBackwards() {
        if (currentlyDisplayedMonth > 0 && !(currentlyDisplayedYear == Calendar.getInstance().get(Calendar.YEAR) && currentlyDisplayedMonth == Calendar.getInstance().get(Calendar.MONTH) )) {
            currentlyDisplayedMonth--;
        }
        else if (currentlyDisplayedYear > Calendar.getInstance().get(Calendar.YEAR)){
            currentlyDisplayedMonth = 11;
            currentlyDisplayedYear--;
        }
    }

    private void setCurrentMonthTextView(){
        try {
            String resourceString = "month_" + currentlyDisplayedMonth;
            int resourceStringId = context.getResources().getIdentifier(resourceString, "string", context.getPackageName());
            monthTextView.setText(context.getResources().getString(resourceStringId, currentlyDisplayedYear));
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
            monthTextView.setText(R.string.month_unknown);
        }
    }


    @Override
    public void updateDayInfoText(Calendar selectedDate) {
        int numberOfPhotos = mPresenter.getNumberOfPhotos(selectedDate);
        int numberOfWords = mPresenter.getNumberOfWords(selectedDate);
        TextView textView = view.findViewById(R.id.dayInfoTextView);
        textView.setText(context.getString(R.string.day_info_text, numberOfWords, numberOfWords != 1 ? "s" : "", numberOfPhotos, numberOfPhotos != 1 ? "s" : ""));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.currentlyDisplayedMonth = Calendar.getInstance().get(Calendar.MONTH);
        this.currentlyDisplayedYear = Calendar.getInstance().get(Calendar.YEAR);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    private void onPhotosButtonClick() {
        Intent intent = new Intent(getActivity(), PhotoGalleryActivity.class);
        intent.putExtra("year", currentlySelectedDate.get(Calendar.YEAR));
        intent.putExtra("dayOfYear", currentlySelectedDate.get(Calendar.DAY_OF_YEAR));
        startActivity(intent);
    }

    private void onNotesButtonClick() {
        Intent intent = new Intent(getActivity(), NotesActivity.class);
        intent.putExtra("year", currentlySelectedDate.get(Calendar.YEAR));
        intent.putExtra("dayOfYear", currentlySelectedDate.get(Calendar.DAY_OF_YEAR));
        startActivity(intent);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
