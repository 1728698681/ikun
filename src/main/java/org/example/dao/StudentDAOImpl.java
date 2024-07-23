package org.example.dao;

import org.example.pojo.Student;
import org.example.test1.DBUtil;
import com.mysql.jdbc.Connection;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**阿巴阿巴
 * @author JWQ
 * @date 2024/7/22 下午3:57
 */
public class StudentDAOImpl implements StudentDAO {
    private static SimpleDateFormat sdf =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @Override
    public void insert(Student student) {
        try(
                Connection c = DBUtil.getConnection();
                Statement st = c.createStatement();
                //1.传参不方便
                //2.先传参后编译 性能较差
                //3.存在sql注入攻击问题

                //preparedStatement
                //先编译 后传参
                //传参方便
                //能够有效防止sql注入攻击的问题
            ){
            String sql = "insert into student (name,gender,birthday,addr,qqnumber)"
                    +"values('%s','%s','%s','%s','%d')";
            sql = String.format(sql,student.getName(),student.getGender(),
                    sdf.format(student.getBirthday()),
                    student.getAddr(),student.getQqnumber());
            st.execute(sql);
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Integer id) {
        Connection c = null;
        String sql = "delete from student where id = ?";
        try{
            c = DBUtil.getConnection();
            //关闭事务的自动提交
            c.setAutoCommit(false);
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
            ps.executeUpdate();
            //使用commit提交
            c.commit();
        }catch(SQLException e){
            try{
                //rollback进行事务回滚
                c.rollback();
            }catch(SQLException ex){
                e.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    @Override
    public void update(Student student) {
        String sql = "update student set name = ?,gender = ?,birthday = ?,addr = ?,qqnumber = ? where id = ?";
        try(
                Connection c = DBUtil.getConnection();
                PreparedStatement ps = c.prepareStatement(sql);
                ){
            //给sql语句传参
            ps.setString(1,student.getName());
            ps.setString(2,student.getGender());
            ps.setDate(3,new Date(student.getBirthday().getTime()));
            ps.setString(4,student.getAddr());
            ps.setLong(5,student.getQqnumber());
            ps.setInt(6,student.getId());
            ps.execute();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public Integer count() {
        String sql = "select count(*) from student";
        try(
                Connection c = DBUtil.getConnection();
                PreparedStatement ps = c.prepareStatement(sql);
                ){
            //结果集
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                //每次从结果集读取一行数据 根据读取字段不同 使用不同的get方法
                //方法参数有两种 一种是获取的字段在查询结果中出现的顺序 可以不写字段出现顺序 而写字段的内容
                return rs.getInt(1);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public Student findById(Integer id) {
        String sql = "select * from student where id = ?";
        Student student = null;
        try(
                Connection c = DBUtil.getConnection();
                PreparedStatement ps = c.prepareStatement(sql);
                ){
            ps.setInt(1,id);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                student = new Student();
                student.setId(id);
                //columnLabel 列标签 通过get方法得到列标签的元素 通过set方法赋值给映射的学生对象
                student.setName(rs.getString("name"));
                student.setGender(rs.getString("gender"));
                student.setBirthday(rs.getDate("birthday"));
                student.setAddr(rs.getString("addr"));
                student.setQqnumber(rs.getLong("qqnumber"));
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return student;
    }

    @Override
    public List<Student> findAll() {
        return findWithLimit(0,Integer.MAX_VALUE);
    }

    @Override
    public List<Student> findByNameLike(String name) {
        return findByNameLikeWithLimit(name,0,Integer.MAX_VALUE);
    }

    @Override
    public List<Student> findByNameLikeWithLimit(String name, Integer start, Integer limit) {
        String sql = "select * from student where name like concat('%',?,'%') limit ?,?";
        List<Student> stus = new ArrayList<>();
        try(
                Connection c =DBUtil.getConnection();
                PreparedStatement ps = c.prepareStatement(sql);
                ){
            ps.setString(1,"%"+name+"%");
            ps.setInt(2,start);
            ps.setInt(3,limit);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Student s = new Student();
                s.setId(rs.getInt("id"));
                s.setName(rs.getString("name"));
                s.setGender(rs.getString("gender"));
                s.setBirthday(rs.getDate("birthday"));
                s.setAddr(rs.getString("addr"));
                s.setQqnumber(rs.getLong("qqnumber"));
                stus.add(s);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return stus.size()==0?null:stus;
    }

    @Override
    public List<Student> findWithLimit(Integer start, Integer limit) {
        String sql = "select * from student limit ?,?";
        List<Student> stus = new ArrayList<>();
        try(
                Connection c = DBUtil.getConnection();
                PreparedStatement ps = c.prepareStatement(sql);
                ){
            ps.setInt(1,start);
            ps.setInt(2,limit);
            //执行并获得结果集
            ResultSet rs = ps.executeQuery();
            //遍历结果集
            while(rs.next()){
                Student s = new Student();
                s.setId(rs.getInt("id"));
                s.setName(rs.getString("name"));
                s.setGender(rs.getString("gender"));
                s.setBirthday(rs.getDate("birthday"));
                s.setAddr(rs.getString("addr"));
                s.setQqnumber(rs.getLong("qqnumber"));
                stus.add(s);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return stus;
    }
}
