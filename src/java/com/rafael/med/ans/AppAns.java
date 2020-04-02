package com.rafael.med.ans;


import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.lang3.SystemUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.rafael.med.AppBase;
import com.rafael.med.common.Constants;
import com.rafael.med.common.ViewUtils;

import javafx.animation.AnimationTimer;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


public class AppAns extends AppBase
{
	private static final String COUNT_CLEAR = "COUNT CLEAR";
	private static final String OPEN_XML 			= "OPEN XML";
	private static final String SAVE 				= "SAVE ";
	private static final String SAVE_AS 			= "SAVE AS";
	private static final String ERROR = "ERROR";
	private static final String ANS 				= "ANS";
	private static final String CANCEL 				= "CANCEL";
	private static final String NEW 				= "NEW";
	private static final String ALL 			= "ALL EXE";
	public GridPane viewPane;
	private int row = 0;



	private ChannelDataView channelDataView;
	private Button addButton;
	private Button cancelButton;
	private Button saveButton;
	private Alert alert;
	private TextArea alertContent;
	private Button allButton;
	private Button loadOpenXmlButton;


	private File configFile;
	private Button saveAsButton;
	private Button countClearButton;
	
	private List<ChannelData> list = new ArrayList<>();
	private BorderPane progressPane;
	private ProgressBar progressBar;
	
	
	
	@Override
	public Parent init(Configuration configuration, Stage stage, List<String> args)
	{
		
		if(args != null && !args.isEmpty())
		{
			String configFileName = args.get(0);
			this.configFile = Paths.get(configFileName).toFile();
		}

		BorderPane scenePane = new BorderPane();
		scenePane.setBackground(Constants.BACKGOUND_20);
		
		
		ViewUtils.glyphLoadFonts();
		HBox top 	= new HBox(20);
		top.setPadding(new Insets(6));
		top.setBackground(Constants.BACKGOUND_60);
		Label label = new Label(ANS);
		label.setStyle("-fx-text-fill: white;-fx-font-size: 18px;-fx-font-weight:700;");


		loadOpenXmlButton	= new Button(OPEN_XML);
		loadOpenXmlButton.setPrefSize(100, 26);

		saveButton = ViewUtils.jfxbutton(SAVE, Color.BLACK, Color.AQUA, null, 2);
		saveButton.setPrefSize(100, 26);
		saveButton.setDisable(true);

		saveAsButton = ViewUtils.jfxbutton(SAVE_AS, Color.BLACK, Color.AQUA, null, 2);
		saveAsButton.setPrefSize(100, 26);
	//	saveAsButton.setDisable(true);

		countClearButton = ViewUtils.jfxbutton(COUNT_CLEAR, Color.BLACK, Color.AQUA, null, 2);
		countClearButton.setPrefSize(140, 26);
		countClearButton.setDisable(true);

		allButton		= ViewUtils.jfxbutton(ALL, Color.BLACK, Color.AQUA, null, 2);;
		allButton.setPrefSize(80, 26);
		allButton.setDisable(true);

		addButton		= ViewUtils.jfxbutton(NEW, Color.BLACK, Color.AQUA, null, 2);
		addButton.setPrefSize(80, 26);

		cancelButton		= ViewUtils.jfxbutton(CANCEL, Color.BLACK, Color.AQUA, null, 2);
		cancelButton.setPrefSize(80, 26);


		top.getChildren().addAll(loadOpenXmlButton,saveButton,saveAsButton,ViewUtils.hspace(),countClearButton,allButton,ViewUtils.hspace(),cancelButton,addButton);
		BorderPane.setMargin(top, new Insets(5, 10, 5, 10));
		scenePane.setTop(top);

		viewPane = new GridPane();
		ColumnConstraints columnConstraints = new ColumnConstraints();
		columnConstraints.setFillWidth(true);
		columnConstraints.setHgrow(Priority.ALWAYS);
		viewPane.getColumnConstraints().add(columnConstraints);



		ScrollPane scrollPane = new ScrollPane(viewPane);
		scrollPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);

		scrollPane.setFitToHeight(true);
		scrollPane.setFitToWidth(true);

		//viewPane.setStyle("-fx-background-color: #0b5868;");
		channelDataView	= new ChannelDataView();
		//channelDataView.setStyle("-fx-background-color: #0f7185;");
		channelDataView.setVisible(false);

		progressBar = new ProgressBar(0.0);
		progressBar.setMaxSize(300, 40);
		progressBar.setMinSize(300, 40);

		progressPane = new BorderPane(progressBar);
		progressPane.setStyle("-fx-background-color: #0f7185;");
		StackPane center = new StackPane(scrollPane,channelDataView,progressPane);
		center.setBackground(Constants.BACKGOUND_40);
		progressPane.setVisible(false);
		//center.setStyle("-fx-background-color: transparent;-fx-effect : innershadow(two-pass-box, gray , 4, 0.5, 0, 0 );");
		scenePane.setCenter(center);

		BorderPane.setMargin(center, new Insets(5, 10, 10, 10));

		alert = new Alert(AlertType.ERROR);
		alert.setHeaderText(ERROR);
		alertContent = new TextArea();
		alert.getDialogPane().setExpandableContent(new ScrollPane(alertContent));

		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(Paths.get(SystemUtils.USER_DIR).toFile());

		loadXml(configFile);

		loadOpenXmlButton.setOnAction(e ->
		{
			File file = fileChooser.showOpenDialog(stage);
			loadXml(file);
		});

		saveButton.setOnAction(e ->
		{
			saveToXml(this.configFile);
		});


		saveAsButton.setOnAction(e ->
		{
			File file = fileChooser.showSaveDialog(stage);
			saveToXml(file);
		});


		allButton.setOnAction(e ->
		{
			for (ChannelData channelData : list)
			{
				channelData.view.onStartChannel();
			}
		});

		addButton.setOnAction(e ->
		{

			if(addButton.getText().equals(NEW))
			{
				openChannelDataView(null);
				channelDataView.setData(null);
			}
			else
			{
				String errorText = channelDataView.validate();
				if(errorText == null) // validate ok
				{
					channelDataView.setVisible(false);
					viewPane.setVisible(true);
					addButton.setText(NEW);
					addOrUpdateChannel(channelDataView.getData());
					cancelButton.setDisable(true);
					saveButton.setDisable(false);
				}
				else
				{
					showError(errorText,null);
				}

			}
		});

		countClearButton.setOnAction(e ->
		{
			for (ChannelData channelData : list)
			{
				channelData.txCount.set(0);
				channelData.rxCount.set(0);
				channelData.httpAvr.set(0);
				channelData.httpSum.set(0);
			}

//			String name = "sender-";
//			int localPort = 30000;
//
//			for (int i = 1; i <= 150; i++)
//			{
//				try
//				{
//					ChannelData channelData = new ChannelData(name + i, "", ByteOrder.BIG_ENDIAN, NetworkInterface.getByName("eth3"),InetAddress.getByName("100.3.114.54") , String.valueOf(localPort + i), Type.SENDING_ONLY,  "", "100.3.114.54", "40000", "2000", "", SendType.PERIODIC, "100");
//					channelData.init();
//					list.add(channelData);
//					addOrUpdateChannel(channelData);
//				}
//				catch (Exception e1)
//				{
//					e1.printStackTrace();
//				}
//			}
//			saveAsButton.setDisable(false);
//			saveButton.setDisable(false);
//			allButton.setDisable(false);
		});

		cancelButton.setDisable(true);
		cancelButton.setOnAction(e ->
		{
			channelDataView.setVisible(false);
			viewPane.setVisible(true);
			addButton.setText(NEW);
			cancelButton.setDisable(true);
			saveButton.setDisable(saveButton.isDisable());
		});



		AnimationTimer animationTimer = new AnimationTimer()
		{
			@Override
			public void handle(long now)
			{
				ObservableList<Node> children = viewPane.getChildren();
				for (Node node : children)
				{
					ChannelView channelView = (ChannelView) node;
					channelView.repaint();
				}
			}
		};
		animationTimer.start();
		
		
		return scenePane;
	}

	private void showError(String errorText, Exception e)
	{
		
		if(e != null)
		{
			alertContent.setText(errorText + e.getMessage());
		}
		else
		{
			alertContent.setText(errorText);
		}
		alert.showAndWait();
	}


	public void openChannelDataView(ChannelData channelData)
	{
		cancelButton.setDisable(false);
		saveButton.setDisable(true);
		channelDataView.setVisible(true);
		viewPane.setVisible(false);
		addButton.setText("OK");
		channelDataView.setData(channelData);
	}

	private void addOrUpdateChannel(ChannelData channelData)
	{
		if(channelData.view == null) // adding new channel
		{
			ChannelView channelView = new ChannelView(AppAns.this);
			channelData.view = channelView;
			viewPane.add(channelView,0,++row);
		}
		channelData.view.updateData(channelData);
	}

	public void closeChannel(ChannelData channelData)
	{
		if(channelData != null)
		{
			viewPane.getChildren().remove(channelData.view);
			channelData.destroy();
		}
	}


	private void loadXml(File file)
	{
		try
		{
			if(file != null && file.exists())
			{

				for (ChannelData channelData : list)
				{
					if(channelData != null)
					{
						viewPane.getChildren().remove(channelData.view);
					}
				}

				progressBar.setProgress(0.0);
				progressPane.setVisible(true);
				loadOpenXmlButton.setDisable(true);
				addButton.setDisable(true);
				saveAsButton.setDisable(true);
				saveButton.setDisable(true);
				allButton.setDisable(true);
				countClearButton.setDisable(true);

				Task<Void> task = new Task<Void>()
				{

					@Override
					protected Void call() throws Exception
					{

						for (ChannelData channelData : list)
						{
							if(channelData != null)
							{
								channelData.destroy();
								channelData = null;
							}
						}


						list.clear();
						InputStream inputStream = Files.newInputStream(file.toPath());
						DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
						Document document = documentBuilder.parse(inputStream);
						Element rootElement 	= document.getDocumentElement();
						NodeList channelsList		= rootElement.getElementsByTagName("channel");

						for (int j = 0; j < channelsList.getLength(); j++)
						{
							org.w3c.dom.Node channelNode = channelsList.item(j);
							if (channelNode instanceof Element)
							{
								Element channelElement = (Element) channelNode;
								ChannelData channelData = ChannelData.fromXmlElement(channelElement);
								channelData.init();
								list.add(channelData);
								//addOrUpdateChannel(channelData);
								updateProgress(j, channelsList.getLength());
							}
						}
						return null;
					}
				};
				progressBar.progressProperty().bind(task.progressProperty());

				task.setOnSucceeded(new EventHandler<WorkerStateEvent>()
				{
					@Override
					public void handle(WorkerStateEvent event)
					{
						progressPane.setVisible(false);
						progressBar.progressProperty().unbind();
						for (ChannelData channelData : list)
						{
							if(channelData != null)
							{
								addOrUpdateChannel(channelData);
							}
						}
						configFile = file;
						saveAsButton.setDisable(false);
						saveButton.setDisable(false);
						allButton.setDisable(false);
						countClearButton.setDisable(false);
						loadOpenXmlButton.setDisable(false);
						addButton.setDisable(false);
					}
				});

				task.setOnFailed(new EventHandler<WorkerStateEvent>()
				{
					@Override
					public void handle(WorkerStateEvent event)
					{
						showError("FAILED LOAD XML", new Exception(""));
					}
				});
				new Thread(task).start();
			}
		}
		catch (Exception e)
		{
			showError("FAILED LOAD CONFIG FILE '" + file + "'.",e);
		}
		
	}



	public void saveToXml(File file)
	{
		try
		{
			if(file != null)
			{
				Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
				Element rootElement = document.createElement("config");
				rootElement.setAttribute("date",LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss")));
				document.appendChild(rootElement);
				for (Node node : viewPane.getChildren())
				{
					ChannelView channelView = (ChannelView) node;
					Element channelElement = document.createElement("channel");
					channelView.channelData.toXmlElement(document, channelElement);
					rootElement.appendChild(channelElement);
				}


				FileOutputStream fileOutputStream = new FileOutputStream(file);
				DOMSource domSource = new DOMSource(document);
				StreamResult streamResult = new StreamResult(fileOutputStream);
				Transformer transformer = TransformerFactory.newInstance().newTransformer();
				transformer.setOutputProperty(OutputKeys.METHOD, "xml");
				transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
				transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

				transformer.transform(domSource, streamResult);
			}
		}
		catch (Exception e)
		{
			showError("FAILED SAVE CONFIG FILE '" + file + "'.",e);
		}
		
		
	}
	
	
//	@Override
//	public void stop() throws Exception
//	{
//		for (Node node : viewPane.getChildren())
//		{
//			ChannelView channelView = (ChannelView) node;
//			channelView.channelData.destroy();
//		}
//	}
}