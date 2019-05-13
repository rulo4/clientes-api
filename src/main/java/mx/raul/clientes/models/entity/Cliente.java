package mx.raul.clientes.models.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "clientes")
public class Cliente implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    @Size(min = 1)
    private String nombre;

    @NotBlank
    private String apellido;

    @NotBlank
    @Email
    @Column(nullable = false, unique = true)
    @Size(min = 1)
    private String email;

    @Column(name = "creacion", updatable = false)
    @Temporal(TemporalType.DATE)
    private Date creacion;

    private String foto;

    @PrePersist
    public void prePersist(){
        creacion = new Date();
    }
}
