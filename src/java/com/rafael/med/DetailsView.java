package com.rafael.med;

import java.awt.Toolkit;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;

import com.jfoenix.controls.JFXTabPane;
import com.rafael.med.common.Constants;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class DetailsView extends JFXTabPane implements CenterView
{
	
	/******************************************** 	DeviceFragment ***********************************/	
	public static final class DeviceFragment extends GridPane
	{
		
		private RowText[] labels;
		private Text serial;
		
		public DeviceFragment(Device prototype, int rows)
		{
			
			setBackground(Constants.BACKGOUND_35);
			
//			setGridLinesVisible(true);
			
			setPadding(new Insets(4));
//			this.setMaxHeight(30 * rows);
//			this.setMinHeight(30 * rows);
			
			double rowPercent = 100/ rows+1;
			
			RowConstraints r = new RowConstraints();
			r.setPercentHeight(rowPercent);
			for (int i = 0; i <= rows; i++) 
			{
				getRowConstraints().add(r);
			}
			
			
			ColumnConstraints c1 = new ColumnConstraints();
			c1.setPercentWidth(23);
			ColumnConstraints c2 = new ColumnConstraints();
			c2.setPercentWidth(18);
			
			getColumnConstraints().addAll(c1, c2,c2,c2, c1);
			GridPane.setMargin(this, new Insets(2));
			
			serial = new Text();
			serial.setFill(Color.BLACK);
			serial.setFont(Font.font(22));
			GridPane.setConstraints(serial, 				0, 0, 5, 1, HPos.CENTER, VPos.CENTER);
			getChildren().add(serial);
			
			labels = new RowText[rows];
			
			for (int i = 0; i < labels.length; i++) 
			{	
				labels[i] = new RowText(18);
				GridPane.setConstraints(labels[i].name, 				0, i + 1, 1, 1, HPos.LEFT, VPos.CENTER);
				GridPane.setConstraints(labels[i].value, 				1, i + 1, 1, 1, HPos.LEFT, VPos.CENTER);
				GridPane.setConstraints(labels[i].units, 				2, i + 1, 1, 1, HPos.LEFT, VPos.CENTER);
				GridPane.setConstraints(labels[i].defaultValue, 		3, i + 1, 1, 1, HPos.LEFT, VPos.CENTER);
				GridPane.setConstraints(labels[i].range, 				4, i + 1, 1, 1, HPos.LEFT, VPos.CENTER);

				getChildren().addAll(labels[i].name, labels[i].value, labels[i].units, labels[i].defaultValue, labels[i].range );
			}
		}

		public void update(Device device) 
		{
			boolean isDeviceNotTransmit = false;
			long delta = System.currentTimeMillis() - device.lastMessageTime;
			isDeviceNotTransmit = delta > 10_000; // 10 seconds
			
			serial.setText("SERIAL :" + device.serial);
			
			int count = 0;
			for (Param param : device.params.values())
			{
				if(param != null && count < labels.length)
				{
					RowText rowText = labels[count];
					rowText.name.setText(param.name);
					rowText.value.setText(param.getValue());
					rowText.units.setText(param.units);
					rowText.defaultValue.setText(param.getDefaultValue());	
					rowText.range.setText(param.getRange());
					rowText.setColor(param.getColor(isDeviceNotTransmit));
				}
				count++;
			}
			
			for (Mfl mfl : device.mfls.values())
			{
				if(mfl != null && count < labels.length)
				{
					RowText rowText = labels[count];
					rowText.name.setText("MFL");
					rowText.value.setText(mfl.name);
					rowText.units.setText(StringUtils.EMPTY);
					
					if(mfl.value == 1)
					{
						rowText.setColor(Color.RED);
					}
					else
					{
						rowText.setColor(Color.GREEN);
					}
					if(isDeviceNotTransmit)
					{
						rowText.setColor(Constants.COLOR_80);
					}
				}
				count++;
			}
		}
	}
	
/******************************************** 	ChartFragment ***********************************/		
	public static final class ChartFragment extends BorderPane
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
		
		private static final double MIN_X 		= 0;
		private static final double MAX_X 		= 10_000;
		private static final double STEP_X 		= 50;
		
		private static final double MIN_Y 		= -1;
		private static final double MAX_Y 		= 1;
		private static final double STEP_Y 		= 0.1;
		

		

		private AtomicBoolean isFirst = new AtomicBoolean();;
		private Series<Number, Number> seriesMain_1;
		private Series<Number, Number> seriesMain_2;
		private ObservableList<Data<Number, Number>> dataMain_1;
		private ObservableList<Data<Number, Number>> dataMain_2;
		
		private ObservableList<Data<Number, Number>> fromList;
		private ObservableList<Data<Number, Number>> toList;
		
		private XY[] points;		
		private Series<Number, Number> seriesLine;
		private ObservableList<Data<Number, Number>> dataLine;
		
		
		private double nextX = 0;
		private AtomicInteger pointCounter = new AtomicInteger(0);
		
		private AtomicInteger randomY  		= new AtomicInteger();
		public int randomDelat;
		
		public ChartFragment(Chart chart)
		{
			setBackground(Constants.BACKGOUND_35);
			
	        
			NumberAxis xAxis = new NumberAxis(); 
			NumberAxis yAxis = new NumberAxis();
			xAxis.setLabel("x");
			xAxis.setAnimated(false);
			yAxis.setLabel("y");
			yAxis.setAnimated(false);
			xAxis.setTickLabelsVisible(false);
			yAxis.setTickLabelsVisible(false);


			LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
			lineChart.setTitle(chart.name);
			lineChart.setAnimated(false);
			lineChart.setCreateSymbols(false);
			lineChart.setLegendVisible(false);
			lineChart.setVerticalGridLinesVisible(true);
			
			
			setCenter(lineChart);

			seriesMain_1 = new XYChart.Series<>();
			dataMain_1 = seriesMain_1.getData();
//			for (double i = MIN_X; i < MAX_X; i+=STEP_X) 
//			{
//				dataMain_1.add(new XYChart.Data<>(i, 0 ));
//			}
			seriesMain_2 = new XYChart.Series<>();
			dataMain_2 = seriesMain_2.getData();
			
			
			seriesLine = new XYChart.Series<>();
			dataLine = seriesLine.getData();
			
			points = new XY[3];
			for (int i = 0; i < points.length; i++) 
			{
				dataLine.add(new XYChart.Data<>(0, 0 ));
				points[i] = new XY();
			}
			
			lineChart.getData().addAll(seriesMain_1, seriesMain_2, seriesLine);
			
			fromList = dataMain_1;
			toList = dataMain_2;
			
			
			chart.isChartCreated.addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> 
			{
				if(newValue)
				{
					System.out.println("------------ CHART CREATED");
					xAxis.setUpperBound(chart.maxX);
					xAxis.setLowerBound(chart.minX);
					xAxis.setTickUnit(chart.stepX);
					
					yAxis.setUpperBound(chart.maxY);
					yAxis.setLowerBound(chart.minY);
					yAxis.setTickUnit(chart.stepY);
					
					for (Data<Number, Number> chartData : chart.data)
					{
						dataMain_1.add(chartData);
					}
				}
			});
		}
		
		public void onTimelineTick()
		{
			if(isFirst.compareAndSet(false, true))
			{
				seriesMain_1.getNode().lookup(".chart-series-line").setStyle("-fx-stroke: white;-fx-stroke-width: 2px;");
				seriesMain_2.getNode().lookup(".chart-series-line").setStyle("-fx-stroke: white;-fx-stroke-width: 2px;");
				seriesLine.getNode().lookup(".chart-series-line").setStyle("-fx-stroke: aqua;-fx-stroke-width: 2px;");
			}
			double y = Math.sin(Math.toRadians(randomY.getAndAdd(randomDelat)));
			
			
			Data<Number, Number> data = fromList.remove(0);
			data.setYValue(y);
			toList.add(data);
			
			int andIncrement = pointCounter.getAndIncrement();
			points[andIncrement].x = data.getXValue().doubleValue();
			points[andIncrement].y = data.getYValue().doubleValue();
			
			pointCounter.compareAndSet(points.length, 0);
			
			int cc = 0;
			for (Data<Number, Number> currentLine : dataLine)
			{
				currentLine.setXValue(points[cc].x);
				currentLine.setYValue(points[cc].y);
				cc++;
			}
						
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

				for (int i = 0; i < points.length; i++) 
				{
					points[i].x = 0;
					points[i].y = 0;
				}
				
				// Toolkit.getDefaultToolkit().beep();
			}
		}
	}
	
	
/******************************************** 	DeviceTab ***********************************/	
	
	public static final class DeviceTab extends ScrollPane
	{
		private Device device;
		private Timeline chartTimeline;
		private DeviceFragment deviceFragment;
		
		public DeviceTab(Device prototype)
		{
			setFitToHeight(true);
			setFitToWidth(true);
			
			BorderPane content = new BorderPane();
			setContent(content);
			
			deviceFragment = new DeviceFragment(prototype,20);
			content.setLeft(deviceFragment);
			deviceFragment.minWidthProperty().bind(widthProperty().divide(3));
			deviceFragment.maxWidthProperty().bind(widthProperty().divide(3));
			
			BorderPane.setMargin(deviceFragment, new Insets(4));
			
			
			GridPane center = new GridPane();
			
			
			
			//center.setGridLinesVisible(true);
			
			Collection<Chart> charts = prototype.charts.values();
			
			for (int i = 0; i < charts.size(); i++) 
			{
				RowConstraints rowConstraints = new RowConstraints();
				rowConstraints.setFillHeight(true);
				rowConstraints.setVgrow(Priority.ALWAYS);
				center.getRowConstraints().add(rowConstraints);
			}
			
			ColumnConstraints columnConstraints = new ColumnConstraints();
			columnConstraints.setFillWidth(true);
			columnConstraints.setHgrow(Priority.ALWAYS);
			center.getColumnConstraints().add(columnConstraints);
			
			int index = 0;
			ChartFragment [] chartFragments = new ChartFragment[charts.size()]; 
			for (Chart chart : charts)
			{
				chartFragments[index] = new ChartFragment(chart);
				chartFragments[index].randomDelat = 10 * index;
				GridPane.setMargin(chartFragments[index], new Insets(4));
				center.add(chartFragments[index], 0, index);
				index++;
			}
			
			content.setCenter(center);
			
			chartTimeline = new Timeline();
			chartTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(100), (ActionEvent actionEvent) -> 
			{
				for (int i = 0; i < chartFragments.length; i++) 
				{
					//chartFragments[i].onTimelineTick();
				}
			}));
			chartTimeline.setCycleCount(Animation.INDEFINITE);
		}

		

		public void stopChart()
		{
			chartTimeline.pause();
			System.out.println( this + " stop");
		}

		public void startChart()
		{
			chartTimeline.play();
			System.out.println( this + " start");
			
		}
		
		public void update()
		{
			deviceFragment.update(device);
			System.out.println( this + " update");
			
		}

		public void setDevice(Device device) 
		{
			this.device = device;
		}
		

		@Override
		public String toString()
		{
			return String.format("DeviceTab [device=%s]", device);
		}
	}
	
	
/******************************************** 	DetailsView ***********************************/
	
	private Bed bed;
	private final Map<Integer, Tab> prototypesDeviceTabs = new HashMap<>();
	
	public DetailsView(MedData data)
	{
		
		for (Device prototype : data.allDevices.values())
		{
			DeviceTab deviceTab = new DeviceTab(prototype);
			Tab tab = new Tab(prototype.name, deviceTab);
			prototypesDeviceTabs.put(prototype.type, tab);	
		}
        setTabMinWidth(200);
        setTabMaxWidth(200);
        
        getSelectionModel().selectedItemProperty().addListener((ChangeListener<Tab>) (observable, oldValue, newValue) ->
        {
        	if(oldValue != null)
        	{
        		DeviceTab deviceTab = (DeviceTab) oldValue.getContent();
        		deviceTab.stopChart();
        	}
        	if(newValue != null )
        	{
        		DeviceTab deviceTab = (DeviceTab) newValue.getContent();
        		deviceTab.startChart();
        	}
		});		
	}
	

	public void setBed(Bed bed) 
	{
		this.bed = bed;
		
		//DELETE
		
		Device prototype = MedManager.INSTANCE.data.allDevices.get(2);
		Device device = new Device(prototype, "123456");
		
		
		
		bed.devices.put("123456", device);
		
		prototype = MedManager.INSTANCE.data.allDevices.get(100);
		device = new Device(prototype, "987654");
		bed.devices.put("987654", device);
		
		//DELETE
	}

	@Override
	public void update(boolean isToFront)
	{
		if(bed != null)
		{
			if(isToFront)
			{
				getTabs().clear();
				
				for (Device device : bed.devices.values())
				{
					Tab tab = prototypesDeviceTabs.get(device.type);
					if(tab != null)
					{
						DeviceTab deviceTab = (DeviceTab) tab.getContent();
						deviceTab.setDevice(device);
						getTabs().add(tab);
					}
					
				}
				if(getTabs().size() > 0)
				{
					getSelectionModel().select(0);
					Tab currentTab = getSelectionModel().getSelectedItem();
					if(currentTab != null)
					{
						DeviceTab deviceTab = (DeviceTab) currentTab.getContent();
						deviceTab.update();
					}
				}
			}
			
			else
			{
				Tab currentTab = getSelectionModel().getSelectedItem();
				if(currentTab != null)
				{
					DeviceTab deviceTab = (DeviceTab) currentTab.getContent();
					deviceTab.update();
				}
			}
		}
	}

	@Override
	public void onToBack()
	{
		for (Tab tab : getTabs())
		{
			DeviceTab deviceTab = (DeviceTab) tab.getContent();
			deviceTab.stopChart();
		}
	}
}
