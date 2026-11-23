
package colegio.services;

import colegio.conexion.Conexion;
import colegio.dao.CicloEscolarDAO;
import colegio.dto.PeticionCiclo;
import colegio.exception.ConflictoException;
import colegio.exception.DatosInvalidosException;
import colegio.exception.NoEncontradoException;
import colegio.model.CicloEscolar;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class CicloEscolarService {

    private final CicloEscolarDAO dao = new CicloEscolarDAO();

    public List<CicloEscolar> listar(int pagina, int tamano)
            throws SQLException {
        return dao.listar(pagina, tamano);
    }

    public int contar() throws SQLException {
        return dao.contar();
    }

    public CicloEscolar buscarPorId(int id)
            throws SQLException, NoEncontradoException {

        CicloEscolar ciclo = dao.buscarPorId(id);

        if (ciclo == null) {
            throw new NoEncontradoException(
                    "No se encontro el año lectivo"
            );
        }

        return ciclo;
    }

    public void crear(PeticionCiclo datos)
            throws SQLException, DatosInvalidosException,
            ConflictoException {

        if (datos == null) {
            throw new DatosInvalidosException(
                    "Debes enviar los datos del año lectivo"
            );
        }

        datos.validar();

        if (dao.existeAnio(datos.getAnio())) {
            throw new ConflictoException(
                    "Ya existe un año lectivo con ese año"
            );
        }

        dao.crear(
                datos.getAnio(),
                Date.valueOf(datos.getFechaInicio()),
                Date.valueOf(datos.getFechaFin())
        );
    }

    public List<Integer> listarGrados(int idCiclo)
            throws SQLException, NoEncontradoException {

        buscarPorId(idCiclo);
        return dao.listarGrados(idCiclo);
    }

    public void agregarGrado(int idCiclo, int idGrado)
            throws SQLException, DatosInvalidosException,
            NoEncontradoException, ConflictoException {

        if (idCiclo <= 0 || idGrado <= 0) {
            throw new DatosInvalidosException(
                    "Los identificadores no son validos"
            );
        }

        try (Connection con = Conexion.obtenerConexion()) {
            con.setAutoCommit(false);

            try {
                String estado = dao.buscarEstado(con, idCiclo);

                if (estado == null) {
                    throw new NoEncontradoException(
                            "No se encontro el año lectivo"
                    );
                }

                if (!estado.equals("PLANIFICADO")) {
                    throw new ConflictoException(
                            "Solo puedes agregar grados a un año planificado"
                    );
                }

                if (!dao.gradoActivo(con, idGrado)) {
                    throw new DatosInvalidosException(
                            "El grado no existe o esta inactivo"
                    );
                }

                if (dao.gradoAsignado(con, idCiclo, idGrado)) {
                    throw new ConflictoException(
                            "Este grado ya esta asignado al año lectivo"
                    );
                }

                dao.agregarGrado(con, idCiclo, idGrado);
                con.commit();

            } catch (SQLException | DatosInvalidosException |
                     NoEncontradoException | ConflictoException e) {
                con.rollback();
                throw e;
            }
        }
    }

    public void quitarGrado(int idCiclo, int idGrado)
            throws SQLException, DatosInvalidosException,
            NoEncontradoException, ConflictoException {

        if (idCiclo <= 0 || idGrado <= 0) {
            throw new DatosInvalidosException(
                    "Los identificadores no son validos"
            );
        }

        try (Connection con = Conexion.obtenerConexion()) {
            con.setAutoCommit(false);

            try {
                String estado = dao.buscarEstado(con, idCiclo);

                if (estado == null) {
                    throw new NoEncontradoException(
                            "No se encontro el año lectivo"
                    );
                }

                if (!estado.equals("PLANIFICADO")) {
                    throw new ConflictoException(
                            "Solo puedes quitar grados de un año planificado"
                    );
                }

                boolean quitado = dao.quitarGrado(
                        con, idCiclo, idGrado
                );

                if (!quitado) {
                    throw new NoEncontradoException(
                            "El grado no esta asignado a este año"
                    );
                }

                con.commit();

            } catch (SQLException | NoEncontradoException |
                     ConflictoException e) {
                con.rollback();
                throw e;
            }
        }
    }

    public void cambiarEstado(int id, String estado)
            throws SQLException, NoEncontradoException,
            DatosInvalidosException, ConflictoException {

        if (estado == null ||
                (!estado.equals("ACTIVO") &&
                        !estado.equals("CERRADO"))) {
            throw new DatosInvalidosException(
                    "El estado solicitado no es valido"
            );
        }

        CicloEscolar ciclo = buscarPorId(id);

        if (ciclo.getEstado().equals("CERRADO")) {
            throw new ConflictoException(
                    "No se puede modificar un año lectivo cerrado"
            );
        }

        if (ciclo.getEstado().equals(estado)) {
            throw new ConflictoException(
                    "El año lectivo ya tiene ese estado"
            );
        }

        if (estado.equals("CERRADO") &&
                !ciclo.getEstado().equals("ACTIVO")) {
            throw new ConflictoException(
                    "Solo puedes cerrar un año lectivo activo"
            );
        }

        if (estado.equals("ACTIVO")) {
            String anioActivo = dao.buscarCicloActivo();

            if (anioActivo != null) {
                throw new ConflictoException(
                        "Ya existe un año lectivo activo: " + anioActivo
                );
            }

            if (dao.contarGrados(id) == 0) {
                throw new ConflictoException(
                        "El año lectivo no tiene grados habilitados"
                );
            }
        }

        dao.cambiarEstado(id, estado);
    }
}
