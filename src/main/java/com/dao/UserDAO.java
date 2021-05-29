package com.dao;

import com.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO implements IUserDAO {
    private String jdbcURL = "jdbc:mysql://localhost:3306/demo?useSSL=false";
    private String jdbcUsername = "root";
    private String jdbcPassword = "duong6046237";

    private static final String INSERT_USERS_SQL = "INSERT INTO users" + "  (name, email, country) VALUES " +
            " (?, ?, ?);";

    private static final String SELECT_USER_BY_ID = "select id,name,email,country from users where id =?";
    private static final String SELECT_ALL_USERS = "select * from users";
    private static final String DELETE_USERS_SQL = "delete from users where id = ?;";
    private static final String UPDATE_USERS_SQL = "update users set name = ?,email= ?, country =? where id = ?;";

    public UserDAO(){

    }

    protected Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return connection;
    }


    @Override
    public void insertUser(User user) throws SQLException {
        System.out.println(INSERT_USERS_SQL);
        // Một Connection trong java là phiên làm việc giữa ứng dụng java và cơ sở dữ liệu.
        // Đối tượng Connection được sử dụng để tạo Statement, PreparedStatement, và DatabaseMetaData.
        try(Connection connection = getConnection();
            //Phương thức prepareStatement() của giao diện Connection được sử dụng để trả về đối
            //tượng PreparedStatement.
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USERS_SQL)) {
            preparedStatement.setString(1,user.getName());
            preparedStatement.setString(2,user.getEmail());
            preparedStatement.setString(3,user.getCountry());
            System.out.println(preparedStatement);
            preparedStatement.executeUpdate();
            // executeUpdate() 	thực hiện truy vấn.
            // Nó được sử dụng để create, drop, insert, update, delete, vv.
        }catch (SQLException e){
            printSQLException(e);
        }
    }

    @Override
    public User selectUser(int id) {
        User user = null;
        //Bước 1 : Thiết lập kết nối
        try (Connection connection = getConnection();
            // Bước 2 : Sử dụng câu lệnh kết nối
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USER_BY_ID);) {
            preparedStatement.setInt(1, id);
            System.out.println(preparedStatement);

            // Bước 3: thực hiện truy vấn chọn. Nó trả về một thể hiện của ResultSet
            ResultSet rs = preparedStatement.executeQuery();


            // Bước 4: Xử lý .
            while (rs.next()) {
                String name = rs.getString("name");
                String email = rs.getString("email");
                String country = rs.getString("country");
                user = new User(id, name, email, country);
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
        return user;
    }

    @Override
    public List<User> selectAllUsers() {
        List<User> users = new ArrayList<>();
//        Câu lệnh try-with-resources  là một câu lệnh try khai báo một
//        hoặc nhiều tài nguyên. Tài nguyên là một đối tượng phải được đóng
//        sau khi hoàn thành chương trình. Câu lệnh try-with-resources đảm bảo rằng mỗi
//        tài nguyên được đóng sau khi thực thi câu lệnh.
        try (Connection connection = getConnection();

             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_USERS);) {
            System.out.println(preparedStatement);

            // Bước 3: Thực thi truy vấn hoặc cập nhật truy vấn
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                String country = resultSet.getString("country");
                User user = new User(id,name,email,country);
                users.add(user);
            }
        } catch (SQLException sqlException) {
            printSQLException(sqlException);
        }
        return users;
    }

    @Override
    public boolean deleteUser(int id) throws SQLException {
        boolean resultDelete;
        // Buoc 1: Thiet lap ket noi voi CSDL bang interface Connection
        Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(DELETE_USERS_SQL);{
        statement.setInt(1, id);
        resultDelete = statement.executeUpdate() > 0;
        }
        return resultDelete;
    }

    @Override
    public boolean updateUser(User user) throws SQLException {
        boolean rowUpdated;
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_USERS_SQL);) {
            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getCountry());
            statement.setInt(4, user.getId());

            rowUpdated = statement.executeUpdate() > 0;
        }
        return rowUpdated;
    }

    private void printSQLException(SQLException ex) {
        for (Throwable e : ex) {
            if (e instanceof SQLException) {
                e.printStackTrace(System.err);
                System.err.println("SQLState: " + ((SQLException) e).getSQLState());
                System.err.println("Error Code: " + ((SQLException) e).getErrorCode());
                System.err.println("Message: " + e.getMessage());
                Throwable t = ex.getCause();
                while (t != null) {
                    System.out.println("Cause: " + t);
                    t = t.getCause();
                }
            }
        }
    }
}
