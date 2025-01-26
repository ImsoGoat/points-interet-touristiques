package ch.hearc.jee_project.pointsinterettouristiques.controller;

import ch.hearc.jee_project.pointsinterettouristiques.model.Place;
import ch.hearc.jee_project.pointsinterettouristiques.model.ValidationStatus;
import ch.hearc.jee_project.pointsinterettouristiques.model.Role;
import ch.hearc.jee_project.pointsinterettouristiques.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "spring.profiles.active=test")
@AutoConfigureMockMvc
public class PlaceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private User adminUser;
    private User normalUser;

    @BeforeEach
    public void setup() throws Exception {
        // Nettoyer les données
        mockMvc.perform(delete("/api/users/all"));

        // Ajouter un administrateur
        adminUser = new User();
        adminUser.setUsername("admin_test");
        adminUser.setPassword("adminpass");
        adminUser.setRole(Role.ADMIN);
        String adminResponse = mockMvc.perform(post("/api/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(adminUser)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        adminUser = objectMapper.readValue(adminResponse, User.class);

        // Ajouter un utilisateur
        normalUser = new User();
        normalUser.setUsername("user_test");
        normalUser.setPassword("userpass");
        normalUser.setRole(Role.USER);
        String userResponse = mockMvc.perform(post("/api/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(normalUser)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        normalUser = objectMapper.readValue(userResponse, User.class);
    }

    // Test : Ajouter un lieu
    @Test
    public void createPlace_shouldCreatePlace() throws Exception {
        Place place = new Place();
        place.setName("Test Place");
        place.setLocation("Test Location");
        place.setLatitude(48.858844);
        place.setLongitude(2.294351);

        mockMvc.perform(post("/api/places")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(place)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Place"));
    }

    // Test : Mettre à jour un lieu en tant qu'admin
    @Test
    public void updatePlace_asAdmin_shouldUpdatePlace() throws Exception {
        Place place = new Place();
        place.setName("Original Name");
        place.setLocation("Original Location");
        place.setLatitude(48.858844);
        place.setLongitude(2.294351);
        place.setStatus(ValidationStatus.UNVALIDATED);

        String placeResponse = mockMvc.perform(post("/api/places")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(place)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        place = objectMapper.readValue(placeResponse, Place.class);

        place.setName("Updated Name");
        place.setLocation("Updated Location");

        mockMvc.perform(put("/api/places/" + place.getId())
                        .param("userId", String.valueOf(adminUser.getId()))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(place)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.location").value("Updated Location"));
    }

    @Test
    public void updatePlace_asUser_shouldReturnUnauthorized() throws Exception {
        Place place = new Place();
        place.setName("Test Place");
        place.setLocation("Test Location");
        place.setLatitude(48.858844);
        place.setLongitude(2.294351);
        place.setStatus(ValidationStatus.UNVALIDATED);

        // Ajouter un lieu
        String placeResponse = mockMvc.perform(post("/api/places")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(place)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        place = objectMapper.readValue(placeResponse, Place.class);

        // Tentative de mise à jour par un utilisateur non autorisé
        place.setName("Updated Name");

        mockMvc.perform(put("/api/places/" + place.getId())
                        .param("userId", String.valueOf(normalUser.getId()))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(place)))
                .andExpect(status().isUnauthorized());
    }


    // Test : Récupérer tous les lieux (admin seulement)
    @Test
    public void getAllPlaces_asAdmin_shouldReturnAllPlaces() throws Exception {
        mockMvc.perform(get("/api/places")
                        .param("userId", String.valueOf(adminUser.getId())))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    public void getAllPlaces_asUser_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/places")
                        .param("userId", String.valueOf(normalUser.getId())))
                .andExpect(status().isUnauthorized());
    }


    // Test : Récupérer un lieu par ID
    @Test
    public void getPlaceById_asAdmin_shouldReturnPlace() throws Exception {
        Place place = new Place();
        place.setName("Test Place");
        place.setLocation("Test Location");
        place.setLatitude(48.858844);
        place.setLongitude(2.294351);
        place.setStatus(ValidationStatus.UNVALIDATED);

        String placeResponse = mockMvc.perform(post("/api/places")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(place)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        place = objectMapper.readValue(placeResponse, Place.class);

        mockMvc.perform(get("/api/places/" + place.getId())
                        .param("userId", String.valueOf(adminUser.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Place"));
    }

    @Test
    public void getPlaceById_asUser_shouldReturnUnauthorizedForUnvalidatedPlace() throws Exception {
        Place place = new Place();
        place.setName("Test Place");
        place.setLocation("Test Location");
        place.setLatitude(48.858844);
        place.setLongitude(2.294351);
        place.setStatus(ValidationStatus.UNVALIDATED);

        // Ajouter un lieu
        String placeResponse = mockMvc.perform(post("/api/places")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(place)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        place = objectMapper.readValue(placeResponse, Place.class);

        // Tentative de récupération du lieu par un utilisateur non autorisé
        mockMvc.perform(get("/api/places/" + place.getId())
                        .param("userId", String.valueOf(normalUser.getId())))
                .andExpect(status().isUnauthorized());
    }


    // Test : Valider un lieu en tant qu'admin
    @Test
    public void validatePlace_asAdmin_shouldValidatePlace() throws Exception {
        Place place = new Place();
        place.setName("Test Place");
        place.setLocation("Test Location");
        place.setLatitude(48.858844);
        place.setLongitude(2.294351);
        place.setStatus(ValidationStatus.UNVALIDATED);

        String placeResponse = mockMvc.perform(post("/api/places")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(place)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        place = objectMapper.readValue(placeResponse, Place.class);

        mockMvc.perform(patch("/api/places/" + place.getId() + "/validate")
                        .param("userId", String.valueOf(adminUser.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("VALIDATED"));
    }

    // Test : Valider un lien en tant que user
    @Test
    public void validatePlace_asUser_shouldReturnUnauthorized() throws Exception {
        Place place = new Place();
        place.setName("Test Place");
        place.setLocation("Test Location");
        place.setLatitude(48.858844);
        place.setLongitude(2.294351);
        place.setStatus(ValidationStatus.UNVALIDATED);

        String placeResponse = mockMvc.perform(post("/api/places")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(place)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        place = objectMapper.readValue(placeResponse, Place.class);

        mockMvc.perform(patch("/api/places/" + place.getId() + "/validate")
                        .param("userId", String.valueOf(normalUser.getId())))
                .andExpect(status().isUnauthorized());
    }


    // Test : Rejeter un lieu
    @Test
    public void rejectPlace_asAdmin_shouldRejectPlace() throws Exception {
        Place place = new Place();
        place.setName("Test Place");
        place.setLocation("Test Location");
        place.setLatitude(48.858844);
        place.setLongitude(2.294351);
        place.setStatus(ValidationStatus.UNVALIDATED);

        String placeResponse = mockMvc.perform(post("/api/places")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(place)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        place = objectMapper.readValue(placeResponse, Place.class);

        mockMvc.perform(patch("/api/places/" + place.getId() + "/reject")
                        .param("userId", String.valueOf(adminUser.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"));
    }

    @Test
    public void rejectPlace_asUser_shouldReturnUnauthorized() throws Exception {
        Place place = new Place();
        place.setName("Test Place");
        place.setLocation("Test Location");
        place.setLatitude(48.858844);
        place.setLongitude(2.294351);
        place.setStatus(ValidationStatus.UNVALIDATED);

        // Ajouter un lieu
        String placeResponse = mockMvc.perform(post("/api/places")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(place)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        place = objectMapper.readValue(placeResponse, Place.class);

        // Tentative de rejet par un utilisateur non autorisé
        mockMvc.perform(patch("/api/places/" + place.getId() + "/reject")
                        .param("userId", String.valueOf(normalUser.getId())))
                .andExpect(status().isUnauthorized());
    }


    // Test : Récupérer les lieux validés
    @Test
    public void getValidatedPlaces_shouldReturnValidatedPlaces() throws Exception {
        mockMvc.perform(get("/api/places/validatedPlaces"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    // Test : Supprimer un lieu
    @Test
    public void deletePlace_asAdmin_shouldDeletePlace() throws Exception {
        Place place = new Place();
        place.setName("Test Place");
        place.setLocation("Test Location");
        place.setLatitude(48.858844);
        place.setLongitude(2.294351);
        place.setStatus(ValidationStatus.UNVALIDATED);

        String placeResponse = mockMvc.perform(post("/api/places")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(place)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        place = objectMapper.readValue(placeResponse, Place.class);

        mockMvc.perform(delete("/api/places/" + place.getId())
                        .param("userId", String.valueOf(adminUser.getId())))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deletePlace_notFound_shouldReturn404() throws Exception {
        mockMvc.perform(delete("/api/places/999")
                        .param("userId", String.valueOf(adminUser.getId())))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deletePlace_asUser_shouldReturnUnauthorized() throws Exception {
        Place place = new Place();
        place.setName("Test Place");
        place.setLocation("Test Location");
        place.setLatitude(48.858844);
        place.setLongitude(2.294351);

        String placeResponse = mockMvc.perform(post("/api/places")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(place)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        place = objectMapper.readValue(placeResponse, Place.class);

        mockMvc.perform(delete("/api/places/" + place.getId())
                        .param("userId", String.valueOf(normalUser.getId())))
                .andExpect(status().isUnauthorized());
    }


    @Test
    public void getUnvalidatedAndRejectedPlaces_asAdmin_shouldReturnPlaces() throws Exception {
        mockMvc.perform(get("/api/places/unvalidatedAndRejectedPlaces")
                        .param("userId", String.valueOf(adminUser.getId())))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    public void getUnvalidatedAndRejectedPlaces_asUser_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/places/unvalidatedAndRejectedPlaces")
                        .param("userId", String.valueOf(normalUser.getId())))
                .andExpect(status().isUnauthorized());
    }


    @Test
    public void getRejectedPlaces_asAdmin_shouldReturnRejectedPlaces() throws Exception {
        mockMvc.perform(get("/api/places/rejectedPlaces")
                        .param("userId", String.valueOf(adminUser.getId())))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    public void getRejectedPlaces_asUser_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/places/rejectedPlaces")
                        .param("userId", String.valueOf(normalUser.getId())))
                .andExpect(status().isUnauthorized());
    }

}
