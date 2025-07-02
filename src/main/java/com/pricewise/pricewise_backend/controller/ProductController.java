package com.pricewise.pricewise_backend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pricewise.pricewise_backend.dto.ProductDto;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class ProductController {

    // private final OkHttpClient client = new OkHttpClient();

    @GetMapping("/search")
    public ResponseEntity<List<ProductDto>> searchAllPlatforms(@RequestParam String query) {
        List<ProductDto> allResults = new ArrayList<>();
        allResults.addAll(fetchFromAmazon(query));
        allResults.addAll(fetchFromFlipkart(query));
        allResults.addAll(fetchFromWalmart(query));
        return ResponseEntity.ok(allResults);
    }


    private List<ProductDto> fetchFromAmazon(String query) {
    List<ProductDto> products = new ArrayList<>();
    OkHttpClient client = new OkHttpClient();

    try {
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String url = "api_key" + encodedQuery +
                "api_key";

        Request request = new Request.Builder()
            .url(url)
            .get()
            .addHeader("x-rapidapi-key", "api_key")
            .addHeader("x-rapidapi-host", "api_key")
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
        String url = "api_key" + encodedQuery + "api_key";

        Request request = new Request.Builder()
            .url(url)
            .get()
            .addHeader("x-rapidapi-key", "api_key")
            .addHeader("x-rapidapi-host", "api_key")
            .build();

        Response response = client.newCall(request).execute();

        if (response.isSuccessful() && response.body() != null) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.body().string());
            JsonNode items = root.path("data").path("products");

            for (JsonNode item : items) {
                ProductDto product = new ProductDto();
                product.setTitle(item.path("title").asText());
                product.setPrice(item.path("price").asText());
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
        String url = "api_key" + encodedQuery +
                "api_key";

        Request request = new Request.Builder()
            .url(url)
            .get()
            .addHeader("x-rapidapi-key", "api_key")
            .addHeader("x-rapidapi-host", "api_key")
            .build();

        Response response = client.newCall(request).execute();
        System.out.println("Before response from walmart");
        if (response.isSuccessful() && response.body() != null) {
            System.out.println("Response from walmart");
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.body().string());
            JsonNode items = root.path("data").path("products");

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
        } else {
            System.out.println("Walmart API ERROR: " + response.code() + " - " + response.message());
        }
    } catch (IOException e) {
        e.printStackTrace();
    }

    return products;
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