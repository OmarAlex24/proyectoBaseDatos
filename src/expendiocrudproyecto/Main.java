package expendiocrudproyecto;

//package com.equipo7.expendiocrud;
//
//import com.equipo7.expendiocrud.modelo.dao.BebidaDAO;
//import com.equipo7.expendiocrud.modelo.pojo.Bebida;
//
//import java.sql.SQLException;
//import java.util.List;
//
//public class Main {
//    public static void main(String[] args) {
//        BebidaDAO bebidaDAO = new BebidaDAO();
//
//        try {
//            List<Bebida> bebidas = bebidaDAO.leerTodo();
//
//
//            System.out.println("Todas las bebidas");
//            for (Bebida bebida : bebidas) {
//                System.out.println(bebida);
//            }
//
//            System.out.println("\nBebida por ID");
//
//            System.out.println(bebidaDAO.leerPorId(1));
//
//            Bebida bebidaAgregada = new Bebida("Cerveza", 10.0f, 100, 10);
//            bebidaAgregada = bebidaDAO.insertar(bebidaAgregada);
//
//            System.out.println("\nBebida agregada");
//            bebidas = bebidaDAO.leerTodo();
//
//            for (Bebida bebida : bebidas) {
//                System.out.println(bebida);
//            }
//
//            bebidaAgregada.setNombre("Cerveza actualizada");
//            bebidaAgregada.setPrecio(12.0f);
//
//            bebidaDAO.actualizar(bebidaAgregada);
//
//            System.out.println("\nBebida actualizada");
//            bebidas = bebidaDAO.leerTodo();
//
//            for (Bebida bebida : bebidas) {
//                System.out.println(bebida);
//            }
//
//            bebidaDAO.eliminar(bebidaAgregada.getId());
//
//            bebidas = bebidaDAO.leerTodo();
//
//            System.out.println("\nBebida eliminada");
//            for (Bebida bebida : bebidas) {
//                System.out.println(bebida);
//            }
//
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }
//}
