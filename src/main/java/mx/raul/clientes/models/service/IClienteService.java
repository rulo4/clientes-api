package mx.raul.clientes.models.service;

import mx.raul.clientes.models.entity.Cliente;

import java.util.List;

public interface IClienteService {

    public List<Cliente> findAll();
}
