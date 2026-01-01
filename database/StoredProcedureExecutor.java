package database;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

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

    // Add movie with validation (IN parameters + multiple OUT parameters)
    // Returns Map with "success" (Boolean) and "message" (String)
    public Map<String, Object> addMovieWithValidation(String title, int year, int genreId, int directorId) throws SQLException {
        CallableStatement cstmt = null;

        try {
            // Prepare CallableStatement with procedure call
            cstmt = connection.prepareCall("{call AddMovieWithValidation(?, ?, ?, ?, ?, ?)}");

            // Set IN parameters
            cstmt.setString(1, title);
            cstmt.setInt(2, year);
            cstmt.setInt(3, genreId);
            cstmt.setInt(4, directorId);

            // Register OUT parameters
            cstmt.registerOutParameter(5, Types.BOOLEAN);  // success
            cstmt.registerOutParameter(6, Types.VARCHAR);  // message

            // Execute procedure
            cstmt.execute();

            // Get OUT parameter values
            boolean success = cstmt.getBoolean(5);
            String message = cstmt.getString(6);

            // Return results in Map
            Map<String, Object> result = new HashMap<>();
            result.put("success", success);
            result.put("message", message);

            return result;

        } finally {
            // Close CallableStatement
            if (cstmt != null) {
                cstmt.close();
            }
        }
    }

    // Get average rating by genre (INOUT + multiple OUT parameters)
    // Returns Map with "genreId" (Integer), "avgRating" (Double), "movieCount" (Integer)
    public Map<String, Object> getAverageRatingByGenre(int genreId) throws SQLException {
        CallableStatement cstmt = null;

        try {
            // Prepare CallableStatement with procedure call
            cstmt = connection.prepareCall("{call GetAverageRatingByGenre(?, ?, ?)}");

            // Set INOUT parameter (acts as both IN and OUT)
            cstmt.setInt(1, genreId);
            cstmt.registerOutParameter(1, Types.INTEGER);  // INOUT genreId

            // Register OUT parameters
            cstmt.registerOutParameter(2, Types.DOUBLE);   // avgRating
            cstmt.registerOutParameter(3, Types.INTEGER);  // movieCount

            // Execute procedure
            cstmt.execute();

            // Get OUT parameter values
            int returnedGenreId = cstmt.getInt(1);
            double avgRating = cstmt.getDouble(2);
            int movieCount = cstmt.getInt(3);

            // Return results in Map
            Map<String, Object> result = new HashMap<>();
            result.put("genreId", returnedGenreId);
            result.put("avgRating", avgRating);
            result.put("movieCount", movieCount);

            return result;

        } finally {
            // Close CallableStatement
            if (cstmt != null) {
                cstmt.close();
            }
        }
    }

    // Update movie rating (IN parameters + OUT parameter)
    // Returns old rating as double
    public double updateMovieRating(int movieId, double newRating) throws SQLException {
        CallableStatement cstmt = null;

        try {
            // Prepare CallableStatement with procedure call
            cstmt = connection.prepareCall("{call UpdateMovieRating(?, ?, ?)}");

            // Set IN parameters
            cstmt.setInt(1, movieId);
            cstmt.setDouble(2, newRating);

            // Register OUT parameter
            cstmt.registerOutParameter(3, Types.DOUBLE);  // oldRating

            // Execute procedure
            cstmt.execute();

            // Get OUT parameter value
            double oldRating = cstmt.getDouble(3);

            return oldRating;

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
