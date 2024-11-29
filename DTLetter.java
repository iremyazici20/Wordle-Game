package dragDrop;

import enums.LetterStatus;
import enums.LetterType;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

/*
 * Kaynak:
 * http://www.iitk.ac.in/esc101/05Aug/tutorial/uiswing/misc/example-1dot4/index.html#DragPictureDemo
 */

// Letter sinifindan miras alan Data Transferrable (DT) Letter sinifimiz mouse hareketlerini de izliyor
public class DTLetter extends Letter implements MouseMotionListener {

    // Bir mouse hareketi algilandigi zaman bu hareketin ilk olup olmadigi bizim icin onemli
    // Bu nedenle ilk hareket kayit altina aliniyor
    private MouseEvent firstMouseEvent = null;

    public DTLetter(Character c, LetterType letterType) {
        super(c);
        // Harfin klavye harfi mi yoksa tahmin harfi mi oldugu da LetterType enum'u ile saklaniyor
        this.letterType = letterType;
        addMouseMotionListener(this);
    }

    @Override
    public Character getChar() {
        return c;
    }

    @Override
    public void setChar(Character c) {
        // Bir JComponent yazdigimiz icin yeni karakter set edildikten sonra
        this.c = c;
        // repaint() metodu ile harf ekrana yeniden ciziliyor
        this.repaint();
    }

    @Override
    public LetterStatus getStatus() {
        return letterStatus;
    }

    @Override
    public void setStatus(LetterStatus status) {
        // Harfin durumu (dogru, yanlis...) LetterStatus enum'u ile saklaniyor
        this.letterStatus = status;
        // Durum degistigi zaman yeniden boyaniyor
        this.repaint();
    }

    // Mouse'a tiklanma durumu
    @Override
    public void mousePressed(MouseEvent e) {
        // Eger harfimiz bos ise metot calistirilmiyor
        if (c == null) return;

        // Harf dolu ise tiklanma hareketi ilk hareket olarak ataniyor
        firstMouseEvent = e;
        e.consume();
    }

    // Mouse'un suruklenme durumu
    @Override
    public void mouseDragged(MouseEvent e) {
        // Eger harfimiz bos ise metot calistirilmiyor
        if (c == null) return;

        // Surukleme hareketinin ilk mouse hareketi olmama durumunda
        if (firstMouseEvent != null) {
            e.consume();

            // Mouse'un x ve y eksenlerindeki konum degisimi hesaplaniyor
            int dx = Math.abs(e.getX() - firstMouseEvent.getX());
            int dy = Math.abs(e.getY() - firstMouseEvent.getY());
            // 2 eksenden herhangi birinde 5 pikselden fazla konum degisimi olmasi halinde
            // bu etkinlik tiklama degil, surukleme olarak nitelendiriliyor
            if (dx > 5 || dy > 5) {
                // Mouse'un tiklandigi kaynak aliniyor
                JComponent c = (JComponent) e.getSource();
                // Veri transferini isleyecek olan TransferHandler nesnesi aliniyor
                TransferHandler handler = c.getTransferHandler();
                // Bu isleyiciye surukleme islemi yaptiriliyor
                handler.exportAsDrag(c, firstMouseEvent, TransferHandler.COPY);
                // Islem tamamlandiktan sonra ilk mouse etkinligi sifirlaniyor
                firstMouseEvent = null;
            }
        }
    }

    // Mouse'un serbest birakilma (sol tusa basilmanin bitmesi) durumu
    @Override
    public void mouseReleased(MouseEvent e) {
        // Ilk mouse etkinligi sifirlaniyor
        firstMouseEvent = null;
    }

    @Override
    public void mouseMoved(MouseEvent e) {}

}
