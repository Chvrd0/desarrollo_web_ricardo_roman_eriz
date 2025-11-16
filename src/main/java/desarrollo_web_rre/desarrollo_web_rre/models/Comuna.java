package desarrollo_web_rre.desarrollo_web_rre.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table
public class Comuna {

    @Id
    @GeneratedValue(
        strategy = GenerationType.IDENTITY
    )
    private Integer id;

    @NotNull
    private String nombre;

    @NotNull
    private Integer regionId;

    public Comuna() {
    }

    public Comuna(String nombre, Integer regionId) {
        this.nombre = nombre;
        this.regionId = regionId;
    }

    public Integer getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public Integer getRegionId() {
        return regionId;
    }
}