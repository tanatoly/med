package com.rafael.med.debrief;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.rafael.med.common.Utilities;
import com.rafael.med.common.ViewUtils;
import com.rafael.med.common.bnet.ChannelType;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Callback;

public class TablesView extends BorderPane
{
	private static final Logger log = LogManager.getLogger();

	private static final int MINUTE_CELL_WIDTH 		= 30;
	private static final String MINUTES 			= "MINUTES";
	private static final String HOURS 				= "HOURS";
	private static final String DAYS 				= "DAYS";
	private static final int headerHeight 			= 66;
	private static final int rows 					= 6;
	private static final int UNIT_VIEW_WIDTH 		= 200;

	
	
	private Map<File, RowData> rowsMap;

	private TableView<RowData> 		tableDays;
	private TableView<RowData> 		tableHours;
	private TableView<RowData> 		tableMinutes;
	private TableView<RowData>		currentView;				
	private TableView<RowData> 		unitView;

	private TreeSet<LocalDate>		dates		= new TreeSet<>();

	private TableColumn<RowData, File> unitColumn;
	private MinutesBar minutesBar;

	private AppDebrief appDebrief;
	
	public TablesView(AppDebrief appDebrief, int durationInSeconds, Map<File, RowData> rowsMap)
	{
		this.appDebrief			= appDebrief;
		this.rowsMap			= rowsMap;
		
		StackPane centerStack	= new StackPane();
		
		this.tableDays 		= createDaysTable();
	    this.tableHours 	= createHoursTable();
	    this.tableMinutes 	= createMinutesTable();
	    this.unitView		= createListTable();
	    this.minutesBar 	= new MinutesBar(appDebrief, durationInSeconds,MINUTE_CELL_WIDTH,tableMinutes,headerHeight);
	    
	    centerStack.getChildren().addAll(tableDays,tableHours,tableMinutes,minutesBar);
	   
	    tableDays.setVisible(false);
	    tableHours.setVisible(false);
	    tableMinutes.setVisible(false);
	    minutesBar.setVisible(false);
	    
	    minutesBar.visibleProperty().bind(tableMinutes.visibleProperty());
	    
	    setCenter(centerStack);
	    setLeft(unitView);
	    
	
		unitView.fixedCellSizeProperty().bind(unitView.heightProperty().subtract(headerHeight).divide(rows));
	    tableDays.fixedCellSizeProperty().bind(tableDays.heightProperty().subtract(headerHeight).divide(rows));
	    tableHours.fixedCellSizeProperty().bind(tableHours.heightProperty().subtract(headerHeight).divide(rows));
	    tableMinutes.fixedCellSizeProperty().bind(tableMinutes.heightProperty().subtract(headerHeight).divide(rows));
	    
	    unitView.setMaxWidth(UNIT_VIEW_WIDTH);
	    unitView.setMinWidth(UNIT_VIEW_WIDTH);
	    
	}

	private TableView<RowData> createDaysTable()
	{
		TableView<RowData> table = new TableView<>();
		table.setEditable(false);
		table.setSelectionModel(null);
		table.setFocusTraversable(false);
		table.setFocusModel(null);
		return table;
	}
	
	
	@SuppressWarnings("unchecked")
	private TableView<RowData> createHoursTable()
	{
		TableView<RowData> table = new TableView<>();
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		table.setEditable(false);
		table.setSelectionModel(null);
		table.setFocusTraversable(false);
		table.setFocusModel(null);
		
		TableColumn<RowData, RowCell> parentBefore 		= new TableColumn<>();
		TableColumn<RowData, RowCell> parentCurrent 	= new TableColumn<>();
		TableColumn<RowData, RowCell> parentAfter 		= new TableColumn<>();
		
		for (int i = 0; i < 26; i++)
		{
			TableColumn<RowData, RowCell> hourColumn = new TableColumn<>();
			hourColumn.setSortable(false);
			hourColumn.setMinWidth(50);
			hourColumn.setCellValueFactory(new PropertyValueFactory<>("hour" + i));
			hourColumn.setCellFactory(new Callback<TableColumn<RowData,RowCell>, TableCell<RowData,RowCell>>()
			{
				@Override
				public TableCell<RowData, RowCell> call(TableColumn<RowData, RowCell> param)
				{
					return new TimeTableCell();
				}
			});

			Label hourHeader = new Label();
			hourHeader.setMaxWidth(Double.MAX_VALUE);
			hourColumn.setGraphic(hourHeader);
			hourColumn.setText(null);
			hourColumn.setSortable(false);
			hourHeader.setOnMouseClicked(new EventHandler<MouseEvent>()
			{
				@Override
				public void handle(MouseEvent event)
				{
					if(event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2)
					{
						showMinutesTable((LocalDateTime) hourHeader.getUserData());
					}
				}
			});
		
			if(i == 0)
			{
				hourHeader.setText("23:00");
				parentBefore.getColumns().add(hourColumn);
			}
			else if (i == 25)
			{
				hourHeader.setText("00:00");
				parentAfter.getColumns().add(hourColumn);
			}
			else
			{
				hourHeader.setText(String.format("%02d:00", i-1));
				parentCurrent.getColumns().add(hourColumn);
			}
		}
		table.getColumns().addAll(parentBefore, parentCurrent, parentAfter);
		return table;
	}
	
	@SuppressWarnings("unchecked")
	private TableView<RowData> createMinutesTable()
	{
		TableView<RowData> table = new TableView<>();
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		table.setEditable(false);
		table.setSelectionModel(null);
		table.setFocusTraversable(false);
		table.setFocusModel(null);
		
		TableColumn<RowData, RowCell> parentBefore 		= new TableColumn<>();
		TableColumn<RowData, RowCell> parentCurrent 	= new TableColumn<>();
		TableColumn<RowData, RowCell> parentAfter 		= new TableColumn<>();
		
		List<TableColumn<RowData, RowCell>> allcolumns = new ArrayList<>(70);
		
		for (int i = 0; i < 70; i++)
		{
			TableColumn<RowData, RowCell> minuteColumn = new TableColumn<>();
			minuteColumn.setSortable(false);
			minuteColumn.setMinWidth(MINUTE_CELL_WIDTH);
			minuteColumn.setMaxWidth(MINUTE_CELL_WIDTH);
			minuteColumn.setSortable(false);
			minuteColumn.setCellValueFactory(new PropertyValueFactory<>("minute" + i));
			minuteColumn.setCellFactory(new Callback<TableColumn<RowData,RowCell>, TableCell<RowData,RowCell>>()
			{
				@Override
				public TableCell<RowData, RowCell> call(TableColumn<RowData, RowCell> param)
				{
					return new TimeTableCell();
				}
			});
			
			allcolumns.add(minuteColumn);
			Label minuteHeader = new Label();
			minuteHeader.setMaxWidth(Double.MAX_VALUE);
			minuteColumn.setGraphic(minuteHeader);
			minuteColumn.setText(null);
			
			if(i < 5)
			{
				minuteHeader.setText(String.format("%02d", 55 + i));
				parentBefore.getColumns().add(minuteColumn);
			}
			else if (i > 64)
			{
				minuteHeader.setText(String.format("%02d", i - 65));
				parentAfter.getColumns().add(minuteColumn);
			}
			else
			{
				minuteHeader.setText(String.format("%02d", i-5));
				parentCurrent.getColumns().add(minuteColumn);
			}
		}
		table.setUserData(allcolumns);
		
		table.getColumns().addAll(parentBefore, parentCurrent, parentAfter);
		return table;
	}
	
	private void showDaysTable(TreeSet<LocalDate> allDates)
	{
		log.debug("showDaysTable allDates {}", allDates);
		
		tableDays.getItems().clear();
		tableDays.getColumns().clear();
		tableDays.refresh();
		TableColumn<RowData, RowCell> parent 			= new TableColumn<>("Record dates");
		
		int index = 0;
		for (LocalDate date : allDates)
		{
			TableColumn<RowData, RowCell> dayColumn = new TableColumn<>();
			dayColumn.setSortable(false);
			dayColumn.setCellValueFactory(new PropertyValueFactory<>("day" + index));
			dayColumn.setCellFactory(new Callback<TableColumn<RowData,RowCell>, TableCell<RowData,RowCell>>()
			{
				@Override
				public TableCell<RowData, RowCell> call(TableColumn<RowData, RowCell> param)
				{
					return new TimeTableCell();
				}
			});
			
			dayColumn.setMinWidth(200);
			parent.getColumns().add(dayColumn);
			
			Label  dayHeader = new Label(date.format(Utilities.DATE_TIME_dd_MM_yyyy));
			dayHeader.setMaxWidth(Double.MAX_VALUE);
			dayColumn.setGraphic(dayHeader);
			dayColumn.setText(null);
			dayHeader.setOnMouseClicked(new EventHandler<MouseEvent>()
			{
				@Override
				public void handle(MouseEvent event)
				{
					if(event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2)
					{
						showHoursTable(date);
					}
				}
			});
			index++;
		}
		tableDays.getColumns().add(parent);
		for (RowData rowData : rowsMap.values())
		{
			if(rowData != null)
			{
				rowData.setAllDates(allDates);
				tableDays.getItems().add(rowData);
			}
		}

		tableDays.setVisible(true);
		tableHours.setVisible(false);
		tableMinutes.setVisible(false);
		currentView = tableDays;
		
		appDebrief.upButton.setDisable(true);
		unitColumn.setText(DAYS);
	}
	private void showHoursTable(LocalDate date)
	{
		log.debug("showHoursTable for date {}", date.format(Utilities.DATE_TIME_dd_MM_yyyy));
		
		LocalDateTime fromHour 	= date.minusDays(1).atStartOfDay().plusHours(23);
		LocalDateTime currentHour = null;

		TableColumn<RowData, ?> parentBefore = tableHours.getColumns().get(0);
		parentBefore.setText(date.minusDays(1).format(Utilities.DATE_TIME_dd_MM));
		TableColumn<RowData, ?> parentCurrent = tableHours.getColumns().get(1);
		parentCurrent.setText(date.format(Utilities.DATE_TIME_dd_MM_yyyy));
		TableColumn<RowData, ?> parentAfter = tableHours.getColumns().get(2);
		parentAfter.setText(date.plusDays(1).format(Utilities.DATE_TIME_dd_MM));
		
		for (int i = 0; i < 26; i++)
		{
			currentHour = fromHour.plusHours(i);
			if(i == 0 )
			{
				Label header = (Label) parentBefore.getColumns().get(0).getGraphic();
				header.setUserData(currentHour);
			}
			else if (i == 25)
			{
				Label header = (Label) parentAfter.getColumns().get(0).getGraphic();
				header.setUserData(currentHour);
			}
			else
			{
				Label header = (Label) parentCurrent.getColumns().get(i - 1).getGraphic();
				header.setUserData(currentHour);
			}
		}
		
		tableHours.getItems().clear();
		for (RowData rowData : rowsMap.values())
		{
			if(rowData != null)
			{
				rowData.setHoursRange(fromHour, currentHour);
				tableHours.getItems().add(rowData);
			}
		}
		
		tableDays.setVisible(false);
		tableHours.setVisible(true);
		tableMinutes.setVisible(false);
		currentView = tableHours;
		appDebrief.upButton.setDisable(false);
		unitColumn.setText(HOURS);
	}

	private void showMinutesTable(LocalDateTime hour)
	{
		log.debug("showMinutesTable for hour {} ",hour.format(Utilities.DATE_TIME_dd_MM_yyyy_HH_mm));
		
		LocalDateTime fromMinute 			= hour.minusMinutes(5);
		LocalDateTime currentMinute 		= null;

		TableColumn<RowData, ?> parentBefore = tableMinutes.getColumns().get(0);
		parentBefore.setText(fromMinute.format(Utilities.DATE_TIME_dd_MM_yyyy_HH_mm));
		TableColumn<RowData, ?> parentCurrent = tableMinutes.getColumns().get(1);
		parentCurrent.setText(hour.plusMinutes(30).format(Utilities.DATE_TIME_dd_MM_yyyy_HH_mm));
		TableColumn<RowData, ?> parentAfter = tableMinutes.getColumns().get(2);
		parentAfter.setText(hour.plusHours(1).plusMinutes(5).format(Utilities.DATE_TIME_dd_MM_yyyy_HH_mm));
		
		for (int i = 0; i < 70; i++)
		{
			currentMinute = fromMinute.plusMinutes(i);
			
			if(i < 5)
			{
				Label header = (Label) parentBefore.getColumns().get(i).getGraphic();
				header.setUserData(currentMinute);
			}
			else if (i > 64)
			{
				Label header = (Label) parentAfter.getColumns().get(i - 65).getGraphic();
				header.setUserData(currentMinute);
			}
			else
			{
				Label header = (Label) parentCurrent.getColumns().get(i - 5).getGraphic();
				header.setUserData(currentMinute);
			}
		}
		
		tableMinutes.getItems().clear();
		for (RowData rowData : rowsMap.values())
		{
			if(rowData != null)
			{
				rowData.setMinutesRange(fromMinute, currentMinute);
				tableMinutes.getItems().add(rowData);
			}
		}
		tableDays.setVisible(false);
		tableHours.setVisible(false);
		tableMinutes.setVisible(true);
		currentView = tableMinutes;
		
		appDebrief.upButton.setDisable(false);
		unitColumn.setText(MINUTES);
		minutesBar.reset();
	}

	public void actionUp()
	{
		if(currentView == null)
		{
			tableDays.setVisible(true);
			currentView = tableDays;
			unitColumn.setText(DAYS);
		}
		else
		{
			if(currentView == tableHours)
			{
				tableDays.setVisible(true);
				tableHours.setVisible(false);
				tableMinutes.setVisible(false);
				currentView = tableDays;
				unitColumn.setText(DAYS);
			}
			else if(currentView == tableMinutes)
			{
				tableDays.setVisible(false);
				tableHours.setVisible(true);
				tableMinutes.setVisible(false);
				currentView = tableHours;
				unitColumn.setText(HOURS);
			}
		}
		appDebrief.upButton.setDisable(currentView == tableDays);
	}
	
	private TableView<RowData> createListTable()
	{
		TableView<RowData> table = new TableView<>();
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		table.setEditable(false);
		table.setSelectionModel(null);
		table.setFocusTraversable(false);
		table.setFocusModel(null);

		TableColumn<RowData, File> parent = new TableColumn<>("Unit");
		table.getColumns().add(parent);
		
		this.unitColumn = new TableColumn<>();
		unitColumn.setCellValueFactory(new PropertyValueFactory<>("unit"));
		unitColumn.setMinWidth(170);
		unitColumn.setSortable(false);
		unitColumn.setCellFactory(new Callback<TableColumn<RowData, File>, TableCell<RowData, File>>()
		{
			@Override
			public TableCell<RowData, File> call(TableColumn<RowData, File> param)
			{
				return new UnitTableCell(appDebrief,param);
			}
		});
		
		parent.getColumns().add(unitColumn);
		return table;
		
	}
	
	public void reset()
	{
		dates.clear();
		unitView.getItems().clear();
		for (RowData rowData : rowsMap.values())
		{
			if(rowData != null)
			{
				dates.addAll(rowData.getDates());
				unitView.getItems().add(rowData);
			}
		}
		showDaysTable(dates);
	}	
}
class TimeTableCell extends TableCell<RowData, RowCell>
{
	private VBox vbox;
	private Map<ChannelType, Label> map = new HashMap<>();
	private static final Background transparentBackground = new Background(new BackgroundFill(Color.TRANSPARENT, null,null));
	
	protected TimeTableCell()
	{
		this.vbox = new VBox();
		for (ChannelType channelType : ChannelType.values())
		{
			Label label = new Label();
			label.setMinHeight(1);
			label.prefWidthProperty().bind(widthProperty());
			map.put(channelType, label);
			vbox.getChildren().add(label);
		}	
	}
	
	@Override
	protected void updateItem(RowCell item, boolean empty)
	{
		super.updateItem(item, empty);
		if(empty || item == null)
		{
			setGraphic(null);
		}
		else
		{
			if(item.isExist())
			{
				map.get(ChannelType.LEGACY_RX_RTP).setBackground((item.isLegacyRx) ? ChannelType.LEGACY_RX_RTP.background : transparentBackground);
				map.get(ChannelType.LEGACY_TX_RTP).setBackground((item.isLegacyTx) ? ChannelType.LEGACY_TX_RTP.background : transparentBackground);
				map.get(ChannelType.GUARD_RX_RTP).setBackground((item.isGuardRx) ? ChannelType.GUARD_RX_RTP.background : transparentBackground);
				map.get(ChannelType.GUARD_TX_RTP).setBackground((item.isGuardTx) ? ChannelType.GUARD_TX_RTP.background : transparentBackground);
				map.get(ChannelType.MANET_RX_RTP).setBackground((item.isManetRx) ? ChannelType.MANET_RX_RTP.background : transparentBackground);
				map.get(ChannelType.MANET_TX_RTP).setBackground((item.isManetTx) ? ChannelType.MANET_TX_RTP.background : transparentBackground);
				map.get(ChannelType.SATCOM_RX_RTP).setBackground((item.isSatcomRx) ? ChannelType.SATCOM_RX_RTP.background : transparentBackground);
				map.get(ChannelType.SATCOM_TX_RTP).setBackground((item.isSatcomTx) ? ChannelType.SATCOM_TX_RTP.background : transparentBackground);
				setGraphic(vbox);
			}
		}
	}
}
class UnitTableCell extends TableCell<RowData,File>
{
	private Label label;
	private VBox vbox;
	private File directory;
	
	protected UnitTableCell(AppDebrief appDebrief, TableColumn<RowData, File> param)
	{		
		setPadding(new Insets(6));
		this.vbox = new VBox(1);
		Button removeButton 	= ViewUtils.jfxbutton(null, FontAwesomeIcon.REMOVE, 30, 30, Color.GRAY, Color.GHOSTWHITE, Color.AQUA, "",2);
		removeButton.setOnAction(e->
		{
			appDebrief.removeDirectory(directory);
		});
		
		this.label = new Label();
		this.label.setAlignment(Pos.CENTER_LEFT);
		this.label.setStyle("-fx-font-size: 16;-fx-font-weight: BOLD;-fx-text-fill: white;");
		
		vbox.getChildren().add(new HBox(label,ViewUtils.hspace(),removeButton));
		vbox.setAlignment(Pos.TOP_CENTER);
		vbox.getChildren().add(ViewUtils.vspace());
		
		for (ChannelType channelType : ChannelType.values())
		{
			HBox hbox = new HBox();
			hbox.setMaxWidth(Double.MAX_VALUE);
			hbox.setAlignment(Pos.CENTER_LEFT);
			
			Label label = new Label(channelType.name());
			label.setStyle("-fx-font-size: 8; -fx-font-weight: BOLD;-fx-text-fill: white;");
			
			Rectangle g = new Rectangle(80,4,channelType.color);
			hbox.getChildren().addAll(label,ViewUtils.hspace(),g);
			
			vbox.getChildren().add(hbox);
		}	
		setStyle("-fx-border-color : gray;-fx-background-color : -color-40;");
	}
	
	@Override
	protected void updateItem(File item, boolean empty)
	{
		super.updateItem(item, empty);
		if(empty || item == null)
		{
			setGraphic(null);
		}
		else
		{
			this.directory = item;
			label.setText(item.getName());
			setGraphic(vbox);
		}
	}
}
