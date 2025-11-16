package desarrollo_web_rre.desarrollo_web_rre.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table
public class Region {

    @Id
    @GeneratedValue(
        strategy = GenerationType.IDENTITY 
    )
    private Integer id;

    @NotNull
    private String nombre;

    public Region() {
    }

    public Region(String nombre) {
        this.nombre = nombre;
    }

    public Integer getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }
}
