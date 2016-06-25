package moteur;


import entites.Pays;
import paperasse.Transaction;

public class Main {

	public Main() {
	}

	public static void main(String[] args) {
		GestionnaireDePartie mj = new GestionnaireDePartie();
		mj.lancerLaPartie();
		
		if(Donnees.debug) {
			for(Transaction t : mj.getDonnees().getToutesLesTransactions()) {
				System.out.println(t);
			}
			System.out.println(mj.getDonnees().arbreComplet());
			for(int i=0; i<5; i++) {
				for(int j=0; j<5; j++) {
					System.out.print(mj.getDonnees().getCooperationEntrePays()[i][j]+"|");
				}
				System.out.println();
			}
			for(Pays p : mj.getDonnees().getMonde()) {
				System.out.print(p.getIdTableCooperation());
			}
			System.out.println();
		}
		
		while(mj.laPartieContinue()) {
			mj.boucleDePartie();
		}
		
		mj.finDePartie();
	}

}
