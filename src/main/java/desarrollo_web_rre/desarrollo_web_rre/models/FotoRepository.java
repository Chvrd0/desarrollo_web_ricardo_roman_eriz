package desarrollo_web_rre.desarrollo_web_rre.models;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA para la entidad {@link Foto}.
 *
 * <p>Se encarga de gestionar las operaciones de persistencia de las fotografías
 * asociadas a cada aviso. Cada foto contiene información sobre la ruta del archivo
 * almacenado y está vinculada al aviso correspondiente.</p>
 */
@Repository
public interface FotoRepository extends JpaRepository<Foto, Integer> {

    /**
     * Obtiene la primera foto asociada a un aviso, determinada por el orden ascendente del ID.
     *
     * <p>Este método suele utilizarse para mostrar una imagen principal o miniatura
     * representativa del aviso.</p>
     *
     * @param avisoId ID del aviso.
     * @return La primera foto del aviso, o {@code null} si no existen fotos asociadas.
     */
    Foto findFirstByAvisoIdOrderByIdAsc(Integer avisoId);

    /**
     * Obtiene todas las fotos asociadas a un aviso ordenadas por ID ascendente.
     *
     * <p>Útil para mostrar una galería completa de imágenes en el detalle del aviso.</p>
     *
     * @param avisoId ID del aviso.
     * @return Lista de fotografías asociadas al aviso, ordenadas de la más antigua a la más reciente.
     */
    List<Foto> findAllByAvisoIdOrderByIdAsc(Integer avisoId);
}