
public enum ElectiveType {
	FACHPRUEFUNG, PRAKTIKUM, SEMINAR, PROJEKTPRAKTIKUM, WEITERE_LEHRFORM;
	
	
	public static ElectiveType filterElectiveType(String str) {
		
		if(str.contains("Fachprüfungen")) {
			return ElectiveType.FACHPRUEFUNG;
		}
		
		if(str.contains("Praktika")) {
			return ElectiveType.PRAKTIKUM;
		}
		
		if(str.contains("Seminare")) {
			return ElectiveType.SEMINAR;
		}
		
		if(str.contains("Projektpraktika")) {
			return ElectiveType.PROJEKTPRAKTIKUM;
		}
		if(str.contains("weitere")) {
			return ElectiveType.WEITERE_LEHRFORM;
		}
		
		
		
		return null;
	}
}
