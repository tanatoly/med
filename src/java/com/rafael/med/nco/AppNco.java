package com.rafael.med.nco;

import java.util.List;

import org.apache.commons.configuration2.Configuration;

import com.rafael.med.AppBase;

import javafx.scene.Parent;
import javafx.stage.Stage;

public class AppNco extends AppBase
{
	@Override
	public Parent init(Configuration configuration, Stage stage, List<String> args)
	{	
		NcoView ncoView 		= new NcoView(stage);
		NcoManager.INSTANCE.init(configuration, ncoView, args);	
		return ncoView;
	}
}
