package com.rafael.med.examples;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import javafx.util.Duration;
 
 
/**
 * A live scatter chart.
 */
public class LiveScatterChartApp extends Application {
 
    private ScatterChart.Series<Number,Number> series;
    private double nextX = 0;
    private SequentialTransition animation;
 
    public LiveScatterChartApp() {
        // create animation
        Timeline timeline1 = new Timeline();
        timeline1.getKeyFrames().add( new KeyFrame(Duration.millis(20), (ActionEvent actionEvent) -> 
        {
        	series.getData().add(new XYChart.Data<Number, Number>( nextX, Math.sin(Math.toRadians(nextX)) * 100));
            nextX += 10;
        })
        );
        timeline1.setCycleCount(200);
        
        Timeline timeline2 = new Timeline();
        timeline2.getKeyFrames().add( new KeyFrame(Duration.millis(50), (ActionEvent actionEvent) -> 
        {
        	series.getData().add(new XYChart.Data<Number, Number>( nextX, Math.sin(Math.toRadians(nextX)) * 100 ));
        	if (series.getData().size() > 54)
        	{
        		series.getData().remove(0);
        	}
        	nextX += 10;
        	if(nextX > 1000)
        	{
        		nextX = 0;
        	}
        })
        );
        timeline2.setCycleCount(Animation.INDEFINITE);
        animation = new SequentialTransition();
       // animation.getChildren().add(timeline1);
        animation.getChildren().add(timeline2);
    }
 
    public Parent createContent() 
    {
        final NumberAxis xAxis = new NumberAxis();
        xAxis.setForceZeroInRange(false);
        final NumberAxis yAxis = new NumberAxis(-100, 100, 10);
        final ScatterChart<Number, Number> sc = new ScatterChart<>(xAxis, yAxis);
        // setup chart
        sc.getStylesheets().add(LiveScatterChartApp.class.getResource("LiveScatterChart.css").toExternalForm());
        sc.setTitle("Animated Sine Wave ScatterChart");
        xAxis.setLabel("X Axis");
        xAxis.setAnimated(false);
        yAxis.setLabel("Y Axis");
        yAxis.setAutoRanging(false);
        // add starting data
        series = new ScatterChart.Series<>();
        series.setName("Sine Wave");
        series.getData().add(new ScatterChart.Data<Number, Number>(5d, 5d));
        sc.getData().add(series);
        return sc;
    }
 
    public void play() {
        animation.play();
    }
 
    @Override public void stop() {
        animation.pause();
    }
 
    @Override public void start(Stage primaryStage) throws Exception {
        primaryStage.setScene(new Scene(createContent()));
        primaryStage.show();
        play();
    }
 
    /**
     * Java main for when running without JavaFX launcher
     */
    public static void main(String[] args) {
        launch(args);
    }
}