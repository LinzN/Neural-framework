package de.linzn.neuralFramework;

import de.linzn.neuralFramework.neuralStructure.NeuralDatabase;
import de.linzn.neuralFramework.neuralStructure.objects.NeuralCombination;
import de.linzn.neuralFramework.neuralStructure.objects.NeuralLocation;
import de.linzn.neuralFramework.neuralStructure.objects.NeuralObject;
import de.stem.stemSystem.STEMSystemApp;

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
        sqlite_location_load();
        sqlite_combination_load();
        sqlite_object_load();
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
