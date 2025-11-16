package desarrollo_web_rre.desarrollo_web_rre.models;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegionRepository extends JpaRepository<Region, Integer> {
    // Con JpaRepository ya tienes findAll(), findById(), etc.
}
