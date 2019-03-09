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
    private ICLienteDao icLienteDao;

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> findAll() {
        return (List<Cliente>) icLienteDao.findAll();
    }
}
