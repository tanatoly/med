package com.rafael.med.common;

import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXButton.ButtonType;
import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.controls.JFXToggleNode;
import com.jfoenix.effects.JFXDepthManager;

import de.jensd.fx.glyphs.GlyphIcons;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.utils.FontAwesomeIconFactory;
import de.jensd.fx.glyphs.icons525.Icons525;
import de.jensd.fx.glyphs.icons525.utils.Icon525Factory;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.utils.MaterialDesignIconFactory;
import de.jensd.fx.glyphs.materialicons.MaterialIcon;
import de.jensd.fx.glyphs.materialicons.utils.MaterialIconFactory;
import de.jensd.fx.glyphs.octicons.OctIcon;
import de.jensd.fx.glyphs.octicons.utils.OctIconFactory;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.StringProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.Event;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;

public class ViewUtils
{
	//private static final String IMAGES = "images/";
	private static final int ZERO = 0;
	private static final String DELIMETER = "-";
	
	private static final Map<String, Image> images = new HashMap<>();
		
	public static Region hspace()
	{
		Region space = new Region();
		HBox.setHgrow(space, Priority.ALWAYS);
		return space;
	}
	
	public static Region hspace(int width)
	{
		Region space = new Region();
		space.setPrefWidth(width);
		return space;
	}
	
	public static Region vspace()
	{
		Region space = new Region();
		VBox.setVgrow(space, Priority.ALWAYS);
		return space;
	}
	
	public static Region vspace(int height)
	{
		Region space = new Region();
		space.setPrefHeight(height);
		return space;
	}
	
	
	public static ImageView imageView(String imageName, int width, int height)
	{
		Image image = image(imageName);
		if(image != null)
		{
			ImageView imageView = new ImageView(image);
			if(width > 0)
			{
				imageView.setFitWidth(width);
			}
			if(height > 0)
			{
				imageView.setFitHeight(height);
			}
			return imageView;
		}
		return null;
	}
	
	public static final Image image(String imageName)
	{
		return image(imageName, ZERO, ZERO);
	}
	
	public static final Image image(String imageName,double width,double height)
	{
		Image image = images.get(imageName);
		if(image == null)
		{
			synchronized (images)
			{
				if(image == null)
				{
					InputStream systemResourceAsStream = ClassLoader.getSystemResourceAsStream(imageName);
					if(systemResourceAsStream != null)
					{
						if(width == ZERO || height == ZERO)
						{
							image = new Image(systemResourceAsStream);
						}
						else
						{
							image = new Image(systemResourceAsStream,width,height,false,false);
						}
						images.put(imageName, image);
					}
				}
			}
		}
		return image;
	}
	
	public static Text createTimeLabel(String string)
	{
		Text timeLabel = new Text();
		Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0), event -> timeLabel.setText(string + " " + LocalDateTime.now().format(Utilities.DATE_TIME_dd_MM_yyyy_HH_mm_ss))),new KeyFrame(Duration.seconds(1)));
		timeline.setCycleCount(Animation.INDEFINITE);
		timeline.play();
		return timeLabel;
	}
	
	
	
	public static JFXButton jfxbutton(String text, GlyphIcons glyphIcon, double width, double height, Color backgroundColor, Color fillColor, Color pressColor, String tooltip , int depth)
	{
		JFXButton button = jfxbutton(width, height, backgroundColor, pressColor, tooltip, depth);
		if(text != null)
		{
			button.setContentDisplay(ContentDisplay.TOP);
			button.setText(text);
			button.setFont(Font.font("Roboto-Bold",FontWeight.SEMI_BOLD, 9));
		}
		
		Text icon = glyphIcon(glyphIcon, String.valueOf(height * 0.4));
		icon.setFill(Color.WHITE);
		button.setGraphic(icon);
		return button;
	}
	
	
	public static JFXToggleNode jfxtogglebutton(String text, GlyphIcons glyphIcon, double width, double height, Color backgroundColor, Color fillColor, Color pressColor, String tooltip , int depth)
	{
		JFXToggleNode button = jfxtogglebutton(width, height, backgroundColor, pressColor, tooltip, depth);
		
		Text buttonText = null;
		if(text != null)
		{
			buttonText = new Text(text);
			buttonText.setFont(new Font(height/3));
		}
		else if(glyphIcon != null)
		{
			buttonText = glyphIcon(glyphIcon, String.valueOf(height * 0.7));	
		}
		
		if(buttonText != null)
		{	
			if(fillColor != null)
			{
				buttonText.setFill(fillColor);
			}
			button.setGraphic(buttonText);
		}
		return button;
	}

	public static JFXButton jfxbutton(double width, double height, Color backgroundColor, Color pressColor,String tooltip, int depth) 
	{
		JFXButton button = jfxbutton(backgroundColor, pressColor, tooltip, depth);
		  
		if(width == 0 || height == 0)
		{
			button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		}
		else
		{
			button.setMinSize(width, height);
			button.setMaxSize(width ,height);
		}
		return button;
	}
	
	public static JFXToggleNode jfxtogglebutton(double width, double height, Color backgroundColor, Color pressColor,String tooltip, int depth) 
	{
		JFXToggleNode button = jfxtogglebutton(backgroundColor, pressColor, tooltip, depth);
		  
		if(width == 0 || height == 0)
		{
			button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		}
		else
		{
			button.setMinSize(width, height);
			button.setMaxSize(width ,height);
		}
		return button;
	}

	
	public static JFXButton jfxbutton(String text, Color backgroundColor, Color pressColor, String tooltip , int depth) 
	{
		JFXButton button =  new JFXButton(text);
		button.setButtonType(ButtonType.RAISED);
		button.setFocusTraversable(false);
		button.setBackground(new Background(new BackgroundFill(backgroundColor, new CornerRadii(6), null)));
		if(tooltip != null)
		{
			button.setTooltip(new Tooltip(tooltip));
		}
		
		if(pressColor != null)
		{
			button.setRipplerFill(pressColor);
		}
		
		JFXDepthManager.setDepth(button, depth);
		return button;
	}
	
	
	public static JFXButton jfxbutton(GlyphIcons glyphIcon,  Color backgroundColor, Color fillColor, Color pressColor, String tooltip , int depth)
	{
		
		JFXButton button = jfxbutton(backgroundColor, pressColor, tooltip, depth);
		Text buttonText = glyphIcon(glyphIcon, String.valueOf(16));	
		
		
		if(buttonText != null)
		{	
			if(fillColor != null)
			{
				buttonText.setFill(fillColor);
			}
			button.setGraphic(buttonText);
		}
		
		return button;
	}
	
	
	
	public static JFXButton jfxbutton(Color backgroundColor, Color pressColor, String tooltip , int depth) 
	{
		JFXButton button =  new JFXButton();
		button.setButtonType(ButtonType.RAISED);
		button.setFocusTraversable(false);
		button.setBackground(new Background(new BackgroundFill(backgroundColor, new CornerRadii(6), null)));
		if(tooltip != null)
		{
			button.setTooltip(new Tooltip(tooltip));
		}
		
		if(pressColor != null)
		{
			button.setRipplerFill(pressColor);
		}
		
		JFXDepthManager.setDepth(button, depth);
		return button;
	}
	
	public static JFXToggleNode jfxtogglebutton(Color backgroundColor, Color pressColor, String tooltip , int depth) 
	{
		JFXToggleNode button =  new JFXToggleNode();
		button.setFocusTraversable(false);
		button.setBackground(new Background(new BackgroundFill(backgroundColor, new CornerRadii(6), null)));
		if(tooltip != null)
		{
			button.setTooltip(new Tooltip(tooltip));
		}
		
		if(pressColor != null)
		{
			button.setSelectedColor(pressColor);
		}
		
		JFXDepthManager.setDepth(button, depth);
		return button;
	}
	
	
	
	
	
	public static Label jfxLabel(String text, GlyphIcons glyphIcon, double width, double height, Color backgroundColor, Color fillColor)
	{
		Label label =  new Label();
		label.setFocusTraversable(false);
		label.setAlignment(Pos.CENTER);
		label.setBackground(new Background(new BackgroundFill(backgroundColor, new CornerRadii(6), null)));
		  
		if(width == 0 || height == 0)
		{
			label.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		}
		else
		{
			label.setMinSize(width, height);
			label.setMaxSize(width ,height);
		}
		
		Text buttonText = null;
		if(text != null)
		{
			buttonText = new Text(text);
			buttonText.setFont(new Font(height/3));
		}
		else if(glyphIcon != null)
		{
			buttonText = glyphIcon(glyphIcon, String.valueOf(height * 0.7));	
		}
		
		if(buttonText != null)
		{	
			if(fillColor != null)
			{
				buttonText.setFill(fillColor);
			}
			label.setGraphic(buttonText);
		}
		return label;
	}
	
	
	
	public static Text glyphIcon(GlyphIcons glyphIconEnum,String size)
	{
		if (glyphIconEnum instanceof FontAwesomeIcon)
		{
			return FontAwesomeIconFactory.get().createIcon(glyphIconEnum, size);	
		}
		else if (glyphIconEnum instanceof Icons525)
		{
			return Icon525Factory.get().createIcon(glyphIconEnum, size);	
		}
		else if (glyphIconEnum instanceof MaterialDesignIcon)
		{
			return MaterialDesignIconFactory.get().createIcon(glyphIconEnum, size);	
		}
		else if (glyphIconEnum instanceof MaterialIcon)
		{
			return MaterialIconFactory.get().createIcon(glyphIconEnum, size);	
		}
		else if (glyphIconEnum instanceof OctIcon)
		{
			return OctIconFactory.get().createIcon(glyphIconEnum, size);	
		}
		
		return null;
	}
	
	public static Text glyphIcon(GlyphIcons glyphIconEnum,String size, Color fillColor)
	{
		Text text = glyphIcon(glyphIconEnum, size);
		if(fillColor != null)
		{
			text.setFill(fillColor);
		}
		return text;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static void stringPropertyToByteArray(ByteBuffer byteBuffer,StringProperty stringProperty,byte[] array)
	{
		byte[] tempArray = stringProperty.get().getBytes();
		int length = Math.min(tempArray.length, array.length);
		System.arraycopy(tempArray, 0, array, 0, length);
		byteBuffer.put(array);
	}
	
	
	public static SVGPath svgPath(String svgString)
	{
		SVGPath svgPath = new SVGPath();
		svgPath.setContent(svgString);
		return svgPath;
	}
	
	public static SVGPath svgGraphic(Labeled labeled,SVGPath svgPath)
	{
		svgPath.scaleXProperty().bind(labeled.widthProperty().divide(80));
		svgPath.scaleYProperty().bind(labeled.heightProperty().divide(80));
		labeled.setGraphic(svgPath);
		return svgPath;
	}
	
	
	public static SVGPath svgGraphic(Labeled labeled,String svgString)
	{
		SVGPath svgPath = svgPath(svgString);
		svgPath.scaleXProperty().bind(labeled.widthProperty().divide(80));
		svgPath.scaleYProperty().bind(labeled.heightProperty().divide(80));
		labeled.setGraphic(svgPath);
		return svgPath;
	}
	
	
	
	
	public static <T> TreeItem<T> newTreeItem(boolean isChecked,T object)
	{
		TreeItem<T> treeItem = null;
		if(isChecked)
		{
			treeItem = new CheckBoxTreeItem<>(object);
		}
		else
		{
			treeItem = new TreeItem<>(object);
		}
		return treeItem;
	}
	
	

	
	
	
	
	

	public static void glyphLoadFonts()
	{
		FontAwesomeIconFactory.get();
		MaterialIconFactory.get();
		MaterialDesignIconFactory.get();
		OctIconFactory.get();
	}

	public static Text glyphIcon(GlyphIcons glyphIconEnum, int iconSize,Color fillColor)
	{
		Text text = new Text(glyphIconEnum.unicode());
		text.setStyle(String.format("-fx-font-family: %s; -fx-font-size: %d; -fx-focus-traversable:false", glyphIconEnum.fontFamily(), iconSize));
		text.setFill(fillColor);
		return text;
	}

	public static Text glyphIconButton(GlyphIcons glyphIconEnum, int iconSize, Color fillColor, Runnable action)
	{
		Text text = glyphIcon(glyphIconEnum, iconSize, fillColor);
		text.getStyleClass().add("glyph-icon-button");
		if(action != null)
		{
			text.setOnMousePressed( e ->
			{
				e.consume();
			});
			text.setOnMouseReleased( e ->
			{
				e.consume();
				action.run();
			});
		}
		return text;
	}
//
//	public static Label glyphIconButtonWithText(GlyphIcons glyphIconEnum,  int iconSize, String text, Font textFont, Color fillColor)
//	{
//		Text icon = new Text(glyphIconEnum.unicode());
//		icon.setStyle(String.format("-fx-font-family: %s; -fx-font-size: %d; -fx-focus-traversable:false", glyphIconEnum.fontFamily(), iconSize));
//
//
//		Label label = new Label(text + " ", icon);
//		if(fillColor != null)
//		{
//			icon.setFill(fillColor);
//			label.setTextFill(fillColor);
//
//		}
//		if(textFont != null)
//		{
//			label.setFont(textFont);
//		}
//		icon.getStyleClass().add("glyph-icon-with-text");
//		label.getStyleClass().add("glyph-icon-with-text");
//		return label;
//	}
//	
	
	
	
	
	
	
	
//	public static Label newLabel(String text,GlyphIcons glyphIconEnum, double width, double height,String tooltip,double fontSize,boolean isDepth)
//	{
//		Label label =  new Label();
//		label.setId("standardLabel");
//		if(StringUtils.isNotBlank(tooltip))
//		{
//			label.setTooltip(new Tooltip(tooltip));
//		}
//		
//		
//		
//		label.setFocusTraversable(false);
//		label.setMinSize(width, height);
//		label.setMaxSize(width ,height);
//		label.setFont(Font.font(null, FontWeight.BOLD, fontSize));
//		
//		if(glyphIconEnum != null)
//		{
//			Text icon = glyphIcon(glyphIconEnum, String.valueOf(height * 0.7));	
//			if(icon != null)
//			{
//				icon.setId("standardText");
//				label.setGraphic(icon);
//			}
//		}
//		if(isDepth)
//		{
//			JFXDepthManager.setDepth(label, 1);
//		}
//		return label;
//	}
//	
	
	
	
	
	public static void fireMouseClick(Node eventTarget)
	{
		Event.fireEvent(eventTarget, new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, MouseButton.PRIMARY, 1, true, true, true, true, true, true, true, true, true, true, null));
		
	}
	
	public static String showInputTextDialog(TextInputDialog textInputDialog,String title,String capture,AlertType alertType)
	{
		textInputDialog.setTitle(title);
		
		textInputDialog.getDialogPane().setContentText(capture);
		Optional<String> result = textInputDialog.showAndWait();	
		if(result.isPresent())
		{
			return result.get();
		}
		return null;
	}
	
	
//	public static Unit showInputTextAndComboDialog(ObservableList<GroupModel> groupTypes, ObservableList<NodeModel> nodeModels,String title,String capture, com.rafael.roip.nms.model.unit.Unit.Type unitType,AlertType alertType)
//	{
//		Dialog<ButtonType> dialog = new Dialog<>();
//		dialog.setTitle("New unit");
//		//dialog.initStyle(StageStyle.UNDECORATED);
//		dialog.initModality(Modality.APPLICATION_MODAL);
//		dialog.initOwner(null);
//		DialogPane dialogPane = dialog.getDialogPane();
//		
//		
//		HBox h = new HBox(10);
//		Label idLabel = new Label("Id ");
//		Label nameLabel = new Label("Name ");
//		Label typeLabel = new Label("Type ");
//		TextField nameTextField = new TextField();
//		TextField nodeIdTextField = new TextField();
//		ComboBox<GroupModel> unitsComboBox = null;
//		ComboBox<NodeModel> nodesComboBox = null;
//		if(unitType == com.rafael.roip.nms.model.unit.Unit.Type.GROUP)
//		{
//			unitsComboBox = new ComboBox<GroupModel>(groupTypes);
//			h.getChildren().addAll(nameLabel,nameTextField,typeLabel,unitsComboBox);
//		}
//		else if(unitType == com.rafael.roip.nms.model.unit.Unit.Type.NODE)
//		{
//			nodesComboBox = new ComboBox<NodeModel>(nodeModels);
//			h.getChildren().addAll(idLabel, nodeIdTextField,nameLabel, nameTextField, typeLabel,nodesComboBox);
//		}
//		
//		dialogPane.setContent(h);
//		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
//		Unit unit = null;
//		Optional<ButtonType> result = dialog.showAndWait();
//		if(result.isPresent())
//		{
//			ButtonType buttonType = result.get();
//			if(buttonType == ButtonType.OK)
//			{
//				String unitName = nameTextField.getText();
//				
//				if(unitType == com.rafael.roip.nms.model.unit.Unit.Type.GROUP)
//				{
//					GroupModel groupModel = unitsComboBox.getValue();
//					unit = new Group(unitName, groupModel.name.get(),groupModel.imageName);
//				}
//				else if(unitType == com.rafael.roip.nms.model.unit.Unit.Type.NODE)
//				{
//					NodeModel nodeModel = nodesComboBox.getValue();
//					unit = new Node(nodeIdTextField.getText(),unitName, nodeModel.getDevices(),nodeModel.name.get(),nodeModel.imageName);
//				}
//			}
//			return unit;
//		}
//		
//		return null;
//	}
	
	
	public static void showAlertWrongPassword()
	{
		Alert alert =  new Alert(AlertType.WARNING);
		alert.setTitle("Credentials error");
		alert.setContentText("Wrong username or password !");
		
		alert.showAndWait();

	}
	
	
	
//	private static Alert createAlert(AlertType type)
//	{
//		Alert alert = new Alert(type);
//		alert.initModality(Modality.APPLICATION_MODAL);
//		alert.initOwner(null);
//		
//		
//		return alert;
//	}
//

//	private static Dialog<ButtonType> createExceptionDialog(TextArea exceptionDialogTextArea)
//	{
//		Dialog<ButtonType> dialog = new Dialog<>();
//		dialog.setTitle("Program error");
//		//dialog.initStyle(StageStyle.UNDECORATED);
//		DialogPane dialogPane = dialog.getDialogPane();
//		dialogPane.setContentText("Details of the problem");
//		dialogPane.getButtonTypes().add(ButtonType.OK);
//		dialog.initModality(Modality.APPLICATION_MODAL);

//		Label label = new Label("Exception stacktrace:");
//		
//		
//		exceptionDialogTextArea.setEditable(false);
//		exceptionDialogTextArea.setWrapText(true);
//		
//		exceptionDialogTextArea.setMaxWidth(Double.MAX_VALUE);
//		exceptionDialogTextArea.setMaxHeight(Double.MAX_VALUE);
//		GridPane.setVgrow(exceptionDialogTextArea, Priority.ALWAYS);
//		GridPane.setHgrow(exceptionDialogTextArea, Priority.ALWAYS);
//		
//		GridPane root = new GridPane();
//		root.setVisible(false);
//		root.setMaxWidth(Double.MAX_VALUE);
//		
//		root.add(label, 0, 0);
//		root.add(exceptionDialogTextArea, 0, 1);
//		dialogPane.setExpandableContent(root);
//		
//		
//		return dialog;
//	}


	

	public static NumberStringConverter createNumberStringConverter()
	{
		NumberStringConverter stringConverter = new NumberStringConverter()
		{			

			@Override
			public Number fromString(String string)
			{
				if (string.matches("\\d+")) {
					return super.fromString(string);
				}
				else {				
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Input Error");
					alert.setHeaderText("Only numbers are allowed for this field");
					alert.showAndWait();
					return 0;
				}
			}
		}; 
		return stringConverter;
	}

	public static <E extends Enum<E>> StringConverter<E> createEnumStringConverter(Class<E> clazz)
	{
		StringConverter<E> stringConverter = new StringConverter<E>()
		{			
			@Override
			public String toString(E object) {
				return object.name();
			}

			@Override
			public E fromString(String string) {
				return E.valueOf(clazz,string);
			}
		}; 
		return stringConverter;
	}

	
	
//	public static String createUnitFilename(Unit unit)
//	{
//		String filename = "not-defined";
//		if (unit instanceof Mission)
//		{
////			filename = "mission_" + unit.nameProperty().get() + "_" + System.currentTimeMillis() + ".xml";
//			filename = unit.nameProperty().get() + ".xml";
//		}
//		else if (unit instanceof Group)
//		{
////			filename = "group_" + unit.nameProperty().get() + "_" + System.currentTimeMillis() + ".xml";
//			filename = unit.nameProperty().get() + ".xml";
//		}
//		else if (unit instanceof com.rafael.roip.nms.model.unit.Node)
//		{
////			filename = "node_" + unit.nameProperty().get() + "_" + System.currentTimeMillis() + ".xml";
//			filename = unit.nameProperty().get() +  ".xml";
//		}
//		return filename;
//	}


//	public static boolean showAlertForSelection(Alert confirmationAlert,Unit unit,String action)
//	{
//		boolean isContinue = false;
//		if(unit != null)
//		{
//			confirmationAlert.getDialogPane().setContentText("You will do " + action + " on " + unit.toString() + ".\nAre you sure ?");
//			Optional<ButtonType> choice = confirmationAlert.showAndWait();
//			isContinue =  choice.get() == ButtonType.OK;
//		}
//		return isContinue;
//	}
	

	public static void error(Logger log, String message,Throwable exception,Object... objects)
	{
		if(log != null)
		{
			log.error(message,objects,exception);
		}
		else
		{
			System.err.println(message);
			if(exception != null)
			{
				exception.printStackTrace();
			}
		}
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		
		exception.printStackTrace(printWriter);
		printWriter.close();
		//exceptionDialogTextArea.setText(stringWriter.toString());
		//exceptionDialog.showAndWait();
	}
	

	
	
	
	public static String toRgbCode(Color color)
	{
		return String.format("#%02X%02X%02X", (int)(color.getRed()*255),(int)(color.getGreen()*255),(int)(color.getBlue()*255));
	}
	
	
	public static ScrollBar getNodeScrollBar(Node target, Orientation orientation)
	{
		for(Node node : target.lookupAll(".scroll-bar"))
		{
			if (node instanceof ScrollBar)
			{
				ScrollBar scrollBar = (ScrollBar) node;
				if(orientation == scrollBar.getOrientation())
				{
					return scrollBar;
				}
			}
		}
		return null;
	}

//	public static StringProperty buildTextRow(String text,TitledPane titledPane,boolean isEditable)
//	{
//		TextField textField = new TextField();
//		textField.setFocusTraversable(false);
//		textField.editableProperty().set(isEditable);
//		
//		Region spacer = new Region();
//		HBox.setHgrow(spacer, Priority.ALWAYS);
//		
//		Text text1 = new Text(text);
//		text1.setFill(Color.WHITE);
//		
//		HBox box = new HBox();
//		box.setPadding(new Insets(0, 20, 0, 20));
//		box.setAlignment(Pos.CENTER_LEFT);
//		box.getChildren().addAll(text1,spacer,textField);
//		
//		
//		
//		titledPane.setContent(box);
//		return textField.textProperty().b;
//	}
	
	public static Scene createFullScene(Stage stage, Screen screen, Parent mainNode, BufferedImage trayImage) throws Exception
	{
		double sceneWidth 	= screen.getBounds().getWidth();
		double sceneHeight 	= screen.getBounds().getHeight();
		
		JFXDecorator decorator = new JFXDecorator(stage, mainNode);
		decorator.setUserData(mainNode);
		decorator.setOnCloseButtonAction(new Runnable()
		{
			@Override
			public void run()
			{
				System.exit(0);
			}
		});
		decorator.setCustomMaximize(true);
		Scene scene = new Scene(mainNode,sceneWidth,sceneHeight);
		stage.setScene(scene);
		stage.centerOnScreen();
		stage.setX(screen.getBounds().getMinX());
		stage.setY(screen.getBounds().getMinY());
		stage.setWidth(screen.getBounds().getWidth());
		stage.setHeight(screen.getBounds().getHeight());
		stage.initStyle(StageStyle.UNDECORATED);
//		stage.setFullScreen(true);
		
		if(trayImage != null)
		{
			stage.getIcons().add(SwingFXUtils.toFXImage(trayImage, null));
			TrayIcon trayIcon = Utilities.createTrayIcon(trayImage);
			SystemTray.getSystemTray().add(trayIcon);
		}
		return scene;
	}
}
