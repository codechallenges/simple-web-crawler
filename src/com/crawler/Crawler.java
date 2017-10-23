package com.crawler;

import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Crawler {
	
	private Set<String> internalUrlSet;
	private Set<String> externalUrlSet;
	private Set<String> errorUrlSet;
	private Set<String> imageSet;
	private Set<String> cssFileSet;
	private Set<String> jsFileSet;
	private int current_depth;	
	
	private String BASE_URL = "http://www.example.com";
	private boolean CRAWL_STATIC = false;
	private boolean DEBUG = false;	
	private int MAX_DEPTH = 3;
	private int SLEEP_TIME = 0;

	

    public static void main(String[] args) {
    	Crawler c = new Crawler();
    	c.init(args);
    	c.crawl();
    	c.printResults();
    }
    
    private void init(String[] args) {
    	internalUrlSet = new TreeSet<String>();
    	externalUrlSet = new TreeSet<String>();
    	errorUrlSet = new TreeSet<String>();
    	imageSet = new TreeSet<String>();
    	cssFileSet = new TreeSet<String>();
    	jsFileSet = new TreeSet<String>();	  
    	current_depth = 1;

    	for(String arg : args) {
    		if(arg.startsWith("-BU")) {
    			arg = arg.replace("-BU", "");
    			if(arg.startsWith("http://") || arg.startsWith("https://")) this.BASE_URL = arg;
    		}
    		else if(arg.startsWith("-CS")) {
    			arg = arg.replace("-CS", "");
    			if(arg.equals("true")) this.CRAWL_STATIC = true;
    		}
    		else if(arg.startsWith("-DB")) {
    			arg = arg.replace("-DB", "");
    			if(arg.equals("true")) this.DEBUG = true;
    		}
    		else if(arg.startsWith("-MD")) {
    			arg = arg.replace("-MD", "");
    			if(arg.matches("((-|\\+)?[0-9]+(\\.[0-9]+)?)+")) {
    				int mxdepth =  Integer.parseInt(arg);
    				if (mxdepth > 0) this.MAX_DEPTH = mxdepth;
    			}
    		}
    		else if(arg.startsWith("-ST")) {
    			arg = arg.replace("-ST", "");
    			if(arg.matches("((-|\\+)?[0-9]+(\\.[0-9]+)?)+")) {
    				int sleeptime =  Integer.parseInt(arg);
    				if (sleeptime > 0) this.SLEEP_TIME = sleeptime;
    			}
    		}
    	}
    	
    	System.out.println("\n\n------------------------------------------------------");
    	System.out.println("STARTING CRAWL WITH FOLLOWING PARAMETERS: ");
    	System.out.println("\tBASE_URL = " + BASE_URL);
    	System.out.println("\tCRAWL_STATIC = " + CRAWL_STATIC);
    	System.out.println("\tDEBUG = " + DEBUG);
    	System.out.println("\tMAX_DEPTH = " + MAX_DEPTH);
    	System.out.println("\tSLEEP_TIME = " + SLEEP_TIME);
    	System.out.println("------------------------------------------------------\n\n");
	}

	public void crawl() {
    	crawl(this.BASE_URL);
    }
    
    private void crawl(String crawlUrl) {
		Document doc = null;
        try {    	
	    	if(this.current_depth <= MAX_DEPTH) {
		    	//Specifying userAgent=Mozilla, to avoid HTTP 403 error messages.
	    		Thread.sleep(SLEEP_TIME); // to avoid 429 - Too Many Requests
		        doc = Jsoup.connect(crawlUrl)
				        .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0") // | All this is to disguise
				        .timeout(1000)																			// | as a genuine request
				        .referrer("http://www.google.com")														// | and not look like a bot.
				        .get();
		        
		        Elements links = doc.select("a[href]");
		        for (Element link : links) {
		        	String currentLink = link.attr("abs:href");
		        	if(isValidLink(currentLink)) {
		        		if(isInternal(currentLink)) {
		        			if (!internalUrlSet.contains(currentLink)) { // this means the link is not visited yet
		        				if(CRAWL_STATIC) crawlStaticContent(currentLink);
		        				if(DEBUG) System.out.println(tabs(current_depth) + "FIRST time = [" + currentLink +"]; depth = [" + current_depth + "]");
		        				internalUrlSet.add(currentLink);
		        				current_depth++;
		        				crawl(currentLink); //recursive crawl
		        			} else {
		        				if(DEBUG) System.out.println(tabs(current_depth) + "ALREADY visited = [" + currentLink +"]; depth = [" + current_depth + "]");
		        			}
		        		}
		        		else {
		        			externalUrlSet.add(currentLink);
		        		}
		        	}
		        }
	    	}
	    	current_depth--;
        } catch(HttpStatusException e) {
        	current_depth--;
        	errorUrlSet.add(e.getUrl() + "; ERROR_CODE [" + e.getStatusCode() + "]");
        } catch(IOException e) {
        	System.out.println("IOException [" + e + "] has occuered.....");
        } catch(Exception e) {
        	System.out.println("Generic Exception [" + e + "] has occuered.....");
        }
    }

    private void printResults() {

    	//removing error URLs from the clean sitemap set
        for(String errorUrl :errorUrlSet) {
        	internalUrlSet.remove(errorUrl.substring(0, errorUrl.indexOf("; ERROR_CODE [")));
        }
        
        System.out.println("\n\n\nInternal URLs [" + internalUrlSet.size() + "]");
        for(String internalUrl :internalUrlSet) {
        	System.out.println("\t" + internalUrl);
        }
        
        System.out.println("\n\n-------------------------------------------------------------------\n\n");
        
        System.out.println("External URLs [" + externalUrlSet.size() + "]");
        for(String externalUrl :externalUrlSet) {
        	System.out.println("\t" + externalUrl);
        }
        
        System.out.println("\n\n-------------------------------------------------------------------\n\n");
        
        System.out.println("Images [" + imageSet.size() + "]");
        for(String imgsrc :imageSet) {
        	System.out.println("\t" + imgsrc);
        }  
        
        System.out.println("\n\n-------------------------------------------------------------------\n\n");
        
        System.out.println("CSS [" + cssFileSet.size() + "]");
        for(String css :cssFileSet) {
        	System.out.println("\t" + css);
        }
        
        System.out.println("\n\n-------------------------------------------------------------------\n\n");
        
        System.out.println("JS [" + jsFileSet.size() + "]");
        for(String js :jsFileSet) {
        	System.out.println("\t" + js);
        }
        
        System.out.println("\n\n-------------------------------------------------------------------\n\n");
        
        System.out.println("Error URLs [" + errorUrlSet.size() + "]");
        for(String errorUrl :errorUrlSet) {
        	System.out.println("\t" + errorUrl);
        }        
        
        System.out.println("\n\n------------------***CRAWLING ENDED***-------------------------------------------------\n\n");
        
    }    
    
    private void crawlStaticContent(String currentLink) {
    	try {
    	
	    	Document doc = Jsoup.connect(currentLink).userAgent("Mozilla").get();
	        Elements images = doc.select("img[src]");
	        Elements cssFiles = doc.select("link[href]");
	        Elements jsFiles = doc.select("script[src]");    	
	    	
	        for (Element image : images) {
	        	String imgsrc = image.attr("abs:src");
	        	imageSet.add(imgsrc);
	        }
	        
	        for (Element cssFile : cssFiles) {
	        	String rel = cssFile.attr("rel");
	        	if(rel.equals("stylesheet")) {
	            	String css = cssFile.attr("abs:href");
	            	cssFileSet.add(css);
	        	}
	        }
	        
	        for (Element jsFile : jsFiles) {
	        	String js = jsFile.attr("abs:src");
	        	jsFileSet.add(js);
	        }
    	} catch(HttpStatusException e) {
         	errorUrlSet.add(e.getUrl() + "; ERROR_CODE [" + e.getStatusCode() + "]");
         } catch(IOException e) {
         	System.out.println("IOException [" + e + "] has occuered.....");
         } catch(Exception e) {
         	System.out.println("Generic Exception [" + e + "] has occuered.....");
         }		
	}

	private boolean isInternal(String currentLink) {
    	currentLink = currentLink.replace("https://", "");
    	currentLink = currentLink.replace("http://", "");
    	currentLink = currentLink.replace("www.", "");
   	
    	String temp_base_url = BASE_URL;
    	temp_base_url = temp_base_url.replace("https://", "");
    	temp_base_url = temp_base_url.replace("http://", "");
    	temp_base_url = temp_base_url.replace("www.", "");
    	return currentLink.startsWith(temp_base_url);
    }
    
    private String tabs(int depth) {
    	StringBuilder tabs = new StringBuilder();
    	for(int i = 1; i < depth; i++) {
    		tabs.append("    ");
    	}
    	tabs.append("|-- "); 
    	return tabs.toString();
    }
    
    private boolean isValidLink(String currentLink) {
    	//can add more validation rules over here
    	if( 
    		!(currentLink.equals(""))     				&&
    		!(currentLink.contains("#")) 				&&
    		!(currentLink.startsWith("mailto"))     	&&
    		!(currentLink.equals(this.BASE_URL+"/"))    	&&
    		!(currentLink.contains(this.BASE_URL+"/#")) 	&&
    		!(currentLink.contains(this.BASE_URL+"#")) ) {
    		return true;
    	}
    	
    	return false;
    }
}
