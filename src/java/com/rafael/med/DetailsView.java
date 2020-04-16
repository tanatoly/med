package com.rafael.med;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.jfoenix.controls.JFXTabPane;
import com.rafael.med.common.Constants;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
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
			
			setBackground(Constants.BACKGOUND_50);
			
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
					if(param.isWarning.get())
					{
						rowText.setColor(Color.RED);
					}
					else
					{
						rowText.setColor(Color.WHITE);
					}
					if(isDeviceNotTransmit)
					{
						rowText.setColor(Constants.COLOR_80);
					}
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
		
		private LineChart<Number, Number> chart;
		private XYChart.Series<Number, Number> hourDataSeries;
		private XYChart.Series<Number, Number> minuteDataSeries;
		private NumberAxis xAxis;
		
		private double hours = 0;
		private double minutes = 0;
		private double timeInHours = 0;
		private double prevY = 10;
		private double y = 10;
		
		public ChartFragment()
		{
			setBackground(Constants.BACKGOUND_70);
			
	        
	        xAxis = new NumberAxis(0, 24, 3);
	        final NumberAxis yAxis = new NumberAxis(0, 100, 10);
	        chart = new LineChart<>(xAxis, yAxis);
	        // setup chart
	       // chart.getStylesheets().add(StockLineChartApp.class.getResource("StockLineChart.css").toExternalForm());
	        chart.setCreateSymbols(false);
	        chart.setAnimated(false);
	        chart.setLegendVisible(false);
	        chart.setTitle("Kuku");
	        xAxis.setLabel("Time");
	        xAxis.setForceZeroInRange(false);
	        yAxis.setLabel("Share Price");
	        yAxis
	                .setTickLabelFormatter(new NumberAxis.DefaultFormatter(yAxis, "$", null));
	        // add starting data
	        hourDataSeries = new XYChart.Series<>();
	        hourDataSeries.setName("Hourly Data");
	        minuteDataSeries = new XYChart.Series<>();
	        minuteDataSeries.setName("Minute Data");
	        // create some starting data
	        hourDataSeries.getData()
	                .add(new XYChart.Data<Number, Number>(timeInHours, prevY));
	        minuteDataSeries.getData()
	                .add(new XYChart.Data<Number, Number>(timeInHours, prevY));
	        for (double m = 0; m < (60); m++)
	        {
	            nextTime();
	            plotTime();
	        }
	        chart.getData().add(minuteDataSeries);
	        chart.getData().add(hourDataSeries);
	        
	        setCenter(chart);
			
		}
		
		private void nextTime() {
	        if (minutes == 59) {
	            hours++;
	            minutes = 0;
	        } else {
	            minutes++;
	        }
	        timeInHours = hours + ((1d / 60d) * minutes);
	    }
	 
	    private void plotTime() {
	        if ((timeInHours % 1) == 0) {
	            // change of hour
	            double oldY = y;
	            y = prevY - 10 + (Math.random() * 20);
	            prevY = oldY;
	            while (y < 10 || y > 90) {
	                y = y - 10 + (Math.random() * 20);
	            }
	            hourDataSeries.getData()
	                    .add(new XYChart.Data<Number, Number>(timeInHours, prevY));
	            // after 25hours delete old data
	            if (timeInHours > 25) {
	                hourDataSeries.getData().remove(0);
	            }
	            // every hour after 24 move range 1 hour
	            if (timeInHours > 24) {
	                xAxis.setLowerBound(xAxis.getLowerBound() + 1);
	                xAxis.setUpperBound(xAxis.getUpperBound() + 1);
	            }
	        }
	        double min = (timeInHours % 1);
	        double randomPickVariance = Math.random();
	        if (randomPickVariance < 0.3) {
	            double minY = prevY + ((y - prevY) * min) - 4 + (Math.random() * 8);
	            minuteDataSeries.getData().add(new XYChart.Data<Number, Number>(timeInHours, minY));
	        } else if (randomPickVariance < 0.7) {
	            double minY = prevY + ((y - prevY) * min) - 6 + (Math.random() * 12);
	            minuteDataSeries.getData().add(new XYChart.Data<Number, Number>(timeInHours, minY));
	        } else if (randomPickVariance < 0.95) {
	            double minY = prevY + ((y - prevY) * min) - 10 + (Math.random() * 20);
	            minuteDataSeries.getData().add(new XYChart.Data<Number, Number>(timeInHours, minY));
	        } else {
	            double minY = prevY + ((y - prevY) * min) - 15 + (Math.random() * 30);
	            minuteDataSeries.getData().add(new XYChart.Data<Number, Number>(timeInHours, minY));
	        }
	        // after 25hours delete old data
	        if (timeInHours > 25) {
	            minuteDataSeries.getData().remove(0);
	        }
	    }
	    
	}
	
	
/******************************************** 	DeviceTab ***********************************/	
	
	public static final class DeviceTab extends ScrollPane
	{
		private Device device;
		private Timeline chartTimeline;
		private ChartFragment[] charts = new ChartFragment[3];
		private DeviceFragment deviceFragment;
		
		public DeviceTab(Device prototype)
		{
			setFitToHeight(true);
			setFitToWidth(true);
			
			BorderPane content = new BorderPane();
			setContent(content);
			
			deviceFragment = new DeviceFragment(prototype,30);
			content.setLeft(deviceFragment);
			deviceFragment.minWidthProperty().bind(widthProperty().divide(3));
			
			BorderPane.setMargin(deviceFragment, new Insets(4));
			
			
			GridPane center = new GridPane();
			//center.setGridLinesVisible(true);
			
			for (int i = 0; i < charts.length; i++) 
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
			
			for (int i = 0; i < charts.length; i++) 
			{
				charts[i] = new ChartFragment();
				GridPane.setMargin(charts[i], new Insets(4));
				center.add(charts[i], 0, i);
			}
			
			content.setCenter(center);
			
			chartTimeline = new Timeline();
			chartTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(1000 / 60), (ActionEvent actionEvent) -> 
			{
				// 6 minutes data per frame
				
				for (int i = 0; i < charts.length; i++) 
				{
					for (int count = 0; count < 6; count++) 
					{
						charts[i].nextTime();
						charts[i].plotTime();
					}
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
		
//		//DELETE
//		
//		Device prototype = MedManager.INSTANCE.data.allDevices.get(2);
//		Device device = new Device(prototype, "123456");
//		bed.devices.put("123456", device);
//		
//		prototype = MedManager.INSTANCE.data.allDevices.get(100);
//		device = new Device(prototype, "987654");
//		bed.devices.put("987654", device);
//		
//		//DELETE
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
