package com.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.product.api.entity.Category;
import com.product.api.entity.Product;
import com.product.api.repository.RepoCategory;
import com.product.api.repository.RepoProduct;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ProductServiceIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RepoProduct repoProduct;

    @Autowired
    private RepoCategory repoCategory;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${jwt.secret}")
    private String testSecret;

    private String customerToken;
    private String adminToken;

    private Integer testCategoryId;
    private String activeGtin = "1234567890123";
    private String inactiveGtin = "9876543210987";

    @BeforeEach
    void setup() {
        repoProduct.deleteAll();
        repoCategory.deleteAll();

        // Generate tokens
        customerToken = generateToken("customer_user", List.of("User"));
        adminToken = generateToken("admin_user", List.of("Administrator"));

        // Seed Category
        Category category = new Category();
        category.setCategory("Electronics");
        category.setTag("ELEC");
        category.setStatus(1);
        category = repoCategory.save(category);
        testCategoryId = category.getCategoryId();

        // Seed Active Product
        // Using native queries since the reference repository creates elements that way,
        // but let's see if we can use JPA save or native query.
        // We'll save using JPA to set test variables.
        Product activeProd = new Product();
        activeProd.setGtin(activeGtin);
        activeProd.setProduct("Smart TV");
        activeProd.setDescription("4K Ultra HD");
        activeProd.setPrice(499.99f);
        activeProd.setStock(10);
        activeProd.setCategory_id(testCategoryId);
        activeProd.setStatus(1);
        repoProduct.save(activeProd);

        // Seed Inactive Product
        Product inactiveProd = new Product();
        inactiveProd.setGtin(inactiveGtin);
        inactiveProd.setProduct("Old Radio");
        inactiveProd.setDescription("AM/FM Radio");
        inactiveProd.setPrice(19.99f);
        inactiveProd.setStock(5);
        inactiveProd.setCategory_id(testCategoryId);
        inactiveProd.setStatus(0);
        repoProduct.save(inactiveProd);
    }

    private String generateToken(String username, List<String> roles) {
        Key key = Keys.hmacShaKeyFor(testSecret.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // @spec PROD-INT-001
    @Test
    void testGetProductByGtinSuccess() throws Exception {
        mockMvc.perform(get("/product/gtin/" + activeGtin)
                .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gtin", is(activeGtin)))
                .andExpect(jsonPath("$.product", is("Smart TV")))
                .andExpect(jsonPath("$.stock", is(10)))
                .andExpect(jsonPath("$.status", is(1)));
    }

    // @spec PROD-INT-001
    @Test
    void testGetProductByGtinInactive() throws Exception {
        mockMvc.perform(get("/product/gtin/" + inactiveGtin)
                .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gtin", is(inactiveGtin)))
                .andExpect(jsonPath("$.product", is("Old Radio")))
                .andExpect(jsonPath("$.status", is(0)));
    }

    // @spec PROD-INT-001
    @Test
    void testGetProductByGtinNotFound() throws Exception {
        mockMvc.perform(get("/product/gtin/0000000000000")
                .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isNotFound());
    }

    // @spec PROD-INT-002
    @Test
    void testDecrementStockSuccess() throws Exception {
        mockMvc.perform(patch("/product/gtin/" + activeGtin + "/stock")
                .header("Authorization", "Bearer " + customerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("5"))
                .andExpect(status().isOk());

        Optional<Product> prod = repoProduct.findAll().stream()
                .filter(p -> p.getGtin().equals(activeGtin))
                .findFirst();
        assertTrue(prod.isPresent());
        assertEquals(5, prod.get().getStock());
    }

    // @spec PROD-INT-003
    @Test
    void testDecrementStockInsufficient() throws Exception {
        mockMvc.perform(patch("/product/gtin/" + activeGtin + "/stock")
                .header("Authorization", "Bearer " + customerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("15"))
                .andExpect(status().isConflict());

        Optional<Product> prod = repoProduct.findAll().stream()
                .filter(p -> p.getGtin().equals(activeGtin))
                .findFirst();
        assertTrue(prod.isPresent());
        assertEquals(10, prod.get().getStock());
    }

    // @spec PROD-INT-003
    @Test
    void testDecrementStockInactive() throws Exception {
        mockMvc.perform(patch("/product/gtin/" + inactiveGtin + "/stock")
                .header("Authorization", "Bearer " + customerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("2"))
                .andExpect(status().isConflict());
    }

    // @spec PROD-INT-004
    @Test
    void testDecrementStockNegativeOrZero() throws Exception {
        mockMvc.perform(patch("/product/gtin/" + activeGtin + "/stock")
                .header("Authorization", "Bearer " + customerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("-5"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(patch("/product/gtin/" + activeGtin + "/stock")
                .header("Authorization", "Bearer " + customerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("0"))
                .andExpect(status().isBadRequest());
    }

    // @spec PROD-INT-005
    @Test
    void testIncrementStockSuccess() throws Exception {
        mockMvc.perform(patch("/product/gtin/" + activeGtin + "/stock/increment")
                .header("Authorization", "Bearer " + customerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("5"))
                .andExpect(status().isOk());

        Optional<Product> prod = repoProduct.findAll().stream()
                .filter(p -> p.getGtin().equals(activeGtin))
                .findFirst();
        assertTrue(prod.isPresent());
        assertEquals(15, prod.get().getStock());
    }

    // @spec PROD-SEC-001
    @Test
    void testRejectRequestsWithoutToken() throws Exception {
        mockMvc.perform(get("/product/gtin/" + activeGtin))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(patch("/product/gtin/" + activeGtin + "/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .content("1"))
                .andExpect(status().isUnauthorized());
    }

    // @spec PROD-SEC-002
    @Test
    void testRestrictManagementEndpointsToAdmin() throws Exception {
        // Customer trying to update category -> Forbidden
        mockMvc.perform(get("/category")
                .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isForbidden());

        // Admin trying to update category -> Success
        mockMvc.perform(get("/category")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    // @spec PROD-SEC-003
    @Test
    void testAllowInternalEndpointsToCustomerAndAdmin() throws Exception {
        // Customer lookup -> Ok
        mockMvc.perform(get("/product/gtin/" + activeGtin)
                .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk());

        // Admin lookup -> Ok
        mockMvc.perform(get("/product/gtin/" + activeGtin)
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }
}
