

import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// See here for good examples of singletons: https://www.journaldev.com/1377/java-singleton-design-pattern-best-practices-examples
// Also here: https://www.baeldung.com/java-singleton
// Note this is a cross-cutting concern and is implemented as a singleton to avoid having to pass it everywhere.
public final class ElementManager {
	 
    private static ElementManager INSTANCE;         
	private HashMap<URI, Element> elements = new HashMap<URI, Element>();
    
    private ElementManager() {}
     
    public static ElementManager getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new ElementManager();
        }
         
        return INSTANCE;
    }
     
    public void addElement(URI uri, Element element)
    {
		elements.put(uri, element);    		    	
    }
    
    public void removeElement(URI uri)
    {    	
		elements.remove(uri);
    }    
    
    public Element getElement(URI uri)
    {
    	return elements.get(uri);
    }
    
    public void moveElement(URI olduri, URI newuri)
    {
    	Element element = elements.remove(olduri);
    	elements.put(newuri, element);
    }

    public ArrayList<Element> getElements(String pattern)
    {
    	Pattern p = Pattern.compile(pattern);
    	    	
    	ArrayList<Element> matches = new ArrayList<Element>();
    	
    	for(URI key : elements.keySet()) 
    	{
    		Matcher m = p.matcher(key.toString());
    		    		
    		if(m.find()) 
    		{
		        matches.add(elements.get(key));
    		}
		}

    	return matches;
    }
    
    public ArrayList<Element> getElementsGlob(String pattern)
    {
    	FileSystem fileSystem = FileSystems.getDefault();
		PathMatcher pathMatcher = fileSystem.getPathMatcher("glob:" + pattern);
    	
    	ArrayList<Element> matches = new ArrayList<Element>();
    	
    	for(URI key : elements.keySet()) 
    	{
    		Path path = Paths.get(key.toString());
    		
    		if(pathMatcher.matches(path)) 
    		{
		        matches.add(elements.get(key));
    		}
		}

    	return matches;
    }    
    
    public int count()
    {
    	return elements.size();
    }
    
    public HashMap<URI, Element> getElements()
    {
    	return elements;
    }
}


