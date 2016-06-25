package entites;

/**
 * Interface definissant une entite jouable pouvant etre rendue automatique 
 * @author Remi Massart
 *
 */
public interface Automatisable {
	/**
	 * Regle les attributs de l'instance a rendre automatique
	 */
	public void rendreAuto();
	/**
	 * Definit le comportement (l'IA) a adopter lorsque l'objet automatique doit jouer
	 * @return Un code d'action sous forme de chaine de caracteres
	 */
	public String jouerAuto();
	/**
	 * Renvoie vrai si l'instance n'est pas une IA, faux sinon
	 */
	public boolean nEstPasAuto();
}
