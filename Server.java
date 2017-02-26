import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

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
  }
}