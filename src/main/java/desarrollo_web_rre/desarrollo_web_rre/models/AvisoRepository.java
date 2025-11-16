package desarrollo_web_rre.desarrollo_web_rre.models;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AvisoRepository extends JpaRepository<Aviso, Integer> {

    Page<Aviso> findAllByOrderByFechaIngresoDesc(Pageable pageable);
}