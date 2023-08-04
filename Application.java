import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@SpringBootApplication
@RestController
public class Application {

  private static final String AUTH_URL = "https://qa2.sunbasedata.com/sunbase/portal/api/assignment_auth.jsp";
  private static final String API_URL = "https://qa2.sunbasedata.com/sunbase/portal/api/assignment.jsp";
  private static final RestTemplate restTemplate = new RestTemplate();
  private String token;

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @PostMapping("/authenticate")
  public ResponseEntity<String> authenticate(@RequestBody Map<String, String> credentials) {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Content-Type", "application/json");
    HttpEntity<Map<String, String>> request = new HttpEntity<>(credentials, headers);
    ResponseEntity<Map> response = restTemplate.postForEntity(AUTH_URL, request, Map.class);
    token = (String) response.getBody().get("token");
    return ResponseEntity.ok("Authenticated successfully");
  }

  @PostMapping("/customers")
  public ResponseEntity<String> createCustomer(@RequestBody Map<String, Object> customer) {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Content-Type", "application/json");
    headers.set("Authorization", "Bearer " + token);
    HttpEntity<Map<String, Object>> request = new HttpEntity<>(customer, headers);
    restTemplate.exchange(API_URL + "?cmd=create", HttpMethod.POST, request, String.class);
    return ResponseEntity.ok("Customer created successfully");
  }

  @GetMapping("/customers")
  public ResponseEntity<List> getCustomerList() {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + token);
    HttpEntity<?> request = new HttpEntity<>(headers);
    ResponseEntity<List> response =
        restTemplate.exchange(API_URL + "?cmd=get_customer_list", HttpMethod.GET, request, List.class);
    return response;
  }

  @DeleteMapping("/customers/{uuid}")
  public ResponseEntity<String> deleteCustomer(@PathVariable String uuid) {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + token);
    HttpEntity<?> request = new HttpEntity<>(headers);
    restTemplate.exchange(API_URL + "?cmd=delete&uuid=" + uuid, HttpMethod.POST, request, String.class);
    return ResponseEntity.ok("Customer deleted successfully");
  }

  @PutMapping("/customers/{uuid}")
  public ResponseEntity<String> updateCustomer(@PathVariable String uuid, @RequestBody Map<String, Object> customer) {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Content-Type", "application/json");
    headers.set("Authorization", "Bearer " + token);
    HttpEntity<Map<String, Object>> request = new HttpEntity<>(customer, headers);
    restTemplate.exchange(API_URL + "?cmd=update&uuid=" + uuid, HttpMethod.POST, request, String.class);
    return ResponseEntity.ok("Customer updated successfully");
  }
}
