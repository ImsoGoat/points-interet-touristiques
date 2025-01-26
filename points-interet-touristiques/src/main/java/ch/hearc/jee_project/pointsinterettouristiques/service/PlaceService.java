package ch.hearc.jee_project.pointsinterettouristiques.service;

import ch.hearc.jee_project.pointsinterettouristiques.model.Place;
import ch.hearc.jee_project.pointsinterettouristiques.model.ValidationStatus;
import ch.hearc.jee_project.pointsinterettouristiques.repository.PlaceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlaceService {

    private final PlaceRepository placeRepository;

    public PlaceService(PlaceRepository placeRepository) {
        this.placeRepository = placeRepository;
    }

    public List<Place> getAllPlaces() {
        return placeRepository.findAll();
    }

    public List<Place> getPlacesByStatus(ValidationStatus status) {
        return placeRepository.findByStatus(status);
    }

    public List<Place> getPlacesByStatuses(List<ValidationStatus> statuses) {
        return placeRepository.findByStatusIn(statuses);
    }

    public Place getPlaceById(Long id) {
        return placeRepository.findById(id).orElseThrow(() -> new RuntimeException("Place not found"));
    }

    public Place createPlace(Place place) {
        place.setStatus(ValidationStatus.UNVALIDATED); // Toujours non validé à la création
        return placeRepository.save(place);
    }

    public Place updatePlace(Long id, Place placeDetails) {
        Place place = getPlaceById(id);
        place.setName(placeDetails.getName());
        place.setDescription(placeDetails.getDescription());
        place.setLocation(placeDetails.getLocation());
        place.setLatitude(placeDetails.getLatitude());
        place.setLongitude(placeDetails.getLongitude());
        return placeRepository.save(place);
    }

    public void deletePlace(Long id) {
        placeRepository.deleteById(id);
    }

    public Place validatePlace(Long id) {
        Place place = getPlaceById(id);
        place.setStatus(ValidationStatus.VALIDATED);
        return placeRepository.save(place);
    }

    public Place rejectPlace(Long id) {
        Place place = getPlaceById(id);
        place.setStatus(ValidationStatus.REJECTED);
        return placeRepository.save(place);
    }
}
