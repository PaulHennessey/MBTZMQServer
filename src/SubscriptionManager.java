
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// See here for good examples of singletons: https://www.journaldev.com/1377/java-singleton-design-pattern-best-practices-examples
// Also here: https://www.baeldung.com/java-singleton
// Note this is a cross-cutting concern and is implemented as a singleton to avoid having to pass it everywhere.
public final class SubscriptionManager {
	 
    private static SubscriptionManager INSTANCE;
         
    private HashMap<URI, ArrayList<Listener>> subscriptions = new HashMap<URI, ArrayList<Listener>>();
    
    private SubscriptionManager() {}
     
    public static SubscriptionManager getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new SubscriptionManager();
        }
         
        return INSTANCE;
    }
     
    public void addListener(URI uri, Listener listener)
    {
    	ArrayList<Listener> list;
    	
    	if(subscriptions.containsKey(uri)) 
    	{
			list = subscriptions.get(uri);
    	}
    	else
    	{
    		list = new ArrayList<Listener>();
    	}
    	
		list.add(listener);
		subscriptions.put(uri, list);    		    	
    }
    
    public void removeListener(URI uri, Listener listener)
    {    	
    	ArrayList<Listener> list = subscriptions.get(uri);
    	list.remove(listener);
    	
    	if(list.isEmpty())
    	{
    		subscriptions.remove(uri);
    	}
    }        
    
    // This finds all the subscriptions that match a given pattern. For example, the pattern "resource:///package/module1" 
    // could return "resource:///package/module1/element1" and "resource:///package/module1/element2".     
    public ArrayList<Listener> getListeners(URI uri)
    {    	    	
    	ArrayList<Listener> matches = new ArrayList<Listener>();
    	
    	for(URI key : subscriptions.keySet()) 
    	{
			Matcher matcher = Pattern.compile(key.toString()).matcher(uri.toString());
       		    	
    		if(matcher.find()) 
    		{
		        matches.addAll(subscriptions.get(key));
    		}
		}

    	return matches;
    }
	    
    public void moveElement(URI olduri, URI newuri)
    {
    	ArrayList<Listener> list = subscriptions.remove(olduri);
    	subscriptions.put(newuri, list);
    }
    
}


