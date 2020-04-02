package com.rafael.med;

import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.URL;
import java.time.ZoneId;
import java.util.List;
import java.util.TimeZone;

import javax.imageio.ImageIO;

import org.apache.commons.configuration2.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXDrawer.DrawerDirection;
import com.rafael.med.common.Configurator;
import com.rafael.med.common.Utilities;
import com.rafael.med.common.ViewUtils;
import com.jfoenix.controls.JFXDrawersStack;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class AtStartup extends Application
{
	private static final int SIDE_WIDTH 						= 300;
	private static final String CONFIG_FILE_NAME 				= "at-config.xml";

	protected static Logger log;
	

	@Override
	public void start(Stage primaryStage) throws Exception
	{
		Configuration configuration 		= Configurator.getConfiguraionByFile(CONFIG_FILE_NAME);
		Screen screen 						= Screen.getPrimary();
		double sceneHeight 					= screen.getVisualBounds().getHeight();
		
		Configuration release 				= Configurator.getConfiguraionByFile("version.properties");
		String version 						= release.getString("version", "dev");
		
		URL fontawesome 					= ClassLoader.getSystemResource("com/rafael/at/fonts/fontawesome-webfont.ttf");
		URL f525icons 						= ClassLoader.getSystemResource("com/rafael/at/fonts/525icons.ttf");
		URL materialdesignicons 			= ClassLoader.getSystemResource("com/rafael/at/fonts/materialdesignicons-webfont.ttf");
		URL materialIcons 					= ClassLoader.getSystemResource("com/rafael/at/fonts/MaterialIcons-Regular.ttf");
		URL octicons 						= ClassLoader.getSystemResource("com/rafael/at/fonts/octicons.ttf");
		Font.loadFont(fontawesome.openStream(), 10.0d);
		Font.loadFont(f525icons.openStream(), 10.0d);
		Font.loadFont(materialdesignicons.openStream(), 10.0d);
		Font.loadFont(materialIcons.openStream(), 10.0d);
		Font.loadFont(octicons.openStream(), 10.0d);
		
		Font.loadFont(ClassLoader.getSystemResource("com/rafael/at/fonts/Roboto-Black.ttf").openStream(), 10.0d);
		Font.loadFont(ClassLoader.getSystemResource("com/rafael/at/fonts/Roboto-Regular.ttf").openStream(), 10.0d);
		
		Font.loadFont(ClassLoader.getSystemResource("com/rafael/at/fonts/Roboto-Light.ttf").openStream(), 10.0d);
		Font.loadFont(ClassLoader.getSystemResource("com/rafael/at/fonts/Roboto-Thin.ttf").openStream(), 10.0d);
		Font.loadFont(ClassLoader.getSystemResource("com/rafael/at/fonts/Roboto-Medium.ttf").openStream(), 10.0d);
		Font.loadFont(ClassLoader.getSystemResource("com/rafael/at/fonts/Roboto-Bold.ttf").openStream(), 10.0d);
		
		
		
		Platform.setImplicitExit(false);
		
		StackPane empty = new StackPane();
		empty.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
		
		
		JFXDrawer rightDrawer = new JFXDrawer();
		StackPane content = new StackPane();
		rightDrawer.setDirection(DrawerDirection.RIGHT);
		rightDrawer.setDefaultDrawerSize(SIDE_WIDTH);
		rightDrawer.setSidePane(content);
		rightDrawer.setOverLayVisible(false);
		rightDrawer.setResizableOnDrag(false);
		rightDrawer.setOnDrawerClosed(e -> primaryStage.hide());
		JFXDrawersStack drawersStack = new JFXDrawersStack();
		drawersStack.setContent(empty);
		
	    final Scene scene = new Scene(drawersStack,SIDE_WIDTH,sceneHeight,Color.TRANSPARENT);
		scene.getStylesheets().add(ClassLoader.getSystemResource("com/rafael/at/at.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.setX(screen.getBounds().getMaxX() - SIDE_WIDTH);
		primaryStage.setY(screen.getBounds().getMinY());
		primaryStage.setWidth(300);
		primaryStage.setHeight(sceneHeight);
		primaryStage.initStyle(StageStyle.TRANSPARENT);
		primaryStage.setOnHidden(e -> rightDrawer.close());
		primaryStage.setAlwaysOnTop(true);
		
		
		Toolkit.getDefaultToolkit();
		if(!SystemTray.isSupported())
		{
			throw new Exception("NO SYSTEM TRAY SUPPORT");
		}
		BufferedImage trayImage 		= ImageIO.read(ClassLoader.getSystemResource("com/rafael/at/images/bnet1.png"));
		if(trayImage == null)
		{
			throw new Exception("NO TRAY IMAGE LOADED");
		}
		primaryStage.getIcons().add(SwingFXUtils.toFXImage(trayImage, null));
		TrayIcon trayIcon = new TrayIcon(trayImage, "bnet software suite");
		SystemTray.getSystemTray().add(trayIcon);
			
		trayIcon.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e) 
			{
				if(e.getClickCount() == 1)
				{
					Platform.runLater(() -> 
					{
						if(!primaryStage.isShowing())
						{
							primaryStage.show();
							primaryStage.toFront();
						}
						drawersStack.toggle(rightDrawer);						
					});
				}
			}
		});
		
		
		content.setStyle("-fx-background-color :  -color-40;");
		BorderPane borderPane = new BorderPane();
		content.getChildren().add(borderPane);
		
		GridPane top = new GridPane();
		BorderPane.setMargin(top, new Insets(4));
		
		RowConstraints r1 = new RowConstraints();
		r1.setPercentHeight(25);
		RowConstraints r2 = new RowConstraints();
		r2.setPercentHeight(50);
		top.getRowConstraints().addAll(r1,r1,r2);
		
		ColumnConstraints c = new ColumnConstraints((SIDE_WIDTH - 8) /4);
		top.getColumnConstraints().addAll(c,c,c,c);
		
		top.setMaxHeight(120);
		top.setMinHeight(120);
		
		borderPane.setTop(top);
		
		top.setStyle("-fx-background-color :  -color-80;");
		
		ImageView rImage 	= ViewUtils.imageView("com/rafael/at/images/rafael.png", 0, 34);
		Text timeLabel = ViewUtils.createTimeLabel("UTC");
		timeLabel.setStyle("-fx-font-size: 14px; -fx-font-family: \"Arial Black\"; -fx-fill: white; -fx-effect :innershadow(one-pass-box, rgba(150,150,150,0.6) , 10 , 0.0 , 0 , 1);");

		Text logo 			= new Text("BNET SOFTWARE SUITE (" + version + ")");
		logo.setStyle("-fx-font-size: 13px; -fx-font-family: \"Arial Black\"; -fx-fill: white; -fx-effect :innershadow(one-pass-box, rgba(200,200,200,0.6) , 10 , 0.0 , 0 , 1);");

		ImageView bnetImage 	= ViewUtils.imageView("com/rafael/at/images/bnet.png", 120, 60);
		
		
		JFXButton hideButton = ViewUtils.jfxbutton(null, FontAwesomeIcon.ARROW_RIGHT, 50, 50, Color.GREEN, Color.WHITE, Color.AQUAMARINE, "hide", 2);
		hideButton.setOnAction(e -> rightDrawer.close());
		JFXButton closeButton = ViewUtils.jfxbutton(null, FontAwesomeIcon.CLOSE, 50, 50, Color.RED, Color.WHITE, Color.AQUAMARINE, "exit", 2);
		closeButton.setOnAction(e -> System.exit(0));
		
		GridPane.setConstraints(rImage, 			0, 0, 1, 1, HPos.CENTER,VPos.CENTER);
		GridPane.setConstraints(timeLabel, 			1, 0, 3, 1 ,HPos.CENTER,VPos.CENTER);
		GridPane.setConstraints(logo, 				0, 1, 4, 1 ,HPos.CENTER,VPos.CENTER);
		GridPane.setConstraints(hideButton, 		0, 2, 1, 1, HPos.CENTER,VPos.CENTER);
		GridPane.setConstraints(bnetImage, 			1, 2, 2, 1, HPos.CENTER,VPos.CENTER);
		GridPane.setConstraints(closeButton, 		3, 2, 1, 1, HPos.CENTER,VPos.CENTER);
		
		top.getChildren().addAll(rImage,timeLabel,logo,hideButton,closeButton,bnetImage);
		
		
		GridPane appsPane = new GridPane();
		appsPane.setAlignment(Pos.CENTER);
		borderPane.setCenter(appsPane);
		appsPane.setStyle("-fx-background-color :  -color-60;");
		
				
		Parameters parameters = getParameters();
		List<String> args = parameters.getRaw();
		AppManager.INSTANCE.init(rightDrawer, appsPane, version,configuration, args);
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

			TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("UTC")));
			
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
			launch(AtStartup.class, "at");
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
