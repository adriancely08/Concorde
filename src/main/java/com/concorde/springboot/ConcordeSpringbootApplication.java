package com.concorde.springboot;

import com.concorde.springboot.repositorio.VehiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de toda la aplicación. Al arrancar, Spring Boot
 * escanea el paquete com.concorde.springboot y registra automáticamente
 * los controladores, repositorios y servicios (@RestController,
 * @Repository, @Service, etc.). El CommandLineRunner de abajo es solo
 * una comprobación rápida: imprime en la consola cuántos vehículos hay
 * en la base para confirmar que la conexión a PostgreSQL sí funcionó.
 */
@SpringBootApplication
public class ConcordeSpringbootApplication implements CommandLineRunner {
    
    @Autowired
    private VehiculoRepository vehiculoRepository;

    public static void main(String[] args) {
        SpringApplication.run(ConcordeSpringbootApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Vehiculos en la base de datos: " + vehiculoRepository.count());
    }
}
