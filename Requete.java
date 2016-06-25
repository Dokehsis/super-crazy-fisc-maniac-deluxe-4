package paperasse;

import entites.Enqueteur;
import entites.PossesseurDeCompte;
import moteur.Donnees;

/**
 * Representation des requetes des enqueteurs adressees aux pays 
 * 
 * @author Remi Massart
 */
@SuppressWarnings("rawtypes")
public class Requete implements Comparable {

	private int dateReponse;
	private String information;
	private String objet;
	private Enqueteur auteur;
	
	public Requete() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Constructeur par attributs connus
	 * 
	 * @param dateActuelle Le tour de jeu en cours
	 * @param temps	Temps de reponse
	 * @param information L'information contenue dans la reponse
	 */
	public Requete(int dateActuelle, int temps, String information) {
		dateReponse = dateActuelle+temps;
		this.information = information;
	}

	/**
	 * Methode de l'interface Comparable
	 */
	@Override
	public int compareTo(Object o) {
		Requete r = (Requete)o;
		int res = this.dateReponse-r.dateReponse;
		if(res == 0) {
			res = this.getInformation().compareTo(r.getInformation());
		}
		return res;
	}
	
	@Override
	public String toString() {
		String res = "Cette requete se terminera le  "+dateReponse+".";
		if(Donnees.debug)
			res += "\nElle contient cette information:\n"+information;
		return res;
	}
	
	/**
	 * Modification d'une information dans le cadre d'un joueur qui souhaite mentir
	 * 
	 * @param aleatoire Un possesseur de compte aleatoire, qui sera dans l'information corrompue
	 * @param reussite vrai si le mensonge est reussi, faux sinon
	 */
	public void corrompre(PossesseurDeCompte aleatoire, boolean reussite) {
		String[] contenu = information.split("[ ]");
		String infoCorrompue = "";
		infoCorrompue += contenu[0] + " " + contenu[1];
		if(information.contains("appartient")) {
			infoCorrompue += " appartient a "+aleatoire.getNom()+", se situant en "+aleatoire.getPays().getNom();
		}
		if(information.contains("possede")) {
			infoCorrompue += " possede "+aleatoire.getNom()+", se situant en "+aleatoire.getPays().getNom();
		}
		if(!reussite)
			infoCorrompue += " *FAUX*";
		information = infoCorrompue;
	}

	public int getDateReponse() {
		return dateReponse;
	}

	public void setDateReponse(int dateReponse) {
		this.dateReponse = dateReponse;
	}

	public String getInformation() {
		return information;
	}

	public void setInformation(String information) {
		this.information = information;
	}

	public String getObjet() {
		return objet;
	}

	public void setObjet(String objet) {
		this.objet = objet;
	}

	public Enqueteur getAuteur() {
		return auteur;
	}

	public void setAuteur(Enqueteur auteur) {
		this.auteur = auteur;
	}

}
