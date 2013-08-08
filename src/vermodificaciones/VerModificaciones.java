/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vermodificaciones;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author mlabrinb
 */
public class VerModificaciones {
    private static String DirBase = "";
    private static String Extensiones = "";
    private static String Desde = "";
    private static String Destino = "";
    
    /**
     * Verifica si la extension de archivo es válida
     * @param archivo Nombre del archivo
     * @return True si la extension es correcta, False en caso contrario
     */
    public static boolean IsExtensionValida(String archivo)
    {
        String[] token = Extensiones.split(";");
        for (int i = 0; i < token.length; i++)
            if (archivo.endsWith(token[i])) return true;
        return false;
    }
    
    /**
     * Obtiene la fecha de modificación del archivo
     * @param archivo Nombre del Archivo
     * @return La fecha del archivo
     */
    public static Date GetFechaArchivo (String archivo)
    {
        File fichero = new File(archivo);        
        long ms = fichero.lastModified();
        Date d = new Date(ms);
        //Calendar c = new GregorianCalendar();
        //c.setTime(d);
        return d;
    }
    
    /**
     * Crea la estructura de los directorios en el destino
     * @param directorio estructura el directorio de la forma dir1\dir2\dir3\....
     */
    public static void CrearDirectorios(String directorio) 
    {
        String[] token = directorio.split("\\\\");
        String dir = Destino;
        for (int i=1; i<token.length; i++)
        {
            dir += ("\\" + token[i]);
            File folder = new File(dir);
            if (!folder.exists())
                folder.mkdir();            
        }
    }
    
    /**
     * Copia el archivo origen al archivo destino
     * @param fileorigen Archivo Origen
     * @param filedestino Archivo Destino
     * @return True si se pudo copiar, False si no
     */
    private static boolean CopiaArchivo(String fileorigen, String filedestino) {
        File origen = new File(fileorigen);
        File destino = new File(filedestino);            
            
        try {
            InputStream in = new FileInputStream(origen);
            OutputStream out = new FileOutputStream(destino);
            
            byte[] buf = new byte[1024];
            int len;

            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            
            in.close();
            out.close();            
            return true;
            
        } catch (Exception ex) {
            System.out.println(ex.getMessage());            
            return false;
        }
    }
    
    /**
     * Recorre recursivamente todos los directorios a partir del directorio base entregado y
     * cuando encuentra archivos con fecha de modificación superior a la fecha from, este es copiado
     * al directorio destino 
     * @param directorio directorio base desde donde se debe empezar a recorrer
     * @param fechafrom  fecha de modificación desde donde se debe considerar
     */
    public static void listarDirectorio(File directorio, Date fechafrom)
    {
        File[] ficheros = directorio.listFiles();

        for (int x=0; x<ficheros.length;x++){
            if (ficheros[x].isDirectory())
                listarDirectorio(ficheros[x], fechafrom);
            
            else if (ficheros[x].isFile())
            {
                String nombre = ficheros[x].getName();
                if (IsExtensionValida(nombre)) {
                    String path = ficheros[x].getPath();
                    String apath = ficheros[x].getParent();
                    Date fecha = GetFechaArchivo(path);
                    if (fecha.after(fechafrom)) {
                        CrearDirectorios(apath.substring(DirBase.length()));
                        if (CopiaArchivo(path, Destino + apath.substring(DirBase.length()) + "\\" + nombre))
                            System.out.println(path + " --> " + Destino + apath.substring(DirBase.length()));
                    }
                }
            }
        }
    };
    
    /**
     * usage: java -jar VerModificaciones.jar {DirBase} {Extensiones Validas separadas por ;} {Fecha de Modificación en formato aaaammdd} {Directorio Destino} 
     * @param args the command line arguments 
     */        
    public static void main(String[] args) {
        // TODO code application logic here
        for (int i=0; i < args.length; i++)
            if (i == 0) DirBase = args[i];
            else if (i == 1) Extensiones = args[i];
            else if (i == 2) Desde = args[i];
            else if (i == 3) Destino = args[i];

        SimpleDateFormat formatoDelTexto = new SimpleDateFormat("yyyyMMdd");
        try {

            Date fecha = formatoDelTexto.parse(Desde);
            File root = new File(DirBase);
            listarDirectorio(root, fecha);
        

        } catch (ParseException ex) {
            System.out.println(ex.getMessage());
        }
        
    }
}
