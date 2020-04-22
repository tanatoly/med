package com.rafael.med.examples;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.stage.Stage;
import javafx.util.Duration;

public class App extends Application 
{
	
	public static final class XY
	{
		public double x;
		public double y;
		
		public XY()
		{
			this.x = 0;
			this.y = 0;
		}
	}
	
	static final double MIN_X 		= 0;
	static final double MAX_X 		= 10_000;
	static final double STEP_X 		= 100;
	static final int POINTS_X		= (int) ((MAX_X - MIN_X) / STEP_X);


	static final double MIN_Y 		= -1;
	static final double MAX_Y 		= 1;
	static final double STEP_Y 		= 0.1;
	static final int POINTS_Y		= (int) ((MAX_X - MIN_X) / STEP_X);



	double nextX = 0;
	double nextY = 0;
	private XY[] points;
	private ObservableList<Data<Number, Number>> fromList;
	private ObservableList<Data<Number, Number>> toList;
	
	
	

	public static void main(String[] args) { launch(args); }

	@Override
	public void start(Stage primaryStage) throws Exception 
	{

		NumberAxis xAxis = new NumberAxis("Time", MIN_X, MAX_X, STEP_X); 
		NumberAxis yAxis = new NumberAxis("Value",MIN_Y, MAX_Y, STEP_Y);
		xAxis.setAnimated(false);
		yAxis.setAnimated(false);


		LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
		lineChart.setTitle("Realtime JavaFX Charts");
		lineChart.setAnimated(false); // disable animations
		lineChart.setCreateSymbols(false);
		lineChart.setLegendVisible(false);

		XYChart.Series<Number, Number> seriesMain_1 = new XYChart.Series<>();
		ObservableList<Data<Number, Number>> dataMain_1 = seriesMain_1.getData();
//		for (double i = MIN_X; i < MAX_X; i+=STEP_X) 
//		{
//			dataMain_1.add(new XYChart.Data<>(i, 0 ));
//		}
		
		XYChart.Series<Number, Number> seriesMain_2 = new XYChart.Series<>();
		ObservableList<Data<Number, Number>> dataMain_2 = seriesMain_2.getData();
		
		
		XYChart.Series<Number, Number> seriesLine = new XYChart.Series<>();
		ObservableList<Data<Number, Number>> dataLine = seriesLine.getData();
		//seriesLine.nodeProperty().get().setStyle("\"-fx-stroke: blue;-fx-stroke-width: 2px;");
//		for (double i = MIN_Y; i < MAX_Y; i+=STEP_Y) 
//		{
//			dataLine.add(new XYChart.Data<>(0, i ));
//		}
		
		points = new XY[3];
		for (int i = 0; i < points.length; i++) 
		{
			dataLine.add(new XYChart.Data<>(0, 0 ));
			points[i] = new XY();
		}
		
		
		lineChart.getData().addAll(seriesMain_1, seriesMain_2);
		
		Timeline timeline1 = new Timeline();
		
		
		AtomicInteger count = new AtomicInteger();
		
		AtomicInteger randomY = new AtomicInteger();
		
		nextX = 0;
		
		AtomicInteger pintCount = new AtomicInteger(0);
		AtomicBoolean isFirst = new AtomicBoolean();
		
		
		AtomicBoolean isChange = new AtomicBoolean();
		
		
		fromList = dataMain_1;
		toList = dataMain_2;
		
		timeline1.getKeyFrames().add( new KeyFrame(Duration.millis(STEP_X), (ActionEvent actionEvent) -> 
		{
			
			if(isFirst.compareAndSet(false, true))
			{
				seriesMain_1.getNode().lookup(".chart-series-line").setStyle("-fx-stroke: green;-fx-stroke-width: 2px;");
				seriesMain_2.getNode().lookup(".chart-series-line").setStyle("-fx-stroke: blue;-fx-stroke-width: 2px;");
				//seriesLine.getNode().lookup(".chart-series-line").setStyle("-fx-stroke: black;-fx-stroke-width: 3px;");
			}
			double y = Math.sin(Math.toRadians(randomY.getAndAdd(10)));
			
			
			
				Data<Number, Number> data = fromList.remove(0);
				data.setYValue(y);
				toList.add(data);
			
			
			
//			Data<Number, Number> currentMain = dataMain.get(count.getAndIncrement());
//			currentMain.setYValue(y);
//			
//			
//			
//			int andIncrement = pintCount.getAndIncrement();
//			points[andIncrement].x = currentMain.getXValue().doubleValue();
//			points[andIncrement].y = currentMain.getYValue().doubleValue();
//			
//			pintCount.compareAndSet(points.length, 0);
//			
//			int cc = 0;
//			for (Data<Number, Number> currentLine : dataLine)
//			{
//				currentLine.setXValue(points[cc].x);
//				currentLine.setYValue(points[cc].y);
//				cc++;
//			}
						
			nextX = nextX + STEP_X;
			if(nextX >= MAX_X)
			{
				nextX = 0;
				
				if(fromList == dataMain_1)
				{
					fromList = dataMain_2;
					toList = dataMain_1;
					
				}
				else
				{
					fromList = dataMain_1;
					toList = dataMain_2;
				}
				
				
//				count.set(0);
//				for (int i = 0; i < points.length; i++) 
//				{
//					points[i].x = 0;
//					points[i].y = 0;
//				}
			}
		}));
		timeline1.setCycleCount(Animation.INDEFINITE);
		timeline1.setDelay(Duration.millis(STEP_X));
		//timeline1.play();
		
		
		

		

		Scene scene = new Scene(lineChart, 800, 600);
		primaryStage.setScene(scene);
		primaryStage.show();

	}

}