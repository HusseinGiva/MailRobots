package src;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Warehouse extends Block {

    public Point point;
    private List<Mail> mails;

    public Warehouse(Shape shape, Point point, Color color, int initialMails) {
        super(shape, color);
        this.point = point;
        this.mails = generateRandomMails(initialMails);
    }

    private List<Mail> generateRandomMails(int initialMails) {
        List<Mail> resp = new LinkedList<>();
        Random rd = new Random();
        for (int i = 0; i < initialMails; i++) {
            Point point = Board.getHouses().get(rd.nextInt(Board.getHouses().size())).point;
            //System.out.println(this.point + " " + point);
            resp.add(new Mail(this.point, point));
        }
        return resp;
    }

    public List<Mail> getMailList() {
        return mails;
    }

}
