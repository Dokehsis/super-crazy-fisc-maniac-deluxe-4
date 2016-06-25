package paperasse;

import entites.PossesseurDeCompte;
import moteur.Donnees;

/**
 * Representation d'une transaction entre deux comptes bancaires
 * 
 * @author Remi Massart
 *
 */
public class Transaction {

	private CompteBancaire compteRecepteur;
	private CompteBancaire compteSuspect;
	private PossesseurDeCompte fraudeur;
	private int montant;
	private int degreDeDifficulte;
	
	public Transaction() {
		
	}
	
	/**
	 * Generateur par couple de comptes
	 * 
	 * @param compteEmetteur Le compte qui emet la somme (compte suspect)
	 * @param compteRecepteur Le compte qui recois la somme
	 */
	public Transaction(CompteBancaire compteEmetteur, CompteBancaire compteRecepteur) {
		this.compteRecepteur = compteRecepteur;
		compteSuspect = compteEmetteur;
		fraudeur = Donnees.trouverUltimeProprietaire(compteEmetteur.getProprietaire());
		montant = Math.min((int)(Math.random()*compteEmetteur.getMontant()+500), compteEmetteur.getMontant());
		compteEmetteur.setMontant(compteEmetteur.getMontant()-montant);
		compteRecepteur.setMontant(compteRecepteur.getMontant()+montant);
		degreDeDifficulte = Donnees.calculerDegreDifficulte(compteSuspect.getProprietaire(), 0);
	}
	
	/**
	 * Retourne une chaine decrivant la transaction dans son integralite
	 * @return Cette chaine
	 */
	public String toString() {
		return compteSuspect.getProprietaire().getNom()+"\n\t\t>"+compteSuspect.getIdCompte()+"\t"+montant+"\t"+compteRecepteur.getIdCompte()+"\t"+degreDeDifficulte;
	}
	
	/**
	 * Retourne une chaine decrivant les uniquement informations accessibles aux joueurs
	 * @return Cette chaine
	 */
	public String infosAccessibles() {
		return ">\t"+compteSuspect.getIdCompte()+"\t\t"+montant+"\t\t"+compteRecepteur.getIdCompte();
	}

	public CompteBancaire getCompteRecepteur() {
		return compteRecepteur;
	}

	public void setCompteRecepteur(CompteBancaire compteRecepteur) {
		this.compteRecepteur = compteRecepteur;
	}

	public CompteBancaire getCompteSuspect() {
		return compteSuspect;
	}

	public void setCompteSuspect(CompteBancaire compteSuspect) {
		this.compteSuspect = compteSuspect;
	}

	public PossesseurDeCompte getFraudeur() {
		return fraudeur;
	}

	public void setFraudeur(PossesseurDeCompte fraudeur) {
		this.fraudeur = fraudeur;
	}

	public int getMontant() {
		return montant;
	}

	public void setMontant(int montant) {
		this.montant = montant;
	}

	public int getDegreDeDifficulte() {
		return degreDeDifficulte;
	}

	public void setDegreDeDifficulte(int degreDeDifficulte) {
		this.degreDeDifficulte = degreDeDifficulte;
	}
	
}
