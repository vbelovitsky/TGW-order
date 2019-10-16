package com.java.boris.tgw.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.java.boris.tgw.R;

import java.util.ArrayList;

public class DiaryFragment extends Fragment {

    ListView goalListView;
    private String[] goalArray = {"Плавать утром", "Здоровое питание", "Найти девушку", "Заработать денег", "Научиться рисовать",
            "Плавать утром", "Здоровое питание", "Найти девушку", "Заработать денег", "Научиться рисовать"};
    private String[] categoryArray = {"Здоровье", "Здоровье", "Отношения", "Работа", "Хобби",
            "Здоровье", "Здоровье", "Отношения", "Работа", "Хобби"};
    private int[] colorArray = {0xFF93E92B, 0xFF93E92B, 0xFFFF7474, 0, 0xFFFD8908,
            0xFF93E92B, 0xFF93E92B, 0xFFFF7474, 0, 0xFFFD8908};
    private boolean[] isCompletedArray = {false, true, true, false, true,
            false, true, true, false, true};

    private static ArrayList<Goal> goalListArray = new ArrayList<>();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.menu_diary);

        // ListView, Adapter, custom Item, AddButton, Db, AddActivity

        goalListView = getActivity().findViewById(R.id.goals_list);
        FloatingActionButton addGoalButton = getActivity().findViewById(R.id.add_goal_button);

        for(int i = 0; i < goalArray.length; i++){
            goalListArray.add(new Goal(goalArray[i], categoryArray[i], isCompletedArray[i], colorArray[i]));
        }

        ArrayAdapter<Goal> adapter = new GoalAdapter(getActivity());

        goalListView.setAdapter(adapter);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_diary, container, false);
    }


    private class Goal{
        public final String goalName;
        public final String categoryName;
        public final boolean isCompleted;
        public final int color;

        Goal(String goalName, String categoryName, boolean isCompleted, int color){
            this.goalName = goalName;
            this.categoryName = categoryName;
            this.isCompleted = isCompleted;
            this.color = color;
        }
    }

    private class GoalAdapter extends ArrayAdapter<Goal> {

        GoalAdapter(Context context){
            super(context, R.layout.custom_goal_item, goalListArray);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            Goal goal = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.custom_goal_item, null);
            }

            ((TextView)convertView.findViewById(R.id.goal_text)).setText(goal.goalName);
            ((TextView)convertView.findViewById(R.id.goal_category)).setText(goal.categoryName);
            ((CheckBox)convertView.findViewById(R.id.goal_completed)).setChecked(goal.isCompleted);
            convertView.findViewById(R.id.goal_background).setBackgroundColor(goal.color);

            return convertView;
        }
    }
}
