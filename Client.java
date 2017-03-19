import java.net.*;
import java.io.*;
import java.util.Scanner;

public class Client {
	
	public static void main (String[] args) {
	    Scanner sc = new Scanner(System.in);
	    int numServer = sc.nextInt();
	    
	    for (int i = 0; i < numServer; i++) {
	      // TODO: parse inputs to get the ips and ports of servers
	    }
	    while(sc.hasNextLine()) {
	      String cmd = sc.nextLine();
	      String[] tokens = cmd.split(" ");
	      if (tokens[0].equals("purchase")) {
	    	  String userName = tokens[1];
    		  String item = tokens[2];
    		  String quantity = tokens[3];
    		  String toSend = "purchase " + userName + " " + item + " " + quantity;
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
	      } else if (tokens[0].equals("cancel")) {
	    	  String ID = tokens[1];
	    	  String toSend = "cancel " + ID;
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
	      } else if (tokens[0].equals("search")) {
	    	  String user = tokens[1];
	    	  String toSend = "search " + user;
	    	  String messageReceived;
	    	  try{
	    		  Socket socket = new Socket(hostAddress, tcpPort);
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
	      } else if (tokens[0].equals("list")) {
	    	  try{
	    		  Socket socket = new Socket(hostAddress, tcpPort);
	    		  DataOutputStream out = new DataOutputStream(socket.getOutputStream());
	    		  BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	    		  out.writeBytes(tokens[0] + '\n');
	    		    String line;
	    		    while ((line = in.readLine()) != null)
	    		    	System.out.println(line);
	    		  socket.close();
	    	  }
	    	  catch (UnknownHostException e) {System.err.println(e);} 
	    	  catch (SocketException e) {System.err.println(e);}
	    	  catch (IOException e){System.err.println(e);}	 
	      } else {
	        System.out.println("ERROR: No such command");
	      }
	    }
	  }
}