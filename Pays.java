package entites;

import java.util.ArrayList;

/**
 * Classe representant un pays
 * 
 * @author Remi Massart
 */
public class Pays {

	private ArrayList<Contribuable> residents;
	private ArrayList<Societe> societes;
	private int nombreBanques;
	private String nom;
	private int idTableCooperation;
	
	private Enqueteur enqueteurNational;
	
	public Pays() {
		nom = "Vide";
		residents = new ArrayList<Contribuable>();
		societes = new ArrayList<Societe>();
		nombreBanques = 0;
	}
	
	public Pays(String nom) {
		this.nom = nom;
		residents = new ArrayList<Contribuable>();
		societes = new ArrayList<Societe>();
		nombreBanques = 0;
	}
	
	/**
	 * Constructeur par attributs
	 * 
	 * @param nom
	 * @param residents
	 * @param societes
	 */
	public Pays(String nom, ArrayList<Contribuable> residents, ArrayList<Societe> societes) {
		this.nom = nom;
		this.residents = residents;
		this.societes = societes;
	}
	
	/**
	 * [obsolete] Constructeur par clonage (reference conservee)
	 * 
	 * @param ancien
	 */
	public Pays(Pays ancien) {
		this.nom = ancien.nom;
		this.residents = ancien.residents;
		this.societes = ancien.societes;
	}
	
	/**
	 * Ajoute un resident a l'ensemble des residents du pays
	 * 
	 * @param resident
	 */
	public void ajouterResident(Contribuable resident) {
		residents.add(resident);
	}
	
	/**
	 * Ajoute une societe a l'ensemble des societes du pays
	 * 
	 * @param societe
	 */
	public void ajouterSociete(Societe societe) {
		societes.add(societe);
	}

	public ArrayList<Contribuable> getResidents() {
		return residents;
	}

	public void setResidents(ArrayList<Contribuable> residents) {
		this.residents = residents;
	}

	public ArrayList<Societe> getSocietes() {
		return societes;
	}

	public void setSocietes(ArrayList<Societe> societes) {
		this.societes = societes;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}
	
	public int getNombreBanques() {
		return nombreBanques;
	}
	
	public void setNombreBanques(int nombreBanques) {
		this.nombreBanques = nombreBanques;
	}
	
	public String arbreDesPossessions() {
		String res = "";
		for(Contribuable c : residents) {
			res += "\n" + c.toString(0);
		}
		return res;
	}

	public int getIdTableCooperation() {
		return idTableCooperation;
	}

	public void setIdTableCooperation(int idTableCooperation) {
		this.idTableCooperation = idTableCooperation;
	}

	public Enqueteur getEnqueteurNational() {
		return enqueteurNational;
	}

	public void setEnqueteurNational(Enqueteur enqueteurNational) {
		this.enqueteurNational = enqueteurNational;
	}
	
	
	

}
