package desarrollo_web_rre.desarrollo_web_rre.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

/**
 * Entidad JPA que representa una fotografía asociada a un aviso.
 * Almacena la información de la ubicación (ruta y nombre) de un archivo de imagen.
 * Esta clase se mapea a la tabla "foto" en la base de datos.
 */
@Entity
@Table // Mapea a la tabla "foto" por nombre de clase
public class Foto {

    /**
     * Identificador único de la foto (Clave Primaria).
     * Se genera automáticamente usando la estrategia de identidad de la base de datos.
     */
    @Id
    @GeneratedValue(
        strategy = GenerationType.IDENTITY
    )
    private Integer id;

    /**
     * La ruta del directorio donde se almacena físicamente el archivo
     * (ej. "uploads/imagenes/").
     * No puede ser nulo.
     */
    @NotNull
    private String rutaArchivo;

    /**
     * El nombre único o asignado al archivo de imagen
     * (ej. "uuid-nombre-original.jpg").
     * No puede ser nulo.
     */
    @NotNull
    private String nombreArchivo;

    /**
     * El ID del 'Aviso' al que está asociada esta foto.
     * Actúa como clave foránea (FK) para la entidad Aviso.
     * No puede ser nulo.
     */
    @NotNull
    private Integer avisoId; // referencia a Aviso

    /**
     * Constructor por defecto.
     * Requerido por el framework JPA para la creación de instancias.
     */
    public Foto() {
    }

    /**
     * Constructor para crear una nueva instancia de Foto.
     *
     * @param rutaArchivo La ruta del directorio donde se guarda el archivo.
     * @param nombreArchivo El nombre del archivo de imagen.
     * @param avisoId El ID del aviso al que se asocia esta foto.
     */
    public Foto(String rutaArchivo, String nombreArchivo, Integer avisoId) {
        this.rutaArchivo = rutaArchivo;
        this.nombreArchivo = nombreArchivo;
        this.avisoId = avisoId;
    }

    /**
     * Obtiene el ID de la foto.
     *
     * @return El ID único de la foto.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Obtiene la ruta del directorio del archivo.
     *
     * @return La ruta del archivo (ej. "uploads/imagenes/").
     */
    public String getRutaArchivo() {
        return rutaArchivo;
    }

    /**
     * Obtiene el nombre del archivo de imagen.
     *
     * @return El nombre del archivo.
     */
    public String getNombreArchivo() {
        return nombreArchivo;
    }

    /**
     * Obtiene el ID del aviso al que pertenece la foto.
     *
     * @return El ID del aviso asociado.
     */
    public Integer getAvisoId() {
        return avisoId;
    }
}