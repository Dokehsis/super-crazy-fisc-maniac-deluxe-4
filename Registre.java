package paperasse;

import java.util.ArrayList;

import entites.Enqueteur;

public class Registre {
	
	private ArrayList<String> denonciationsActuelle;
	private ArrayList<String> denonciationsVeille;

	public Registre() {
		denonciationsActuelle = new ArrayList<String>();
		denonciationsVeille = new ArrayList<String>();
	}
	
	/**
	 * Affiche un tableau des scores des joueurs, trie par score decroissant (et donc affiche du meilleur score au plus faible)
	 * 
	 * @param joueurs Les joueurs de la partie
	 */
	public static void afficherScores(ArrayList<Enqueteur> joueurs) {
		ArrayList<Enqueteur> copie = new ArrayList<Enqueteur>();
		for(Enqueteur e : joueurs) {
			copie.add(e);
		}
		copie.sort(new Enqueteur.ComparatorEnqueteur());
		for(Enqueteur e : copie) {
			System.out.println(e.getCompte().getMontant()+"$\t|  "+e.getNom());
		}
	}
	
	public void afficherDenonciations() {
		for(String s : denonciationsVeille) {
			System.out.println(s);
		}
	}
	
	/**
	 * Ajoute une denonciation a la liste des denonciations
	 * 
	 * @param denonciaton La denonciation a ajouter
	 */
	public void ajouterDenonciation(String denonciaton) {
		denonciationsActuelle.add(denonciaton);
	}
	
	/**
	 * Fait "vieillir" les informations, en passant les infos actuelles a la veille
	 */
	public void rotationDesInfos() {
		denonciationsVeille.removeAll(denonciationsVeille);
		denonciationsVeille.addAll(denonciationsActuelle);
		denonciationsActuelle = new ArrayList<String>();
	}

	public ArrayList<String> getDenonciationsActuelle() {
		return denonciationsActuelle;
	}

	public void setDenonciationsActuelle(ArrayList<String> denonciationsActuelle) {
		this.denonciationsActuelle = denonciationsActuelle;
	}

	public ArrayList<String> getDenonciationsVeille() {
		return denonciationsVeille;
	}

	public void setDenonciationsVeille(ArrayList<String> denonciationsVeille) {
		this.denonciationsVeille = denonciationsVeille;
	}
	
	

}
