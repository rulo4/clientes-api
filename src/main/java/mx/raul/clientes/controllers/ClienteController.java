package mx.raul.clientes.controllers;

import mx.raul.clientes.models.entity.Cliente;
import mx.raul.clientes.models.service.IClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = {"http://localhost:4200"})
@RestController
@RequestMapping("/api")
public class ClienteController {

    private static final String CLIENTE_RESPONSE_KEY = "cliente";

    private IClienteService clienteService;

    @Autowired
    public ClienteController(IClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping("/clientes")
    public List<Cliente> index() {
        return clienteService.findAll();
    }

    @GetMapping("/clientes/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Map> show(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Cliente cliente = clienteService.finfdById(id);
            if (cliente != null) {
                response.put(CLIENTE_RESPONSE_KEY, cliente);
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.put("msj", "No existe cliente con ID " + id);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (DataAccessException e) {
            response.put("msj", "Error al intentar obtener datos");
            response.put("err", e.getMessage() + ". " + e.getMostSpecificCause());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/clientes")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Map> create(@Valid @RequestBody Cliente cliente, BindingResult bindingResult) {
        ResponseEntity<Map> responseEntity = validate(bindingResult);
        Map<String, Object> response = new HashMap<>();

        if (!responseEntity.hasBody()) {
            try {
                Cliente clienteCreado = clienteService.save(cliente);
                response.put(CLIENTE_RESPONSE_KEY, clienteCreado);
                response.put("msj", "Cliente creado");
                return new ResponseEntity<>(response, HttpStatus.CREATED);
            } catch (DataAccessException e) {
                response.put("msj", "Error al intentar crear cliente");
                response.put("err", e.getMessage() + ". " + e.getMostSpecificCause());
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return responseEntity;
        }
    }

    @PutMapping("/clientes/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Map> update(@Valid @RequestBody Cliente cliente, BindingResult bindingResult, @PathVariable Long id) {
        ResponseEntity<Map> responseEntity = validate(bindingResult);
        Map<String, Object> response = new HashMap<>();

        if (!responseEntity.hasBody()) {
            try {
                Cliente clienteActualizado = clienteService.finfdById(id);
                if (clienteActualizado != null) {
                    clienteActualizado.setNombre(cliente.getNombre());
                    clienteActualizado.setApellido(cliente.getApellido());
                    clienteActualizado.setEmail(cliente.getEmail());

                    clienteActualizado = clienteService.save(clienteActualizado);

                    response.put("msj", "Cliente actualizado");
                    response.put(CLIENTE_RESPONSE_KEY, clienteActualizado);
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
        } else {
            return responseEntity;
        }
    }

    @DeleteMapping("/clientes/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Map> delete(@PathVariable Long id) {
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

    private ResponseEntity<Map> validate(BindingResult bindingResult) {
        Map<String, Object> response = new HashMap<>();
        if (bindingResult.hasErrors()) {
            StringBuilder error = new StringBuilder();
            bindingResult.getFieldErrors().forEach(e ->
                    error.append(e.getField()).append(": ").append(e.getDefaultMessage()).append("<br/>")
            );
            response.put("msj", error.toString());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

}
