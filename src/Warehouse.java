package src;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Warehouse extends Block {

    public Point point;
    private List<Mail> mails;

    public Warehouse(Shape shape, Point point, Color color, int initialMails, int nX, int nY, List<Warehouse> warehouses) {
        super(shape, color);
        this.point = point;
        this.mails = generateRandomMails(initialMails, nX, nY, point, warehouses);
    }

    private List<Mail> generateRandomMails(int initialMails, int nX, int nY, Point avoid, List<Warehouse> warehouses) {
        List<Mail> resp = new LinkedList<>();
        Random rd = new Random();
        for (int i = 0; i < initialMails; i++) {
            Point point = new Point(rd.nextInt(nX), rd.nextInt(nY));
            boolean different = false;
            boolean exit = true;
            while (!different && point.equals(avoid)) {
                for (Warehouse w: warehouses) {
                    if (w.point.equals(point)) {
                        point = new Point(rd.nextInt(nX), rd.nextInt(nY));
                        exit = false;
                        break;
                    }
                }
                if (exit == true) {
                    different = true;
                }
            }
            // generate the delivery point for the mail
            // avoiding it to be the same as the warehouse location
            resp.add(new Mail(this.point, point, new Color(255, 255, 255, 255)));
        }
        return resp;
    }


    public List<Mail> getMailList() {
        return mails;
    }

    public void removeMail(Mail mail) {
        mails.remove(mail);
    }

    public Boolean isEmpty() {
        return mails.isEmpty();
    }

}
