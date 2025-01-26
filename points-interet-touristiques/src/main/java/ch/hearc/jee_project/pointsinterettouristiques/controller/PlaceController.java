package ch.hearc.jee_project.pointsinterettouristiques.controller;

import ch.hearc.jee_project.pointsinterettouristiques.model.Place;
import ch.hearc.jee_project.pointsinterettouristiques.model.Role;
import ch.hearc.jee_project.pointsinterettouristiques.model.User;
import ch.hearc.jee_project.pointsinterettouristiques.model.ValidationStatus;
import ch.hearc.jee_project.pointsinterettouristiques.service.PlaceService;
import ch.hearc.jee_project.pointsinterettouristiques.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/places")
public class PlaceController {

    private final PlaceService placeService;
    private final UserRepository userRepository;

    public PlaceController(PlaceService placeService, UserRepository userRepository) {
        this.placeService = placeService;
        this.userRepository = userRepository;
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public static class UnauthorizedAccessException extends RuntimeException {
        public UnauthorizedAccessException(String message) {
            super(message);
        }
    }

    private void authorizeAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getRole() != Role.ADMIN) {
            throw new UnauthorizedAccessException("Unauthorized access");
        }
    }

    // Récupérer tous les lieux (admin)
    @GetMapping
    public ResponseEntity<List<Place>> getAllPlaces(@RequestParam Long userId) {
        authorizeAdmin(userId);
        return ResponseEntity.ok(placeService.getAllPlaces());
    }

    // Ajouter un lieu
    @PostMapping
    public ResponseEntity<Place> createPlace(@RequestBody Place place) {
        Place savedPlace = placeService.createPlace(place);
        return new ResponseEntity<>(savedPlace, HttpStatus.CREATED);
    }

    // Récupérer un lieu par ID (admin peut tout voir, user seulement lieux publiés)
    @GetMapping("/{id}")
    public ResponseEntity<Place> getPlaceById(@PathVariable Long id, @RequestParam Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Place place = placeService.getPlaceById(id);
        if (user.getRole() == Role.ADMIN || place.getStatus() == ValidationStatus.VALIDATED) {
            return ResponseEntity.ok(place);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    // Mettre à jour un lieu (admin)
    @PutMapping("/{id}")
    public ResponseEntity<Place> updatePlace(@PathVariable Long id, @RequestParam Long userId, @RequestBody Place placeDetails) {
        authorizeAdmin(userId);
        Place updatedPlace = placeService.updatePlace(id, placeDetails);
        return ResponseEntity.ok(updatedPlace);
    }

    // Supprimer un lieu (admin)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlace(@PathVariable Long id, @RequestParam Long userId) {
        authorizeAdmin(userId);
        placeService.deletePlace(id);
        return ResponseEntity.notFound().build();
    }

    // Récupérer tous les lieux validés
    @GetMapping("/validatedPlaces")
    public ResponseEntity<List<Place>> getValidatedPlaces() {
        return ResponseEntity.ok(placeService.getPlacesByStatus(ValidationStatus.VALIDATED));
    }

    // Récupérer les lieux non validés (admin)
    @GetMapping("/unvalidatedPlaces")
    public ResponseEntity<List<Place>> getUnvalidatedPlaces(@RequestParam Long userId) {
        authorizeAdmin(userId);
        return ResponseEntity.ok(placeService.getPlacesByStatus(ValidationStatus.UNVALIDATED));
    }

    // Récupérer les lieux refusés (admin)
    @GetMapping("/rejectedPlaces")
    public ResponseEntity<List<Place>> getRejectedPlaces(@RequestParam Long userId) {
        authorizeAdmin(userId);
        return ResponseEntity.ok(placeService.getPlacesByStatus(ValidationStatus.REJECTED));
    }

    // Récupérer les lieux non validés et refusés (admin)
    @GetMapping("/unvalidatedAndRejectedPlaces")
    public ResponseEntity<List<Place>> getUnvalidatedAndRejectedPlaces(@RequestParam Long userId) {
        authorizeAdmin(userId);
        return ResponseEntity.ok(placeService.getPlacesByStatuses(List.of(ValidationStatus.UNVALIDATED, ValidationStatus.REJECTED)));
    }

    // Validation d'un lieu (admin)
    @PatchMapping("/{id}/validate")
    public ResponseEntity<Place> validatePlace(@PathVariable Long id, @RequestParam Long userId) {
        authorizeAdmin(userId);
        return ResponseEntity.ok(placeService.validatePlace(id));
    }

    // Rejeter un lieu (admin)
    @PatchMapping("/{id}/reject")
    public ResponseEntity<Place> rejectPlace(@PathVariable Long id, @RequestParam Long userId) {
        authorizeAdmin(userId);
        return ResponseEntity.ok(placeService.rejectPlace(id));
    }

    // Ratings :

    // Endpoint pour ajouter une note
    @PostMapping("/{id}/rate")
    public ResponseEntity<Void> ratePlace(@PathVariable Long id, @RequestParam Long userId, @RequestParam int rating) {
        if (rating < 1 || rating > 10) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        try {
            placeService.ratePlace(id, userId, rating);
            return ResponseEntity.ok().build();
        } catch (RuntimeException ex) {
            if ("Only validated places can be rated".equals(ex.getMessage())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            throw ex; // Pour les autres exceptions, laissez-les être gérées ailleurs.
        }
    }

    // Endpoint pour voir les notes
    @GetMapping("/{id}/ratings")
    public ResponseEntity<List<Integer>> getRatings(@PathVariable Long id) {
        List<Integer> ratings = placeService.getRatings(id);
        return ResponseEntity.ok(ratings);
    }

    // Endpoint pour voir la moyenne des notes
    @GetMapping("/{id}/average-rating")
    public ResponseEntity<Double> getAverageRating(@PathVariable Long id) {
        double averageRating = placeService.getAverageRating(id);
        return ResponseEntity.ok(averageRating);
    }
}
