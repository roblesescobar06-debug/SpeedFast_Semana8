-- =====================================================
-- SpeedFast - Semana 8
-- Script de creacion de la base de datos speedfast_db
-- =====================================================

DROP DATABASE IF EXISTS speedfast_db;
CREATE DATABASE speedfast_db;
USE speedfast_db;

CREATE TABLE repartidores (
                              id INT AUTO_INCREMENT PRIMARY KEY,
                              nombre VARCHAR(100) NOT NULL
);

CREATE TABLE pedidos (
                         id INT AUTO_INCREMENT PRIMARY KEY,
                         direccion VARCHAR(100) NOT NULL,
                         tipo ENUM('COMIDA','ENCOMIENDA','EXPRESS'),
                         estado ENUM('PENDIENTE','EN_REPARTO','ENTREGADO')
);

CREATE TABLE entregas (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          id_pedido INT,
                          id_repartidor INT,
                          fecha DATE,
                          hora TIME,
                          FOREIGN KEY (id_pedido) REFERENCES pedidos(id),
                          FOREIGN KEY (id_repartidor) REFERENCES repartidores(id)
);

-- Datos de prueba
INSERT INTO repartidores (nombre) VALUES
                                      ('Juan Perez'), ('Camila Soto'), ('Pedro Rojas');

INSERT INTO pedidos (direccion, tipo, estado) VALUES
                                                  ('Av. Concha y Toro 1234', 'COMIDA', 'PENDIENTE'),
                                                  ('Los Aromos 6559', 'ENCOMIENDA', 'EN_REPARTO'),
                                                  ('Los Bautistas 7585', 'EXPRESS', 'ENTREGADO');

INSERT INTO entregas (id_pedido, id_repartidor, fecha, hora) VALUES
    (3, 1, '2026-10-05', '15:40:00');