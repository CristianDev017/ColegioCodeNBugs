
package service.reportes;

import config.DBConnectionSingleton;
import dao.finanzas.PagoDao;
import exception.DatosInvalidosException;
import exception.NoEncontradoException;
import model.reportes.FilaSolvencia;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SaldoPendienteService {

    private final PagoDao pagoDao = new PagoDao();
    private final SolventesService solventesService = new SolventesService();

    public BigDecimal calcular(int idEstudiante)
            throws SQLException, DatosInvalidosException, NoEncontradoException {

        String[] estudiante = pagoDao.datosEstudiante(idEstudiante);

        if (estudiante == null) {
            throw new NoEncontradoException("El estudiante no existe");
        }

        BigDecimal saldo = BigDecimal.ZERO;

        for (Integer idCiclo : ciclos(idEstudiante)) {

            List<FilaSolvencia> filas = solventesService.filas(
                    idCiclo,
                    0,
                    0,
                    "",
                    null,
                    null
            );

            for (FilaSolvencia fila : filas) {
                if (estudiante[0].equals(fila.getCarnet())) {
                    saldo = saldo.add(fila.getMontoAdeudado());
                    break;
                }
            }
        }

        return saldo;
    }

    private List<Integer> ciclos(int idEstudiante) throws SQLException {

        List<Integer> lista = new ArrayList<>();

        String sql =
                "SELECT DISTINCT id_ciclo " +
                        "FROM inscripcion " +
                        "WHERE id_estudiante = ? " +
                        "ORDER BY id_ciclo";

        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idEstudiante);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(rs.getInt("id_ciclo"));
                }
            }
        }

        return lista;
    }
}
