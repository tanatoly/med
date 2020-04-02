package com.rafael.med.wave;

import java.io.File;
import java.util.List;

import org.apache.commons.configuration2.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXDrawer.DrawerDirection;
import com.rafael.med.AppBase;
import com.rafael.med.AppManager;
import com.rafael.med.common.ViewUtils;
import com.jfoenix.controls.JFXDrawersStack;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.beans.value.ChangeListener;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class AppWave extends AppBase
{
	private static final Logger log = LogManager.getLogger();
	
	
	
	
	
	private Pane 			firstView;
	private TestPane 		testPane;
	public Label 			testNameLabel;


	private JFXDrawersStack cardPane;
	private JFXDrawer drawer;


	private MosTablePane mosTablePane;
	
	@Override
	public Parent init(Configuration configuration, Stage stage, List<String> args) 
	{
		BorderPane main = new BorderPane();
		main.setStyle("-fx-background-color :  -color-30;");		
		StackPane center		= new StackPane();		
		BorderPane.setMargin(center, new Insets(0, 4, 4, 4));
		center.setStyle("-fx-background-color :  -color-40;");
		main.setCenter(center);
	    firstView					= createFirstView();
	    testPane = new TestPane(this);
	    center.setPadding(new Insets(6));	    
	    
	    
	    mosTablePane = new MosTablePane();
	   
		StackPane drawerPane 		= new StackPane(mosTablePane);
		cardPane = new JFXDrawersStack();
		((JFXDrawersStack)cardPane).setContent(testPane);
		drawer = new JFXDrawer();
		drawer.setDirection(DrawerDirection.RIGHT);
		drawer.setSidePane(drawerPane);
		drawer.setOverLayVisible(false);
		drawer.setResizableOnDrag(false);
		cardPane.setUserData(drawer);
		((JFXDrawersStack)cardPane).toggle(drawer, false);
	    
	    center.getChildren().addAll(firstView,cardPane);	
		StackPane.setAlignment(testPane, Pos.CENTER);
		testPane.setVisible(false);
		firstView.setVisible(true);
		
		Pane top = createTop();
		main.setTop(top);
		
		stage.widthProperty().addListener((ChangeListener<Number>) (observable, oldValue, newValue) -> 
		{
			drawer.setDefaultDrawerSize((double) newValue);
		});
		
		return main;
	}

	
	private Pane createTop() 
	{
		GridPane top = new GridPane();
		BorderPane.setMargin(top, new Insets(4));
		top.setPadding(new Insets(4, 0, 0, 0));
		
		Text expandedTrue 			= ViewUtils.glyphIcon(FontAwesomeIcon.CHEVRON_DOWN, String.valueOf(40 * 0.7),Color.WHITE);
		Text expandedFalse	 		= ViewUtils.glyphIcon(FontAwesomeIcon.CHEVRON_RIGHT, String.valueOf(40 * 0.7),Color.WHITE);
		JFXButton expandedButton 	= ViewUtils.jfxbutton(60,40,Color.BLACK,Color.AQUA, null, 2);
		expandedButton.setGraphic(expandedFalse);	
		expandedButton.setVisible(false);
		expandedButton.setOnAction( e-> 
		{
			testPane.onExpandedAction(expandedButton, expandedTrue, expandedFalse);
		});
		
		
		testNameLabel = new Label();
		testNameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 30));
		GridPane.setHgrow(testNameLabel, Priority.ALWAYS);

		JFXButton chooseButton = ViewUtils.jfxbutton(null, FontAwesomeIcon.FOLDER_OPEN_ALT, 60, 40, Color.BLACK,Color.WHITE, Color.AQUA,null,2);		
		JFXButton closeButton 		= ViewUtils.jfxbutton(null, FontAwesomeIcon.CLOSE, 60, 40, Color.BLACK,Color.WHITE, Color.AQUA,null,2);

		closeButton.setVisible(false);
		
		chooseButton.setOnAction(e -> 
		{
			try
			{
				File selectedir =  AppManager.INSTANCE.showDirectoryChooser(AppWave.class);
				if(selectedir != null && selectedir.isDirectory() && selectedir.exists())
				{
					WaveManager.INSTANCE.reset(selectedir, testPane, closeButton, mosTablePane,cardPane,chooseButton,expandedButton,closeButton);
					testNameLabel.setText(selectedir.toPath().getFileName().toString());
					firstView.setVisible(false);
					testPane.setVisible(true);
					expandedButton.setVisible(true);
				}
			}
			catch (Exception ex)
			{
				AppManager.INSTANCE.showError(AppWave.class, log, "ON ACTION ERROR : ", ex);
			}
		});
		
	
		
		closeButton.setOnAction(e -> 
		{
			try
			{
				
				((JFXDrawersStack)cardPane).toggle((JFXDrawer) cardPane.getUserData(),false);
				chooseButton.setDisable(false);
				expandedButton.setDisable(false);
				closeButton.setVisible(false);
			}
			catch (Exception ex)
			{
				AppManager.INSTANCE.showError(AppWave.class, log, "ON ACTION ERROR : ", ex);
			}
		});
		
		
		RowConstraints r = new RowConstraints();
		r.setPercentHeight(100);
		ColumnConstraints c1 = new ColumnConstraints();
		c1.setPercentWidth(10);
		ColumnConstraints c2 = new ColumnConstraints();
		c2.setPercentWidth(80);
		top.getRowConstraints().add(r);
		top.getColumnConstraints().addAll(c1,c2,c1);
		
		HBox buttons = new HBox(20, closeButton, chooseButton);
		buttons.setAlignment(Pos.CENTER_RIGHT);
		GridPane.setConstraints(expandedButton, 	0, 0, 1, 1, HPos.LEFT, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
		GridPane.setConstraints(testNameLabel, 		1, 0, 1, 1, HPos.CENTER,  VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
		GridPane.setConstraints(buttons, 			2, 0, 1, 1, HPos.RIGHT,  VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
		//GridPane.setConstraints(chooseButton, 		3, 0, 1, 1, HPos.RIGHT,  VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
		
		top.getChildren().addAll(expandedButton,   testNameLabel ,buttons);
		return top;
	}

	private Pane createFirstView()
	{
		Label label = new Label("Add folder with test recording");
		label.setStyle("-fx-font-size: 60;-fx-text-fill: white;");
		BorderPane pane = new BorderPane(label);
		return pane;
	}
}
