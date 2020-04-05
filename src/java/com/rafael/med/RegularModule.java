package com.rafael.med;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import com.rafael.med.MedData.Bed;
import com.rafael.med.MedData.Device;
import com.rafael.med.MedData.Param;
import com.rafael.med.common.ViewUtils;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class RegularModule extends GridPane
{
	private Bed bed;
	private Text bedNumber;
	private RowText[] params;
	private Text alarm;

	public RegularModule()
	{
		setGridLinesVisible(true);
		
		
		setPadding(new Insets(0, 10, 0, 10));
		RowConstraints r1 = new RowConstraints();
		r1.setPercentHeight(15);
		RowConstraints r2 = new RowConstraints();
		r2.setPercentHeight(5);
		RowConstraints r3 = new RowConstraints();
		r3.setPercentHeight(10);
		getRowConstraints().addAll(r1,r2, r3,r3,r3,r3,r3,r3,r3,r3);
		
		ColumnConstraints c = new ColumnConstraints();
		c.setPercentWidth(25);
		getColumnConstraints().addAll(c,c,c,c);
		
		Text fullView = ViewUtils.glyphIcon(FontAwesomeIcon.ID_CARD, 30, Color.WHITE);
		fullView.setOnMouseClicked(e ->
		{

			MedManager.INSTANCE.showDetails(bed);
		});
		bedNumber = new Text("007");
		bedNumber.setFill(Color.AQUA);
		bedNumber.setFont(Font.font(18));
		alarm = ViewUtils.glyphIcon(FontAwesomeIcon.WARNING, 30, Color.TRANSPARENT);
		alarm.setOnMouseClicked(e ->
		{
			
		});
		
		GridPane.setConstraints(bedNumber, 				0, 0, 2, 1, HPos.CENTER, VPos.CENTER);
		GridPane.setConstraints(alarm, 					2, 0, 1, 1, HPos.CENTER, VPos.CENTER);
		GridPane.setConstraints(fullView, 				3, 0, 1, 1, HPos.CENTER, VPos.CENTER);
		getChildren().addAll(bedNumber, fullView, alarm);
		
		params = new RowText[8];
		
		for (int i = 0; i < params.length; i++) 
		{
			params[i] = new RowText();
			GridPane.setConstraints(params[i].name, 	0, i + 2, 2, 1, HPos.LEFT, VPos.CENTER);
			GridPane.setConstraints(params[i].value, 	2, i + 2, 1, 1, HPos.CENTER, VPos.CENTER);
			GridPane.setConstraints(params[i].units, 	3, i + 2, 1, 1, HPos.RIGHT, VPos.CENTER);
			
			getChildren().addAll(params[i].name, params[i].value, params[i].units );
		}
		
		
		
	}
	
	public void setBed(Bed bed)
	{
		this.bed = bed;
		this.bedNumber.setText("חדר " + bed.room + "  מיטה " + bed.number);	
	}
	
	public void onTimeClick()
	{
		int count = 0;
		for (Map.Entry<String, Device> entry : bed.devices.entrySet()) 
		{
			String serial = entry.getKey();
			Device device = entry.getValue();
			AtomicBoolean isWarning = new AtomicBoolean(false);
			for (Param param : device.params.values())
			{
				if(param.isRegular)
				{
					if(count < params.length)
					{
						RowText rowText = params[count];
						//System.out.println(count);
						String name = param.name;
						rowText.name.setText(name);
						String value = param.getValue();
						rowText.value.setText(value);
						String units = param.units;
						rowText.units.setText(units);
						
						if(param.isWarning)
						{
							rowText.setColor(Color.RED);
							isWarning.compareAndSet(false, true);
						}
						else
						{
							rowText.setColor(Color.WHITE);
						}
						
						
						//System.out.println(" name = " + name + " value = " + value + " units = " + units);
					}
					count++;
				}
			}
			if(isWarning.get())
			{
				alarm.setFill(Color.RED);
				MedManager.INSTANCE.addAlarmBed(bed);
			}
			else
			{
				alarm.setFill(Color.TRANSPARENT);
			}
			
			
			//System.out.println("------------- " + serial);
		}
	}
}
