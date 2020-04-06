package com.rafael.med;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

final class RowText
{
	public Text name;
	public Text value;
	public Text units;
	
	public RowText(double fontSize)
	{
		name = new Text();
		name.setFill(Color.WHITE);
		name.setFont(Font.font(fontSize));
		
		value = new Text();
		value.setFill(Color.WHITE);
		value.setFont(Font.font(fontSize));
		
		units = new Text();
		units.setFill(Color.WHITE);
		units.setFont(Font.font(fontSize));
	}
	
	public void setColor(Color color)
	{
		name.setFill(color);
		value.setFill(color);
		units.setFill(color);
	}
}