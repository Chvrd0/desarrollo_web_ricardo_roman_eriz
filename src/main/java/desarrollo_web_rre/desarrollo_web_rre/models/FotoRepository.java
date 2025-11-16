package desarrollo_web_rre.desarrollo_web_rre.models;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FotoRepository extends JpaRepository<Foto, Integer> {

    // Primera foto asociada a un aviso (por id del aviso)
    Foto findFirstByAvisoIdOrderByIdAsc(Long avisoId);
    List<Foto> findAllByAvisoIdOrderByIdAsc(Long avisoId);
}
