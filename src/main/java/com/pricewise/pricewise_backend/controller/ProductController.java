package com.pricewise.pricewise_backend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pricewise.pricewise_backend.dto.ProductDto;

import org.springframework.beans.factory.annotation.Value;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class ProductController {

    // private final OkHttpClient client = new OkHttpClient();
     @Value("${walmart.api.key}")
    private String walmartApiKey;

    @Value("${walmart.api.host}")
    private String walmartApiHost;
    @Value("${amazon.api.key}")
    private String amazonApiKey;

    @Value("${amazon.api.host}")
    private String amazonApiHost;

    @Value("${flipcart.api.key}")
    private String flipcartApiKey;

    @Value("${flipcart.api.host}")
    private String flipcartApiHost;

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchAllPlatforms(@RequestParam String query) {
        List<ProductDto> allResults = new ArrayList<>();
        allResults.addAll(fetchFromAmazon(query));
        // allResults.addAll(fetchFromFlipkart(query));
        allResults.addAll(fetchFromWalmart(query));
        Map<String, Object> filteredResults = computeFilteredResults(allResults);
    filteredResults.put("allResults", allResults); 

    return ResponseEntity.ok(filteredResults);
        // return ResponseEntity.ok(allResults);
    }


    private List<ProductDto> fetchFromAmazon(String query) {
    List<ProductDto> products = new ArrayList<>();
    OkHttpClient client = new OkHttpClient();

    try {
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String url = "https://real-time-amazon-data.p.rapidapi.com/search?query=" + encodedQuery +
                "&page=1&country=US&sort_by=RELEVANCE&product_condition=ALL&is_prime=false&deals_and_discounts=NONE";

        Request request = new Request.Builder()
            .url(url)
            .get()
            .addHeader("x-rapidapi-key", amazonApiKey)
            .addHeader("x-rapidapi-host", amazonApiHost)
            .build();

        Response response = client.newCall(request).execute();

        if (response.isSuccessful() && response.body() != null) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.body().string());
            JsonNode items = root.path("data").path("products");

            for (JsonNode item : items) {
                ProductDto product = new ProductDto();
                product.setTitle(item.path("product_title").asText());
                product.setPrice(item.path("product_price").asText());
                product.setImage(item.path("product_photo").asText());
                product.setLink(item.path("product_url").asText());
                product.setRating(item.path("product_star_rating").asText());
                product.setPlatform("Amazon");
                products.add(product);
            }
        } else {
            System.out.println("Amazon API ERROR: " + response.code() + " - " + response.message());
        }
    } catch (IOException e) {
        e.printStackTrace();
    }

    return products;
}

    private List<ProductDto> fetchFromFlipkart(String query) {
    List<ProductDto> products = new ArrayList<>();
    OkHttpClient client = new OkHttpClient();

    try {
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String url = "https://real-time-flipkart-data2.p.rapidapi.com/products-by-category?query=" + encodedQuery + "&page=1&categoryId=tyy%2C4io&sortBy=POPULARITY";

        Request request = new Request.Builder()
            .url(url)
            .get()
            .addHeader("x-rapidapi-key", flipcartApiKey)
            .addHeader("x-rapidapi-host", flipcartApiHost)
            .build();

        Response response = client.newCall(request).execute();

        if (response.isSuccessful() && response.body() != null) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.body().string());
            JsonNode items = root.path("data").path("products");

            for (JsonNode item : items) {
                ProductDto product = new ProductDto();
                product.setTitle(item.path("title").asText());
                product.setPrice(item.path("price").asText(""));
                product.setImage(item.path("image").asText());
                product.setLink(item.path("product_url").asText());
                product.setRating(item.path("rating").asText());
                product.setPlatform("Flipkart");
                System.out.println(product);
                products.add(product);
            }
        } else {
            System.out.println("Flipkart API ERROR: " + response.code() + " - " + response.message());
        }
    } catch (IOException e) {
        e.printStackTrace();
    }

    return products;
}


private List<ProductDto> fetchFromWalmart(String query) {
    List<ProductDto> products = new ArrayList<>();
    OkHttpClient client = new OkHttpClient();

    try {
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String url = "https://realtime-walmart-data.p.rapidapi.com/search?keyword=" + encodedQuery +
                "&page=1&sort=price_high";

        Request request = new Request.Builder()
            .url(url)
            .get()
            .addHeader("x-rapidapi-key", walmartApiKey)
            .addHeader("x-rapidapi-host", walmartApiHost)
            .build();

        Response response = client.newCall(request).execute();
        System.out.println("Before response from walmart");
        if (response.isSuccessful() && response.body() != null) {
            System.out.println("Response from walmart");
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.body().string());
            JsonNode items = root.path("results");
            if(items.isArray()){

            for (JsonNode item : items) {
                ProductDto product = new ProductDto();
                    product.setTitle(item.path("name").asText());
                    product.setPrice(item.path("price").asText(""));
                    product.setImage(item.path("image").asText());
                    product.setLink(item.path("canonicalUrl").asText());
                    product.setRating(item.path("rating").asText());
                    product.setPlatform("Walmart");
                    products.add(product);
                    System.out.println(product);
            }
        }else{
            System.out.println("Not an array");
        }

        } else {
            System.out.println("Walmart API ERROR: " + response.code() + " - " + response.message());
        }
    } catch (IOException e) {
        e.printStackTrace();
    }

    return products;
}

private Map<String, Object> computeFilteredResults(List<ProductDto> products) {
    Map<String, Object> result = new HashMap<>();

    List<ProductDto> lowestPriceList = new ArrayList<>();
    List<ProductDto> highestRatingList = new ArrayList<>();
    List<ProductDto> bestComboList = new ArrayList<>();
    List<ProductDto> validForRecommendation = new ArrayList<>();

    double minPrice = Double.MAX_VALUE;
    double maxRating = 0.0;

    // First pass: find min price and max rating
    for (ProductDto p : products) {
        Double price,rating;
        if(p.getPrice()==""|| p.getPrice()==null){price=0.00;}
        else{
            String cleanedPriceStr = p.getPrice().replaceAll("[^0-9.]", ""); // removes $, commas, etc.
        price = cleanedPriceStr.isEmpty() ? null : Double.parseDouble(cleanedPriceStr);
        }
        if(p.getRating()==null|| p.getRating()==""){rating=0.00;}
        else{
            String cleanedPriceStr = p.getRating().replaceAll("[^0-9.]", ""); // removes $, commas, etc.
        rating = cleanedPriceStr.isEmpty() ? null : Double.parseDouble(cleanedPriceStr);
        }
        //Double rating = Double.parseDouble(p.getRating());

        if (price != null && price < minPrice) {
            minPrice = price;
        }

        if (rating != null && rating > maxRating) {
            maxRating = rating;
        }
    }

    // Second pass: collect matching products
    for (ProductDto p : products) {
        Double price,rating;
        if(p.getPrice()=="" || p.getPrice()==null){price=0.00;}
        else{
            String cleanedPriceStr = p.getPrice().replaceAll("[^0-9.]", ""); // removes $, commas, etc.
        price = cleanedPriceStr.isEmpty() ? null : Double.parseDouble(cleanedPriceStr);
        }
        if(p.getRating()==null|| p.getRating()==""){rating=0.00;}
        else{
            String cleanedPriceStr = p.getRating().replaceAll("[^0-9.]", ""); // removes $, commas, etc.
        rating = cleanedPriceStr.isEmpty() ? null : Double.parseDouble(cleanedPriceStr);
        }
        // Double rating = Double.parseDouble(p.getRating());

        if (price != null && price == minPrice) {
            lowestPriceList.add(p);
        }

        if (rating != null && rating == maxRating) {
            highestRatingList.add(p);
        }

        if (price != null && rating != null &&
            rating >= maxRating - 0.3 &&
            price <= minPrice * 1.15) {
            bestComboList.add(p);
        }

        if (price != null && rating != null) {
            validForRecommendation.add(p);
        }
    }

    // Sort for recommendation
    for (int i = 0; i < validForRecommendation.size() - 1; i++) {
    for (int j = i + 1; j < validForRecommendation.size(); j++) {
        ProductDto p1 = validForRecommendation.get(i);
        ProductDto p2 = validForRecommendation.get(j);
        String cleanedPriceStr1 = p1.getPrice().replaceAll("[^0-9.]", ""); // removes $, commas, etc.
        Double price1 = cleanedPriceStr1.isEmpty() ? null : Double.parseDouble(cleanedPriceStr1);
        double score1 = price1 * 0.4 - price1 * 0.6;
        String cleanedPriceStr2 = p2.getPrice().replaceAll("[^0-9.]", ""); // removes $, commas, etc.
        Double price2 = cleanedPriceStr2.isEmpty() ? null : Double.parseDouble(cleanedPriceStr2);
        double score2 = price2 * 0.4 - price2 * 0.6;

        if (score2 < score1) {
            ProductDto temp = validForRecommendation.get(i);
            validForRecommendation.set(i, validForRecommendation.get(j));
            validForRecommendation.set(j, temp);
        }
    }
}


    List<ProductDto> topRecommendations = new ArrayList<>();
    for (int i = 0; i < validForRecommendation.size() && i < 2; i++) {
        topRecommendations.add(validForRecommendation.get(i));
    }

    // Best platform among top recommendations
    Map<String, Integer> platformCount = new HashMap<>();
    for (ProductDto p : topRecommendations) {
        String platform = p.getPlatform();
        if (platform != null) {
            platformCount.put(platform, platformCount.getOrDefault(platform, 0) + 1);
        }
    }

    String bestPlatform = "Unknown";
    int maxCount = 0;
    for (Map.Entry<String, Integer> entry : platformCount.entrySet()) {
        if (entry.getValue() > maxCount) {
            bestPlatform = entry.getKey();
            maxCount = entry.getValue();
        }
    }

    // Store all results in one map
    result.put("lowestPrice", lowestPriceList);
    result.put("highestRating", highestRatingList);
    result.put("bestCombo", bestComboList);
    result.put("topRecommendations", topRecommendations);
    result.put("bestPlatform", bestPlatform);

    return result;
}



//========================================================================================
    
    // private List<ProductDto> fetchFromFlipkart(String query) {
    // List<ProductDto> products = new ArrayList<>();
    // OkHttpClient client = new OkHttpClient();

    // try {
    //     String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
    //     String url = "https://dummyjson.com/products/search?q=" + encodedQuery;

    //     Request request = new Request.Builder()
    //         .url(url)
    //         .get()
    //         .build();
    //     System.out.println("after req in flipcart");
    //     Response response = client.newCall(request).execute();

    //     if (response.isSuccessful() && response.body() != null) {
    //         ObjectMapper mapper = new ObjectMapper();
    //         JsonNode root = mapper.readTree(response.body().string());
    //         JsonNode items = root.path("products");

    //         for (JsonNode item : items) {
    //             ProductDto product = new ProductDto();
    //             product.setTitle(item.path("title").asText());
    //             product.setPrice("₹" + item.path("price").asText());
    //             product.setImage(item.path("thumbnail").asText());
    //             product.setLink("https://dummyjson.com/products/" + item.path("id").asText());
    //             product.setRating(item.path("rating").asText());
    //             product.setPlatform("Flipkart (Mock)");
    //             products.add(product);
    //         }
    //     } else {
    //         System.out.println("DummyJSON API ERROR: " + response.code() + " - " + response.message());
    //     }
    // } catch (IOException e) {
    //     e.printStackTrace();
    // }

    // return products;
    // }
}