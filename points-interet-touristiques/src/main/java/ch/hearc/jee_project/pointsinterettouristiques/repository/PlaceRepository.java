package ch.hearc.jee_project.pointsinterettouristiques.repository;

import ch.hearc.jee_project.pointsinterettouristiques.model.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {
}
