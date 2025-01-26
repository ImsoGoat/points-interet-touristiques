package ch.hearc.jee_project.pointsinterettouristiques.model;

import jakarta.persistence.*;
import org.hibernate.annotations.Cascade;

import java.util.*;

@Entity
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 1000) // Limite sa longueur maximale
    private String description;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "place_ratings", joinColumns = @JoinColumn(name = "place_id"))
    @MapKeyJoinColumn(name = "user_id")
    @Column(name = "rating")
    @Cascade(org.hibernate.annotations.CascadeType.ALL) // Ajout de la cascade
    private Map<Long, Integer> ratings = new HashMap<>();


    @Column(nullable = false)
    private double averageRating = 0.0; // Moyenne des notes

    @Enumerated(EnumType.STRING) // Stocke l'état en tant que chaîne
    @Column(nullable = false)
    private ValidationStatus status = ValidationStatus.UNVALIDATED;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public ValidationStatus getStatus() {
        return status;
    }

    public void setStatus(ValidationStatus status) {
        this.status = status;
    }

    // Ratings
    public List<Integer> getRatings() {
        return new ArrayList<>(ratings.values());
    }

    public Optional<Integer> getRatingByUser(User user) {
        return Optional.ofNullable(ratings.get(user));
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void removeRating(User user) {
        if (ratings.containsKey(user)) {
            ratings.remove(user);
            updateAverageRating(); // Recalculer la moyenne en interne
        }
    }

    /**
     * Ajoute ou met à jour une note pour un utilisateur spécifique et met à jour la moyenne des notes.
     */
    public void addOrUpdateRating(User user, int rating) {
        ratings.put(user.getId(), rating); // Utilise l'ID de l'utilisateur comme clé
        updateAverageRating(); // Recalcule la moyenne après chaque mise à jour
    }

    public void recalculateAverageRating() {
        updateAverageRating(); // Appelle la méthode privée
    }


    public Optional<Integer> getRatingByUser(Long userId) {
        return Optional.ofNullable(ratings.get(userId));
    }

    /**
     * Met à jour la moyenne des notes en fonction des ratings actuels.
     */
    private void updateAverageRating() {
        if (!ratings.isEmpty()) {
            this.averageRating = ratings.values().stream().mapToInt(Integer::intValue).average().orElse(0.0);
        } else {
            this.averageRating = 0.0;
        }
    }
}
