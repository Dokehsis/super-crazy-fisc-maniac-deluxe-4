package entites;

import java.util.ArrayList;

import paperasse.CompteBancaire;

/**
 * Classe representant un possesseur de compte humain
 * 
 * @author Remi Massart
 */
public class Contribuable extends PossesseurDeCompte {

	public Contribuable() {
		super();
	}
	
	public Contribuable(String nom, CompteBancaire compte, PossesseurDeCompte proprietaire, ArrayList<PossesseurDeCompte> possessions, Pays pays) {
		super(nom, compte, proprietaire, possessions, pays);
	}
	
	@Override
	public String getNom() {
		return "Contribuable "+nom;
	}

}
