
public class Module {
	private String moduleName;
	private int credits;
	private String turnus;
	private Elective elective;
	private ElectiveType electiveType;

	public Module(String moduleName, int credits, String turnus, Elective elective, ElectiveType electiveType) {
		setModuleName(moduleName);
		setCredits(credits);
		setTurnus(turnus);
		setElective(elective);
		setElectiveType(electiveType);

	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public int getCredits() {
		return credits;
	}

	public void setCredits(int credits) {
		this.credits = credits;
	}

	public String getTurnus() {
		return turnus;
	}

	public void setTurnus(String turnus) {
		this.turnus = turnus;
	}

	public Elective getElective() {
		return elective;
	}

	public void setElective(Elective elective) {
		this.elective = elective;
	}

	public ElectiveType getElectiveType() {
		return electiveType;
	}

	public void setElectiveType(ElectiveType electiveType) {
		this.electiveType = electiveType;
	}

}
