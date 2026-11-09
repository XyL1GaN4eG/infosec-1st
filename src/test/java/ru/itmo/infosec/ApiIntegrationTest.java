package ru.itmo.infosec;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import ru.itmo.infosec.entity.ProductModel;
import ru.itmo.infosec.repository.AuthRepository;
import ru.itmo.infosec.repository.ProductRepository;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ApiIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired AuthRepository users;
    @Autowired ProductRepository products;
    @Autowired PasswordEncoder encoder;

    @BeforeEach
    void clearDatabase() {
        users.deleteAll();
        products.deleteAll();
    }

    @Test
    void registrationLoginAndProtectedData() throws Exception {
        String credentials = "{\"login\":\"user<script>\",\"password\":\"password123\"}";
        String token = mvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(credentials))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        assertThat(users.findById("user<script>")).hasValueSatisfying(user -> {
            assertThat(user.getPassword()).isNotEqualTo("password123");
            assertThat(encoder.matches("password123", user.getPassword())).isTrue();
        });
        mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(credentials))
                .andExpect(status().isOk());
        mvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(credentials))
                .andExpect(status().isConflict());

        ProductModel product = new ProductModel();
        product.setData("<script>alert('xss')</script>");
        products.saveAndFlush(product);
        mvc.perform(get("/api/data").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].data").value("&lt;script&gt;alert(&#39;xss&#39;)&lt;/script&gt;"));
        assertThat(products.findAll().get(0).getData()).isEqualTo("<script>alert('xss')</script>");
    }

    @Test
    void rejectsMissingInvalidForgedAndExpiredTokens() throws Exception {
        mvc.perform(get("/api/data")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/data").header("Authorization", "Bearer nonsense"))
                .andExpect(status().isUnauthorized());
        String forged = Jwts.builder().setSubject("user1").setExpiration(new Date(System.currentTimeMillis() + 60000))
                .signWith(Keys.hmacShaKeyFor("another-secret-key-with-32-bytes!".getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256).compact();
        mvc.perform(get("/api/data").header("Authorization", "Bearer " + forged))
                .andExpect(status().isUnauthorized());
        String expired = Jwts.builder().setSubject("user1").setExpiration(new Date(System.currentTimeMillis() - 60000))
                .signWith(Keys.hmacShaKeyFor("test-only-secret-key-at-least-32-bytes".getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256).compact();
        mvc.perform(get("/api/data").header("Authorization", "Bearer " + expired))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void rejectsInvalidCredentialsAndInput() throws Exception {
        String registration = "{\"login\":\"user123\",\"password\":\"password123\"}";
        mvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(registration))
                .andExpect(status().isCreated());
        for (String login : new String[]{"user123", "missing", "' OR 1=1 --"}) {
            mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                            .content("{\"login\":\"" + login + "\",\"password\":\"wrongpass\"}"))
                    .andExpect(status().isUnauthorized()).andExpect(content().string("Invalid login or password"));
        }
        mvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON).content("{\"login\":\"x\",\"password\":\"short\"}"))
                .andExpect(status().isBadRequest());
    }
}
