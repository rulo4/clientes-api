package mx.raul.clientes.models.dao;

import mx.raul.clientes.models.entity.Cliente;
import org.springframework.data.repository.CrudRepository;

public interface ICLienteDao  extends CrudRepository<Cliente, Long> {
}
