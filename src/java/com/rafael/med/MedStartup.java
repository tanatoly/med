package com.rafael.med;

import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.image.BufferedImage;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.URL;

import javax.imageio.ImageIO;

import org.apache.commons.configuration2.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jfoenix.controls.JFXDecorator;
import com.rafael.med.common.Configurator;
import com.rafael.med.common.Constants;
import com.rafael.med.common.Utilities;
import com.rafael.med.common.ViewUtils;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class MedStartup extends Application
{
	private static final String CONFIG_FILE_NAME 				= "med-config.xml";

	protected static Logger log;
	

	@Override
	public void start(Stage primaryStage) throws Exception
	{
		Configuration configuration 		= Configurator.getConfiguraionByFile(CONFIG_FILE_NAME);
		
		
		
		URL fontawesome 					= ClassLoader.getSystemResource("com/rafael/med/fonts/fontawesome-webfont.ttf");
		URL f525icons 						= ClassLoader.getSystemResource("com/rafael/med/fonts/525icons.ttf");
		URL materialdesignicons 			= ClassLoader.getSystemResource("com/rafael/med/fonts/materialdesignicons-webfont.ttf");
		URL materialIcons 					= ClassLoader.getSystemResource("com/rafael/med/fonts/MaterialIcons-Regular.ttf");
		URL octicons 						= ClassLoader.getSystemResource("com/rafael/med/fonts/octicons.ttf");
		Font.loadFont(fontawesome.openStream(), 10.0d);
		Font.loadFont(f525icons.openStream(), 10.0d);
		Font.loadFont(materialdesignicons.openStream(), 10.0d);
		Font.loadFont(materialIcons.openStream(), 10.0d);
		Font.loadFont(octicons.openStream(), 10.0d);
		
		Font.loadFont(ClassLoader.getSystemResource("com/rafael/med/fonts/Roboto-Black.ttf").openStream(), 10.0d);
		Font.loadFont(ClassLoader.getSystemResource("com/rafael/med/fonts/Roboto-Regular.ttf").openStream(), 10.0d);
		
		Font.loadFont(ClassLoader.getSystemResource("com/rafael/med/fonts/Roboto-Light.ttf").openStream(), 10.0d);
		Font.loadFont(ClassLoader.getSystemResource("com/rafael/med/fonts/Roboto-Thin.ttf").openStream(), 10.0d);
		Font.loadFont(ClassLoader.getSystemResource("com/rafael/med/fonts/Roboto-Medium.ttf").openStream(), 10.0d);
		Font.loadFont(ClassLoader.getSystemResource("com/rafael/med/fonts/Roboto-Bold.ttf").openStream(), 10.0d);
		
		
		
		if(!SystemTray.isSupported())
		{
			throw new Exception("NO SYSTEM TRAY SUPPORT");
		}
		BufferedImage trayImage 		= ImageIO.read(ClassLoader.getSystemResource("com/rafael/med/images/tray.png"));
		
		if(trayImage == null)
		{
			throw new Exception("NO TRAY IMAGE LOADED");
		}
		primaryStage.getIcons().add(SwingFXUtils.toFXImage(trayImage, null));
		TrayIcon trayIcon = new TrayIcon(trayImage, "bnet software suite");
		SystemTray.getSystemTray().add(trayIcon);
			
		
		Screen primaryScreen = Screen.getPrimary();
		double screenWidth  = primaryScreen.getBounds().getWidth();
		double screenHeight = primaryScreen.getBounds().getHeight();
		BorderPane mainPane = new BorderPane();
		
		JFXDecorator decorator = new JFXDecorator(primaryStage, mainPane, false, true, true);
		decorator.setUserData(mainPane);
		decorator.setOnCloseButtonAction(() -> 
		{
			System.exit(0);
		});
	
		
		ImageView rafaelImage = new ImageView(ClassLoader.getSystemResource("com/rafael/med/images/rafael.png").toExternalForm());
		Text text = new Text("  Medical monitor");
		text.setFill(Constants.COLOR_95);
		text.setFont(Font.font(20));
		Text centeTitle = new Text("ROOM 1");
		centeTitle.setFill(Color.WHITE);
		centeTitle.setFont(Font.font(22));
		Text createTimeLabel = ViewUtils.createTimeLabel("");
		createTimeLabel.setFill(Constants.COLOR_95);
		createTimeLabel.setFont(Font.font(20));
		
		HBox top = new HBox(rafaelImage, text, ViewUtils.hspace(), centeTitle, ViewUtils.hspace(), createTimeLabel,ViewUtils.hspace(50));
		top.setAlignment(Pos.CENTER);
		HBox.setHgrow(top, Priority.ALWAYS);
		
		decorator.setGraphic(top);
		decorator.setCustomMaximize(true);
		Scene scene = new Scene(decorator, screenWidth, screenHeight);
		scene.getStylesheets().add(ClassLoader.getSystemResource("com/rafael/med/med.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.centerOnScreen();
		primaryStage.setMaximized(true);
		
		MedManager.INSTANCE.init(mainPane, centeTitle);
		
		primaryStage.show();
		
	}

	
	
	public static void main(String[] args)
	{
		try
		{
			System.out.println(	"***************************************************************\n"
							  + "***                     application starting                ***\n"
							  + "***************************************************************\n");

			Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler()
			{
				@Override
				public void uncaughtException(Thread t, Throwable e)
				{
					System.err.println("UNCAUGHT EXCEPTION - thread = " + t.getName() + "-> "  + e.getMessage());
					e.printStackTrace();
				}
			});

			//TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("UTC")));
			
			URL url = Utilities.resolveConfigResource("log4j2.xml");
			if (url != null)
			{
				System.setProperty("log4j.configurationFile", url.getFile());
			}
			else
			{
				System.out.println("log4j2.xml not found - > default of log4j logger");
			}

			log = LogManager.getLogger();
			launch(MedStartup.class, "med");
		}
		catch (Throwable e)
		{
			System.out.println("\n***************************************************************\n"
								+ "***                     application failed         	      ***\n"
								+ "***************************************************************\n");
			e.printStackTrace();
			System.exit(-1);
		}
		System.out.println("\n***************************************************************\n"
						   + "***                     application finished                ***\n"
						   + "***************************************************************\n");
	}
}
