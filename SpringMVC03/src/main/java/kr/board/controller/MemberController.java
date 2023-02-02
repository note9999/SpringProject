package kr.board.controller;

import javax.servlet.http.HttpSession;

import org.omg.CORBA.PUBLIC_MEMBER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.support.SmartTransactionObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kr.board.entity.Member;
import kr.board.mapper.MemberMapper;

@Controller
public class MemberController {

	@Autowired
	MemberMapper memberMapper;
	
	@RequestMapping("/memJoin.do")
	public String memJoin() {
		return "member/join";
	}
	
	
	@RequestMapping("/memRegisterCheck.do")
	public @ResponseBody int memRegisterCheck(@RequestParam("memID") String memID) {
	  
	  Member m = memberMapper.registerCheck(memID);
	  
	  if(m != null || memID.equals("")) { 
		  return 0; //이미 존재하는 회원, 입력불가 }
	  	}
	  
	  return 1; //사용가능한 아이디 }
	
	  }
	  // 회원가입 처리
	@RequestMapping("/memRegister.do")
	public String memRegister(Member m, String memPassword1, String memPassword2,
			RedirectAttributes rttr, HttpSession session) {
		if(m.getMemID()==null || m.getMemID().equals("") ||
				memPassword1==null|| memPassword1.equals("") ||
				memPassword2==null|| memPassword2.equals("") ||
				m.getMemName()==null || m.getMemName().equals("") ||
				m.getMemAge()==0 || 
				m.getMemGender()==null || m.getMemGender().equals("") ||
				m.getMemEmail()==null || m.getMemEmail().equals("")) {
			//누락메세지를 가지고 가기 => 객체바인딩 (Model, HttpServleRequest, HttpSession)
			rttr.addFlashAttribute("msgType","실패 메세지");
			rttr.addFlashAttribute("msg","모든 내용을 입력하세요.");
			return "redirect://memJoin.do"; //${msgType},${msg} 로도 가능 (EL)
		}
			if(!memPassword1.equals(memPassword2)) {
				rttr.addFlashAttribute("msgType","실패 메세지");
				rttr.addFlashAttribute("msg","비밀번호가 서로 다릅니다.");
				return "redirect://memJoin.do"; //${msgType},${msg} 
			}
			m.setMemProfile(null); // 사진이미지는 없다는 의미""
			//회원을 테이블에 저장하기
			int result=memberMapper.register(m);
			if(result==1) { //회원가입 성공메세지
				rttr.addFlashAttribute("msgType","성공 메세지");
				rttr.addFlashAttribute("msg","회원가입에 성공했습니다.");
			//회원가입이 성공하면-> 로그인 처리하기 
				session.setAttribute("mvo", m); //${!empty m}
				return "redirect:/";
			}else {
				rttr.addFlashAttribute("msgType","성공 메세지");
				rttr.addFlashAttribute("msg","이미 존재하는 회원입니다.");
				return "redirect:/memJoin.do";
		}
	}
		//로그아웃 처리
	@RequestMapping ("/memLogout.do")
	public String memLogout(HttpSession session) {
	session.invalidate();
	return "redirect:/";
 }
	// 로그인화면
  @RequestMapping ("/memLoginForm.do")
  public String memLoginForm() {
	return "member/memLoginForm"; // memLoginForm.jsp
 }
  // 로그인기능구현
  @RequestMapping("/memLogin.do")
  public String memLogin(Member m, RedirectAttributes rttr, HttpSession session) {
     if(m.getMemID() == null || m.getMemPassword().equals("") || 
        m.getMemPassword() == null || m.getMemPassword().equals("")) {
        rttr.addFlashAttribute("msgType", "실패 메시지");
        rttr.addFlashAttribute("msg", "모든 내용을 입력해주세요");
        return "redirect:/memLoginForm.do";
     }
     Member mvo=memberMapper.memLogin(m);
      if(mvo!=null) {
    	rttr.addFlashAttribute("msgType","성공 메세지");
		rttr.addFlashAttribute("msg","로그인에 성공했습니다.");
		session.setAttribute("mvo", mvo); //${!empty mvo}
		return "redirect:/"; //메인 
     }else { // 로그인에 실패
    	 rttr.addFlashAttribute("msgType", "실패 메시지");
         rttr.addFlashAttribute("msg", "다시 로그인 해주세요");
         return "redirect:/memLoginForm.do";
      }
  	}
 // 회원정보 수정 화면 으로 가게끔
  @RequestMapping("/memUpdateForm.do")
  public String memUpdateForm() {
	  return "member/memUpdateForm";
			  
  }
  // 회원정보수정
  @RequestMapping("/memUpdate.do")
  public String memUpdate(Member m, RedirectAttributes rttr,
		  String memPassword1, String memPassword2, HttpSession session) {
		
	  if(m.getMemID()==null || m.getMemID().equals("") ||
				memPassword1==null|| memPassword1.equals("") ||
				memPassword2==null|| memPassword2.equals("") ||
				m.getMemName()==null || m.getMemName().equals("") ||
				m.getMemAge()==0 || 
				m.getMemGender()==null || m.getMemGender().equals("") ||
				m.getMemEmail()==null || m.getMemEmail().equals("")) {
			//누락메세지를 가지고 가기 => 객체바인딩 (Model, HttpServleRequest, HttpSession)
			rttr.addFlashAttribute("msgType","실패 메세지");
			rttr.addFlashAttribute("msg","모든 내용을 입력하세요.");
			return "redirect://memUpdateForm.do"; //${msgType},${msg} 로도 가능 (EL)
		}
			if(!memPassword1.equals(memPassword2)) {
				rttr.addFlashAttribute("msgType","실패 메세지");
				rttr.addFlashAttribute("msg","비밀번호가 서로 다릅니다.");
				return "redirect://memUpdateForm.do"; //${msgType},${msg} 
			}
			//회원정보수정 후 저장하기
			int result=memberMapper.memUpdate(m);
			if(result==1) { //수정 공메세지
				rttr.addFlashAttribute("msgType","성공 메세지");
				rttr.addFlashAttribute("msg","회원정보 수정에 성공했습니다.");
			//회원정보수정이 성공하면 -> 
				session.setAttribute("mvo", m); //${!empty m}
				return "redirect:/";
			}else {
				rttr.addFlashAttribute("msgType","성공 메세지");
				rttr.addFlashAttribute("msg","회원정보 수정에 실패했습니다.");
				return "redirect:/memUpdateForm.do";
			}
		}
	// 회원의 사진등록 화면
	@RequestMapping("/memImageForm.do")
	public String memImageForm() {
		return "member/memImageForm"; // memImageForm.jsp
	}
  }
  	
