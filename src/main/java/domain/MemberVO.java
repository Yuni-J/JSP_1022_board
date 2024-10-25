package domain;

public class MemberVO {
	private String id;
	private String pwd;
	private String email;
	private String phone;
	private String regdate;
	private String lastlogin;
	
	public MemberVO() {}
	// 관리자 입장 / 사용자 입장
	// 회원등급 : admin(사이트 관리자), manager(각 파트의 최고관리자), user(사용자)
	
	// 회원가입(수정) : id, pwd, email, phone
	public MemberVO(String id, String pwd, String email, String phone) {
		this.id = id;
		this.pwd = pwd;
		this.email = email;
		this.phone = phone;
	}
	// 로그인 : id, pwd
	public MemberVO(String id, String pwd) {
		this.id = id;
		this.pwd = pwd;
		
	}
	// 회원리스트 : 관리자만 접근가능(전체)
	public MemberVO(String id, String pwd, String email, String phone, String regdate, String lastlogin) {
		this.id = id;
		this.pwd = pwd;
		this.email = email;
		this.phone = phone;
		this.regdate = regdate;
		this.lastlogin = lastlogin;
	}
	
	
	
	@Override
	public String toString() {
		return "MemberVO [id=" + id + ", pwd=" + pwd + ", email=" + email + ", phone=" + phone + ", regdate=" + regdate
				+ ", lastlogin=" + lastlogin + "]";
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getRegdate() {
		return regdate;
	}

	public void setRegdate(String regdate) {
		this.regdate = regdate;
	}

	public String getLastlogin() {
		return lastlogin;
	}

	public void setLastlogin(String lastlogin) {
		this.lastlogin = lastlogin;
	}

}
