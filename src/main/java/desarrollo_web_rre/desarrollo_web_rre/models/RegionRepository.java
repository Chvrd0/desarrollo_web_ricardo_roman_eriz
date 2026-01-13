package desarrollo_web_rre.desarrollo_web_rre.models;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA para la entidad {@link Region}.
 *
 * <p>Proporciona operaciones CRUD completas para la tabla de regiones gracias
 * a la extensión de {@link JpaRepository}. Dado que las consultas estándar son
 * suficientes para esta entidad, no se definen métodos personalizados.</p>
 */
@Repository
public interface RegionRepository extends JpaRepository<Region, Integer> {
    // No se requieren métodos adicionales por ahora.
}