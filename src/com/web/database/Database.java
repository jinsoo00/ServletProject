package com.web.database;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet("/database")
public class Database extends HttpServlet {
	String jdbc_driver = "com.mysql.cj.jdbc.Driver";
	String url = "jdbc:mysql://127.0.0.1:3306/databasetest?serverTimezone=UTC";
	Connection con = null;
	
	@Override
	public void init() throws ServletException {
		try {
			Class.forName(jdbc_driver).newInstance();
			con = DriverManager.getConnection(url, "root", "123123"); 

			//1) member.html �� �����͸� ������ �� �ִ� DB table ���� (����� ����)
			String create_sql = "create table databasetest.member ( id VARCHAR(30) PRIMARY KEY,pwd VARCHAR(20),name VARCHAR(20),"
					+ "tel VARCHAR(30), email VARCHAR(50),dept VARCHAR(50),gender VARCHAR(20),birth VARCHAR(30),"
					+ "introduction VARCHAR(60))";
			PreparedStatement pstmt = con.prepareStatement(create_sql);
			pstmt.executeUpdate();
			pstmt.close();
		} catch (SQLException e) {
			System.out.print("table�� �̹� �����Ǿ��ֽ��ϴ�.");
		} catch (Exception e) {
			e.printStackTrace();
		} 
		super.init();
	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/html;charset=UTF-8");
		req.setCharacterEncoding("UTF-8");
		PrintWriter out = resp.getWriter();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			String btn = req.getParameter("button");
			if(btn.equals("����")) {
				String id = req.getParameter("id");
				String pwd =  req.getParameter("pwd");
				String name = req.getParameter("name");
				String tel = req.getParameter("tel");
				String email = req.getParameter("email");
				String dept = req.getParameter("dept");
				String gender = req.getParameter("gender");
				String birth = req.getParameter("birth");
				String introduction = req.getParameter("introduction");
				
				//�Է� �����Ͱ� �̹� DB�� �ִ��� Ȯ���ϱ����� ������ ��������
				String db_id="";
				String db_name="";
				String db_pwd="";
				String select_sql = "select id,pwd,name from databasetest.member where id=? and name=?";
				
				pstmt = con.prepareStatement(select_sql);
				pstmt.setString(1, id);
				pstmt.setString(2, name);
				rs = pstmt.executeQuery();
				
				while(rs.next()){					
					db_id = rs.getString("id");
					db_pwd = rs.getString("pwd");
					db_name = rs.getString("name");
				}
				
				//3) ���� ����ڰ� �Է��� �����Ͱ� �̹� DB�� ID, �̸��� ��� ���� ������ ������ ��,
				if(db_id.equals(id) && db_name.equals(name)) {
					//3.2) ��й�ȣ�� ��ġ�ϸ� ���� �����͸� ������Ʈ�� �� ���� ������Ʈ �� ������ ��°� �Բ� "������Ʈ ���� �޽���" ȭ�鿡 ��� (��й�ȣ ����)
					if(db_pwd.equals(pwd)) {
						String update_sql = "update databasetest.member set tel=?,email=?,dept=?,gender=?,birth=?,introduction=?"
								+ "where id=? and pwd=? and name=?";
						pstmt = con.prepareStatement(update_sql);
						pstmt.setString(1,tel);
						pstmt.setString(2,email);
						pstmt.setString(3,dept);
						pstmt.setString(4,gender);
						pstmt.setString(5,birth);
						pstmt.setString(6,introduction);
						pstmt.setString(7,id);	
						pstmt.setString(8,pwd);
						pstmt.setString(9,name);
						pstmt.executeUpdate();
						
						out.print("<html><head><title>DB ������</title></head>");
						out.print("<body>");
						out.print("<h1>");
						out.print(" id : "+id+"<br> name : "+ name +"<br> tel : "+ tel+"<br> email : "+ email
								+"<br> dept : "+ dept+"<br> gender : "+ gender+"<br> birth : "+ birth+"<br> introduction : "+introduction
											+ "<br> �����Ͱ� ����Ǿ����ϴ�!!");
						out.print("</h1></body></html>");
					}
					//3.1) ��й�ȣ�� �ٸ��� ȭ�鿡 ��й�ȣ�� �ٸ��ٴ� �޽��� ���
					else {
						out.print("<html><head><title>DB ������</title></head>");
						out.print("<body>");
						out.print("<h1>�Է� ��й�ȣ�� DB�� ��й�ȣ�� �ٸ��ϴ�!!</h1>");
						out.print("</body></html>");
					}
				}
				/* 
				   2) ����ڰ� member.html �� �����Ϳ� ���� �Է��ؼ� ���� ��ư ������, ������ �� �����Ͱ����� �޾Ƽ�  
				      1)���� ������ DB table�� ���� ��, ���� ����� ������ ��°� �Բ� "���� ���� �޽���" ȭ�鿡 ��� (��й�ȣ ����)
				*/
				else {
					String insert_sql = "insert into databasetest.member(id,pwd,name,tel,email,dept,gender,birth,introduction)"
										+ " values(?,?,?,?,?,?,?,?,?)";
					pstmt = con.prepareStatement(insert_sql);
					pstmt.setString(1,id);	
					pstmt.setString(2,pwd);
					pstmt.setString(3,name);
					pstmt.setString(4,tel);
					pstmt.setString(5,email);
					pstmt.setString(6,dept);
					pstmt.setString(7,gender);
					pstmt.setString(8,birth);
					pstmt.setString(9,introduction);
					pstmt.executeUpdate();
					
					out.print("<html><head><title>DB ������</title></head>");
					out.print("<body>");
					out.print("<h1>");
					out.print(" id : "+id+"<br> name : "+ name +"<br> tel : "+ tel+"<br> email : "+ email
							+"<br> dept : "+ dept+"<br> gender : "+ gender+"<br> birth : "+ birth+"<br> introduction : "+introduction
										+ "<br> �����Ͱ� ��������ϴ�!!");
					out.print("</h1></body></html>");
				}
			}
			//	4) member.html�� submit ��ư�� 2�� �߰�  - ��ư value: DB ����, DB ����
			//	4.1) ����ڰ� DB ���� ��ư Ŭ�� ��, ����� ȭ�鿡 ���� �����ϴ� DB table�� ��� �����͵��� ��� (��й�ȣ ����)
			else if(btn.equals("DB����")) {
				String select_sql = "select id,name,tel,email,dept,gender,birth,introduction from databasetest.member";
				pstmt = con.prepareStatement(select_sql);
				rs = pstmt.executeQuery();
				
				out.print("<html><head><title>DB ������</title></head>");
				out.print("<body>");
				out.print("<h1>");
				while(rs.next()){					
					String id = rs.getString("id");
					String name = rs.getString("name");
					String tel = rs.getString("tel");
					String email = rs.getString("email");
					String dept = rs.getString("dept");
					String gender = rs.getString("gender");
					String birth = rs.getString("birth");
					String introduction = rs.getString("introduction");
					out.print(" id : "+id+"| name : "+ name +"| tel : "+ tel+"| email : "+ email
						+"| dept : "+ dept+"| gender : "+ gender+"| birth : "+ birth+"| introduction : "+introduction
									+ "<br>");
				}
				out.print("</h1></body></html>");
				out.close();
				rs.close();
				pstmt.close();
			}
			//	 4.2) ����ڰ� DB ���� ��ư Ŭ�� ��, ���� �����ϴ� DB table�� ��� �����͵��� ����
			else if(btn.equals("DB����")) {
				String delete_sql = "delete from databasetest.member";
				
				pstmt = con.prepareStatement(delete_sql);
				pstmt.executeUpdate();
				
				out.print("<html><head><title>DB ������</title></head>");
				out.print("<body>");
				out.print("<h1>DB�� ���� �����Ͽ����ϴ�!!</h1>");
				out.print("</body></html>");
			}
			out.close();
			rs.close();
			pstmt.close();
		}catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
}