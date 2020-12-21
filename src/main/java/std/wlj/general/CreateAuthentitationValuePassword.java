package std.wlj.general;

import java.util.Base64;

import javax.swing.JOptionPane;


public class CreateAuthentitationValuePassword {
    public static void main(String...args) {
        String password = JOptionPane.showInputDialog("Password: ");
        String what = "wjohnson000" + ":" + password;
        System.out.println(Base64.getEncoder().encodeToString(what.getBytes()));
    }
}
