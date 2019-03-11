package mx.raul.clientes.models.service;

import mx.raul.clientes.models.entity.Cliente;

import java.util.List;

public interface IClienteService {

    List<Cliente> findAll();
    Cliente finfdById(Long id);
    Cliente save(Cliente cliente);
    void delete(Long id);
}
