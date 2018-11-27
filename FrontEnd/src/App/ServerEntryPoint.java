/*
 * The MIT License
 *
 * Copyright 2018
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package App;

import FrontEnd.FrontEnd;
import Interface.Corba.IFrontEnd;
import Interface.Corba.IFrontEndHelper;
import Models.AddressBook;
import java.net.SocketException;
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
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

/**
 *
 * @author cmcarthur
 */


public class ServerEntryPoint {
        /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length < 4) {
            args = Stream.of("-ORBInitialPort", "1050", "-ORBInitialHost", "localhost").toArray(String[]::new);
        }
        try {
            // create and initialize the ORB
            ORB orb = ORB.init(args, null);

            // get reference to rootpoa & activate the POAManager
            POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootpoa.the_POAManager().activate();

            // get the root naming context
            // NameService invokes the name service
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");

            // Use NamingContextExt which is part of the Interoperable
            // Naming Service (INS) specification.
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

            // get object reference from the servant
            FrontEnd frontEnd = new FrontEnd();
            
            org.omg.CORBA.Object ref = rootpoa.servant_to_reference(frontEnd);
            IFrontEnd href = IFrontEndHelper.narrow(ref);

            // bind the Object Reference in Naming
            NameComponent path[] = ncRef.to_name(AddressBook.FRONTEND.getShortHandName());
            ncRef.rebind(path, href);
            
            System.out.println("The Front-end (CORBA) is now running on port 1050 ...");
            


            // wait for invocations from clients
            orb.run();
            
            frontEnd.shutdown();
            
        } catch (InterruptedException | SocketException | InvalidName | CannotProceed | org.omg.CosNaming.NamingContextPackage.InvalidName | NotFound | AdapterInactive | ServantNotActive | WrongPolicy e) {
            System.out.println("Failed to start the Front-End!");
            e.printStackTrace();
        }
    }
}
