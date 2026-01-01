package database;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

// Handles stored procedure execution using CallableStatement
public class StoredProcedureExecutor {

    private Connection connection;

    public StoredProcedureExecutor() {
        // Get connection from singleton
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    // Get movies by genre (IN parameter, returns ResultSet)
    // Note: Caller must close the returned ResultSet
    public ResultSet getMoviesByGenre(String genreName) throws SQLException {
        CallableStatement cstmt = null;

        try {
            // Prepare CallableStatement with procedure call
            cstmt = connection.prepareCall("{call GetMoviesByGenre(?)}");

            // Set IN parameter
            cstmt.setString(1, genreName);

            // Execute and get ResultSet
            ResultSet rs = cstmt.executeQuery();

            return rs;

        } catch (SQLException e) {
            // Close CallableStatement on error
            if (cstmt != null) cstmt.close();
            throw e;
        }
    }

    // Count movies by director (IN + OUT parameters)
    public int countMoviesByDirector(String directorLastName) throws SQLException {
        CallableStatement cstmt = null;

        try {
            // Prepare CallableStatement with procedure call
            cstmt = connection.prepareCall("{call CountMoviesByDirector(?, ?)}");

            // Set IN parameter
            cstmt.setString(1, directorLastName);

            // Register OUT parameter
            cstmt.registerOutParameter(2, Types.INTEGER);

            // Execute procedure
            cstmt.execute();

            // Get OUT parameter value
            int movieCount = cstmt.getInt(2);

            return movieCount;

        } finally {
            // Close CallableStatement
            if (cstmt != null) {
                cstmt.close();
            }
        }
    }

    // Generic method to execute any stored procedure that returns a ResultSet
    // Note: Caller must close the returned ResultSet
    public ResultSet executeProcedureWithResultSet(String procedureCall, Object... parameters) throws SQLException {
        CallableStatement cstmt = null;

        try {
            // Prepare CallableStatement
            cstmt = connection.prepareCall(procedureCall);

            // Set parameters
            for (int i = 0; i < parameters.length; i++) {
                cstmt.setObject(i + 1, parameters[i]);
            }

            // Execute and get ResultSet
            return cstmt.executeQuery();

        } catch (SQLException e) {
            // Close CallableStatement on error
            if (cstmt != null) cstmt.close();
            throw e;
        }
    }

    // Generic method to execute any stored procedure (no ResultSet return)
    public void executeProcedure(String procedureCall, Object... parameters) throws SQLException {
        CallableStatement cstmt = null;

        try {
            // Prepare CallableStatement
            cstmt = connection.prepareCall(procedureCall);

            // Set parameters
            for (int i = 0; i < parameters.length; i++) {
                cstmt.setObject(i + 1, parameters[i]);
            }

            // Execute procedure
            cstmt.execute();

        } finally {
            // Close CallableStatement
            if (cstmt != null) {
                cstmt.close();
            }
        }
    }

    // Generic method to prepare CallableStatement for custom handling
    public CallableStatement prepareCall(String procedureCall) throws SQLException {
        return connection.prepareCall(procedureCall);
    }

    // Close CallableStatement
    public void closeCallableStatement(CallableStatement cstmt) throws SQLException {
        if (cstmt != null) {
            cstmt.close();
        }
    }
}
