package desarrollo_web_rre.desarrollo_web_rre.models;

import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "aviso_adopcion")
public class Aviso {

    @Id
    @GeneratedValue(
        strategy = GenerationType.IDENTITY
    )
    private Integer id;

    @NotNull
    private Integer comunaId;

    private String sector;

    @NotNull
    private String nombre;

    @NotNull
    private String email;

    private String celular;

    @NotNull
    private String tipo;

    @NotNull
    private String cantidad;

    @NotNull
    private String edad;

    @NotNull
    private String unidadMedida;

    @NotNull
    private String fechaEntrega;

    private String descripcion;

    private LocalDateTime fechaIngreso;

    public Aviso() {
    }

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

    public static Boolean validateAviso(String descripcion, MultipartFile foto) {
        return true;
    }
}