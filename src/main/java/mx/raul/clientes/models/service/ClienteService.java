package mx.raul.clientes.models.service;

import mx.raul.clientes.models.dao.ICLienteDao;
import mx.raul.clientes.models.entity.Cliente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClienteService implements IClienteService {

    @Autowired
    private ICLienteDao cLienteDao;

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> findAll() {
        return (List<Cliente>) cLienteDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Cliente finfdById(Long id) {
        return cLienteDao.findById(id).orElse(null);
    }

    @Override
    public Cliente save(Cliente cliente) {
        return cLienteDao.save(cliente);
    }

    @Override
    public void delete(Long id) {
        cLienteDao.deleteById(id);
    }
}
