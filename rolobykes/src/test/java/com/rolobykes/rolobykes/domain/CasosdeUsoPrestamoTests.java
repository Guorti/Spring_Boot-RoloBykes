package com.rolobykes.rolobykes.domain;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.rolobykes.dataaccess.BicicletaRepository;
import com.rolobykes.dataaccess.PrestamoRepository;
import com.rolobykes.dataaccess.ReservaRepository;
import com.rolobykes.dataaccess.TipoBicicletaRepository;
import com.rolobykes.dataaccess.UsuarioRepository;
import com.rolobykes.domain.Bicicleta;
import com.rolobykes.domain.Prestamo;
import com.rolobykes.domain.TipoBicicleta;
import com.rolobykes.domain.Usuario;
import com.rolobykes.logic.CasosDeUsoPrestamo;
import com.rolobykes.logic.CasosDeUsoUsuario;

@SpringBootTest
public class CasosdeUsoPrestamoTests {
    // SUT -- Subject under test
    @Autowired
    CasosDeUsoPrestamo casosdeUsoPrestamo;

    // Fixing -- Clases adicionales necesarias para la prueba
    @Autowired
    PrestamoRepository prestamos;

    @Autowired
    ReservaRepository reservas;

    @Autowired
    UsuarioRepository usuarios;

    @Autowired
    BicicletaRepository bicicletas;

    @Autowired
    TipoBicicletaRepository tipos;

    @Autowired
    CasosDeUsoUsuario casosDeUsoUsuario;

    @BeforeAll
    public static void prepararAmbienteParaTodaLaSuite() {
        System.out.println("Antes de todas las pruebas de la clase");
        System.out.println();
    }

    @BeforeEach
    public void prepararAmbienteDePruebas() {
        System.out.println("Antes de cada pruebas");
        System.out.println();

        usuarios.deleteAll();
        bicicletas.deleteAll();
        reservas.deleteAll();
        tipos.deleteAll();
        prestamos.deleteAll();

        Usuario u = new Usuario();
        u.setCorreo("jaime");
        u.setNombre("jaime");
        u.setPassword("jaime");
        u.setSessionId("hola");
        usuarios.save(u);

    }

    // CU1: PrestarBicicleta
    // ----------------------
    @Test
    @Transactional
    public void realizarPrestamoSinErrores() {
        try {
            List<Usuario> usuariosExistentes = usuarios.findByCorreo("jaime");
            Usuario u = usuariosExistentes.get(0);

            TipoBicicleta tipo = new TipoBicicleta("montaña");
            tipo = tipos.save(tipo);

            Bicicleta bicicleta = new Bicicleta("MyBici");
            bicicleta.setDisponible(true);

            bicicleta.setTipo(tipo);
            tipo.getBicicletas().add(bicicleta);

            bicicleta = bicicletas.save(bicicleta);
            tipo = tipos.save(tipo);
            casosdeUsoPrestamo.prestarBicicleta(u,tipo);
            List<Prestamo> prestamosIniciados = prestamos.findByBicicleta(bicicleta);
            if (prestamosIniciados.size() == 0) {
                // mal !!
                fail("No se grabó el prestamo");
            }
            
        } catch (Exception e) {
            fail("No dejo guardar el prestamo",e);
        }

    }
}
