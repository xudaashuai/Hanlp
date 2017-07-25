package com.company;

import java.sql.*;

/**
 * Created by Sakura on 2017/6/6.
 */
public class Databaseoperator {
    public static Connection connection = null;
    public static Statement statement = null;
    private static  int inId;
    static {
        getConnection();
        inId=0;
    }
    public static void DoNoResultSql(String sql){
        try{
            statement.execute(sql);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static ResultSet DoGetResultSql(String sql)
    {
        ResultSet rs = null;
        try {
                 rs=statement.executeQuery(sql);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        return rs;
    }
    //连接
    public  static void getConnection(){
        try {
            String url = "jdbc:postgresql://39.108.90.218:5432/postgres";
            //换成自己PostgreSQL数据库实例所在的ip地址，并设置自己的端口
            String user = "postgres";
            String password = "123456";
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(url, user, password);
            System.out.println(connection==null?"连接数据库失败啊":"连接数据库成功");
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    //关闭连接
    public static void close(Connection connection, Statement statement) {

        try {
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
