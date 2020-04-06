package com.rafael.med;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import com.rafael.med.common.Constants;
import com.rafael.med.common.ViewUtils;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class DepartmentView extends ScrollPane
{
	
	public static final class RegularModule extends GridPane
	{
		private static final int ROWS = 8;
		
		private Bed bed;
		
		private final RowText[] rows = new RowText[ROWS];
		private Text alarm;
		
		private final AtomicBoolean isWarning = new AtomicBoolean();
		
		public RegularModule(Bed bed)
		{
			this.bed = bed;
			
			this.setMaxHeight(50 +  (32 * 9));
			this.setMinHeight(50 +  (32 * 9));
			this.setBackground(Constants.BACKGOUND_10);
			this.setPadding(new Insets(0,8,0,8));
			
			RowConstraints r1 = new RowConstraints(50);
			RowConstraints r2 = new RowConstraints(32);
			getRowConstraints().addAll(r1, r2, r2, r2 ,r2 , r2, r2, r2, r2 , r2);
			
			ColumnConstraints c = new ColumnConstraints();
			c.setPercentWidth(25);
			getColumnConstraints().addAll(c,c,c,c);
			
			
			Text bedNumber 	= new Text(bed.getName());
			bedNumber.setFont(Font.font(16));
			bedNumber.setFill(Color.AQUA);
			alarm = ViewUtils.glyphIcon(FontAwesomeIcon.WARNING, 24, Color.RED);
			Text card 		= ViewUtils.glyphIcon(FontAwesomeIcon.ID_CARD, 24, Color.WHITE);
			card.setOnMouseClicked(e ->
			{
				MedManager.INSTANCE.showDetails(bed);
			});
			
			GridPane.setConstraints(bedNumber, 			0, 0, 2, 1, HPos.CENTER, VPos.CENTER);
			GridPane.setConstraints(alarm, 				2, 0, 1, 1, HPos.CENTER, VPos.CENTER);
			GridPane.setConstraints(card, 				3, 0, 1, 1, HPos.RIGHT, VPos.CENTER);
			getChildren().addAll(bedNumber, alarm, card);

			for (int i = 0; i < rows.length; i++) 
			{
				rows[i] = new RowText(16);
				GridPane.setConstraints(rows[i].name, 	0, i + 1, 2, 1, HPos.LEFT, VPos.CENTER);
				GridPane.setConstraints(rows[i].value, 	2, i + 1, 1, 1, HPos.CENTER, VPos.CENTER);
				GridPane.setConstraints(rows[i].units, 	3, i + 1, 1, 1, HPos.RIGHT, VPos.CENTER);
				
				getChildren().addAll(rows[i].name, rows[i].value, rows[i].units );
			}
		}
		
		public void update()
		{
			int count = 0;
			isWarning.set(false);
			for (Device device : bed.devices.values()) 
			{
				if(device != null)
				{
					boolean isDeviceNotTransmit = false;
					long delta = System.currentTimeMillis() - device.lastMessageTime;
					isDeviceNotTransmit = delta > 10_000; // 10 seconds
					
					
					for (Param param : device.params.values())
					{
						if(param !=null && param.isRegular && count < rows.length)
						{
							RowText row = rows[count];
							row.name.setText(param.name);
							row.value.setText(param.getValue());
							row.units.setText(param.units);
								
							
							if(param.isWarning.get())
							{
								row.setColor(Color.RED);
								isWarning.compareAndSet(false, true);
							}
							else
							{
								row.setColor(Color.WHITE);
							}
							
							if(isDeviceNotTransmit)
							{
								row.setColor(Constants.COLOR_95);
							}
							count++;
						}
					}
				}
			}
			if(isWarning.get())
			{
				alarm.setFill(Color.RED);
				MedManager.INSTANCE.addToEmergency(bed);
			}
			else
			{
				alarm.setFill(Color.TRANSPARENT);
			}
		}
	}
	
	
	
	private final Map<Bed, RegularModule> map = new HashMap<>();
	private FlowPane flowPane;
	
	private double moduleWidth = 0;
	
	public  DepartmentView() 
	{
		flowPane = new FlowPane(4,4);
		
		setFitToWidth(true);
		setFitToHeight(true);
		flowPane.setAlignment(Pos.BASELINE_LEFT);
		setContent(flowPane);
		
	}
	
	public void addBed(Bed bed)
	{
		if(moduleWidth == 0)
		{
			moduleWidth = (getWidth() - 22) / 6  - 4;
		}
		
		if(!map.containsKey(bed))
		{
			RegularModule module = new RegularModule(bed);
			module.setMaxWidth(moduleWidth);
			module.setMinWidth(moduleWidth);
			map.put(bed, module);
			flowPane.getChildren().add(module);
		}
	}
	
	
	public void update()
	{
		for (RegularModule module : map.values()) 
		{
			module.update();
		}
	}
}
