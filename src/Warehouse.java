package src;


import java.awt.*;
import java.util.*;
import java.util.List;

public class Warehouse extends Block {

    public Point point;
    private List<Mail> mails;

    public Warehouse(Shape shape, Point point, Color color, int initialMails, int nX, int nY) {
        super(shape, color);
        this.point = point;
        this.mails = generateRandomMails(initialMails, nX, nY, point);
    }

    private static List<Mail> generateRandomMails(int initialMails, int nX, int nY, Point avoid) {
        List<Mail> resp = new LinkedList<>();
        Random rd = new Random();
        for (int i = 0; i < initialMails; i++) {
            // generate the delivery point for the mail
            // avoiding it to be the same as the warehouse location
            Point point = new Point(rd.nextInt(nX), rd.nextInt(nY)); // todo check if x-1 and y-1
            while (point.equals(avoid)) point = new Point(rd.nextInt(nX), rd.nextInt(nY));
            resp.add(new Mail(point, Color.green));
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
