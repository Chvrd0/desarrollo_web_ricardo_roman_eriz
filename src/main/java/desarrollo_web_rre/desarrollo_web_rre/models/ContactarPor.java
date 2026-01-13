package desarrollo_web_rre.desarrollo_web_rre.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

/**
 * Entidad JPA que representa un método de contacto específico para un aviso.
 * Permite almacenar múltiples formas de contacto para un solo aviso (ej. WhatsApp, Correo, etc.).
 * Esta clase se mapea a la tabla "contactar_por" en la base de datos.
 */
@Entity
@Table // Mapea a la tabla "contactar_por" (por convención de JPA)
public class ContactarPor {

    /**
     * Identificador único del método de contacto (Clave Primaria).
     * Se genera automáticamente usando la estrategia de identidad de la base de datos.
     */
    @Id
    @GeneratedValue(
        strategy = GenerationType.IDENTITY 
    )
    private Integer id;

    /**
     * El nombre o tipo de medio de contacto (ej. "WhatsApp", "Correo", "Teléfono").
     * No puede ser nulo.
     */
    @NotNull
    private String nombre;       // Ej: "WhatsApp", "Correo"

    /**
     * El dato específico del contacto (ej. el número de teléfono, la dirección de email, el @usuario).
     * No puede ser nulo.
     */
    @NotNull
    private String identificador; // número, mail, @usuario, etc.

    /**
     * El ID del 'Aviso' al que está asociado este método de contacto.
     * Actúa como clave foránea (FK) para la entidad Aviso.
     * No puede ser nulo.
     */
    @NotNull
    private Integer avisoId;       // referencia a Aviso

    /**
     * Constructor por defecto.
     * Requerido por el framework JPA para la creación de instancias.
     */
    public ContactarPor() {
    }

    /**
     * Constructor para crear una nueva instancia de ContactarPor.
     *
     * @param nombre El tipo de medio de contacto (ej. "WhatsApp").
     * @param identificador El dato de contacto (ej. "+569...").
     * @param avisoId El ID del aviso al que se asocia.
     */
    public ContactarPor(String nombre, String identificador, Integer avisoId) {
        this.nombre = nombre;
        this.identificador = identificador;
        this.avisoId = avisoId;
    }

    /**
     * Obtiene el ID del registro de contacto.
     *
     * @return El ID único.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Obtiene el nombre del medio de contacto.
     *
     * @return El tipo de contacto (ej. "Correo").
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Obtiene el identificador o dato específico del contacto.
     *
     * @return El dato de contacto (ej. "ejemplo@correo.com").
     */
    public String getIdentificador() {
        return identificador;
    }

    /**
     * Obtiene el ID del aviso al que pertenece este método de contacto.
     *
     * @return El ID del aviso asociado.
     */
    public Integer getAvisoId() {
        return avisoId;
    }
}