package desarrollo_web_rre.desarrollo_web_rre.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table
public class Foto {

    @Id
    @SequenceGenerator(
        name = "foto_sequence",
        sequenceName = "foto_sequence",
        allocationSize = 1
    )
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "foto_sequence"
    )
    private Integer id;

    @NotNull
    private String rutaArchivo;

    @NotNull
    private String nombreArchivo;

    @NotNull
    private Long avisoId; // referencia a Aviso

    public Foto() {
    }

    public Foto(String rutaArchivo, String nombreArchivo, Long avisoId) {
        this.rutaArchivo = rutaArchivo;
        this.nombreArchivo = nombreArchivo;
        this.avisoId = avisoId;
    }

    public Integer getId() {
        return id;
    }

    public String getRutaArchivo() {
        return rutaArchivo;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public Long getAvisoId() {
        return avisoId;
    }
}
