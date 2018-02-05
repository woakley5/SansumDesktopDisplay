package SansumDesktopDisplay;

import com.backendless.Backendless;
import com.backendless.IDataStore;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.util.Duration;

import java.lang.reflect.Array;
import java.util.*;

public class Display {


    public Label timeLabel;
    public Label messageLabel;

    public int messageNumber = 0;
    public ArrayList<String> messages = new ArrayList<>();

    public Timer messageTimer = new Timer();

    public Display(){
        Timer updateTimer = new Timer();
        TimerTask updateTask = new TimerTask() {
            @Override
            public void run() {
                updateTime();
            }
        };
        updateTimer.schedule(updateTask, 1000, 60000);
        queryForMessages();
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

                for(int x = 0; x < foundTimes.size(); x++){
                    Integer time = (Integer) foundTimes.get(x).get("Time");
                    times.add(time);
                }

                updateWaitLabel(String.valueOf(times.get(0)));
            }
            @Override
            public void handleFault( BackendlessFault fault )
            {
                System.out.println("Error");
            }
        });
    }

    public void updateMessage(){
        Platform.runLater(
                () -> {
                    if(messageNumber < messages.size()){
                        TranslateTransition moveOut = new TranslateTransition(Duration.millis(1500), messageLabel);
                        moveOut.setToY(150);
                        moveOut.play();
                        moveOut.setOnFinished(new EventHandler<ActionEvent>() {

                            @Override
                            public void handle(ActionEvent event) {
                                messageLabel.setText(messages.get(messageNumber));
                                messageNumber++;
                                TranslateTransition moveIn = new TranslateTransition(Duration.millis(1500), messageLabel);
                                moveIn.setToY(0);
                                moveIn.play();
                            }
                        });
                    }
                    else{
                        messageNumber = 0;
                        messageTimer.cancel();
                        messageTimer.purge();
                        queryForMessages();
                    }
                }
        );
    }

    public void queryForMessages(){
        System.out.println("Updating Messages");
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        Backendless.Persistence.of( "Messages" ).find(queryBuilder, new AsyncCallback<List<Map>>(){
            @Override
            public void handleResponse( List<Map> foundMessages )
            {
                messages.clear();
                for(int x = 0; x < foundMessages.size(); x++){
                    messages.add((String)foundMessages.get(x).get("Message"));
                }

                scheduleMessageTimer();

            }
            @Override
            public void handleFault( BackendlessFault fault )
            {
                System.out.println("Error");
            }
        });
    }

    public void scheduleMessageTimer(){
        Platform.runLater(
                () -> {
                    TimerTask updateTask = new TimerTask() {
                        @Override
                        public void run() {
                            updateMessage();
                        }
                    };
                    messageTimer = new Timer();
                    messageTimer.schedule(updateTask, 1000, 30000);
                }
        );
    }

    public void updateWaitLabel(String time){
        Platform.runLater(
                () -> {
                    FadeTransition ft = new FadeTransition(Duration.millis(1000), timeLabel);
                    ft.setFromValue(1.0);
                    ft.setToValue(0.0);
                    ft.setCycleCount(1);
                    ft.setAutoReverse(false);
                    ft.play();
                    ft.setOnFinished(new EventHandler<ActionEvent>() {

                        @Override
                        public void handle(ActionEvent event) {
                            System.out.println("Time: " + time);
                            int hours = Integer.parseInt(time) / 60;
                            int minutes = Integer.parseInt(time) % 60;
                            System.out.println("Hours: " + hours);
                            System.out.println("Mins: " + minutes);
                            String hourText;
                            String minuteText;

                            if(hours == 0){
                                hourText = "";
                            }
                            else if(hours == 1){
                                hourText = hours + " hour";
                            }
                            else{
                                hourText = hours + " hours";
                            }

                            if(minutes == 0){
                                minuteText = "";
                            }
                            else if(minutes == 1){
                                minuteText = " and\n" +  minutes + " minutes";
                            }
                            else{
                                minuteText = " and\n" + minutes + " minutes";
                            }
                            timeLabel.setText(hourText + minuteText);
                            FadeTransition ft2 = new FadeTransition(Duration.millis(1000), timeLabel);
                            ft2.setFromValue(0.0);
                            ft2.setToValue(1.0);
                            ft2.setCycleCount(1);
                            ft2.setAutoReverse(false);
                            ft2.play();
                        }
                    });
                }
        );


    }
}
