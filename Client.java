import java.net.*;
import java.nio.charset.StandardCharsets;
import java.io.*;
import java.util.Scanner;

public class Client {
	
	public static void main (String[] args) {
	   
		String hostAddress;
	    int tcpPort;
	    int udpPort;

	    if (args.length != 3) {
	      System.out.println("ERROR: Provide 3 arguments");
	      System.out.println("\t(1) <hostAddress>: the address of the server");
	      System.out.println("\t(2) <tcpPort>: the port number for TCP connection");
	      System.out.println("\t(3) <udpPort>: the port number for UDP connection");
	      System.exit(-1);
	    }

	    hostAddress = args[0];
	    tcpPort = Integer.parseInt(args[1]);
	    udpPort = Integer.parseInt(args[2]);

	    Scanner sc = new Scanner(System.in);
	    while(sc.hasNextLine()) {
	      String cmd = sc.nextLine();
	      String[] tokens = cmd.split(" ");
	      boolean isTCP = true;
	      if (tokens[0].equals("setmode")) {
	        // TODO: set the mode of communication for sending commands to the server 
	        // and display the name of the protocol that will be used in future
	    	  if(tokens[1].equals("U")){
	    		  isTCP = false;
	    		  System.out.println("Mode: UDP");
	    	  }
	    	  else if(tokens[1].equals("T")){
	    		  isTCP = true;
	    		  System.out.println("Mode: TCP");
	    	  }
	    	  else{
	    		  System.out.println("INVALID");
	    	  }
	      }
	      else if (tokens[0].equals("purchase")) {
	        // TODO: send appropriate command to the server and display the
	        // appropriate responses form the server
	    	  String userName = tokens[1];
    		  String item = tokens[2];
    		  String quantity = tokens[3];
    		  String toSend = "purchase " + userName + " " + item + " " + quantity;
	    	  if(isTCP){
	    		  String messageReceived;
	    		  try{
	    			  Socket socket = new Socket(hostAddress, tcpPort);
	    			  DataOutputStream out = new DataOutputStream(socket.getOutputStream());
	    			  BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	    			  out.writeBytes(toSend + '\n');
	    			  messageReceived = in.readLine();
	    			  System.out.println("Received from Server: " + messageReceived);
	    			  socket.close();
	    		  }
	    		  catch (UnknownHostException e) {System.err.println(e);} 
	    		  catch (SocketException e) {System.err.println(e);}
	    		  catch (IOException e){System.err.println(e);}	    		  
	    	  }
	    	  else{
	    		  DatagramPacket rPacket, sPacket;
	    		  int len = 1024;
	    		  byte[] rBuffer = new byte[len];
	    		  try{
	    			  InetAddress ia = InetAddress.getByName(hostAddress);
	    			  DatagramSocket datasocket = new DatagramSocket();
	    			  byte[] buffer = new byte[toSend.length()];
	    			  buffer = toSend.getBytes();
	    			  sPacket = new DatagramPacket(buffer, buffer.length, ia, udpPort);
	    			  datasocket.send(sPacket);
	    			  rPacket = new DatagramPacket(rBuffer, rBuffer.length);
	    			  datasocket.receive(rPacket);
	    			  String retstring = new String(rPacket.getData(), 0, rPacket.getLength());
	    			  System.out.println("Received from Server: " + retstring); 
	    		  }
	    		  catch (UnknownHostException e) {System.err.println(e);} 
	    		  catch (SocketException e) {System.err.println(e);}
	    		  catch (IOException e){System.err.println(e);}
	    	  }
	      } else if (tokens[0].equals("cancel")) {
	        // TODO: send appropriate command to the server and display the
	        // appropriate responses form the server
	    	  String ID = tokens[1];
	    	  String toSend = "cancel " + ID;
	    	  if(isTCP){
	    		  String messageReceived;
	    		  try{
	    			  Socket socket = new Socket(hostAddress, tcpPort);
	    			  DataOutputStream out = new DataOutputStream(socket.getOutputStream());
	    			  BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	    			  out.writeBytes(toSend + '\n');
	    			  messageReceived = in.readLine();
	    			  System.out.println("Received from Server: " + messageReceived);
	    			  socket.close();
	    		  }
	    		  catch (UnknownHostException e) {System.err.println(e);} 
	    		  catch (SocketException e) {System.err.println(e);}
	    		  catch (IOException e){System.err.println(e);}	 
	    	  }
	    	  else{
	    		  DatagramPacket rPacket, sPacket;
	    		  int len = 1024;
	    		  byte[] rBuffer = new byte[len];
	    		  try{
	    			  InetAddress ia = InetAddress.getByName(hostAddress);
	    			  DatagramSocket datasocket = new DatagramSocket();
	    			  byte[] buffer = new byte[toSend.length()];
	    			  buffer = toSend.getBytes();
	    			  sPacket = new DatagramPacket(buffer, buffer.length, ia, udpPort);
	    			  datasocket.send(sPacket);
	    			  rPacket = new DatagramPacket(rBuffer, rBuffer.length);
	    			  datasocket.receive(rPacket);
	    			  String retstring = new String(rPacket.getData(), 0, rPacket.getLength());
	    			  System.out.println("Received from Server: " + retstring); 
	    		  }
	    		  catch (UnknownHostException e) {System.err.println(e);} 
	    		  catch (SocketException e) {System.err.println(e);}
	    		  catch (IOException e){System.err.println(e);}
	    	  }
	      } else if (tokens[0].equals("search")) {
	        // TODO: send appropriate command to the server and display the
	        // appropriate responses form the server
	    	  String user = tokens[1];
	    	  String toSend = "search " + user;
	    	  if(isTCP){
	    		  String messageReceived;
	    		  try{
	    			  Socket socket = new Socket(hostAddress, tcpPort);
	    			  DataOutputStream out = new DataOutputStream(socket.getOutputStream());
	    			  BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	    			  out.writeBytes(toSend + '\n');
	    			  messageReceived = in.readLine();
	    			  System.out.println("Received from Server: " + messageReceived);
	    			  socket.close();
	    		  }
	    		  catch (UnknownHostException e) {System.err.println(e);} 
	    		  catch (SocketException e) {System.err.println(e);}
	    		  catch (IOException e){System.err.println(e);}	 
	    	  }
	    	  else{
	    		  DatagramPacket rPacket, sPacket;
	    		  int len = 1024;
	    		  byte[] rBuffer = new byte[len];
	    		  try{
	    			  InetAddress ia = InetAddress.getByName(hostAddress);
	    			  DatagramSocket datasocket = new DatagramSocket();
	    			  byte[] buffer = new byte[toSend.length()];
	    			  buffer = toSend.getBytes();
	    			  sPacket = new DatagramPacket(buffer, buffer.length, ia, udpPort);
	    			  datasocket.send(sPacket);
	    			  rPacket = new DatagramPacket(rBuffer, rBuffer.length);
	    			  datasocket.receive(rPacket);
	    			  String retstring = new String(rPacket.getData(), 0, rPacket.getLength());
	    			  System.out.println("Received from Server: " + retstring); 
	    		  }
	    		  catch (UnknownHostException e) {System.err.println(e);} 
	    		  catch (SocketException e) {System.err.println(e);}
	    		  catch (IOException e){System.err.println(e);}
	    	  }
	      } else if (tokens[0].equals("list")) {
	        // TODO: send appropriate command to the server and display the
	        // appropriate responses form the server
	    	  if(isTCP){
	    		  String messageReceived;
	    		  try{
	    			  Socket socket = new Socket(hostAddress, tcpPort);
	    			  DataOutputStream out = new DataOutputStream(socket.getOutputStream());
	    			  BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	    			  out.writeBytes(tokens[0] + '\n');
	    			  messageReceived = in.readLine();
	    			  System.out.println("Received from Server: " + messageReceived);
	    			  socket.close();
	    		  }
	    		  catch (UnknownHostException e) {System.err.println(e);} 
	    		  catch (SocketException e) {System.err.println(e);}
	    		  catch (IOException e){System.err.println(e);}	 
	    	  }
	    	  else{
	    		  DatagramPacket rPacket, sPacket;
	    		  int len = 1024;
	    		  byte[] rBuffer = new byte[len];
	    		  try{
	    			  InetAddress ia = InetAddress.getByName(hostAddress);
	    			  DatagramSocket datasocket = new DatagramSocket();
	    			  byte[] buffer = new byte[tokens[0].length()];
	    			  buffer = tokens[0].getBytes();
	    			  sPacket = new DatagramPacket(buffer, buffer.length, ia, udpPort);
	    			  datasocket.send(sPacket);
	    			  rPacket = new DatagramPacket(rBuffer, rBuffer.length);
	    			  datasocket.receive(rPacket);
	    			  String retstring = new String(rPacket.getData(), 0, rPacket.getLength());
	    			  System.out.println("Received from Server: " + retstring); 
	    		  }
	    		  catch (UnknownHostException e) {System.err.println(e);} 
	    		  catch (SocketException e) {System.err.println(e);}
	    		  catch (IOException e){System.err.println(e);}
	    	  }
	      } else {
	        System.out.println("ERROR: No such command");
	      }
	    }
	  }
}
