
public enum Elective {
	IT_SICHERHEIT, NETZE_UND_VERTEILTE_SYSTEME, ROBOTIK_COMPUTATIONAL_UND_COMPUTER_ENGINEERING, SOFTWARE_SYSTEME_UND_FORMALE_GRUNDLAGEN, VISUAL_AND_INTERACTIVE_COMPUTING, WEB_WISSENS_UND_INFORMATIONSVERARBEITUNG, BACHELORARBEIT_MASTERARBEIT; 

	
	public static Elective filterElective(String str) {
		if(str.contains("icherh")) {
			return IT_SICHERHEIT;
		}
		
		if(str.contains("tze und verteilt")) {
			return NETZE_UND_VERTEILTE_SYSTEME;
		}
		
		if(str.contains("obotik, Comput")) {
			return ROBOTIK_COMPUTATIONAL_UND_COMPUTER_ENGINEERING;
		}
		
		if(str.contains("re-Syste")) {
			return SOFTWARE_SYSTEME_UND_FORMALE_GRUNDLAGEN;
		}
		
		if(str.contains("ual & Interac")) {
			return VISUAL_AND_INTERACTIVE_COMPUTING;
		}
		
		if(str.contains("sens- u")) {
			return WEB_WISSENS_UND_INFORMATIONSVERARBEITUNG;
		}
		if(str.contains("Bachelorarbeit")) {
			return BACHELORARBEIT_MASTERARBEIT;
		}
		
		return null;
	}
	
}
