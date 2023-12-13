package com.example.sumup;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.sumup.databinding.NotificationsBinding;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class notifications extends Fragment {
    private NotificationsBinding binding;
    ArrayList<String> stringArray = new ArrayList<String>();

    TextView check;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = NotificationsBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences sp = getActivity().getSharedPreferences("userpref", Context.MODE_PRIVATE);
        String name = sp.getString("name","");

        check= getView().findViewById(R.id.check);

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                //Starting Write and Read data with URL
                //Creating array for parameters
                String[] field = new String[1];
                field[0] = "username";

                //Creating array for data
                String[] data = new String[1];
                data[0] =   name;


                PutData putData = new PutData("https://running-wolf.co.za/android/checkExpenses.php", "POST", field, data);
                if (putData.startPut()) {
                    if (putData.onComplete()) {
                        String result = putData.getResult();
                        if (result.equals("expenses found!")){


                            PutData newdata = new PutData("https://running-wolf.co.za/android/grab_expenses.php", "POST", field, data);
                            newdata.startPut();
                            newdata.onComplete();
                            String result2 =newdata.getResult();
                           try {

                                jsonStringToArray(result2).get(0);
                                populateGraphs(stringArray);

                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }



                        }

                    }
                }

            }
        });





            binding.settings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    NavHostFragment.findNavController(notifications.this)
                            .navigate(R.id.action_notifications2_to_setting2);
                }
            });
            binding.home.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    NavHostFragment.findNavController(notifications.this)
                            .navigate(R.id.action_notifications2_to_home3);
                }

            });
            binding.savings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    NavHostFragment.findNavController(notifications.this)
                            .navigate(R.id.action_notifications2_to_myprofile2);
                }

            });
            binding.stats.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    NavHostFragment.findNavController(notifications.this)
                            .navigate(R.id.action_notifications2_to_stats2);
                }

            });
        }
    ArrayList<String> jsonStringToArray(String str) throws JSONException {


        StringBuilder sb = new StringBuilder();


        JSONArray jsonArray = new JSONArray(str);
        String format = null;
        for (int i = 0; i < jsonArray.length(); i++) {
            for (int j = 0; j < jsonArray.getString(i).length(); j++) {
                if ((jsonArray.getString(i).charAt(j)>= '0' && jsonArray.getString(i).charAt(j) <= '9')
                        || (jsonArray.getString(i).charAt(j) >= 'A' && jsonArray.getString(i).charAt(j) <= 'z'
                        || (jsonArray.getString(i).charAt(j) == ':' || jsonArray.getString(i).charAt(j) == ']'))){
                    format =  sb.append(jsonArray.getString(i).charAt(j)).toString();
                }
                
            }

            stringArray.add(format);


        }

        return stringArray;
    }
    public void populateGraphs(ArrayList<String> str) {
        ArrayList<String> costArray = new ArrayList<String>();
        ArrayList<String> monthArray = new ArrayList<String>();
        for (int j = 0; j < str.size(); j++) {
            for (int i = 0; i < str.get(j).length(); i++) {

                int startCos = str.get(j).indexOf("_");
                int startDate = str.get(j).indexOf("*");

                 costArray.add(str.get(i).substring(startCos+5,startDate));



                // monthArray.add(str.get(i).substring(startDate+10,str.get(i).length()-1));
            } Toast toast = Toast.makeText(getActivity(), costArray.get(0), Toast.LENGTH_LONG);
            toast.show();
            check.setText(str.get(0));
            GraphView graphView = getView().findViewById(R.id.graph);
            // on below line we are adding data to our graph view.
            LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[]{
                    // on below line we are adding
                    // each point on our x and y axis.
                    new DataPoint(0, 1),
                    new DataPoint(1, 3),
                    new DataPoint(2, 4),
                    new DataPoint(3, 9),
                    new DataPoint(4, 6),
                    new DataPoint(5, 3),
                    new DataPoint(6, 6),
                    new DataPoint(7, 1),
                    new DataPoint(8, 2)
            });

            // after adding data to our line graph series.
            // on below line we are setting
            // title for our graph view.
            graphView.setTitle(costArray.get(0));

            // on below line we are setting
            // text color to our graph view.
            graphView.setTitleColor(R.color.purple_200);

            // on below line we are setting
            // our title text size.
            graphView.setTitleTextSize(18);

            // on below line we are adding
            // data series to our graph view.
            graphView.addSeries(series);

        }

    }

    }
