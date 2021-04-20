package com._98point6.dropTokenGame.validationTests;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com._98point6.dropTokenGame.api.CreateGameRequest;
import com.fasterxml.jackson.databind.ObjectMapper;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ValidationTests {
    
    @Autowired
    private MockMvc mvc;
    
    @Test
    void createNewGame400NotEnoughPlayers() throws Exception {
        List<String> players = new ArrayList<>();
        players.add("dfs");
        
        mvc.perform(post("/drop_token")
                .content(asJsonString(new CreateGameRequest(players, 4, 4)))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }
    
    @Test
    void createNewGame400BoardTooSmall() throws Exception {
        List<String> players = new ArrayList<>();
        players.add("dfs");
        players.add("a");
        
        mvc.perform(post("/drop_token")
                .content(asJsonString(new CreateGameRequest(players, 2, 2)))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    
    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    
}
