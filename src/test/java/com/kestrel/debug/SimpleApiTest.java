// Create this file: src/test/java/com/kestrel/debug/SimpleApiTest.java
package com.kestrel.debug;

import com.kestrel.api.clients.ApiClient;
import io.restassured.response.Response;

public class SimpleApiTest {
    public static void main(String[] args) {
        System.out.println("🦅 Testing JSONPlaceholder connectivity...");
        
        try {
            // Set the system property for API URL
            System.setProperty("api.url", "https://jsonplaceholder.typicode.com");
            
            ApiClient client = new ApiClient();
            
            // Test basic connectivity
            System.out.println("🔍 Testing API connectivity...");
            boolean reachable = client.isApiReachable();
            System.out.println("API Reachable: " + reachable);
            
            // Test getAllUsers
            System.out.println("🔍 Testing getAllUsers...");
            Response response = client.getAllUsers();
            System.out.println("Status Code: " + response.getStatusCode());
            System.out.println("Response Body (first 200 chars): " + 
                response.getBody().asString().substring(0, Math.min(200, response.getBody().asString().length())));
            
            // Test getUserById
            System.out.println("🔍 Testing getUserById(1)...");
            Response userResponse = client.getUserById("1");
            System.out.println("User Status: " + userResponse.getStatusCode());
            System.out.println("User Data: " + userResponse.getBody().asString());
            
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}