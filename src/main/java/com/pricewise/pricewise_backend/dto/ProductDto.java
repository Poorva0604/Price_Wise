package com.pricewise.pricewise_backend.dto;

public class ProductDto {
    private String title;
    // private String description;
    private String price;
    private String link;
    private String image;
    private String platform;
    private String rating;

    public String getTitle(){
        return title;
    }
    public String getPrice(){
        return price;
    }
    public String getLink(){
        return link;
    }
    public String getImage(){
        return image;
    }
    public String getPlatform(){
        return platform;
    }
    public String getRating(){
        return rating;
    }
    public void setTitle(String title){
        this.title=title;
    }
    public void setPrice(String price){
        this.price=price;
    }
    public void setLink(String link){
        this.link=link;
    }
    public void setImage(String image){
        this.image=image;
    }
    public void setPlatform(String platform){
        this.platform=platform;
    }
    public void setRating(String rating){
        this.rating=rating;
    }
}
