package com.rafael.med.common;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.INIConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.FileBasedBuilderParameters;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;



/**
 * The Class Configurator.
 */
public class Configurator 
{
	
	/** The Constant XML. */
	private static final String XML 			= "xml";
	
	/** The Constant PROPERTIES. */
	private static final String PROPERTIES 		= "properties";
	
	/** The Constant INI. */
	private static final String INI 			= "ini";
	
	/** The Constant PARAMETERS. */
	private static final Parameters PARAMETERS = new Parameters();
	
	/** The Constant map. */
	private static final Map<String, FileBasedConfigurationBuilder<? extends FileBasedConfiguration>> map = new HashMap<String, FileBasedConfigurationBuilder<? extends FileBasedConfiguration>>();
	
	/** The Constant CONFIG_XML. */
	private static final String CONFIG_XML = "config." + XML;
	
	/**
	 * Load configuration.
	 *
	 * @param filename the filename
	 * @return the file based configuration builder<? extends file based configuration>
	 */
	private static FileBasedConfigurationBuilder<? extends FileBasedConfiguration> loadConfiguration(String filename)
	{
		FileBasedConfigurationBuilder<? extends FileBasedConfiguration> builder = null;
		try
		{
			URL url = Utilities.resolveConfigResource(filename);
			if(url == null)
			{
				throw new Exception("not found file " + filename);
			}
			String suffix = filename.substring(filename.lastIndexOf(".") + 1);
			if(XML.equalsIgnoreCase(suffix))
			{
				builder = new FileBasedConfigurationBuilder<XMLConfiguration>(XMLConfiguration.class);
			}
			else if(PROPERTIES.equalsIgnoreCase(suffix))
			{
				builder = new FileBasedConfigurationBuilder<PropertiesConfiguration>(PropertiesConfiguration.class);
			}
			else if(INI.equalsIgnoreCase(suffix))
			{
				builder = new FileBasedConfigurationBuilder<INIConfiguration>(INIConfiguration.class);
			}
			else
			{
				throw new Exception("not recognized file suffix " + suffix + " only " + XML + ", " + PROPERTIES + ", " + INI + " allowed");
			}
			
			FileBasedBuilderParameters fileBasedBuilderParameters = PARAMETERS.fileBased();
			fileBasedBuilderParameters.setURL(url);
			builder.configure(fileBasedBuilderParameters);
		}
		catch(Exception e)
		{
			throw new IllegalStateException("failed load configration from file " + filename,e);
		}
		return builder;
	}
	
	/**
	 * Gets the builder by file.
	 *
	 * @param filename the filename
	 * @return the builder by file
	 */
	private static FileBasedConfigurationBuilder<? extends FileBasedConfiguration> getBuilderByFile(String filename)
	{
		FileBasedConfigurationBuilder<? extends FileBasedConfiguration> builder = map.get(filename);
		if(builder == null)
		{
			synchronized (map)
			{
				builder = map.get(filename);
				if(builder == null)
				{
					builder = loadConfiguration(filename);
					map.put(filename, builder);
				}
			}
		}
		return builder;
	}
	
	
	/**
	 * Gets the configuraion by file.
	 *
	 * @param filename the filename
	 * @return the configuraion by file
	 */
	public static Configuration getConfiguraionByFile(String filename)
	{
		Configuration configuration = null;
		try 
		{
			FileBasedConfigurationBuilder<? extends FileBasedConfiguration> builder = getBuilderByFile(filename);
			configuration = builder.getConfiguration();
		} 
		catch (ConfigurationException e)
		{
			throw new IllegalStateException("failed load configration from file " + filename,e);
		}
		return configuration;
	}
	
	
	/**
	 * Save configuration by file.
	 *
	 * @param filename the filename
	 */
	public static void saveConfigurationByFile(String filename)
	{
		try 
		{
			FileBasedConfigurationBuilder<? extends FileBasedConfiguration> builder = getBuilderByFile(filename);
			builder.save();
		} 
		catch (ConfigurationException e)
		{
			throw new IllegalStateException("failed save configration to file " + filename,e);
		}
	}
	
	
	/**
	 * Gets the default configuration.
	 *
	 * @return the default configuration
	 */
	public static Configuration getDefaultConfiguration()
	{
		return getConfiguraionByFile(CONFIG_XML);
	}
	
	/**
	 * Save default configuration.
	 */
	public static void saveDefaultConfiguration()
	{
		saveConfigurationByFile(CONFIG_XML);
	}
}
