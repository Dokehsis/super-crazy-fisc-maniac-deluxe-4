package paperasse;

import entites.Banque;
import entites.PossesseurDeCompte;

public class CompteBancaire {

	private int idCompte;
	private int montant;
	private Banque hebergeur;
	private PossesseurDeCompte proprietaire;
	
	public CompteBancaire() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Generateur par attributs
	 * 
	 * @param idCompte
	 * @param montant
	 * @param hebergeur
	 * @param proprietaire
	 */
	public CompteBancaire(int idCompte, int montant, Banque hebergeur, PossesseurDeCompte proprietaire) {
		this.idCompte = idCompte;
		this.montant = montant;
		this.hebergeur = hebergeur;
		this.proprietaire = proprietaire;
	}

	public int getIdCompte() {
		return idCompte;
	}

	public void setIdCompte(int idCompte) {
		this.idCompte = idCompte;
	}

	public int getMontant() {
		return montant;
	}

	public void setMontant(int montant) {
		this.montant = montant;
	}

	public Banque getHebergeur() {
		return hebergeur;
	}

	public void setHebergeur(Banque hebergeur) {
		this.hebergeur = hebergeur;
	}

	public PossesseurDeCompte getProprietaire() {
		return proprietaire;
	}

	public void setProprietaire(PossesseurDeCompte proprietaire) {
		this.proprietaire = proprietaire;
	}
	
	

}
