package dragDrop;

import enums.LetterStatus;
import enums.LetterType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/*
 * Kaynak:
 * http://www.iitk.ac.in/esc101/05Aug/tutorial/uiswing/misc/example-1dot4/index.html#DragPictureDemo
 */

// Mouse hareketlerini ve odak durumlarini dinleyen custom bir JComponent yaziyoruz
// Bu JComponent icerisinde harfleri barindiracak
// Ayni zamanda Wordle oyunun gereksinimi olarak farkli arkaplan renklerinde bulunabilecek
public abstract class Letter extends JComponent implements MouseListener, FocusListener {

    // Letter sinifinin attributelari paket icerisinden erisilebilmesi icin protected olarak nitelendiriliyor
    protected Character c; // Hucredeki harfi tutan c nesnesi
    protected LetterType letterType; // Harfin tipini (klavye harfi/tahmin harfi) belirten enum
    protected LetterStatus letterStatus; // Harfin durumunu (dogru/yanlis vs) belirten enum

    // Constructor metodunda gerekli ozellikler saglaniyor
    public Letter(Character c) {
        this.c = c;
        this.letterStatus = LetterStatus.ONGOING_ANSWER;
        // Mouse tiklandiginda pencerede odaklanan nesnenin tiklanan harf olabilmesi icin
        // odaklanabilme ozelligi veriliyor
        setFocusable(true);
        // Mouse ve odak dinleyici saglaniyor
        addMouseListener(this);
        addFocusListener(this);
    }

    public abstract Character getChar();

    public abstract void setChar(Character c);

    public abstract LetterStatus getStatus();

    public abstract void setStatus(LetterStatus status);

    @Override
    public void focusGained(FocusEvent e) {}

    @Override
    public void focusLost(FocusEvent e) {}

    // Mouse tiklandiginda calisan metot
    @Override
    public void mouseClicked(MouseEvent e) {
        // Program penceresinde odak isteniyor
        requestFocusInWindow();
    }

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    // repaint() metodu cagirildiginda calisan metot
    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics g = graphics.create();
        // Harf durumuna gore arkaplan rengi seciliyor
        switch (letterStatus) {
            // Harf dogru ve dogru yerde ise yesil
            case CORRECT -> g.setColor(Color.GREEN);
            // Harf dogru ama yanlis yerde ise sari
            case MISPLACED -> g.setColor(Color.YELLOW);
            // Harf yanlis ise gri
            case WRONG -> g.setColor(Color.GRAY);
            // Hicbiri degilse (klavye harfi, devam eden cevap) beyaz
            default -> g.setColor(Color.WHITE);
        }
        g.fillRect(0, 0, 100, 100);
        // Daha sonra renk siyah yapiliyor ve cerceve ciziliyor
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, 100, 100);

        // Eger harfimiz bos degilse kutunun icine yaziliyor
        if (c != null) {
            g.drawString(String.valueOf(c), 25, 25);
        }

        // Tum islemler bittikten sonra cizim sonlandiriliyor
        g.dispose();
    }
}
