package desarrollo_web_rre.desarrollo_web_rre.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

/**
 * Entidad JPA que representa una Comuna (municipio o división administrativa).
 * Esta clase se mapea a la tabla "comuna" en la base de datos.
 */
@Entity
@Table // Mapea a la tabla "comuna" por nombre de clase
public class Comuna {

    /**
     * Identificador único de la comuna (Clave Primaria).
     * Se genera automáticamente usando la estrategia de identidad de la base de datos.
     */
    @Id
    @GeneratedValue(
        strategy = GenerationType.IDENTITY
    )
    private Integer id;

    /**
     * Nombre de la comuna (ej. "Santiago", "Providencia").
     * No puede ser nulo.
     */
    @NotNull
    private String nombre;

    /**
     * El ID de la 'Region' a la que pertenece esta comuna.
     * Actúa como clave foránea (FK) para la entidad Region.
     * No puede ser nulo.
     */
    @NotNull
    private Integer regionId;

    /**
     * Constructor por defecto.
     * Requerido por el framework JPA para la hidratación de entidades.
     */
    public Comuna() {
    }

    /**
     * Constructor para crear una nueva instancia de Comuna.
     *
     * @param nombre El nombre de la comuna.
     * @param regionId El ID de la región a la que se asocia.
     */
    public Comuna(String nombre, Integer regionId) {
        this.nombre = nombre;
        this.regionId = regionId;
    }

    /**
     * Obtiene el ID de la comuna.
     *
     * @return El ID único de la comuna.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Obtiene el nombre de la comuna.
     *
     * @return El nombre de la comuna.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Obtiene el ID de la región a la que pertenece la comuna.
     *
     * @return El ID de la región asociada.
     */
    public Integer getRegionId() {
        return regionId;
    }
}