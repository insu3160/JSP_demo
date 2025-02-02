package com.example.jsp_demo;

import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.beanutils.BeanUtils;

/**
 * Servlet implementation class NewsController
 */
@WebServlet(urlPatterns ="/news.nhn")
@MultipartConfig(maxFileSize=1024*1024*2, location="c:/Temp/img")

public class NewsController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    /*
     * public NewsController() { super(); // TODO Auto-generated constructor stub }
     */

    private NewsDAO dao;
    private ServletContext ctx;

    // 웹 리소스 기본 경로 지정
    private final String START_PAGE = "ch10/newsList.jsp";

    public void init(ServletConfig config) throws ServletException{
        super.init(config);
        dao = new NewsDAO();
        ctx = getServletContext();
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        String action = request.getParameter("action");

        dao = new NewsDAO();

        // 자바 리플렉션을 사용해 if(swtich) 없이 요청에 따라 구현 메소드가 실행되도록 구성

        Method m;
        String view = null;

        try {
            if (action == null) {
                action = "listNews"; // action이 null인 경우 기본값으로 "listNews"를 사용
            }
            m = this.getClass().getMethod(action, HttpServletRequest.class);

            view = (String) m.invoke(this, request);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();

            ctx.log("요청 action 없음!!");
            request.setAttribute("error", "action 파라미터가 잘못 되었습니다!!");
            view = START_PAGE;
        } catch (Exception e) {
            e.printStackTrace();
            ctx.log("오류 발생!!");
            request.setAttribute("error", "오류가 발생했습니다!!");
            view = START_PAGE;
        }

        if (view == null) {
            view = START_PAGE;
        }

        if (view.startsWith("redirect:/")) {
            String rview = view.substring("redirect:/".length()); // redirect:/ 문자열 이후 경로만 가져옴
            response.sendRedirect(rview);
        } else {
            RequestDispatcher dispatcher = request.getRequestDispatcher(view);
            dispatcher.forward(request, response); // 지정된 뷰로 포워딩, 포워딩 시 콘텍스트 경로는 필요 없음
        }
    }

    public String addNews(HttpServletRequest request) {

        News n = new News();

        try {
            // 이미지 파일 저장
            Part part = request.getPart("file");
            String fileName = getFileName(part);

            if(fileName != null && !fileName.isEmpty()) {
                part.write(fileName);
            }


            // 입략값을 News 객체로 매핑
            BeanUtils.populate(n, request.getParameterMap());

            // 이미지 파일 이름을 News 객체에도 저장
            n.setImg("/img/"+fileName);

            dao.addNews(n);
        } catch (Exception e) {
            e.printStackTrace();
            ctx.log("뉴스 추가 과정에서 문제 발생!!", e);
            request.setAttribute("error", "뉴스가 정상적으로 등록되지 않았습니다!!");
            return listNews(request);
        }

        return "redirect:/news.nhn?action=listNews";
    }

    public String getFileName(Part part) {
        String fileName = null;
        // 파일이름이 들어있는 헤더 영역을 가지고 옴
        String header = part.getHeader("content-disposition");
        // part.getHeader -> form-data; name="img"; filename="사진5.jpg"
        System.out.println("Header =>" + header);

        int start = header.indexOf("filename=");
        fileName = header.substring(start + 10, header.length() - 1);
        ctx.log("파일명:" + fileName);
        return fileName;
    }

    public String getNews(HttpServletRequest request) {
        int aid = Integer.parseInt(request.getParameter("aid"));
        try {
            News n = dao.getNews(aid);
            request.setAttribute("news", n);
        } catch (Exception e) {
            e.printStackTrace();
            ctx.log("뉴스를 가져오는 과정에서 문제 발생!!", e);
            request.setAttribute("error", "뉴스를 정상적으로 가져오지 못했습니다!!");
        }
        return "ch10/newsView.jsp";
    }

    public String listNews(HttpServletRequest request) {
        List<News> list;
        try {
            list = dao.getAll();
            request.setAttribute("newslist", list);
        } catch (Exception e) {
            e.printStackTrace();
            ctx.log("뉴스 목록 생성 과정에서 문제 발생!!");
            request.setAttribute("error", "뉴스 목록이 정상적으로 처리되지 않았습니다: " + e.getMessage());
        }
        return "ch10/newsList.jsp";
    }

    public String deleteNews(HttpServletRequest request) {
        int aid = Integer.parseInt(request.getParameter("aid"));

        try {
            dao.delNews(aid);
        } catch (SQLException e) {
            e.printStackTrace();
            ctx.log("뉴스 삭제 과정에서 문제 발생!!", e);
            request.setAttribute("error", "뉴스가 정상적으로 삭제되지 않았습니다!!");
            return listNews(request);
        }

        return "redirect:/news.nhn?action=listNews";
    }


}