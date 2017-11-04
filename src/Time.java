import org.jnativehook.keyboard.NativeKeyListener;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;

public class Time implements NativeKeyListener
{
	static double distance = 0.0;
	static double time = 0.0;
	static int mode = 0;
	static Stopwatch s;
	static ArrayList<Double> results = new ArrayList<Double>();
	static JFrame frame;
	static JLabel label;
	static JLabel label2;
	static JTextField textfield;
	static JPanel panel;
	static JButton button;
	static JButton help;
	static JButton makeCSV;
	static String getValue = "0.0";
	static String OS = System.getProperty("os.name").toLowerCase();
	static boolean oswarn = false;
	public static void main (String[] args)
	{	
		 System.out.println("OS: " + OS);
		 if (!isUnix() && !isWindows())
		 {
			 System.err.println("[!] This OS is not supported, so file I/O may not work.");
			 oswarn = true;
		 }
		 button = new JButton("Set Distance");
		 help = new JButton("Help");
		 makeCSV = new JButton("Export to CSV");
		 panel = new JPanel(new BorderLayout());
		 label2 = new JLabel("Distance(m): ");
		 label2.setDisplayedMnemonic(KeyEvent.VK_N);
		 textfield = new JTextField();
		 textfield.setText("Enter a distance (i.e. 1.0)");
		 label2.setLabelFor(textfield);
		 panel.add(label2, BorderLayout.WEST);
		 panel.add(textfield, BorderLayout.CENTER);
		 if (oswarn)
		 {
			 frame = new JFrame("Makey Makey Speedometer v0.1b (OS Not Supported)");
		 }
		 else
		 {
			 frame = new JFrame("Makey Makey Speedometer v0.1b by Victor Du");
		 }
		 frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         label = new JLabel("speed", JLabel.CENTER);
         label.setFont(new Font("Comic Sans MS", Font.PLAIN, 22));
		 label.setText("Waiting for start input");  
		 frame.add(label);
		 frame.add(panel, BorderLayout.NORTH);
		 frame.add(button, BorderLayout.SOUTH);
		 frame.add(help, BorderLayout.EAST);
		 frame.add(makeCSV, BorderLayout.WEST);
		 frame.setSize(560, 420); 
		 frame.setVisible(true); // Display the frame
		 button.addActionListener(new ActionListener(){
			   public void actionPerformed(ActionEvent ae){
				   distance = Double.parseDouble(textfield.getText());
				   System.out.println(textfield.getText());
			   }
		});
		 help.addActionListener(new ActionListener(){
			   public void actionPerformed(ActionEvent ae){
				  JFrame kawaii = new JFrame("Help");
				  JLabel kawaiichan = new JLabel("kawaiichan", JLabel.CENTER);
				  kawaiichan.setText("Bind the first MakeyMakey to key [W] and the second to key [A]. This software is open source and freely redistributable (MIT License).");
				  kawaii.add(kawaiichan);
				  kawaii.setSize(800, 200);
				  kawaii.setVisible(true);
			   }
		});
		 makeCSV.addActionListener(new ActionListener(){
			   public void actionPerformed(ActionEvent ae){
				  try
				  {
					makeCSV(results);
				  } 
				  catch (IOException e)
				  {
					// TODO Auto-generated catch block
					e.printStackTrace();
				  }
			   }
		});
		try
		{
			GlobalScreen.registerNativeHook();
		} 
		catch (NativeHookException e)
		{
			// TODO Auto-generated catch block
			System.err.println("NativeHookException:" + e);
			e.printStackTrace();
		}
		GlobalScreen.getInstance().addNativeKeyListener(new Time());
		System.out.print("\n Ready to start. Please bind Makey sensor 1 to key [W] and sensor 2 to key [A].");
		while(true)
		{
			if (mode == 9)
			{
				break;
			}
			System.out.print("");
			if (mode == 2)
			{
				double spd = Math.round((distance/time) * 100.0) / 100.0;;
				label.setText("The speed is "+ spd + "m/s");
				label.setPreferredSize(new Dimension(400, 170));
				results.add(spd);
				drawChart(results);
				System.out.println("\n          This trial, the car travelled at " + spd + " meters per second. (Trial " + results.size() + ")\n");
				mode = 0;
			}
		}
		//drawChart(results);
		System.exit(0);
		}
	@Override
	public void nativeKeyPressed(NativeKeyEvent e)
	{
		// TODO Auto-generated method stub
		if (mode == 0 && NativeKeyEvent.getKeyText(e.getKeyCode()).matches("W"))
		{
			s = new Stopwatch();
			mode = 1;
			System.out.println("\n1st sensor triggered");
		}
		if (mode == 1 && NativeKeyEvent.getKeyText(e.getKeyCode()).matches("A"))
		{
			time = s.elapsedTime();
			mode = 2;
			System.out.println("\n2nd sensor triggered");
		}
	}
	@Override
	public void nativeKeyReleased(NativeKeyEvent e)
	{
		// TODO Auto-generated method stub
	}
	@Override
	public void nativeKeyTyped(NativeKeyEvent arg0)
	{
		// TODO Auto-generated method stub
	}
	public static double getAverage(ArrayList<Double> list)
	{
		double sum = 0.0;
		for (int i = 0; i < list.size(); i++)
		{
			sum += list.get(i);
		}
		return sum / (list.size());
	}
	public static void drawChart(ArrayList<Double> list)
	{
		System.out.println("\n\nTrial#       |            Speed (m/s)");
		for(int k = 0; k < list.size(); k++)
		{
			System.out.println(k+1+"            |            " +list.get(k));
		}
		System.out.println("===================================================");
		System.out.println("Average speed of all trials: " + Math.round((getAverage(list)) * 100.0) / 100.0 + "m/s");
	}
	public static void makeCSV(ArrayList<Double> list) throws IOException
	{
		System.out.println("Printing Excel CSV file...");
		File log;
		if (isUnix())
		{
			log = new File("/home/" + System.getProperty("user.name") + "/Documents/speedometer.csv");
		}
		else if (isWindows())
		{
			log = new File("C:/LanSchool Files/speedometer.csv");
		}
		else
		{
			System.out.println("[!] This OS isn't supported, attempting to save w/ UNIX fs...");
			log = new File("/home/" + System.getProperty("user.name") + "/Documents/speedometer.csv");
		}
		System.out.println("Log saved at: " + log);
		if(!log.exists())
		{
			System.out.println("Creating a new CSV file for you...");
			log.createNewFile();
		} 
		PrintWriter madoka = new PrintWriter(new FileWriter(log, true));
		madoka.println("Trial, Speed(m/s)");
		for(int k = 0; k < list.size(); k++)
		{
			madoka.println(k+1 + "," + list.get(k));
		}
		madoka.println("Avg=," + getAverage(list));
		madoka.flush();
		System.out.println("Done");
	}
	public static boolean isUnix()
	{
		return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 || OS.indexOf("mac") >= 0);
	}
	public static boolean isWindows() {
		return (OS.indexOf("win") >= 0);
	}
}