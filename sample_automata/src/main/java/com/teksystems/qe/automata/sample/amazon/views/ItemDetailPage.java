package com.teksystems.qe.automata.sample.amazon.views;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import com.teksystems.qe.automata.annotations.ViewStates;
import com.teksystems.qe.automata.sample.amazon.data.AmazonUser;
import com.teksystems.qe.automata.sample.amazon.data.Item;
import com.teksystems.qe.automata.sample.amazon.data.ItemStatus;

@ViewStates(value = { "detail" })
public class ItemDetailPage extends AmazonBaseView{
    protected Logger LOG                                        = LogManager.getLogger(this.getClass());
	public ItemDetailPage(WebDriver driver, AmazonUser user) {
        super(driver, user);
    }
	
    public void complete(){
        if(user.getCurrentUnfinishedOrder() != null){
            Item toBuy;
            if((toBuy = user.getCurrentUnfinishedOrder().getCurrentItemBelowStatus(ItemStatus.CART)) != null){
               toBuy.setStatus(ItemStatus.VIEWING);
            }
        }
        //TODO: We should never leave a page in a state where nothing happens, always place a default behavior (even going Home)
    }
}
