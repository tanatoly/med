package com.rafael.med;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import com.rafael.med.common.Constants;
import com.rafael.med.common.ViewUtils;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
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
		
		public RegularModule(Bed bed)
		{
			this.bed = bed;
			
			this.setMaxHeight(50 +  (30 * 8));
			this.setMinHeight(50 +  (30 * 8));
			this.setBackground(Constants.BACKGOUND_20);
			this.setPadding(new Insets(0,6,0,6));
			
			RowConstraints r1 = new RowConstraints(50);
			RowConstraints r2 = new RowConstraints(30);
			getRowConstraints().addAll(r1, r2, r2, r2 ,r2 , r2, r2, r2, r2);
			
			ColumnConstraints c = new ColumnConstraints();
			c.setPercentWidth(25);
			getColumnConstraints().addAll(c,c,c,c);
			
			
			Text bedNumber 	= new Text(bed.getName());
			bedNumber.setFont(Font.font(20));
			bedNumber.setFill(Color.AQUA);
			alarm = ViewUtils.glyphIcon(FontAwesomeIcon.WARNING, 30, Color.TRANSPARENT);
			Text card 		= ViewUtils.glyphIcon(FontAwesomeIcon.ID_CARD, 30, Color.WHITE);
			card.setOnMouseClicked(e ->
			{
				MedManager.INSTANCE.showDetails(bed);
			});
			
			
			GridPane.setConstraints(bedNumber, 			0, 0, 2, 1, HPos.LEFT, VPos.CENTER);
			GridPane.setConstraints(alarm, 				2, 0, 1, 1, HPos.CENTER, VPos.CENTER);
			GridPane.setConstraints(card, 				3, 0, 1, 1, HPos.RIGHT, VPos.CENTER);
			getChildren().addAll(bedNumber, alarm, card);

			for (int i = 0; i < rows.length; i++) 
			{
				rows[i] = new RowText();
				GridPane.setConstraints(rows[i].name, 	0, i + 1, 2, 1, HPos.LEFT, VPos.CENTER);
				GridPane.setConstraints(rows[i].value, 	2, i + 1, 1, 1, HPos.CENTER, VPos.CENTER);
				GridPane.setConstraints(rows[i].units, 	3, i + 1, 1, 1, HPos.RIGHT, VPos.CENTER);
				
				getChildren().addAll(rows[i].name, rows[i].value, rows[i].units );
			}
			setBorder(new Border(new BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID, new CornerRadii(2.0), BorderWidths.DEFAULT)));
		}
		
		public void update()
		{
			int count = 0;
			for (Device device : bed.devices.values()) 
			{
				if(device != null)
				{
					for (Param param : device.params.values())
					{
						if(param !=null && param.isRegular && count < rows.length)
						{
							RowText row = rows[count];
							row.name.setText(param.name);
							row.value.setText(param.getValue());
							row.units.setText(param.units);
								
							Color color = param.isWarning.get() ? Color.RED : Color.WHITE;
							row.setColor(color);
							count++;
						}
					}
				}
			}
		}
	}
	
	
	
	private final Map<Bed, RegularModule> map = new ConcurrentSkipListMap<>((bed1, bed2) -> (int) (bed1.firstTime.get() - bed2.firstTime.get()));
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
//		for (EmergencyModule module : map.values()) 
//		{
//			module.update();
//		}
	}
}
