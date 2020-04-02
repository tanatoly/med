package com.rafael.med.transfer;

import java.io.ByteArrayInputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;

import com.rafael.med.common.Datagram;
import com.rafael.med.common.Datagram.Listener;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ImageReceiverDemo extends Application 
{
	
	 private static final int OFFSET = 700;

	private class ParticleReceiver
	 {
	    public final DoubleProperty x = new SimpleDoubleProperty();
	    public final DoubleProperty y = new SimpleDoubleProperty();
	    public final Color color;

	    public ParticleReceiver(double x, double y, Color color) 
	    {
	        this.x.set(x);
	        this.y.set(y);
	        this.color = color;
	    }

	    public void draw(GraphicsContext g) 
	    {
	        g.setFill(color);
	        g.fillOval(x.get(), y.get(), 1, 1);
	    }
	}
	
	
    private static final int HEIGHT = 800;

	private static final int WIDTH = 1400;

	private double time = 0;

    private GraphicsContext g;

    private List<ParticleReceiver> particles =	 new ArrayList<>();
    private List<KeyValue> values 				= new ArrayList<>();

	private double x0;

	private double y0;
	
	
//	private Image image;

	private Canvas canvas;

	private AnimationTimer timer;

	private Timeline timeline;
	

	private byte[] receiveBuffer;
	private int receiverIndex = 0;
    private void start(Image image) 
    { 	
         long b = System.nanoTime();
    	
    	particles.clear();
    	values.clear();
    	
    	
    	
    	PixelReader pixelReader = image.getPixelReader();
        for (int y = 0; y < image.getHeight(); y++) 
        {
            for (int x = 0; x < image.getWidth(); x++)
            {
                Color color = pixelReader.getColor(x, y);

                if (!color.equals(Color.TRANSPARENT)) 
                {
                    ParticleReceiver p = new ParticleReceiver(x - OFFSET, y + y0, color);
                    particles.add(p);
                }
            }
        }
        
        particles.forEach(p -> 
        {
            values.add(new KeyValue(p.x, p.x.get()  + x0 + 700));
        });
        
       
        Collections.shuffle(values);
        int chunkSize = 200;
        int chunks = values.size() / chunkSize + 1;
        
        System.out.println("values.size = " + values.size() + " " + chunks);
       
        time = 0;
        timer.stop();
        timeline.stop();
      
       
        
        timeline.getKeyFrames().clear();
        
       
        
        g.setGlobalAlpha(0);
  
        System.out.println("1-> " + (System.nanoTime() - b ) / 1_000_000);
        System.out.println("chunks.size - "  + chunks);
        
        
        for (int i = 0; i < chunks; i++) 
        {
            int fromIndex = i * chunkSize;
			int toIndex = i == chunks - 1 ? values.size() : (i+1) * chunkSize;
						
			int size = toIndex - fromIndex;
			KeyValue[] array = new KeyValue[size];
			for (int j = 0; j < array.length; j++)
			{
				array[j] = values.get(fromIndex + j);
			}
			
	
			
			KeyFrame e = new KeyFrame(Duration.seconds(Math.random() * 4),array);
			timeline.getKeyFrames().add(e);
        }
        
        System.out.println("2-> " + (System.nanoTime() - b ) / 1_000_000);
        timer.start();
     	timeline.play();
     	System.out.println("3-> " + (System.nanoTime() - b ) / 1_000_000);
    }

    

    private void update() 
    {
        g.clearRect(0, 0, WIDTH, HEIGHT);
        particles.forEach(p ->
        {
            p.draw(g);
        });
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
    	 Pane root = new Pane();
         canvas = new Canvas(WIDTH, HEIGHT);
         root.getChildren().add(canvas);
         g = canvas.getGraphicsContext2D();
        
    	
         timer = new AnimationTimer() 
         {
             @Override
             public void handle(long now)
             {
                 time += 0.3;

                 if (time > 1)
                 {
                 	g.setGlobalAlpha(1.0);
                 }
                 update();
             }
         };
         
         timeline = new Timeline();
         timeline.setOnFinished(event -> 
         {
        	Executors.newSingleThreadExecutor().execute(() -> 
        	{
           	 	try {Thread.sleep(2000);} catch (InterruptedException e) {}
           	 	timer.stop();
			});
         });
         
         //----------------------
         receiveBuffer = new byte[5_000_000];
     	InetSocketAddress localSocketAddress = new InetSocketAddress(InetAddress.getLocalHost(), 43789);
     	Datagram datagram					= new Datagram(Short.MAX_VALUE * 2, ByteOrder.BIG_ENDIAN, null, localSocketAddress, null,null);
		datagram.open("receiver");
		
		datagram.setListener(new Listener()
		{
			@Override
			public void onIncomingMessage(ByteBuffer buffer, InetSocketAddress source) throws Exception
			{
				int size 	= buffer.getInt();
				int count	= buffer.getInt();
				
				if(count == 1)
				{
					g.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
				}
				
				int remaining = buffer.remaining();
				buffer.get(receiveBuffer, receiverIndex, remaining);
				
				receiverIndex = receiverIndex + remaining;
				
				if(count == size)
				{
					datagram.send(ByteBuffer.allocate(4), source);
					ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(receiveBuffer, 0, receiverIndex);
					receiverIndex = 0;
					Image image = new Image(byteArrayInputStream);
					x0 = (canvas.getWidth() - image.getWidth() ) /2;
				    y0 = (canvas.getHeight() - image.getHeight() ) /2;
				    start(image);
				}
			}
		});
		
		//----------------------
         
        //canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> start());
    	
        Scene scene = new Scene(root,WIDTH,HEIGHT);
        primaryStage.setTitle("Disintegration App");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) 
    {
        launch(args);
    }
}
