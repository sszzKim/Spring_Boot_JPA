package com.board.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.board.dto.BoardDTO;
import com.board.dto.FileDTO;
import com.board.dto.LikeDTO;
import com.board.dto.ReplyDTO;
import com.board.dto.ReplyInterface;
import com.board.entity.BoardEntity;
import com.board.entity.FileEntity;
import com.board.entity.LikeEntity;
import com.board.entity.MemberEntity;
import com.board.entity.ReplyEntity;
import com.board.entity.repository.BoardRepository;
import com.board.entity.repository.FileRepository;
import com.board.entity.repository.LikeRepository;
import com.board.entity.repository.MemberRepository;
import com.board.entity.repository.ReplyRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {
	
	private final BoardRepository boardRepository;
	private final FileRepository fileRepository;
	private final LikeRepository likeRepository;
	private final MemberRepository memberRepository;
	private final ReplyRepository replyRepository;
	
	//게시물 목록보기
	@Override
	public Page<BoardEntity> list(int pageNum, int postNum, String keyword){
		PageRequest pageRequest = PageRequest.of(pageNum-1,postNum, Sort.by(Direction.DESC,"seqno"));
	    return boardRepository.findByTitleContainingOrWriterContainingOrContentContaining(keyword, keyword, keyword, pageRequest);
	}

	//게시물 내용보기
	@Override
	public BoardDTO view(Long seqno) throws Exception {
		//가급적 Entity 값을 DTO에 옮겨서 사용하기
		return boardRepository.findById(seqno).map(view -> new BoardDTO(view)).get();  //Optional null인지 check해야하기 때문에 get을 쓴다.
		//return mapper.view(seqno);
	}
	
	//게시물 이전보기
	@Override
	public Long pre_seqno(Long seqno, String keyword) throws Exception {
		//return mapper.pre_seqno(data);
		Long val = boardRepository.pre_seqno(seqno, keyword, keyword, keyword);
		return (val == null) ? 0 : val;
	}
	
	//게시물 다음보기
	@Override
	public Long next_seqno(Long seqno, String keyword) throws Exception {
		//return mapper.next_seqno(data);
		Long val = boardRepository.next_seqno(seqno, keyword, keyword, keyword);
		return (val == null) ? 0 : val;
	}
	
	//게시물 조회증가
	@Override
	public void hitNoUpdate(Long seqno) throws Exception {
		//mapper.hitNoUpdate(seqno);
		boardRepository.hitNoUpdate(seqno);
	}
	
	//게시물 등록하기
	@Override
	public void write(BoardDTO board) throws Exception {
		//mapper.write(board);
		board.setRegdate(LocalDateTime.now());
		boardRepository.save(board.dtoToEntity(board));
	}
	
	//게시물 번호 구하기
	@Override
	public Long getSeqnoNextval() throws Exception {
		//return mapper.getSeqnoNextval();
		return boardRepository.getSeqnoNextval();
	}
	
	//첨부파일 정보 등록하기
	@Override
	public void fileUpload(FileDTO fileDTO) throws Exception {
		//mapper.fileUpload(map);
		fileRepository.save(fileDTO.dtoToEntity(fileDTO));
	}
	
	//첨부파일 목록보기
	@Override
	public List<FileEntity> getFileList(Long seqno) throws Exception { //얜 Entity로 받는다.
		//return mapper.getFileList(seqno);
		return fileRepository.findBySeqnoAndCheckfile(seqno, "O");
	}
	
	//게시글 삭제 시 첨부파일 삭제 checkfile 정보변경
	@Override
	public void checkfileUpdate(Long seqno) throws Exception {
		//mapper.checkfileUpdate(seqno);
		fileRepository.findBySeqno(seqno).stream().forEach(file -> {
			file.setCheckfile("N");
			fileRepository.save(file);
		});
	}
	
	//파일 정보 가져오기
	@Override
	public FileDTO getFile(Long fileseqno) throws Exception {
		//return mapper.getFile(fileseqno);
		return fileRepository.findById(fileseqno).map(file->new FileDTO(file)).get();
	}
	
	@Override
	public void delete(Long seqno) throws Exception {
		//mapper.delete(seqno);
		BoardEntity boardEntity = boardRepository.findById(seqno).get();
		boardRepository.delete(boardEntity);
	}
	
	//게시물 수정 하기
	@Override
	public void modify(BoardDTO board) throws Exception {
		//apper.modify(board);
		BoardEntity boardEntity = boardRepository.findById(board.getSeqno()).get();
		boardEntity.setTitle(board.getTitle());
		boardEntity.setContent(board.getContent());
		
		boardRepository.save(boardEntity); //JPA는 insert나 update나 save로 퉁!
	}
	
	//게시물 좋아요 싫어요 수정
	@Override
	public void boardLikeUpdate(BoardDTO board) throws Exception {
		//mapper.boardLikeUpdate(board);
		BoardEntity boardEntity = boardRepository.findById(board.getSeqno()).get();
		boardEntity.setLikecnt(board.getLikecnt());
		boardEntity.setDislikecnt(board.getDislikecnt());
		boardRepository.save(boardEntity);
	}

	//파일 개별 삭제
	@Override
	public void deleteFileList(Long fileseqno) throws Exception {
		//mapper.deleteFileList(fileseqno);
		FileEntity fileEntity = fileRepository.findById(fileseqno).get();
		fileEntity.setCheckfile("X");
		fileRepository.save(fileEntity);
	}
	
	//좋아요 싫어요 등록여부 확인
	@Override
	public LikeEntity likeCheckView(Long seqno, String email) throws Exception {
		//return mapper.likeCheckView(like);
		BoardEntity boardEntity = boardRepository.findById(seqno).get();
		MemberEntity memberEntity = memberRepository.findById(email).get();
		return likeRepository.findBySeqnoAndEmail(boardEntity, memberEntity);
	}
	
	//좋아요 싫어요 신규 등록
	@Override
	public void likeCheckRegistry(Long seqno, String email, String mylikeCheck, String mydislikeCheck, String likeDate, String dislikeDate) throws Exception {
		//mapper.likeCheckRegistry(like);
		BoardEntity boardEntity = boardRepository.findById(seqno).get();
		MemberEntity memberEntity = memberRepository.findById(email).get();
		
		LikeEntity likeEntity = LikeEntity.builder()
				.seqno(boardEntity)
				.email(memberEntity)
				.mylikecheck(mylikeCheck)
				.mydislikeckeck(mydislikeCheck)
				.likedate(likeDate)
				.dislikedate(dislikeDate)
				.build();
		
		likeRepository.save(likeEntity);
	}
	
	//좋아요 싫어요 수정
	@Override
	public void likeCheckUpdate(Long seqno, String email, String mylikeCheck, String mydislikeCheck, String likeDate, String dislikeDate) throws Exception {
		//mapper.likeCheckUpdate(like);
		BoardEntity boardEntity = boardRepository.findById(seqno).get();
		MemberEntity memberEntity = memberRepository.findById(email).get();
		
		LikeEntity likeEntity = likeRepository.findBySeqnoAndEmail(boardEntity,memberEntity);
		
		likeEntity.setSeqno(boardEntity);
		likeEntity.setEmail(memberEntity);
		likeEntity.setMylikecheck(mylikeCheck);
		likeEntity.setMydislikeckeck(mydislikeCheck);
		likeEntity.setLikedate(likeDate);
		likeEntity.setDislikedate(dislikeDate);
		
		likeRepository.save(likeEntity);
	}
	
	
	//댓글 목록 보기
	@Override
	public List<ReplyInterface> replyView(ReplyInterface reply) throws Exception {
		//return mapper.replyView(seqno);
		return replyRepository.replyView(reply.getSeqno());
	}

	//댓글 등록
	@Override
	public void replyRegistry(ReplyInterface reply) throws Exception {
		//mapper.replyRegistry(reply);
		
		BoardEntity boardEntity = boardRepository.findById(reply.getSeqno()).get();
		MemberEntity memberEntity = memberRepository.findById(reply.getEmail()).get();
		
		ReplyEntity replyEntity = ReplyEntity.builder()
				.seqno(boardEntity)
				.email(memberEntity)
				.replywriter(reply.getReplywriter())
				.replycontent(reply.getReplycontent())
				.replyregdate(LocalDateTime.now())
				.build();
		
		replyRepository.save(replyEntity);
		
	}

	//댓글 수정
	@Override
	public void replyUpdate(ReplyInterface reply) throws Exception {
		//mapper.replyUpdate(reply);
		//BoardEntity boardEntity = boardRepository.findById(reply.getSeqno()).get();
		//MemberEntity memberEntity = memberRepository.findById(reply.getEmail()).get();
		
		ReplyEntity replyEntity = replyRepository.findById(reply.getReplyseqno()).get();
		replyEntity.setReplycontent(reply.getReplycontent());
		
		replyRepository.save(replyEntity);
	}

	//댓글 삭제
	@Override
	public void replyDelete(ReplyInterface reply) throws Exception {
        ReplyEntity replyEntity = replyRepository.findById(reply.getReplyseqno()).get();
        replyRepository.delete(replyEntity);
	}

}
