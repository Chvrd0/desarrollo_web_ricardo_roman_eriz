package desarrollo_web_rre.desarrollo_web_rre.models;

import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

/**
 * Representa un aviso de adopción registrado por un usuario en la plataforma.
 * 
 * <p>Cada aviso contiene información sobre el animal o animales ofrecidos en adopción,
 * así como datos de contacto del usuario que publica el aviso. Los avisos se almacenan
 * en la tabla <code>aviso_adopcion</code>.</p>
 */
@Entity
@Table(name = "aviso_adopcion")
public class Aviso {

    /**
     * Identificador único del aviso.
     * Se genera automáticamente mediante autoincrement.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** Identificador de la comuna asociada al aviso. */
    @NotNull
    private Integer comunaId;

    /** Sector dentro de la comuna. Puede ser opcional. */
    private String sector;

    /** Nombre del usuario que publica el aviso. */
    @NotNull
    private String nombre;

    /** Correo electrónico de contacto. */
    @NotNull
    private String email;

    /** Número de celular del usuario. Es opcional. */
    private String celular;

    /** Tipo de animal ofrecido (gato, perro, etc.). */
    @NotNull
    private String tipo;

    /** Cantidad de animales ofrecidos. */
    @NotNull
    private String cantidad;

    /** Edad del animal o animales. */
    @NotNull
    private String edad;

    /**
     * Unidad de medida asociada a la edad.
     * Ejemplo: "a" para años, "m" para meses.
     */
    @NotNull
    private String unidadMedida;

    /** Fecha aproximada de entrega del animal. */
    @NotNull
    private String fechaEntrega;

    /** Descripción adicional del aviso. */
    private String descripcion;

    /** Fecha y hora en que el aviso fue creado en el sistema. */
    private LocalDateTime fechaIngreso;

    /**
     * Constructor vacío requerido por JPA.
     */
    public Aviso() {
    }

    /**
     * Constructor principal para crear un aviso completo.
     *
     * @param comunaId       ID de la comuna asociada.
     * @param sector         Sector dentro de la comuna.
     * @param nombre         Nombre del contacto.
     * @param email          Email del contacto.
     * @param celular        Número de celular.
     * @param tipo           Tipo de animal.
     * @param cantidad       Cantidad de animales.
     * @param edad           Edad del animal.
     * @param unidadMedida   Unidad de medida para la edad.
     * @param fechaEntrega   Fecha en que se entregará el animal.
     * @param descripcion    Descripción adicional.
     * @param fechaIngreso   Fecha/hora en que se registra el aviso.
     */
    public Aviso(
        Integer comunaId,
        String sector,
        String nombre,
        String email,
        String celular,
        String tipo,
        String cantidad,
        String edad,
        String unidadMedida,
        String fechaEntrega,
        String descripcion,
        LocalDateTime fechaIngreso
    ) {
        this.comunaId = comunaId;
        this.sector = sector;
        this.nombre = nombre;
        this.email = email;
        this.celular = celular;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.edad = edad;
        this.unidadMedida = unidadMedida;
        this.fechaEntrega = fechaEntrega;
        this.descripcion = descripcion;
        this.fechaIngreso = fechaIngreso;
    }

    // ============================
    // GETTERS
    // ============================

    public Integer getId() {
        return id;
    }

    public Integer getComunaId() {
        return comunaId;
    }

    public String getSector() {
        return sector;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public String getCelular() {
        return celular;
    }

    public String getTipo() {
        return tipo;
    }

    public String getCantidad() {
        return cantidad;
    }

    public String getEdad() {
        return edad;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public String getFechaEntrega() {
        return fechaEntrega;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public LocalDateTime getFechaIngreso() {
        return fechaIngreso;
    }

    /**
     * Valida un aviso antes de ser procesado.
     * 
     * <p>Actualmente retorna siempre <code>true</code>, pero se deja preparado
     * para futuras validaciones, como:</p>
     * 
     * <ul>
     *   <li>Validar largo mínimo de la descripción</li>
     *   <li>Validar extensión y tipo de archivo de la foto</li>
     *   <li>Validar presencia de campos obligatorios antes de guardar</li>
     * </ul>
     *
     * @param descripcion Descripción ingresada por el usuario.
     * @param foto        Foto subida mediante formulario.
     * @return true si el aviso es válido; false en caso contrario.
     */
    public static Boolean validateAviso(String descripcion, MultipartFile foto) {
        return true;  // Por ahora sin validación real.
    }
}