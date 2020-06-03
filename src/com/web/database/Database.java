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

			//1) member.html 폼 데이터를 수신할 수 있는 DB table 생성 (방법은 무관)
			String create_sql = "create table databasetest.member ( id VARCHAR(30) PRIMARY KEY,pwd VARCHAR(20),name VARCHAR(20),"
					+ "tel VARCHAR(30), email VARCHAR(50),dept VARCHAR(50),gender VARCHAR(20),birth VARCHAR(30),"
					+ "introduction VARCHAR(60))";
			PreparedStatement pstmt = con.prepareStatement(create_sql);
			pstmt.executeUpdate();
			pstmt.close();
		} catch (SQLException e) {
			System.out.print("table이 이미 생성되어있습니다.");
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
			if(btn.equals("전송")) {
				String id = req.getParameter("id");
				String pwd =  req.getParameter("pwd");
				String name = req.getParameter("name");
				String tel = req.getParameter("tel");
				String email = req.getParameter("email");
				String dept = req.getParameter("dept");
				String gender = req.getParameter("gender");
				String birth = req.getParameter("birth");
				String introduction = req.getParameter("introduction");
				
				//입력 데이터가 이미 DB에 있는지 확인하기위해 데이터 가져오기
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
				
				//3) 만약 사용자가 입력한 데이터가 이미 DB에 ID, 이름이 모두 같은 데이터 존재할 때,
				if(db_id.equals(id) && db_name.equals(name)) {
					//3.2) 비밀번호가 일치하면 기존 데이터를 업데이트한 뒤 현재 업데이트 한 데이터 출력과 함께 "업데이트 성공 메시지" 화면에 출력 (비밀번호 제외)
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
						
						out.print("<html><head><title>DB 실행결과</title></head>");
						out.print("<body>");
						out.print("<h1>");
						out.print(" id : "+id+"<br> name : "+ name +"<br> tel : "+ tel+"<br> email : "+ email
								+"<br> dept : "+ dept+"<br> gender : "+ gender+"<br> birth : "+ birth+"<br> introduction : "+introduction
											+ "<br> 데이터가 변경되었습니다!!");
						out.print("</h1></body></html>");
					}
					//3.1) 비밀번호가 다르면 화면에 비밀번호가 다르다는 메시지 출력
					else {
						out.print("<html><head><title>DB 실행결과</title></head>");
						out.print("<body>");
						out.print("<h1>입력 비밀번호와 DB의 비밀번호가 다릅니다!!</h1>");
						out.print("</body></html>");
					}
				}
				/* 
				   2) 사용자가 member.html 폼 데이터에 값을 입력해서 전송 버튼 누르면, 서블릿이 폼 데이터값들을 받아서  
				      1)에서 생성한 DB table에 저장 후, 현재 저장된 데이터 출력과 함께 "저장 성공 메시지" 화면에 출력 (비밀번호 제외)
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
					
					out.print("<html><head><title>DB 실행결과</title></head>");
					out.print("<body>");
					out.print("<h1>");
					out.print(" id : "+id+"<br> name : "+ name +"<br> tel : "+ tel+"<br> email : "+ email
							+"<br> dept : "+ dept+"<br> gender : "+ gender+"<br> birth : "+ birth+"<br> introduction : "+introduction
										+ "<br> 데이터가 저장었습니다!!");
					out.print("</h1></body></html>");
				}
			}
			//	4) member.html에 submit 버튼을 2개 추가  - 버튼 value: DB 보기, DB 삭제
			//	4.1) 사용자가 DB 보기 버튼 클릭 시, 사용자 화면에 현재 존재하는 DB table의 모든 데이터들을 출력 (비밀번호 제외)
			else if(btn.equals("DB보기")) {
				String select_sql = "select id,name,tel,email,dept,gender,birth,introduction from databasetest.member";
				pstmt = con.prepareStatement(select_sql);
				rs = pstmt.executeQuery();
				
				out.print("<html><head><title>DB 실행결과</title></head>");
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
			//	 4.2) 사용자가 DB 삭제 버튼 클릭 시, 현재 존재하는 DB table의 모든 데이터들을 삭제
			else if(btn.equals("DB삭제")) {
				String delete_sql = "delete from databasetest.member";
				
				pstmt = con.prepareStatement(delete_sql);
				pstmt.executeUpdate();
				
				out.print("<html><head><title>DB 실행결과</title></head>");
				out.print("<body>");
				out.print("<h1>DB를 전부 삭제하였습니다!!</h1>");
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