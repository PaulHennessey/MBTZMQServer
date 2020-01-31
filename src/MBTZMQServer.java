//Hello World server in Java
//Binds REP socket to tcp://*:5555
//Expects "Hello" from client, replies with "World"

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.Properties;

import org.zeromq.SocketType;
import org.zeromq.ZMQ;

import com.thetransactioncompany.jsonrpc2.JSONRPC2ParseException;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.server.Dispatcher;

import org.zeromq.ZContext;

public class MBTZMQServer 
{
	ElementManager Elements = ElementManager.getInstance();
	
	public static void main(String[] args) throws Exception 
	{
		MBTZMQServer server = new MBTZMQServer();
		server.Start();
	}
	
	public void Start() throws JSONRPC2ParseException, FileNotFoundException, IOException 
	{		
		System.out.println("Instantiating server...");
		InstantiateData();
		
		try (ZContext context = new ZContext()) 
		{
			ZMQ.Socket socket = context.createSocket(SocketType.REP);
			socket.bind("tcp://*:5555");

    		Dispatcher dispatcher =  new Dispatcher();
    		dispatcher.register(new ModelHandler());
			
			while (!Thread.currentThread().isInterrupted()) 
			{							
				String message = socket.recvStr();
				
				JSONRPC2Request request = JSONRPC2Request.parse(message);
				System.out.println("Request received: " + request);
				
				JSONRPC2Response response = dispatcher.process(request, null);
				System.out.println("Response sent: " + response);
				
				socket.send(response.toString());
			}
		}
    	catch (JSONRPC2ParseException e) 
    	{
			System.out.println(e.getMessage());
			return;
		}            	            		
	}
	
    private void InstantiateData() throws FileNotFoundException, IOException
    {
    	System.out.println("Generating elements...");
		 
		Properties appProps = new Properties();
		appProps.load(new FileInputStream(System.getProperty("user.dir") + "/res/app.properties"));
		 		   		  
		int packageCount = Integer.parseInt(appProps.getProperty("packages").trim());		
		int moduleCount = Integer.parseInt(appProps.getProperty("modules").trim());
		int elementCount = Integer.parseInt(appProps.getProperty("elements").trim());

		if(packageCount > 0)
		{
			InstantiatePackages(packageCount, moduleCount, elementCount);
		}
		else if(moduleCount > 0)
		{
			InstantiateModules(moduleCount, elementCount);
		}
		else if(elementCount > 0)
		{
			InstantiateElements(elementCount);
		}			
		
		System.out.println(Elements.count() + " elements generated");		
    }
    
    private void InstantiatePackages(int packageCount, int moduleCount, int elementCount)
    {
    	for (int i = 1; i <= packageCount; i++)
    	{    	
	    	for (int j = 1; j <= moduleCount; j++)
	    	{
		    	for (int k = 1; k <= elementCount; k++) 
		    	{
		    		URI uri = URI.create("p" + i + "/" + "m" + j + "/" + "e" + k + "/");
		    		Element element = new Element(uri, "some content");    		
		    		Elements.addElement(uri, element);			
				}
	    	}
    	}    	
    }
    
    private void InstantiateModules(int moduleCount, int elementCount)
    {
    	for (int i = 0; i < moduleCount; i++)
    	{
	    	for (int j = 0; j < elementCount; j++) 
	    	{
	    		URI uri = URI.create("m" + i + "/" + "e" + j + "/");
	    		Element element = new Element(uri, "some content");    		
	    		Elements.addElement(uri, element);			
			}
    	}
    }
    
    private void InstantiateElements(int elementCount)
    {
    	for (int i = 0; i < elementCount; i++) 
    	{
    		URI uri = URI.create("e" + i + "/");
    		Element element = new Element(uri, "some content");    		
    		Elements.addElement(uri, element);			
		}
    }        	
	
}
