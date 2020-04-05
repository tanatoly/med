package com.rafael.med;

import java.util.ArrayList;
import java.util.List;

import com.rafael.med.MedData.Bed;
import com.rafael.med.MedData.Department;
import com.rafael.med.MedData.Room;
import com.rafael.med.common.Constants;
import com.rafael.med.common.ViewUtils;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class MainView extends BorderPane
{
	
	public final StackPane center;
	private Text title;
	
	public MainView(Text title)
	{
		this.title = title;
		setBackground(Constants.BACKGOUND_10);
		center = new StackPane();
		BorderPane.setMargin(center, new Insets(5));
		center.setBackground(Constants.BACKGOUND_90);
	}
	
	
	public void buildView(MedData data)
	{
		VBox pane = new VBox(40);
		BorderPane.setMargin(pane, new Insets(5));
		pane.setBackground(Constants.BACKGOUND_60);
		pane.setAlignment(Pos.TOP_CENTER);
		pane.getChildren().add(ViewUtils.vspace(10));
		
		Button buttonw = ViewUtils.jfxbutton("לא תקינים", FontAwesomeIcon.WARNING, 70, 70, Constants.COLOR_20, Color.GHOSTWHITE, Color.AQUA, "",2);
		pane.getChildren().add(buttonw);		
		pane.getChildren().add(ViewUtils.vspace());
		GridPane warningPane = createWarningPane();
		center.getChildren().add(warningPane);
		buttonw.setOnAction(e ->
		{
			title.setText("לא תקינים");
			ObservableList<Node> children = center.getChildren();
			for (Node node : children)
			{
				node.setVisible(node == warningPane);
			}
		});
		
		
		for (Department department : data.departments.values())
		{
			if(department != null)
			{
				Button button = ViewUtils.jfxbutton(department.id, FontAwesomeIcon.BUILDING, 70, 70, Constants.COLOR_20, Color.GHOSTWHITE, Color.AQUA, "",2);
				pane.getChildren().add(button);
				GridPane gridPane = createPane(department.rooms);
				center.getChildren().add(gridPane);
				
				button.setOnAction(e ->
				{
					title.setText(department.id);
					ObservableList<Node> children = center.getChildren();
					for (Node node : children)
					{
						node.setVisible(node == gridPane);
					}
				});
			}
		}
			
		pane.getChildren().add(ViewUtils.vspace());
		Button button = ViewUtils.jfxbutton("הגדרות", FontAwesomeIcon.COG, 70, 70, Constants.COLOR_20, Color.GHOSTWHITE, Color.AQUA, "",2);
		pane.getChildren().add(button);		
		pane.getChildren().add(ViewUtils.vspace(10));
		pane.setMaxWidth(110);
		pane.setMinWidth(110);
		
		
		setRight(pane);
		setCenter(center);
	}
	
	
	
	private GridPane createPane(List<Room> rooms)
	{
		List<Bed> beds = new ArrayList<>();
		for (Room room : rooms) 
		{
			beds.addAll(room.beds);
		}
		
		
		GridPane pane = new GridPane();
		//pane.setGridLinesVisible(true);
		int rows = 3;
		int columns = 7;
		
		for (int i = 0; i < rows; i++) 
		{
			RowConstraints rowConstraints = new RowConstraints();
			rowConstraints.setFillHeight(true);
			rowConstraints.setVgrow(Priority.ALWAYS);
			pane.getRowConstraints().add(rowConstraints);
		}
		
		for (int i = 0; i < columns; i++)
		{
			ColumnConstraints columnConstraints = new ColumnConstraints();
			columnConstraints.setFillWidth(true);
			columnConstraints.setHgrow(Priority.ALWAYS);
			pane.getColumnConstraints().add(columnConstraints);
		}
		
		int bedIndex = 0;
		for (int j = 0; j < rows; j++)
		{
			for (int i = 0; i < columns; i++)
			{
				
				Bed bed = beds.get(bedIndex);
				if(bed != null)
				{
					ThinModule thinModule = new ThinModule();
					thinModule.setBed(bed);
					thinModule.setBackground(Constants.BACKGOUND_10);
					GridPane.setMargin(thinModule, new Insets(1));
					pane.add(thinModule,i,j);
				}
				bedIndex++;
			}
		}
		return pane;
	}
	
	
	private GridPane createWarningPane()
	{
		GridPane pane = new GridPane();
		//pane.setGridLinesVisible(true);
		int rows = 6;
		int columns = 7;
		
		for (int i = 0; i < rows; i++) 
		{
			RowConstraints rowConstraints = new RowConstraints();
			rowConstraints.setFillHeight(true);
			rowConstraints.setVgrow(Priority.ALWAYS);
			pane.getRowConstraints().add(rowConstraints);
		}
		
		for (int i = 0; i < columns; i++)
		{
			ColumnConstraints columnConstraints = new ColumnConstraints();
			columnConstraints.setFillWidth(true);
			columnConstraints.setHgrow(Priority.ALWAYS);
			pane.getColumnConstraints().add(columnConstraints);
		}
		
		for (int j = 0; j < rows; j++)
		{
			for (int i = 0; i < columns; i++)
			{
				AlarmModule alarmModule = new AlarmModule();
				alarmModule.setBackground(Constants.BACKGOUND_10);
				GridPane.setMargin(alarmModule, new Insets(1));
				pane.add(alarmModule,i,j);
			}
		}
		return pane;
	}
}
