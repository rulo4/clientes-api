package mx.raul.clientes.models.dao;

import mx.raul.clientes.models.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IClienteJpaDao extends JpaRepository<Cliente, Long> {
}
