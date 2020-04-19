package com.rafael.med.examples;
import java.util.concurrent.atomic.AtomicInteger;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.util.Duration;

public class App extends Application 
{
  final int WINDOW_SIZE = 10_000;
  final int XSTEP = 100;
  
  double nextX = 0;
  double nextY = 0;
  double deltaY = 0;

  public static void main(String[] args) { launch(args); }

  @Override
  public void start(Stage primaryStage) throws Exception 
  {
    primaryStage.setTitle("JavaFX Realtime Chart Demo");
    
    
    StackPane stackPane = new StackPane();

    // defining the axes
    final NumberAxis xAxis = new NumberAxis("Time", 0, WINDOW_SIZE, XSTEP); 
    final NumberAxis yAxis = new NumberAxis("Value",-1, 1, 0.1);
    xAxis.setLabel("x");
    xAxis.setAnimated(false); // axis animations are removed
   
    yAxis.setLabel("y");
    yAxis.setAnimated(false); // axis animations are removed

    
  final LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
 //   ScatterChart<Number, Number> lineChart = new ScatterChart<>(xAxis, yAxis);
    lineChart.setTitle("Realtime JavaFX Charts");
    lineChart.setAnimated(true); // disable animations

    // defining a series to display data
    XYChart.Series<Number, Number> series = new XYChart.Series<>();
    series.setName("Data Series");

    // add series to chart
   // lineChart.getData().add(series);
    
   // styleString.append("-fx-stroke: blue; -fx-stroke-width: 2; ");
  lineChart.setCreateSymbols(false);
   

  XYChart.Series<Number, Number> seriesP = new XYChart.Series<>();
 
  seriesP.setName("TOCHKa");
 // seriesP.setStyle("-fx-stroke: yellow;");
  
  lineChart.getData().addAll(series,seriesP);
  
  
 
  
    // setup scene
  	StackPane upper = new StackPane();
  	Background BACKGOUND_95 			= new Background(new BackgroundFill(Color.rgb(0, 0, 0, 0.5), CornerRadii.EMPTY, Insets.EMPTY));
  	upper.setBackground(BACKGOUND_95);
  	upper.setOnMouseMoved(new EventHandler<javafx.scene.input.MouseEvent>() {

		@Override
		public void handle(javafx.scene.input.MouseEvent event) {
			
			System.out.println("------------------ x = " + event.getX() + " y = " + event.getY());
			
			
		}
	});
  	
  	
  	
  	//stackPane.getChildren().addAll(lineChart,upper);
  	
    Scene scene = new Scene(lineChart, 800, 600);
    primaryStage.setScene(scene);

    // show the stage
    primaryStage.show();

   

    // put dummy data onto graph per second
    
    
    int index = 0;
    ObservableList<Data<Number, Number>> data = series.getData();
	for (int i = 0; i < WINDOW_SIZE; i = i + XSTEP)
    {
    	data.add(index,new XYChart.Data<>(i, 0 ));
    	index++;
	}
    

	
	
	 // find chart area Node
    
    
    // remember scene position of chart area
//    yShift = chartAreaBounds.getMinY();
//    // set x parameters of the valueMarker to chart area bounds
//    valueMarker.setStartX(chartAreaBounds.getMinX());
//    valueMarker.setEndX(chartAreaBounds.getMaxX());
	
	
	final int sum = index;
    
    final AtomicInteger count = new AtomicInteger();
    Timeline timeline1 = new Timeline();
    lineChart.setVerticalGridLinesVisible(true);
    
    Line l = new Line(0, 0, 0, 0);
    
  Region chartArea = (Region) lineChart.lookup(".chart-plot-background");
  Bounds chartAreaBounds = chartArea.localToScene(chartArea.getBoundsInLocal());
  double xmax = chartAreaBounds.getMaxX();
  double ymax = chartAreaBounds.getMaxY();
  
  double xmin = chartAreaBounds.getMinX();
  double ymin = chartAreaBounds.getMinY();
  
  
  System.out.println("xmin = " + xmin + " ymin = " + ymin + " xmax =  " + xmax + " ymax =  " + ymax);
//  
  
 
  
//  
//  
//  upper.getChildren().add(x);
  AtomicInteger k = new AtomicInteger();
   AtomicInteger s = new AtomicInteger();
    timeline1.getKeyFrames().add( new KeyFrame(Duration.millis(XSTEP), (ActionEvent actionEvent) -> 
    {
    	
    	double y = Math.sin(Math.toRadians(s.getAndAdd(10)));
    	
    	 Circle x = new Circle(10,Color.AQUA);
    	Data<Number, Number> current = data.get(count.getAndIncrement());
    	current.setYValue(y);
    	current.setNode(x);
    	
    	
    	
    	seriesP.getData().clear();
    	for (float i = 0; i < y; i = (float) (i + 0.1)) 
    	{
    		seriesP.getData().add(new Data<Number, Number>(current.getXValue(), i));
		}
    	

    	//System.out.println("x = " + current.getXValue() + " y = " + current.getYValue());
        if(count.compareAndSet(sum, 0))
        {
        	Node lookup = series.getNode().lookup(".chart-series-line");
        	lookup.setStyle("-fx-stroke: blue;");
        }
        

         
    }));
    timeline1.setCycleCount(Animation.INDEFINITE);
    timeline1.setDelay(Duration.millis(100));
    timeline1.play();
    
    
    
 
  
  }
  
}