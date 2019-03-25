package mx.raul.clientes.controllers;

import mx.raul.clientes.models.entity.Cliente;
import mx.raul.clientes.models.service.IClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = {"http://localhost:4200"})
@RestController
@RequestMapping("/api")
public class ClienteController {

    @Autowired
    private IClienteService clienteService;

    @GetMapping("/clientes")
    public List<Cliente> index() {
        return clienteService.findAll();
    }

    @GetMapping("/clientes/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> show(@PathVariable Long id) {
        try {
            Cliente cliente = clienteService.finfdById(id);
            if (cliente != null) {
                return new ResponseEntity<>(cliente, HttpStatus.OK);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("msj", "No existe cliente con ID " + id);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (DataAccessException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("msj", "Error al intentar obtener datos");
            response.put("err", e.getMessage() + ". " + e.getMostSpecificCause());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/clientes")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> create(@RequestBody Cliente cliente) {
        Map<String, Object> response = new HashMap<>();
        try {
            Cliente clienteCreado = clienteService.save(cliente);
            response.put("cliente", clienteCreado);
            response.put("msj", "Cliente creado");
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (DataAccessException e) {
            response.put("msj", "Error al intentar crear cliente");
            response.put("err", e.getMessage() + ". " + e.getMostSpecificCause());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/clientes/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> update(@RequestBody Cliente cliente, @PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Cliente clienteActualizado = clienteService.finfdById(id);
            if (clienteActualizado != null) {
                clienteActualizado.setNombre(cliente.getNombre());
                clienteActualizado.setApellido(cliente.getApellido());
                clienteActualizado.setEmail(cliente.getEmail());

                clienteActualizado = clienteService.save(clienteActualizado);

                response.put("msj", "Cliente actualizado");
                response.put("cliente", clienteActualizado);
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.put("msj", "No existe el cliente");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (DataAccessException e) {
            response.put("msj", "Error al intentar actualizar cliente");
            response.put("err", e.getMessage() + ". " + e.getMostSpecificCause());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/clientes/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            clienteService.delete(id);
            response.put("msj", "Cliente eliminado");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (DataAccessException e) {
            response.put("msj", "Error al intentar eliminar cliente");
            response.put("err", e.getMessage() + ". " + e.getMostSpecificCause());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
