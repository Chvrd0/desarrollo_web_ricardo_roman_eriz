package desarrollo_web_rre.desarrollo_web_rre.models;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotaRepository extends JpaRepository<Nota, Integer> {

    // Método para buscar todas las notas de un aviso específico
    List<Nota> findAllByAvisoId(Integer avisoId);
}