import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.text.PDFTextStripperByArea;

/**
 * This application parse the TU Darmstadt University module guide to filter
 * module names, credits and the turnus of the offered modules and export them
 * into a clear and easy to handle overview pdf.
 * 
 * @author pstiegele
 *
 */
public class ModuleGuideParser {
	
	final static int DEFAULT_EXPORT_START_HEIGHT = 750;
	private static int exportHeight = DEFAULT_EXPORT_START_HEIGHT;
	static Integer pageToStartWith = 0;

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);  
		
		String filepath = getFilePath(scanner);
		String exportfilepath = getExportFilePath(scanner);
		
		PDDocument document = readFile(filepath);
		ArrayList<Module> module = parseFile(document);
		exportModules(module, exportfilepath);
		System.out.println("Vorgang abgeschlossen. Datei wurde erstellt: "+exportfilepath);
		scanner.close();

	}

	private static void exportModules(ArrayList<Module> module, String exportfilepath) {
		PDDocument doc = new PDDocument();
		PDPage page = new PDPage();
		doc.addPage(page);

		Elective[] allElectives = Elective.values();
		ElectiveType[] allElectiveTypes = ElectiveType.values();
		
		ArrayList<Module> modulesWithoutElective = new ArrayList<Module>();
		exportHeight = DEFAULT_EXPORT_START_HEIGHT;
		
		
		
		try {
			PDPageContentStream contents = new PDPageContentStream(doc, page);
			addText(contents, ExportType.HEADLINE, "Übersicht Modulhandbuch");
			
			
			for (int i = 0; i < allElectives.length; i++) {
				boolean alreadyPrintedElective = false;
				for (int j = 0; j < allElectiveTypes.length; j++) {
					boolean alreadyPrintedElectiveType = false;
					Iterator<Module> moduleIterator = module.iterator();
					while (moduleIterator.hasNext()) {
						Module moduleElement = (Module) moduleIterator.next();
						if(moduleElement.getElective()==null||moduleElement.getElectiveType()==null) {
							modulesWithoutElective.add(moduleElement);
							continue;
						}
						if(moduleElement.getElective().equals(allElectives[i])&&moduleElement.getElectiveType().equals(allElectiveTypes[j])) {
							if(exportHeight<50) {
								page = new PDPage();
								doc.addPage(page);
								contents.close();
								exportHeight = DEFAULT_EXPORT_START_HEIGHT;
								contents = new PDPageContentStream(doc, page);
							}
							if(alreadyPrintedElective==false) {
								if(exportHeight<150) {
									page = new PDPage();
									doc.addPage(page);
									contents.close();
									exportHeight = DEFAULT_EXPORT_START_HEIGHT;
									contents = new PDPageContentStream(doc, page);
								}
								addText(contents, ExportType.ELECTIVE, allElectives[i].name());
								alreadyPrintedElective = true;
							}
							if(alreadyPrintedElectiveType==false) {
								addText(contents, ExportType.ELECTIVETYPE, allElectiveTypes[j].name());
								alreadyPrintedElectiveType = true;
							}
							addText(contents, ExportType.MODULE, moduleElement);
						}
						
					}
				}
			}
			
			if(!modulesWithoutElective.isEmpty()) {
				addText(contents, ExportType.ELECTIVE, "Module ohne Kategorie");
				Iterator<Module> it = modulesWithoutElective.iterator();
				while (it.hasNext()) {
					if(exportHeight<50) {
						page = new PDPage();
						doc.addPage(page);
						contents.close();
						exportHeight = DEFAULT_EXPORT_START_HEIGHT;
						contents = new PDPageContentStream(doc, page);
					}
					Module moduleElement = (Module) it.next();
					addText(contents, ExportType.MODULE, moduleElement);
					
				}
				
			}
			
			
			
			contents.close();
			doc.save(exportfilepath);
			doc.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void addText(PDPageContentStream contents, ExportType type, Object moduleOrStringText) throws IOException {
		contents.beginText();
		switch (type) {
		case HEADLINE:
			String headlineText = (String) moduleOrStringText;
			contents.setFont(PDType1Font.HELVETICA_BOLD, 21);
			contents.newLineAtOffset(20, exportHeight);
			contents.showText(headlineText);
			break;
		case ELECTIVE:
			String electiveText = (String) moduleOrStringText;
			exportHeight -= 35;
			contents.setFont(PDType1Font.HELVETICA_BOLD, 16);
			contents.newLineAtOffset(35, exportHeight);
			contents.showText(electiveText);
			break;
		case ELECTIVETYPE:
			String electiveTypeText = (String) moduleOrStringText;
			exportHeight -= 25;
			contents.setFont(PDType1Font.HELVETICA_BOLD_OBLIQUE, 12);
			contents.newLineAtOffset(50, exportHeight);
			contents.showText(electiveTypeText);
			break;
		case MODULE:
			Module module = (Module) moduleOrStringText;
			exportHeight -= 15;
			contents.setFont(PDType1Font.HELVETICA_BOLD, 11);
			contents.newLineAtOffset(70, exportHeight);
			contents.showText(module.getModuleName());
			if(module.getModuleName().length()>60) {
				exportHeight -= 15;
				contents.newLineAtOffset(0, -15);
			}
			contents.setFont(PDType1Font.HELVETICA_OBLIQUE, 10);
			contents.showText("  ("+module.getCredits()+" CP, "+module.getTurnus()+")");
			break;
		}
		contents.endText();
	}
	
	private static ArrayList<Module> parseFile(PDDocument doc) {
		ArrayList<Module> listOfModules = new ArrayList<Module>();

		Iterator<PDPage> iterator = doc.getPages().iterator();
		PDFTextStripperByArea stripper = null;
		try {
			stripper = new PDFTextStripperByArea();
			stripper.setSortByPosition(true);
			Rectangle rectModuleName = new Rectangle(62, 109, 480, 44);
			Rectangle rectCredits = new Rectangle(127, 154, 77, 40);
			Rectangle rectTurnus = new Rectangle(455, 152, 95, 46);
			Rectangle rectElective = new Rectangle(30, 250, 530, 290);
			stripper.addRegion("moduleName", rectModuleName);
			stripper.addRegion("credits", rectCredits);
			stripper.addRegion("turnus", rectTurnus);
			stripper.addRegion("elective", rectElective);
		} catch (IOException e) {
			e.printStackTrace();
		}

		int i = 1;
		Elective currentElective = null;
		ElectiveType currentElectiveType = null;
		while (iterator.hasNext()) {
			PDPage page = iterator.next();
			i++;
			if(i-1<pageToStartWith) {
				continue;
			}
			

			try {
				stripper.extractRegions(page);
			} catch (IOException e) {
				e.printStackTrace();
			}

			String elective = stripper.getTextForRegion("elective");
			if (elective.contains("Modulhandbuch") && elective.contains("B. Sc./M. Sc. Informatik")) {
				if(Elective.filterElective(elective)!=null) {
					currentElective = Elective.filterElective(elective);
				}
				if(ElectiveType.filterElectiveType(elective)!=null) {
					currentElectiveType = ElectiveType.filterElectiveType(elective);
				}
				
				
			}

			String moduleName = stripper.getTextForRegion("moduleName");
			String credits = stripper.getTextForRegion("credits");
			String turnus = stripper.getTextForRegion("turnus");

			if (moduleName.contains("Modulname")) {
				int intCredits = -1;

				// System.out.println("Page " + (i));
				moduleName = moduleName.substring(moduleName.lastIndexOf("Modulname") + 10).replace("\n", "")
						.replace("\r", "").trim();
				if (moduleName.length() > 1 && moduleName.charAt(1) == ' ') {
					moduleName = moduleName.replaceFirst(" ", "");
				}

				if (credits.contains("ditpunkte")) {
					credits = credits.substring(credits.lastIndexOf("ditpunkte") + 9).replace("\n", "")
							.replace("\r", "").trim();

					try {
						intCredits = Integer.valueOf(credits.replaceAll("[^0-9]", ""));
					} catch (Exception e) {
						//e.printStackTrace();
					}

				}

				if (turnus.contains("sturnus")) {
					turnus = turnus.substring(turnus.lastIndexOf("sturnus") + 7).replace("\n", "").replace("\r", "")
							.trim();
				}

				Module module = new Module(moduleName, intCredits, turnus, currentElective, currentElectiveType);
				listOfModules.add(module);
			}

		}
		return listOfModules;

	}

	private static PDDocument readFile(String filepath) {
		PDDocument document = null;
		try {
			document = PDDocument.load(new File(filepath));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return document;
	}

	private static String getExportFilePath(Scanner sc) {
		System.out.println("Bitte geb den Pfad zum Export an (Pfad+Dateiname+Dateiendung):");
		String res = sc.nextLine();
		System.out.println("Jetzt noch die Seite, ab der mit der Suche gestartet werden soll:");
		pageToStartWith = Integer.valueOf(sc.nextLine().replaceAll("[^0-9]", ""));
		System.out.println("Pfad zum Export: " + res);
		return res;
	}

	private static String getFilePath(Scanner sc) {
		System.out.println("Bitte geb den Pfad zum Modulhandbuch an:");
		String res = sc.nextLine();
		System.out.println("Danke dir!");
		return res;
	}

}
