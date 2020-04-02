package com.rafael.med;

import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.rafael.med.common.Constants;
import com.rafael.med.common.ViewUtils;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class MedManager 
{
	public static final MedManager INSTANCE = new MedManager();
	
	private JFXDialog 										dialog;
	private JFXDialogLayout 								dialogLayout;
	private Text 											dialogHead;
	private Text 											dialogBody;
	private Button 											okButton;


	public void init(BorderPane mainPane, Text title) 
	{
		mainPane.setBackground(Constants.BACKGOUND_10);
		
		StackPane center = new StackPane();
		BorderPane.setMargin(center, new Insets(5));
		center.setBackground(Constants.BACKGOUND_90);
		
		VBox pane = new VBox(40);
		BorderPane.setMargin(pane, new Insets(5));
		pane.setBackground(Constants.BACKGOUND_60);
		pane.setAlignment(Pos.TOP_CENTER);
		pane.getChildren().add(ViewUtils.vspace(0));
		for (int i = 1; i <= 6; i++) 
		{
			
			String str = "מרכז בקרה " + i;
			Button button = ViewUtils.jfxbutton(str, FontAwesomeIcon.BUILDING, 70, 70, Constants.COLOR_20, Color.GHOSTWHITE, Color.AQUA, "",2);
			pane.getChildren().add(button);
			GridPane gridPane = createPane();
			center.getChildren().add(gridPane);
			
			button.setOnAction(e ->
			{
				title.setText(str);
				ObservableList<Node> children = center.getChildren();
				for (Node node : children)
				{
					node.setVisible(node == gridPane);
				}
			});
			
		}
		
		
		pane.getChildren().add(ViewUtils.vspace());
		Button button = ViewUtils.jfxbutton("הגדרות", FontAwesomeIcon.COG, 70, 70, Constants.COLOR_20, Color.GHOSTWHITE, Color.AQUA, "",2);
		pane.getChildren().add(button);		
		pane.getChildren().add(ViewUtils.vspace());
		pane.setMaxWidth(110);
		pane.setMinWidth(110);
		
		
		mainPane.setRight(pane);
		mainPane.setCenter(center);
		
		
		dialog = new JFXDialog();
		
		dialogLayout = new JFXDialogLayout();
		dialogLayout.setBackground(Constants.BACKGOUND_20);
		dialogLayout.prefWidthProperty().bind(mainPane.widthProperty().multiply(0.8));
		dialogLayout.prefHeightProperty().bind(mainPane.heightProperty().multiply(0.8));
		dialog.setContent(dialogLayout);
		dialog.setDialogContainer(center);
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
	
	private GridPane createPane()
	{
		GridPane pane = new GridPane();
		//pane.setGridLinesVisible(true);
		int rows = 3;
		int columns = 7;
		
		for (int i = 0; i < rows; i++) 
		{
			RowConstraints rowConstraints = new RowConstraints();
			rowConstraints.setFillHeight(true);
			rowConstraints.setVgrow(Priority.ALWAYS);
			pane.getRowConstraints().add(rowConstraints);
		}
		
		for (int i = 0; i < columns; i++)
		{
			ColumnConstraints columnConstraints = new ColumnConstraints();
			columnConstraints.setFillWidth(true);
			columnConstraints.setHgrow(Priority.ALWAYS);
			pane.getColumnConstraints().add(columnConstraints);
		}
		
		for (int j = 0; j < rows; j++)
		{
			for (int i = 0; i < columns; i++)
			{
				ThinModule thinModule = new ThinModule();
				thinModule.setBackground(Constants.BACKGOUND_10);
				GridPane.setMargin(thinModule, new Insets(1));
				pane.add(thinModule,i,j);
			}
		}
		
		return pane;
	}
	
	public void showCard()
	{
		dialogHead.setText("INFO");
		dialogHead.setFill(Color.LIME);
		//dialogBody.setText(message);
		dialog.show();
	}
}
