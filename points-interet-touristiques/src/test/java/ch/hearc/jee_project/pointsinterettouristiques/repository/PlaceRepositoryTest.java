package ch.hearc.jee_project.pointsinterettouristiques.repository;

import ch.hearc.jee_project.pointsinterettouristiques.model.Place;
import ch.hearc.jee_project.pointsinterettouristiques.model.ValidationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class PlaceRepositoryTest {

    @Autowired
    private PlaceRepository placeRepository;

    @BeforeEach
    public void setup() {
        // Ajouter des lieux dans la base de données pour les tests
        Place validatedPlace = new Place();
        validatedPlace.setName("Validated Place");
        validatedPlace.setLocation("Location 1");
        validatedPlace.setLatitude(48.858844);
        validatedPlace.setLongitude(2.294351);
        validatedPlace.setDescription("A description for the test place");
        validatedPlace.setStatus(ValidationStatus.VALIDATED);
        placeRepository.save(validatedPlace);

        Place unvalidatedPlace = new Place();
        unvalidatedPlace.setName("Unvalidated Place");
        unvalidatedPlace.setLocation("Location 2");
        unvalidatedPlace.setLatitude(48.8566);
        unvalidatedPlace.setLongitude(2.3522);
        unvalidatedPlace.setDescription("A description for the test place");
        unvalidatedPlace.setStatus(ValidationStatus.UNVALIDATED);
        placeRepository.save(unvalidatedPlace);

        Place rejectedPlace = new Place();
        rejectedPlace.setName("Rejected Place");
        rejectedPlace.setLocation("Location 3");
        rejectedPlace.setLatitude(40.712776);
        rejectedPlace.setLongitude(-74.005974);
        rejectedPlace.setDescription("A description for the test place");
        rejectedPlace.setStatus(ValidationStatus.REJECTED);
        placeRepository.save(rejectedPlace);
    }

    @Test
    public void findByStatus_shouldReturnCorrectPlaces() {
        // Trouver les lieux validés
        List<Place> validatedPlaces = placeRepository.findByStatus(ValidationStatus.VALIDATED);
        assertThat(validatedPlaces).hasSize(1);
        assertThat(validatedPlaces.get(0).getName()).isEqualTo("Validated Place");

        // Trouver les lieux non validés
        List<Place> unvalidatedPlaces = placeRepository.findByStatus(ValidationStatus.UNVALIDATED);
        assertThat(unvalidatedPlaces).hasSize(1);
        assertThat(unvalidatedPlaces.get(0).getName()).isEqualTo("Unvalidated Place");

        // Trouver les lieux rejetés
        List<Place> rejectedPlaces = placeRepository.findByStatus(ValidationStatus.REJECTED);
        assertThat(rejectedPlaces).hasSize(1);
        assertThat(rejectedPlaces.get(0).getName()).isEqualTo("Rejected Place");
    }

    @Test
    public void findByStatusIn_shouldReturnCorrectPlaces() {
        // Trouver les lieux non validés et rejetés
        List<Place> unvalidatedAndRejectedPlaces = placeRepository.findByStatusIn(
                List.of(ValidationStatus.UNVALIDATED, ValidationStatus.REJECTED));
        assertThat(unvalidatedAndRejectedPlaces).hasSize(2);
        assertThat(unvalidatedAndRejectedPlaces)
                .extracting("name")
                .containsExactlyInAnyOrder("Unvalidated Place", "Rejected Place");
    }

    @Test
    public void findByStatus_emptyResult_shouldReturnEmptyList() {
        // Chercher un statut valide mais non utilisé dans les données de test
        List<Place> places = placeRepository.findByStatus(ValidationStatus.valueOf("VALIDATED"));

        // Vérifiez que la liste est vide si aucun lieu validé n'existe
        placeRepository.deleteAll(); // Supprime tous les lieux pour garantir un test propre
        places = placeRepository.findByStatus(ValidationStatus.VALIDATED);

        assertThat(places).isEmpty();
    }

}
