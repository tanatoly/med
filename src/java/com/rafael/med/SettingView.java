package com.rafael.med;

import javafx.scene.control.ScrollPane;

public class SettingView extends ScrollPane implements CenterView
{

	@Override
	public void update(boolean isToFront)
	{
		if(MedManager.INSTANCE.isSlowUpdate())
		{
			
		}
		
	}

	public void goToBed(Bed bed) {
		// TODO Auto-generated method stub
		
	}
	
}
