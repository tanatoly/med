package com.rafael.med.common;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class Constants
{
	public static final int PCM_PAYLOAD_TYPE 				= 11;
	public static final int PCM_PAYLOAD_LEN 				= 120;
	public static final int ILBC_20_PAYLOD_TYPE 			= 101;
	public static final int ILBC_30_PAYLOD_TYPE 			= 102;
	
	public static final int PCAP_GLOBAL_HEADER_LENGTH 		= 24;
	public static final int PCAP_PACKET_HEADER_LENGTH 		= 16;
	public static final int PCAP_UDP_HEADER_LENGTH			= 14 + 20 + 8;
	
	
	public static final ZoneOffset	ZONE_OFFSET				= OffsetDateTime.now().getOffset();
	
	
	
	public static Font FONT_8 		= Font.font("Roboto", FontWeight.NORMAL, 8.0);
	public static Font FONT_9 		= Font.font("Roboto", FontWeight.NORMAL, 9.0);
	public static Font FONT_10 		= Font.font("Roboto", FontWeight.NORMAL, 10.0);
	public static Font FONT_11 		= Font.font("Roboto", FontWeight.NORMAL, 11.0);
	public static Font FONT_12 		= Font.font("Roboto", FontWeight.NORMAL, 12.0);
	public static Font FONT_13 		= Font.font("Roboto", FontWeight.NORMAL, 13.0);
	public static Font FONT_14 		= Font.font("Roboto", FontWeight.NORMAL, 14.0);
	public static Font FONT_15 		= Font.font("Roboto", FontWeight.NORMAL, 15.0);
	public static Font FONT_16 		= Font.font("Roboto", FontWeight.NORMAL, 16.0);
	public static Font FONT_17 		= Font.font("Roboto", FontWeight.NORMAL, 17.0);
	public static Font FONT_18 		= Font.font("Roboto", FontWeight.NORMAL, 18.0);
	public static Font FONT_19 		= Font.font("Roboto", FontWeight.NORMAL, 19.0);
	public static Font FONT_20 		= Font.font("Roboto", FontWeight.NORMAL, 20.0);
	public static Font FONT_21 		= Font.font("Roboto", FontWeight.NORMAL, 21.0);
	public static Font FONT_22 		= Font.font("Roboto", FontWeight.NORMAL, 22.0);
	public static Font FONT_23 		= Font.font("Roboto", FontWeight.NORMAL, 23.0);
	public static Font FONT_24 		= Font.font("Roboto", FontWeight.NORMAL, 24.0);
	

	public static Color COLOR_05 			= com.sun.javafx.util.Utils.deriveColor(Color.BLACK, 0.05);
	public static Color COLOR_10 			= com.sun.javafx.util.Utils.deriveColor(Color.BLACK, 0.10);
	public static Color COLOR_15 			= com.sun.javafx.util.Utils.deriveColor(Color.BLACK, 0.15);
	public static Color COLOR_20 			= com.sun.javafx.util.Utils.deriveColor(Color.BLACK, 0.20);
	public static Color COLOR_25 			= com.sun.javafx.util.Utils.deriveColor(Color.BLACK, 0.25);
	public static Color COLOR_30 			= com.sun.javafx.util.Utils.deriveColor(Color.BLACK, 0.30);
	public static Color COLOR_35 			= com.sun.javafx.util.Utils.deriveColor(Color.BLACK, 0.35);
	public static Color COLOR_40 			= com.sun.javafx.util.Utils.deriveColor(Color.BLACK, 0.40);
	public static Color COLOR_45 			= com.sun.javafx.util.Utils.deriveColor(Color.BLACK, 0.45);
	public static Color COLOR_50 			= com.sun.javafx.util.Utils.deriveColor(Color.BLACK, 0.50);
	public static Color COLOR_55 			= com.sun.javafx.util.Utils.deriveColor(Color.BLACK, 0.55);
	public static Color COLOR_60 			= com.sun.javafx.util.Utils.deriveColor(Color.BLACK, 0.60);
	public static Color COLOR_65 			= com.sun.javafx.util.Utils.deriveColor(Color.BLACK, 0.65);
	public static Color COLOR_70 			= com.sun.javafx.util.Utils.deriveColor(Color.BLACK, 0.70);
	public static Color COLOR_75 			= com.sun.javafx.util.Utils.deriveColor(Color.BLACK, 0.75);
	public static Color COLOR_80 			= com.sun.javafx.util.Utils.deriveColor(Color.BLACK, 0.80);
	public static Color COLOR_85 			= com.sun.javafx.util.Utils.deriveColor(Color.BLACK, 0.85);
	public static Color COLOR_90 			= com.sun.javafx.util.Utils.deriveColor(Color.BLACK, 0.90);
	public static Color COLOR_95 			= com.sun.javafx.util.Utils.deriveColor(Color.BLACK, 0.95);
	

	
	public static Background BACKGOUND_BASE 		= new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY));
	public static Background BACKGOUND_5 			= new Background(new BackgroundFill(COLOR_05, CornerRadii.EMPTY, Insets.EMPTY));
	public static Background BACKGOUND_10 			= new Background(new BackgroundFill(COLOR_10, CornerRadii.EMPTY, Insets.EMPTY));
	public static Background BACKGOUND_15 			= new Background(new BackgroundFill(COLOR_15, CornerRadii.EMPTY, Insets.EMPTY));
	public static Background BACKGOUND_20 			= new Background(new BackgroundFill(COLOR_20, CornerRadii.EMPTY, Insets.EMPTY));
	public static Background BACKGOUND_25 			= new Background(new BackgroundFill(COLOR_25, CornerRadii.EMPTY, Insets.EMPTY));
	public static Background BACKGOUND_30 			= new Background(new BackgroundFill(COLOR_30, CornerRadii.EMPTY, Insets.EMPTY));
	public static Background BACKGOUND_35 			= new Background(new BackgroundFill(COLOR_35, CornerRadii.EMPTY, Insets.EMPTY));
	public static Background BACKGOUND_40 			= new Background(new BackgroundFill(COLOR_40, CornerRadii.EMPTY, Insets.EMPTY));
	public static Background BACKGOUND_45 			= new Background(new BackgroundFill(COLOR_45, CornerRadii.EMPTY, Insets.EMPTY));
	public static Background BACKGOUND_50 			= new Background(new BackgroundFill(COLOR_50, CornerRadii.EMPTY, Insets.EMPTY));
	public static Background BACKGOUND_55 			= new Background(new BackgroundFill(COLOR_55, CornerRadii.EMPTY, Insets.EMPTY));
	public static Background BACKGOUND_60 			= new Background(new BackgroundFill(COLOR_60, CornerRadii.EMPTY, Insets.EMPTY));
	public static Background BACKGOUND_65 			= new Background(new BackgroundFill(COLOR_65, CornerRadii.EMPTY, Insets.EMPTY));
	public static Background BACKGOUND_70 			= new Background(new BackgroundFill(COLOR_70, CornerRadii.EMPTY, Insets.EMPTY));
	public static Background BACKGOUND_75 			= new Background(new BackgroundFill(COLOR_75, CornerRadii.EMPTY, Insets.EMPTY));
	public static Background BACKGOUND_80 			= new Background(new BackgroundFill(COLOR_80, CornerRadii.EMPTY, Insets.EMPTY));
	public static Background BACKGOUND_85 			= new Background(new BackgroundFill(COLOR_85, CornerRadii.EMPTY, Insets.EMPTY));
	public static Background BACKGOUND_90 			= new Background(new BackgroundFill(COLOR_90, CornerRadii.EMPTY, Insets.EMPTY));
	public static Background BACKGOUND_95 			= new Background(new BackgroundFill(COLOR_95, CornerRadii.EMPTY, Insets.EMPTY));
	public static Background BACKGOUND_YELLOW 		= new Background(new BackgroundFill(Color.YELLOW, CornerRadii.EMPTY, Insets.EMPTY));
	public static Background BACKGOUND_BLUE 		= new Background(new BackgroundFill(Color.BLUE, CornerRadii.EMPTY, Insets.EMPTY));
	public static Background BACKGOUND_RED 			= new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY));
	public static Background BACKGOUND_GREEN 		= new Background(new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY));
	public static Background BACKGOUND_LIME 		= new Background(new BackgroundFill(Color.LIME, CornerRadii.EMPTY, Insets.EMPTY));
	public static Background BACKGOUND_WHITE 		= new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY));
	
	public static final int MAX_UPD_SIZE 		= 65507;


	public static class Strings
	{
		public static final String ONLINE 												= "ONLINE";
		public static final String OFFLINE 												= "OFFLINE";
		public static final String RADIO_NETWORK_MANAGMENT_SYSTEM 						= "RADIO NETWORK MANAGMENT SYSTEM";
		public static final String CONTROL_COMMUNICATION_TERMINAL 						= "CONTROL COMMUNICATION TERMINAL";
		public static final String CONFIGURATION 										= "PLAN & MANAGE";
		public static final String MONITORING 											= "MONITORING";
		public static final String TYPES 												= "TYPES";
		public static final String TOPOLOGY 											= "TOPOLOGY";
		public static final String LITTLE 												= "LITTLE";
		public static final String NAME 												= "NAME";
		public static final String NO_NAME_STRING 										= "DEFAULT".intern();
		public static final String Name 												= "Name".intern();
		public static final String Type 												= "Type".intern();
		public static final String Icon 												= "Icon".intern();
		public static final String Ship 												= "Ship";
		public static final String DEVICE_UP 											= "THE APPLICATION IS UP";
		public static final String LOADED_LOCAL_CONFIGURATION 							= "LOCAL CONFIGURATION %s";
		public static final String NEW_CONFIGURATION_VERSION_IS_LOADED 					= "NEW CONFIGURATION VERSION %s IS LOADED";
		public static final String CONFIGURATION_VERSION_IS_DISTRIBUTED 				= "CONFIGURATION VERSION %s IS DISTRIBUTED";
		public static final String CURRENT_CCT_LOGIN_SUCCESSFULLY 						= "CURRENT CCT LOGIN SUCCESSFULLY";
		public static final String DEVICE_IS_CONNECTED 									= "DEVICE %s %d IS CONNECTED";
		public static final String DEVICE_IS_DISCONNECTED 								= "DEVICE %s %d IS DISCONNECTED";
	}
	
	
	public static void main(String[] args)
	{
		
//		StyleConverter<String, Color> colorConverter = StyleConverter.getColorConverter();
//		colorConverter.convert(new Parsedv, font)
		
		
		for (double i = 1.0; i <= 20.0; i++)
		{
			Color x = Color.BLACK.deriveColor(0, 1.0, i, 1.0);
			System.out.println("i = " + i + " color rgb = " + x.getRed() + "," + x.getGreen() + "," + x.getBlue() + " hue = " + x.getHue() + ", br " + x.getBrightness());
		}
		
	}

}
