module com.rafael.med
{
	requires java.base;
	requires java.desktop;
	requires java.logging;
	requires java.management;
	requires java.compiler;
	requires jdk.unsupported;
	
	requires transitive javafx.base;
	requires transitive javafx.controls;
	requires transitive javafx.media;
	requires transitive javafx.swing;
	requires transitive javafx.graphics;
	requires transitive javafx.web;
	
	
//	requires com.jfoenix;
	
	
	exports com.rafael.med;
		

	exports com.jfoenix.demo.components;
	
	
	opens com.rafael.med;
	opens com.rafael.med.images;
	opens com.rafael.med.fonts;
	opens com.rafael.med.common;
	opens com.rafael.med.common.entity;
	opens com.rafael.med.common.bnet;
	
	
}