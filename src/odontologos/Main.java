package odontologos;

import odontologos.db.H2Connection;
import odontologos.model.Odontologo;
import org.apache.log4j.Logger;

import java.sql.*;

public class Main {
    //Afuera del método main, hago mi logger
    public static Logger LOGGER = Logger.getLogger(Main.class);
    //Hago mi tabla de SQL
    public static String SQL_CREATE = "DROP TABLE IF EXISTS ODONTOLOGOS;" +
            "CREATE TABLE ODONTOLOGOS (ID INT AUTO_INCREMENT PRIMARY KEY,"+
            "NOMBRE VARCHAR(50) NOT NULL, APELLIDO VARCHAR(50) NOT NULL, " +
            "MATRICULA VARCHAR(50) NOT NULL )";

        // A insertar una fila sobre la tabla odontólogos
    public static String SQL_INSERT = "INSERT INTO ODONTOLOGOS VALUES(DEFAULT, ?,?,?)";

        //hacer un update que le cambie la matrícula.
    public static String SQL_UPDATE = "UPDATE ODONTOLOGOS SET MATRICULA = ? WHERE ID = ?";

        //hacer un select para ver la insercion correctamente
    public static String SQL_SELECT = "SELECT * FROM ODONTOLOGOS";



    public static void main(String[] args) {
        Connection connection = null;
        Odontologo odontologo = new Odontologo("Mauri","Sanchez","123");
        Odontologo odontologoDB = null;

        try{
            connection = H2Connection.getConnection();
            Statement statement = connection.createStatement();
            statement.execute(SQL_CREATE);


                // Ahora creo el statment prepared para insertar mis data y ejecuto al final
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_INSERT);
            preparedStatement.setString(1,odontologo.getNombre());
            preparedStatement.setString(2,odontologo.getApellido());
            preparedStatement.setString(3, odontologo.getMatricula());
            preparedStatement.execute();

            ResultSet resultSet = statement.executeQuery(SQL_SELECT);
            while(resultSet.next()){
                odontologoDB = new Odontologo(resultSet.getInt(1),resultSet.getString(2),resultSet.getString(3),
                        resultSet.getString(4));
            }
            odontologo.setId(odontologoDB.getId());
            LOGGER.info("odontologo desde la base de datos" + odontologoDB);

            //Para hacer el update de la matricula a un registro previo
            connection.setAutoCommit(false);
            String nuevaMatricula = "2345";

            //Preparo nuevamente mi connection, y asigno el update, los indices son de acuerdo a los datos faltantes
            //en el SQL_UPDATE, lo ejecuto y lo commiteo
            try{
                preparedStatement = connection.prepareStatement(SQL_UPDATE);
                preparedStatement.setString(1,nuevaMatricula);
                preparedStatement.setInt(2,odontologo.getId());
                preparedStatement.execute();

                connection.commit();


            } catch(Exception e){
                connection.rollback();
            }

            odontologo.setMatricula(nuevaMatricula);
            connection.setAutoCommit(true);

             //Actualizo mi resultset
            resultSet = statement.executeQuery(SQL_SELECT);
            while(resultSet.next()){
                odontologoDB = new Odontologo(resultSet.getInt(1),resultSet.getString(2),
                        resultSet.getString(3), resultSet.getString(4));
            }
            LOGGER.info("Odontologo desde la base de datos: "  + odontologoDB);
            LOGGER.info("odontologo: "+ odontologo);


        } catch(Exception e){
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        } finally{
            try{
                connection.close();
            } catch (SQLException e){
                LOGGER.error(e.getMessage());
            }
        }
    }
}
