package com.rafael.med.transfer;

import java.io.File;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.rafael.med.AppManager;
import com.rafael.med.common.Datagram;
import com.rafael.med.common.Datagram.Listener;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

public class ImageTransmitter
{
	private static final Logger log = LogManager.getLogger();
	private static class ParticleTransmitter
	{
	    private DoubleProperty x = new SimpleDoubleProperty();
	    private DoubleProperty y = new SimpleDoubleProperty();
	    private Point2D velocity = Point2D.ZERO;
	    private Color color;
	    private double life = 1.0;
	    private boolean active = false;

	    public ParticleTransmitter(double x, double y, Color color)
	    {
	        this.x.set(x);
	        this.y.set(y);
	        this.color = color;
	    }

	    public double getX()
	    {
	        return x.get();
	    }

	    public double getY() 
	    {
	        return y.get();
	    }

	    public boolean isDead()
	    {
	        return life == 0;
	    }

	    public boolean isActive()
	    {
	        return active;
	    }

	    public void activate(Point2D velocity) 
	    {
	        active = true;
	        this.velocity = velocity;
	    }

	    public void update()
	    {
	        if (!active)
	            return;

	        life -= 0.017 * 0.75;

	        if (life < 0)
	            life = 0;

	        this.x.set(getX() + velocity.getX());
	        this.y.set(getY() + velocity.getY());
	    }

	    public void draw(GraphicsContext g) 
	    {
	        g.setFill(color);
	        g.setGlobalAlpha(life);
	        g.fillOval(getX(), getY(), 1, 1);
	    }
	}

	
	private double x0;
	private double y0;
	private List<ParticleTransmitter> particles 	= new ArrayList<>();
	private Datagram datagram;
	private double canvasW;
	private double canvasH;
	private GraphicsContext g;
	private AnimationTimer timer;
	private byte[] sendBuffer;
	private int fullSize;
	private AtomicInteger speedControl = new AtomicInteger();
	private int sendIndex;
	private long fileSize;
	private ExecutorService sender;
	private Image image;
	private  int chunkSize = 1300;
	private long txDelayMs;
	private int speedControlK;
	
	public ImageTransmitter(AppTransfer appTransfer, Configuration configuration, GraphicsContext g, double canvasW, double canvasH)
	{
		this.g = g;
		this.canvasW = canvasW;
		this.canvasH = canvasH;
		
		this.chunkSize 	= configuration.getInt("chunkSize", 1300);
		this.txDelayMs   = configuration.getInt("txDelayMs", 5);	
		this.speedControlK = configuration.getInt("speedControlK", 480);
		
		timer = new AnimationTimer() 
        {
            @Override
            public void handle(long now) 
            {
            	 g.clearRect(0, 0, canvasW, canvasH);
                 particles.removeIf(ParticleTransmitter::isDead);
                 if(particles.size() == 0)
                 {
                 	timer.stop();
                 	g.clearRect(0, 0, canvasW, canvasH);
                 	Platform.runLater(() -> {
                 		try
						{
							Thread.sleep(4000);
						}
						catch (InterruptedException e){}
                 		
                 		appTransfer.addButton.setDisable(false);
                     	appTransfer.startButton.setDisable(false);
                 		
                 	});
                 	
                 	
                 }
                 particles.parallelStream().filter(p -> !p.isActive()).sorted((p1, p2) -> (int)(p1.getX() - p2.getX())).limit(fullSize/speedControl.get()).forEach(p ->p.activate(new Point2D(Math.random() * 30, -Math.random() * 2)));
                 particles.forEach(p ->
                 {
                 	p.update();
                    p.draw(g);
                 });
            }
        };
        sender = Executors.newSingleThreadExecutor();
        log.info("IMAGE TRANSMITTER CREATED");
	}

	public void onNewFile(File selectedFile) throws Exception
	{	
		fileSize = selectedFile.length();
		
		FileInputStream is = new FileInputStream(selectedFile);
		image = new Image(is);
		is.close();
		
		sendBuffer = Files.readAllBytes(selectedFile.toPath());
		
	    x0 = (canvasW - image.getWidth() ) /2;
	    y0 = (canvasH - image.getHeight() ) /2; 
	    g.setGlobalAlpha(1.0);
	    g.drawImage(image, x0, y0);
	   
	   
	   
        log.info("IMAGE TRANSMITTER ON NEW FILE");
	}

	public void start(NetworkInterface networkInterface, InetAddress localAddress, int port, String targetHost) throws Exception
	{
		timer.stop();
		particles.clear();
		PixelReader pixelReader = image.getPixelReader();
		for (int y = 0; y < image.getHeight(); y++) 
		{
			for (int x = 0; x < image.getWidth(); x++)
			{
				Color color = pixelReader.getColor(x, y);

				if (!color.equals(Color.TRANSPARENT)) 
				{
					ParticleTransmitter p = new ParticleTransmitter(x + x0, y + y0, color);
					particles.add(p);
				}
			}
		}
	       
	    fullSize = particles.size();
		g.setGlobalAlpha(1.0);
		g.drawImage(image, x0, y0);
		speedControl.set(speedControlK);
		timer.start();
		if(datagram != null)
		{
			datagram.close();
			datagram = null;
		}
		
		if(StringUtils.isBlank(targetHost))
		{
			//AppManager.showError("TARGET HOST IS BLANK");
			return;
		}
			
		
		datagram					= new Datagram(4, ByteOrder.BIG_ENDIAN, networkInterface, new InetSocketAddress(localAddress, port), new InetSocketAddress(targetHost, port),null);
		datagram.open("transmiter");
		datagram.setListener(new Listener()
		{
			@Override
			public void onIncomingMessage(ByteBuffer message, InetSocketAddress source) throws Exception
			{
				speedControl.set(1);
				log.info("GOT ACK FROM " + source);
			}
		});
			
		sendIndex = 0;
		long chunks = fileSize / chunkSize + 1;
	
		sender.execute(new Runnable()
		{

			@Override
			public void run()
			{
				ByteBuffer buffer = ByteBuffer.allocate(chunkSize + 8);
				for (int i = 1; i <= chunks; i++)
				{
					try
					{
						Thread.sleep(txDelayMs);
						buffer.clear();
						buffer.putInt((int) chunks);
						buffer.putInt((int) i);

						if( i < chunks)
						{
							buffer.put(sendBuffer, sendIndex, chunkSize);
						}
						else
						{
							int remaining = sendBuffer.length - sendIndex;
							buffer.put(sendBuffer, sendIndex, remaining);
						}

						sendIndex = sendIndex + chunkSize;
						datagram.send(buffer, null);
					}
					catch (Exception e)
					{
						AppManager.INSTANCE.showError(AppTransfer.class,log,"ON ACTION FAILED : ",e);
					}
				}
			}
		});
		log.info("IMAGE TRANSMITTER START");
	}
	
}
