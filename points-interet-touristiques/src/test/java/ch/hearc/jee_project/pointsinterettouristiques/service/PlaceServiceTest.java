package ch.hearc.jee_project.pointsinterettouristiques.service;

import ch.hearc.jee_project.pointsinterettouristiques.model.Place;
import ch.hearc.jee_project.pointsinterettouristiques.model.ValidationStatus;
import ch.hearc.jee_project.pointsinterettouristiques.repository.PlaceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class PlaceServiceTest {

    @Mock
    private PlaceRepository placeRepository;

    @InjectMocks
    private PlaceService placeService;

    private Place place;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        place = new Place();
        place.setName("Test Place");
        place.setLocation("Test Location");
        place.setLatitude(48.858844);
        place.setLongitude(2.294351);
        place.setStatus(ValidationStatus.UNVALIDATED);
    }

    // Test : Récupérer tous les lieux
    @Test
    public void getAllPlaces_shouldReturnAllPlaces() {
        when(placeRepository.findAll()).thenReturn(List.of(place));

        List<Place> places = placeService.getAllPlaces();

        assertThat(places).hasSize(1);
        assertThat(places.get(0).getName()).isEqualTo("Test Place");
        verify(placeRepository, times(1)).findAll();
    }

    // Test : Créer un lieu
    @Test
    public void createPlace_shouldSavePlaceWithUnvalidatedStatus() {
        when(placeRepository.save(place)).thenReturn(place);

        Place savedPlace = placeService.createPlace(place);

        assertThat(savedPlace.getStatus()).isEqualTo(ValidationStatus.UNVALIDATED);
        verify(placeRepository, times(1)).save(place);
    }

    // Test : Récupérer les lieux par statut
    @Test
    public void getPlacesByStatus_shouldReturnCorrectPlaces() {
        when(placeRepository.findByStatus(ValidationStatus.UNVALIDATED)).thenReturn(List.of(place));

        List<Place> unvalidatedPlaces = placeService.getPlacesByStatus(ValidationStatus.UNVALIDATED);

        assertThat(unvalidatedPlaces).hasSize(1);
        assertThat(unvalidatedPlaces.get(0).getName()).isEqualTo("Test Place");
        verify(placeRepository, times(1)).findByStatus(ValidationStatus.UNVALIDATED);
    }

    // Test : Supprimer un lieu
    @Test
    public void deletePlace_shouldCallRepositoryDeleteById() {
        Long placeId = 1L;

        placeService.deletePlace(placeId);

        verify(placeRepository, times(1)).deleteById(placeId);
    }
}
