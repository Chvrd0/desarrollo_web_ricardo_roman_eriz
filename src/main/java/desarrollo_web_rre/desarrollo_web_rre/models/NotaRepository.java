package desarrollo_web_rre.desarrollo_web_rre.models;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA para la entidad {@link Nota}.
 *
 * <p>Administra las operaciones de persistencia relacionadas con las notas
 * (calificaciones) que los usuarios asignan a los avisos. Extiende
 * {@link JpaRepository}, lo que proporciona métodos CRUD completos sin necesidad
 * de implementación manual.</p>
 */
@Repository
public interface NotaRepository extends JpaRepository<Nota, Integer> {

    /**
     * Obtiene todas las notas asociadas a un aviso específico.
     *
     * <p>Este método es fundamental para calcular estadísticas como el promedio
     * de notas que se muestra en la vista del detalle del aviso.</p>
     *
     * @param avisoId ID del aviso cuyas notas se quieren obtener.
     * @return Lista de notas vinculadas al aviso.
     */
    List<Nota> findAllByAvisoId(Integer avisoId);
}