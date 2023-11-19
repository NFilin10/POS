package ee.ut.math.tvt.salessystem.logic;

import ee.ut.math.tvt.salessystem.dao.HibernateSalesSystemDAO;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.User;

import java.lang.ref.SoftReference;

public class AuthenticationService {

    private static SalesSystemDAO dao = new HibernateSalesSystemDAO();


    public static User authenticateUser(String username, String password) {
        User user = dao.getUserByUsername(username);

        if (user != null && user.verifyPassword(password)) {
            return user;
        } else {
           return null;
        }
    }

    public static User checkIfUserExists(SalesSystemDAO dao, String username){
        return dao.getUserByUsername(username);
    }

    public static User registerUser(String name, String role, String username, String password){
        if (dao.getUserByUsername(username) == null){
            dao.addUser(new User(name, role, username, password));
            User user = authenticateUser(username, password);
            return user;
        } else{
            return null;
        }
    }




}

