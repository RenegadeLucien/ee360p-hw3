import java.net.*;
import java.io.*;
import java.util.Scanner;

public class Client {
	
	public static void main (String[] args) {
	    Scanner sc = new Scanner(System.in);
	    int numServer = sc.nextInt();
	    String[] hostAddresses = new String[numServer];
	    int[] tcpPorts = new int[numServer];
	    //TODO: parse the ips and ports of servers
	    for (int i = 0; i < numServer; i++) {
	      String[] line = sc.nextLine().split(":");
	      hostAddresses[i] = line[0];
	      tcpPorts[i] = Integer.parseInt(line[1]);
	    }
	    int i = 0;
	    while(sc.hasNextLine()) {
	      String cmd = sc.nextLine();
	      String[] tokens = cmd.split(" ");
	      if (tokens[0].equals("purchase")) {
	    	  String userName = tokens[1];
    		  String item = tokens[2];
    		  String quantity = tokens[3];
    		  String toSend = "purchase " + userName + " " + item + " " + quantity;
    		  String messageReceived = null;
    		  while (messageReceived == null && i < numServer)
    		  {
	    	  try{
	    		  Socket socket = new Socket(hostAddresses[i], tcpPorts[i]);
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
	    	  i++;
    		  }
	      } else if (tokens[0].equals("cancel")) {
	    	  String ID = tokens[1];
	    	  String toSend = "cancel " + ID;
	    	  String messageReceived = null;
	    	  while (messageReceived == null && i < numServer)
	    	  {
	    	  try{
	    		  Socket socket = new Socket(hostAddresses[i], tcpPorts[i]);
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
	    	  i++;
    		  }
	      } else if (tokens[0].equals("search")) {
	    	  String user = tokens[1];
	    	  String toSend = "search " + user;
	    	  String messageReceived = null;
	    	  while (messageReceived == null && i < numServer)
	    	  {
	    	  try{
	    		  Socket socket = new Socket(hostAddresses[i], tcpPorts[i]);
	    		  DataOutputStream out = new DataOutputStream(socket.getOutputStream());
	    		  BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	    		  out.writeBytes(toSend + '\n');
	    		  String line;
	    		    while ((line = in.readLine()) != null)
	    		    	System.out.println(line);
	    		  socket.close();
	    	  }
	    	  catch (UnknownHostException e) {System.err.println(e);} 
	    	  catch (SocketException e) {System.err.println(e);}
	    	  catch (IOException e){System.err.println(e);}
	    	  i++;
    		  }
	      } else if (tokens[0].equals("list")) {
	    	  boolean messageReceived = false;
	    	  while (messageReceived == false && i < numServer)
	    	  {
	    	  try{
	    		  Socket socket = new Socket(hostAddresses[i], tcpPorts[i]);
	    		  DataOutputStream out = new DataOutputStream(socket.getOutputStream());
	    		  BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	    		  out.writeBytes(tokens[0] + '\n');
	    		    String line;
	    		    while ((line = in.readLine()) != null)
	    		    	System.out.println(line);
	    		  socket.close();
	    		  messageReceived = true;
	    	  }
	    	  catch (UnknownHostException e) {System.err.println(e);} 
	    	  catch (SocketException e) {System.err.println(e);}
	    	  catch (IOException e){System.err.println(e);}	 
	    	  i++;
    		  }
	      } else {
	        System.out.println("ERROR: No such command");
	      }
	    }
	  }
}