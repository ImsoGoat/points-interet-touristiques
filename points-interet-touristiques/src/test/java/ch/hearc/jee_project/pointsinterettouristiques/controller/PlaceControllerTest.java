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
    private Place validatedPlace;
    private Place unvalidatedPlace;

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

        // Ajouter un lieu non validé (qui reste non validé)
        unvalidatedPlace = new Place();
        unvalidatedPlace.setName("Unvalidated Place");
        unvalidatedPlace.setLocation("Lyon");
        unvalidatedPlace.setLatitude(45.7640);
        unvalidatedPlace.setLongitude(4.8357);
        unvalidatedPlace.setStatus(ValidationStatus.UNVALIDATED);
        String unvalidatedPlaceResponse = mockMvc.perform(post("/api/places")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(unvalidatedPlace)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        unvalidatedPlace = objectMapper.readValue(unvalidatedPlaceResponse, Place.class);

        // Ajouter un lieu non validé, puis le valider via un admin
        validatedPlace = new Place();
        validatedPlace.setName("Validated Place");
        validatedPlace.setLocation("Paris");
        validatedPlace.setLatitude(48.8566);
        validatedPlace.setLongitude(2.3522);
        validatedPlace.setStatus(ValidationStatus.UNVALIDATED); // Initialement non validé
        String validatedPlaceResponse = mockMvc.perform(post("/api/places")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(validatedPlace)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        validatedPlace = objectMapper.readValue(validatedPlaceResponse, Place.class);

        // Valider le lieu via l'admin
        mockMvc.perform(patch("/api/places/" + validatedPlace.getId() + "/validate")
                        .param("userId", String.valueOf(adminUser.getId())))
                .andExpect(status().isOk());

        // Récupérer le lieu validé pour être sûr de son état
        validatedPlace = objectMapper.readValue(mockMvc.perform(get("/api/places/" + validatedPlace.getId())
                        .param("userId", String.valueOf(adminUser.getId())))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(), Place.class);
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

    // Test : Ajouter une note à un lieu validé
    @Test
    public void rateValidatedPlace_shouldAddRating() throws Exception {
        mockMvc.perform(post("/api/places/" + validatedPlace.getId() + "/rate")
                        .param("userId", String.valueOf(normalUser.getId()))
                        .param("rating", "8"))
                .andExpect(status().isOk());
    }

    // Test : Ajouter une note à un lieu non validé (doit échouer)
    @Test
    public void rateUnvalidatedPlace_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/places/" + unvalidatedPlace.getId() + "/rate")
                        .param("userId", String.valueOf(normalUser.getId()))
                        .param("rating", "8"))
                .andExpect(status().isBadRequest());
    }


    // Test : Ajouter une note invalide
    @Test
    public void ratePlace_withInvalidRating_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/places/" + validatedPlace.getId() + "/rate")
                        .param("userId", String.valueOf(normalUser.getId()))
                        .param("rating", "15")) // Note invalide
                .andExpect(status().isBadRequest());
    }

    // Test : Voir les notes d'un lieu
    @Test
    public void getRatings_shouldReturnRatings() throws Exception {
        // Ajouter des notes
        mockMvc.perform(post("/api/places/" + validatedPlace.getId() + "/rate")
                        .param("userId", String.valueOf(normalUser.getId()))
                        .param("rating", "8"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/places/" + validatedPlace.getId() + "/rate")
                        .param("userId", String.valueOf(normalUser.getId()))
                        .param("rating", "7"))
                .andExpect(status().isOk());

        // Vérifier les notes
        mockMvc.perform(get("/api/places/" + validatedPlace.getId() + "/ratings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").value(8))
                .andExpect(jsonPath("$[1]").value(7));
    }

    // Test : Voir la moyenne des notes d'un lieu
    @Test
    public void getAverageRating_shouldReturnAverageRating() throws Exception {
        // Ajouter des notes
        mockMvc.perform(post("/api/places/" + validatedPlace.getId() + "/rate")
                        .param("userId", String.valueOf(normalUser.getId()))
                        .param("rating", "8"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/places/" + validatedPlace.getId() + "/rate")
                        .param("userId", String.valueOf(normalUser.getId()))
                        .param("rating", "6"))
                .andExpect(status().isOk());

        // Vérifier la moyenne
        mockMvc.perform(get("/api/places/" + validatedPlace.getId() + "/average-rating"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(7.0));
    }

}
