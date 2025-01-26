package ch.hearc.jee_project.pointsinterettouristiques.model;

import jakarta.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Entity
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    @ElementCollection
    @CollectionTable(name = "place_ratings", joinColumns = @JoinColumn(name = "place_id"))
    @MapKeyColumn(name = "user_id")
    @Column(name = "rating")
    private Map<Long, Integer> ratings = new HashMap<>();

    @Column(nullable = false)
    private double averageRating = 0.0;

    @Enumerated(EnumType.STRING)
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

    public Map<Long, Integer> getRatings() {
        return ratings;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void addOrUpdateRating(Long userId, int rating) {
        ratings.put(userId, rating);
        updateAverageRating();
    }

    public void removeRating(Long userId) {
        ratings.remove(userId);
        updateAverageRating();
    }

    private void updateAverageRating() {
        if (!ratings.isEmpty()) {
            this.averageRating = ratings.values().stream().mapToInt(Integer::intValue).average().orElse(0.0);
        } else {
            this.averageRating = 0.0;
        }
    }
}
