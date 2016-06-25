package entites;

import java.util.ArrayList;

import paperasse.CompteBancaire;

/**
 * Classe representant les societes, i.e les possesseurs de compte non humains 
 * 
 * @author Remi Massart
 */
public class Societe extends PossesseurDeCompte {

	public Societe() {
		super();
	}
	
	/**
	 * Constructeur par attributs
	 * 
	 * @param nom
	 * @param idCompte
	 * @param proprietaire
	 * @param possessions
	 * @param pays
	 */
	public Societe(String nom, CompteBancaire compte, PossesseurDeCompte proprietaire, ArrayList<PossesseurDeCompte> possessions, Pays pays) {
		super(nom, compte, proprietaire, possessions, pays);
	}
	
	/**
	 * Constructeur par clonage (reference conservee)
	 * 
	 * @param ancienne
	 */
	public Societe(Societe ancienne) {
		super(ancienne);
	}
	
	@Override
	public String getNom() {
		return "Societe "+nom;
	}

}
