/*
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
*/
package org.apache.airavata.grichem.migration;


import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.transport.http.HttpTransportProperties;
import org.wso2.carbon.um.ws.api.stub.RemoteUserStoreManagerServiceStub;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;

public class GridChemUserBaseMigrator {
    /**
     * Server url of the WSO2 Carbon Server
     */
    private static String SEVER_URL = "https://localhost:9443/services/";

    /**
     * User Name to access WSO2 Carbon Server
     */
    private static String USER_NAME = "admin";

    /**
     * Password of the User who access the WSO2 Carbon Server
     */
    private static String PASSWORD = "admin";


    public static void main(String args[]){

        GridChemAdapter adp = new GridChemAdapter();
        Connection dbConn  = adp.getConnection();
        ArrayList<LoginUserProfiles> profileList = adp.getUserProfiles (dbConn);

        /**
         * trust store path.  this must contains server's  certificate or Server's CA chain
         */
        String trustStore = System.getProperty("user.dir") + File.separator +
                "src" + File.separator + "main" + File.separator +
                "resources" + File.separator + "wso2carbon.jks";

        /**
         * Call to https://localhost:9443/services/   uses HTTPS protocol.
         * Therefore we to validate the server certificate or CA chain. The server certificate is looked up in the
         * trust store.
         * Following code sets what trust-store to look for and its JKs password.
         */

        System.setProperty("javax.net.ssl.trustStore",  trustStore );

        System.setProperty("javax.net.ssl.trustStorePassword", "wso2carbon");

        /**
         * Axis2 configuration context
         */
        ConfigurationContext configContext;

        try {

            /**
             * Create a configuration context. A configuration context contains information for
             * axis2 environment. This is needed to create an axis2 service client
             */
            configContext = ConfigurationContextFactory.createConfigurationContextFromFileSystem( null, null);

            /**
             * end point url with service name
             */
            String serviceEndPoint = SEVER_URL + "RemoteUserStoreManagerService";

            /**
             * create stub and service client
             */
            RemoteUserStoreManagerServiceStub adminStub = new RemoteUserStoreManagerServiceStub(configContext, serviceEndPoint);
            ServiceClient client = adminStub._getServiceClient();
            Options option = client.getOptions();

            /**
             * Setting a authenticated cookie that is received from Carbon server.
             * If you have authenticated with Carbon server earlier, you can use that cookie, if
             * it has not been expired
             */
            option.setProperty(HTTPConstants.COOKIE_STRING, null);

            /**
             * Setting basic auth headers for authentication for carbon server
             */
            HttpTransportProperties.Authenticator auth = new HttpTransportProperties.Authenticator();
            auth.setUsername(USER_NAME);
            auth.setPassword(PASSWORD);
            auth.setPreemptiveAuthentication(true);
            option.setProperty(org.apache.axis2.transport.http.HTTPConstants.AUTHENTICATE, auth);
            option.setManageSession(true);

//            for(LoginUserProfiles profile : profileList) {
//                adminStub.deleteUser (profile.getUserName ());
//            }
            /**
             * creates user
             */

            try{
                for(LoginUserProfiles profile : profileList){
                    adminStub.addUser (profile.getUserName (),"password",null,null,profile.getUserName (), false);
                    adminStub.setUserClaimValue (profile.getUserName (), "http://wso2.org/claims/givenname", profile.getFirstName (),profile.getUserName ());
                    adminStub.setUserClaimValue (profile.getUserName (), "http://wso2.org/claims/lastname", profile.getLastName (),profile.getUserName ());
                    adminStub.setUserClaimValue (profile.getUserName (),"http://wso2.org/claims/emailaddress", profile.getEmail (),profile.getEmail ());
                    System.out.println("User Profile migrated for "+ profile.getUserName ());
                }
            } catch (Exception e){
                System.err.println("User creation is failed");
                e.printStackTrace();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}