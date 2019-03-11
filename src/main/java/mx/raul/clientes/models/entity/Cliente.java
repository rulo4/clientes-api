package mx.raul.clientes.models.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "clientes")
public class Cliente implements Serializable {

    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    private String nombre;

    @Getter
    @Setter
    private String apellido;

    @Getter
    @Setter
    private String email;

    @Getter
    @Setter
    @Column(name = "creacion")
    @Temporal(TemporalType.DATE)
    private Date creacion;

    @PrePersist
    public void prePersist(){
        creacion = new Date();
    }
}
