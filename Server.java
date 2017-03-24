import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
	
    ArrayList<String> inventory = new ArrayList<String>();
    ArrayList<Integer> quantity = new ArrayList<Integer>();
    int orderNum = 1;
    ArrayList<Order> orders = new ArrayList<Order>();
    static ArrayList<Server> serverList = new ArrayList<Server>();
    static String[] hostAddresses;
    static int[] tcpPorts;
    int sClock = 0;
	
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
	  	Scanner sc = new Scanner(System.in);
	    int myID = sc.nextInt();
	    int numServer = sc.nextInt();
	    String inventoryPath = sc.next();
	    hostAddresses = new String[numServer];
	    tcpPorts = new int[numServer];
	    System.out.println("[DEBUG] my id: " + myID);
	    System.out.println("[DEBUG] numServer: " + numServer);
	    System.out.println("[DEBUG] inventory path: " + inventoryPath);
	    for (int i = 0; i < numServer; i++) {
	        // TODO: parse inputs to get the ips and ports of servers
	    	String str = sc.next();
	        System.out.println("address for server " + i + ": " + str);
	        String[] line = sc.nextLine().split(":");
		    hostAddresses[i] = line[0];
		    tcpPorts[i] = Integer.parseInt(line[1]);
	    
		    Server server = new Server(inventoryPath);  // parse the inventory file
		    server.sClock++;// TODO: handle request from clients
		    serverList.add(server);
		    TCPListener tl = new TCPListener(server, tcpPorts[i]);
			tl.start(); 
	    }  
  }
  
  //**********************************************************************
  //***pretty sure everything below this point shouldn't need to change***
  //**********************************************************************
	    
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
  
  public void serverUpdate(String[] data, int clock){
		  String message = "UPDATE: " + data + " " + clock;
		  if(clock > this.sClock)
			  this.sClock = clock + 1;
		  int i = 0;
		  for(Server s : serverList){
			  try{
				  ServerSocket listener = new ServerSocket(tcpPorts[i]);
				  Socket sock = listener.accept();
				  DataOutputStream output = new DataOutputStream(sock.getOutputStream());
				  output.writeBytes(message);
				  sock.close();
				  listener.close();
				  i++;
			  }
			  catch(IOException e){e.printStackTrace();}			  
		  }
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
			int clock = 0;
			while (result == null)
			{
				if (data[0].equals("purchase"))
				{
					result = server.purchase(data);
					server.serverUpdate(data, clock);
				}
				else if (data[0].equals("cancel"))
				{
					result = server.cancel(data);
					server.serverUpdate(data, clock);
				}
				else if (data[0].equals("search"))
				{
					result = server.search(data);
				}
				else if (data[0].trim().equals("list"))
				{
					result = server.list();
				}
				else if (data[0].equals("UPDATE: "))
				{
					String[] newData = new String[data.length - 1];
					for(int i = 1; i < data.length; i++)
						newData[i-1] = data[1];
					if(newData[0].equals("purchase"))
					{
						result = server.purchase(newData);
						
					}
						
					else if(newData[0].equals("cancel"))
					{
						result = server.cancel(newData);
					}
						
				}
				clock++;
			}
			output.writeBytes(result);
			sock.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
