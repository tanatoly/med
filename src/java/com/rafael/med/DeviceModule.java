package com.rafael.med;

import com.rafael.med.common.Constants;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public final class DeviceModule extends GridPane
{
	private static final int ROWS = 12;
	
	private RowText[] params;
	private Text deviceType;
	private Text deviceSerial;
	
	public DeviceModule()
	{
		//setGridLinesVisible(true);
		
		setBackground(Constants.BACKGOUND_20);
		setPadding(new Insets(5, 15, 5, 15));
		
		RowConstraints r1 = new RowConstraints();
		r1.setPercentHeight(8);
		RowConstraints r2 = new RowConstraints();
		r2.setPercentHeight(2);
		RowConstraints r3 = new RowConstraints();
		r3.setPercentHeight((100 - 8 - 2)/ ROWS);
		getRowConstraints().addAll(r1,r2);
		
		for (int i = 0; i < ROWS; i++)
		{
			getRowConstraints().add(r3);
		}
		
		ColumnConstraints c = new ColumnConstraints();
		c.setPercentWidth(25);
		getColumnConstraints().addAll(c,c,c,c);
		
	
		GridPane.setMargin(this, new Insets(3));
		setBorder(new Border(new BorderStroke(Color.GHOSTWHITE, BorderStrokeStyle.SOLID, new CornerRadii(2.0), BorderWidths.DEFAULT)));
		
		deviceType 		= new Text();
		deviceType.setFill(Color.GREENYELLOW);
		deviceType.setFont(Font.font(22));
		deviceSerial	= new Text();
		deviceSerial.setFill(Constants.COLOR_95);
		deviceSerial.setFont(Font.font(22));

	
		GridPane.setConstraints(deviceType, 				0, 0, 2, 1, HPos.CENTER, VPos.CENTER);
		GridPane.setConstraints(deviceSerial, 				2, 0, 2, 1, HPos.CENTER, VPos.CENTER);
		getChildren().addAll(deviceType, deviceSerial);
		
		
		params = new RowText[ROWS];
		
		for (int i = 0; i < params.length; i++) 
		{
			params[i] = new RowText();
			GridPane.setConstraints(params[i].name, 	0, i + 2, 2, 1, HPos.LEFT, VPos.CENTER);
			GridPane.setConstraints(params[i].value, 	2, i + 2, 1, 1, HPos.CENTER, VPos.CENTER);
			GridPane.setConstraints(params[i].units, 	3, i + 2, 1, 1, HPos.RIGHT, VPos.CENTER);
			getChildren().addAll(params[i].name, params[i].value, params[i].units );
		}

	}

	public void update(Device device) 
	{
		deviceType.setText(device.name);
		deviceSerial.setText(device.serial);
		int count = 0;
		for (Param param : device.params.values())
		{
			if(count < params.length)
			{
				RowText rowText = params[count];
				rowText.name.setText(param.name);
				rowText.value.setText(param.getValue());
				rowText.units.setText(param.units);
				if(param.isWarning.get())
				{
					rowText.setColor(Color.RED);
				}
				else
				{
					rowText.setColor(Color.WHITE);
				}
			}
			count++;
		}
	}
}