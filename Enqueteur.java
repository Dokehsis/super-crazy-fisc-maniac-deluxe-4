package entites;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;
import java.util.TreeSet;

import moteur.Donnees;
import moteur.GestionnaireDePartie;
import paperasse.Requete;

/**
 * Classe representant les joueurs
 * 
 * @author Remi Massart
 */
public class Enqueteur extends Contribuable implements Automatisable{

	private int determination;
	private int charisme;
	private int reputation;
	
	private boolean estJoueur;
	
	public static final String[] caracteristiques = {"Determination", "Charisme", "Reputation"};
	
	private TreeSet<Requete> requetes;
	private String informations;
	private int primesEnAttente;
	
	public Enqueteur() {
		requetes = new TreeSet<Requete>();
		determination = 0;
		charisme = 0;
		reputation = 0;
		estJoueur = false;
		informations = "";
		primesEnAttente = 0;
	}
	
	/**
	 * Comparateur triant les enqueteurs par score, puis par ordre alphabetique a scores identiques
	 * @author Remi Massart
	 *
	 */
	public static class ComparatorEnqueteur implements Comparator<Enqueteur> {
		
		@Override
		public int compare(Enqueteur e1, Enqueteur e2) {
			int res = -e1.getCompte().getMontant() + e2.getCompte().getMontant();
			if(res == 0)
				res = e1.getNom().compareTo(e2.getNom());
			return res;
		}
		
	}
	
	public void rendreJoueur(Scanner clavier, ArrayList<String> nomUtilises) {
		estJoueur = true;
		String monty;
		int points = Donnees.pointsCompetences;
		int investissement;
		System.out.println("Entrez votre nom: ");
		while(nomUtilises.contains(nom = clavier.nextLine())) {
			System.out.println("Ce nom n'est pas disponible. Choisissez en un autre:");
		}
		nomUtilises.add(nom);
		if(nom.equalsIgnoreCase("Tim"))
			monty = "Greetings, Tim the Enchanter. ";
		else
			monty = "Bienvenue "+nom+"! ";
		System.out.println(monty+"Dans SCFMD4, les enqueteurs tels que vous sont particulierement entraines dans certains domaines.");
		System.out.println("Vous disposez de "+points+" points de competences, a repartir dans les 3 competences suivantes a hauteur de "+Donnees.maxCompetence+" points maximum par competence:");
		GestionnaireDePartie.promptEntree(clavier);
		System.out.println("La Determination vous donne acces a un point d'action supplementaire par point de competence investi par round.");
		System.out.println("Le Charisme vous permet de fournir de fausse information plus difficile a detecter.");
		System.out.println("La Reputation reduit le temps de reponse a vos requetes.");
		GestionnaireDePartie.promptEntree(clavier);
		for(int i=0; i<caracteristiques.length; i++) {
			System.out.println("Il vous reste "+points+" points a depenser.");
			System.out.println("Combien de points investir en "+caracteristiques[i]+"? ");
			investissement = GestionnaireDePartie.lireEntier(clavier);
			investissement = Math.min(Math.min(points, Donnees.maxCompetence), Math.max(0, investissement));
			if(caracteristiques[i].equals("Determination")) {
				determination = investissement;
			}
			if(caracteristiques[i].equals("Charisme")) {
				charisme = investissement;
			}
			if(caracteristiques[i].equals("Reputation")) {
				reputation = investissement;
			}
			points -= investissement;
			System.out.println(investissement+" points investis en "+caracteristiques[i]);
		}
	}
	
	/**
	 * Ajoute une requete a la liste des requetes de l'enqueteur pour savoir a qui appartient une societe
	 * 
	 * @param nom Le nom de la societe en question
	 * @param donnees Les donnees de la partie en cours (voir la classe Donnees)
	 */
	public Requete aQuiAppartient(PossesseurDeCompte enQuestion, Donnees donnees) {
		PossesseurDeCompte proprietaire = enQuestion.getProprietaire();
		int tempsDeReponse = Math.max(1+donnees.getCooperationCouple(pays, enQuestion.getPays())-reputation, 1);
		String information = enQuestion.getNom()+" appartient a "+proprietaire.getNom()+", se situant en "+proprietaire.getPays().getNom();
		Requete requete = new Requete(donnees.getTourDeJeu(), tempsDeReponse, information);
		requete.setAuteur(this);
		requete.setObjet(this.nom+" souhaite savoir a qui appartient "+enQuestion.getNom()+".");
		requetes.add(requete);
		return requete;
	}
	
	/**
	 * Ajoute une requete a la liste des requetes de l'enqueteur pour savoir ce que possede une societe
	 * 
	 * @param nom La societe en question
	 * @param donnees Les donnees de la partie en cours (voir la classe Donnees)
	 */
	public ArrayList<Requete> quePossede(PossesseurDeCompte enQuestion, Donnees donnees) {
		//PossesseurDeCompte enQuestion = donnees.trouverEntite(nom);
		int tempsDeReponse;
		ArrayList<Requete> aRetourner = new ArrayList<Requete>();
		for(PossesseurDeCompte possession : enQuestion.getPossessions()) {
			tempsDeReponse = Math.max(1+donnees.getCooperationCouple(pays, enQuestion.getPays())+donnees.getCooperationCouple(possession.getPays(), enQuestion.getPays())-reputation, 1);
			String information  = enQuestion.getNom()+" possede "+possession.getNom()+", se situant en "+possession.getPays().getNom();
			Requete requete = new Requete(donnees.getTourDeJeu(), tempsDeReponse, information);
			requete.setAuteur(this);
			requete.setObjet(this.nom+" souhaite savoir ce que possede "+enQuestion.getNom()+".");
			requetes.add(requete);
			aRetourner.add(requete);
		}
		return aRetourner;
	}
	
	/**
	 * Cherche le proprietaire d'un compte dans une banque donnee et ajoute la requete correspondante a la liste des requetes
	 * 
	 * @param banque La banque sollicitee
	 * @param idCompte Le compte a examiner
	 * @param donnees Les donnees de la partie en cours (voir la classe Donnees)
	 * @return true si le compte etait bien dans cette banque, faux sinon.
	 */
	public boolean quiEstDerriereCeCompte(Banque banque, int idCompte, Donnees donnees) {
		int tempsDeReponse;
		for(PossesseurDeCompte pdc : banque.getClients()) {
			if(pdc.getCompte().getIdCompte() == idCompte) {
				tempsDeReponse = Math.max(1+(int)(Math.random()*7)-reputation, 1);
				String information = "Le compte "+idCompte+" appartient a "+pdc.getNom();
				requetes.add(new Requete(donnees.getTourDeJeu(), tempsDeReponse, information));
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Ajoute les primes en attente au compte en banque de l'enqueteur
	 */
	public void recompenser() {
		compte.setMontant(compte.getMontant()+primesEnAttente);
		primesEnAttente = 0;
	}

	public int getDetermination() {
		return determination;
	}

	public void setDetermination(int determination) {
		this.determination = determination;
	}

	public int getCharisme() {
		return charisme;
	}

	public void setCharisme(int charisme) {
		this.charisme = charisme;
	}

	public int getReputation() {
		return reputation;
	}

	public void setReputation(int reputation) {
		this.reputation = reputation;
	}

	public TreeSet<Requete> getRequetes() {
		return requetes;
	}

	public void setRequetes(TreeSet<Requete> requetes) {
		this.requetes = requetes;
	}

	public String getInformations() {
		return informations;
	}

	public void setInformations(String informations) {
		this.informations = informations;
	}
	
	@Override
	public String getNom() {
		if(!nom.equals("Vincent"))
			return "Enqueteur "+nom;
		else
			return "Vincent l'Affabulateur";
	}

	public int getPrimesEnAttente() {
		return primesEnAttente;
	}

	public void setPrimesEnAttente(int primesEnAttente) {
		this.primesEnAttente = primesEnAttente;
	}

	@Override
	public void rendreAuto() {
		estJoueur = false;
		int points = Donnees.pointsCompetences;
		points -= (determination = Math.min((int)(Math.random()*Donnees.maxCompetence), points));
		points -= (charisme = Math.min((int)(Math.random()*Donnees.maxCompetence), points));
		points -= (reputation = Math.min((int)(Math.random()*Donnees.maxCompetence), points));
	}

	/*
	 * (non-Javadoc)
	 * @see entites.Automatisable#jouerAuto()
	 * Le comportement de l'IA est simple: elle ne joue pas vraiment.
	 * Elle a une certaine probabilite (amelioree par sa Reputation)
	 * de faire une denonciation correcte, dont le degre de difficulte
	 * est tire aleatoirement suivant une loi en racine carree (amelioree
	 * par sa Determination).
	 * Le code retourne est vide si la denonciation n'a pas lieu, sinon
	 * il est egal au degre de difficulte de la transaction a denoncer.
	 */
	@Override
	public String jouerAuto() {
		String code = "";
		if((int)(Math.random()*15)-reputation == 14-reputation) {
			code += Donnees.intelligenceIA-(int)Math.sqrt(Math.max(0,Math.random()*Math.pow(Donnees.intelligenceIA+1, 2)-determination));
			if(Donnees.debug)
				System.out.println("Denonciation possible: "+nom+" "+code);
		}
		return code;
	}
	
	@Override
	public boolean nEstPasAuto() {
		return estJoueur;
	}

}
