import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
	
    ArrayList<String> inventory = new ArrayList<String>();
    ArrayList<Integer> quantity = new ArrayList<Integer>();
    int orderNum = 1;
    ArrayList<Order> orders = new ArrayList<Order>();
	
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
    
    public Server(String fileName)
    {
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
    }
    
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
    
	// parse the inventory file
    Server server = new Server(fileName);
    // TODO: handle request from clients

    TCPListener tl = new TCPListener(server, tcpPort);
    UDPListener ul = new UDPListener(server, udpPort);
    
    ul.start();
    tl.start();

  }
  
  public synchronized String purchase(String[] data)
  {
	  String s = null;
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
  	return s;
  }
  
  public synchronized String cancel(String[] data)
  {
	 String s = null;
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
  	return s;
  }
  
  public synchronized String search(String[] data)
  {
	  String s = "";
	  String user = data[1].trim();
  	for (Order o: orders)
  	{
  		if (o.username.equals(user))
  		{
  			if (s.equals("") == false)
  			{
  				s.concat("" + '\n');
  			}
  			s+=(o.number + " " + o.item + " " + o.quant + "\n");
  		}
  	}
  	if (s.equals(""))
  	{
  		s = "No order found for " + user;
  	}
  	return s;
  }
  
  public synchronized String list()
  {
	  String s = "";
	for (int i = 0; i < inventory.size(); i++)
  	{
  		s+=(inventory.get(i) + " " + quantity.get(i) + '\n');
  	}
	return s;
  }
}

class TCPListener extends Thread
{
	Server server = null;
	int tcpPort = 0;
	
	public TCPListener(Server sv, int p)
	{
		server = sv;
		tcpPort = p;
	}
	
	@SuppressWarnings("resource")
	@Override
	public void run() {
			ServerSocket listener = null;
			try {
				listener = new ServerSocket(tcpPort);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		while (true) {
			try {
	    	Socket sock = listener.accept();
	    	TCPThread t = new TCPThread(sock, server);
	    	t.run();
			} 
	    	catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}		
	}	
}

class UDPListener extends Thread
{
	Server server = null;
	int udpPort = 0;
	
	public UDPListener(Server sv, int u)
	{
		server = sv;
		udpPort = u;
	}
	@Override
	public void run() {
	    DatagramPacket datapacket;
	    int len = 1024;
	    DatagramSocket datasocket = null;
		try {
			datasocket = new DatagramSocket(udpPort);
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	while (true) {	    	
			try {
	    	byte [] buf = new byte [len];
			datapacket = new DatagramPacket(buf,buf.length);
			Arrays.fill(buf,(byte)0);
			datasocket.receive(datapacket);
			UDPThread u = new UDPThread(datasocket, datapacket, server);
			u.run();
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}		
	}
}

class TCPThread extends Thread
{
	DataInputStream input = null;
	DataOutputStream output = null;
	Socket sock = null;
	Server server = null;
	
	public TCPThread(Socket s, Server sv)
	{
		sock = s;
		server = sv;
	}
	
	public void run() 
	{
		try {
			input = new DataInputStream(sock.getInputStream());
			output = new DataOutputStream(sock.getOutputStream());
			BufferedReader b = new BufferedReader(new InputStreamReader(input));
			String[] data = b.readLine().split("\\s+");
			String result = null;
			while (result == null)
			{
				if (data[0].equals("purchase"))
				{
					result = server.purchase(data);
				}
				else if (data[0].equals("cancel"))
				{
					result = server.cancel(data);
				}
				else if (data[0].equals("search"))
				{
					result = server.search(data);
				}
				else if (data[0].trim().equals("list"))
				{
					result = server.list();
				}
			}
			output.writeBytes(result);
			sock.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

class UDPThread extends Thread
{
	DatagramSocket sock = null;
	DatagramPacket input = null;
	Server server = null;
	
	public UDPThread(DatagramSocket ds, DatagramPacket dp, Server sv)
	{
		sock = ds;
		input = dp;
		server = sv;
	}
	
	public void run()
	{
		String[] data = new String(input.getData()).split("\\s+");
		String result = null;
		while (result == null)
		{
			if (data[0].equals("purchase"))
			{
				result = server.purchase(data);
			}
			else if (data[0].equals("cancel"))
			{
				result = server.cancel(data);
			}
			else if (data[0].equals("search"))
			{
				result = server.search(data);
			}
			else if (data[0].trim().equals("list"))
			{
				result = server.list();
			}
		}
		byte[] returnData;
		returnData = result.getBytes();
		DatagramPacket returnpacket = new DatagramPacket( 
		    	returnData ,
		    	returnData.length, 
		    	input.getAddress() , 
		    	input.getPort()) ;
		try {
			sock.send(returnpacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}