package com.rafael.med.common;

import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.util.Patterns;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Utilities
{

	private static Map<String, NetworkInterface> addressNetworkInterfaceMap;
	private static Object mutex = new Object();
	
	public static boolean isLanExists()
	{
		boolean isExists = false;
		try 
		{
			for (Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces(); e.hasMoreElements() && !isExists;)
			{
				NetworkInterface localni = e.nextElement();
				if( !localni.isLoopback() && !localni.isVirtual())
				{
					isExists = localni.isUp();
				}
			}
		} 
		catch (SocketException e)
		{
			throw new IllegalStateException(e);
		}
		return isExists;
	}
	
	public static Map<NetworkInterface, List<InetAddress>> getNetworkAddresses()
	{
		Map<NetworkInterface, List<InetAddress>> nisMap = new HashMap<>();
		try 
		{
			for (Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces(); e.hasMoreElements();)
			{
				NetworkInterface localni = e.nextElement();
				if(!localni.isLoopback() && !localni.isVirtual() && localni.isUp())
				{
					List<InetAddress> list = new ArrayList<>();
					nisMap.put(localni, list);
					for (InterfaceAddress currentIntAdress : localni.getInterfaceAddresses())
					{
						InetAddress address = currentIntAdress.getAddress();
						list.add(address);
					}
				}
			}
		} 
		catch (Exception e1)
		{
			throw new RuntimeException(e1);
		}
		return nisMap;
	}
	
	public static Map<String, NetworkInterface> getAddressNetworkMap()
	{
		if(addressNetworkInterfaceMap == null)
		{
			synchronized (mutex)
			{
				if(addressNetworkInterfaceMap == null)
				{
					addressNetworkInterfaceMap = new TreeMap<>();
					
					try 
					{
						for (Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces(); e.hasMoreElements();)
						{
							NetworkInterface localni = e.nextElement();
							if(!localni.isLoopback() && !localni.isVirtual() && localni.isUp())
							{
								for (InterfaceAddress currentIntAdress : localni.getInterfaceAddresses())
								{
									InetAddress address = currentIntAdress.getAddress();
									if (address instanceof Inet4Address) 
									{
										String hostname = address.getHostAddress();
										addressNetworkInterfaceMap.put(hostname, localni);
									}
								}
							}
						}
					} 
					catch (Exception e1)
					{
						throw new RuntimeException(e1);
					}
				}
			}
		}
		return addressNetworkInterfaceMap;
	}
	
	 public static NetworkInterface getNetworkInterfaceByAddress(String address)
	 {
		return getAddressNetworkMap().get(address);
	 }
	
	
	public static ByteBuffer readFile(File targetFile, ByteOrder byteOrder) throws IOException
	{
		FileChannel file = FileChannel.open(targetFile.toPath());		
		ByteBuffer fileBuffer = ByteBuffer.allocate((int) file.size()).order(byteOrder);		
		file.read(fileBuffer);
		file.close();
		return fileBuffer;
	}
	
	
	public static int convertFrequencyFromBytesToInt(byte[] frequency) {

      	byte tempByte = (byte) frequency[0];
        byte D1 = (byte) (tempByte & 0x0F);
        tempByte >>= 4;
        byte D2 = (byte) (tempByte & 0x0F);

        tempByte = (byte) frequency[1];
        byte D3 = (byte) (tempByte & 0x0F);
        tempByte >>= 4;
        byte D4 = (byte) (tempByte & 0x0F);

        tempByte = (byte) frequency[2];
        byte D5 = (byte) (tempByte & 0x0F);
        tempByte >>= 4;
        byte D6 = (byte) (tempByte & 0x0F);

        return (D1 * 100000 + D2 * 10000 + D3 * 1000 + D4 * 100 + D5 * 10 + D6);

	}

	public static void convertFrequencyFromIntToBytes(int frequency, byte[] frequencyArr)
	{
		byte D6 = (byte) (frequency % 10);
		frequency /= 10;

		byte D5 = (byte) (frequency % 10);
		frequency /= 10;

		byte D4 = (byte) (frequency % 10);
		frequency /= 10;

		byte D3 = (byte) (frequency % 10);
		frequency /= 10;

		byte D2 = (byte) (frequency % 10);
		frequency /= 10;

		byte D1 = (byte) (frequency % 10);

		frequencyArr[0] = D1;
		frequencyArr[0] |= D2 << 4;

		frequencyArr[1] = D3;
		frequencyArr[1] |= D4 << 4;

		frequencyArr[2] = D5;
		frequencyArr[2] |= D6 << 4;

	}



	private static DocumentBuilderFactory 	documentBuilderFactory 		= DocumentBuilderFactory.newInstance();
	private static TransformerFactory 		transformerFactory			= TransformerFactory.newInstance();








	private static InetAddress localHost;

	public static InetAddress getLocalHost()
	{
		if (localHost == null)
		{
			synchronized (Utilities.class)
			{
				if (localHost == null)
				{
					try
					{
						localHost = InetAddress.getLocalHost();
					}
					catch (UnknownHostException e)
					{
						throw new RuntimeException(e);
					}
				}
			}
		}
		return localHost;
	}


//	public static InetAddress LOCAL_HOST = null;
//
//
//
//	public static void getAndSetLocalAddressByInterface(String ifname)
//	{
//		try
//		{
//			for (Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces(); e.hasMoreElements();)
//			{
//				NetworkInterface localni = e.nextElement();
//				String name = localni.getName();
//				if(name.equals(ifname))
//				{
//					LOCAL_HOST =  localni.getInetAddresses().nextElement();
//				}
//			}
//		}
//		catch (Exception e)
//		{
//			throw new RuntimeException(e);
//		}
//	}


	public static final DateFormat LOG_DATE_FORMAT = new SimpleDateFormat("dd-MM-YYYY HH-mm-ss.SSS");

	public static final DateTimeFormatter DATE_TIME_PATTERN_1 = DateTimeFormatter.ofPattern("YYYY-MM-dd-HH-mm-ss");
	public static final DateTimeFormatter DATE_TIME_PATTERN_2 = DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss");
	
	public static final DateTimeFormatter DATE_TIME_dd_MM_yyyy 			= DateTimeFormatter.ofPattern("dd/MM/yyyy");
	public static final DateTimeFormatter DATE_TIME_dd_MM 				= DateTimeFormatter.ofPattern("dd/MM");
	public static final DateTimeFormatter DATE_TIME_dd_MM_yyyy_HH_mm 	= DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
	public static final DateTimeFormatter DATE_TIME_dd_MM_yyyy_HH_mm_ss = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
	public static final DateTimeFormatter DATE_TIME_dd_MM_yyyy_HH_mm_ss_SSS = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss.SSS");
	
	public static final DateTimeFormatter DATE_TIME_dd_MM_yyyy_HH_mm_ss_minus = DateTimeFormatter.ofPattern("dd-MM-yyyy-HH-mm-ss");
	public static final DateTimeFormatter DATE_TIME_dd_MM_yyyy_HH 		= DateTimeFormatter.ofPattern("dd/MM/yyyy HH");
	public static final DateTimeFormatter DATE_TIME_HH_mm 				= DateTimeFormatter.ofPattern("HH:mm");
	public static final DateTimeFormatter DATE_TIME_HH_mm_ss 				= DateTimeFormatter.ofPattern("HH:mm:ss");



	private static class CustomThreadFactory implements ThreadFactory
	{
		private final ThreadGroup group;
		private final AtomicInteger threadNumber = new AtomicInteger(1);
		private final String namePrefix;
		private final boolean isDaemon;

		CustomThreadFactory(String poolName, boolean isDaemon)
		{
			SecurityManager s = System.getSecurityManager();
			this.isDaemon = isDaemon;
			this.group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
			this.namePrefix = poolName + "-thread-";
		}

		public Thread newThread(Runnable r)
		{
			Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
			t.setDaemon(isDaemon);
			if (t.getPriority() != Thread.NORM_PRIORITY)
			{
				t.setPriority(Thread.NORM_PRIORITY);
			}
			return t;
		}
	}

	public static ThreadFactory threadFactory(final String name, final boolean isDaemon)
	{
		return new CustomThreadFactory(name, isDaemon);
	}

	public static String toSocketAddressToString(InetSocketAddress socketAddress)
	{
		StringBuilder builder = new StringBuilder();
		String hostString = socketAddress.getAddress().getHostAddress();
		builder.append(hostString);
		builder.append(":");
		builder.append(socketAddress.getPort());
		return builder.toString();
	}

	public static StringBuilder infoNetworkToString() throws Exception
	{
		StringBuilder builder = new StringBuilder();

		builder.append("\n***************************************************************\n" + "***           NETWOTK INTERFACES FOR LOCAL MACHINE :        ***\n" + "***************************************************************\n");

		for (Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces(); e.hasMoreElements();)
		{
			NetworkInterface localni = e.nextElement();
			builder.append(getInfoForNetworkInterface(localni));
			builder.append("\n");
		}
		return builder;
	}

	public static StringBuilder infoDateToString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("\n****************************************\n" + "***          " + DateFormat.getDateTimeInstance().format(new Date()) + "     ***\n" + "****************************************\n");

		DateFormat dateFormat = DateFormat.getDateTimeInstance();
		dateFormat.format(new Date());

		return builder;
	}

	public static StringBuilder infoSystemToString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("\n***************************************************************\n" + "***     JAVA SYSTEM FOR LOCAL MACHINE :        			    ***\n" + "***************************************************************\n");

		for (Entry<Object, Object> entry : System.getProperties().entrySet())
		{
			builder.append(entry.getKey()).append(" = ").append(entry.getValue()).append("\n");
		}
		return builder;
	}

	public static StringBuilder infoJVMToString()
	{
		StringBuilder builder = new StringBuilder();

		return builder;
	}

	private static String getInfoForNetworkInterface(NetworkInterface ni) throws IOException
	{
		StringBuilder buf = new StringBuilder();
		buf.append("\n\tNetworkInterface properties : ");
		buf.append(ni.getDisplayName());
		buf.append("\n\t\tName = ");
		buf.append(ni.getName());
		buf.append("\n\t\tMaximum Transmission Unit (MTU) = ");
		buf.append(ni.getMTU());
		buf.append("\n\t\tHardware Address (usually MAC)  = ");
		byte[] ha = ni.getHardwareAddress();
		if (ha != null)
		{
			for (int i = 0; i < ha.length; i++)
			{
				buf.append(ha[i]);
			}
		}
		buf.append("\n\t\tisLoopback = ");
		buf.append(ni.isLoopback());
		buf.append("\n\t\tisPointToPoint = ");
		buf.append(ni.isPointToPoint());
		buf.append("\n\t\tis Up and running = ");
		buf.append(ni.isUp());
		buf.append("\n\t\tSupportsMulticast = ");
		buf.append(ni.supportsMulticast());
		buf.append("\n\t\tisVirtual = ");
		buf.append(ni.isVirtual());

		for (InterfaceAddress currentIntAdress : ni.getInterfaceAddresses())
		{
			InetAddress address = currentIntAdress.getAddress();
			buf.append(getInfoForInetAddress(address));
			buf.append("\n\t\t\t Broadcast = ");
			buf.append(currentIntAdress.getBroadcast());
			buf.append("\n\t\t\t NetworkPrefixLength = ");
			buf.append(currentIntAdress.getNetworkPrefixLength());
		}
		return buf.toString();
	}

	private static String getInfoForInetAddress(InetAddress address)
	{
		StringBuilder buf = new StringBuilder();
		if (address != null)
		{
			buf.append("\n\t\t\t HostAddress = ");
			buf.append(address.getHostAddress());
			buf.append("\n\t\t\t HostName = ");
			buf.append(address.getHostName());
			buf.append("\n\t\t\t CanonicalHostName = ");
			buf.append(address.getCanonicalHostName());
			buf.append("\n\t\t\t isAnyLocalAddress = ");
			buf.append(address.isAnyLocalAddress());
			buf.append("\n\t\t\t isLinkLocalAddress = ");
			buf.append(address.isLinkLocalAddress());
			buf.append("\n\t\t\t isLoopbackAddress = ");
			buf.append(address.isLoopbackAddress());
			buf.append("\n\t\t\t isMCGlobal = ");
			buf.append(address.isMCGlobal());
			buf.append("\n\t\t\t isMCLinkLocal = ");
			buf.append(address.isMCLinkLocal());
			buf.append("\n\t\t\t isMCNodeLocal = ");
			buf.append(address.isMCNodeLocal());
			buf.append("\n\t\t\t isMCOrgLocal = ");
			buf.append(address.isMCOrgLocal());
			buf.append("\n\t\t\t isMCSiteLocal = ");
			buf.append(address.isMCSiteLocal());
			buf.append("\n\t\t\t isMulticastAddress = ");
			buf.append(address.isMulticastAddress());
			buf.append("\n\t\t\t isSiteLocalAddress = ");
			buf.append(address.isSiteLocalAddress());
		}
		else
		{
			buf.append("target bnetAddress = null");
		}
		return buf.toString();
	}

	public static InputStream loadFile(Path path) throws Exception
	{
		InputStream inputStream = null;
		if (Files.exists(path))
		{
			inputStream = Files.newInputStream(path);
		}
		else
		{
			inputStream = ClassLoader.getSystemResourceAsStream(path.getFileName().toString());
		}
		if (inputStream == null)
		{
			throw new Exception("not found file on path = " + path + " and on classpath");
		}
		return inputStream;
	}

	public static Properties loadPropertiesFile(Path propertiesFilePath)
	{
		Properties propertiesFromFile = new Properties();
		try (InputStream inputStream = loadFile(propertiesFilePath))
		{
			propertiesFromFile.load(inputStream);
		}
		catch (Exception e)
		{
			throw new IllegalStateException("failed load properties path = " + propertiesFilePath, e);
		}
		return propertiesFromFile;
	}

	public static void logDirectoryProcess(final Path logDirectory, int numberRemain) throws Exception
	{
		final Path previousDirectory = Paths.get(logDirectory.toString(), "previous");
		final Path zipFile = Paths.get(previousDirectory.toString(), "log-" + LOG_DATE_FORMAT.format(new Date()) + ".zip");
		Files.createDirectories(previousDirectory);
		final TreeMap<Long, Path> map = new TreeMap<>();

		final byte[] buffer = new byte[1024 * 8];
		final ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile.toFile()));

		Files.walkFileTree(logDirectory, new SimpleFileVisitor<Path>()
		{
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
			{
				try
				{
					if (file.getParent().equals(previousDirectory) && file.toString().endsWith(".zip"))
					{
						map.put(file.toFile().lastModified(), file);
					}
					else if (file.getParent().equals(logDirectory) && file.toString().endsWith(".log"))
					{

						InputStream fis = Files.newInputStream(file);
						zos.putNextEntry(new ZipEntry(file.getFileName().toString()));
						int length;
						while ((length = fis.read(buffer)) > 0)
						{
							zos.write(buffer, 0, length);

						}
						zos.closeEntry();
						fis.close();

						if (!file.getFileName().toString().equals("wrapper.log"))
						{
							Files.delete(file);
						}
					}
					else
					{
						Files.delete(file);
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException
			{
				if (e == null)
				{
					try
					{
						if (!previousDirectory.equals(dir) && !logDirectory.equals(dir))
						{
							deleteDirectory(dir);
						}
					}
					catch (Exception e1)
					{
						e1.printStackTrace();
					}
					return FileVisitResult.CONTINUE;
				}
				else
				{
					throw e;
				}
			}
		});
		zos.close();
		int count = map.size();
		for (Path path : map.values())
		{
			if (count > numberRemain)
			{
				Files.delete(path);
			}
			count--;
		}
	}

	public static void copyDirectory(final Path source, final Path target) throws IOException
	{
		Files.walkFileTree(source, new SimpleFileVisitor<Path>()
		{
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException
			{
				Path targetdir = target.resolve(source.relativize(dir));
				try
				{
					Files.copy(dir, targetdir, StandardCopyOption.REPLACE_EXISTING);
				}
				catch (FileAlreadyExistsException e)
				{
					if (!Files.isDirectory(targetdir))
					{
						throw e;
					}
				}
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
			{
				Files.copy(file, target.resolve(source.relativize(file)), StandardCopyOption.REPLACE_EXISTING);
				return FileVisitResult.CONTINUE;
			}
		});
	}

	public static void copyDirectoryToExistingDirectory(final Path source, final Path target) throws IOException
	{
		Files.walkFileTree(source, new SimpleFileVisitor<Path>()
		{
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException
			{
				Path targetdir = target.resolve(source.relativize(dir));
				try
				{
					if (Files.isDirectory(targetdir) && Files.notExists(targetdir))
					{
						Files.copy(dir, targetdir, StandardCopyOption.REPLACE_EXISTING);
					}
				}
				catch (FileAlreadyExistsException e)
				{
					if (!Files.isDirectory(targetdir))
					{
						throw e;
					}
				}
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
			{
				Files.copy(file, target.resolve(source.relativize(file)), StandardCopyOption.REPLACE_EXISTING);
				return FileVisitResult.CONTINUE;
			}
		});
	}

	public static void deleteDirectory(Path directoryPath) throws IOException
	{
		if (Files.exists(directoryPath) && Files.isDirectory(directoryPath))
		{
			Files.walkFileTree(directoryPath, new SimpleFileVisitor<Path>()
			{
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
				{
					Files.delete(file);
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException
				{
					if (e == null)
					{
						Files.delete(dir);
						return FileVisitResult.CONTINUE;
					}
					else
					{
						throw e;
					}
				}
			});
		}
	}

	
	public static List<File> listFiles(Path directoryPath,String... extensions) throws IOException
	{
		List<File> result = new ArrayList<>();
		if (Files.exists(directoryPath) && Files.isDirectory(directoryPath))
		{
			Files.walkFileTree(directoryPath, new SimpleFileVisitor<Path>()
			{
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
				{
					result.add(file.toFile());
					
					
					return FileVisitResult.CONTINUE;
				}
			});
		}
		return result;
	}
	
	public static String getByAdressWithoutException(byte[] address)
	{
		String result = null;
		try
		{
			result = InetAddress.getByAddress(address).getHostAddress();
		}
		catch (Exception e)
		{
			throw new IllegalArgumentException(Utilities.oneLineException(e), e);
		}
		return result;
	}
	
	public static InetAddress getByNameWithoutException(String address)
	{
		InetAddress result = null;
		try
		{
			result = InetAddress.getByName(address);
		}
		catch (Exception e)
		{
			throw new IllegalArgumentException(Utilities.oneLineException(e), e);
		}
		return result;
	}

	public static String oneLineException(Throwable throwable)
	{
		if (throwable != null)
		{
			StringBuilder builder = new StringBuilder(throwable.getClass().getCanonicalName());
			builder.append(" : ").append(throwable.getMessage());
			return builder.toString();
		}
		return "no throwable";
	}

	public static void writeFileToDisk(Path path, ByteBuffer byteBuffer) throws Exception
	{
		BufferedWriter bufferedWriter = Files.newBufferedWriter(path, Charset.defaultCharset());
		while (byteBuffer.hasRemaining())
		{
			bufferedWriter.write(byteBuffer.getChar());
		}
		bufferedWriter.close();
	}

	public static void machineShutdown()
	{
		try
		{
			System.out.println("**************************************** machine shutdown request");
			if (SystemUtils.IS_OS_WINDOWS)
			{

			}
			Runtime.getRuntime().exec("SHUTDOWN -s -f -t 00");
		}
		catch (Exception e)
		{
			System.err.println("**************************************** machineShutdown failed - " + Utilities.oneLineException(e));
		}
	}

	public static void machineReboot()
	{
		try
		{
			System.out.println("**************************************** machine reboot request");
			Runtime.getRuntime().exec("SHUTDOWN -r -f -t 00");
		}
		catch (Exception e)
		{
			System.err.println("**************************************** machineReboot failed - " + Utilities.oneLineException(e));
		}
	}

	public static void serviceRestart()
	{
		try
		{
			System.out.println("**************************************** service restart request");
			System.exit(-1);
		}
		catch (Exception e)
		{
			System.err.println("**************************************** service restart failed - " + Utilities.oneLineException(e));
		}
	}

	public static void serviceExit()
	{
		try
		{
			System.out.println("**************************************** service stop request");
			System.exit(0);
		}
		catch (Exception e)
		{
			System.err.println("**************************************** serviceExit failed - " + Utilities.oneLineException(e));
		}
	}

	

	public static TrayIcon createTrayIcon(BufferedImage image)
	{
		TrayIcon trayIcon = null;
		try
		{
			PopupMenu popupMenu = new PopupMenu();
			// MenuItem logItem = new MenuItem("log");
			// logItem.addActionListener(new ActionListener()
			// {
			// public void actionPerformed(ActionEvent e)
			// {
			// try
			// {
			// Runtime.getRuntime().exec(com.rafael.maoz.Constants.Paths.BARETAILPRO_PATH.toString());
			// }
			// catch (Throwable e1)
			// {
			// System.err.println("**************************************** failed execute command '" + com.rafael.maoz.Constants.Paths.BARETAILPRO_PATH.toString() + "' : but continue startup " + e1.getMessage());
			// }
			// }
			// });
			// popupMenu.add(logItem);
			// popupMenu.addSeparator();

			MenuItem exit = new MenuItem("exit");
			exit.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					System.exit(0);
				}
			});
			popupMenu.add(exit);
			popupMenu.addSeparator();

			MenuItem reboot = new MenuItem("reboot machine");
			reboot.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					Utilities.machineReboot();
				}
			});
			popupMenu.add(reboot);
			popupMenu.addSeparator();

			MenuItem shutdown = new MenuItem("shutdown machine");
			shutdown.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					Utilities.machineShutdown();
				}
			});
			popupMenu.add(shutdown);
			popupMenu.addSeparator();

			trayIcon = new TrayIcon(image, "maoz", popupMenu);

		}
		catch (Exception e)
		{
			System.err.println("failed create tray - " + e.getMessage());

		}
		return trayIcon;
	}


	public static void extractZipStream(InputStream zipAsInputStream, String destinationFolder) throws IOException
	{
		ZipInputStream zis = new ZipInputStream(zipAsInputStream);
		ZipEntry zipEntry = null;
		while ((zipEntry = zis.getNextEntry()) != null)
		{
			File fileOrDir = new File(destinationFolder + File.separator + zipEntry.getName());
			if (!zipEntry.isDirectory())
			{
				File parentDir = fileOrDir.getParentFile();
				if (!parentDir.exists())
				{
					parentDir.mkdirs();
				}
				int size = 0;
				byte[] buffer = new byte[2048];
				
				
//				LocalDateTime lastModifiedDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(zipEntry.getTime()), ZoneId.systemDefault());
//				
//				System.out.println("-------------- " + fileOrDir + "    " + lastModifiedDateTime.format(Utilities.DATE_TIME_dd_MM_yyyy_HH_mm_ss));
						
				FileOutputStream fos = new FileOutputStream(fileOrDir);
				BufferedOutputStream bos = new BufferedOutputStream(fos, buffer.length);

				while ((size = zis.read(buffer, 0, buffer.length)) != -1)
				{
					bos.write(buffer, 0, size);
				}
				bos.flush();
				bos.close();
				fileOrDir.setLastModified(zipEntry.getTime());
			}
			else
			{
				fileOrDir.mkdirs();
			}
		}
		zis.close();
	}

	public static void extractZipStream(File zipFile, String destinationFolder) throws IOException
	{
		if (zipFile == null || !zipFile.exists() || zipFile.isDirectory())
		{
			throw new IOException("zip file is not correct");
		}
		FileInputStream fileInputStream = new FileInputStream(zipFile);
		extractZipStream(fileInputStream, destinationFolder);
	}



	public String systemPropertiesToString()
	{
		Properties properties = System.getProperties();
		StringBuilder builder = new StringBuilder("System properties : \n");
		for (Entry<Object, Object> entry : properties.entrySet())
		{
			builder.append(entry.getKey()).append("=").append(entry.getValue()).append("\n");
		}
		return builder.toString();
	}







	/*******************************************machine utilities *************************************************/
	public void machineRestart()
	{
		String command = null;
		if(SystemUtils.IS_OS_WINDOWS)
		{
			command = "SHUTDOWN -r -f -t 00";
		}
		else
		{
			throw new UnsupportedOperationException("not windows OS");
		}

		try
		{
			Runtime.getRuntime().exec(command);
		}
		catch (Exception e)
		{
			System.err.println("failed machine restart - " + e.getMessage());
		}
	}



	/****************************************** xml utilities *****************************************************/

	public static Document xmlFromByteArray(byte[] byteArray) throws Exception
	{
		DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
		return builder.parse(byteArrayInputStream);
	}

	public static void xmlSaveToFile(Document document,Path directory, String filename) throws Exception
	{
		if(directory != null)
		{
			directory = Files.createDirectories(directory);
		}

		Path filepath = Paths.get(directory.toString(),filename);
		FileOutputStream fileOutputStream = new FileOutputStream(filepath.toFile());
		xmlDocumentToOutputStream(document, fileOutputStream);
	}


	public static void xmlSaveToFile(Document document,Path filepath) throws Exception
	{
		FileOutputStream fileOutputStream = new FileOutputStream(filepath.toFile());
		xmlDocumentToOutputStream(document, fileOutputStream);
	}

	public static byte[] xmlToByteArray(Document document) throws Exception
	{
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		xmlDocumentToOutputStream(document, byteArrayOutputStream);
		return byteArrayOutputStream.toByteArray();
	}

	public static Document xmlNewDocument() throws Exception
	{
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		return documentBuilder.newDocument();
	}

	public static Element xmlNewDocumentRootElement(String rootName) throws Exception
	{
		Element rootElement = xmlNewDocument().createElement(rootName);
		return rootElement;
	}

	public static Document xmlReadFromFile(File file) throws Exception
	{
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document document = documentBuilder.parse(new FileInputStream(file));
		return document;
	}

	public static Document xmlReadResource(String filename) throws Exception
	{
		URL url = Utilities.resolveConfigResource(filename);
		if(url == null)
		{
			throw new Exception("not found file " + filename);
		}
		InputStream inputStream = url.openStream();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document document = documentBuilder.parse(inputStream);
		return document;
	}


	public static Document xmlReadResource(Path path) throws Exception
	{
		InputStream inputStream = Files.newInputStream(path);
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document document = documentBuilder.parse(inputStream);
		return document;
	}

	private static void xmlDocumentToOutputStream(Document document,OutputStream outputStream) throws Exception
	{
		DOMSource domSource = new DOMSource(document);
		StreamResult streamResult = new StreamResult(outputStream);
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

		transformer.transform(domSource, streamResult);
	}


























	public static URL resolveConfigResource(String filename)
	{
		String configDir = System.getProperty("config.dir", "conf");
		return resolveResource(configDir, filename);
	}


	public static URL resolveResource(String basePath,String filename)
	{
		URL result = null;
		File directory = null;
		File file = null;

		boolean isContinue = true;
		if(StringUtils.isNotBlank(basePath))
		{
			directory = new File(basePath);
			file = new File(directory,filename);
			if(file.exists())
			{
				try
				{
					result = file.toURI().toURL();
					if(result != null)
					{
						System.out.println("Loading resource from the defined directory (" + result + ")");
						isContinue = false;
					}
				}
				catch (MalformedURLException e){e.printStackTrace();}
			}
		}

		if(isContinue)
		{
			file = new File(filename);
			if(file.exists())
			{
				try
				{
					result = file.toURI().toURL();
					if(result != null)
					{
						System.out.println("Loading resource from the user dir (" + result + ")");
						isContinue = false;
					}
				}
				catch (MalformedURLException e){e.printStackTrace();}
			}
		}
		if(isContinue)
		{
	        ClassLoader loader = Thread.currentThread().getContextClassLoader();
	        if (loader != null)
	        {
	            result = loader.getResource(filename);
	            if(result != null)
				{
	            	System.out.println("Loading resource from the context classpath (" + result + ")");
				}
	        }
	        if (result == null)
	        {
	        	result = ClassLoader.getSystemResource(filename);
	        	if(result != null)
				{
	            	System.out.println("Loading resource from the system classpath (" + result + ")");
				}
	        }
		}
		return result;
	}


	 /**
     * System property that may be used to seed the UUID generation with an integer value.
     */
    public static final String UUID_SEQUENCE = "org.apache.logging.log4j.uuidSequence";

    private static final String ASSIGNED_SEQUENCES = "org.apache.logging.log4j.assignedSequences";

    private static final AtomicInteger count = new AtomicInteger(0);

    private static final long TYPE1 = 0x1000L;

    private static final byte VARIANT = (byte) 0x80;

    private static final int SEQUENCE_MASK = 0x3FFF;

    private static final long NUM_100NS_INTERVALS_SINCE_UUID_EPOCH = 0x01b21dd213814000L;

    private static final long uuidSequence = PropertiesUtil.getProperties().getLongProperty(UUID_SEQUENCE, 0);

    private static final long least;

    private static final long LOW_MASK = 0xffffffffL;
    private static final long MID_MASK = 0xffff00000000L;
    private static final long HIGH_MASK = 0xfff000000000000L;
    private static final int NODE_SIZE = 8;
    private static final int SHIFT_2 = 16;
    private static final int SHIFT_4 = 32;
    private static final int SHIFT_6 = 48;
    private static final int HUNDRED_NANOS_PER_MILLI = 10000;
	

    static {
        byte[] mac = getLocalMacAddress();
        final Random randomGenerator = new SecureRandom();
        if (mac == null || mac.length == 0) {
            mac = new byte[6];
            randomGenerator.nextBytes(mac);
        }
        final int length = mac.length >= 6 ? 6 : mac.length;
        final int index = mac.length >= 6 ? mac.length - 6 : 0;
        final byte[] node = new byte[NODE_SIZE];
        node[0] = VARIANT;
        node[1] = 0;
        for (int i = 2; i < NODE_SIZE; ++i) {
            node[i] = 0;
        }
        System.arraycopy(mac, index, node, index + 2, length);
        final ByteBuffer buf = ByteBuffer.wrap(node);
        long rand = uuidSequence;
        String assigned = PropertiesUtil.getProperties().getStringProperty(ASSIGNED_SEQUENCES);
        long[] sequences;
        if (assigned == null) {
            sequences = new long[0];
        } else {
            final String[] array = assigned.split(Patterns.COMMA_SEPARATOR);
            sequences = new long[array.length];
            int i = 0;
            for (final String value : array) {
                sequences[i] = Long.parseLong(value);
                ++i;
            }
        }
        if (rand == 0) {
            rand = randomGenerator.nextLong();
        }
        rand &= SEQUENCE_MASK;
        boolean duplicate;
        do {
            duplicate = false;
            for (final long sequence : sequences) {
                if (sequence == rand) {
                    duplicate = true;
                    break;
                }
            }
            if (duplicate) {
                rand = (rand + 1) & SEQUENCE_MASK;
            }
        } while (duplicate);
        assigned = assigned == null ? Long.toString(rand) : assigned + ',' + Long.toString(rand);
        System.setProperty(ASSIGNED_SEQUENCES, assigned);

        least = buf.getLong() | rand << SHIFT_6;
    }




    /**
     * Generates Type 1 UUID. The time contains the number of 100NS intervals that have occurred
     * since 00:00:00.00 UTC, 10 October 1582. Each UUID on a particular machine is unique to the 100NS interval
     * until they rollover around 3400 A.D.
     * <ol>
     * <li>Digits 1-12 are the lower 48 bits of the number of 100 ns increments since the start of the UUID
     * epoch.</li>
     * <li>Digit 13 is the version (with a value of 1).</li>
     * <li>Digits 14-16 are a sequence number that is incremented each time a UUID is generated.</li>
     * <li>Digit 17 is the variant (with a value of binary 10) and 10 bits of the sequence number</li>
     * <li>Digit 18 is final 16 bits of the sequence number.</li>
     * <li>Digits 19-32 represent the system the application is running on.</li>
     * </ol>
     *
     * @return universally unique identifiers (UUID)
     */
    public static UUID getTimeBasedUuid()
    {

        final long time = ((System.currentTimeMillis() * HUNDRED_NANOS_PER_MILLI) + NUM_100NS_INTERVALS_SINCE_UUID_EPOCH) + (count.incrementAndGet() % HUNDRED_NANOS_PER_MILLI);
        final long timeLow = (time & LOW_MASK) << SHIFT_4;
        final long timeMid = (time & MID_MASK) >> SHIFT_2;
        final long timeHi = (time & HIGH_MASK) >> SHIFT_6;
        final long most = timeLow | timeMid | TYPE1 | timeHi;
        return new UUID(most, least);
    }

    /**
     * Returns the local network interface's MAC address if possible. The local network interface is defined here as
     * the {@link java.net.NetworkInterface} that is both up and not a loopback interface.
     *
     * @return the MAC address of the local network interface or {@code null} if no MAC address could be determined.
     */
    public static byte[] getLocalMacAddress()
    {
        byte[] mac = null;
        try
        {
            final InetAddress localHost = InetAddress.getLocalHost();
            try
            {
                final NetworkInterface localInterface = NetworkInterface.getByInetAddress(localHost);
                if (isUpAndNotLoopback(localInterface))
                {
                    mac = localInterface.getHardwareAddress();
                }
                if (mac == null)
                {
                    final Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
                    while (networkInterfaces.hasMoreElements() && mac == null)
                    {
                        final NetworkInterface nic = networkInterfaces.nextElement();
                        if (isUpAndNotLoopback(nic))
                        {
                            mac = nic.getHardwareAddress();
                        }
                    }
                }
            }
            catch (final SocketException e)
            {
                System.err.println(e.getMessage());
            }
            if (mac == null || mac.length == 0) {
                mac = localHost.getAddress();
            }
        } catch (final UnknownHostException ignored) {
        }
        return mac;
    }
    
    
   
    

    private static boolean isUpAndNotLoopback(final NetworkInterface ni) throws SocketException {
        return ni != null && !ni.isLoopback() && ni.isUp();
    }


    public static boolean isLocalAddress(String address)
    {
    	InetAddress inetAddress = null;
		try
		{
			inetAddress = InetAddress.getByName(address);
		}
		catch (UnknownHostException e)
		{
			throw new IllegalStateException("Problem with ip - ", e);
		}
		return isLocalAddress(inetAddress);
    }


    public static boolean isLocalAddress(InetAddress inetAddress)
    {
    	if(inetAddress == null)
    	{
    		throw new IllegalArgumentException("inetAddress is null");
    	}

    	if(inetAddress.isAnyLocalAddress() || inetAddress.isLoopbackAddress())
    	{
    		return true;
    	}

    	try
		{
			return NetworkInterface.getByInetAddress(inetAddress) != null;
		}
		catch (Exception e)
		{
			return false;
		}
    }

    public static boolean toBooleanFromInt(int value,int trueInt)
    {
    	return value == trueInt;
    }
    public static int fromBooleanToInt(boolean value,int trueInt,int falseInt)
    {
    	return (value) ? trueInt : falseInt;
    }


    public static String toHex(byte[] array)
	{
		BigInteger bi = new BigInteger(1, array);
		String hex = bi.toString(16);
		int paddingLength = (array.length * 2) - hex.length();
		if(paddingLength > 0)
		{
			return String.format("%0"  +paddingLength + "d", 0) + hex;
		}
		else
		{
			return hex;
		}
	}

    public static byte[] fromHex(String hex)
	{
		byte[] bytes = new byte[hex.length() / 2];
		for(int i = 0; i<bytes.length ;i++)
		{
			bytes[i] = (byte)Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
		}
		return bytes;
	}

    public static String getSalt() throws NoSuchAlgorithmException
	{
		SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
		byte[] salt = new byte[16];
		sr.nextBytes(salt);
		return salt.toString();
	}

    public static String resultToString(int result)
    {
    	return result == 0 ? "SUCCESS" : "FAILED";
    }

	public static String timestampString()
	{
		return LOG_DATE_FORMAT.format(new Date());
	}

	public static String getManifestVersion() throws IOException
	{
		String result = null;

		InputStream inputStream = Utilities.class.getClassLoader().getResourceAsStream("META-INF/MANIFEST.MF");
		Manifest manifest = new Manifest(inputStream);

		Attributes mainAttributes = manifest.getMainAttributes();
		result = mainAttributes.getValue(Attributes.Name.IMPLEMENTATION_VERSION);
		return result;
	}

	public static String getDateTimeString(long millisecons)
	{
		Instant instant = Instant.ofEpochMilli(millisecons);
		LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
		return localDateTime.format(DATE_TIME_PATTERN_2);
	}


	public static String getDateString()
	{
		return LocalDateTime.now().format(DATE_TIME_PATTERN_1);
	}

	public static void fatalErrorToLog(Throwable e, String message, Logger logger)
	{
		if(logger != null)
		{
			logger.error("\n***************************************************************\n"
							+ "***                     application failed         	      ***\n"
							+ "***************************************************************\n",e);
		}
		else
		{
			System.out.println("\n***************************************************************\n"
					+ "***                     application failed         	      ***\n"
					+ "***************************************************************\n");
			e.printStackTrace();
		}
		//System.exit(-1);
	}

	public static byte[] stringToByteArray(String string,int size)
	{
		StringTokenizer tokenizer = new StringTokenizer(string, ".");

		byte[] array = new byte[size];
		int i = 0;
		while(tokenizer.hasMoreTokens() && i < size )
		{
			array[i++] = (byte) Integer.parseInt(tokenizer.nextToken());
		}
		return array;
	}



	

	public static String byteArrayToString(byte[] ba)
	{
		String result;
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < ba.length; i++)
		{
			builder.append(BitByteUtils.toUnsignedByte(ba[i]));
			if(i < ba.length - 1)
			{
				builder.append(".");
			}
		}
		result = builder.toString();
		return result;
	}

	public static void main(String[] args)
	{
		for (int i = 0; i < 360; i++)
		{
			System.out.println("public Boolean getSecond" + i + "() {return isSecondsExists[" + (i) +"];}");
		}
	}

	
}
