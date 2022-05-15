package de.linzn.neuralFramework.neuralStructure;

import de.linzn.neuralFramework.neuralStructure.objects.NeuralCombination;
import de.linzn.neuralFramework.neuralStructure.objects.NeuralLocation;
import de.linzn.neuralFramework.neuralStructure.objects.NeuralObject;
import de.stem.stemSystem.STEMSystemApp;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class NeuralDatabaseLoader {

    private NeuralDatabase neuralDatabase;

    public NeuralDatabaseLoader(NeuralDatabase neuralDatabase) {
        this.neuralDatabase = neuralDatabase;
    }

    public void loadDatabase() {
        sqlite_table_check();
        sqlite_location_load();
        sqlite_combination_load();
        sqlite_object_load();
    }

    private void sqlite_table_check() {
        try {
            Connection connection = STEMSystemApp.getInstance().getDatabaseModule().getConnection();
            Statement outerStatement = connection.createStatement();

            String outerQuery = "";
            outerQuery += "CREATE TABLE IF NOT EXISTS 'neural_combination' ('id' INTEGER NOT NULL UNIQUE, 'description' TEXT, PRIMARY KEY('id' AUTOINCREMENT));";
            outerQuery += "CREATE TABLE IF NOT EXISTS 'neural_combination_names' ('id' INTEGER NOT NULL UNIQUE, 'combination_id' INTEGER NOT NULL, 'name' TEXT NOT NULL UNIQUE, PRIMARY KEY('id' AUTOINCREMENT));";
            outerQuery += "CREATE TABLE IF NOT EXISTS 'neural_location' ('id' INTEGER NOT NULL UNIQUE, 'description' TEXT, PRIMARY KEY('id' AUTOINCREMENT));";
            outerQuery += "CREATE TABLE IF NOT EXISTS 'neural_location_names' ('id' INTEGER NOT NULL UNIQUE, 'location_id' INTEGER NOT NULL, 'name' TEXT NOT NULL UNIQUE, PRIMARY KEY('id' AUTOINCREMENT));";
            outerQuery += "CREATE TABLE IF NOT EXISTS 'neural_object' ('id' INTEGER NOT NULL UNIQUE, 'description' TEXT, PRIMARY KEY('id' AUTOINCREMENT));";
            outerQuery += "CREATE TABLE IF NOT EXISTS 'neural_object_combination_assignment' ('id' INTEGER NOT NULL UNIQUE, 'object_id' INTEGER NOT NULL, 'combination_id'INTEGER NOT NULL, 'static_task_id' INTEGER NOT NULL, 'description' TEXT, PRIMARY KEY('id' AUTOINCREMENT));";
            outerQuery += "CREATE TABLE IF NOT EXISTS 'neural_object_location_assignment' ('id' INTEGER NOT NULL UNIQUE, 'object_id' INTEGER NOT NULL, 'location_id' INTEGER NOT NULL, 'description' TEXT, PRIMARY KEY('id' AUTOINCREMENT));";
            outerQuery += "CREATE TABLE IF NOT EXISTS 'neural_object_names' ('id' INTEGER NOT NULL UNIQUE, 'object_id' INTEGER NOT NULL, 'name' TEXT NOT NULL UNIQUE, PRIMARY KEY('id' AUTOINCREMENT));";
            outerStatement.executeUpdate(outerQuery);
            outerStatement.close();
            STEMSystemApp.getInstance().getDatabaseModule().releaseConnection(connection);
        } catch (SQLException e) {
            STEMSystemApp.LOGGER.ERROR(e);
        }
    }

    private void sqlite_location_load() {
        try {
            Connection connection = STEMSystemApp.getInstance().getDatabaseModule().getConnection();
            Statement outerStatement = connection.createStatement();
            String outerQuery = ("SELECT * FROM `neural_location`;");
            ResultSet outerResult = outerStatement.executeQuery(outerQuery);
            while (outerResult.next()) {
                NeuralLocation neuralLocation = new NeuralLocation(outerResult.getInt("id"));
                STEMSystemApp.LOGGER.CORE("Load location_id: " + outerResult.getInt("id"));
                Statement innerStatement = connection.createStatement();
                String innerQuery = ("SELECT * FROM `neural_location_names` where `location_id`= '" + neuralLocation.GET_LOCATION_ID() + "';");
                ResultSet innerResult = innerStatement.executeQuery(innerQuery);
                while (innerResult.next()) {
                    STEMSystemApp.LOGGER.CORE("Load name " + innerResult.getString("name") + " for location_id: " + neuralLocation.GET_LOCATION_ID());
                    neuralLocation.ADD_NAME(innerResult.getString("name"));
                }
                neuralDatabase.addLocation(neuralLocation);
                innerResult.close();
                innerStatement.close();
            }
            outerResult.close();
            outerStatement.close();
            STEMSystemApp.getInstance().getDatabaseModule().releaseConnection(connection);
        } catch (SQLException e) {
            STEMSystemApp.LOGGER.ERROR(e);
        }
    }

    private void sqlite_combination_load() {
        try {
            Connection connection = STEMSystemApp.getInstance().getDatabaseModule().getConnection();
            Statement outerStatement = connection.createStatement();
            String outerQuery = ("SELECT * FROM `neural_combination`;");
            ResultSet outerResult = outerStatement.executeQuery(outerQuery);
            while (outerResult.next()) {
                NeuralCombination neuralCombination = new NeuralCombination(outerResult.getInt("id"));
                STEMSystemApp.LOGGER.CORE("Load combination_id: " + outerResult.getInt("id"));
                Statement innerStatement = connection.createStatement();
                String innerQuery = ("SELECT * FROM `neural_combination_names` where `combination_id`= '" + neuralCombination.GET_COMBINATION_ID() + "';");
                ResultSet innerResult = innerStatement.executeQuery(innerQuery);
                while (innerResult.next()) {
                    STEMSystemApp.LOGGER.CORE("Load name " + innerResult.getString("name") + " for combination_id: " + neuralCombination.GET_COMBINATION_ID());
                    neuralCombination.ADD_NAME(innerResult.getString("name"));
                }
                neuralDatabase.addCombination(neuralCombination);
                innerResult.close();
                innerStatement.close();
            }
            outerResult.close();
            outerStatement.close();
            STEMSystemApp.getInstance().getDatabaseModule().releaseConnection(connection);
        } catch (SQLException e) {
            STEMSystemApp.LOGGER.ERROR(e);
        }
    }

    private void sqlite_object_load() {
        try {
            Connection connection = STEMSystemApp.getInstance().getDatabaseModule().getConnection();
            Statement outerStatement = connection.createStatement();
            String outerQuery = ("SELECT * FROM `neural_object`;");
            ResultSet outerResult = outerStatement.executeQuery(outerQuery);
            while (outerResult.next()) {
                NeuralObject neuralObject = new NeuralObject(outerResult.getInt("id"));
                STEMSystemApp.LOGGER.CORE("Load object_id: " + outerResult.getInt("id"));
                Statement innerStatement = connection.createStatement();
                String innerQuery = ("SELECT * FROM `neural_object_names` where `object_id`= '" + neuralObject.GET_OBJECT_ID() + "';");
                ResultSet innerResult = innerStatement.executeQuery(innerQuery);
                while (innerResult.next()) {
                    STEMSystemApp.LOGGER.CORE("Load name " + innerResult.getString("name") + " for object_id: " + neuralObject.GET_OBJECT_ID());
                    neuralObject.ADD_NAME(innerResult.getString("name"));
                }

                innerQuery = ("SELECT * FROM `neural_object_location_assignment` where `object_id`= '" + neuralObject.GET_OBJECT_ID() + "';");
                innerResult = innerStatement.executeQuery(innerQuery);

                while (innerResult.next()) {
                    STEMSystemApp.LOGGER.CORE("Register location_id: " + innerResult.getInt("location_id") + " for object_id: " + neuralObject.GET_OBJECT_ID());
                    neuralObject.registerLocation(neuralDatabase.getLocation(innerResult.getInt("location_id")));
                }

                innerQuery = ("SELECT * FROM `neural_object_combination_assignment` where `object_id`= '" + neuralObject.GET_OBJECT_ID() + "';");
                innerResult = innerStatement.executeQuery(innerQuery);

                while (innerResult.next()) {
                    STEMSystemApp.LOGGER.CORE("Register combination_id: " + innerResult.getInt("combination_id") + " static_task_id: " + innerResult.getInt("static_task_id") + " for object_id: " + neuralObject.GET_OBJECT_ID());
                    neuralObject.registerCombination(neuralDatabase.getCombination(innerResult.getInt("combination_id")), innerResult.getInt("static_task_id"));
                }

                innerQuery = ("SELECT * FROM `neural_object_combination_location_assignment` where `object_id`= '" + neuralObject.GET_OBJECT_ID() + "';");
                innerResult = innerStatement.executeQuery(innerQuery);

                while (innerResult.next()) {
                    STEMSystemApp.LOGGER.CORE("Set location combination data combination_id: " + innerResult.getInt("combination_id") +" location_id: "+ innerResult.getInt("location_id") + " for object_id: " + neuralObject.GET_OBJECT_ID());
                    neuralObject.addCombinationLocationData(neuralDatabase.getCombination(innerResult.getInt("combination_id")), neuralDatabase.getLocation(innerResult.getInt("location_id")), new JSONObject(innerResult.getString("data")));
                }

                neuralDatabase.addObject(neuralObject);
                innerResult.close();
                innerStatement.close();
            }
            outerResult.close();
            outerStatement.close();
            STEMSystemApp.getInstance().getDatabaseModule().releaseConnection(connection);
        } catch (SQLException e) {
            STEMSystemApp.LOGGER.ERROR(e);
        }
    }

    private void sqlite_task_assignment_load() {
        try {
            Connection connection = STEMSystemApp.getInstance().getDatabaseModule().getConnection();
            Statement outerStatement = connection.createStatement();
            String outerQuery = ("SELECT * FROM `neural_task_assignment`;");
            ResultSet outerResult = outerStatement.executeQuery(outerQuery);
            while (outerResult.next()) {
                NeuralObject neuralObject = new NeuralObject(outerResult.getInt("id"));
                STEMSystemApp.LOGGER.CORE("Load object_id " + outerResult.getInt("id"));
                Statement innerStatement = connection.createStatement();
                String innerQuery = ("SELECT * FROM `neural_object_names` where `object_id`= '" + neuralObject.GET_OBJECT_ID() + "';");
                ResultSet innerResult = innerStatement.executeQuery(innerQuery);
                while (innerResult.next()) {
                    STEMSystemApp.LOGGER.CORE("Load name " + innerResult.getString("name") + " for object_id: " + neuralObject.GET_OBJECT_ID());
                    neuralObject.ADD_NAME(innerResult.getString("name"));
                }
                innerResult.close();
                innerStatement.close();
            }
            outerResult.close();
            outerStatement.close();
            STEMSystemApp.getInstance().getDatabaseModule().releaseConnection(connection);
        } catch (SQLException e) {
            STEMSystemApp.LOGGER.ERROR(e);
        }
    }
}
