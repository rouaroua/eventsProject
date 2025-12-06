package tn.fst.eventsproject.controllers;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tn.fst.eventsproject.entities.Participant;
import tn.fst.eventsproject.services.IEventServices;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventRestController.class)
class EventRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IEventServices eventServices;

    @Test
    void testAddParticipant() throws Exception {
        Participant participant = new Participant();
        participant.setNom("Doe");
        participant.setPrenom("John");

        Mockito.when(eventServices.addParticipant(Mockito.any())).thenReturn(participant);

        mockMvc.perform(post("/event/addPart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nom\":\"Doe\",\"prenom\":\"John\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Doe"))
                .andExpect(jsonPath("$.prenom").value("John"));
    }
}
