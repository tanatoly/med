package com.rafael.med;



import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.Logger;

import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;

import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


public abstract class AppBase
{
	private JFXDialog 										dialog;
	private JFXDialogLayout 								dialogLayout;
	private Text 											dialogHead;
	private Text 											dialogBody;
	private Button 											okButton;
	
	private DirectoryChooser 								directoryChooser;	
	private FileChooser 									fileChooser;
	boolean isLanNeeded;
	Button appButton;
	
	protected Stage stage;
	
		
	protected AppBase() 
	{
		this.stage = new Stage();
		this.stage.setMaximized(true);
		this.stage.centerOnScreen();
		this.stage.setOnCloseRequest( e -> stage.hide());
		
		
		//this.stage.setTitle(title);
	}
	
	
	
	
	public void postInit(StackPane stackPane)
	{
		directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Choose folder");
		directoryChooser.setInitialDirectory(SystemUtils.getUserHome());
		
		
		fileChooser = new FileChooser();
		fileChooser.setTitle("Choose file");
		fileChooser.setInitialDirectory(SystemUtils.getUserHome());
		
		
		dialog = new JFXDialog();
		dialogLayout = new JFXDialogLayout();
		dialogLayout.setStyle("-fx-background-color : -color-70;");
		dialog.setContent(dialogLayout);
		dialog.setDialogContainer(stackPane);
		dialog.setOverlayClose(false);

		dialogHead = new Text();
		dialogHead.setStyle("-fx-fill : white;-fx-font-size: 18px;");
		dialogLayout.setHeading(dialogHead);
		dialogBody = new Text();
		dialogBody.setStyle("-fx-fill : white;-fx-font-size: 20px;");
		dialogLayout.setBody(dialogBody);
		okButton = new Button("OK");
		
		
		dialogLayout.setActions(okButton);
		okButton.setOnAction(event ->
		{
			dialog.close();
		});
	}
	
	
	File showDirectoryChooser()
	{
		return directoryChooser.showDialog(null);
	}
	
	File showOpenFile() 
	{
		return fileChooser.showOpenDialog(null);
		
	}
	
	File showSaveFile() 
	{
		return fileChooser.showSaveDialog(null);
	}
	
	List<File> showOpenMultiple()
	{
		return fileChooser.showOpenMultipleDialog(null);
	}

	
	void showError(Logger log, String message, Throwable throwable) 
	{
		if(StringUtils.isBlank(message))
		{
			message = "GENERAL ERROR : ";
		}
		
		if(throwable != null)
		{
			message = message + throwable.getMessage();
		}
		
		if(log != null)
		{
			log.error(message,throwable);
		}
		else
		{
			System.err.println(message);
			if(throwable != null)
			{
				throwable.printStackTrace();
			}
		}
		
		dialogHead.setText("ERROR");
		dialogHead.setFill(Color.RED);
		
		dialogBody.setText(message);
		dialog.show();
	}


	void showInfo(String message) 
	{
		dialogHead.setText("INFO");
		dialogHead.setFill(Color.LIME);
		dialogBody.setText(message);
		dialog.show();
	}
	
	
	
	
	
	public abstract Parent init(Configuration configuration, Stage stage, List<String> args);


}
