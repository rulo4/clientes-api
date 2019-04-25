package mx.raul.clientes.models.service;

import mx.raul.clientes.models.entity.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IClienteJpaService {

    List<Cliente> findAll();

    Page<Cliente> findAll(Pageable pageable);

    Cliente finfdById(Long id);

    Cliente save(Cliente cliente);

    void delete(Long id);
}
