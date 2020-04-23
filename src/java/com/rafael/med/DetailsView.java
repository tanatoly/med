package com.rafael.med;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;

import com.jfoenix.controls.JFXTabPane;
import com.rafael.med.common.Constants;

import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.chart.AreaChart;
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
			
			serial.setText(device.serial);
			
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
		private Chart chart;
		
		private AtomicBoolean isCreated = new AtomicBoolean(false);
		public int modulus = 1;
		private double previous = 0;
		private AreaChart<Number, Number> lineChart;
		
		public ChartFragment(Chart chart)
		{
			this.chart = chart;
			setBackground(Constants.BACKGOUND_35);
		}
		
		@SuppressWarnings("unchecked")
		public void createChart()
		{
			NumberAxis xAxis = new NumberAxis(chart.labelX, chart.minX, chart.maxX, chart.stepX); 
			NumberAxis yAxis = new NumberAxis(chart.labelY, chart.minY, chart.maxY, chart.stepY);
			
			xAxis.setAnimated(false);
			yAxis.setAnimated(false);

			lineChart = new AreaChart<>(xAxis, yAxis);
			lineChart.setTitle(chart.getName());
			lineChart.setAnimated(false);
			lineChart.setCreateSymbols(false);
			lineChart.setLegendVisible(false);
			
			setCenter(lineChart);

			seriesMain_1 = new XYChart.Series<>();
			dataMain_1 = seriesMain_1.getData();
			for (double i = xAxis.getLowerBound(); i < xAxis.getUpperBound(); i+=xAxis.getTickUnit()) 
			{
				dataMain_1.add(new XYChart.Data<>(i, 0 ));
			}
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
			
			if(chart.stepX > MedManager.VIEW_UPDATE_MINIMAL_PERIOD)
			{
				modulus = (int) (chart.stepX / MedManager.VIEW_UPDATE_MINIMAL_PERIOD);
			}
		}
		
		
		
		public void onTimelineTick()
		{
			if(isFirst.compareAndSet(false, true))
			{
				seriesMain_1.getNode().lookup(".chart-series-area-line").setStyle("-fx-stroke: rgb(192,192,192, 0.7);-fx-stroke-width: 2px;");
				seriesMain_2.getNode().lookup(".chart-series-area-line").setStyle("-fx-stroke: rgb(192,192,192, 0.7);-fx-stroke-width: 2px;");
				seriesLine.getNode().lookup(".chart-series-area-line").setStyle("-fx-stroke: aqua;-fx-stroke-width: 2px;");
				
				seriesMain_1.getNode().lookup(".chart-series-area-fill").setStyle("-fx-fill: rgba(0,255,255,0.1);");
				seriesMain_2.getNode().lookup(".chart-series-area-fill").setStyle("-fx-fill: rgba(0,255,255,0.1);");
				seriesLine.getNode().lookup(".chart-series-area-fill").setStyle("-fx-fill: transparent;");
				
			}
			if(MedManager.INSTANCE.data.isChartTitle && MedManager.INSTANCE.isSlowUpdate())
			{
				lineChart.setTitle(chart.getFullName());
			}
			
			if(MedManager.INSTANCE.isUpdateTick(modulus))
			{
				//System.out.println(chart.data.size());
				
				double result = 0;
				int size = chart.data.size();
				if(size == 1)
				{
					result = chart.data.remove(0);
				}
				else if(size == 0)
				{
					result = previous;
				}
				else
				{
					result = chart.data.get(size - 1);
					chart.data.clear();
				}
				previous = result;
				
				Data<Number, Number> data = fromList.remove(0);
				data.setYValue(result);
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

				nextX = nextX + chart.stepX;
				if(nextX >= chart.maxX)
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

				}
			}
		}
	}
	
	
/******************************************** 	DeviceTab ***********************************/	
	
	public static final class DeviceTab extends ScrollPane
	{
		private Device device;
		private DeviceFragment deviceFragment;
		private ChartFragment[] chartFragments;
		
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
			chartFragments = new ChartFragment[charts.size()]; 
			for (Chart chart : charts)
			{
				chartFragments[index] = new ChartFragment(chart);
				GridPane.setMargin(chartFragments[index], new Insets(4));
				
				chartFragments[index].maxHeightProperty().bind(center.heightProperty().divide(charts.size()).subtract(8));
				chartFragments[index].minHeightProperty().bind(center.heightProperty().divide(charts.size()).subtract(8));
				center.add(chartFragments[index], 0, index);
				index++;
			}
			content.setCenter(center);
		}
		
		public void update(boolean isToFront)
		{
			for (int i = 0; i < chartFragments.length; i++) 
			{
				ChartFragment chartFragment = chartFragments[i];
				if(chartFragment.chart.isChartReady && chartFragment.isCreated.compareAndSet(false, true))
				{
					chartFragment.createChart();
				}
				if(chartFragment.isCreated.get())
				{
					chartFragments[i].onTimelineTick();
				}
			}
			
			if(MedManager.INSTANCE.isSlowUpdate())
			{
				deviceFragment.update(device);
			}
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
	}
	

	public void setBed(Bed bed) 
	{
		this.bed = bed;
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
						deviceTab.update(isToFront);
					}
				}
			}
			
			else
			{
				Tab currentTab = getSelectionModel().getSelectedItem();
				if(currentTab != null)
				{
					DeviceTab deviceTab = (DeviceTab) currentTab.getContent();
					deviceTab.update(isToFront);
				}
			}
		}
	}

	@Override
	public void onToBack()
	{
//		for (Tab tab : getTabs())
//		{
//			DeviceTab deviceTab = (DeviceTab) tab.getContent();
//			deviceTab.stopChart();
//		}
	}
}
