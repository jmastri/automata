package com.teksystems.qe.automata.sample.amazon.views;

import org.openqa.selenium.WebDriver;

import com.teksystems.qe.automata.annotations.ViewStates;
import com.teksystems.qe.automata.sample.amazon.data.AmazonUser;
import com.teksystems.qe.automata.sample.amazon.data.Item;
import com.teksystems.qe.automata.sample.amazon.data.ItemStatus;

@ViewStates(value = { "home" })
public class HomePage extends AmazonBaseView{

	public HomePage(WebDriver driver, AmazonUser user) {
        super(driver, user);
    }

    public void complete(){
        if(user.getCurrentUnfinishedOrder() != null){
            Item toSearch;
            if((toSearch = user.getCurrentUnfinishedOrder().getCurrentItemBelowStatus(ItemStatus.CART)) != null){
                searchInput.sendKeys(toSearch.getItemName());
                searchSubmit.click();
            }
        }
        //maybe register user?
        //what else could the data tell us to do here?
    }

}
