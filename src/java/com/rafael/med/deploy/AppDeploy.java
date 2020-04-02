package com.rafael.med.deploy;

import java.util.List;

import org.apache.commons.configuration2.Configuration;

import com.rafael.med.AppBase;
import com.rafael.med.common.entity.Bnet;

import javafx.scene.Parent;
import javafx.stage.Stage;

public class AppDeploy extends AppBase 
{
	@Override
	public Parent init(Configuration configuration, Stage stage, List<String> args)
	{
		DeployView main = new DeployView();
		
		for (int i = 0; i < 10; i++)
		{
			main.addBnet(new Bnet("" + i, "127.90.90." +i));
		}
		return main;
	}
}
