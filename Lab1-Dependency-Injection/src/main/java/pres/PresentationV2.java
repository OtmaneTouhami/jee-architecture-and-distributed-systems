package pres;

import dao.IDao;
import metier.IMetier;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class PresentationV2 {
    public static void main(String[] args) {
        /*
         * dependency injection with dynamic instance
         */
        try (
                Scanner scanner = new Scanner(new File("config.txt"))
        ) {
            String daoClassName = scanner.nextLine();
            Class<?> daoClass = Class.forName(daoClassName);
            IDao dao = (IDao) daoClass.getConstructor().newInstance();

            String metierClassName = scanner.nextLine();
            Class<?> metierClass = Class.forName(metierClassName);
            // Constructor injection
            IMetier metier = (IMetier) metierClass.getConstructor(IDao.class).newInstance(dao);

            // Setter injection
            // IMetier metier = (IMetier) metierClass.getConstructor().newInstance();
            // metierClass.getDeclaredMethod("setDao", IDao.class).invoke(metier, dao);



            System.out.println("Result: " + metier.calcul());

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


    }
}
