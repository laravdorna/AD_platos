/*
 * a partir del platos.xml dado recoger el cod del plato y buscar su codigo de composicion
 en el composicion.txt y la grasa en la tabla componentes
 */
/* COMANDOS
lanzar servidor oracle para trabajar desde java con el listener

. oraenv
 orcl
rlwrap sqlplus sys/oracle as sysdba 
startup
conn hr/hr
exit
lsnrctl start
lsnrctl status

*/


package exap2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 *
 * @author oracle
 */
public class ExaP2 {

    public static Connection conexion = null;

    public static Connection getConexion() throws SQLException {
        String usuario = "hr";
        String password = "hr";
        String host = "localhost";
        String puerto = "1521";
        String sid = "orcl";
        String ulrjdbc = "jdbc:oracle:thin:" + usuario + "/" + password + "@" + host + ":" + puerto + ":" + sid;

        conexion = DriverManager.getConnection(ulrjdbc);
        return conexion;
    }

    public static void closeConexion() throws SQLException {
        conexion.close();
    }

    public static void main(String[] args) throws SQLException, FileNotFoundException, XMLStreamException, IOException, ClassNotFoundException {
        //primero conectarse a la BD 
        getConexion();

        //crear lector del xml
        String Rutaf = "/home/oracle/Desktop/compartido/ExamenPrueba2/platos.xml";
        XMLInputFactory xmlIF = XMLInputFactory.newInstance();
        XMLStreamReader leerXML = xmlIF.createXMLStreamReader(new FileReader(new File(Rutaf)));

        //texto delimitado
        //no puede ir aqui porque no vuleve a leer el txt SOLO LO LEE UNA VEZ!
        String RutaS = "/home/oracle/Desktop/compartido/ExamenPrueba2/composicion.txt";
       // BufferedReader leerB = new BufferedReader(new FileReader(RutaS));

        //SERIALIZADO
        String RutaSerializado = "/home/oracle/Desktop/compartido/ExamenPrueba2/serializado.txt";
        ObjectOutputStream escribirO = new ObjectOutputStream(new FileOutputStream(RutaSerializado));
        ObjectInputStream leerO = new ObjectInputStream(new FileInputStream(RutaSerializado));

        //VARIABLES
        String codp = null;
        String nomep = null;
        String linea;
        String comp;
        int peso = 0;
        int grasatotal = 0;

        while (leerXML.hasNext()) {
            //crear objeto
            Platos p = new Platos();
            int tipoE = 0;
            tipoE = leerXML.getEventType();
            if (tipoE == XMLStreamConstants.START_ELEMENT) {
                String localName = leerXML.getLocalName();
                if (localName.equals("Plato")) {
                    codp = leerXML.getAttributeValue(0);
                    System.out.println(codp);

                } else if (localName.equals("nomep")) {
                    nomep = leerXML.getElementText();
                    System.out.println(nomep);
                }
            }

            //leer delimitado si hay algo en codp y nomep
            if ((codp != null) && (nomep != null)) {
                BufferedReader leerB = new BufferedReader(new FileReader(RutaS));

                //lee el texto  y lo guarda en un array linea a linea
                while ((linea = leerB.readLine()) != null) {
                    String[] leer = linea.split("#");//cuuidado de cambiar el delimitador
                    // System.out.println(leer[0] + leer[1] + leer[2]);
                    //compara si codp está en el texto delimitado
                    if (leer[0].equals(codp)) {
                        comp = leer[1];
                        peso = Integer.parseInt(leer[2]);
                        System.out.println("\tcomp= " + comp + "\tpeso= " + peso);

                        //leer tabla de db 
                        Statement stm = conexion.createStatement();
                        ResultSet rs = stm.executeQuery("select * from componentes where CODC = '" + comp + "' ");
                        rs.next();
                        int pgrasa = rs.getInt(3);
                        System.out.println("\tporcetaje grasa del componente=" + pgrasa);
                        int grasatotalcomp = peso * pgrasa / 100;
                        grasatotal += grasatotalcomp;

                    }

                }
                System.out.println("grasa total plato =\t" + grasatotal);

                //GUARDAR EN UN OBJETO PLATO TODOS LOS COMPONENTES
                //tiene que ser justo despues de acceder a todos los datos antes de poner a null si no crea otro objeto con nulls!!
                p.setCodigop(codp);
                p.setNomep(nomep);
                p.setGrasa(grasatotal);
                //System.out.println("\nresultado guardado en el objeto plato");
                // System.out.println(p.toString());

                //guardar en serializado 
                escribirO.writeObject(p);

                //PONER A NULL LOS COMPONENTES para que recoja el segundo
                codp = null;
                nomep = null;
                leerB.close();
            }

            leerXML.next();
        }

        //escribir un objeto null para poder saber cuando acabo
        escribirO.writeObject(null);
        escribirO.close();
        //leerB.close();
        leerXML.close();

        Object contenido = 0;
        System.out.println("\nLEER SERIALIZADO:  ");
        while ( (contenido = leerO.readObject()) != null) {
            
                //contenido = leerO.readObject();
                System.out.println(contenido);
            
        }

        //ultimo cerrar la bd
        closeConexion();
    }
}
