
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URI;
import java.util.ArrayList;
import java.util.Map;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Error;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Notification;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.server.MessageContext;
import com.thetransactioncompany.jsonrpc2.server.RequestHandler;

public class ModelHandler implements RequestHandler
{		
	ElementManager Elements = ElementManager.getInstance();
	SubscriptionManager Subscriptions = SubscriptionManager.getInstance();
		
	// Reports the method names of the handled requests
	public String[] handledRequests() 
	{		
	    return new String[]{"create", "model.create_element", 
	    					"update", "model.update_element", 
	    					"subscribe", "model.subscribe_to_element", 
	    					"unsubscribe", "model.unsubscribe_from_element", 
	    					"move", "model.move_element",
	    					"select", "model.select_elements"};
	}
		   		   
	// Processes the requests
	public JSONRPC2Response process(JSONRPC2Request req, MessageContext ctx) 
	{		
        if (req.getMethod().equals("model.create_element") || req.getMethod().equals("create")) 
        {		    
        	JSONObject rawResult = CreateElement(req);            		    
    	  	return new JSONRPC2Response(rawResult.toString(), req.getID()); 	   
        }
        else if (req.getMethod().equals("model.update_element") || req.getMethod().equals("update")) 
        {		    
        	JSONObject rawResult = UpdateElement(req);            		    
    	  	return new JSONRPC2Response(rawResult.toString(), req.getID()); 	   
        }	            
        else if (req.getMethod().equals("model.subscribe_to_element") || req.getMethod().equals("subscribe")) 
        {		    
        	JSONObject rawResult = SubscribeToElement(req);              	
    	  	return new JSONRPC2Response(rawResult.toString(), req.getID()); 	   
        }
        else if (req.getMethod().equals("model.unsubscribe_from_element") || req.getMethod().equals("unsubscribe")) 
        {		    
        	JSONObject rawResult = UnsubscribeFromElement(req);              	
    	  	return new JSONRPC2Response(rawResult.toString(), req.getID()); 	   
        }        
        else if (req.getMethod().equals("model.move_element") || req.getMethod().equals("move")) 
        {		    
        	JSONObject rawResult = MoveElement(req);              	
    	  	return new JSONRPC2Response(rawResult.toString(), req.getID()); 	   
        }
        else if (req.getMethod().equals("model.select_elements") || req.getMethod().equals("select")) 
        {		    
        	JSONObject rawResult = SelectElements(req);              	
    	  	return new JSONRPC2Response(rawResult.toString(), req.getID()); 	   
        }                        
        else 
	    {		    			
	    	return new JSONRPC2Response(JSONRPC2Error.METHOD_NOT_FOUND, req.getID());
        }
    }
		
	private JSONObject CreateElement(JSONRPC2Request req)
	{		
    	Map<String, Object> params = req.getNamedParams();
    	
    	URI uri = URI.create(params.get("uri").toString());
    	String content = params.get("content").toString();
    	
    	Element element = new Element(uri, content);    	
    	Elements.addElement(uri, element);
    	
    	return new JSONObject("{\"result\":\"Element " + element.getURI() + " created...\"}");
	}
	
	private JSONObject SubscribeToElement(JSONRPC2Request req)
    {		
    	Map<String, Object> params = req.getNamedParams();
    	    	
    	String host = params.get("host").toString();
    	String port = params.get("port").toString();

    	URI uri = URI.create(params.get("uri").toString());
    	Listener listener = new Listener(host, port);
    	
    	Subscriptions.addListener(uri, listener);
    	
    	return new JSONObject("{\"result\":\"Element " + uri + " subscribed...\"}");
    }
    
	private JSONObject UnsubscribeFromElement(JSONRPC2Request req)
    {		
    	Map<String, Object> params = req.getNamedParams();
    	
    	String host = params.get("host").toString();
    	String port = params.get("port").toString();

    	URI uri = URI.create(params.get("uri").toString());    	
    	Listener listener = new Listener(host, port);

    	Subscriptions.removeListener(uri, listener);
    	    	
    	return new JSONObject("{\"result\":\"Element " + uri + " unsubscribed...\"}");
    }
	
	private JSONObject UpdateElement(JSONRPC2Request req)
	{
    	Map<String, Object> params = req.getNamedParams();
    	
    	URI uri = URI.create(params.get("uri").toString());
    	String content = params.get("content").toString();
    	
		Element element = Elements.getElement(uri);		
		element.setContent(content);    
					
		ArrayList<Listener> listeners = Subscriptions.getListeners(uri);
		
    	for(Listener listener : listeners)
    	{    		
            try 
            (                            		
            	Socket socket = new Socket(listener.getUrl(), listener.getPort());
        		PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            ) 
            {                   	          
            	JSONRPC2Notification notification = new JSONRPC2Notification("{\"result\":\"Element content set to " + content + "\"}");
            	System.out.println("Notification: " + notification.toString());
            	out.println(notification.toString());
            } 
            catch (IOException e) 
            {
                System.out.println(e.getMessage());
            }		
    	}
				
		return new JSONObject("{\"result\":\"Element " + uri + " updated...\"}");
	}
	
	private JSONObject MoveElement(JSONRPC2Request req)
    {		
    	Map<String, Object> params = req.getNamedParams();
    	
    	URI olduri = URI.create(params.get("olduri").toString());    	
    	URI newuri = URI.create(params.get("newuri").toString());
    	
    	Elements.moveElement(olduri, newuri);
    	Subscriptions.moveElement(olduri, newuri);
    	    	
    	return new JSONObject("{\"result\":\"Element " + olduri + " moved to " + newuri + " \"}");
    }	

	private JSONObject SelectElements(JSONRPC2Request req)
    {		
    	Map<String, Object> params = req.getNamedParams();
    	
    	String pattern = params.get("pattern").toString();
    	    	
    	System.out.println("Total number of elements: " + Elements.count());
    	
    	ArrayList<Element> elements = Elements.getElements(pattern);
    	    	
    	System.out.println("Number of elements selected: " + elements.size());
    	
    	ArrayList<String> serializedElements = new ArrayList<String>();
    	JSONObject result = new JSONObject();
    	
    	for(Element element : elements)
    	{
    		serializedElements.add(new Gson().toJson(element));
    	}
    	
    	result.put("elements", serializedElements);
    	return result;
    }	
}
