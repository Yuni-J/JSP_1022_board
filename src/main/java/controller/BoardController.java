package controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import domain.BoardVO;
import serivce.BoardService;
import serivce.BoardServiceImpl;

@WebServlet("/brd/*")
public class BoardController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	// 로그 객체 생성
	private static final Logger log = LoggerFactory.getLogger(BoardController.class);
    // jsp에서 받은 요청을 처리, 그 결과를 다른 jsp로 보내는 역할을 하는 객체
	private RequestDispatcher rdp;
	private String destPage;  //응답할 jsp의 주소를 저장하는 변수
	private int isOk;  //db 구문 체크값 저장 변수
	private BoardService bsv;  //interface로 생성


    public BoardController() {
        // 생성자
    	bsv = new BoardServiceImpl();  //class로 생성(service package에) bsv 구현체 연결
    }


	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 실제 get post 요청처리
		log.info("log 객체 test");
		System.out.println("sysout console 출력");
		
		// request, response 객체의 인코딩 설정
		request.setCharacterEncoding("UTF-8");  //요청객체
		response.setCharacterEncoding("utf-8");  //응답객체
		//response는 jsp로 갈 응답 객체 => 화면을 생성해서 응답 => jsp 형식으로...
		//contentType="text/html; charset=UTF-8" 
		response.setContentType("text/html; charset=UTF-8");  //html5 형식으로 보내라\
		
		// 경로 가져오기  /brd/register
		String uri = request.getRequestURI();
		log.info(uri);
		
		String path = uri.substring(uri.lastIndexOf("/")+1);
		log.info(path);
		
		switch(path) {
		case "register": 
			//정보가 필요하다면 정보를를 DB에서 요청 request 객체에 싣고 보내기
			destPage = "/board/register.jsp";
			break;
		case "insert":
			try {
				
				log.info("insert case in~!!");
				// jsp 화면에서 보내온 파라미터 값을 저장 => Service 전송
				String title = request.getParameter("title");
				String writer = request.getParameter("writer");
				String content = request.getParameter("content");
				BoardVO bvo = new BoardVO(title, writer, content);
				log.info(">>>>> bvo insert 객체 >>>>> {}", bvo);
				
				isOk = bsv.register(bvo);
				log.info(">>> bvo insert"+(isOk>0?"성공":"실패"));
				
				// 처리 후 목적지
				destPage = "/index.jsp";
				
			} catch (Exception e) {
				log.info("insert error!!");
				e.printStackTrace();
			}
			break;
		case "list": 
			try {
				//전체 리스트를 가지고 list.jsp로 전달
				List<BoardVO> list = bsv.getList();
				log.info(">>> list >>> {}", list);
				// request 객체에 파라미터로 값을 보내는 방법
				request.setAttribute("list", list);
				destPage = "/board/list.jsp";
				
			} catch (Exception e) {
				log.info("list error!!");
				e.printStackTrace();
			}
			break;
		case "detail":
			try {
				int bno = Integer.parseInt(request.getParameter("bno"));
				BoardVO bvo = bsv.getDetail(bno);
				log.info(">>> detail bvo >> {}", bvo);
				request.setAttribute("bvo", bvo);
				destPage="/board/detail.jsp";
				
			} catch (Exception e) {
				log.info("detail error!!");
				e.printStackTrace();
			}
			break;
		}
		
		// 처리 완료 후
		// 목적지 주소(destPage)로 데이터를 전달(RequestDispatcher)
		rdp = request.getRequestDispatcher(destPage);
		// 요청에 필요한 객체를 가지고 destPage에 적힌 경로로 이동
		rdp.forward(request, response);
		
		
	}


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// get으로 들어오는 요청을 처리하는 메서드 => service를 호출하여 처리
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		service(request, response);
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// post로 들어오는 요청을 처리하는 메서드
		//doGet(request, response);  //service가 있으므로 service로 대체
		service(request, response);
	}

}