package com.rafael.med;

import java.util.ArrayList;
import java.util.List;

import com.jfoenix.controls.JFXDialog;
import com.rafael.med.common.Constants;
import com.rafael.med.common.ViewUtils;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class DetailsView extends JFXDialog
{
	private Text bedNumber;
	private Text name;
	private Text id;
	private final List<DeviceModule> deviceViews = new ArrayList<>(8);
	private Bed bed;
	
	
	public DetailsView(BorderPane mainPane, StackPane center)
	{
		super(center, null, DialogTransition.CENTER);
		Pane content = createContent();
		content.setBackground(Constants.BACKGOUND_40);
		content.setPadding(new Insets(0, 0, 4, 4));
		content.setMinSize(1200, 800);
		content.setMaxSize(1200, 800);
		
		setContent(content);
		content.setBorder(new Border(new BorderStroke(Constants.COLOR_95, BorderStrokeStyle.SOLID, new CornerRadii(2.0), BorderWidths.DEFAULT)));
	}
	
	private GridPane createContent()
	{
		GridPane content = new GridPane();
		//content.setGridLinesVisible(true);
		
		RowConstraints r0 = new RowConstraints(10);
		RowConstraints r1 = new RowConstraints(60);
		RowConstraints r2 = new RowConstraints((800 - 60 - 20 - 4)/2);
		content.getRowConstraints().addAll(r0,r1,r2, r2);
		
		
		ColumnConstraints c2 = new ColumnConstraints((1200 - 8 - 4 )/3);
		//c2.setPercentWidth(33);
		
		content.getColumnConstraints().addAll(c2,c2,c2);
		
		
		
		bedNumber = new Text();
		bedNumber.setFill(Color.AQUA);
		bedNumber.setFont(Font.font(32));
		
		name = new Text();
		name.setFill(Color.WHITE);
		name.setFont(Font.font(32));
		
		id = new Text();
		id.setFill(Constants.COLOR_95);
		id.setFont(Font.font(32));
		
		Text close = ViewUtils.glyphIcon(FontAwesomeIcon.CLOSE, 30, Color.WHITE);
		
		close.setOnMouseClicked(e ->
		{
			close();
		});
		GridPane.setConstraints(close, 					2, 0, 1, 1, HPos.RIGHT, VPos.TOP);
		GridPane.setConstraints(bedNumber, 				0, 1, 1, 1, HPos.CENTER, VPos.TOP);
		GridPane.setConstraints(name, 					1, 1, 1, 1, HPos.CENTER, VPos.TOP);
		GridPane.setConstraints(id, 					2, 1, 1, 1, HPos.CENTER, VPos.TOP);
		
		
		
		for (int i = 0; i < 3; i++)
		{
			for (int j = 2; j <= 3; j++)
			{
				DeviceModule deviceView = new DeviceModule();
				GridPane.setConstraints(deviceView, 				i, j, 1, 1, HPos.CENTER, VPos.CENTER);
				content.getChildren().add(deviceView);
				deviceViews.add(deviceView);
			}
		}
		
		content.getChildren().addAll(bedNumber, name, id, close);
		
		
		
		
		return content;
	}
	
	
	public void setBed(Bed bed) 
	{
		this.bed = bed;
		update();
	}

	public void update()
	{
		if(bed != null)
		{
			bedNumber.setText(bed.getName());
			name.setText(bed.patientName);
			id.setText(bed.patientId);
		
			int index = 0;
			for (Device device : bed.devices.values())
			{
				
				if(index < deviceViews.size())
				{
				
					DeviceModule deviceView = deviceViews.get(index);
					if(deviceView == null)
					{
						throw new IllegalStateException("NOT FOUND DEVICEVIEW FOR INDEX = " +index);
					}
					deviceView.update(device);
					index++;
				}
			}
		}
	}
}
