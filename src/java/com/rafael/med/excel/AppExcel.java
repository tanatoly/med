package com.rafael.med.excel;

import java.util.List;

import org.apache.commons.configuration2.Configuration;

import com.rafael.med.AppBase;

import javafx.scene.Parent;
import javafx.stage.Stage;

public class AppExcel extends AppBase
{	
	@Override
	public Parent init(Configuration configuration, Stage stage, List<String> args) 
	{
		ExcelView main = new ExcelView(this);
		return main;
	}
}
