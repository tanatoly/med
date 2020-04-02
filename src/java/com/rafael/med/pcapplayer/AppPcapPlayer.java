package com.rafael.med.pcapplayer;

import java.util.List;

import org.apache.commons.configuration2.Configuration;

import com.rafael.med.AppBase;

import javafx.scene.Parent;
import javafx.stage.Stage;

public class AppPcapPlayer extends AppBase 
{
	@Override
	public Parent init(Configuration configuration, Stage stage, List<String> args)
	{		
		PcapPlayerView main = new PcapPlayerView(this);
		return main;
	}
}
