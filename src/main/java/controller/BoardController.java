package controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import domain.BoardVO;
import handler.FileRemoveHandler;
import net.coobird.thumbnailator.Thumbnails;
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
	private String savePath;  //파일저장 경로


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
		response.setContentType("text/html; charset=UTF-8");  //html5 형식으로 보내라\ =>동기방식, 비동기는 안해도됌
		
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
				// 첨부파일 없을 경우(주석처리)
				// jsp 화면에서 보내온 파라미터 값을 저장 => Service 전송
//				String title = request.getParameter("title");
//				String writer = request.getParameter("writer");
//				String content = request.getParameter("content");
//				BoardVO bvo = new BoardVO(title, writer, content);
//				log.info(">>>>> bvo insert 객체 >>>>> {}", bvo);
				
				// 첨부파일 있는 경우
				// bvo를 구성하여 DB로 전송
				// file을 저장하는 작업 => 파일 이름만 imageFile에 저장
				// 첨부파일 형식으로 들어오게되면 모든 파라미터는 바이트단위로 분해되서 전송
				// 바이트단위로 전송된 파라미터의 값을 String으로 조립을 해야함.
				// 1. 파일을 업도로할 물리적인 경로 설정
				savePath = getServletContext().getRealPath("/_fileUpload");
				log.info(">>> savePath > {} ", savePath);
				
				File fileDir = new File(savePath);
				DiskFileItemFactory fileItemFactory = new DiskFileItemFactory();
				// 파일 저장을 위한 임시 메모리 설정
				fileItemFactory.setSizeThreshold(1024*1024*3);
				// 저장위치를 담은 File 객체
				fileItemFactory.setRepository(fileDir);
				
				BoardVO bvo = new BoardVO();  //빈 객체 생성 후 set을 통해서 셋팅
				
				// multipart/form-data 형식으로 넘어온 request 객체를 다루기 쉽게 변환해주는 객체
				ServletFileUpload fileUpload = new ServletFileUpload(fileItemFactory);
				// request 객체를 FileItem 형식의 리스트로 리턴
				List<FileItem> itemList = fileUpload.parseRequest(request);
				// title, writer, content => text(String)  imageFile => image
				for(FileItem item : itemList) {
					log.info("fileItem > {}", item.toString());
					switch(item.getFieldName()) {
					case "title":
						bvo.setTitle(item.getString("utf-8"));
						break;
					case "writer":
						bvo.setWriter(item.getString("utf-8"));
						break;
					case "content":
						bvo.setContent(item.getString("utf-8"));
						break;
					case "imageFile":
						// 이미지 파일 여부를 체크
						if(item.getSize() > 0) {
							// 파일 이름 추출
							// 경로+ ~~~/dog.jpg
							String fileName = item.getName();
							// 경로 빼고 이름만 가져오기
							//String fileName2 = item.getName()
							//		.substring(item.getName().lastIndexOf(File.separator)+1);
							// File.separator : 파일 경로 기준 => 운영체제마다 다를 수 있어서 자동 변환
							// 시스템의 시간을 이용하여 파일을 구분 / 시간_dog.jpg
							// UUID를 사용하는 구문도 있음 (=> 많이 사용)
							fileName = System.currentTimeMillis()+"_"+fileName;
							
							// 아까 경로 만들어 놓은 파일 fileDir + fileName
							File uploadFilePath = new File(fileDir + File.separator+fileName);
							log.info(">>> uploadFilePath > {}", uploadFilePath.toString());
							
							//저장
							try {
								item.write(uploadFilePath);  //객체를 디스크에 쓰기
								bvo.setImageFile(fileName);  //bvo에 저장할 값 (DB에 들어가는 값)
								
								// 썸네일 작업 : 리스트 페이지에서 트레픽 과다 사용 방지
								// fileDir + / +_th_+ fileName
								Thumbnails.of(uploadFilePath).size(75, 75)
								.toFile(new File(fileDir + File.separator+"_th_"+fileName));
								
							} catch (Exception e) {
								log.info(">>> file writer on disk error");
								e.printStackTrace();
							}
							
						}
						break;
					}
				} 
				//D:\yuni_240725\jstl_workspace\.metadata\.plugins\org.eclipse.wst.server.core\tmp0\wtpwebapps\jsp_study\_fileUpload
				// 결과 사진은 여기에 저장됨.
							
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
			
		case "detail": case "modify": 
			try {
				int bno = Integer.parseInt(request.getParameter("bno"));
				// readCount 반영 (case가 detail인 경우만)
				if(path.equals("detail")) {
					int cnt = bsv.readCount(bno);
					log.info(">>> detail readCount >> {}", cnt);
				}
				BoardVO bvo = bsv.getDetail(bno);
				log.info(">>> detail bvo >> {}", bvo);
				request.setAttribute("bvo", bvo);
//				if(path.equals("detail")) {
//					destPage="/board/detail.jsp";					
//				} else {
//					destPage="/board/modify.jsp";
//				}
				destPage = "/board/"+path+".jsp";
				
			} catch (Exception e) {
				log.info("detail error!!");
				e.printStackTrace();
			}
			break;
			
		case "update":
			try {
				// 첨부파일 이전 처리 방식
//				int bno = Integer.parseInt(request.getParameter("bno"));
//				String title = request.getParameter("title");
//				String content = request.getParameter("content");
//				BoardVO bvo = new BoardVO(bno, title, content);
				
				// 첨부파일 처리 포함 방식
				savePath = getServletContext().getRealPath("_fileUpload");
				File fileDir = new File(savePath);
				
				DiskFileItemFactory fileItemFactory = new DiskFileItemFactory();
				fileItemFactory.setSizeThreshold(1024*1024*3);  //3MB 정도 설정
				fileItemFactory.setRepository(fileDir);
				
				BoardVO bvo = new BoardVO();
				
				ServletFileUpload fileUpload = new ServletFileUpload(fileItemFactory);
				List<FileItem> itemList = fileUpload.parseRequest(request);
				
				// 원래 이미지 파일이 있었는데 변경하는 케이스
				// 없던 이미지를 추가하는 케이스
				String old_file = null;
				
				for(FileItem item : itemList) {
					switch(item.getFieldName()) {
					case "bno":
						bvo.setBno(Integer.parseInt(item.getString("utf-8")));
						break;
					case "title":
						bvo.setTitle(item.getString("utf-8"));
						break;
					case "content":
						bvo.setContent(item.getString("utf-8"));
						break;
					case "imageFile":
						// 기존 파일 => 있을 수도 있고, 없을 수도 있고
						old_file = item.getString("utf-8");  //파일명만
						break;
					case "newFile":
						// 새로 추가된 파일 => 있을 수도 있고, 없을 수도 있고
						if(item.getSize()>0) {
							if(old_file != null) {
								// 기존파일이 있다면
								// 파일 삭제 작업 : 별도 핸들러로 작업진행
								FileRemoveHandler fileHandler = new FileRemoveHandler();
								isOk = fileHandler.deleteFile(path, old_file);
								
							}
							// 새로운 파일 등록 작업
							String fileName = item.getName();
							fileName = System.currentTimeMillis()+"_"+fileName;
							
							File uploadFilePath = new File(fileDir+File.separator+fileName);
							try {
								item.write(uploadFilePath);
								bvo.setImageFile(fileName);  //파일 이름 바꾸기
								Thumbnails.of(uploadFilePath)  //기존 파일
								.size(75, 75)
								.toFile(fileDir+File.separator+"_th_"+fileName);
								
							} catch (Exception e) {
								log.info("File writer update error!!");
								e.printStackTrace();
							}
						} else {
							// 기존 파일은 있지만, 새로운 이미지 파일이 없다면
							// 기존 객체를 담기
							bvo.setImageFile(old_file);
						}
						break;
					}
				}
				
				int isOk = bsv.update(bvo);
				log.info(">>> update >>> "+(isOk>0?"성공":"실패"));
				// 컨트롤러 내부 케이스는 /brd/ 따로 적을 필요가 없음.
				destPage = "detail?bno="+bvo.getBno();
				
			} catch (Exception e) {
				log.info("update error!!");
				e.printStackTrace();
			}
			break;
			
		case "delete":
			try {
				int bno = Integer.parseInt(request.getParameter("bno"));
				int isOk = bsv.delete(bno);
				log.info(">>> delete >>> "+(isOk>0?"성공":"실패"));
				destPage = "list";  // 내부 케이스를 탈 때 
			} catch (Exception e) {
				log.info("delete error!!");
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
