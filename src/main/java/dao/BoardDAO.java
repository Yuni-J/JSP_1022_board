package dao;

import java.util.List;

import domain.BoardVO;

public interface BoardDAO {

	int insert(BoardVO bvo);

	List<BoardVO> getList();

	BoardVO getDetail(int bno);

	int update(BoardVO bvo);

	int delete(int bno);

<<<<<<< HEAD
	int readCount(int bno);

=======
>>>>>>> origin/main
}
