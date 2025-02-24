package pres;

import dao.DaoImpl;
import metier.IMetier;
import metier.MetierImpl;

public class PresentationV1 {
    public static void main(String[] args) {
        /*
         * dependency injection with static instance
         */
        DaoImpl dao = new DaoImpl();

        MetierImpl metier = new MetierImpl(dao); // Constructor injection
        // metier.setDao(dao); // Setter injection
        System.out.println("Result: " + metier.calcul());
    }
}
