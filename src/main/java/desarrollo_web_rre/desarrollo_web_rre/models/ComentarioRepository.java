package desarrollo_web_rre.desarrollo_web_rre.models;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Integer> {

    // Todos los comentarios de un aviso, ordenados por fecha ascendente
    List<Comentario> findAllByAvisoIdOrderByFechaAsc(Integer avisoId);
}
