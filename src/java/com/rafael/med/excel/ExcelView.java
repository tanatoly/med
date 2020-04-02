package com.rafael.med.excel;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTimePicker;
import com.rafael.med.AppManager;
import com.rafael.med.common.Utilities;
import com.rafael.med.common.ViewUtils;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class ExcelView extends BorderPane
{
	
	private static final Logger log = LogManager.getLogger();
	
	private LocalDateTime from;
	private LocalDate fromLocalDate;
	
	private LocalDateTime to;
	private LocalDate toLocalDate;
	private JFXButton chooseButton;
	private JFXButton executeButton;
	private File selectedir;
	private AppExcel app;
	private Label resultLabel;
	private File resultDir;

	public ExcelView(AppExcel appExcel)
	{
		this.app = appExcel;
		setTop(buildTop());
		setCenter(buildCenter());
		setBottom(buildBottom());	
		
		setPadding(new Insets(20));
		setStyle("-fx-background-color :  -color-60;");
	}
	
	public GridPane buildTop()
	{
		GridPane top = new GridPane();
		//top.setGridLinesVisible(true);
		top.setStyle("-fx-background-color:-color-60");
		
	
		Label  versionLabel = new Label();
		versionLabel.setFont(Font.font("Arial", FontWeight.BOLD, 40));
		GridPane.setHgrow(versionLabel, Priority.ALWAYS);
		
		chooseButton = ViewUtils.jfxbutton(null, FontAwesomeIcon.FOLDER_OPEN_ALT, 90, 60, Color.BLACK,Color.WHITE, Color.AQUA,null,2);	
		executeButton =  ViewUtils.jfxbutton(null, FontAwesomeIcon.GEARS, 90, 60, Color.BLACK,Color.WHITE, Color.AQUA,null,2);
		
		chooseButton.setOnAction(e -> 
		{
			
			selectedir = AppManager.INSTANCE.showDirectoryChooser(AppExcel.class);
			if(selectedir != null && selectedir.isDirectory() && selectedir.exists())
			{
				versionLabel.setText(selectedir.getAbsolutePath());
			}
		});
		
		
		executeButton.setOnAction(e -> 
		{
			if(from == null || to == null || selectedir == null || from.isAfter(to) || from.isEqual(to))
			{
				AppManager.INSTANCE.showError(AppExcel.class,log,"one of parameters is not correct",null);
			}
			else
			{
				
				try 
				{
					resultDir = process(selectedir);
					resultLabel.setText(resultDir.getAbsolutePath());
				} 
				catch (Exception e1) 
				{
					AppManager.INSTANCE.showError(AppExcel.class,log,"ON ACTION FAILED : ",e1);
				}
			}
		});
		
		
		RowConstraints r = new RowConstraints();
		r.setPercentHeight(100);
		ColumnConstraints c = new ColumnConstraints();
		c.setPercentWidth(20);
		ColumnConstraints c1 = new ColumnConstraints();
		c1.setPercentWidth(60);
		top.getRowConstraints().add(r);
		top.getColumnConstraints().addAll(c,c1,c);
		GridPane.setConstraints(chooseButton, 		0, 0, 1, 1, HPos.LEFT, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
		GridPane.setConstraints(versionLabel, 		1, 0, 1, 1, HPos.CENTER,  VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
		GridPane.setConstraints(executeButton, 		2, 0, 1, 1, HPos.RIGHT,  VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
		top.getChildren().addAll(chooseButton,  versionLabel, executeButton);

		
		BorderPane.setMargin(top, new Insets(0, 0,20,0));
		return top;
	}
	
	
	
	public GridPane buildCenter()
	{
		GridPane center = new GridPane();
		//center.setGridLinesVisible(true);
		center.setStyle("-fx-background-color:-color-40");
		
		
		RowConstraints r = new RowConstraints();
		r.setPercentHeight(10);
		center.getRowConstraints().addAll(r,r,r,r,r,r,r,r,r,r);
		ColumnConstraints c = new ColumnConstraints();
		c.setPercentWidth(20);
		center.getColumnConstraints().addAll(c,c,c,c,c);
		
		
		Label fromLabel			= new Label("FROM");
		JFXDatePicker fromDate 	= new JFXDatePicker();
		JFXTimePicker fromTime 	= new JFXTimePicker();
		fromDate.valueProperty().addListener(new ChangeListener<LocalDate>()
		{
			@Override
			public void changed(ObservableValue<? extends LocalDate> observable, LocalDate oldValue,LocalDate newValue)
			{
				fromLocalDate = newValue;
				from = LocalDateTime.of(fromLocalDate, LocalTime.MIN);
				fromLabel.setText("FROM " + from.format(Utilities.DATE_TIME_dd_MM_yyyy_HH_mm_ss));
			}
		});
		
		fromTime.valueProperty().addListener(new ChangeListener<LocalTime>()
		{
			@Override
			public void changed(ObservableValue<? extends LocalTime> observable, LocalTime oldValue,LocalTime newValue)
			{
				from = LocalDateTime.of(fromLocalDate, newValue);
				fromLabel.setText("FROM " + from.format(Utilities.DATE_TIME_dd_MM_yyyy_HH_mm_ss));
			}
		});
		
		Label toLabel			= new Label("TO");
		JFXDatePicker toDate 	= new JFXDatePicker();
		JFXTimePicker toTime 	= new JFXTimePicker();
		
		
		toDate.valueProperty().addListener(new ChangeListener<LocalDate>()
		{
			@Override
			public void changed(ObservableValue<? extends LocalDate> observable, LocalDate oldValue,LocalDate newValue)
			{
				toLocalDate = newValue;
				to = LocalDateTime.of(toLocalDate, LocalTime.MIN);
				toLabel.setText("TO " + to.format(Utilities.DATE_TIME_dd_MM_yyyy_HH_mm_ss));
			}
		});
		
		toTime.valueProperty().addListener(new ChangeListener<LocalTime>() {

			@Override
			public void changed(ObservableValue<? extends LocalTime> observable, LocalTime oldValue,LocalTime newValue)
			{
				to = LocalDateTime.of(toLocalDate, newValue);
				toLabel.setText("TO " + to.format(Utilities.DATE_TIME_dd_MM_yyyy_HH_mm_ss));
			}
		});
		
		fromTime.set24HourView(true);
		toTime.set24HourView(true);
		
		fromLabel.setFont(Font.font("Arial", FontWeight.BOLD, 50));
		GridPane.setHgrow(fromLabel, Priority.ALWAYS);
		
		toLabel.setFont(Font.font("Arial", FontWeight.BOLD, 50));
		GridPane.setHgrow(toLabel, Priority.ALWAYS);
		
		GridPane.setConstraints(fromLabel, 		0, 1, 2, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
		GridPane.setConstraints(fromDate, 		0, 2, 1, 1, HPos.RIGHT,  VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
		GridPane.setConstraints(fromTime, 		1, 2, 1, 1, HPos.RIGHT,  VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
		
		GridPane.setConstraints(toLabel, 		3, 1, 2, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
		GridPane.setConstraints(toDate, 		3, 2, 1, 1, HPos.LEFT,  VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
		GridPane.setConstraints(toTime, 		4, 2, 1, 1, HPos.LEFT,  VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);

		center.getChildren().addAll(fromLabel,  fromDate, fromTime , toLabel, toDate, toTime);
		return center;
	}
	
	public Node buildBottom()
	{
		resultLabel = new Label();	
		resultLabel.setFont(Font.font("Arial", FontWeight.BOLD, 40));
		HBox.setHgrow(resultLabel, Priority.ALWAYS);
		JFXButton resultButton = ViewUtils.jfxbutton(null, FontAwesomeIcon.FILES_ALT, 90, 60, Color.BLACK, Color.WHITE, Color.AQUAMARINE, "open", 2);
		resultButton.setOnAction(e -> 
		{
			try 
			{
				Desktop.getDesktop().open(resultDir);
			} 
			catch (IOException e1) 
			{
				e1.printStackTrace();
			}
		});
		HBox bottom = new HBox(100, resultButton,resultLabel);
		BorderPane.setAlignment(bottom, Pos.CENTER);
		BorderPane.setMargin(bottom, new Insets(20, 0,0,0));
		
		bottom.setStyle("-fx-background-color:-color-60");
		return bottom;
	}
	
	private File process(File sourceDir) throws Exception
	{
		Path resultDir = Path.of(sourceDir.getParent(), sourceDir.getName() + "-result-" + from.format(Utilities.DATE_TIME_dd_MM_yyyy_HH_mm_ss_minus) + "_" + to.format(Utilities.DATE_TIME_dd_MM_yyyy_HH_mm_ss_minus)); 	
		Utilities.copyDirectory(selectedir.toPath(), resultDir);
		
		List<File> zipFiles = Utilities.listFiles(resultDir, "zip");
		for (File zipFile : zipFiles) 
		{
			String zipfiString = zipFile.getName().substring(0, zipFile.getName().indexOf("."));
			Utilities.extractZipStream(zipFile, resultDir.toString() + File.separator + zipfiString);
			zipFile.delete();
		}
		
		Files.walkFileTree(resultDir, new SimpleFileVisitor<Path>()
		{
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
			{
				long lastModified = file.toFile().lastModified();	
				LocalDateTime lastModifiedDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(lastModified), ZoneId.systemDefault());
//				System.out.println(file + "    " + lastModifiedDateTime.format(Utilities.DATE_TIME_dd_MM_yyyy_HH_mm_ss));
				if(lastModifiedDateTime.isBefore(from) || lastModifiedDateTime.isAfter(to))
				{
					Files.delete(file);
				}
				return FileVisitResult.CONTINUE;
			}
			
			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException 
			{
				if(dir.toFile().listFiles().length == 0)
				{
					Files.delete(dir);
				}
				return FileVisitResult.CONTINUE;
			}
		});
		
		
		return resultDir.toFile();
	}

}
