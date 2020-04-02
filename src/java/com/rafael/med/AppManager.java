package com.rafael.med;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jfoenix.controls.JFXDrawer;
import com.rafael.med.common.Utilities;
import com.rafael.med.common.ViewUtils;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.geometry.HPos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class AppManager
{
	private static final Logger log = LogManager.getLogger();
	
	
	private static final String LIB_DIR = SystemUtils.USER_DIR + File.separator + "lib";
	private static final String BIN_DIR = SystemUtils.USER_DIR + File.separator + "bin";
	
	public static final AppManager INSTANCE = new AppManager();

	private AppManager() {}
	private JFXDrawer rightDrawer;
	
	
	private Map<Class<? extends AppBase>, AppBase> map = new HashMap<>();
		
	public void init(JFXDrawer rightDrawer, GridPane appsPane, String version, Configuration configuration, List<String> args) throws Exception 
	{
		this.rightDrawer  = rightDrawer;
		
		int columns	= 3;
		int rows 	= 10;

		for (int i = 0; i < rows; i++)
		{
			RowConstraints rowConstraints = new RowConstraints();
			rowConstraints.setFillHeight(true);
			rowConstraints.setVgrow(Priority.ALWAYS);
			appsPane.getRowConstraints().add(rowConstraints);
		}

		for (int j = 0; j < columns; j++)
		{
			ColumnConstraints columnConstraints = new ColumnConstraints();
			columnConstraints.setFillWidth(true);
			columnConstraints.setPercentWidth(100/columns);
			columnConstraints.setHgrow(Priority.ALWAYS);
			appsPane.getColumnConstraints().add(columnConstraints);
		}

		
		appsPane.add(createShelfApp("wireshark", 		"wireshark.png", 				"WiresharkPortable.exe"), 	0, 0);
		appsPane.add(createShelfApp("winscp", 			"winscp.png", 					"WinSCP.exe")			,	1, 0);
		appsPane.add(createShelfApp("putty", 			"putty.png", 					"PuTTYPortable.exe")	,	2, 0);
		

		int row 			= 0;
		int column 			= 0;
		Object list 	= configuration.getProperty("apps.app[@class]");
		if (list instanceof Collection<?>) 
		{
			Collection<?> appList = (Collection<?>) list;
			int count 			= 0;
			for (Object object : appList) 
			{
				String className 	= (String) object;
				String title 		= (String) configuration.getProperty("apps.app(" + count + ")[@title]");
				String icon 		= (String) configuration.getProperty("apps.app(" + count + ")[@icon]");
				String isLanNeeded 	= (String) configuration.getProperty("apps.app(" + count + ")[@isLanNeeded]");
				
				@SuppressWarnings("unchecked")
				Class<? extends AppBase> clazz 			= (Class<? extends AppBase>) Class.forName((String) className);
				AppBase app 							= clazz.getDeclaredConstructor().newInstance();
				app.isLanNeeded							= Boolean.parseBoolean(isLanNeeded);
//				Button appButton 						= createApp(app,title, icon, configuration);
				Button appButton 						= ViewUtils.jfxbutton(null, FontAwesomeIcon.valueOf(icon), 50, 50, Color.BLACK, Color.WHITE, Color.AQUAMARINE, title, 2);
				appButton.setMinSize(70, 70);
				appButton.setMaxSize(70 ,70);
				
				app.appButton = appButton;
				
				GridPane.setHalignment(appButton, HPos.CENTER);
				AtomicBoolean isCreated	= new AtomicBoolean(false);
				
				appButton.setOnAction( e -> 
				{
					if(isCreated.compareAndSet(false, true))
					{
						Parent mainNode = app.init(configuration, app.stage, args);
						StackPane baseStack = new StackPane(mainNode);
						app.postInit(baseStack);
						
						Scene scene = new Scene( baseStack );
						scene.getStylesheets().add(ClassLoader.getSystemResource("com/rafael/at/at.css").toExternalForm());
						app.stage.setScene(scene);
						app.stage.setTitle(title);
					}
					app.stage.setMaximized(true);
					app.stage.centerOnScreen();
					app.stage.show();
					app.stage.toFront();
					rightDrawer.close();
				});
				
				
				if(count %3 == 0)
				{
					column = 0;
					row++;
				}
				else
				{
					column++;
				}
				count++;
				appsPane.add(appButton, column	, row);
				map.put(clazz, app);
			}
		}	
		
		
		if(!Utilities.isLanExists())
		{
			for (AppBase appBase : map.values())
			{
				appBase.appButton.setDisable(appBase.isLanNeeded);
			}
			
			ScheduledExecutorService newSingleThreadScheduledExecutor = Executors.newSingleThreadScheduledExecutor();
			newSingleThreadScheduledExecutor.scheduleAtFixedRate(() -> 
			{
				boolean isLanExists = Utilities.isLanExists();
				for (AppBase appBase : map.values())
				{
					boolean isDisable = appBase.isLanNeeded && !isLanExists;
					appBase.appButton.setDisable(isDisable);
				}
			},1000, 1000, TimeUnit.MILLISECONDS);
		}
	}
	
	
	private Button createShelfApp(String zipName, String imageNameWithsuffix, String exeNameWithSuffix)
	{
		Button button = new Button(null, ViewUtils.imageView("com/rafael/at/images/" + imageNameWithsuffix, 50, 50));
		button.setTooltip(new Tooltip(zipName));
				
		button.setOnAction(e -> 
		{
			try
			{
				String command = findOrCreate(zipName).getAbsolutePath() + File.separator + exeNameWithSuffix;
				Runtime.getRuntime().exec(command);
				rightDrawer.close();
			}
			catch (Exception ex)
			{
				log.error("ON ACTION ERROR : ",ex);
			}
		});
		
		GridPane.setHalignment(button, HPos.CENTER);
		return button;
	}
	
	private File findOrCreate(String appName) throws Exception
	{
		File appDir = new File(BIN_DIR + File.separator + appName);
		if(appDir == null || !appDir.exists() || !appDir.isDirectory())
		{
			File libDir = new File(LIB_DIR);
			if(libDir == null || !libDir.exists() || !libDir.isDirectory())
			{
				throw new IllegalStateException("Not found directory " + LIB_DIR);
			}
			String[] files = libDir.list();
			for (int i = 0; i < files.length; i++)
			{
				if(files[i].equalsIgnoreCase(appName + ".zip"))
				{
					File zipFile = new File(libDir,appName + ".zip");
					Utilities.extractZipStream(zipFile, BIN_DIR);
					return appDir;
				}
			}
			throw new IllegalStateException("Not found zip file '" + appName + ".zip in dir = " + LIB_DIR);
		}
		return appDir;
	}
	
	
	public void showError(Class<? extends AppBase> clazz, Logger log, String message, Throwable throwable)
	{
		AppBase app = map.get(clazz);
		app.showError(log, message, throwable);
	}
	
	public void showInfo(Class<? extends AppBase> clazz, String info)
	{
		AppBase app = map.get(clazz);
		app.showInfo(info);
	}
	
	public File showDirectoryChooser(Class<? extends AppBase> clazz)
	{
		AppBase app = map.get(clazz);
		return app.showDirectoryChooser();
	}
	
	public File showOpenFile(Class<? extends AppBase> clazz)
	{
		AppBase app = map.get(clazz);
		return app.showOpenFile();
	}
	
	
	public File showSaveFile(Class<? extends AppBase> clazz)
	{
		AppBase app = map.get(clazz);
		return app.showSaveFile();
	}
	
	public List<File> showOpenMultiple(Class<? extends AppBase> clazz)
	{
		AppBase app = map.get(clazz);
		return app.showOpenMultiple();
	}
}

