package com.rafael.med;

import org.apache.commons.lang3.StringUtils;

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
	private static final int ROWS = 20;
	
	private RowText[] rows;
	private Text deviceType;
	private Text deviceSerial;
	
	public DeviceModule()
	{
		//setGridLinesVisible(true);
		
		setBackground(Constants.BACKGOUND_10);
		setPadding(new Insets(4,10,4,10));
		
		this.setMaxHeight(50 +  (30 * ROWS));
		this.setMinHeight(50 +  (30 * ROWS));
		
		double rowPercent = 100/ (ROWS + 1);
		
		RowConstraints r = new RowConstraints();
		r.setPercentHeight(rowPercent);
		for (int i = 0; i <= ROWS; i++) 
		{
			getRowConstraints().add(r);
		}
		
		ColumnConstraints c = new ColumnConstraints();
		c.setPercentWidth(10);
		for (int i = 0; i <10; i++) 
		{
			getColumnConstraints().add(c);
		}
		
		
		GridPane.setMargin(this, new Insets(3));
		setBorder(new Border(new BorderStroke(Color.GHOSTWHITE, BorderStrokeStyle.SOLID, new CornerRadii(2.0), BorderWidths.DEFAULT)));
		
		deviceType 		= new Text();
		deviceType.setFill(Color.GREENYELLOW);
		deviceType.setFont(Font.font(22));
		deviceSerial	= new Text();
		deviceSerial.setFill(Constants.COLOR_95);
		deviceSerial.setFont(Font.font(22));

	
		GridPane.setConstraints(deviceType, 				0, 0, 5, 1, HPos.CENTER, VPos.CENTER);
		GridPane.setConstraints(deviceSerial, 				5, 0, 4, 1, HPos.CENTER, VPos.CENTER);
		getChildren().addAll(deviceType, deviceSerial);
		
		
		rows = new RowText[ROWS];
		
		for (int i = 0; i < rows.length; i++) 
		{	
			rows[i] = new RowText(18);
			GridPane.setConstraints(rows[i].name, 				0, i + 1, 2, 1, HPos.LEFT, VPos.CENTER);
			GridPane.setConstraints(rows[i].value, 				2, i + 1, 2, 1, HPos.LEFT, VPos.CENTER);
			GridPane.setConstraints(rows[i].units, 				4, i + 1, 2, 1, HPos.LEFT, VPos.CENTER);
			GridPane.setConstraints(rows[i].defaultValue, 		6, i + 1, 1, 1, HPos.LEFT, VPos.CENTER);
			GridPane.setConstraints(rows[i].range, 				7, i + 1, 3, 1, HPos.LEFT, VPos.CENTER);
			
			getChildren().addAll(rows[i].name, rows[i].value, rows[i].units, rows[i].defaultValue, rows[i].range );
	
		}

	}

	public void update(Device device) 
	{
		
		boolean isDeviceNotTransmit = false;
		long delta = System.currentTimeMillis() - device.lastMessageTime;
		isDeviceNotTransmit = delta > 10_000; // 10 seconds
		
		
		deviceType.setText(device.name);
		deviceSerial.setText(device.serial);
		int count = 0;
		for (Param param : device.params.values())
		{
			if(count < rows.length)
			{
				RowText rowText = rows[count];
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
			
			if(mfl != null && mfl.isError && count < rows.length)
			{
				RowText rowText = rows[count];
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