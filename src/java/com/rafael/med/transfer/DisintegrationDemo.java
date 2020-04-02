package com.rafael.med.transfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

public class DisintegrationDemo extends Application 
{
	
	private enum TYPE
	{
		RANDOM_10,UP ,DOWN,LEFT,RIGHT,COPY_X,COPY_RANDOM,RANDOM_SIDE_10;
	}

    private static final int HEIGHT = 800;

	private static final int WIDTH = 1400;

	private double time = 0;

    private GraphicsContext g;

    private List<Particle> particles = new ArrayList<>();
    private int fullSize;

	private double x0;

	private double y0;
	
	private TYPE type = TYPE.COPY_RANDOM;

    private Parent createContent() 
    {
        Pane root = new Pane();

        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        root.getChildren().add(canvas);

        g = canvas.getGraphicsContext2D();

        Image image = new Image(getClass().getResource("armor.png").toExternalForm());
        
       this.x0 = (canvas.getWidth() - image.getWidth() ) /2;
       this.y0 = (canvas.getHeight() - image.getHeight() ) /2;
       
        
        
        
        g.drawImage(image, x0, y0);

        long b = System.nanoTime();
        disintegrate(image);
        System.out.println((System.nanoTime() - b ) / 1_000_000);

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
//                time += 0.017;
//
//                if (time > 2)
                    update();
            }
        };
        
        
        
       // timer.start();

        
        
        
        
        Timeline timeline = new Timeline();
        timeline.setDelay(Duration.seconds(2));
//        timeline.setCycleCount(5);
//        timeline.setAutoReverse(true);

        List<KeyValue> values = new ArrayList<>();

        particles.forEach(p -> {
            values.add(new KeyValue(p.xProperty(), p.getX() - x0 + 100));
        });

        if(type == TYPE.COPY_RANDOM)
        {
        	Collections.shuffle(values);
        }

        int chunkSize = 50;
        int chunks = values.size() / chunkSize + 1;

        for (int i = 0; i < chunks; i++) {
            timeline.getKeyFrames().add(
                    new KeyFrame(Duration.seconds(Math.random() * 3),
                            values.subList(i * chunkSize, i == chunks - 1 ? values.size() : (i+1) * chunkSize).toArray(new KeyValue[0]))
            );
        }

        
        
        
        
        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, 
                new EventHandler<MouseEvent>(){
 
            @Override
            public void handle(MouseEvent event) {
            	
            	timer.start();
            	if(type == TYPE.COPY_X || type == TYPE.COPY_RANDOM)
            	{
            		timeline.play();
            	}
 
            }
        });

        return root;
    }
    

    private void disintegrate(Image image)
    {
        PixelReader pixelReader = image.getPixelReader();

        for (int y = 0; y < image.getHeight(); y++) 
        {
            for (int x = 0; x < image.getWidth(); x++)
            {
                Color color = pixelReader.getColor(x, y);

                if (!color.equals(Color.TRANSPARENT)) 
                {
                    Particle p = new Particle(x + x0, y + y0, color);
                    particles.add(p);
                }
            }
        }

        fullSize = particles.size();
    }

    private void update() 
    {
        g.clearRect(0, 0, WIDTH, HEIGHT);
        particles.removeIf(Particle::isDead);

        switch (type) {
		case RANDOM_10:
		
			particles.parallelStream().filter(p -> !p.isActive())
			.forEach(p -> 
			{
				Point2D velocity = new Point2D(Math.random() * 10, -Math.random() * 10);
				p.activate(velocity);
			});
			break;
		case RANDOM_SIDE_10:
			 particles.parallelStream()
            .filter(p -> !p.isActive())
            .sorted((p1, p2) -> 
            {
					int i = (int)(p1.getX() - p2.getX());
					return i;
				})
            .limit(fullSize / 60 / 4 )
            .forEach(p ->
            {
            	Point2D velocity = new Point2D(Math.random() * 20, -Math.random() * 20);
					p.activate(velocity);
				});
			break;	
			
		
			
		case RIGHT:
			 particles.parallelStream()
             .filter(p -> !p.isActive())
             .sorted((p1, p2) -> 
             {
					int i = (int)(p1.getX() - p2.getX());
					return i;
				})
             .limit(fullSize / 60 / 2 )
             .forEach(p ->
             {
					Point2D point2d = new Point2D(Math.random() - 0.5, Math.random() - 0.5);
					p.activate(point2d.multiply(-1));
				});
			break;
			
		case LEFT:
			 particles.parallelStream()
            .filter(p -> !p.isActive())
            .sorted((p1, p2) -> 
            {
					int i = (int)(p2.getX() - p1.getX());
					return i;
				})
            .limit(fullSize / 60 / 2 )
            .forEach(p ->
            {
					Point2D point2d = new Point2D(Math.random() - 0.5, Math.random() - 0.5);
					p.activate(point2d.multiply(-1));
				});
			break;
			
		case UP:
			 particles.parallelStream()
           .filter(p -> !p.isActive())
           .sorted((p1, p2) -> 
           {
					int i = (int)(p2.getY() - p1.getY());
					return i;
				})
           .limit(fullSize / 60 / 2 )
           .forEach(p ->
           {
					Point2D point2d = new Point2D(Math.random() - 0.5, Math.random() - 0.5);
					p.activate(point2d.multiply(-1));
				});
			break;
			
			
		case DOWN:
			 particles.parallelStream()
          .filter(p -> !p.isActive())
          .sorted((p1, p2) -> 
          {
					int i = (int)(p1.getY() - p2.getY());
					return i;
				})
          .limit(fullSize / 60 / 2 )
          .forEach(p ->
          {
					Point2D point2d = new Point2D(Math.random() - 0.5, Math.random() - 0.5);
					p.activate(point2d.multiply(-1));
				});
			break;
			
		case COPY_X:
			 
			break;
			
		case COPY_RANDOM:
			 
			break;

		default:
			break;
		}
        
        
       

        particles.forEach(p ->
        {
        	if( !(type == TYPE.COPY_X || type == TYPE.COPY_RANDOM))
        	{
        		p.update();
        	}
            p.draw(g);
        });
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        Scene scene = new Scene(createContent(),WIDTH,HEIGHT);
        primaryStage.setTitle("Disintegration App");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) 
    {
        launch(args);
    }
}
