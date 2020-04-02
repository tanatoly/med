package com.rafael.med.debrief;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.rafael.med.AppManager;
import com.rafael.med.common.Utilities;
import com.rafael.med.common.ViewUtils;

import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.effect.Lighting;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class MinutesBar extends VBox
{
	private static final Logger log = LogManager.getLogger();
	
	private double currentRectangleX;
	private double currentMouseX;

	private long currentTime;

	private ScrollBar minutesTableScrollBar;
	private LocalDateTime currentFromSecond;
	private Label dateLabel;
	private Label timeLabel;
	private List<TableColumn<RowData, RowCell>> allcolumns;
	private int durationInSeconds;

	@SuppressWarnings("unchecked")
	public MinutesBar(AppDebrief appDebrief, int durationInSeconds, int minuteCellWidth,TableView<RowData> tableMinutes, int headerheight)
	{
		this.durationInSeconds = durationInSeconds;
		setAlignment(Pos.TOP_CENTER);
		int width = (durationInSeconds - 60)/60 * minuteCellWidth;
		setMinWidth(width);
		setMaxWidth(width);
		prefHeightProperty().bind(tableMinutes.heightProperty());
		Color color = Color.color(Color.BLACK.getRed(), Color.BLACK.getGreen(), Color.BLACK.getBlue(), 0.4);
		setBackground(new Background(new BackgroundFill(color, null, null)));
		setEffect(new Lighting());
		
		this.allcolumns = (List<TableColumn<RowData, RowCell>>) tableMinutes.getUserData();
		
		TableColumn<RowData, RowCell> first = allcolumns.get(0);
		TableColumn<RowData, RowCell> last = allcolumns.get(allcolumns.size() - 1);
		
		VBox up = new VBox(8);
		up.setAlignment(Pos.CENTER);
		up.setMaxHeight(headerheight);
		up.setMinHeight(headerheight);
		up.setMaxWidth(Double.MAX_VALUE);
		up.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
		
		dateLabel = new Label();
		dateLabel.setAlignment(Pos.CENTER);
		dateLabel.setStyle("-fx-font-size: 22;-fx-font-weight: BOLD;-fx-text-fill: black;");
		timeLabel = new Label();
		timeLabel.setAlignment(Pos.CENTER);
		timeLabel.setStyle("-fx-font-size: 15;-fx-font-weight: BOLD;-fx-text-fill: black;");
		up.getChildren().addAll(dateLabel,timeLabel);
		getChildren().add(up);
		
		setOnMouseClicked(new EventHandler<MouseEvent>()
		{
			@Override
			public void handle(MouseEvent event)
			{
				if(event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2 && currentFromSecond != null)
				{
					try
					{
						appDebrief.showDebrief(currentFromSecond);
					}
					catch (Exception ex)
					{
						AppManager.INSTANCE.showError(AppDebrief.class, log, "ON ACTION ERROR : ", ex);
					}
				}
			}
		});
		
		
		
		
		
		setOnMousePressed(new EventHandler<MouseEvent>()
		{
			@Override
			public void handle(MouseEvent event)
			{
				event.consume();
				if(event.getButton() == MouseButton.PRIMARY)
				{
					currentMouseX 		= event.getSceneX();
					currentRectangleX 	= getTranslateX();
				}
				if(minutesTableScrollBar == null)
				{
					minutesTableScrollBar = ViewUtils.getNodeScrollBar(tableMinutes,Orientation.HORIZONTAL);
					minutesTableScrollBar.setDisable(true);
				}
			}	
		});
		
		
		setOnMouseDragged(new EventHandler<MouseEvent>()
		{
			@Override
			public void handle(MouseEvent event)
			{
				event.consume();
				long delta = System.nanoTime() - currentTime;
				
				if(event.getButton() == MouseButton.PRIMARY && delta > 10_000_000)
				{
					double deltaX = event.getSceneX() - currentMouseX;
					currentRectangleX = currentRectangleX 	+  deltaX;		
					double stackWidth = (tableMinutes.getParent().getLayoutBounds().getWidth() - getWidth())/2;
					if(currentRectangleX < stackWidth && currentRectangleX > -stackWidth)
					{
						setTranslateX(currentRectangleX);
						currentMouseX = event.getSceneX();
					}
					
					if (currentRectangleX >= stackWidth - minuteCellWidth)
					{
						if(minutesTableScrollBar != null)
						{
							tableMinutes.scrollToColumn(last);
						}
					}
					else if (currentRectangleX <= -stackWidth + minuteCellWidth)
					{
						if(minutesTableScrollBar != null)
						{
							tableMinutes.scrollToColumn(first);
						}
					}
					currentTime = System.nanoTime();
					rectangleToMinute();
				}
			}
		});
	}
	
	
	private void rectangleToMinute()
	{
		double rectangleX = localToScene(getLayoutBounds()).getMinX();
		
		for (TableColumn<RowData,RowCell> tableColumn : allcolumns)
		{
			Label label = (Label) tableColumn.getGraphic();
			double x = label.localToScene(label.getLayoutBounds()).getMinX();
			
			if(rectangleX >= x - 2 && rectangleX < x + label.getWidth() + 2)
			{
				currentFromSecond = (LocalDateTime) label.getUserData();
			}
		}
		if(currentFromSecond != null)
		{
			dateLabel.setText(currentFromSecond.format(Utilities.DATE_TIME_dd_MM_yyyy));
			timeLabel.setText(currentFromSecond.format(Utilities.DATE_TIME_HH_mm_ss) + " - " + currentFromSecond.plusSeconds(durationInSeconds).format(Utilities.DATE_TIME_HH_mm_ss));
		}
	}
	
	
	public void reset()
	{
		currentRectangleX	= 0;
		currentMouseX		= 0;
		currentTime			= 0;
		currentFromSecond 	= null;
		dateLabel.setText(null);
		timeLabel.setText(null);
		
		rectangleToMinute();
	}
}
