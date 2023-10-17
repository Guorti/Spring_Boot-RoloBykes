package com.rolobykes.logic;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rolobykes.dataaccess.BicicletaRepository;
import com.rolobykes.dataaccess.PrestamoRepository;
import com.rolobykes.dataaccess.ReservaRepository;
import com.rolobykes.dataaccess.TipoBicicletaRepository;
import com.rolobykes.dataaccess.UsuarioRepository;
import com.rolobykes.domain.Bicicleta;
import com.rolobykes.domain.Prestamo;
import com.rolobykes.domain.Reserva;
import com.rolobykes.domain.TipoBicicleta;
import com.rolobykes.domain.Usuario;

@Service
public class CasosDeUsoPrestamo {

    @Autowired
    PrestamoRepository prestamos;

    @Autowired
    BicicletaRepository bicicletas;

    @Autowired
    UsuarioRepository usuarios;

    @Autowired
    TipoBicicletaRepository tiposBicicletas;

    @Autowired
    ReservaRepository reservas;

    public void prestarBicicleta(Usuario usuario,TipoBicicleta tipo) throws ExcepcionPrestamo {
        List<Usuario> usuariosExistentes = usuarios.findByCorreo(usuario.getCorreo());
        if (usuariosExistentes.isEmpty()) {
			// 2.1. Sistema muestra un mensaje "No existen usuarios con este correo"
			// 2.2. Sistema termina
			throw new ExcepcionPrestamo("No existen usuarios registradps con este correo");
		}
        Usuario usuarioEncontrado = usuariosExistentes.get(0);
		if (usuarioEncontrado.getSessionId() == null){
			throw new ExcepcionPrestamo("Este usuario no ha iniciado sesion");
		}
        List<TipoBicicleta> tiposExistentes = tiposBicicletas.findByNombre(tipo.getNombre());
		if (tiposExistentes.isEmpty()) { 
			// 2.1. Sistema muestra un mensaje "No existen usuarios con este correo"
			// 2.2. Sistema termina
			throw new ExcepcionPrestamo("No existe ese tipo de bicicleta");
		}
        List<Bicicleta> bicicletasExistentes = bicicletas.findByTipo(tipo);
        if (bicicletasExistentes.isEmpty()) {
            throw new ExcepcionPrestamo("No existen bicicletas de ese tipo");
        }
        Bicicleta bici = bicicletasExistentes.get(0);
        if(bici.isDisponible() == false){
            throw new ExcepcionPrestamo("No hay bicicletas disponibles de ese tipo");
        }
        Prestamo prestamo = new Prestamo();
        Reserva reserva = new Reserva();
        reserva = reservas.save(reserva);
        // Asignar prestamo al usuario
        prestamo.setUsuario(usuario);
        usuario.getPrestamos().add(prestamo);
        usuario = usuarios.save(usuario);
        prestamo.setUsuario(usuario);
        prestamo.setReserva(reserva);
        prestamo.setActivo(true);
        prestamo.setBicicleta(bici);
        prestamo.setUsuario(usuario);
        prestamos.save(prestamo);
    }

    public void finalizarPrestamo(Bicicleta bicicleta) throws ExcepcionPrestamo {
        List<Bicicleta> bicicletasEncontradas = bicicletas.findByCodigo(bicicleta.getCodigo());
        if (bicicletasEncontradas.isEmpty()) {
            // 2.1. Sistema muestra un mensaje "No se encontró ninguna bicicleta con ese codigo"
            // 2.2. Sistema termina
            throw new ExcepcionPrestamo("No se encontró ninguna bicicleta con ese codigo");
        }
        Bicicleta bicicletaEncontrada = bicicletasEncontradas.get(0);
    
        if (bicicletaEncontrada.isDisponible() == false) {
            throw new ExcepcionPrestamo("Esta bicicleta ya fue entregada");
        }
        List<Prestamo> prestamosEncontrados = prestamos.findByBicicleta(bicicletaEncontrada);
        if (prestamosEncontrados.isEmpty()) {
            // 2.1. Sistema muestra un mensaje "No se encontró ninguna bicicleta con ese codigo"
            // 2.2. Sistema termina
            throw new ExcepcionPrestamo("No se encontró ningun prestamo relacionado a esa bicicleta");
        }
        Prestamo prestamo = prestamosEncontrados.get(0);
        if(prestamo.isActivo() == false){
            throw new ExcepcionPrestamo("El prestamo de esa bicicleta ya ha sido finalizado");
        }
        Bicicleta biciDevuelta = prestamo.getBicicleta();
        biciDevuelta.setDisponible(false);
        biciDevuelta = bicicletas.save(biciDevuelta);
        prestamo.setBicicleta(biciDevuelta);
        prestamo.setActivo(false);
        Usuario us = prestamo.getUsuario();
        us.getPrestamos().add(prestamo);
        us = usuarios.save(us);
        prestamo.setUsuario(us);
        us = usuarios.save(us);
        prestamo = prestamos.save(prestamo);
    }
}
