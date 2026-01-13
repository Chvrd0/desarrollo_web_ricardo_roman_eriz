package desarrollo_web_rre.desarrollo_web_rre.models;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA para la entidad {@link Comentario}.
 *
 * <p>Proporciona acceso a la tabla de comentarios y permite realizar operaciones
 * CRUD estándar mediante la extensión de {@link JpaRepository}. Además,
 * incorpora una consulta personalizada para obtener todos los comentarios
 * asociados a un aviso específico, ordenados cronológicamente.</p>
 */
@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Integer> {

    /**
     * Obtiene todos los comentarios asociados a un aviso, ordenados por fecha en
     * orden ascendente (del más antiguo al más reciente).
     *
     * <p>Este método es ideal para mostrar la conversación completa en el orden
     * en que se fueron publicando los comentarios.</p>
     *
     * @param avisoId ID del aviso del cual se desean obtener los comentarios.
     * @return Lista de comentarios ordenada por fecha ascendente.
     */
    List<Comentario> findAllByAvisoIdOrderByFechaAsc(Integer avisoId);
}