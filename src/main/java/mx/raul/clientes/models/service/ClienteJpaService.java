package mx.raul.clientes.models.service;

import mx.raul.clientes.models.dao.IClienteJpaDao;
import mx.raul.clientes.models.entity.Cliente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClienteJpaService implements IClienteJpaService {

    @Autowired
    private IClienteJpaDao iClienteJpaDao;

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> findAll() {
        return iClienteJpaDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Cliente> findAll(Pageable pageable) {
        return iClienteJpaDao.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Cliente finfdById(Long id) {
        return iClienteJpaDao.findById(id).orElse(null);
    }

    @Override
    public Cliente save(Cliente cliente) {
        return iClienteJpaDao.save(cliente);
    }

    @Override
    public void delete(Long id) {
        iClienteJpaDao.deleteById(id);
    }
}
