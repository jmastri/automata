package com.teksystems.qe.automata.sample.amazon.views;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.teksystems.qe.automata.annotations.ViewStates;
import com.teksystems.qe.automata.sample.amazon.data.AmazonUser;
import com.teksystems.qe.automata.sample.amazon.data.Item;
import com.teksystems.qe.automata.sample.amazon.data.ItemStatus;

@ViewStates(value = { "search-alias%3Daps" })
public class SearchResults extends AmazonBaseView{
    protected Logger LOG                                        = LogManager.getLogger(this.getClass());
	public SearchResults(WebDriver driver, AmazonUser user) {
        super(driver, user);
    }

	class SearchResult{
	    String name;
	    WebElement ele;
	    public SearchResult(WebElement ele){
	        name = ele.findElement(By.xpath(".//a[contains(@class,'s-access-detail-page')]")).getAttribute("title");
	        this.ele = ele;
	    }

	    public void select(){
	        ele.findElement(By.xpath(".//a[contains(@class,'s-access-detail-page')]")).click();
	    }
        public String getName() {
            return name;
        }

        public int getMatch(String toMatch){
            
            int matched = 0;
            String[] words = toMatch.split(" ");
            for(String word : words){
                if(name.toLowerCase().contains(word.toLowerCase().trim())){
                    matched++;
                }
            }
            int match   = (int) ((100*matched)/words.length);
            LOG.info("Search item ("+name+") matched: ("+toMatch+") @ "+match);
            return match;
        }
	}
	
    public void complete(){
        if(user.getCurrentUnfinishedOrder() != null){
            Item toBuy;
            if((toBuy = user.getCurrentUnfinishedOrder().getCurrentItemBelowStatus(ItemStatus.CART)) != null){
               for(SearchResult each : getSearchResults()){
                   if(each.getMatch(toBuy.getItemName()) >= toBuy.getSearchThreshold()){
                       LOG.info("Selecting item: \""+each.getName()+"\"");
                       each.select();
                       return;
                   }
               }
            }
        }
        //maybe register user?
        //what else could the data tell us to do here?
    }

    
    public List<SearchResult> getSearchResults(){
        long start  = System.currentTimeMillis();
        List<SearchResult> ret      = new ArrayList<SearchResult>();
        
        while(true){
            List<WebElement> eles   = driver.findElements(By.xpath("//li[contains(@id,'result_')]"));
            if(eles.size() == 0){
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {}
                continue;
            }
            if(System.currentTimeMillis()-start>10000){
                return ret;
            }
            for(WebElement ele : eles){
                ret.add(new SearchResult(ele));
            }
            break;
            
        }
        return ret;
    }
}
