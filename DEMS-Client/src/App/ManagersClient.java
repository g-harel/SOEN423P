/* 
    MIT License

    Copyright (c) 2018 Chris Mc, prince.chrismc(at)gmail(dot)com

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.
 */
package App;

import Client.RegionalClient;
import Interface.Corba.Project;
import Models.Location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.omg.CORBA.ORB;

/**
 *
 * @author cmcarthur
 */
public class ManagersClient {

    private static RegionalClient client;
    private static Location location;
    private static Scanner reader = new Scanner(System.in);

    public static void main(String[] args) {
        if (args.length < 4) {
            args = Stream.of("-ORBInitialPort", "1050", "-ORBInitialHost", "localhost").toArray(String[]::new);
        }

        ORB orb = ORB.init(args, null);

        System.out.println("Welcome to the Distributed Employee Management System (DEMS)\r\n");

        boolean loginIsValid;
        String userLogin;

        do {
            loginIsValid = true;
            System.out.print("Please enter your 'Human Resources Manager ID': ");
            userLogin = reader.nextLine();

            Pattern idPattern = Pattern.compile("(?<location>[a-zA-Z]{2})(?<number>[0-9]{4})");
            Matcher matcher = idPattern.matcher(userLogin);

            if (matcher.matches()) {
                try {
                    location = Location.fromString(userLogin.substring(0, 2));
                } catch (Exception ex) {
                    System.out.println("Invalid ID region code. Available { CA, US, UK }. Ex: 'CA1111'");
                    loginIsValid = false;
                }
            } else {
                System.out.println("Invalid ID format. Format  if region code ( two letters ) "
                        + "followed by 4 digits. Ex: 'CA1111'");
                loginIsValid = false;
            }

        } while (!loginIsValid);

        try {
            client = new RegionalClient(orb, userLogin);
        } catch (Exception ex) {
            System.out.println("Unable to establish connection! Please try again later...");
            System.exit(-1);
        }

        boolean userRequestedExit = false;
        do {
            System.out.println("\nWhat operation would you like to perform?");
            System.out.println("   1. Create manager record");
            System.out.println("   2. Create employee record");
            System.out.println("   3. Edit an existing record");
            System.out.println("   4. Transfer record to remote location");
            System.out.println("   5. Display total number of records");
            System.out.println("   6. Software Failure (Non-malicious Byzantine)");
            System.out.println("   7. Process Crash Failure");
            System.out.println("   8. Exit");
            System.out.print("Selection: ");

            int userSelection = reader.nextInt();
            reader.nextLine();

            switch (userSelection) {
                case 1:
                    createManagerRecord();
                    break;
                case 2:
                    createEmployeeRecord();
                    break;
                case 3:
                    editRecord();
                    break;
                case 4:
                    transferRecord();
                    break;
                case 5:
                    getRecordCounts();
                    break;
                case 6:
                    softwareFailure();
                    break;
                case 7:
                    replicaCrash();
                    break;
                case 8:
                    System.out.println("Good-bye!");
                    userRequestedExit = true;
                    break;
                default:
                    System.out.println("Invalid selection");
                    break;
            }

        } while (!userRequestedExit);

        reader.close();
        client.clearLog();
    }

    private static void createManagerRecord() {
        createEmployee(true);
    }

    private static void createEmployeeRecord() {
        createEmployee(false);
    }

    private static void createEmployee(boolean isManager) {
        String fName, lName, mailID;
        int empId;
        Pattern basicInfoPattern = Pattern.compile("(?<fname>[A-Za-z][A-Za-z ]*);[ ]*(?<lname>[A-Za-z][A-Za-z- ]*);[ ]*(?<empID>[0-9]+);[ ]*(?<mailID>[\\w-]+@[a-z0-9.]+)");
        Matcher matcher;

        System.out.println("Please enter the " + ((isManager) ? "manager" : "employee") + " information in the following order (separated by semicolons ';'):");
        System.out.println("First Name; Last Name; Employee ID; Mail ID");

        String recordBasicInfo = reader.nextLine();
        matcher = basicInfoPattern.matcher(recordBasicInfo);

        if (matcher.matches()) {
            fName = matcher.group("fname");
            lName = matcher.group("lname");
            empId = Integer.parseInt(matcher.group("empID"));
            mailID = matcher.group("mailID");
        } else {
            // Error
            System.out.println("The " + ((isManager) ? "manager" : "employee") + "'s information was not entered with the proper format!");
            return;
        }

        // IF the record is a Manager
        if (isManager) {
            Project project;
            Pattern projectInfoPattern = Pattern.compile("(?<projectID>P[0-9]{5});[ ]*(?<clientName>[A-Za-z][A-Za-z-. ]*);[ ]*(?<projectName>\\w[\\w- ]*)");

            System.out.println("\nPlease enter the Manager's PROJECT INFORMATION in the following order (separated by semicolons ';'):");
            System.out.println("ProjectID (format: P00001); Client's Name; Project Name");

            String projectInfo = reader.nextLine();
            matcher = projectInfoPattern.matcher(projectInfo);

            if (matcher.matches()) {
                project = new Project(matcher.group("projectID"), matcher.group("clientName"), matcher.group("projectName"));
            } else {
                // Error
                System.out.println("The project information was not entered with the proper format!");
                return;
            }

            //  Send server request
            String operationStatus = client.createManagerRecord(fName, lName, empId, mailID, project, location.getPrefix());

            System.out.println("Operation result >> " + operationStatus);
        } // It's a regular Employee
        else {
            String projectID;
            Pattern projectIDPattern = Pattern.compile("(?<projectID>P[0-9]{5})");

            System.out.println("\nPlease enter the Employee's Project ID (format: P00001):");

            projectID = reader.nextLine();
            matcher = projectIDPattern.matcher(projectID);

            if (matcher.matches()) {
                projectID = matcher.group("projectID");
            } else {
                // Error
                System.out.println("The Project ID entered does not have the proper format! (format: P00001)");
                return;
            }

            //  Send server request
            String operationStatus = client.createEmployeeRecord(fName, lName, empId, mailID, projectID);

            System.out.println("Operation result >> " + operationStatus + "\n");
        }
    }

    private static void editRecord() {
        String recordID, fieldToChange, newValue;
        Pattern recordIDPattern = Pattern.compile("(?<recordID>(?>MR|ER)[0-9]{5})");
        Matcher matcher;
        ArrayList<String> fieldsCanBeChanged = new ArrayList<>();

        System.out.println("Please enter the Record ID:");

        recordID = reader.nextLine().toUpperCase();
        matcher = recordIDPattern.matcher(recordID);

        if (matcher.matches()) {
            recordID = matcher.group("recordID");
        } // Error: invalid Record ID format
        else {
            System.out.println("The Record ID entered does not have the proper format! (ex: MR10000, ER10001)");
            return;
        }

        // Set fields allowed to be edited based on employee type: Manager or Employee
        if (recordID.substring(0, 2).equalsIgnoreCase("MR")) {
            fieldsCanBeChanged.addAll(Arrays.asList("mailID", "projectID", "clientName", "projectName", "location"));

        } else {
            fieldsCanBeChanged.addAll(Arrays.asList("mailID", "projectID"));
        }

        System.out.println("\nWhich of the following fields would you like to edit?");
        System.out.println(String.join(", ", fieldsCanBeChanged));

        fieldToChange = reader.nextLine();

        if (!fieldsCanBeChanged.contains(fieldToChange)) {
            // Error: invalid field name
            System.out.println("The field name that you wish to edit is not valid or allowed. The field names that can be modified are the following: " + String.join(", ", fieldsCanBeChanged));
            return;
        }

        System.out.print("\nNew value: ");

        newValue = reader.nextLine();

        if (newValue.equals("")) {
            // Error: Empty new value
            System.out.println("The new value cannot be empty");
            return;
        }

        if (fieldToChange.equalsIgnoreCase("location") && !Location.isValidLocation(newValue)) {
            // Error: invalid location
            System.out.println("The new location is not valid. Valid locations = " + Location.printLocations());
            return;
        }

        String operationStatus = client.editRecord(recordID, fieldToChange, newValue);

        System.out.println("Operation result >> " + operationStatus + "\n");
    }

    private static void transferRecord() {
        String recordID, centerServerLocation;
        List<String> locations = Location.getLocationsAsStrings();
        Pattern recordIDPattern = Pattern.compile("(?<recordID>(?>MR|ER)[0-9]{5})");
        Matcher matcher;

        System.out.println("Please enter the Record ID:");

        recordID = reader.nextLine().toUpperCase();
        matcher = recordIDPattern.matcher(recordID);

        if (matcher.matches()) {
            recordID = matcher.group("recordID");
        } else {
            System.out.println("The Record ID entered does not have the proper format! (ex: MR10000, ER10001)");
            return;
        }

        // Remove the Manager's location. Transferring to the same location is useless.
        locations.remove(location.toString());

        System.out.println("\nWhere would you like to transfer the record?");
        System.out.println("Options: " + String.join(", ", locations));

        centerServerLocation = reader.nextLine().toUpperCase();

        if (!locations.contains(centerServerLocation)) {
            System.out.println("The center server location entered is not valid.");
            return;
        }

        String operationStatus = client.transferRecord(recordID, centerServerLocation);

        System.out.println("Operation result >> " + operationStatus + "\n");
    }

    private static void getRecordCounts() {
        String operationStatus = client.getRecordCounts();

        System.out.println("Operation result >> " + operationStatus + "\n");
    }

    private static void softwareFailure() {
        client.softwareFailure();
    }

    private static void replicaCrash() {
        client.replicaCrash();
    }
}
