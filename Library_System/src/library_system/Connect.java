
package library_system;

/**
 *
 * @author sanjay12345
 */
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
public class Connect {
    
    public static Connection connectdb() throws SQLException{
        Connection con=null;
            try {
                Class.forName("com.mysql.jdbc.Driver");
                con=DriverManager.getConnection("jdbc:mysql://localhost/library", "root","");
                if(con!=null){
                    System.out.println("success");
                } else {
                }
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Connect.class.getName()).log(Level.SEVERE, null, ex);
            } 
            return con;
    }
            
}
