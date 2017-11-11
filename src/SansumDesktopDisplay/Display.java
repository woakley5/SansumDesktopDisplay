package SansumDesktopDisplay;

import com.backendless.Backendless;
import com.backendless.IDataStore;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import javafx.application.Platform;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

import java.util.*;

public class Display {

    public Label waitLabel;
    public Label avgWaitLabel;
    public LineChart graph;

    public Display(){
        Timer updateTimer = new Timer();
        TimerTask updateTask = new TimerTask() {
            @Override
            public void run() {
                updateTime();
            }
        };
        updateTimer.schedule(updateTask, 1000, 60000);
    }

    public void updateTime(){
        System.out.println("Updating");
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setSortBy("created DESC");
        Backendless.Persistence.of( "Times" ).find(queryBuilder, new AsyncCallback<List<Map>>(){
            @Override
            public void handleResponse( List<Map> foundTimes )
            {
                ArrayList<Integer> times = new ArrayList<>();
                Integer average = 0;

                for(int x = 0; x < foundTimes.size(); x++){
                    Integer time = (Integer) foundTimes.get(x).get("Time");
                    times.add(time);
                    average += time;
                }
                average /= foundTimes.size();
                updateWaitLabel(String.valueOf(times.get(0)));
                updateAverageLabel(String.valueOf(average));
                updateGraph(times);
            }
            @Override
            public void handleFault( BackendlessFault fault )
            {
                System.out.println("Error");
            }
        });
    }

    public void updateWaitLabel(String time){
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                waitLabel.setText(time);
            }
        });


    }

    public void updateAverageLabel(String time){
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                avgWaitLabel.setText(time);
            }
        });
    }

    public void updateGraph(ArrayList<Integer> times){
        Platform.runLater(new Runnable(){
            @Override
            public void run() {

                graph.getData().clear();

                XYChart.Series series = new XYChart.Series();

                series.setName("Recent Times");

                for(int x = 0; x < times.size(); x++){
                    series.getData().add(new XYChart.Data(x + 1, times.get(times.size() - 1 - x)));

                }

                graph.getData().add(series);
            }
        });
    }

}
