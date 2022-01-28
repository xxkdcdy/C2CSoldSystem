package com.cdy.mobiledesign.DB;


import android.content.Intent;
import android.os.StrictMode;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.util.Base64;
import android.util.Log;

import com.cdy.mobiledesign.util.ImageUtil;

import static android.service.controls.ControlsProviderService.TAG;

public class MySQLHelper {
    public static Connection getConnection(String dbName) {
        Connection conn = null;
        try {
            //连接数据库的操作在子线程中执行
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            Class.forName("com.mysql.jdbc.Driver").newInstance(); //加载驱动
            String ip = "rm-uf6tho9jy157akqj50o.mysql.rds.aliyuncs.com";
            conn =(Connection) DriverManager.getConnection(
                    "jdbc:mysql://" + ip + ":3306/" + dbName,
                    "xxkdcdy", "5352948#Cdy");
        } catch (SQLException ex) {//错误捕捉
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return conn;//返回Connection型变量conn用于后续连接
    }

    public static void closeConn(Connection conn){
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //插入用户名和手机号（手机验证码登录）
    public static int insertUserTel(final String username, final String tel) throws SQLException {//增加数据
        Connection  conn = null;
        conn = getConnection("mobiledesign");//填写需要连接的数据库的名称
        //使用DriverManager获取数据库连接
        Statement stmt = conn.createStatement();
        //使用Connection来创建一个Statment对象
        String sql = "insert INTO user (username,tel)VALUES('"+username+"','"+tel+"')";
        return stmt.executeUpdate(sql);//返回的同时执行sql语句，返回受影响的条目数量，一般不作处理
    }

    //插入用户名和密码（账密登录）
    public static int insertUserPwd(final String username, final String password) throws SQLException {//增加数据
        Connection  conn = null;
        conn = getConnection("mobiledesign");//填写需要连接的数据库的名称
        //使用DriverManager获取数据库连接
        Statement stmt = conn.createStatement();
        //使用Connection来创建一个Statment对象
        String sql = "insert INTO user (username,password)VALUES('"+username+"','"+password+"')";
        return stmt.executeUpdate(sql);//返回的同时执行sql语句，返回受影响的条目数量，一般不作处理
    }

    //更新字符串类型数据
    public static int updateStringMessage(final String username, final String col, final String content) throws SQLException {//增加数据
        Connection  conn = null;
        conn = getConnection("mobiledesign");//填写需要连接的数据库的名称
        //使用DriverManager获取数据库连接
        Statement stmt = conn.createStatement();
        //使用Connection来创建一个Statment对象
        String sql = "update user SET "+ col + "='"+content+"' where username = '" + username + "'";
        return stmt.executeUpdate(sql);//返回的同时执行sql语句，返回受影响的条目数量，一般不作处理
    }

    //更新数字类型数据
    public static int updateNumberMessage(final String username, final String col, final String content) throws SQLException {//增加数据
        Connection  conn = null;
        conn = getConnection("mobiledesign");//填写需要连接的数据库的名称
        //使用DriverManager获取数据库连接
        Statement stmt = conn.createStatement();
        //使用Connection来创建一个Statment对象
        String sql = "update user SET " + col + "="+ content +" where username = '" + username + "'";
        return stmt.executeUpdate(sql);//返回的同时执行sql语句，返回受影响的条目数量，一般不作处理
    }

    //更新字符串类型订单数据
    public static int updateStringOrder(final String orderId, final String col, final String content) throws SQLException {//增加数据
        Connection  conn = null;
        conn = getConnection("mobiledesign");//填写需要连接的数据库的名称
        //使用DriverManager获取数据库连接
        Statement stmt = conn.createStatement();
        //使用Connection来创建一个Statment对象
        String sql = "update m_order SET "+ col + "='"+content+"' where orderId = '" + orderId + "'";
        return stmt.executeUpdate(sql);//返回的同时执行sql语句，返回受影响的条目数量，一般不作处理
    }

    //根据用户名查询是否存在账户
    public static HashMap<String, String> queryOneByUserName(final String name) {//读取某一行
        HashMap<String, String> map = new HashMap<>();
        Connection  conn = null;
        conn = getConnection("mobiledesign");//填写需要连接的数据库的名称
        try {
            Statement st = conn.createStatement();
            String sql = "select username,password,nickname,sex,tel,gold,is_validate" +
                    " from user where username = '" + name + "'";//用name定位一行数据
            ResultSet res = st.executeQuery(sql);
            if (res == null) {
                return null;
            } else {
                int cnt = res.getMetaData().getColumnCount();
                //res.last(); int rowCnt = res.getRow(); res.first();
                res.next();
                for (int i = 1; i <= cnt; ++i) {
                    String field = res.getMetaData().getColumnName(i);
                    map.put(field, res.getString(field));
                }
                conn.close();
                st.close();
                res.close();
                return map;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, " 数据操作异常");
            return null;
        }
    }

    //根据订单号查询订单的收货信息
    public static HashMap<String, String> queryOneDetialById(final String orderId) {//读取某一行
        HashMap<String, String> map = new HashMap<>();
        Connection  conn = null;
        conn = getConnection("mobiledesign");//填写需要连接的数据库的名称
        try {
            Statement st = conn.createStatement();
            String sql = "select * from packinfo where orderId = '" + orderId + "'";
            ResultSet res = st.executeQuery(sql);
            if (res == null) {
                return null;
            } else {
                int cnt = res.getMetaData().getColumnCount();
                //res.last(); int rowCnt = res.getRow(); res.first();
                res.next();
                for (int i = 1; i <= cnt; ++i) {
                    String field = res.getMetaData().getColumnName(i);
                    map.put(field, res.getString(field));
                }
                conn.close();
                st.close();
                res.close();
                return map;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, " 数据操作异常");
            return null;
        }
    }

    //查询最近一段时间内的信息(统计收入）
    public static List<HashMap<String, String>> queryFeeByTime(final String deliver, final Long d_time) {//读取某一行
        HashMap<String, String> map = new HashMap<>();
        List<HashMap<String, String>> list = new ArrayList<>();
        Connection  conn = null;
        conn = getConnection("mobiledesign");//填写需要连接的数据库的名称
        try {
            String sql = "select orderId,fee,finishTime from m_order where deliver = ? and (UNIX_TIMESTAMP()-UNIX_TIMESTAMP(`finishTime`)<?) ";
            PreparedStatement ps = null;
            ps = conn.prepareStatement(sql);
            ps.setString(1, deliver);
            ps.setLong(2, d_time);
            ResultSet res = ps.executeQuery();
            if (res == null) {
                return null;
            } else {
                int cnt = res.getMetaData().getColumnCount();
                while(res.next()){
                    map = new HashMap<>();
                    for (int i = 1; i <= cnt; ++i) {
                        String field = res.getMetaData().getColumnName(i);
                        map.put(field, res.getString(field));
                    }
                    list.add(map);
                }
                conn.close();
                ps.close();
                res.close();
                return list;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, " 数据操作异常");
            return null;
        }
    }

    //查询不是自己的订单信息,且订单状态未抢单（用于抢单）
    public static List<HashMap<String, String>> queryAllOrderByNoUserName(final String name) {//读取某一行
        HashMap<String, String> map;
        List<HashMap<String, String>> list = new ArrayList<>();
        Connection  conn = null;
        conn = getConnection("mobiledesign");//填写需要连接的数据库的名称
        try {
            Statement st = conn.createStatement();
            String sql = "select * from m_order where owner <> '" + name + "' and state = '未抢单' ORDER BY UNIX_TIMESTAMP(`expectedTime`)  DESC";//用name定位一行数据
            ResultSet res = st.executeQuery(sql);
            if (res == null) {
                return null;
            } else {
                int cnt = res.getMetaData().getColumnCount();
                while(res.next()){
                    map = new HashMap<>();
                    for (int i = 1; i <= cnt; ++i) {
                        String field = res.getMetaData().getColumnName(i);
                        map.put(field, res.getString(field));
                    }
                    list.add(map);
                }
                conn.close();
                st.close();
                res.close();
                return list;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, " 数据操作异常");
            return null;
        }
    }

    //查询自己抢到的订单信息
    public static List<HashMap<String, String>> queryAllOrderByDeliverUserName(final String name) {//读取某一行
        HashMap<String, String> map;
        List<HashMap<String, String>> list = new ArrayList<>();
        Connection  conn = null;
        conn = getConnection("mobiledesign");//填写需要连接的数据库的名称
        try {
            String sql = "select * from m_order where deliver =? ORDER BY UNIX_TIMESTAMP(`expectedTime`)  DESC";//用name定位一行数据
            PreparedStatement ps = null;
            ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ResultSet res = ps.executeQuery();
            if (res == null) {
                return null;
            } else {
                int cnt = res.getMetaData().getColumnCount();
                while(res.next()){
                    map = new HashMap<>();
                    for (int i = 1; i <= cnt; ++i) {
                        String field = res.getMetaData().getColumnName(i);
                        map.put(field, res.getString(field));
                    }
                    list.add(map);
                }
                conn.close();
                ps.close();
                res.close();
                return list;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, " 数据操作异常");
            return null;
        }
    }

    //查询自己的订单信息
    public static List<HashMap<String, String>> queryAllOrderByUserName(final String name) {//读取某一行
        HashMap<String, String> map;
        List<HashMap<String, String>> list = new ArrayList<>();
        Connection  conn = null;
        conn = getConnection("mobiledesign");//填写需要连接的数据库的名称
        try {
            Statement st = conn.createStatement();
            String sql = "select * from m_order where owner = '" + name + "' ORDER BY UNIX_TIMESTAMP(`expectedTime`)  DESC";//用name定位一行数据
            ResultSet res = st.executeQuery(sql);
            if (res == null) {
                return null;
            } else {
                int cnt = res.getMetaData().getColumnCount();
                while(res.next()){
                    map = new HashMap<>();
                    for (int i = 1; i <= cnt; ++i) {
                        String field = res.getMetaData().getColumnName(i);
                        map.put(field, res.getString(field));
                    }
                    list.add(map);
                }
                conn.close();
                st.close();
                res.close();
                return list;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, " 数据操作异常");
            return null;
        }
    }

    //根据订单号查询订单状态
    public static String queryStateById(final String orderId) {//读取某一行
        String result = null;
        Connection  conn = null;
        conn = getConnection("mobiledesign");//填写需要连接的数据库的名称
        try {
            Statement st = conn.createStatement();
            String sql = "select state from m_order where orderId = '" + orderId + "'";
            ResultSet res = st.executeQuery(sql);
            if (res == null) {
                return null;
            } else {
                int cnt = res.getMetaData().getColumnCount();
                if(res.next()){
                    for (int i = 1; i <= cnt; ++i) {
                        String field = res.getMetaData().getColumnName(i);
                        result = res.getString(field);
                    }
                }
                conn.close();
                st.close();
                res.close();
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, " 数据操作异常");
            return null;
        }
    }

    //根据订单编号查找所有的流程信息（用于显示详细信息）
    public static List<HashMap<String, String>> queryProcessById(final String id) {//读取某一行
        HashMap<String, String> map;
        List<HashMap<String, String>> list = new ArrayList<>();
        Connection  conn = null;
        conn = getConnection("mobiledesign");//填写需要连接的数据库的名称
        try {
            String sql = "select * from process where orderId =? ORDER BY UNIX_TIMESTAMP(`acceptTime`)  DESC";
            PreparedStatement ps = null;
            ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ResultSet res = ps.executeQuery();
            if (res == null) {
                return null;
            } else {
                int cnt = res.getMetaData().getColumnCount();
                while(res.next()){
                    map = new HashMap<>();
                    for (int i = 1; i <= cnt; ++i) {
                        String field = res.getMetaData().getColumnName(i);
                        map.put(field, res.getString(field));
                    }
                    list.add(map);
                }
                conn.close();
                ps.close();
                res.close();
                return list;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, " 数据操作异常");
            return null;
        }
    }

    // 将图片插入数据库
    public static void readImage2DB(String username, String path, String col) {
        Connection conn = null;
        PreparedStatement ps = null;
        FileInputStream in = null;
        try {
            in = ImageUtil.readImage(path);
            conn = getConnection("mobiledesign");
            String sql = "update user SET " + col + " = ? where username = ?";
            ps = conn.prepareStatement(sql);
            ps.setBinaryStream(1, in, in.available());
            ps.setString(2, username);
            int count = ps.executeUpdate();
            if (count > 0) {
                System.out.println("插入成功！");
            } else {
                System.out.println("插入失败！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConn(conn);
            if (null != ps) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    // 读取数据库中图片
    public static void readDB2Image(String username, String targetPath, String col) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getConnection("mobiledesign");
            String sql = "select * from user where username = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            rs = ps.executeQuery();
            while (rs.next()) {
                InputStream in = rs.getBinaryStream(col);
                ImageUtil.readBin2Image(in, targetPath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConn(conn);
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    //插入订单详情（新建订单）
    public static int insertOrder(final String owner, final String address,
                                  final String weight, final String fee,
                                  final String expectedtime,final String pack_name, final String pack_phone, final String pack_code) throws SQLException {//增加数据
        Connection  conn = null;
        conn = getConnection("mobiledesign");//填写需要连接的数据库的名称
        String orderId = owner + System.currentTimeMillis();
        //使用DriverManager获取数据库连接
        Statement stmt = conn.createStatement();
        //使用Connection来创建一个Statment对象
        String m_fee = fee.substring(0,fee.length() - 1);
        insertProcess(orderId, "订单创建");
        insertPackMessage(orderId, pack_name, pack_phone,pack_code);
        String sql = "insert INTO m_order (orderId,owner,address,weight,fee,expectedtime)VALUES('"
                +orderId+"','"+owner+"','" + address + "','" + weight + "'," + m_fee + ",'" + expectedtime + ":00" + "')";
        return stmt.executeUpdate(sql);//返回的同时执行sql语句，返回受影响的条目数量，一般不作处理
    }

    //插入订单流程（新建流程）
    public static int insertProcess(final String orderId, final String acceptStation) throws SQLException {//增加数据
        Connection  conn = null;
        conn = getConnection("mobiledesign");//填写需要连接的数据库的名称
        Date date = new Date();
        String strDateFormat = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
        String acceptTime = sdf.format(date);
        //使用DriverManager获取数据库连接
        Statement stmt = conn.createStatement();
        //使用Connection来创建一个Statment对象
        String sql = "insert INTO process(orderId,acceptTime,acceptStation) VALUES('" + orderId + "','" + acceptTime + "','" +acceptStation+ "')";
        return stmt.executeUpdate(sql);//返回的同时执行sql语句，返回受影响的条目数量，一般不作处理
    }

    //插入收货信息（新建流程）
    public static int insertPackMessage(final String orderId, final String pack_name,
                                        final String pack_phone,final String pack_code) throws SQLException {//增加数据
        Connection  conn = null;
        conn = getConnection("mobiledesign");//填写需要连接的数据库的名称
        //使用DriverManager获取数据库连接
        Statement stmt = conn.createStatement();
        //使用Connection来创建一个Statment对象
        String sql = "insert INTO packinfo(orderId,pack_name,pack_phone,pack_code) VALUES('" + orderId + "','" + pack_name + "','" +pack_phone+ "','" + pack_code +"')";
        return stmt.executeUpdate(sql);//返回的同时执行sql语句，返回受影响的条目数量，一般不作处理
    }

    //查看用户是否拥有权限
    public static boolean checkPermission(final String name) {//读取某一行
        Connection  conn = null;
        conn = getConnection("mobiledesign");//填写需要连接的数据库的名称
        try {
            Statement st = conn.createStatement();
            String sql = "select tel,gold,is_validate from user where username = '" + name + "'";//用name定位一行数据
            ResultSet res = st.executeQuery(sql);
            if (res == null) {
                return false;
            } else {
                int cnt = res.getMetaData().getColumnCount();
                if(res.next()){
                    String tel = res.getString("tel");
                    String gold = res.getString("gold");
                    String is_validate = res.getString("is_validate");
                    if(tel != null && !tel.equals("") &&
                            gold != null && !gold.equals("") &&
                            is_validate != null && !is_validate.equals("")){
                        int m_gold = Integer.parseInt(gold);
                        if(m_gold < 100 || is_validate.equals("0"))
                            return false;
                    }
                    else{
                        return false;
                    }
                }
                conn.close();
                st.close();
                res.close();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, " 数据操作异常");
            return false;
        }
    }
}
