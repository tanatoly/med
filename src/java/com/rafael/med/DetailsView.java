package com.rafael.med;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jfoenix.controls.JFXDialog;
import com.rafael.med.MedData.Bed;
import com.rafael.med.MedData.Device;
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
	public static final class DeviceView extends GridPane
	{
		public DeviceView()
		{
			setGridLinesVisible(true);
			
			setBackground(Constants.BACKGOUND_20);
			
			RowConstraints r1 = new RowConstraints();
			r1.setPercentHeight(8);
			RowConstraints r2 = new RowConstraints();
			r2.setPercentHeight(2);
			RowConstraints r3 = new RowConstraints();
			r3.setPercentHeight(9);
			getRowConstraints().addAll(r1,r2,  r3,r3,r3,r3,r3 ,r3,r3,r3,r3,r3);
			
			ColumnConstraints c = new ColumnConstraints();
			c.setPercentWidth(25);
			getColumnConstraints().addAll(c,c,c,c);
			
			
			
			
			GridPane.setMargin(this, new Insets(3));
			setBorder(new Border(new BorderStroke(Color.GHOSTWHITE, BorderStrokeStyle.SOLID, new CornerRadii(2.0), BorderWidths.DEFAULT)));

		}

		public void update(Device device) 
		{
			
			
		}
	}
	
	
	private Text bedNumber;
	private Text name;
	private Text id;
	private final List<DeviceView> deviceViews = new ArrayList<>(8);
	
	
	public DetailsView(BorderPane mainPane, StackPane center)
	{
		super(center, null, DialogTransition.CENTER);
		Pane content = createContent();
		content.setBackground(Constants.BACKGOUND_40);
		content.setPadding(new Insets(0, 4, 4, 4));
		content.minWidthProperty().bind(mainPane.widthProperty().multiply(0.8));
		content.minHeightProperty().bind(mainPane.heightProperty().multiply(0.8));
		setContent(content);
		content.setBorder(new Border(new BorderStroke(Constants.COLOR_95, BorderStrokeStyle.SOLID, new CornerRadii(2.0), BorderWidths.DEFAULT)));
	}
	
	private GridPane createContent()
	{
		GridPane content = new GridPane();
		//content.setGridLinesVisible(true);
		
		RowConstraints r = new RowConstraints();
		r.setPercentHeight(3);
		RowConstraints r1 = new RowConstraints();
		r1.setPercentHeight(5);
		RowConstraints r2 = new RowConstraints();
		r2.setPercentHeight(46);
		content.getRowConstraints().addAll(r,r1,r2, r2);
		
		
		ColumnConstraints c2 = new ColumnConstraints();
		c2.setPercentWidth(25);
		
		content.getColumnConstraints().addAll(c2,c2,c2,c2);
		
		
		
		bedNumber = new Text("007");
		bedNumber.setFill(Color.AQUA);
		bedNumber.setFont(Font.font(32));
		
		name = new Text("��� ���");
		name.setFill(Color.WHITE);
		name.setFont(Font.font(32));
		
		id = new Text("0856");
		id.setFill(Constants.COLOR_95);
		id.setFont(Font.font(32));
		
		Text close = ViewUtils.glyphIcon(FontAwesomeIcon.CLOSE, 30, Color.WHITE);
		
		close.setOnMouseClicked(e ->
		{
			close();
		});
		GridPane.setConstraints(close, 					3, 0, 1, 1, HPos.RIGHT, VPos.TOP);
		GridPane.setConstraints(bedNumber, 				0, 1, 1, 1, HPos.CENTER, VPos.TOP);
		GridPane.setConstraints(name, 					1, 1, 2, 1, HPos.CENTER, VPos.TOP);
		GridPane.setConstraints(id, 					3, 1, 1, 1, HPos.CENTER, VPos.TOP);
		
		
		
		for (int i = 0; i < 4; i++)
		{
			for (int j = 2; j <= 3; j++)
			{
				Pane deviceView = new DeviceView();
				GridPane.setConstraints(deviceView, 				i, j, 1, 1, HPos.CENTER, VPos.CENTER);
				content.getChildren().add(deviceView);
			}
		}
		
		content.getChildren().addAll(bedNumber, name, id, close);
		
		
		
		
		return content;
	}
	
	
	public void update(Bed bed) 
	{
		bedNumber.setText(bed.number);
		name.setText(bed.patientName);
		id.setText(bed.patientId);
		
		int index = 0;
		for (Map.Entry<String, Device> entry : bed.devices.entrySet())
		{
			String serial = entry.getKey();
			Device device = entry.getValue();
			DeviceView deviceView = deviceViews.get(index);
			if(deviceView == null)
			{
				throw new IllegalStateException("NOT FOUND DEVICEVIEW FOR INDEX = " +index);
			}
			deviceView.update(device);
		}
	}
}
