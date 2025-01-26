package ch.hearc.jee_project.pointsinterettouristiques.service;

import ch.hearc.jee_project.pointsinterettouristiques.model.Place;
import ch.hearc.jee_project.pointsinterettouristiques.model.User;
import ch.hearc.jee_project.pointsinterettouristiques.model.ValidationStatus;
import ch.hearc.jee_project.pointsinterettouristiques.repository.PlaceRepository;
import ch.hearc.jee_project.pointsinterettouristiques.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PlaceService {

    private final PlaceRepository placeRepository;
    private final UserRepository userRepository;

    public PlaceService(PlaceRepository placeRepository, UserRepository userRepository) {
        this.placeRepository = placeRepository;
        this.userRepository = userRepository;
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

    // Ajouter une note à un lieu
    public void ratePlace(Long placeId, Long userId, int rating) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new RuntimeException("Place not found"));

        if (place.getStatus() != ValidationStatus.VALIDATED) {
            throw new RuntimeException("Only validated places can be rated");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        place.addOrUpdateRating(userId, rating);
        placeRepository.save(place);
    }


    // Récupérer les notes d'un lieu
    public Map<Long,Integer> getRatings(Long placeId) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new RuntimeException("Place not found"));

        return place.getRatings();
    }

    // Récupérer la moyenne des notes d'un lieu
    public double getAverageRating(Long placeId) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new RuntimeException("Place not found"));

        return place.getAverageRating();
    }
}
