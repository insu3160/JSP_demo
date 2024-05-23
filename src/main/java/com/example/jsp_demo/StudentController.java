package com.example.jsp_demo;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.beanutils.BeanUtils;

@WebServlet("/studentControl")
public class StudentController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    StudentDAO dao;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        dao = new StudentDAO();
    }

    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        request.setCharacterEncoding("UTF-8"); // 인코딩 설정
        String action = request.getParameter("action");
        String view = "";
        if(request.getParameter("action") == null){
            getServletContext().getRequestDispatcher("/studentControl?action=list").forward(request, response);
        } else{
            switch (action){
                case "list": view = list(request,response); break;
                case "insert": view = insert(request,response); break;
            }
            getServletContext().getRequestDispatcher("/ch09/"+view).forward(request,response);
        }
    }
    public String list(HttpServletRequest request, HttpServletResponse response) {
        request.setAttribute("students",dao.getAll());
        return "studentInfo.jsp";
    }
    public String insert(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        request.setCharacterEncoding("UTF-8"); // 인코딩 설정
        Student s = new Student();
        try {
            BeanUtils.populate(s, request.getParameterMap());
        }catch(Exception e){
            e.printStackTrace();
        }
        try {
            dao.insert(s);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return list(request,response);
    }

}