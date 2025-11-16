package desarrollo_web_rre.desarrollo_web_rre.models;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComunaRepository extends JpaRepository<Comuna, Integer> {

    // Devuelve todas las comunas de una región dada (por id)
    List<Comuna> findAllByRegionId(Integer regionId);
    Comuna findByNombre(String nombre);
}
