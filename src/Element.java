
import java.net.URI;

public class Element
{
	private URI _uri;
	private String _content;
		
	public Element(URI uri, String content) 
	{
		_uri = uri;
		_content = content;
	}
	
	public URI getURI() 
	{ 
		return _uri; 
	}

    public void setURI(URI value) 
    { 
    	_uri = value; 
    }
    
	public String getContent() 
	{ 
		return _content; 
	}

    public void setContent(String value) 
    { 
    	_content = value; 
    }
    
    @Override
    public boolean equals(Object obj) 
    {
        if (obj instanceof Element) 
        {
        	Element otherElement = (Element) obj;
        	if (_content.equals(otherElement._content) && _uri == otherElement._uri)
                return true;
        }
        return false;
    }                     
}
