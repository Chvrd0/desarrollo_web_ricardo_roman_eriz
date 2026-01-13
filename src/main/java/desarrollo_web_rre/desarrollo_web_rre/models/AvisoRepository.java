package desarrollo_web_rre.desarrollo_web_rre.models;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA para la entidad {@link Aviso}.
 *
 * <p>Permite realizar operaciones CRUD y consultas personalizadas sobre la tabla
 * <code>aviso_adopcion</code>. Extiende {@link JpaRepository}, lo que proporciona
 * automáticamente métodos como:</p>
 *
 * <ul>
 *     <li>findAll()</li>
 *     <li>findById()</li>
 *     <li>save()</li>
 *     <li>delete()</li>
 *     <li>count()</li>
 * </ul>
 *
 * <p>Además, incluye una consulta personalizada para obtener los avisos ordenados
 * por fecha de ingreso, permitiendo paginación nativa mediante {@link Pageable}.</p>
 */
@Repository
public interface AvisoRepository extends JpaRepository<Aviso, Integer> {

    /**
     * Retorna una página de avisos ordenados de forma descendente por la fecha de ingreso.
     *
     * <p>Este método es útil para mostrar los avisos más recientes primero y
     * manejar listas extensas mediante paginación en el frontend.</p>
     *
     * @param pageable Parámetros de paginación: número de página, tamaño de página y ordenamiento.
     * @return Una página de {@link Aviso} ordenada desde el más reciente al más antiguo.
     */
    Page<Aviso> findAllByOrderByFechaIngresoDesc(Pageable pageable);
}