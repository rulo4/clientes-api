package mx.raul.clientes.controllers;

import mx.raul.clientes.models.entity.Cliente;
import mx.raul.clientes.models.service.IClienteJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = {"http://localhost:4200"})
@RestController
@RequestMapping("/api")
public class ClienteController {

    private static final String CLIENTE_RESPONSE_KEY = "cliente";
    private static final Integer PAGE_SIZE = 3;
    private static final String DIRECTORIO_FOTOS = "resources/fotos";

    private IClienteJpaService clienteService;

    @Autowired
    public ClienteController(IClienteJpaService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping("/clientes")
    public List<Cliente> index() {
        return clienteService.findAll();
    }

    @GetMapping("/clientes/pagina/{pagina}")
    public Page<Cliente> index(@PathVariable Integer pagina) {
        return clienteService.findAll(PageRequest.of(pagina, PAGE_SIZE));
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
            deletePhoto(id);
            clienteService.delete(id);
            response.put("msj", "Cliente eliminado");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (DataAccessException e) {
            response.put("msj", "Error al intentar eliminar cliente");
            response.put("err", e.getMessage() + ". " + e.getMostSpecificCause());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/clientes/upload")
    public ResponseEntity<Map> uploadFile(@RequestParam("archivo") MultipartFile archivo, @RequestParam("id") Long id) {
        Cliente cliente = clienteService.finfdById(id);
        Map<String, Object> response = new HashMap<>();
        if (!archivo.isEmpty()) {
            String nombreArchivo = archivo.getOriginalFilename();
            Path rutaArchivo = Paths.get(DIRECTORIO_FOTOS).resolve(nombreArchivo).toAbsolutePath();
            try {
                Files.copy(archivo.getInputStream(), rutaArchivo);
            } catch (IOException e) {
                response.put("msj", "Error al intentar subir archivo");
                response.put("err", e.getMessage());
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            deletePhoto(cliente.getId());
            cliente.setFoto(nombreArchivo);
            clienteService.save(cliente);
            response.put("msj", "Archivo agregado");
            response.put("cliente", cliente);
        }
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/resources/fotos/{nombreFoto:.+}")
    public ResponseEntity<Resource> getFoto(@PathVariable String nombreFoto) {
        Path rutaFoto = Paths.get(DIRECTORIO_FOTOS).resolve(nombreFoto).toAbsolutePath();
        Map<String, Object> response = new HashMap<>();
        try {
            Resource foto = new UrlResource(rutaFoto.toUri());
            response.put("foto", foto);
            return ResponseEntity
                    .ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+ foto.getFilename() +"\"")
                    .body(foto);
        } catch (MalformedURLException e) {
//            response.put("msj", "Error al intentar obtener archivo");
//            response.put("err", e.getMessage());
//            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            throw new RuntimeException("Error al intentar leer archivo");
        }
    }

    private void deletePhoto(Long clienteId) {
        Cliente cliente = clienteService.finfdById(clienteId);
        String nombreArchivoAnterior = cliente.getFoto();
        if (nombreArchivoAnterior != null && !nombreArchivoAnterior.isEmpty()) {
            Path rutaAnterior = Paths.get(DIRECTORIO_FOTOS).resolve(nombreArchivoAnterior).toAbsolutePath();
            File archivoAnterior = rutaAnterior.toFile();
            archivoAnterior.delete();
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
