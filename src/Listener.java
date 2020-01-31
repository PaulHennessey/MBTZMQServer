
public class Listener
{
	public Listener(String url, String port)
	{
		this.url = url;					
		this.port = Integer.parseInt(port);
	}
					
	private String url;
	private int port;

    public String getUrl() 
    { 
    	return this.url; 
    }
    
    public void setUrl(String url) 
    { 
    	this.url = url; 
    }
	
    public int getPort() 
    { 
    	return this.port; 
    }
    
    public void setPort(String port) 
    { 
    	this.port = Integer.parseInt(port); 
    }	   
    
    @Override
    public boolean equals(Object obj) 
    {
        if (obj instanceof Listener) 
        {
        	Listener otherListener = (Listener) obj;
        	if (url.equals(otherListener.url) && port == otherListener.port)
                return true;
        }
        return false;
    }                 
}
