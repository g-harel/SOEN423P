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

import Models.AddressBook;
import Interface.DEMS.RegionalRecordManipulator;
import Interface.DEMS.RegionalRecordManipulatorHelper;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;

/**
 *
 * @author cmcarthur
 */
public class FrontEnd {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Hello World!");

        if (args.length < 4) {
            args = Stream.of("-ORBInitialPort", "1050", "-ORBInitialHost", "localhost").toArray(String[]::new);
        }
        try {
            // create and initialize the ORB //// get reference to rootpoa & activate the POAManager
            ORB orb = ORB.init(args, null);
            POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootpoa.the_POAManager().activate();

            // LEt's get a reference to the NamingService of CORBA
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

            // get object reference from the servant
            org.omg.CORBA.Object refFrontend = rootpoa.servant_to_reference(Frontend);
            RegionalRecordManipulator Href = RegionalRecordManipulatorHelper.narrow(refFrontend);
            // Create path and bind Frontend reference
            NameComponent pathFrontend[] = ncRef.to_name(AddressBook.FRONTEND.getShortHandName());
            ncRef.rebind(pathFrontend, Href);

        } catch (InvalidName | AdapterInactive | org.omg.CosNaming.NamingContextPackage.InvalidName
                | NotFound | CannotProceed ex) {
            System.out.println("Failed to start CORBA sub-systems!");
        }
        // TODO code application logic here

        // TODO code application logic here
    }

}
