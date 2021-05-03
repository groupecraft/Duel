package fr.groupecraft.duel.commun;

public class DuelExeption extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public DuelExeption() {
		super("Un joueur n'est pas dans le duel dans lequel il est sencé être");
	}

}
