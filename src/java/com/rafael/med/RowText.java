package com.rafael.med;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

final class RowText
{
	public Text name;
	public Text value;
	public Text units;
	
	public RowText()
	{
		name = new Text();
		name.setFill(Color.WHITE);
		name.setFont(Font.font(18));
		
		value = new Text();
		value.setFill(Color.WHITE);
		value.setFont(Font.font(18));
		
		units = new Text();
		units.setFill(Color.WHITE);
		units.setFont(Font.font(18));
	}
	
	public void setColor(Color color)
	{
		name.setFill(color);
		value.setFill(color);
		units.setFill(color);
	}
}