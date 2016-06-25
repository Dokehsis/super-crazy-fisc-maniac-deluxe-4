package entites;

import java.util.ArrayList;

import paperasse.CompteBancaire;

/**
 * Classe abstraite representant toute entite pouvant posseder un compte en banque
 * 
 * @author Remi Massart
 */
public abstract class PossesseurDeCompte {

	protected CompteBancaire compte;
	protected PossesseurDeCompte proprietaire;
	protected ArrayList<PossesseurDeCompte> possessions;
	protected Pays pays;
	protected String nom;
	
	public CompteBancaire getCompte() {
		return compte;
	}

	public void setCompte(CompteBancaire compte) {
		this.compte = compte;
	}

	public PossesseurDeCompte getProprietaire() {
		return proprietaire;
	}

	public void setProprietaire(PossesseurDeCompte proprietaire) {
		this.proprietaire = proprietaire;
	}

	public ArrayList<PossesseurDeCompte> getPossessions() {
		return possessions;
	}

	public void setPossessions(ArrayList<PossesseurDeCompte> possessions) {
		this.possessions = possessions;
	}

	public Pays getPays() {
		return pays;
	}

	public void setPays(Pays pays) {
		this.pays = pays;
	}

	public String getNom() {
		return nom;
	}
	
	public String getNomCourt() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public PossesseurDeCompte() {
		this.nom = "Vide";
		this.compte = null;
		this.proprietaire = null;
		this.possessions = null;
		this.pays = null;
	}
	
	/**
	 * Constructeur par attributs
	 * 
	 * @param nom
	 * @param compte
	 * @param proprietaire
	 * @param possessions
	 * @param pays
	 */
	public PossesseurDeCompte(String nom, CompteBancaire compte, PossesseurDeCompte proprietaire, ArrayList<PossesseurDeCompte> possessions, Pays pays) {
		this.nom = nom;
		this.compte = compte;
		this.proprietaire = proprietaire;
		this.possessions = possessions;
		this.pays = pays;
	}
	
	/**
	 * Constructeur par clonage (references conservees)
	 * 
	 * @param ancien
	 */
	public PossesseurDeCompte(PossesseurDeCompte ancien) {
		this.nom = ancien.nom;
		this.compte = ancien.compte;
		this.proprietaire = ancien.proprietaire;
		this.possessions = ancien.possessions;
		this.pays = ancien.pays;
	}
		
	@Override
	public String toString() {
		return nom;
	}
	
	/**
	 * Calcul le nombre de possessions total, i.e en comptant les possessions des possessions.
	 * 
	 * @return Le nombre de possessions total
	 */
	public int nombrePossessionsTotal() {
		if(this.getPossessions().size() == 0)
			return 0;
		int res = 0;
		for(PossesseurDeCompte possession : this.getPossessions()) {
			res += 1+possession.nombrePossessionsTotal();
		}
		return res;
	}
	
	public String toString(int n) {
		String total = "";
		for(int i=0; i<n; i++)
			total += "\t";
		total += getNom()+" >-----< "+pays.getNom()+" >-----< "+compte.getHebergeur().getNom()+" | "+compte.getIdCompte()+"\n";
		for(PossesseurDeCompte p : possessions) {
			total += p.toString(n+1);
		}
		return total;
	}

}
