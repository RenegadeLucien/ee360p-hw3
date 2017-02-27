import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
  public static void main (String[] args) {
    int tcpPort;
    int udpPort;
    if (args.length != 3) {
      System.out.println("ERROR: Provide 3 arguments");
      System.out.println("\t(1) <tcpPort>: the port number for TCP connection");
      System.out.println("\t(2) <udpPort>: the port number for UDP connection");
      System.out.println("\t(3) <file>: the file of inventory");

      System.exit(-1);
    }
    tcpPort = Integer.parseInt(args[0]);
    udpPort = Integer.parseInt(args[1]);
    String fileName = args[2];
    
    class Order
    {
    	int number;
    	String item;
    	String username;
    	int quant;
    	
    	public Order(int on, String i, String u, int q)
    	{
    		number = on;
    		item = i;
    		username = u;
    		quant = q;
    	}
    }
    
    int orderNum = 1;
    ArrayList<Order> orders = new ArrayList<Order>();

    // parse the inventory file
    ArrayList<String> inventory = new ArrayList<String>();
    ArrayList<Integer> quantity = new ArrayList<Integer>();
    String line;
    try {
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		while((line = reader.readLine()) != null)
		{
			String[] thisLine = line.split(" ");
			inventory.add(thisLine[0]);
			quantity.add(Integer.parseInt(thisLine[1]));
		}
		reader.close();
	} catch (Exception e) {
		e.printStackTrace();
	}
    // TODO: handle request from clients
    DatagramPacket datapacket,returnpacket;
    int len = 1024;
    try {
    	@SuppressWarnings("resource")
		DatagramSocket datasocket = new DatagramSocket(udpPort);
    	byte [] buf = new byte [len];
    	while (true) {
		    datapacket = new DatagramPacket(buf,buf.length);
		    Arrays.fill(buf,(byte)0);
		    datasocket.receive(datapacket);
		    String[] data = new String(buf).split("\\s+");
		    byte[] returnData;
	    	String s = "";
		    if(data[0].equals("purchase"))
		    {
		    	for (int i = 0; i < inventory.size(); i++)
		    	{
		    		if (data[2].equals(inventory.get(i)))
		    		{
		    			int numPurchased = Integer.parseInt(data[3].trim());
		    			if (numPurchased > quantity.get(i))
		    			{
		    				s = "Not Available - Not enough items";
		    				break;
		    			}
		    			else
		    			{
		    				orders.add(new Order(orderNum, data[2], data[1], numPurchased));
		    				quantity.set(i, quantity.get(i) - numPurchased);
		    				s = "Your order has been placed, " + orderNum + " " + data[1] + " " + data[2] + " " + numPurchased;
		    				orderNum+=1;
		    				break;
		    			}
		    		}
		    	}
		    	if (s.equals(""))
		    	{
		    		s = "Not Available - We do not sell this product";
		    	}
		    }
		    else if(data[0].equals("cancel"))
		    {
		    	int number = Integer.parseInt(data[1].trim());
		    	boolean exists = false;
		    	for (Order o: orders)
		    	{
		    		if (number == o.number)
		    		{
		    			for (int i = 0; i < inventory.size(); i++)
				    	{
		    				if (o.item.equals(inventory.get(i)))
		    				{
		    					quantity.set(i, quantity.get(i) + o.quant);
		    				}
				    	}
		    			orders.remove(o);
		    			s = "Order " + number + " is canceled";
		    			exists = true;
		    			break;
		    		}
		    	}
		    	if (exists == false)
		    	{
		    		s = number + " not found, no such order";
		    	}
		    }
		    else if(data[0].equals("search"))
		    {
		    	String user = data[1].trim();
		    	for (Order o: orders)
		    	{
		    		if (o.username.equals(user))
		    		{
		    			if (s.equals("") == false)
		    			{
		    				s.concat("" + '\n');
		    			}
		    			s+=(o.number + " " + o.item + " " + o.quant + " ");
		    		}
		    	}
		    	if (s.equals(""))
		    	{
		    		s = "No order found for " + user;
		    	}
		    }
		    else if(data[0].trim().equals("list"))
		    {
		    	for (int i = 0; i < inventory.size(); i++)
		    	{
		    		s+=(inventory.get(i) + " " + quantity.get(i) + '\n');
		    	}
		    }
		    returnData = s.getBytes();
		    returnpacket = new DatagramPacket( 
		    		returnData ,
		    		returnData.length, 
		    		datapacket.getAddress() , 
		    		datapacket.getPort()) ;
		    datasocket.send(returnpacket);
		    /*ServerSocket listener = new ServerSocket(tcpPort); 
	    	Socket s;
	    	while (( s = listener.accept ()) != null ) {
	    		Thread t = new Thread();
	    		t.start();
	    	}	*/
    	}
    } catch (Exception e) {
    System.err.println(e);
    }
  }
}