package org.apache.airavata.grichem.migration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by abhandar on 9/14/16.
 */
public class GridChemAdapter {

    private String url = "jdbc:mysql://localhost:3306/gridChem";
    private String username = "";
    private String password = "";

    public Connection getConnection() {
        System.out.println ("Connecting to gridChem database...");

        try {
            Connection connection = DriverManager.getConnection (url, username, password);
            System.out.println ("Database connected!");
            return connection;
        } catch (SQLException e) {
            throw new IllegalStateException ("Cannot connect the database!", e);
        }
    }

    public ArrayList<LoginUserProfiles> getUserProfiles(Connection dbConn) {
        ArrayList<LoginUserProfiles> profileList = new ArrayList<LoginUserProfiles> ();
        try {
            Statement stmt = dbConn.createStatement ();
            ResultSet rs = stmt.executeQuery ("SELECT firstName, lastName, middleInitial, userName, email FROM Users");
            while (rs.next ()) {
                LoginUserProfiles userProfiles = new LoginUserProfiles ();
                userProfiles.setFirstName (rs.getString ("firstName"));
                userProfiles.setLastName (rs.getString ("lastName"));
                userProfiles.setMiddleInitials (rs.getString ("middleInitial"));
                userProfiles.setUserName (rs.getString ("userName"));
                userProfiles.setEmail (rs.getString ("email"));
                profileList.add (userProfiles);
            }
            rs.close ();
            stmt.close ();
        } catch (SQLException e) {
            e.printStackTrace ();
        }
        return profileList;
    }

}
