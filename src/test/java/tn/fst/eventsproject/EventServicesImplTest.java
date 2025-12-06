package tn.fst.eventsproject.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.fst.eventsproject.entities.*;
import tn.fst.eventsproject.repositories.*;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServicesImplTest {

    @InjectMocks
    private EventServicesImpl eventServices;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private ParticipantRepository participantRepository;

    @Mock
    private LogisticsRepository logisticsRepository;

    private Event event;
    private Participant participant;
    private Logistics logistics;

    @BeforeEach
    void setUp() {
        participant = new Participant();
        participant.setIdPart(1);
        participant.setNom("Doe");
        participant.setPrenom("John");
        participant.setTache(Tache.ORGANISATEUR);
        participant.setEvents(new HashSet<>());

        event = new Event();
        event.setIdEvent(1);
        event.setDescription("Test Event");
        event.setDateDebut(LocalDate.now());
        event.setDateFin(LocalDate.now().plusDays(1));
        event.setCout(0f);
        event.setParticipants(new HashSet<>(Collections.singletonList(participant)));
        event.setLogistics(new HashSet<>());

        logistics = new Logistics();
        logistics.setIdLog(1);
        logistics.setDescription("Chaises");
        logistics.setReserve(true);
        logistics.setPrixUnit(10f);
        logistics.setQuantite(5);
    }


    @Test
    void testAddParticipant() {
        when(participantRepository.save(any(Participant.class))).thenReturn(participant);

        Participant saved = eventServices.addParticipant(participant);

        assertNotNull(saved);
        assertEquals("Doe", saved.getNom());
        verify(participantRepository, times(1)).save(participant);
    }


    @Test
    void testAddAffectEvenParticipantWithId() {
        when(participantRepository.findById(1)).thenReturn(Optional.of(participant));
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        Event saved = eventServices.addAffectEvenParticipant(event, 1);

        assertNotNull(saved);
        assertTrue(participant.getEvents().contains(event));
        verify(eventRepository, times(1)).save(event);
    }

    @Test
    void testAddAffectEvenParticipantWithoutId() {
        when(participantRepository.findById(anyInt())).thenReturn(Optional.of(participant));
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        Event saved = eventServices.addAffectEvenParticipant(event);

        assertNotNull(saved);
        assertTrue(participant.getEvents().contains(event));
        verify(eventRepository, times(1)).save(event);
    }



    @Test
    void testAddAffectLog() {
        when(eventRepository.findByDescription("Test Event")).thenReturn(event);
        when(logisticsRepository.save(any(Logistics.class))).thenReturn(logistics);

        Logistics saved = eventServices.addAffectLog(logistics, "Test Event");

        assertNotNull(saved);
        assertTrue(event.getLogistics().contains(logistics));
        verify(logisticsRepository, times(1)).save(logistics);
    }


    @Test
    void testGetLogisticsDates() {
        event.getLogistics().add(logistics);
        when(eventRepository.findByDateDebutBetween(any(), any())).thenReturn(Collections.singletonList(event));

        List<Logistics> result = eventServices.getLogisticsDates(LocalDate.now(), LocalDate.now().plusDays(2));

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Chaises", result.get(0).getDescription());
    }


    @Test
    void testCalculCout() {
        when(eventRepository.findByParticipants_NomAndParticipants_PrenomAndParticipants_Tache(
                "Tounsi", "Ahmed", Tache.ORGANISATEUR))
                .thenReturn(Collections.singletonList(event));

        Logistics log = new Logistics();
        log.setReserve(true);
        log.setPrixUnit(20f);
        log.setQuantite(3);
        event.getLogistics().add(log);

        eventServices.calculCout();

        assertEquals(60f, event.getCout());
        verify(eventRepository, times(1)).save(event);
    }


}
