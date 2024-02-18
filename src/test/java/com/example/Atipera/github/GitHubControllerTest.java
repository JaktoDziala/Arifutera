//package com.example.Atipera.github;
//
//import com.example.Atipera.exceptions.ResourceNotFoundException;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.hamcrest.Matchers.hasSize;
//import static org.mockito.Mockito.doThrow;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(GitHubController.class)
//public class GitHubControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//    @MockBean
//    private GitHubService gitHubService;
//
//    @Test
//    void getRepositories_withNotExistingUsername_throwsException() throws Exception {
//        doThrow(new ResourceNotFoundException("Username could not be found!")).when(gitHubService).getRepositories("Cuba");
//
//        mockMvc.perform(get("/user/{username}", "Cuba")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.message").value("Username could not be found!"))
//                .andExpect(jsonPath("$.status").value("NOT_FOUND"));
//    }
//
//    @Test
//    void getRepositories_withExistingUsernameAndNoRepositories_isStatusCodeOkAndEmptySet() throws Exception {
//        mockMvc.perform(get("/user/{username}", "Cuba")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(0)));
//    }
//}
