package com.rafael.med.transfer;

import java.io.ByteArrayInputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.configuration2.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.rafael.med.common.Datagram;
import com.rafael.med.common.Datagram.Listener;

import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class ImageReceiver
{
	private static final Logger log = LogManager.getLogger();
	
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
	
	private static final int OFFSET = 700;
	
	private double time = 0;
    private GraphicsContext g;

	private double x0;
	private double y0;

	private AnimationTimer timer;
	private Timeline timeline;
	
	private List<ParticleReceiver> particles 	= new ArrayList<>();
	private List<KeyValue> values 				= new ArrayList<>();
	private ExecutorService endDelayer;
	private Datagram datagram;
	private double canvasW;
	private double canvasH;
	private byte[] receiveBuffer;
	private int receiverIndex = 0;
	private FadeTransition fadeTransition;
	private Canvas canvas;
	private ScaleTransition scaleTransition;
	private ParallelTransition parallelTransition;

	private int rxDurationSec;
	

	public ImageReceiver(AppTransfer appTransfer, Configuration configuration, GraphicsContext g, double canvasW, double canvasH, Canvas canvas)
	{
		this.g = g;
		this.canvasW = canvasW;
		this.canvasH = canvasH;
		this.canvas  = canvas;
		this.rxDurationSec = configuration.getInt("rxDurationSec", 1);
		
		
		endDelayer = Executors.newSingleThreadExecutor();
		timer = new AnimationTimer() 
        {
            @Override
            public void handle(long now)
            {
                time += 0.3;

                if (time > 3)
                {
                	g.setGlobalAlpha(1.0);
                }
                g.clearRect(0, 0, canvasW, canvasH);
                particles.forEach(p ->
                {
                    p.draw(g);
                });
            }
        };
        
        timeline = new Timeline();
        timeline.setOnFinished(event -> 
        {
        	endDelayer.execute(() -> 
        	{
          	 	try {Thread.sleep(3000);} catch (InterruptedException e) {}
          	 	timer.stop();
			});
        }); 
        receiveBuffer = new byte[5_000_000];
        
        fadeTransition = new FadeTransition(Duration.seconds(rxDurationSec),canvas);
        fadeTransition.setFromValue(0.0);
        fadeTransition.setToValue(1.0);
        fadeTransition.setAutoReverse(false);
        
        scaleTransition = new ScaleTransition(Duration.seconds(rxDurationSec));
        scaleTransition.setToX(1);
        scaleTransition.setToY(1);
        
        
        parallelTransition = new ParallelTransition(canvas, fadeTransition, scaleTransition);
        
        log.info("IMAGE RECEIVER CREATED");
   	}
	
	public void start(NetworkInterface networkInterface, InetAddress localAddress, int port) throws Exception
	{
		InetSocketAddress localSocketAddress = new InetSocketAddress(localAddress, port);
		datagram					= new Datagram(Short.MAX_VALUE * 2, ByteOrder.BIG_ENDIAN, networkInterface, localSocketAddress, null,null);
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
					g.clearRect(0, 0, canvasW, canvasH);
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
					x0 = (canvasW - image.getWidth() ) /2;
				    y0 = (canvasH - image.getHeight() ) /2;
				    onImageReceived(image);
			        //onImageReceivedFade(image);
				}
			}
		});
		log.info("IMAGE RECEIVER STARTED");
	}
	
	
	private void onImageReceivedFade(Image image) 
    {
		g.drawImage(image, x0, y0);
		canvas.setScaleX(0.1);
		canvas.setScaleY(0.1);
		parallelTransition.play();
    }
	
	private void onImageReceived(Image image) 
    {
		System.out.println(image.getWidth() + " - " + image.getHeight());
		
		log.info("ON IMAGE RECEIVE STARTED");
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
            values.add(new KeyValue(p.x, p.x.get()  + x0 + OFFSET ));
        });
        
       
        Collections.shuffle(values);
        int chunkSize = 200;
        int chunks = values.size() / chunkSize + 1;
        
        time = 0;
        timer.stop();
        timeline.stop();
      
        timeline.getKeyFrames().clear();
        
        g.setGlobalAlpha(0);
  
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
			KeyFrame e = new KeyFrame(Duration.seconds(Math.random() * rxDurationSec),array);
			timeline.getKeyFrames().add(e);
        	
        	
            //timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(Math.random() * 3),values.subList(i * chunkSize, i == chunks - 1 ? values.size() : (i+1) * chunkSize).toArray(new KeyValue[0])));
        }
        timer.start();
        timeline.play();
        log.info("ON IMAGE RECEIVE FINISHED - {} ms" ,(System.nanoTime() - b) / 1_000_000);
    
    }
}
